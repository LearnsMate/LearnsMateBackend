package intbyte4.learnsmate.coupon.domain.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AdminCouponEditResponseVO {

    @JsonProperty("coupon_name")
    private String couponName;

    @JsonProperty("coupon_contents")
    private String couponContents;

    @JsonProperty("coupon_discount_rate")
    private Integer couponDiscountRate;

    @JsonProperty("coupon_start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime couponStartDate;

    @JsonProperty("coupon_expire_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime couponExpireDate;
}
