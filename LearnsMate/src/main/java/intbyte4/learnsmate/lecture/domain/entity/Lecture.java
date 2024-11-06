package intbyte4.learnsmate.lecture.domain.entity;

import intbyte4.learnsmate.lecture.domain.dto.LectureDTO;
import intbyte4.learnsmate.lecture.domain.vo.request.RequestEditLectureInfoVO;
import intbyte4.learnsmate.lecture.enums.LectureCategoryEnum;
import intbyte4.learnsmate.lecture.enums.LectureLevelEnum;
import intbyte4.learnsmate.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Entity(name = "lecture")
@Table(name = "lecture")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_code", nullable = false)
    private Long lectureCode;

    @Column(name = "lecture_title", nullable = false)
    private String lectureTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_category", nullable = false)
    private LectureCategoryEnum lectureCategoryEnum;

    @Column(name = "lecture_confirm_status", nullable = false)
    private Boolean lectureConfirmStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "lecture_image", nullable = false , length = 3000)
    private String lectureImage;

    @Column(name = "lecture_price", nullable = false)
    private Integer lecturePrice;

    @ManyToOne
    @JoinColumn(name = "tutor_code", nullable = false)
    @Where(clause = "member_type = 'TUTOR'")
    private Member tutorCode;

    @Column(name = "lecture_status", nullable = false)
    private Boolean lectureStatus;

    @Column(name = "lecture_click_count", nullable = false)
    private Integer lectureClickCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_level", nullable = false)
    private LectureLevelEnum lectureLevel;


    public void toUpdate(@Validated RequestEditLectureInfoVO requestEditLectureInfoVO) {
         this.lectureTitle = requestEditLectureInfoVO.getLectureTitle();
         this.lectureCategoryEnum = requestEditLectureInfoVO.getLectureCategoryEnum();
         this.lectureConfirmStatus = requestEditLectureInfoVO.getLectureConfirmStatus();
         this.updatedAt = LocalDateTime.now();
         this.lectureImage = requestEditLectureInfoVO.getLectureImage();
         this.lecturePrice = requestEditLectureInfoVO.getLecturePrice();
         this.lectureStatus = requestEditLectureInfoVO.getLectureStatus();
         this.lectureClickCount = requestEditLectureInfoVO.getLectureClickCount();
         this.lectureLevel = requestEditLectureInfoVO.getLectureLevel();
    }

    public void toDelete(LectureDTO lectureDTO){
        this.lectureStatus = false;
        this.updatedAt = LocalDateTime.now();
    }

}
