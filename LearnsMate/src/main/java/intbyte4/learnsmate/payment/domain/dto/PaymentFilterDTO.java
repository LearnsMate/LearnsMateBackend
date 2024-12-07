package intbyte4.learnsmate.payment.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class PaymentFilterDTO {
    @JsonProperty("payment_code")
    private Long paymentCode;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("lecture_code")
    private String lectureCode;

    @JsonProperty("lecture_title")
    private String lectureTitle;

    @JsonProperty("lecture_category_name")
    private String lectureCategoryName;

    @JsonProperty("tutor_code")
    private Long tutorCode;

    @JsonProperty("tutor_name")
    private String tutorName;

    @JsonProperty("student_code")
    private Long studentCode;

    @JsonProperty("student_name")
    private String studentName;

    @JsonProperty("payment_price")
    private Integer paymentPrice;

    @JsonProperty("coupon_issuance_code")
    private String couponIssuanceCode;

    @JsonProperty("coupon_issuance_name")
    private String couponIssuanceName;

    @JsonProperty("lecture_price")
    private Integer lecturePrice;

    public PaymentFilterDTO(Long paymentCode, LocalDateTime createdAt, String lectureCode,
                            String lectureTitle, String lectureCategoryName, Long tutorCode,
                            String tutorName, Long studentCode, String studentName,
                            Integer paymentPrice, String couponIssuanceCode, String couponIssuanceName,
                            Integer lecturePrice) {
        this.paymentCode = paymentCode;
        this.createdAt = createdAt;
        this.lectureCode = lectureCode;
        this.lectureTitle = lectureTitle;
        this.lectureCategoryName = lectureCategoryName;
        this.tutorCode = tutorCode;
        this.tutorName = tutorName;
        this.studentCode = studentCode;
        this.studentName = studentName;
        this.paymentPrice = paymentPrice;
        this.couponIssuanceCode = couponIssuanceCode;
        this.couponIssuanceName = couponIssuanceName;
        this.lecturePrice = lecturePrice;
    }
}
