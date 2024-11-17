package intbyte4.learnsmate.issue_coupon.repository;

import intbyte4.learnsmate.issue_coupon.domain.IssueCoupon;
import intbyte4.learnsmate.issue_coupon.domain.dto.AllIssuedCouponDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    // 특정 학생의 사용하지 않은 쿠폰 조회
    @Query("SELECT c FROM issueCoupon c WHERE c.student.memberCode = :studentCode AND c.couponIssueDate <= CURRENT_TIMESTAMP AND c.couponUseStatus = false AND c.couponUseDate IS NULL")
    List<IssueCoupon> findUnusedCouponsByStudentCode(@Param("studentCode") Long studentCode);

    // 특정 학생의 사용한 쿠폰 조회
    @Query("SELECT c FROM issueCoupon c WHERE c.student.memberCode = :studentCode AND c.couponIssueDate <= CURRENT_TIMESTAMP AND c.couponUseStatus = true AND c.couponUseDate IS NOT NULL")
    List<IssueCoupon> findUsedCouponsByStudentCode(@Param("studentCode") Long studentCode);

    @Query("SELECT ic FROM issueCoupon ic WHERE ic.student.memberCode = :studentCode " +
            "AND ic.coupon.couponExpireDate > :currentDate " +
            "AND ic.couponUseStatus = false " +
            "AND ic.couponUseDate IS NULL")
    List<IssueCoupon> findCouponsByStudentCodeAndExpireDate(
            @Param("studentCode") Long studentCode,
            @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT new intbyte4.learnsmate.issue_coupon.domain.dto.AllIssuedCouponDTO(" +
            "ic.couponIssuanceCode, " +
            "c.couponName, " +
            "c.couponContents, " +
            "c.couponCategory, " +
            "s.memberCode, " +
            "s.memberName, " +
            "ic.couponUseStatus, " +
            "c.couponDiscountRate, " +
            "c.couponStartDate, " +
            "c.couponExpireDate, " +
            "ic.couponUseDate, " +
            "ic.couponIssueDate) " +
            "FROM issueCoupon ic " +
            "JOIN ic.coupon c " +
            "JOIN ic.student s")
    List<AllIssuedCouponDTO> findAllIssuedCoupons();
}
