package intbyte4.learnsmate.coupon.service;

import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.campaign.domain.dto.FindCampaignDetailDTO;
import intbyte4.learnsmate.coupon.domain.dto.CouponDTO;
import intbyte4.learnsmate.coupon.domain.dto.CouponFilterDTO;
import intbyte4.learnsmate.coupon.domain.dto.RegisterCouponDTO;
import intbyte4.learnsmate.coupon.domain.entity.CouponEntity;
import intbyte4.learnsmate.coupon.domain.vo.request.AdminCouponRegisterRequestVO;
import intbyte4.learnsmate.coupon.domain.vo.request.CouponFilterRequestVO;
import intbyte4.learnsmate.coupon_category.domain.CouponCategory;
import intbyte4.learnsmate.lecture.domain.dto.LectureDTO;
import intbyte4.learnsmate.member.domain.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponService {

    List<CouponDTO> findAllCoupons();

    CouponDTO findCouponDTOByCouponCode(Long couponCode);

    CouponEntity findByCouponCode(Long couponCode);

//    CouponDTO adminRegisterCoupon(AdminCouponRegisterRequestVO request, Admin admin);

    Page<CouponDTO> findCouponsByCampaignCode(FindCampaignDetailDTO campaignDTO, Pageable pageable);

    @Transactional
    CouponDTO adminRegisterCoupon(CouponDTO requestCoupon
            , List<String> lectureCodeList);

    CouponDTO editAdminCoupon(CouponDTO couponDTO);

    CouponDTO editTutorCoupon(CouponDTO couponDTO);

    CouponDTO deleteAdminCoupon(Long couponCode);

    CouponDTO tutorDeleteCoupon(Long couponCode);

    @Transactional
    CouponDTO tutorInactiveCoupon(Long couponCode);

    @Transactional
    CouponDTO tutorActivateCoupon(Long couponCode);

    @Transactional
    void saveCoupon(CouponEntity couponEntity);

    List<CouponEntity> filterCoupons(CouponFilterDTO dto);
}
