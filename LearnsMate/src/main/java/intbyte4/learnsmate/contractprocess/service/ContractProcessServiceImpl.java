package intbyte4.learnsmate.contractprocess.service;

import intbyte4.learnsmate.admin.domain.dto.AdminDTO;
import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.admin.mapper.AdminMapper;
import intbyte4.learnsmate.admin.service.AdminService;
import intbyte4.learnsmate.common.exception.StatusEnum;
import intbyte4.learnsmate.contractprocess.domain.dto.ContractProcessDTO;
import intbyte4.learnsmate.contractprocess.domain.entity.ContractProcess;
import intbyte4.learnsmate.contractprocess.mapper.ContractProcessMapper;
import intbyte4.learnsmate.contractprocess.repository.ContractProcessRepository;
import intbyte4.learnsmate.common.exception.CommonException;
import intbyte4.learnsmate.lecture.domain.dto.LectureDTO;
import intbyte4.learnsmate.lecture.domain.entity.Lecture;
import intbyte4.learnsmate.lecture.mapper.LectureMapper;
import intbyte4.learnsmate.lecture.service.LectureService;
import intbyte4.learnsmate.lecture_category.domain.dto.LectureCategoryDTO;
import intbyte4.learnsmate.lecture_category.domain.entity.LectureCategory;
import intbyte4.learnsmate.lecture_category.mapper.LectureCategoryMapper;
import intbyte4.learnsmate.lecture_category.service.LectureCategoryService;
import intbyte4.learnsmate.member.domain.MemberType;
import intbyte4.learnsmate.member.domain.dto.MemberDTO;
import intbyte4.learnsmate.member.domain.entity.Member;
import intbyte4.learnsmate.member.mapper.MemberMapper;
import intbyte4.learnsmate.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ContractProcessServiceImpl implements ContractProcessService {
    private final ContractProcessRepository contractProcessRepository;
    private final ContractProcessMapper contractProcessMapper;
    private final LectureCategoryService lectureCategoryService;
    private final LectureCategoryMapper lectureCategoryMapper;
    private final AdminService adminService;
    private final AdminMapper adminMapper;
    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final LectureService lectureService;
    private final LectureMapper lectureMapper;


    // 단건 조회
    @Override
    public ContractProcessDTO getContractProcess(Long contractProcessCode) {
        ContractProcess contractProcess = contractProcessRepository.findById(contractProcessCode)
                .orElseThrow(() -> new CommonException(StatusEnum.CONTRACT_PROCESS_NOT_FOUND));
        return contractProcessMapper.toDTO(contractProcess);
    }


    // 강의별 승인과정 절차 조회
    @Override
    public ContractProcessDTO getApprovalProcessByLectureCode(Long lectureCode) {
        LectureDTO lecturedto = lectureService.getLectureById(lectureCode);

        MemberDTO tutorDTO = memberService.findMemberByMemberCode(lecturedto.getLectureCode(), MemberType.TUTOR);
        Member tutor = memberMapper.fromMemberDTOtoMember(tutorDTO);

        LectureCategoryDTO lectureCategoryDTO = lectureCategoryService.findByLectureCategoryCode(lecturedto.getLectureCategoryCode());
        LectureCategory lectureCategory = lectureCategoryMapper.toEntity(lectureCategoryDTO);

        Lecture lecture = lectureMapper.toEntity(lecturedto, tutor, lectureCategory);

        ContractProcess contractProcess = contractProcessRepository.findByLecture(lecture);

        if (contractProcess == null) {
            throw new CommonException(StatusEnum.CONTRACT_PROCESS_NOT_FOUND);
        }

        return contractProcessMapper.toDTO(contractProcess);
    }



    // 계약과정 등록
    @Transactional
    @Override
    public ContractProcessDTO createContractProcess(Long lectureCode, ContractProcessDTO contractProcessDTO) {
        LectureDTO lectureDTO = lectureService.getLectureById(lectureCode);
        MemberDTO tutorDTO = memberService.findMemberByMemberCode(lectureDTO.getTutorCode(), MemberType.TUTOR);
        Member tutor = memberMapper.fromMemberDTOtoMember(tutorDTO);

        LectureCategoryDTO lectureCategoryDTO = lectureCategoryService.findByLectureCategoryCode(lectureDTO.getLectureCategoryCode());
        LectureCategory category = lectureCategoryMapper.toEntity(lectureCategoryDTO);

        Lecture lecture = lectureMapper.toEntity(lectureDTO, tutor, category);

        AdminDTO adminDTO = adminService.findByAdminCode(contractProcessDTO.getAdminCode());
        Admin admin = adminMapper.toEntity(adminDTO);


        ContractProcess contractProcess = ContractProcess.builder()
                .lecture(lecture)
                .admin(admin)
                .approvalProcess(contractProcessDTO.getApprovalProcess())
                .createdAt(LocalDateTime.now())
                .note("신규 강의 계약")
                .build();


        contractProcessRepository.save(contractProcess);

        return contractProcessMapper.toDTO(contractProcess);
    }

}
