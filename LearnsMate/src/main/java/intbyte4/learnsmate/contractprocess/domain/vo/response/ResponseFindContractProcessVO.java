package intbyte4.learnsmate.contractprocess.domain.vo.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseFindContractProcessVO {

    private Long contractProcessCode;
    private Integer approvalProcess;
    private LocalDateTime createdAt;
    private String note;
    private String lectureCode;
    private Long adminCode;
}
