package intbyte4.learnsmate.issue_coupon.mapper;

import intbyte4.learnsmate.issue_coupon.domain.IssueCoupon;
import intbyte4.learnsmate.issue_coupon.domain.dto.IssueCouponDTO;
import intbyte4.learnsmate.issue_coupon.domain.vo.response.IssueCouponRegisterResponseVO;
import org.springframework.stereotype.Component;

@Component
public class IssueCouponMapper {
    public IssueCouponDTO toDTO(IssueCoupon issueCoupon) {
        return IssueCouponDTO.builder()
                .couponIssuanceCode(issueCoupon.getCouponIssuanceCode())
                .couponIssueDate(issueCoupon.getCouponIssueDate())
                .couponUseStatus(issueCoupon.getCouponUseStatus())
                .couponUseDate(issueCoupon.getCouponUseDate())
                .studentCode(issueCoupon.getStudent().getMemberCode())
                .couponCode(issueCoupon.getCoupon().getCouponCode())
                .build();
    }

    public IssueCouponRegisterResponseVO fromDtoToResponseVO(IssueCouponDTO issueCoupon) {
        return IssueCouponRegisterResponseVO.builder()
                .couponIssuanceCode(issueCoupon.getCouponIssuanceCode())
                .couponIssueDate(issueCoupon.getCouponIssueDate())
                .couponUseStatus(issueCoupon.getCouponUseStatus())
                .couponUseDate(issueCoupon.getCouponUseDate())
                .studentCode(issueCoupon.getStudentCode())
                .couponCode(issueCoupon.getCouponCode())
                .build();
    }
}
