package intbyte4.learnsmate.lecture.domain.vo.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import intbyte4.learnsmate.lecture.domain.entity.LectureLevelEnum;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestEditLectureInfoVO {
    private String lectureTitle;
    private Boolean lectureConfirmStatus;
    private LocalDateTime updatedAt;
    private String lectureImage;
    private Integer lecturePrice;
    private Boolean lectureStatus;
    private Integer lectureClickCount;
    private LectureLevelEnum lectureLevel;
}
