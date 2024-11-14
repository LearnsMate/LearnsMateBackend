package intbyte4.learnsmate.blacklist.mapper;

import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.blacklist.domain.dto.BlacklistDTO;
import intbyte4.learnsmate.blacklist.domain.dto.BlacklistReportCommentDTO;
import intbyte4.learnsmate.blacklist.domain.entity.Blacklist;
import intbyte4.learnsmate.blacklist.domain.vo.response.ResponseFindBlacklistVO;
import intbyte4.learnsmate.blacklist.domain.vo.response.ResponseFindReservedBlacklistOneVO;
import intbyte4.learnsmate.blacklist.domain.vo.response.ResponseFindReservedStudentBlacklistVO;
import intbyte4.learnsmate.blacklist.domain.vo.response.ResponseFindReservedTutorBlacklistVO;
import intbyte4.learnsmate.member.domain.entity.Member;
import intbyte4.learnsmate.report.domain.dto.ReportedMemberDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BlacklistMapper {


    public BlacklistDTO fromBlacklistToBlacklistDTO(Blacklist blacklist) {
        return BlacklistDTO.builder()
                .blackCode(blacklist.getBlackCode())
                .blackReason(blacklist.getBlackReason())
                .createdAt(blacklist.getCreatedAt())
                .memberCode(blacklist.getMember().getMemberCode())
                .adminCode(blacklist.getAdmin().getAdminCode())
                .build();
    }

    public ResponseFindBlacklistVO fromBlacklistDTOToResponseFindReportVO(BlacklistDTO blacklistDTO) {
        return ResponseFindBlacklistVO.builder()
                .blackCode(blacklistDTO.getBlackCode())
                .blackReason(blacklistDTO.getBlackReason())
                .createdAt(blacklistDTO.getCreatedAt())
                .memberCode(blacklistDTO.getMemberCode())
                .adminCode(blacklistDTO.getAdminCode())
                .build();
    }

    public ResponseFindReservedStudentBlacklistVO fromReportedMemberDTOToResponseFindReservedStudentBlacklistVO(ReportedMemberDTO dto) {
        return ResponseFindReservedStudentBlacklistVO.builder()
                .memberCode(dto.getReportedMember().getMemberCode())
                .memberName(dto.getReportedMember().getMemberName())
                .reportCount(dto.getReportCount())
                .build();
    }

    public ResponseFindReservedTutorBlacklistVO fromReportedMemberDTOToResponseFindReservedTutorBlacklistVO(ReportedMemberDTO dto) {
        return ResponseFindReservedTutorBlacklistVO.builder()
                .memberCode(dto.getReportedMember().getMemberCode())
                .memberName(dto.getReportedMember().getMemberName())
                .reportCount(dto.getReportCount())
                .build();
    }

    public List<ResponseFindReservedBlacklistOneVO> fromBlacklistReportCommentDTOToResponseFindReservedBlacklistOneVO(List<BlacklistReportCommentDTO> dtoList) {
        return dtoList.stream()
                .map(dto -> ResponseFindReservedBlacklistOneVO.builder()
                        .reportDTO(dto.getReportDTO())
                        .commentDTO(dto.getCommentDTO())
                        .build())
                .collect(Collectors.toList());
    }

    public Blacklist fromBlacklistDTOtoBlacklist(BlacklistDTO dto, Member member, Admin admin) {
        return Blacklist.builder()
                .createdAt(LocalDateTime.now())
                .blackReason(dto.getBlackReason())
                .admin(admin)
                .member(member)
                .build();
    }
}
