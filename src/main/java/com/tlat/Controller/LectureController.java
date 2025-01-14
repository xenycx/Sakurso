package com.tlat.Controller;

import com.tlat.Dto.LectureDto;
import com.tlat.service.LectureService;
import com.tlat.service.RoomService;
import com.tlat.service.UserService;
import com.tlat.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lectures")
public class LectureController {

    private final LectureService lectureService;
    private final RoomService roomService;
    private final UserService userService;

    @Autowired
    public LectureController(LectureService lectureService, 
                            RoomService roomService,
                            UserService userService) {
        this.lectureService = lectureService;
        this.roomService = roomService;
        this.userService = userService;
    }

    @GetMapping
public String listLectures(Model model, Principal principal) {
    // Get logged in user
    User user = userService.findUserByEmail(principal.getName());
    String fullName = user.getName();
    
    List<LectureDto> lectures = new ArrayList<>(); // Initialize empty list
    
    try {
        // If user has ADMIN role, show all lectures
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            lectures = lectureService.findAllLectures();
        } else {
            // For regular users, show only their lectures
            lectures = lectureService.findLecturesByLecturer(fullName);
        }
    } catch (Exception e) {
        // Log the error
        e.printStackTrace();
    }
    
    model.addAttribute("lectures", lectures != null ? lectures : new ArrayList<>());
    return "lecture/list";
}

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("lecture", new LectureDto());
        model.addAttribute("rooms", roomService.findAllRooms());
        model.addAttribute("users", userService.findAllUsers());
        return "lecture/add";
    }
    @PostMapping("/add")
    public String addLecture(@jakarta.validation.Valid @ModelAttribute LectureDto lecture, 
                           org.springframework.validation.BindingResult result) {
        if (result.hasErrors()) {
            return "lecture/add";
        }
        lectureService.saveLecture(lecture);
        return "redirect:/lectures";
    }

    @PostMapping("/import")
    public String importCsv(@RequestParam("file") MultipartFile file) {
        lectureService.importLecturesFromCsv(file);
        return "redirect:/lectures";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        LectureDto lecture = lectureService.findLectureById(id);
        model.addAttribute("lecture", lecture);
        model.addAttribute("rooms", roomService.findAllRooms());
        model.addAttribute("users", userService.findAllUsers());
        return "lecture/edit";
    }

    @PostMapping("/edit/{id}")
    public String editLecture(@jakarta.validation.Valid @ModelAttribute LectureDto lecture, 
                            org.springframework.validation.BindingResult result,
                            @PathVariable Long id) {
        if (result.hasErrors()) {
            return "lecture/edit";
        }
        lectureService.editLecture(lecture, id);
        return "redirect:/lectures";
    }

    @GetMapping("/delete/{id}")
    public String deleteLecture(@PathVariable Long id) {
        lectureService.deleteLectureById(id);
        return "redirect:/lectures";
    }

    @PostMapping("/start/{id}")
    public String startLecture(@PathVariable Long id, 
                             HttpServletRequest request, 
                             RedirectAttributes redirectAttributes,
                             @RequestHeader(value = "Referer", required = false) String referer) {
        try {
            lectureService.startLecture(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Lecture started successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error starting lecture: " + e.getMessage());
        }
        return referer != null ? "redirect:" + referer : "redirect:/lectures";
    }

    @PostMapping("/stop/{id}")
    public String stopLecture(@PathVariable Long id, 
                            HttpServletRequest request, 
                            RedirectAttributes redirectAttributes,
                            @RequestHeader(value = "Referer", required = false) String referer) {
        try {
            lectureService.stopLecture(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Lecture stopped successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error stopping lecture: " + e.getMessage());
        }
        return referer != null ? "redirect:" + referer : "redirect:/lectures";
    }
}