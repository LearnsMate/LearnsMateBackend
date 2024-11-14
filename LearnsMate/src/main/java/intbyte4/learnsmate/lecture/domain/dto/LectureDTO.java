package intbyte4.learnsmate.lecture.domain.dto;


import intbyte4.learnsmate.lecture.domain.entity.LectureLevelEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LectureDTO {
    private Long lectureCode;
    private String lectureTitle;
    private Boolean lectureConfirmStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String lectureImage;
    private Integer lecturePrice;
    private Long tutorCode;
    private Boolean lectureStatus;
    private Integer lectureClickCount;
    private LectureLevelEnum lectureLevel;

}
