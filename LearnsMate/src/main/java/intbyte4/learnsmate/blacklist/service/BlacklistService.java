package intbyte4.learnsmate.blacklist.service;

import intbyte4.learnsmate.blacklist.domain.dto.BlacklistDTO;
import intbyte4.learnsmate.blacklist.domain.dto.BlacklistReportCommentDTO;
import intbyte4.learnsmate.blacklist.domain.entity.Blacklist;
import intbyte4.learnsmate.blacklist.mapper.BlacklistMapper;
import intbyte4.learnsmate.blacklist.repository.BlacklistRepository;
import intbyte4.learnsmate.comment.domain.dto.CommentDTO;
import intbyte4.learnsmate.comment.service.CommentService;
import intbyte4.learnsmate.member.domain.MemberType;
import intbyte4.learnsmate.member.service.MemberService;
import intbyte4.learnsmate.report.domain.dto.ReportDTO;
import intbyte4.learnsmate.report.domain.dto.ReportedMemberDTO;
import intbyte4.learnsmate.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;
    private final BlacklistMapper blacklistMapper;
    private final ReportService reportService;
    private final MemberService memberService;
    private final CommentService commentService;

    @Autowired
    public BlacklistService(BlacklistRepository blacklistRepository, BlacklistMapper blacklistMapper, ReportService reportService, MemberService memberService, CommentService commentService) {
        this.blacklistRepository = blacklistRepository;
        this.blacklistMapper = blacklistMapper;
        this.reportService = reportService;
        this.memberService = memberService;
        this.commentService = commentService;
    }

    // 1. flag는 볼필요 없음. -> 학생, 강사만 구분해야함.
    public List<BlacklistDTO> findAllBlacklistByMemberType(MemberType memberType) {

        List<Blacklist> blacklistList = blacklistRepository.findAllBlacklistByMemberType(memberType);

        List<BlacklistDTO> blacklistDTOList = new ArrayList<>();

        for (Blacklist blacklist : blacklistList) {
            // Blacklsit -> BlacklsitDTO
            blacklistDTOList.add(blacklistMapper.fromBlacklistToBlacklistDTO(blacklist));
        }

        return blacklistDTOList;
    }

    // 1. 멤버 타입에 따라 신고내역 횟수 뒤져서 찾기 reportService.findCount
    // (피신고자 코드의 횟수만 가져오면 됨. -> 피신고자 멤버코드, 신고 횟수)
    // 2. Member table에서 가져오기(true인 놈들)
    public List<ReportedMemberDTO> findAllReservedBlacklistByMemberType(MemberType memberType) {

        // 모든 멤버 가져옴.
        List<ReportedMemberDTO> reportedMoreThanFiveMemberList = reportService.findReportCountByMemberCode();

        // 멤버 타입이 동일한거만 가져오기 -> 원래는 sql로 처리해야하지만 많지 않을것이기 때문에 백엔드에서 처리해도 무방하다 생각
        // + flag가 true인 사람 가져오기
        List<ReportedMemberDTO> filteredList = reportedMoreThanFiveMemberList.stream()
                .filter(dto -> dto.getReportedMember().getMemberType().equals(memberType)
                                && dto.getReportedMember().getMemberFlag())
                .collect(Collectors.toList());

        return filteredList;
    }

    // 블랙리스트에서 신고당한 댓글 내역까지 모두 볼수 있는 서비스 메서드
    public List<BlacklistReportCommentDTO> findBlacklistReportComment(Long memberCode) {

        // 1. Report table에서 memberCode와 reportedMemberCode 가 같은거 가져오기
        List<ReportDTO> reportDTOlist = reportService.findAllReportByMemberCode(memberCode);

        // 2. ReportDTO의 comment_code 내역 가져오기 -> comment table
        List<CommentDTO> commentDTOList = reportDTOlist.stream()
                .map(reportDTO -> commentService.findComentByCommentCode(reportDTO.getCommentCode()))
                .collect(Collectors.toList());

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
}
