package intbyte4.learnsmate.payment.service;

import intbyte4.learnsmate.admin.domain.dto.AdminDTO;
import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.common.exception.CommonException;
import intbyte4.learnsmate.common.exception.StatusEnum;
import intbyte4.learnsmate.coupon.domain.dto.CouponDTO;
import intbyte4.learnsmate.lecture.domain.dto.LectureDTO;
import intbyte4.learnsmate.lecture.domain.entity.Lecture;
import intbyte4.learnsmate.lecture.mapper.LectureMapper;
import intbyte4.learnsmate.lecture.service.LectureService;
import intbyte4.learnsmate.lecture_by_student.domain.dto.LectureByStudentDTO;
import intbyte4.learnsmate.lecture_by_student.service.LectureByStudentService;
import intbyte4.learnsmate.lecture_category.domain.dto.LectureCategoryDTO;
import intbyte4.learnsmate.lecture_category.domain.entity.LectureCategory;
import intbyte4.learnsmate.lecture_category.mapper.LectureCategoryMapper;
import intbyte4.learnsmate.lecture_category.service.LectureCategoryService;
import intbyte4.learnsmate.member.domain.dto.MemberDTO;
import intbyte4.learnsmate.member.domain.entity.Member;
import intbyte4.learnsmate.member.mapper.MemberMapper;
import intbyte4.learnsmate.member.service.MemberService;
import intbyte4.learnsmate.payment.domain.dto.PaymentDTO;
import intbyte4.learnsmate.payment.domain.entity.Payment;
import intbyte4.learnsmate.payment.mapper.PaymentMapper;
import intbyte4.learnsmate.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final LectureMapper lectureMapper;
    private final MemberMapper memberMapper;
    private final LectureCategoryMapper lectureCategoryMapper;
    private final LectureService lectureService;
    private final MemberService memberService;
    private final LectureCategoryService lectureCategoryService;;
    private final LectureByStudentService lectureByStudentService;

    // 직원이 전체 결제 내역을 조회
    @Override
    public List<PaymentDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        if (payments.isEmpty()) {
            throw new CommonException(StatusEnum.PAYMENT_NOT_FOUND);
        }
        return payments.stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }


    // 직원이 특정 결제 내역을 단건 상세 조회
    @Override
    public PaymentDTO getPaymentDetails(Long paymentCode) {
        Payment payment = paymentRepository.findById(paymentCode)
                .orElseThrow(() -> new CommonException(StatusEnum.PAYMENT_NOT_FOUND));
        return paymentMapper.toDTO(payment);
    }

    @Override
    public PaymentDTO lecturePayment(MemberDTO memberDTO, LectureDTO lectureDTO, CouponDTO couponDTO) {
        LectureDTO selectedLecture = lectureService.getLectureById(lectureDTO.getLectureCode());
        if (selectedLecture == null) throw new CommonException(StatusEnum.LECTURE_NOT_FOUND);

        MemberDTO paidStudent = memberService
                .findMemberByMemberCode(memberDTO.getMemberCode(),memberDTO.getMemberType());
        if(paidStudent == null) throw new CommonException(StatusEnum.STUDENT_NOT_FOUND);

//        private Long lectureByStudentCode;
//        private Boolean refundStatus;
//        private Lecture lecture;
//        private Member student;

        Member member = memberMapper.fromMemberDTOtoMember(memberDTO);
//        Lecture getLectureCode = lectureMapper.toEntity(lectureDTO, );
        LectureByStudentDTO lectureByStudentDTO = new LectureByStudentDTO();
        lectureByStudentDTO.setRefundStatus(false);
//        lectureByStudentDTO.setLecture(getLectureCode);

//        lectureByStudentService.registerLectureByStudent();
        //학생별강의서비스.저장메서드()
        //결제레포.세이브()
        //학생별강의동영상.저장메서드()
        //
        //리턴 결제디티오

        return null;
    }
    // 직원이 예상 매출액과 할인 매출액을 비교해서 조회

    // 직원이 기간 별 매출액을 리스트와 그래프로 조회

}
