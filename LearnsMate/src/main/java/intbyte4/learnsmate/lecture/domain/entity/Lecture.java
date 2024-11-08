package intbyte4.learnsmate.lecture.domain.entity;

import intbyte4.learnsmate.lecture.domain.dto.LectureDTO;
import intbyte4.learnsmate.lecture.domain.vo.EditLectureInfoVO;
import intbyte4.learnsmate.lecture.enums.LectureCategory;
import intbyte4.learnsmate.lecture.enums.LectureLevel;
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
    private LectureCategory lectureCategory;

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
    private Member tutor;

    @Column(name = "lecture_status", nullable = false)
    private Boolean lectureStatus;

    @Column(name = "lecture_click_count", nullable = false)
    private Integer lectureClickCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_level", nullable = false)
    private LectureLevel lectureLevel;

    public LectureDTO convertToDTO() {
        return LectureDTO.builder()
                .lectureCode(this.lectureCode)
                .lectureTitle(this.lectureTitle)
                .lectureCategory(this.lectureCategory)
                .lectureConfirmStatus(this.lectureConfirmStatus)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .lectureImage(this.lectureImage)
                .lecturePrice(this.lecturePrice)
                .lectureStatus(this.lectureStatus)
                .lectureClickCount(this.lectureClickCount)
                .lectureLevel(this.lectureLevel)
                .build();

    }

    public void toUpdate(@Validated EditLectureInfoVO editLectureInfoVO) {
         this.lectureTitle = editLectureInfoVO.getLectureTitle();
         this.lectureCategory = editLectureInfoVO.getLectureCategory();
         this.lectureConfirmStatus = editLectureInfoVO.getLectureConfirmStatus();
         this.updatedAt = LocalDateTime.now();
         this.lectureImage = editLectureInfoVO.getLectureImage();
         this.lecturePrice = editLectureInfoVO.getLecturePrice();
         this.lectureStatus = editLectureInfoVO.getLectureStatus();
         this.lectureClickCount = editLectureInfoVO.getLectureClickCount();
         this.lectureLevel = editLectureInfoVO.getLectureLevel();
    }

    public void toDelete(LectureDTO lectureDTO){
        this.lectureStatus = false;
        this.updatedAt = LocalDateTime.now();
    }

}
