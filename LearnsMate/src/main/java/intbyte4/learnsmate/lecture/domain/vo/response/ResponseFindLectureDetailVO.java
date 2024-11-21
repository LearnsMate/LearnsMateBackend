package intbyte4.learnsmate.lecture.domain.vo.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import intbyte4.learnsmate.lecture.domain.entity.LectureLevelEnum;
import intbyte4.learnsmate.video_by_lecture.domain.dto.VideoByLectureDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResponseFindLectureDetailVO {
    private String lectureCode;
    private String lectureTitle;
    private Long tutorCode;
    private String tutorName;
    private String lectureCategoryName;
    private LectureLevelEnum lectureLevel;
    private LocalDateTime createdAt;
    private Integer lecturePrice;
    private Boolean lectureConfirmStatus;
    private Boolean lectureStatus;

    private String lectureImage;
    private String lectureContent;
    private Integer lectureClickCount;
    private int totalStudents;
    private int totalRevenue;
    private List<VideoByLectureDTO> lectureVideos;

    private Integer purchaseCount;
    private Double purchaseConversionRate;
}