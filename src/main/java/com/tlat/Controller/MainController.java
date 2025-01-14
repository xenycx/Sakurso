package com.tlat.Controller;

import com.tlat.Dto.LectureDto;
import com.tlat.Entity.User;
import com.tlat.service.LectureService;
import com.tlat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/main")
public class MainController {

    private final UserService userService;
    private final LectureService lectureService;

    @Autowired
    public MainController(UserService userService, LectureService lectureService) {
        this.userService = userService;
        this.lectureService = lectureService;
    }

    @GetMapping
    public String mainPage(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName());
        model.addAttribute("user", user);

        // Get today's lectures for the logged-in lecturer
        List<LectureDto> todaysLectures;
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            // Admin sees all lectures
            todaysLectures = lectureService.findLecturesByDate(LocalDate.now());
        } else {
            // Regular users see only their lectures
            todaysLectures = lectureService.findLecturesByDateAndLecturer(LocalDate.now(), user.getName());
        }
        model.addAttribute("todaysLectures", todaysLectures);
        
        return "main";
    }
}