package intbyte4.learnsmate.coupon.controller;

import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.common.exception.CommonException;
import intbyte4.learnsmate.coupon.domain.dto.CouponDTO;
import intbyte4.learnsmate.coupon.domain.vo.request.AdminCouponEditRequestVO;
import intbyte4.learnsmate.coupon.domain.vo.request.AdminCouponRegisterRequestVO;
import intbyte4.learnsmate.coupon.domain.vo.request.CouponFilterRequestVO;
import intbyte4.learnsmate.coupon.domain.vo.request.TutorCouponRegisterRequestVO;
import intbyte4.learnsmate.coupon.domain.vo.response.AdminCouponEditResponseVO;
import intbyte4.learnsmate.coupon.domain.vo.response.CouponFilterResponseVO;
import intbyte4.learnsmate.coupon.domain.vo.response.CouponFindResponseVO;
import intbyte4.learnsmate.coupon.domain.vo.response.CouponRegisterResponseVO;
import intbyte4.learnsmate.coupon.mapper.CouponMapper;
import intbyte4.learnsmate.coupon.service.CouponService;
import intbyte4.learnsmate.coupon_category.domain.CouponCategory;
import intbyte4.learnsmate.member.domain.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("couponController")
@RequestMapping("coupon")
@RequiredArgsConstructor
@Slf4j
public class CouponController {

    private final CouponService couponService;
    private final CouponMapper couponMapper;

    @Operation(summary = "쿠폰 전체 조회")
    @GetMapping("/coupons")
    public ResponseEntity<List<CouponFindResponseVO>> getAllCoupons() {
        List<CouponDTO> couponDTOList = couponService.findAllCoupons();
        List<CouponFindResponseVO> responseList = couponMapper.fromDTOListToCouponFindVO(couponDTOList);

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(summary = "쿠폰 단 건 조회")
    @GetMapping("/coupon/{couponCode}")
    public ResponseEntity<CouponFindResponseVO> getCouponByCouponCode(@PathVariable("couponCode") Long couponCode) {
        CouponDTO couponDTO = couponService.findCouponByCouponCode(couponCode);
        CouponFindResponseVO response = couponMapper.fromDTOToFindResponseVO(couponDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "쿠폰 필터링 조회")
    @GetMapping("/filters")
    public ResponseEntity<List<CouponFilterResponseVO>> filterCoupons(@RequestBody CouponFilterRequestVO request) {
        List<CouponDTO> coupons = couponService.getCouponsByFilters(request);
        List<CouponFilterResponseVO> response = couponMapper.fromDTOToCouponFilterResponseVO(coupons);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "직원 - 쿠폰 등록")
    @PostMapping("/admin/register")
    public ResponseEntity<CouponRegisterResponseVO> createCoupon(@RequestBody AdminCouponRegisterRequestVO request, Admin admin, CouponCategory couponCategory) {
        CouponDTO couponDTO = couponService.adminRegisterCoupon(request, admin, couponCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponMapper.fromDTOToRegisterResponseVO(couponDTO));
    }

    @Operation(summary = "강사 - 쿠폰 등록")
    @PostMapping("/tutor/register")
    public ResponseEntity<CouponRegisterResponseVO> createCoupon (@RequestBody TutorCouponRegisterRequestVO request
            , Member tutor
            , CouponCategory couponCategory
            , Long lectureCode) {
        CouponDTO couponDTO = couponService.tutorRegisterCoupon(request, tutor, couponCategory, lectureCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponMapper.fromDTOToRegisterResponseVO(couponDTO));
    }

    @Operation(summary = "직원 - 쿠폰 수정")
    @PostMapping("/admin/edit")
    public ResponseEntity<?> editCoupon(@RequestBody AdminCouponEditRequestVO request, Admin admin, Long couponCode) {
        log.info("직원 쿠폰 수정 요청 : {}", request);
        try {
            CouponDTO couponDTO = couponMapper.fromEditRequestVOToDto(request);
            couponDTO.setCouponCode(couponCode);

            CouponDTO updatedCouponDTO = couponService.editAdminCoupon(couponDTO, admin);

            AdminCouponEditResponseVO response = couponMapper.fromDTOToEditResponseVO(updatedCouponDTO);

            log.info("직원 쿠폰 수정 성공: {}", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (CommonException e) {
            log.error("직원 쿠폰 수정 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예상치 못한 오류가 발생했습니다");
        }
    }
}
