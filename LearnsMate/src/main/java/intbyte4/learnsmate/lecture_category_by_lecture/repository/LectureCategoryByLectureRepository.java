package intbyte4.learnsmate.lecture_category_by_lecture.repository;

import intbyte4.learnsmate.lecture_category_by_lecture.domain.entity.LectureCategoryByLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureCategoryByLectureRepository extends JpaRepository<LectureCategoryByLecture, Long> {
    @Query("SELECT lc.lectureCategoryName " +
            "FROM lectureCategoryByLecture lcl " +
            "JOIN lcl.lectureCategory lc " +
            "WHERE lcl.lecture.lectureCode = :lectureCode")
    List<String> findCategoryNamesByLectureCode(@Param("lectureCode") Long lectureCode);
}
