package intbyte4.learnsmate.contract_status.domain.vo.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseRegisterContractStatusVO {
    private Long contractStatusCode;
    private Integer approvalStatus;
    private LocalDateTime createdAt;
    private String note;
    private String lectureCode;
    private Long adminCode;
}