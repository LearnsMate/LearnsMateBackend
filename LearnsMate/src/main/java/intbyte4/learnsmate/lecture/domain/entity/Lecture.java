package intbyte4.learnsmate.lecture.domain.entity;

import intbyte4.learnsmate.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

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
    @Column(name = "lecture_code")
    private Long lectureCode;

    @Column(name = "lecture_title", nullable = false)
    private String lectureTitle;

    @Column(name = "lecture_category", nullable = false)
    private Integer lectureCategory;

    @Column(name = "lecture_confirm_status")
    private Boolean lectureConfirmStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "lecture_image", length = 3000)
    private String lectureImage;

    @Column(name = "lecture_price")
    private Integer lecturePrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_code")
    @Where(clause = "member_type = 'TUTOR'")
    private Member tutor;

    @Column(name = "lecture_status")
    private Boolean lectureStatus;

    @Column(name = "lecture_click_count")
    private Integer lectureClickCount;

    @Column(name = "lecture_level")
    private Integer lectureLevel;
}
