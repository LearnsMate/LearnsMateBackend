package intbyte4.learnsmate.blacklist.domain.vo.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseFindReservedTutorBlacklistVO {
    // 강사코드, 강사명, 누적 신고 횟수 이렇게가 필요함. -> dto 하나 만들자.
    private Long memberCode;
    private String memberName;
    private Integer reportCount;
}