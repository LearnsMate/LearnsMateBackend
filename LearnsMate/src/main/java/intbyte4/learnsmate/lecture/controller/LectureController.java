package intbyte4.learnsmate.lecture.controller;

import intbyte4.learnsmate.lecture.domain.dto.MonthlyLectureCountDTO;
import intbyte4.learnsmate.lecture.service.LectureFacade;
import intbyte4.learnsmate.lecture.domain.dto.LectureDTO;
import intbyte4.learnsmate.lecture.domain.dto.LectureDetailDTO;
import intbyte4.learnsmate.lecture.domain.vo.request.RequestEditLectureInfoVO;
import intbyte4.learnsmate.lecture.domain.vo.request.RequestRegisterLectureVO;
import intbyte4.learnsmate.lecture.domain.vo.response.ResponseEditLectureInfoVO;
import intbyte4.learnsmate.lecture.domain.vo.response.ResponseFindLectureVO;
import intbyte4.learnsmate.lecture.domain.vo.response.ResponseRegisterLectureVO;
import intbyte4.learnsmate.lecture.domain.vo.response.ResponseRemoveLectureVO;
import intbyte4.learnsmate.lecture.mapper.LectureMapper;
import intbyte4.learnsmate.lecture.service.LectureService;
import intbyte4.learnsmate.video_by_lecture.domain.dto.VideoByLectureDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lecture")
@Slf4j
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;
    private final LectureMapper lectureMapper;
    private final LectureFacade lectureFacade;

    @Operation(summary = "학생이 강의를 클릭할 때 클릭 수 증가")
    @PatchMapping("/{lectureCode}/click")
    public ResponseEntity<?> incrementClickCount(@PathVariable("lectureCode") String lectureCode) {
        try {
            lectureService.incrementClickCount(lectureCode);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "강의 정보 전체 조회")
    @GetMapping
    public ResponseEntity<List<ResponseFindLectureVO>> getAllLectures() {
        List<LectureDetailDTO> lectureDTOs = lectureFacade.getAllLecture();
        List<ResponseFindLectureVO> lectureVOs = lectureDTOs.stream()
                .map(lectureMapper::fromDtoToResponseVO)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(lectureVOs);
    }

    @Operation(summary = "강의 단건 조회")
    @GetMapping("/{lectureCode}")
    public ResponseEntity<ResponseFindLectureVO> getLecture(@PathVariable("lectureCode") String lectureCode) {
        LectureDetailDTO lectureDTO = lectureFacade.getLectureById(lectureCode);
        return ResponseEntity.status(HttpStatus.OK).body(lectureMapper.fromDtoToResponseVO(lectureDTO));
    }

    @Operation(summary = "강의와 강의별 동영상 등록 요청")
    @PostMapping("/register")
    public ResponseEntity<ResponseRegisterLectureVO> registerLecture(@RequestBody RequestRegisterLectureVO registerLectureVO) {
        LectureDTO lectureDTO = lectureMapper.fromRegisterRequestVOtoDto(registerLectureVO);
        List<Integer> lectureCategoryCodeList = registerLectureVO.getLectureCategoryCodeList();
        List<VideoByLectureDTO> videoByLectureDTOList = registerLectureVO.getVideoByLectureDTOList();

        LectureDTO registeredLectureDTO = lectureFacade.registerLecture(lectureDTO, lectureCategoryCodeList, videoByLectureDTOList);

        return ResponseEntity.status(HttpStatus.CREATED).body(lectureMapper.fromDtoToRegisterResponseVO(registeredLectureDTO));
    }

    @Operation(summary = "강의 수정")
    @PatchMapping("/{lectureCode}/info")
    public ResponseEntity<ResponseEditLectureInfoVO> updateLecture(@RequestBody RequestEditLectureInfoVO requestVO, @PathVariable String lectureCode) {
        LectureDTO lectureDTO = lectureMapper.fromRequestVOtoDto(requestVO);
        lectureDTO.setLectureCode(lectureCode);

        LectureDTO updatedLecture = lectureFacade.updateLecture(lectureDTO, requestVO.getNewVideoTitle(), requestVO.getNewVideoLink(), requestVO.getLectureCategoryCodeList());

        ResponseEditLectureInfoVO responseVO = lectureMapper.fromDtoToEditResponseVO(updatedLecture);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseVO);
    }

    @Operation(summary = "강의 삭제")
    @PatchMapping("/{lectureCode}/status")
    public ResponseEntity<ResponseRemoveLectureVO> removeLecture(@PathVariable("lectureCode")  String lectureCode) {
        LectureDTO removedLecture = lectureFacade.removeLecture(lectureCode);
        return ResponseEntity.status(HttpStatus.OK).body(lectureMapper.fromDtoToRemoveResponseVO(removedLecture));
    }

    @Operation(summary = "월별/연도별 전체 강의 수 조회")
    @GetMapping("/monthly-counts")
    public ResponseEntity<List<MonthlyLectureCountDTO>> getMonthlyLectureCounts() {
        List<MonthlyLectureCountDTO> lectureCounts = lectureService.getMonthlyLectureCounts();
        return ResponseEntity.ok(lectureCounts);
    }
}
