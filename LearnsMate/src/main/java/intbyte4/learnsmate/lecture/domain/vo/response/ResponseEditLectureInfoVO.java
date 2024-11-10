package intbyte4.learnsmate.lecture.domain.vo.response;

import intbyte4.learnsmate.lecture.enums.LectureCategoryEnum;
import intbyte4.learnsmate.lecture.enums.LectureLevelEnum;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ResponseEditLectureInfoVO {
    private String lectureTitle;
    private LectureCategoryEnum lectureCategoryEnum;
    private Boolean lectureConfirmStatus;
    private LocalDateTime updatedAt;
    private String lectureImage;
    private Integer lecturePrice;
    private Boolean lectureStatus;
    private Integer lectureClickCount;
    private LectureLevelEnum lectureLevel;
}