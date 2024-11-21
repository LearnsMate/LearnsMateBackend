package intbyte4.learnsmate.blacklist.service;

import intbyte4.learnsmate.admin.domain.dto.AdminDTO;
import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.admin.mapper.AdminMapper;
import intbyte4.learnsmate.admin.service.AdminService;
import intbyte4.learnsmate.admin.service.AdminServiceImpl;
import intbyte4.learnsmate.blacklist.domain.dto.*;
import intbyte4.learnsmate.blacklist.domain.entity.Blacklist;
import intbyte4.learnsmate.blacklist.domain.vo.response.ResponseFindBlacklistVO;
import intbyte4.learnsmate.blacklist.domain.vo.response.ResponseFindReservedStudentBlacklistVO;
import intbyte4.learnsmate.blacklist.mapper.BlacklistMapper;
import intbyte4.learnsmate.blacklist.repository.BlacklistRepository;
import intbyte4.learnsmate.comment.domain.dto.CommentDTO;
import intbyte4.learnsmate.comment.service.CommentService;
import intbyte4.learnsmate.member.domain.MemberType;
import intbyte4.learnsmate.member.domain.dto.MemberDTO;
import intbyte4.learnsmate.member.domain.entity.Member;
import intbyte4.learnsmate.member.mapper.MemberMapper;
import intbyte4.learnsmate.member.service.MemberService;
import intbyte4.learnsmate.report.domain.dto.ReportDTO;
import intbyte4.learnsmate.report.domain.dto.ReportedMemberDTO;
import intbyte4.learnsmate.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;
    private final BlacklistMapper blacklistMapper;
    private final ReportService reportService;
    private final MemberService memberService;
    private final CommentService commentService;
    private final MemberMapper memberMapper;
    private final AdminMapper adminMapper;
    private final AdminService adminService;

    // 1. flag는 볼필요 없음. -> 학생, 강사만 구분해야함.
    public BlacklistPageResponse<ResponseFindBlacklistVO> findAllBlacklistByMemberType(int page, int size, MemberType memberType) {

        // Pageable 객체 생성
        PageRequest pageable = PageRequest.of(page, size);

        // 페이징 처리된 데이터 조회
        Page<BlacklistDTO> blacklistPage = blacklistRepository.findAllBlacklistByMemberType(memberType, pageable);

        // DTO -> VO 변환
        List<ResponseFindBlacklistVO> responseList = blacklistPage.getContent().stream()
                .map(blacklistMapper::fromBlacklistDTOtoResponseFindBlacklistVO)
                .collect(Collectors.toList());

        // 페이지 응답 생성
        return new BlacklistPageResponse<>(
                responseList,
                blacklistPage.getTotalElements(),
                blacklistPage.getTotalPages(),
                blacklistPage.getNumber(),
                blacklistPage.getSize()
        );
    }

    // 1. 멤버 타입에 따라 신고내역 횟수 뒤져서 찾기 reportService.findCount
    // (피신고자 코드의 횟수만 가져오면 됨. -> 피신고자 멤버코드, 신고 횟수)
    // 2. Member table에서 가져오기(true인 놈들)
    public ReservedBlacklistPageResponse<?> findAllReservedBlacklistByMemberType(
            int page, int size, MemberType memberType)
    {

        Pageable pageable = PageRequest.of(page, size);

        // 모든 멤버 가져옴 (페이지네이션 포함)
        Page<ReportedMemberDTO> reportedMemberPage = reportService.findReportCountByMemberCode(pageable);

        // 멤버 타입이 동일하고 flag가 true인 사람만 필터링
        List<ReportedMemberDTO> filteredList = reportedMemberPage.stream()
                .filter(dto -> dto.getReportedMember().getMemberType().equals(memberType)
                        && dto.getReportedMember().getMemberFlag())
                .toList();

        // Page로 다시 변환
        Page<ReportedMemberDTO> filteredPage = new PageImpl<>(filteredList, pageable, filteredList.size());

        // DTO -> VO 변환
        List<ResponseFindReservedStudentBlacklistVO> content = filteredPage.getContent().stream()
                .map(blacklistMapper::fromReportedMemberDTOToResponseFindReservedStudentBlacklistVO)
                .toList();

        return new ReservedBlacklistPageResponse<>(
                content,
                filteredPage.getTotalElements(),
                filteredPage.getTotalPages(),
                page,
                size
        );
    }

    // 블랙리스트에서 신고당한 댓글 내역까지 모두 볼수 있는 서비스 메서드
    public List<BlacklistReportCommentDTO> findBlacklistReportComment(Long memberCode) {

        // 1. Report table에서 memberCode와 reportedMemberCode 가 같은거 가져오기
        List<ReportDTO> reportDTOlist = reportService.findAllReportByMemberCode(memberCode);

        // 2. ReportDTO의 comment_code 내역 가져오기 -> comment table
        List<CommentDTO> commentDTOList = reportDTOlist.stream()
                .map(reportDTO -> commentService.findCommentByCommentCode(reportDTO.getCommentCode()))
                .toList();

        // 3. List<BlacklistReportCommentDTO> 생성 및 데이터 추가
        List<BlacklistReportCommentDTO> blacklistReportCommentDTOList = new ArrayList<>();
        for (int i = 0; i < reportDTOlist.size(); i++) {
            blacklistReportCommentDTOList.add(BlacklistReportCommentDTO.builder()
                    .reportDTO(reportDTOlist.get(i))
                    .commentDTO(commentDTOList.get(i))
                    .build());
        }
        return blacklistReportCommentDTOList;
    }

    // 블랙리스트 등록 메서드
    public void addMemberToBlacklist(BlacklistDTO dto) {
        // 1. memberCode에 해당하는 사람
        MemberDTO memberDTO = memberService.findById(dto.getMemberCode());
        Member member = memberMapper.fromMemberDTOtoMember(memberDTO);

        // 2. admin을 찾아와야 하는데 나중에 token으로 처리 할듯?
        AdminDTO adminDTO = adminService.findByAdminCode(202004002L);
//        AdminDTO adminDTO = new AdminDTO();
        Admin admin = adminMapper.toEntity(adminDTO);

        // 3. BlacklistDTO 생성 -> 이유만 있으면 됨. -> 이유도 넘겨받아야함. -> dto 자체를 넘겨받으면 해결
        // 블랙리스트로 저장해야하니까 Blacklist 엔티티를 만들어야함.
        Blacklist blacklist = blacklistMapper.fromBlacklistDTOtoBlacklist(dto, member, admin);

        // 4. 블랙리스트에 저장
        blacklistRepository.save(blacklist);

        // 5. 회원 flag false로 수정
        memberService.deleteMember(dto.getMemberCode());
    }

    // 블랙리스트 필터링 메서드
    public List<BlacklistDTO> filterBlacklistMember(BlacklistFilterRequestDTO dto){
        List<Blacklist> blacklistList = blacklistRepository.searchBy(dto);

        return blacklistList.stream()
                .map(blacklistMapper::fromBlacklistToBlacklistDTO)
                .collect(Collectors.toList());
    }
}
