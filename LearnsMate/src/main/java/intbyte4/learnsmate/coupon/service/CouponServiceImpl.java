package intbyte4.learnsmate.coupon.service;

import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.common.exception.CommonException;
import intbyte4.learnsmate.common.exception.StatusEnum;
import intbyte4.learnsmate.coupon.domain.dto.CouponDTO;
import intbyte4.learnsmate.coupon.domain.entity.CouponEntity;
import intbyte4.learnsmate.coupon.domain.vo.request.AdminCouponRegisterRequestVO;
import intbyte4.learnsmate.coupon.domain.vo.request.CouponFilterRequestVO;
import intbyte4.learnsmate.coupon.domain.vo.request.TutorCouponRegisterRequestVO;
import intbyte4.learnsmate.coupon.mapper.CouponMapper;
import intbyte4.learnsmate.coupon.repository.CouponRepository;
import intbyte4.learnsmate.coupon_by_lecture.service.CouponByLectureService;
import intbyte4.learnsmate.coupon_category.domain.CouponCategory;
import intbyte4.learnsmate.lecture.domain.dto.LectureDTO;
import intbyte4.learnsmate.lecture.domain.entity.Lecture;
import intbyte4.learnsmate.lecture.mapper.LectureMapper;
import intbyte4.learnsmate.lecture.service.LectureService;
import intbyte4.learnsmate.lecture_category.domain.dto.LectureCategoryDTO;
import intbyte4.learnsmate.lecture_category.domain.entity.LectureCategory;
import intbyte4.learnsmate.lecture_category.mapper.LectureCategoryMapper;
import intbyte4.learnsmate.lecture_category.service.LectureCategoryService;
import intbyte4.learnsmate.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("couponService")
@Slf4j
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;
    private final CouponByLectureService couponByLectureService;
    private final LectureService lectureService;
    private final LectureMapper lectureMapper;
    private final LectureCategoryService lectureCategoryService;
    private final LectureCategoryMapper lectureCategoryMapper;

    // 쿠폰 전체 조회
    @Override
    public List<CouponDTO> findAllCoupons() {

        List<CouponEntity> couponEntities = couponRepository.findAll();
        List<CouponDTO> couponDTOList = new ArrayList<>();
        couponEntities.forEach(dto -> couponDTOList.add(couponMapper.toDTO(dto)));

        return couponDTOList;
    }
    // 쿠폰 단 건 조회 (쿠폰코드로)
    @Override
    public CouponDTO findCouponByCouponCode(Long couponCode) {
        CouponEntity couponEntity = couponRepository.findById(couponCode)
                .orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));
        return couponMapper.toDTO(couponEntity);
    }

    @Override
    public CouponEntity findByCouponCode(Long couponCode) {
        return couponRepository.findById(couponCode).orElseThrow(() -> new CommonException(StatusEnum.COUPON_NOT_FOUND));
    }

    // 쿠폰 필터링해서 조회
    @Override
    public List<CouponDTO> getCouponsByFilters(CouponFilterRequestVO request) {
        List<CouponEntity> entities = couponRepository.findCouponsByFilters(request);
        return entities.stream()
                .map(couponMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CouponDTO adminRegisterCoupon(AdminCouponRegisterRequestVO request, Admin admin, CouponCategory couponCategory) {
        CouponEntity newCoupon = CouponEntity.builder()
                .couponName(request.getCouponName())
                .couponContents(request.getCouponContents())
                .couponDiscountRate(request.getCouponDiscountRate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .couponStartDate(request.getCouponStartDate())
                .couponExpireDate(request.getCouponExpireDate())
                .couponFlag(true)
                .couponCategory(couponCategory)
                .admin(admin)
                .build();

        couponRepository.save(newCoupon);

        return couponMapper.toDTO(newCoupon);
    };

    @Override
    public CouponDTO tutorRegisterCoupon(TutorCouponRegisterRequestVO request, Member tutor, CouponCategory couponCategory, Long lectureCode) {
        CouponEntity newCouponEntity = couponMapper.newCouponEntity(request, tutor, couponCategory);

        Lecture lectureEntity = getLecture(tutor, lectureCode);
        couponRepository.save(newCouponEntity);
        couponByLectureService.registerCouponByLecture(lectureEntity, newCouponEntity);

        return couponMapper.toDTO(newCouponEntity);
    }
    // 쿠폰 수정
    // 쿠폰 삭제

    private Lecture getLecture(Member tutor, Long lectureCode) {
        LectureDTO lectureDTO = lectureService.getLectureById(lectureCode);
        LectureCategoryDTO lectureCategoryDTO = lectureCategoryService.findByLectureCategoryCode(lectureDTO.getLectureCategoryCode());
        LectureCategory lectureCategory = lectureCategoryMapper.toEntity(lectureCategoryDTO);
        return lectureMapper.toEntity(lectureDTO, tutor, lectureCategory);
    }
}
