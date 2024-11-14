package intbyte4.learnsmate.lecture.domain.vo.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import intbyte4.learnsmate.lecture.enums.LectureLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestLectureFilterVO {
    private Long lectureCode; // 강의 코드
    private String lectureTitle; // 강의명
    private Long tutorCode; // 강사 코드
    private String tutorName; // 강사명
    private String lectureCategoryName; // 강의 카테고리
    private LectureLevelEnum lectureLevel; // 강의 난이도
    private Boolean lectureConfirmStatus; // 강의 계약 상태
    private Boolean lectureStatus; // 강의 상태
    private Integer minPrice; // 최소 금액
    private Integer maxPrice; // 최대 금액
    private LocalDateTime startCreatedAt; // 강의 생성일 시작
    private LocalDateTime endCreatedAt; // 강의 생성일 끝
}
