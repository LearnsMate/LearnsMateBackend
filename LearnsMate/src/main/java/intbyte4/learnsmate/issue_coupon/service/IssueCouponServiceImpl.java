package intbyte4.learnsmate.issue_coupon.service;

import intbyte4.learnsmate.common.exception.CommonException;
import intbyte4.learnsmate.common.exception.StatusEnum;
import intbyte4.learnsmate.coupon.domain.entity.CouponEntity;
import intbyte4.learnsmate.coupon.service.CouponService;
import intbyte4.learnsmate.issue_coupon.domain.IssueCoupon;
import intbyte4.learnsmate.issue_coupon.domain.dto.IssueCouponDTO;
import intbyte4.learnsmate.issue_coupon.mapper.IssueCouponMapper;
import intbyte4.learnsmate.issue_coupon.repository.IssueCouponRepository;
import intbyte4.learnsmate.member.domain.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("issueCouponService")
@RequiredArgsConstructor
public class IssueCouponServiceImpl implements IssueCouponService {

    private final IssueCouponRepository issueCouponRepository;
    private final IssueCouponMapper issueCouponMapper;
    private final CouponService couponService;

    @Override
    @Transactional
    public IssueCouponDTO createAndSaveIssueCoupon(Member student, Long couponCode) {
        CouponEntity coupon = couponService.findByCouponCode(couponCode);
        IssueCoupon issueCoupon = IssueCoupon.createIssueCoupon(coupon, student);
        issueCouponRepository.save(issueCoupon);

        return issueCouponMapper.toDTO(issueCoupon);
    }

    @Override
    public List<IssueCouponDTO> findIssuedCouponsByStudent(Long studentCode) {
        List<IssueCoupon> issueCouponList = issueCouponRepository.findAllByStudentAndActiveCoupon(studentCode);
        return issueCouponList.stream()
                .map(issueCouponMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IssueCouponDTO useIssuedCoupon(Long studentCode, String couponIssuanceCode) {
        IssueCoupon issueCoupon = issueCouponRepository.findByCouponIssuanceCodeAndStudentCode(couponIssuanceCode, studentCode).orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));

        issueCoupon.useCoupon();
        issueCouponRepository.save(issueCoupon);
        return issueCouponMapper.toDTO(issueCoupon);
    }

    // 보유중인 쿠폰 조회
    @Override
    @Transactional
    public List<IssueCouponDTO> findAllStudentCoupons(IssueCouponDTO dto, Long studentCode) {
        // 발급일이 오늘 이전, 사용 여부는 false, 쿠폰 사용 일자 null인 것들만 조회
        if (studentCode == null) {
            throw new CommonException(StatusEnum.STUDENT_NOT_FOUND);
        }
        if (dto.getCouponIssueDate().isBefore(LocalDateTime.now()) && !dto.getCouponUseStatus() && dto.getCouponUseDate() == null) {
            List<IssueCoupon> coupons = issueCouponRepository.findCouponsByStudent_MemberCode(studentCode);
            return coupons.stream()
                    .map(issueCouponMapper::toDTO)
                    .collect(Collectors.toList());
        }
        throw new CommonException(StatusEnum.ISSUE_COUPON_NOT_FOUND);
    }
    // 사용한 쿠폰 조회
    @Override
    @Transactional
    public List<IssueCouponDTO> findAllUsedStudentCoupons(IssueCouponDTO dto, Long studentCode) {
        if (studentCode == null) {
            throw new CommonException(StatusEnum.STUDENT_NOT_FOUND);
        }
        if (dto.getCouponIssueDate().isBefore(LocalDateTime.now()) && dto.getCouponUseStatus() && dto.getCouponUseDate() != null) {
            List<IssueCoupon> coupons = issueCouponRepository.findCouponsByStudent_MemberCode(studentCode);
            return coupons.stream()
                    .map(issueCouponMapper::toDTO)
                    .collect(Collectors.toList());
        }
        throw new CommonException(StatusEnum.ISSUE_COUPON_NOT_FOUND);
    }
}
