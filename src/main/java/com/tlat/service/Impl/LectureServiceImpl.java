package com.tlat.service.Impl;

import com.tlat.Dto.LectureDto;
import com.tlat.Entity.Lecture;
import com.tlat.Entity.LectureStatus;
import com.tlat.Entity.Room;
import com.tlat.Repository.LectureRepository;
import com.tlat.Repository.RoomRepository;
import com.tlat.service.LectureService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureServiceImpl implements LectureService {
    
    private final LectureRepository lectureRepository;
    private final RoomRepository roomRepository;

    public LectureServiceImpl(LectureRepository lectureRepository, RoomRepository roomRepository) {
        this.lectureRepository = lectureRepository;
        this.roomRepository = roomRepository;
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void updateLectureStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Lecture> lectures = lectureRepository.findAll();
        
        for (Lecture lecture : lectures) {
            LocalDateTime lectureStart = LocalDateTime.of(lecture.getDate(), lecture.getStartTime());
            LocalDateTime lectureEnd = LocalDateTime.of(lecture.getDate(), lecture.getEndTime());
            
            if (lecture.getStatus() == LectureStatus.SCHEDULED) {
                if (now.isAfter(lectureEnd)) {
                    lecture.setStatus(LectureStatus.MISSED);
                } else if (now.isAfter(lectureStart) && now.isBefore(lectureEnd)) {
                    lecture.setStatus(LectureStatus.IN_PROGRESS);
                }
            } else if (lecture.getStatus() == LectureStatus.IN_PROGRESS && now.isAfter(lectureEnd)) {
                lecture.setStatus(LectureStatus.COMPLETED);
            }
        }
        
        lectureRepository.saveAll(lectures);
    }

    @Override
    public void editLecture(LectureDto lectureDto, Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lecture not found: " + id));
        lecture.setDate(lectureDto.getDate());
        lecture.setStartTime(lectureDto.getStartTime());
        lecture.setEndTime(lectureDto.getEndTime());
        lecture.setLecturer(lectureDto.getLecturer());
        lecture.setSubject(lectureDto.getSubject());
        Room room = roomRepository.findByRoomNumber(lectureDto.getRoomNumber());
        if (room == null) {
            throw new RuntimeException("Room not found: " + lectureDto.getRoomNumber());
        }
        lecture.setRoom(room);
        lectureRepository.save(lecture);
    }

    @Override
    public LectureDto findLectureById(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lecture not found: " + id));
        return mapToDto(lecture);
    }

    @Override
    public void deleteLectureById(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lecture not found: " + id));
        lectureRepository.delete(lecture);
    }

    @Override
    public void saveLecture(LectureDto lectureDto) {
        Lecture lecture = mapToEntity(lectureDto);
        lectureRepository.save(lecture);
    }

    @Override
    public List<LectureDto> findAllLectures() {
        List<Lecture> lectures = lectureRepository.findAll();
        return lectures.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void importLecturesFromCsv(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int lineNumber = 1; // Skip header

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Skip header
            reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] values = line.split(",");
                    String roomNumber = values[0].trim();
                    
                    // Verify room exists
                    Room room = roomRepository.findByRoomNumber(roomNumber);
                    if (room == null) {
                        errors.add("Line " + lineNumber + ": Room not found: " + roomNumber);
                        continue;
                    }

                    LectureDto lectureDto = new LectureDto();
                    lectureDto.setRoomNumber(roomNumber);
                    lectureDto.setDate(LocalDate.parse(values[1].trim()));
                    lectureDto.setStartTime(LocalTime.parse(values[2].trim()));
                    lectureDto.setEndTime(LocalTime.parse(values[3].trim()));
                    lectureDto.setLecturer(values[4].trim());
                    lectureDto.setSubject(values[5].trim());
                    
                    saveLecture(lectureDto);
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV file: " + e.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException("Import completed with errors:\n" + String.join("\n", errors));
        }
    }

    @Override
    public List<LectureDto> findLecturesByLecturer(String lecturer) {
        List<Lecture> lectures = lectureRepository.findByLecturer(lecturer);
        return lectures.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private Lecture mapToEntity(LectureDto dto) {
        Lecture lecture = new Lecture();
        Room room = roomRepository.findByRoomNumber(dto.getRoomNumber());
        if (room == null) {
            throw new RuntimeException("Room not found: " + dto.getRoomNumber());
        }
        
        lecture.setRoom(room);
        lecture.setDate(dto.getDate());
        lecture.setStartTime(dto.getStartTime());
        lecture.setEndTime(dto.getEndTime());
        lecture.setLecturer(dto.getLecturer());
        lecture.setSubject(dto.getSubject());
        lecture.setIsActive(false);
        lecture.setStatus(dto.getStatus());
        return lecture;
    }

    private LectureDto mapToDto(Lecture lecture) {
        LectureDto dto = new LectureDto();
        dto.setId(lecture.getId());
        dto.setRoomNumber(lecture.getRoom().getRoomNumber());
        dto.setDate(lecture.getDate());
        dto.setStartTime(lecture.getStartTime());
        dto.setEndTime(lecture.getEndTime());
        dto.setLecturer(lecture.getLecturer());
        dto.setSubject(lecture.getSubject());
        dto.setStatus(lecture.getStatus());
        
        return dto;
    }
}
