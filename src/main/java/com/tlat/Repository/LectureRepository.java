package com.tlat.Repository;

import java.util.List;
import com.tlat.Entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
   List<Lecture> findByLecturer(String lecturer);
}