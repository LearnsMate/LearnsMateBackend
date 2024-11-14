package intbyte4.learnsmate.lecture.service;

import intbyte4.learnsmate.lecture.domain.dto.LectureDTO;


import java.util.List;

public interface LectureService {
    List<LectureDTO> getAllLecture();
    LectureDTO getLectureById(Long lectureCode);
    LectureDTO getLecturesByStudentCode(Long studentCode);
    List<LectureDTO> getLecturesByTutorCode(Long tutorCode);
    LectureDTO updateLectureConfirmStatus(Long lectureCode);
}