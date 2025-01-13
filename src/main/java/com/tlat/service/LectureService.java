package com.tlat.service;

import com.tlat.Dto.LectureDto;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface LectureService {
    void saveLecture(LectureDto lectureDto);
    List<LectureDto> findAllLectures();
    List<LectureDto> findLecturesByLecturer(String lecturer);
    LectureDto findLectureById(Long id);
    void editLecture(LectureDto lectureDto, Long id);
    void deleteLectureById(Long id);
    void importLecturesFromCsv(MultipartFile file);
    void startLecture(Long id, HttpServletRequest request);
    void stopLecture(Long id, HttpServletRequest request);
}