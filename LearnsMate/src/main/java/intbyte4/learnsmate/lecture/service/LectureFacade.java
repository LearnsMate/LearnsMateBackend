package intbyte4.learnsmate.lecture.service;

import intbyte4.learnsmate.common.exception.CommonException;
import intbyte4.learnsmate.common.exception.StatusEnum;
import intbyte4.learnsmate.admin.domain.dto.AdminDTO;
import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.admin.mapper.AdminMapper;
import intbyte4.learnsmate.admin.service.AdminService;
import intbyte4.learnsmate.coupon.domain.dto.CouponDTO;
import intbyte4.learnsmate.coupon.domain.entity.CouponEntity;
import intbyte4.learnsmate.coupon.mapper.CouponMapper;
import intbyte4.learnsmate.coupon.service.CouponService;
import intbyte4.learnsmate.coupon_by_lecture.domain.dto.CouponByLectureDTO;
import intbyte4.learnsmate.coupon_by_lecture.service.CouponByLectureService;
import intbyte4.learnsmate.coupon_category.domain.CouponCategory;
import intbyte4.learnsmate.coupon_category.service.CouponCategoryServiceImpl;
import intbyte4.learnsmate.issue_coupon.domain.dto.IssueCouponDTO;
import intbyte4.learnsmate.issue_coupon.service.IssueCouponService;
import intbyte4.learnsmate.lecture.domain.dto.LectureDTO;
import intbyte4.learnsmate.lecture.domain.dto.LectureDetailDTO;
import intbyte4.learnsmate.lecture.domain.entity.Lecture;
import intbyte4.learnsmate.lecture.domain.entity.LectureLevelEnum;
import intbyte4.learnsmate.lecture.domain.vo.response.ResponseFindLectureDetailVO;
import intbyte4.learnsmate.lecture.mapper.LectureMapper;
import intbyte4.learnsmate.lecture.pagination.CursorPaginationResponse;
import intbyte4.learnsmate.lecture.repository.LectureRepository;
import intbyte4.learnsmate.lecture_by_student.domain.entity.LectureByStudent;
import intbyte4.learnsmate.lecture_by_student.repository.LectureByStudentRepository;
import intbyte4.learnsmate.lecture_by_student.service.LectureByStudentService;
import intbyte4.learnsmate.lecture_category_by_lecture.service.LectureCategoryByLectureService;
import intbyte4.learnsmate.member.domain.MemberType;
import intbyte4.learnsmate.member.domain.dto.MemberDTO;
import intbyte4.learnsmate.member.domain.entity.Member;
import intbyte4.learnsmate.member.mapper.MemberMapper;
import intbyte4.learnsmate.member.service.MemberService;
import intbyte4.learnsmate.payment.service.PaymentService;
import intbyte4.learnsmate.video_by_lecture.domain.dto.VideoByLectureDTO;
import intbyte4.learnsmate.video_by_lecture.service.VideoByLectureService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureFacade {
    private final LectureRepository lectureRepository;
    private final LectureMapper lectureMapper;
    private final CouponService couponService;
    private final LectureByStudentService lectureByStudentService;
    private final LectureByStudentRepository lectureByStudentRepository;
    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final VideoByLectureService videoByLectureService;
    private final LectureService lectureService;
    private final LectureCategoryByLectureService lectureCategoryByLectureService;
    private final CouponByLectureService couponByLectureService;
    private final CouponMapper couponMapper;
    private final CouponCategoryServiceImpl couponCategoryService;
    private final AdminService adminService;
    private final AdminMapper adminMapper;
    private final IssueCouponService issueCouponService;
    private final PaymentService paymentService;

    @Transactional
    public LectureDTO discountLecturePrice(LectureDTO lectureDTO, IssueCouponDTO issueCouponDTO) {
        Result result = getResult(lectureDTO, issueCouponDTO);
        applyDiscountAndSetStatus(result);
        return lectureDTO;
    }

    private void applyDiscountAndSetStatus(Result result) {
        CouponEntity couponEntity;

        if (result.adminDTO() == null) {
            couponEntity = couponMapper.toTutorCouponEntity(result.couponDTOInfo(), result.couponCategory(), result.tutor());
            CouponByLectureDTO couponByLectureDTO = couponByLectureService.findByCouponAndLecture(result.lecture(), couponEntity);

            Long couponCode = couponByLectureDTO.getCouponCode();
            adaptCoupon(result, couponCode, couponEntity);
        } else {
            Admin admin = adminMapper.toEntity(result.adminDTO());
            couponEntity = couponMapper.toAdminCouponEntity(result.couponDTOInfo(), result.couponCategory(), admin);

            Long couponCode = couponEntity.getCouponCode();
            adaptCoupon(result, couponCode, couponEntity);
        }
    }

    private void adaptCoupon(Result result, Long couponCode, CouponEntity couponEntity) {
        CouponDTO couponDTO = couponService.findCouponDTOByCouponCode(couponCode);
        result.lectureDTO().setLecturePrice(result.lectureDTO().getLecturePrice() * (1 - couponDTO.getCouponDiscountRate() / 100));
        result.issueCouponDTO().setCouponUseStatus(true);

        MemberDTO studentDTO = memberService.findByStudentCode(result.issueCouponDTO.getStudentCode());
        Member student = memberMapper.fromMemberDTOtoMember(studentDTO);
        issueCouponService.updateCouponUseStatus(result.issueCouponDTO(), student, couponEntity);
    }

    private Result getResult(LectureDTO lectureDTO, IssueCouponDTO issueCouponDTO) {
        MemberDTO memberDTO = memberService.findMemberByMemberCode(lectureDTO.getTutorCode(), MemberType.TUTOR);
        Member tutor = memberMapper.fromMemberDTOtoMember(memberDTO);
        Lecture lecture = lectureMapper.toEntity(lectureDTO, tutor);
        CouponDTO couponDTOInfo = couponService.findCouponDTOByCouponCode(issueCouponDTO.getCouponCode());
        CouponCategory couponCategory = couponCategoryService.findByCouponCategoryCode(couponDTOInfo.getCouponCategoryCode());
        AdminDTO adminDTO = adminService.findByAdminCode(couponDTOInfo.getAdminCode());

        return new Result(lectureDTO, tutor, lecture, issueCouponDTO, couponDTOInfo, couponCategory, adminDTO);
    }

    public CursorPaginationResponse<LectureDetailDTO> getLecturesWithPagination(LocalDateTime cursor, int pageSize) {
        List<LectureDTO> lectureDTOs = lectureService.getLecturesWithPagination(cursor, pageSize);
        List<LectureDetailDTO> lectureDetailDTOs = lectureDTOs.stream()
                .map(this::buildLectureDetailDTO)
                .collect(Collectors.toList());

        LocalDateTime nextCursor = lectureDTOs.isEmpty() ? null : lectureDTOs.get(lectureDTOs.size() - 1).getCreatedAt();

        return new CursorPaginationResponse<>(lectureDetailDTOs, nextCursor);
    }

    private record Result(LectureDTO lectureDTO, Member tutor, Lecture lecture, IssueCouponDTO issueCouponDTO, CouponDTO couponDTOInfo, CouponCategory couponCategory, AdminDTO adminDTO) {
    }

    @Transactional
    public LectureDTO removeLecture(String lectureCode) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new CommonException(StatusEnum.LECTURE_NOT_FOUND));
        lecture.toDelete();
        lectureRepository.save(lecture);

        updateOwnStatus(lecture);
        return lectureMapper.toDTO(lecture);
    }

    @Transactional
    public void updateOwnStatus(Lecture lecture) {
        List<Long> lectureByStudentCodes = lectureByStudentRepository.findLectureByStudentCodesByLectureCode(lecture.getLectureCode());
        List<LectureByStudent> lectureByStudents = lectureByStudentRepository.findAllById(lectureByStudentCodes);
        for (LectureByStudent lectureByStudent : lectureByStudents) {
            lectureByStudent.changeOwnStatus();
        }

        lectureByStudentRepository.saveAll(lectureByStudents);
    }

    @Transactional
    public LectureDTO registerLecture(LectureDTO lectureDTO, List<Integer> lectureCategoryCodeList, List<VideoByLectureDTO> videoByLectureDTOList) {
        MemberDTO memberDTO = memberService.findMemberByMemberCode(lectureDTO.getTutorCode(), MemberType.TUTOR);
        Member tutor = memberMapper.fromMemberDTOtoMember(memberDTO);

        Lecture lecture = Lecture.builder()
                .lectureTitle(lectureDTO.getLectureTitle())
                .lectureConfirmStatus(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lectureImage(lectureDTO.getLectureImage())
                .lecturePrice(lectureDTO.getLecturePrice())
                .tutor(tutor)
                .lectureStatus(true)
                .lectureClickCount(0)
                .lectureLevel(LectureLevelEnum.valueOf(lectureDTO.getLectureLevel()))
                .build();

        setLectureCode(lecture, lectureCategoryCodeList);
        lectureRepository.save(lecture);

        for (Integer lectureCategoryCode : lectureCategoryCodeList) {
            lectureCategoryByLectureService.saveLectureCategoryByLecture(lecture.getLectureCode(), lectureCategoryCode);
        }

        videoByLectureDTOList.forEach(videoDTO -> videoByLectureService.registerVideoByLecture(lecture.getLectureCode(), videoDTO));

        return lectureMapper.toDTO(lecture);
    }

    private void setLectureCode(Lecture lecture, List<Integer> lectureCategoryCodeList) {
        String lectureCodePrefix;
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomUUID = UUID.randomUUID().toString().substring(0, 8);

        if (lectureCategoryCodeList.contains(7)) lectureCodePrefix = "L007";
        else if (lectureCategoryCodeList.size() > 1) lectureCodePrefix = "Lmul";
        else lectureCodePrefix = "L" + String.format("%03d", lectureCategoryCodeList.get(0));

        lecture.setLectureCode(lectureCodePrefix + "-" + date + "-" + randomUUID);
    }

    public ResponseFindLectureDetailVO getLectureById(String lectureCode) {
        LectureDTO lectureDTO = lectureService.getLectureById(lectureCode);
        if (lectureDTO == null) {
            throw new CommonException(StatusEnum.LECTURE_NOT_FOUND);
        }

        int purchaseCount = paymentService.getPurchaseCountByLectureCode(lectureCode);
        double purchaseConversionRate = calculateConversionRate(lectureDTO.getLectureClickCount(), purchaseCount);
        String lectureCategoryName = lectureCategoryByLectureService.findLectureCategoryNameByLectureCode(lectureCode);

        return ResponseFindLectureDetailVO.builder()
                .lectureCode(lectureDTO.getLectureCode())
                .lectureTitle(lectureDTO.getLectureTitle())
                .tutorCode(lectureDTO.getTutorCode())
                .lectureCategoryName(lectureCategoryName)
                .lectureLevel(LectureLevelEnum.valueOf(lectureDTO.getLectureLevel()))
                .createdAt(lectureDTO.getCreatedAt())
                .lecturePrice(lectureDTO.getLecturePrice())
                .lectureConfirmStatus(lectureDTO.getLectureConfirmStatus())
                .lectureStatus(lectureDTO.getLectureStatus())
                .lectureImage(lectureDTO.getLectureImage())
                .lectureClickCount(lectureDTO.getLectureClickCount())
                .purchaseCount(purchaseCount)
                .purchaseConversionRate(purchaseConversionRate)
                .build();
    }

    private double calculateConversionRate(int clickCount, int purchaseCount) {
        if (clickCount == 0) {
            return 0.0;
        }
        return (double) purchaseCount / clickCount * 100;
    }

    private LectureDetailDTO buildLectureDetailDTO(LectureDTO lecture) {
        MemberDTO tutor = memberService.findMemberByMemberCode(lecture.getTutorCode(), MemberType.TUTOR);
        long totalStudents = lectureByStudentService.countStudentsByLectureAndOwnStatus(lecture.getLectureCode());
        int totalRevenue = lectureByStudentService.calculateTotalRevenue(lecture.getLectureCode());
        List<VideoByLectureDTO> lectureVideos = videoByLectureService.findVideoByLectureByLectureCode(lecture.getLectureCode());
        List<String> lectureCategories = lectureCategoryByLectureService.findCategoryNamesByLectureCode(lecture.getLectureCode());

        return LectureDetailDTO.builder()
                .lectureCode(lecture.getLectureCode())
                .lectureTitle(lecture.getLectureTitle())
                .lectureConfirmStatus(lecture.getLectureConfirmStatus())
                .createdAt(lecture.getCreatedAt())
                .lectureImage(lecture.getLectureImage())
                .lecturePrice(lecture.getLecturePrice())
                .tutorCode(tutor.getMemberCode())
                .tutorName(tutor.getMemberName())
                .lectureStatus(lecture.getLectureStatus())
                .lectureCategory(String.join(", ", lectureCategories))
                .lectureClickCount(lecture.getLectureClickCount())
                .lectureLevel(lecture.getLectureLevel())
                .totalStudents((int) totalStudents)
                .totalRevenue(totalRevenue)
                .lectureVideos(lectureVideos)
                .build();
    }

    @Transactional
    public LectureDTO updateLecture(LectureDTO lectureDTO, String newVideoTitle, String newVideoLink, List<Integer> lectureCategoryCodeList) {
        Lecture lecture = lectureRepository.findById(lectureDTO.getLectureCode())
                .orElseThrow(() -> new CommonException(StatusEnum.LECTURE_NOT_FOUND));
        lecture.toUpdate(lectureDTO);
        lectureRepository.save(lecture);

        List<VideoByLectureDTO> videoByLectureDTOs = videoByLectureService.findVideoByLectureByLectureCode(lectureDTO.getLectureCode());
        for (VideoByLectureDTO videoDTO : videoByLectureDTOs) {
            VideoByLectureDTO updatedVideoDTO = VideoByLectureDTO.builder()
                    .lectureCode(videoDTO.getLectureCode())
                    .videoTitle(newVideoTitle)
                    .videoLink(newVideoLink)
                    .build();
            videoByLectureService.updateVideoByLecture(updatedVideoDTO);
        }

        if (!lectureCategoryCodeList.isEmpty()) {
            lectureCategoryByLectureService.deleteByLectureCode(lecture.getLectureCode());

            for (Integer lectureCategoryCode : lectureCategoryCodeList) {
                lectureCategoryByLectureService.saveLectureCategoryByLecture(lecture.getLectureCode(), lectureCategoryCode);
            }
        }

        return lectureMapper.toDTO(lecture);
    }
}