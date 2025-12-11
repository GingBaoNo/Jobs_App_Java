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
@RequestMapping("/admin")
public class AdminChatController {

    @Autowired
    private ChatService chatService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/chat")
    public String adminChatPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return "redirect:/login";
        }

        // Kiểm tra vai trò của người dùng có phải là Admin không
        if (!"ADMIN".equals(currentUser.get().getRole().getTenVaiTro())) {
            return "redirect:/unauthorized";
        }

        model.addAttribute("currentUser", currentUser.get());
        model.addAttribute("chatUsers", chatService.getChatUsersForAdmin(currentUser.get()));
        
        return "admin/chat";
    }

    @GetMapping("/chat/{userId}")
    public String adminChatWithUser(@PathVariable Integer userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> currentUser = userService.getUserByTaiKhoan(username);
        if (!currentUser.isPresent()) {
            return "redirect:/login";
        }

        // Kiểm tra vai trò của người dùng có phải là Admin không
        if (!"ADMIN".equals(currentUser.get().getRole().getTenVaiTro())) {
            return "redirect:/unauthorized";
        }

        User otherUser = userService.findById(userId);
        if (otherUser == null) {
            return "redirect:/admin/chat";
        }

        model.addAttribute("currentUser", currentUser.get());
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("chatUsers", chatService.getChatUsersForAdmin(currentUser.get()));
        
        return "admin/chat_detail";
    }
}