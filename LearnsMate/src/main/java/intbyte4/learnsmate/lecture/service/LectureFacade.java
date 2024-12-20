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
import intbyte4.learnsmate.lecture.domain.dto.LectureFilterDTO;
import intbyte4.learnsmate.lecture.domain.dto.LectureStatsFilterDTO;
import intbyte4.learnsmate.lecture.domain.entity.Lecture;
import intbyte4.learnsmate.lecture.domain.entity.LectureLevelEnum;
import intbyte4.learnsmate.lecture.domain.vo.response.LectureStatsVO;
import intbyte4.learnsmate.lecture.domain.vo.response.ResponseFindLectureDetailVO;
import intbyte4.learnsmate.lecture.domain.vo.response.ResponseFindLectureVO;
import intbyte4.learnsmate.lecture.mapper.LectureMapper;
import intbyte4.learnsmate.lecture.pagination.LecturePaginationResponse;
import intbyte4.learnsmate.lecture.repository.LectureRepository;
import intbyte4.learnsmate.lecture_by_student.domain.entity.LectureByStudent;
import intbyte4.learnsmate.lecture_by_student.repository.LectureByStudentRepository;
import intbyte4.learnsmate.lecture_category.domain.dto.LectureCategoryDTO;
import intbyte4.learnsmate.lecture_category.domain.entity.LectureCategoryEnum;
import intbyte4.learnsmate.lecture_category.service.LectureCategoryService;
import intbyte4.learnsmate.lecture_category_by_lecture.domain.dto.LectureCategoryByLectureDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LectureFacade {
    private final LectureRepository lectureRepository;
    private final LectureMapper lectureMapper;
    private final CouponService couponService;
    private final LectureByStudentRepository lectureByStudentRepository;
    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final VideoByLectureService videoByLectureService;
    private final LectureService lectureService;
    private final LectureCategoryService lectureCategoryService;
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

    private LectureDTO buildLectureDTOWithDetails(Lecture lecture) {
        return LectureDTO.builder()
                .lectureCode(lecture.getLectureCode())
                .lectureTitle(lecture.getLectureTitle())
                .lectureConfirmStatus(lecture.getLectureConfirmStatus())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt())
                .lectureImage(lecture.getLectureImage())
                .lecturePrice(lecture.getLecturePrice())
                .tutorCode(memberService.findById(lecture.getTutor().getMemberCode()).getMemberCode())
                .lectureStatus(lecture.getLectureStatus())
                .lectureClickCount(lecture.getLectureClickCount())
                .lectureLevel(String.valueOf(lecture.getLectureLevel()))
                .build();
    }

    public LecturePaginationResponse<ResponseFindLectureVO> getLecturesWithPaginationByOffset(int page, int size) {
        Page<Lecture> lectures = lectureRepository.findLecturesByOffset(PageRequest.of(page, size));
        List<ResponseFindLectureVO> responseList = new ArrayList<>();

        for (Lecture lecture : lectures) {
            LectureDTO lectureDTO = buildLectureDTOWithDetails(lecture);
            MemberDTO memberDTO = memberService.findById(lectureDTO.getTutorCode());
            LectureCategoryByLectureDTO lectureCategoryByLecture = lectureCategoryByLectureService.findLectureCategoryByLectureCode(lectureDTO.getLectureCode());
            LectureCategoryDTO lectureCategoryDTO = lectureCategoryService.findByLectureCategoryCode(lectureCategoryByLecture.getLectureCategoryCode());

            ResponseFindLectureVO responseFindLectureVO = lectureMapper.fromDTOToResponseVOAll(lectureDTO, memberDTO, lectureCategoryDTO);

            responseList.add(responseFindLectureVO);
        }

        return new LecturePaginationResponse<>(
                responseList,
                lectures.getTotalElements(),
                lectures.getTotalPages(),
                lectures.getNumber(),
                lectures.getSize()
        );
    }

    public LecturePaginationResponse<ResponseFindLectureVO> getLecturesWithPaginationByOffsetWithSort(
            int page, int size, String sortField, String sortDirection) {

        Sort sort = Sort.by(
                sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortField
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResponseFindLectureVO> lectures = lectureRepository.findLecturesWithSorting(pageable);

        return new LecturePaginationResponse<>(
                lectures.getContent(),
                lectures.getTotalElements(),
                lectures.getTotalPages(),
                lectures.getNumber(),
                lectures.getSize()
        );
    }

    public LecturePaginationResponse<ResponseFindLectureVO> filterLecturesByPage(
            LectureFilterDTO filterDTO, int page, int size, String sortField, String sortDirection) {
        Page<LectureDTO> lectures = lectureService.filterLectureWithPaging(filterDTO, PageRequest.of(page, size));
        log.info("{}", lectures.toString());
        List<ResponseFindLectureVO> responseList = new ArrayList<>();

        for (LectureDTO lectureDTO : lectures) {
            MemberDTO memberDTO = memberService.findById(lectureDTO.getTutorCode());
            LectureCategoryByLectureDTO lectureCategoryByLecture = lectureCategoryByLectureService.findLectureCategoryByLectureCode(lectureDTO.getLectureCode());
            LectureCategoryDTO lectureCategoryDTO = lectureCategoryService.findByLectureCategoryCode(lectureCategoryByLecture.getLectureCategoryCode());

            ResponseFindLectureVO responseFindLectureVO = lectureMapper.fromDTOToResponseVOAll(lectureDTO, memberDTO, lectureCategoryDTO);

            responseList.add(responseFindLectureVO);
            log.info("{}", responseFindLectureVO.toString());
        }

        log.info("{}, {}", lectures.getSize(), responseList.size());

        return new LecturePaginationResponse<>(
                responseList,
                lectures.getTotalElements(),
                lectures.getTotalPages(),
                lectures.getNumber() + 1,
                lectures.getSize()
        );
    }

    public LecturePaginationResponse<ResponseFindLectureVO> filterLecturesByPageWithSort(
            LectureFilterDTO filterDTO, int page, int size, String sortField, String sortDirection) {

        Sort sort = Sort.by(
                sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortField
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResponseFindLectureVO> lectures = lectureRepository.searchByWithPaging(filterDTO, pageable);

        return new LecturePaginationResponse<>(
                lectures.getContent(),
                lectures.getTotalElements(),
                lectures.getTotalPages(),
                lectures.getNumber(),
                lectures.getSize()
        );
    }

    public ResponseFindLectureDetailVO getLectureDetailsWithConversionRates(String lectureCode) {
        LectureDTO lectureDTO = lectureService.getLectureById(lectureCode);
        if (lectureDTO == null) throw new CommonException(StatusEnum.LECTURE_NOT_FOUND);

        MemberDTO memberDTO = memberService.findById(lectureDTO.getTutorCode());
        LectureCategoryByLectureDTO lectureCategoryByLecture = lectureCategoryByLectureService.findLectureCategoryByLectureCode(lectureDTO.getLectureCode());
        LectureCategoryDTO lectureCategoryDTO = lectureCategoryService.findByLectureCategoryCode(lectureCategoryByLecture.getLectureCategoryCode());
        LectureCategoryEnum categoryEnum = LectureCategoryEnum.fromValue(lectureCategoryDTO.getLectureCategoryName());

        int purchaseCount = paymentService.getPurchaseCountByLectureCode(lectureCode);
        double singleLectureRate = calculateConversionRate(lectureDTO.getLectureClickCount(), purchaseCount);

        List<VideoByLectureDTO> videoByLectureDTOS = videoByLectureService.findVideoByLectureByLectureCode(lectureCode);

        int totalClickCount = lectureService.getTotalClickCount();
        int totalPurchaseCount = paymentService.getTotalPurchaseCount();
        double overallRate = calculateConversionRate(totalClickCount, totalPurchaseCount);

        int categoryClickCount = lectureService.getClickCountByCategory(String.valueOf(categoryEnum));
        int categoryPurchaseCount = paymentService.getPurchaseCountByCategory(String.valueOf(categoryEnum));
        double categoryRate = calculateConversionRate(categoryClickCount, categoryPurchaseCount);

        return ResponseFindLectureDetailVO.builder()
                .lectureCode(lectureDTO.getLectureCode())
                .lectureTitle(lectureDTO.getLectureTitle())
                .tutorCode(lectureDTO.getTutorCode())
                .tutorName(memberDTO.getMemberName())
                .lectureCategoryName(String.valueOf(categoryEnum))
                .lectureLevel(LectureLevelEnum.valueOf(lectureDTO.getLectureLevel()))
                .createdAt(lectureDTO.getCreatedAt())
                .lecturePrice(lectureDTO.getLecturePrice())
                .lectureConfirmStatus(lectureDTO.getLectureConfirmStatus())
                .lectureStatus(lectureDTO.getLectureStatus())
                .lectureImage(lectureDTO.getLectureImage())
                .lectureClickCount(lectureDTO.getLectureClickCount())
                .purchaseCount(purchaseCount)
                .singleLectureConversionRate((int) singleLectureRate)
                .overallConversionRate((int) overallRate)
                .categoryConversionRate((int) categoryRate)
                .lectureVideos(videoByLectureDTOS)
                .build();
    }

    private double calculateConversionRate(Integer clickCount, Integer purchaseCount) {
        if (clickCount == null || clickCount == 0 || purchaseCount == null) {
            return 0.0;
        }
        return (double) purchaseCount / clickCount * 100;
    }

    public LectureStatsVO getLectureStatsWithFilterAndRates(String lectureCode, LectureStatsFilterDTO filter) {
        LectureDTO lectureDTO = lectureService.getLectureById(lectureCode);
        if (lectureDTO == null) throw new CommonException(StatusEnum.LECTURE_NOT_FOUND);

        LocalDateTime startDate = LocalDateTime.of(filter.getStartYear(), filter.getStartMonth(), 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(
                filter.getEndYear(),
                filter.getEndMonth(),
                filter.getEndMonth() == 12 ? 31 : YearMonth.of(filter.getEndYear(), filter.getEndMonth()).lengthOfMonth(),
                23, 59, 59
        );

        Integer totalStudentCount = paymentService.getTotalStudentCountBetween(startDate, endDate);
        Integer totalClickCount = lectureService.getTotalClickCountBetween(startDate, endDate);
        Double totalConversionRate = calculateConversionRate(totalClickCount, totalStudentCount);

        Integer studentCount = paymentService.getStudentCountByLectureCodeBetween(lectureCode, startDate, endDate);
        Integer clickCount = lectureService.getClickCountByLectureCodeBetween(lectureCode, startDate, endDate);
        Double conversionRate = calculateConversionRate(clickCount, studentCount);

        LectureCategoryByLectureDTO lectureCategoryByLecture = lectureCategoryByLectureService.findLectureCategoryByLectureCode(lectureDTO.getLectureCode());
        LectureCategoryDTO lectureCategoryDTO = lectureCategoryService.findByLectureCategoryCode(lectureCategoryByLecture.getLectureCategoryCode());
        LectureCategoryEnum categoryEnum = LectureCategoryEnum.fromValue(lectureCategoryDTO.getLectureCategoryName());

        Integer categoryClickCount = lectureService.getClickCountByCategoryWithDateRange(String.valueOf(categoryEnum), startDate, endDate);
        Integer categoryPurchaseCount = paymentService.getPurchaseCountByCategoryWithDateRange(String.valueOf(categoryEnum), startDate, endDate);
        Double categoryConversionRate = calculateConversionRate(categoryClickCount, categoryPurchaseCount);

        return LectureStatsVO.builder()
                .totalStudentCount(totalStudentCount)
                .totalLectureClickCount(totalClickCount)
                .totalConversionRate(totalConversionRate)
                .lectureCode(lectureCode)
                .lectureTitle(lectureDTO.getLectureTitle())
                .studentCount(studentCount)
                .lectureClickCount(clickCount)
                .conversionRate(conversionRate)
                .categoryClickCount(categoryClickCount)
                .categoryPurchaseCount(categoryPurchaseCount)
                .categoryConversionRate(categoryConversionRate)
                .build();
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
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .updatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
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
        String date = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomUUID = UUID.randomUUID().toString().substring(0, 8);

        if (lectureCategoryCodeList.contains(7)) lectureCodePrefix = "L007";
        else if (lectureCategoryCodeList.size() > 1) lectureCodePrefix = "Lmul";
        else lectureCodePrefix = "L" + String.format("%03d", lectureCategoryCodeList.get(0));

        lecture.setLectureCode(lectureCodePrefix + "-" + date + "-" + randomUUID);
    }

    public ResponseFindLectureDetailVO getLectureById(String lectureCode) {
        LectureDTO lectureDTO = lectureService.getLectureById(lectureCode);
        if (lectureDTO == null) throw new CommonException(StatusEnum.LECTURE_NOT_FOUND);

        MemberDTO memberDTO = memberService.findById(lectureDTO.getTutorCode());
        int purchaseCount = paymentService.getPurchaseCountByLectureCode(lectureCode);
        double purchaseConversionRate = calculateConversionRate(lectureDTO.getLectureClickCount(), purchaseCount, lectureCode);
        String lectureCategoryName = lectureCategoryByLectureService.findLectureCategoryNameByLectureCode(lectureCode);
        List<VideoByLectureDTO> videoByLectureDTOS = videoByLectureService.findVideoByLectureByLectureCode(lectureDTO.getLectureCode());
        List<String> formattedVideoTitles = new ArrayList<>();
        for (int i = 0; i < videoByLectureDTOS.size(); i++) {
            VideoByLectureDTO videoDTO = videoByLectureDTOS.get(i);
            formattedVideoTitles.add((i + 1) + "강. " + videoDTO.getVideoTitle());
        }

        return ResponseFindLectureDetailVO.builder()
                .lectureCode(lectureDTO.getLectureCode())
                .lectureTitle(lectureDTO.getLectureTitle())
                .tutorCode(lectureDTO.getTutorCode())
                .tutorName(memberDTO.getMemberName())
                .lectureCategoryName(lectureCategoryName)
                .lectureLevel(LectureLevelEnum.valueOf(lectureDTO.getLectureLevel()))
                .createdAt(lectureDTO.getCreatedAt())
                .lecturePrice(lectureDTO.getLecturePrice())
                .lectureConfirmStatus(lectureDTO.getLectureConfirmStatus())
                .lectureStatus(lectureDTO.getLectureStatus())
                .lectureImage(lectureDTO.getLectureImage())
                .lectureClickCount(lectureDTO.getLectureClickCount())
                .purchaseCount(purchaseCount)
                .purchaseConversionRate((int) purchaseConversionRate)
                .lectureVideos(videoByLectureDTOS)
                .formattedVideoTitles(formattedVideoTitles)
                .build();
    }

    private double calculateConversionRate(Integer  clickCount, Integer  purchaseCount, String lectureCode) {
        if (clickCount == 0) {
            log.warn("Click count is 0 for lectureCode: {}", lectureCode);
            return 0.0;
        }
        return (double) purchaseCount / clickCount * 100;
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

    public List<ResponseFindLectureVO> findAllLecturesByFilter(LectureFilterDTO filterDTO) {
        List<Lecture> lectures = lectureRepository.findAllByFilter(filterDTO);
        List<ResponseFindLectureVO> responseList = new ArrayList<>();

        for (Lecture lecture : lectures) {
            LectureDTO lectureDTO = lectureMapper.toDTO(lecture);
            MemberDTO tutorDTO = memberService.findById(lectureDTO.getTutorCode());
            LectureCategoryByLectureDTO lectureCategoryByLectureDTO =
                    lectureCategoryByLectureService.findLectureCategoryByLectureCode(lectureDTO.getLectureCode());
            LectureCategoryDTO lectureCategoryDTO =
                    lectureCategoryService.findByLectureCategoryCode(lectureCategoryByLectureDTO.getLectureCategoryCode());

            ResponseFindLectureVO responseVO =
                    lectureMapper.fromDTOToResponseVOAll(lectureDTO, tutorDTO, lectureCategoryDTO);
            responseList.add(responseVO);
        }

        return responseList;
    }

    public List<ResponseFindLectureVO> findAllLectures() {
        List<Lecture> lectures = lectureRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<ResponseFindLectureVO> responseList = new ArrayList<>();

        for (Lecture lecture : lectures) {
            LectureDTO lectureDTO = lectureMapper.toDTO(lecture);
            MemberDTO tutorDTO = memberService.findById(lectureDTO.getTutorCode());
            LectureCategoryByLectureDTO lectureCategoryByLectureDTO =
                    lectureCategoryByLectureService.findLectureCategoryByLectureCode(lectureDTO.getLectureCode());
            LectureCategoryDTO lectureCategoryDTO =
                    lectureCategoryService.findByLectureCategoryCode(lectureCategoryByLectureDTO.getLectureCategoryCode());

            ResponseFindLectureVO responseVO =
                    lectureMapper.fromDTOToResponseVOAll(lectureDTO, tutorDTO, lectureCategoryDTO);
            responseList.add(responseVO);
        }

        return responseList;
    }
}