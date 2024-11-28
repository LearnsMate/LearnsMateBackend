package intbyte4.learnsmate.coupon.service;

import intbyte4.learnsmate.admin.domain.dto.AdminDTO;
import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.admin.service.AdminService;
import intbyte4.learnsmate.campaign.domain.dto.FindCampaignDetailDTO;
import intbyte4.learnsmate.common.exception.CommonException;
import intbyte4.learnsmate.common.exception.StatusEnum;
import intbyte4.learnsmate.coupon.domain.dto.CouponDTO;
import intbyte4.learnsmate.coupon.domain.dto.CouponFilterDTO;
import intbyte4.learnsmate.coupon.domain.entity.CouponEntity;
import intbyte4.learnsmate.coupon.domain.vo.request.AdminCouponRegisterRequestVO;
import intbyte4.learnsmate.coupon.mapper.CouponMapper;
import intbyte4.learnsmate.coupon.repository.CouponRepository;
import intbyte4.learnsmate.coupon_by_lecture.service.CouponByLectureService;
import intbyte4.learnsmate.coupon_category.domain.CouponCategory;
import intbyte4.learnsmate.coupon_category.domain.dto.CouponCategoryEnum;
import intbyte4.learnsmate.lecture.domain.dto.LectureDTO;
import intbyte4.learnsmate.lecture.domain.entity.Lecture;
import intbyte4.learnsmate.lecture.mapper.LectureMapper;
import intbyte4.learnsmate.lecture.service.LectureService;
import intbyte4.learnsmate.member.domain.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("couponService")
@Slf4j
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;
    private final AdminService adminService;
    private final LectureService lectureService;
    private final CouponByLectureService couponByLectureService;
    private final LectureMapper lectureMapper;

    @Override
    public List<CouponDTO> findAllCoupons() {

        List<CouponEntity> couponEntities = couponRepository.findAllByCouponFlagTrue();
        List<CouponDTO> couponDTOList = new ArrayList<>();
        couponEntities.forEach(entity -> couponDTOList.add(couponMapper.toDTO(entity)));

        return couponDTOList;
    }

    @Override
    public CouponDTO findCouponDTOByCouponCode(Long couponCode) {
        CouponEntity couponEntity = couponRepository.findById(couponCode)
                .orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));
        return couponMapper.toDTO(couponEntity);
    }

    @Override
    public CouponEntity findByCouponCode(Long couponCode) {
        return couponRepository.findById(couponCode).orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));
    }

    @Override
    public Page<CouponDTO> findCouponsByCampaignCode(FindCampaignDetailDTO campaignDTO, Pageable pageable) {
        return couponRepository.findCouponsByCampaignCode(campaignDTO.getCampaignCode(), pageable)
                .map(couponMapper::toDTO);
    }

    @Transactional
    @Override
    public CouponDTO adminRegisterCoupon(AdminCouponRegisterRequestVO request
            , Admin admin
            , List<LectureDTO> requestLectures) {

        CouponCategoryEnum categoryEnum = getCouponCategoryEnumByName(request.getCouponCategoryName());

        CouponCategory couponCategory = CouponCategory.builder()
                .couponCategoryCode(categoryEnum.getCategoryCode())
                .couponCategoryName(categoryEnum.getCategoryName())
                .build();

        CouponEntity newCoupon = CouponEntity.builder()
                .couponName(request.getCouponName())
                .couponContents(request.getCouponContents())
                .couponDiscountRate(request.getCouponDiscountRate())
                .couponStartDate(request.getCouponStartDate())
                .couponExpireDate(request.getCouponExpireDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .couponFlag(true)
                .activeState(true)
                .couponCategory(couponCategory)
                .admin(admin)
                .build();

        couponRepository.save(newCoupon);
        CouponDTO savedCoupon = couponMapper.toDTO(newCoupon);

        requestLectures.forEach(lectureDTO -> {
            LectureDTO foundLectures = lectureService.getLectureById(lectureDTO.getLectureCode());
            Lecture lectures = lectureMapper.fromDTOToEntity(lectureDTO);
            if (foundLectures == null) throw new CommonException(StatusEnum.LECTURE_NOT_FOUND);
            couponByLectureService.registerCouponByLecture(lectures, newCoupon);
        });

        return couponMapper.toDTO(newCoupon);
    }

    private CouponCategoryEnum getCouponCategoryEnumByName(String categoryName) {
        return Arrays.stream(CouponCategoryEnum.values())
                .filter(category -> category.getCategoryName().equals(categoryName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리 이름이 존재하지 않습니다: " + categoryName));
    }

    @Override
    @Transactional
    public CouponDTO editAdminCoupon(CouponDTO couponDTO, Admin admin) {
        log.info("직원 쿠폰 수정 중: {}", couponDTO);
        validAdmin(adminService, couponDTO.getAdminCode(), log);

        CouponEntity coupon = couponRepository.findById(couponDTO.getCouponCode()).orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));
        coupon.updateAdminCouponDetails(couponDTO);

        log.info("데이터베이스에 수정된 직원 쿠폰 저장 중: {}", coupon);
        CouponEntity updatedCoupon = couponRepository.save(coupon);
        log.info("수정된 직원 쿠폰 객체: {}", updatedCoupon);

        return couponMapper.fromEntityToDTO(updatedCoupon);
    }

    @Override
    @Transactional
    public CouponDTO tutorEditCoupon(CouponDTO couponDTO, Member tutor) {
        log.info("강사 쿠폰 수정 중: {}", couponDTO);
        validTutor(couponDTO, tutor);

        CouponEntity coupon = couponRepository.findById(couponDTO.getCouponCode()).orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));
        coupon.updateTutorCouponDetails(couponDTO);

        log.info("데이터베이스에 수정된 강사 쿠폰 저장 중: {}", coupon);
        CouponEntity updatedCoupon = couponRepository.save(coupon);
        log.info("수정된 강사 쿠폰 객체: {}", updatedCoupon);

        return couponMapper.fromEntityToDTO(updatedCoupon);
    }

    @Override
    @Transactional
    public CouponDTO deleteAdminCoupon(Long couponCode, Admin admin) {
        log.info("직원 쿠폰 삭제 중: couponCode = {}", couponCode);

        validAdmin(adminService, admin.getAdminCode(), log);

        CouponEntity coupon = couponRepository.findById(couponCode).orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));
        coupon.deleteCoupon();

        log.info("쿠폰 비활성화: {}", coupon);
        CouponEntity updatedCoupon = couponRepository.save(coupon);

        return couponMapper.fromEntityToDTO(updatedCoupon);
    }

    @Override
    @Transactional
    public CouponDTO tutorDeleteCoupon(CouponDTO couponDTO, Long couponCode, Member tutor) {
        log.info("강사 쿠폰 삭제 중: couponCode = {}", couponCode);

        validTutor(couponDTO, tutor);

        CouponEntity coupon = couponRepository.findById(couponCode).orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));
        coupon.deleteCoupon();

        log.info("강사 쿠폰 삭제: {}", coupon);
        CouponEntity updatedCoupon = couponRepository.save(coupon);

        return couponMapper.fromEntityToDTO(updatedCoupon);
    }

    @Override
    @Transactional
    public CouponDTO tutorInactiveCoupon(Long couponCode, CouponDTO couponDTO, Member tutor) {
        log.info("강사 쿠폰 비활성화 중: couponCode = {}", couponCode);

        validTutor(couponDTO, tutor);

        if (!couponDTO.getActiveState()) {
            throw new CommonException(StatusEnum.INACTIVATE_NOT_ALLOWED);
        }

        CouponEntity coupon = couponRepository.findById(couponCode).orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));
        coupon.inactivateCoupon();

        log.info("강사 쿠폰 비활성화: {}", coupon);
        CouponEntity updatedCoupon = couponRepository.save(coupon);

        return couponMapper.fromEntityToDTO(updatedCoupon);
    }

    @Override
    @Transactional
    public CouponDTO tutorActivateCoupon(Long couponCode, CouponDTO couponDTO, Member tutor) {
        log.info("강사 쿠폰 활성화 중: couponCode = {}", couponCode);

        validTutor(couponDTO, tutor);

        if (couponDTO.getActiveState()) {
            throw new CommonException(StatusEnum.ACTIVATE_NOT_ALLOWED);
        }

        CouponEntity coupon = couponRepository.findById(couponCode).orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));
        coupon.activateCoupon();

        log.info("강사 쿠폰 활성화: {}", coupon);
        CouponEntity updatedCoupon = couponRepository.save(coupon);

        return couponMapper.fromEntityToDTO(updatedCoupon);
    }

    @Override
    @Transactional
    public void saveCoupon(CouponEntity couponEntity) {
        couponRepository.save(couponEntity);
    }

    @Override
    public List<CouponEntity> filterCoupons(CouponFilterDTO dto) {
        return couponRepository.findCouponsByFilters(couponMapper.fromFilterDTOToFilterVO(dto));
    }

    public void validAdmin(AdminService adminService, Long adminCode, Logger log) {
        AdminDTO adminDTO = adminService.findByAdminCode(adminCode);
        if (adminDTO == null) {
            log.warn("존재하지 않는 직원 : {}", adminCode);
            throw new CommonException(StatusEnum.ADMIN_NOT_FOUND);
        }
        log.info(adminDTO.toString());
    }

    private static void validTutor(CouponDTO couponDTO, Member tutor) {
        if (!couponDTO.getTutorCode().equals(tutor.getMemberCode())) {
            log.warn("수정 권한 없음: {}", tutor);
            throw new CommonException(StatusEnum.RESTRICTED);
        }
        log.info(tutor.toString());
    }
}
