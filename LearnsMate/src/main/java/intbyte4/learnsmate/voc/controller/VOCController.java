package intbyte4.learnsmate.voc.controller;

import intbyte4.learnsmate.common.exception.CommonException;
import intbyte4.learnsmate.member.domain.dto.MemberDTO;
import intbyte4.learnsmate.voc.domain.dto.VOCDTO;
import intbyte4.learnsmate.voc.domain.vo.response.ResponseFindVOCVO;
import intbyte4.learnsmate.voc.mapper.VOCMapper;
import intbyte4.learnsmate.voc.service.VOCService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController("vocController")
@RequestMapping("voc")
@Slf4j
@RequiredArgsConstructor
public class VOCController {

    private final VOCService vocService;
    private final VOCMapper vocMapper;

    @Operation(summary = "직원 - VOC 전체 조회")
    @GetMapping("/list")
    public ResponseEntity<List<ResponseFindVOCVO>> listVOC() {
        List<VOCDTO> vocDTOList = vocService.findAllByVOC();
        List<ResponseFindVOCVO> response = new ArrayList<>();
        for (VOCDTO vocDTO : vocDTOList) {
            ResponseFindVOCVO vocVO = vocMapper.fromDTOToResponseVO(vocDTO);
            response.add(vocVO);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "직원 - VOC 단 건 조회")
    @GetMapping("/{vocCode}")
    public ResponseEntity<?> getVOC(@PathVariable("vocCode") Long vocCode) {
        log.info("조회 요청된 VOC 코드 : {}", vocCode);
        try {
            VOCDTO vocdto = vocService.findByVOCCode(vocCode);
            ResponseFindVOCVO response = vocMapper.fromDTOToResponseVO(vocdto);
            log.info("캠페인 템플릿 조회 성공: {}", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (CommonException e) {
            log.error("캠페인 템플릿 조회 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예상치 못한 오류가 발생했습니다");
        }
    }

    @Operation(summary = "직원 - 특정 유저 미답변 VOC 리스트 조회")
    @GetMapping("/{memberCode}/unanswered")
    public ResponseEntity<List<ResponseFindVOCVO>> listUnansweredVOC(@PathVariable("memberCode") Long memberCode) {
        log.info("특정 유저 미답변 VOC 조회 요청: userCode = {}", memberCode);
        List<VOCDTO> vocDTOList = vocService.findUnansweredVOCByMember(memberCode);
        List<ResponseFindVOCVO> response = vocDTOList.stream()
                .map(vocMapper::fromDTOToResponseVO)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "직원 - 특정 유저 답변 VOC 리스트 조회")
    @GetMapping("/{memberCode}/answered")
    public ResponseEntity<List<ResponseFindVOCVO>> listAnsweredVOC(@PathVariable("memberCode") Long memberCode) {
        log.info("특정 유저 답변 VOC 조회 요청: userCode = {}", memberCode);
        List<VOCDTO> vocDTOList = vocService.findAnsweredVOCByMember(memberCode);
        List<ResponseFindVOCVO> response = vocDTOList.stream()
                .map(vocMapper::fromDTOToResponseVO)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "VOC 필터링")
    @PostMapping("/filter")
    public ResponseEntity<List<VOCDTO>> filterVOC(@RequestBody VOCDTO vocDTO, MemberDTO memberDTO) {
        log.info("VOC 필터링 요청 수신");
        try {
            List<VOCDTO> filteredVOCList = vocService.filterVOC(vocDTO, memberDTO);

            log.info("VOC 필터링 성공, 필터링된 데이터 수: {}", filteredVOCList.size());
            return ResponseEntity.ok(filteredVOCList);
        } catch (CommonException e) {
            log.error("VOC 필터링 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
