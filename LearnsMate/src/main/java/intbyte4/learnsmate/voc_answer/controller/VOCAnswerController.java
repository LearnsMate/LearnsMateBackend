package intbyte4.learnsmate.voc_answer.controller;

import intbyte4.learnsmate.common.exception.CommonException;
import intbyte4.learnsmate.voc_answer.domain.dto.VOCAnswerDTO;
import intbyte4.learnsmate.voc_answer.domain.vo.request.RequestRegisterVOCAnswerVO;
import intbyte4.learnsmate.voc_answer.domain.vo.response.ResponseRegisterVOCAnswerVO;
import intbyte4.learnsmate.voc_answer.mapper.VOCAnswerMapper;
import intbyte4.learnsmate.voc_answer.service.VOCAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("vocController")
@RequestMapping("voc-answer")
@Slf4j
@RequiredArgsConstructor
public class VOCAnswerController {

    private final VOCAnswerService vocAnswerService;
    private final VOCAnswerMapper vocAnswerMapper;

    @Operation(summary = "직원 - VOC 답변 등록")
    @PostMapping("/register")
    public ResponseEntity<?> registerTemplate(@RequestBody final RequestRegisterVOCAnswerVO request) {
        log.info("VOC 답변 등록 요청 : {}", request);
        try {
            VOCAnswerDTO vocAnswerDTO = vocAnswerMapper.fromRegisterRequestVOToDTO(request);
            VOCAnswerDTO registerVocAnswer = vocAnswerService.registerVOCAnswer(vocAnswerDTO);
            ResponseRegisterVOCAnswerVO response = vocAnswerMapper.fromDtoToRegisterResponseVO(registerVocAnswer);

            log.info("VOC 답변 등록 성공: {}", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (CommonException e) {
            log.error("VOC 답변 등록 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예상치 못한 오류가 발생했습니다");
        }
    }
}