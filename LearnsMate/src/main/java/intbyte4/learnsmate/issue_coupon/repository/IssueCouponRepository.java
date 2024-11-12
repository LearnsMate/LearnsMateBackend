package intbyte4.learnsmate.issue_coupon.repository;

import intbyte4.learnsmate.issue_coupon.domain.IssueCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("issueCouponRepository")
public interface IssueCouponRepository extends JpaRepository<IssueCoupon, Long> {

    @Query("SELECT ic FROM issueCoupon ic " +
            "WHERE ic.student.memberCode = :studentCode " +
              "AND ic.coupon.couponFlag = true " +
              "AND ic.couponUseStatus = false")
    List<IssueCoupon> findAllByStudentAndActiveCoupon(@Param("studentCode") Long studentCode);

    @Query("SELECT ic FROM issueCoupon ic " +
            "WHERE ic.couponIssuanceCode = :couponIssuanceCode " +
              "AND ic.student.memberCode = :studentCode")
    Optional<IssueCoupon> findByCouponIssuanceCodeAndStudentCode(@Param("couponIssuanceCode") String couponIssuanceCode, @Param("studentCode") Long studentCode);

    List<IssueCoupon> findCouponsByStudent_MemberCode(@Param("studentCode") Long studentCode);

}
