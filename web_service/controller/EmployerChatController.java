package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.ChatService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/employer")
public class EmployerChatController {

    @Autowired
    private ChatService chatService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/chat")
    public String employerChatPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return "redirect:/login";
        }

        // Kiểm tra vai trò của người dùng có phải là NTD không
        if (!"NTD".equals(currentUser.get().getRole().getTenVaiTro())) {
            return "redirect:/unauthorized";
        }

        model.addAttribute("currentUser", currentUser.get());
        model.addAttribute("adminUsers", chatService.getAdminsForEmployer(currentUser.get()));
        model.addAttribute("applicantUsers", chatService.getApplicantsForEmployer(currentUser.get()));
        
        return "employer/chat";
    }

    @GetMapping("/chat/admin/{adminId}")
    public String employerChatWithAdmin(@PathVariable Integer adminId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return "redirect:/login";
        }

        // Kiểm tra vai trò của người dùng có phải là NTD không
        if (!"NTD".equals(currentUser.get().getRole().getTenVaiTro())) {
            return "redirect:/unauthorized";
        }

        User adminUser = userService.findById(adminId);
        if (adminUser == null || !"ADMIN".equals(adminUser.getRole().getTenVaiTro())) {
            return "redirect:/employer/chat";
        }

        model.addAttribute("currentUser", currentUser.get());
        model.addAttribute("otherUser", adminUser);
        model.addAttribute("adminUsers", chatService.getAdminsForEmployer(currentUser.get()));
        model.addAttribute("applicantUsers", chatService.getApplicantsForEmployer(currentUser.get()));
        
        return "employer/chat";
    }

    @GetMapping("/chat/applicant/{applicantId}")
    public String employerChatWithApplicant(@PathVariable Integer applicantId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return "redirect:/login";
        }

        // Kiểm tra vai trò của người dùng có phải là NTD không
        if (!"NTD".equals(currentUser.get().getRole().getTenVaiTro())) {
            return "redirect:/unauthorized";
        }

        User applicantUser = userService.findById(applicantId);
        if (applicantUser == null) {
            return "redirect:/employer/chat";
        }

        // Kiểm tra xem người này có phải là ứng viên của công ty NTD này không
        boolean isApplicant = chatService.getApplicantsForEmployer(currentUser.get())
                .stream()
                .anyMatch(u -> u.getUser().getMaNguoiDung().equals(applicantId));
        
        if (!isApplicant) {
            return "redirect:/employer/chat";
        }

        model.addAttribute("currentUser", currentUser.get());
        model.addAttribute("otherUser", applicantUser);
        model.addAttribute("adminUsers", chatService.getAdminsForEmployer(currentUser.get()));
        model.addAttribute("applicantUsers", chatService.getApplicantsForEmployer(currentUser.get()));
        
        return "employer/chat";
    }
}