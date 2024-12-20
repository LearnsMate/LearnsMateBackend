package intbyte4.learnsmate.voc.service;

import intbyte4.learnsmate.member.domain.dto.MemberDTO;
import intbyte4.learnsmate.voc.domain.dto.VOCCategoryCountDTO;
import intbyte4.learnsmate.voc.domain.dto.VOCClientDTO;
import intbyte4.learnsmate.voc.domain.dto.VOCDTO;
import intbyte4.learnsmate.voc.domain.dto.VOCFilterRequestDTO;
import intbyte4.learnsmate.voc.domain.vo.response.ResponseFindClientVOCVO;
import intbyte4.learnsmate.voc_category.domain.dto.VOCCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface VOCService {
    VOCDTO findByVOCCode(String vocDTO);

    void updateVOCAnswerStatus(String vocCode, boolean vocAnswerStatus);

    List<VOCDTO> findUnansweredVOCByMember(Long memberCode);

    List<VOCDTO> findAnsweredVOCByMember(Long memberCode);

    List<VOCDTO> findUnansweredVOC();

    Map<Integer, Long> countVOCByCategory(LocalDateTime startDate, LocalDateTime endDate);

    Page<VOCDTO> findAllByVOCWithPaging(Pageable of);

    // 필터링x 정렬o
    Page<VOCDTO> findAllByVOCWithPagingWithSort(Pageable pageable);

    Page<VOCDTO> filterVOCWithPaging(VOCFilterRequestDTO dto, Pageable pageable);

    // 필터링o 정렬o
    Page<VOCDTO> filterVOCWithPagingWithSort(VOCFilterRequestDTO dto, Pageable pageable);

    List<VOCCategoryCountDTO> getCategoryCounts();

    List<VOCCategoryCountDTO> getFilteredCategoryCounts(LocalDateTime startDate, LocalDateTime endDate);

    List<VOCDTO> findAllByFilter(VOCFilterRequestDTO dto);

    List<VOCDTO> findAllVOCs();

    VOCDTO saveVOC(VOCDTO dto, MemberDTO memberDTO, VOCCategoryDTO vocCategoryDto);

    List<VOCClientDTO> findAllClientVOC(Long memberCode);

    void updateVocSatisfaction(String vocCode, Long satisfaction);
}
