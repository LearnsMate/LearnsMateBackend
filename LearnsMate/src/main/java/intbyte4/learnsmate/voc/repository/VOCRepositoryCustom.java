package intbyte4.learnsmate.voc.repository;

import intbyte4.learnsmate.voc.domain.VOC;
import intbyte4.learnsmate.voc.domain.dto.VOCFilterRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface VOCRepositoryCustom {
    Page<VOC> searchByWithPaging(VOCFilterRequestDTO dto, Pageable pageable);

    List<VOC> findAllByFilter(VOCFilterRequestDTO dto);

    Page<VOC> findAllBeforeNowWithSort(LocalDateTime now, Pageable pageable);
}
