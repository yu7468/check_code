package com.example.nagoyameshi.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.PasswordResetTokenRepository;
import com.example.nagoyameshi.service.EmailService;
import com.example.nagoyameshi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PasswordResetController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;  

    @GetMapping("/auth/reset")
    public String showResetPasswordForm() {
        // 返回重置密码的表单页面
        return "passwordResetForm"; // 确保页面名称正确
    }

    @PostMapping("/auth/reset")
    public String resetPassword(HttpServletRequest request, @RequestParam("email") String userEmail) {
        User user = userService.findUserByEmail(userEmail);
        if (user == null) {
            return "redirect:/login?error=User not found!";
        }

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        String resetUrl = "http://localhost:8080/changePassword?token=" + token;
        emailService.sendSimpleMessage(userEmail, "Password Reset", "Reset your password using this link: " + resetUrl);

        return "redirect:/login?resetPassword";
    }
    

    

    @GetMapping("/auth/changePassword") // 改为处理GET请求
    public String showChangePasswordPage(@RequestParam("token") String token, Model model) {
        PasswordResetToken passToken = userService.getPasswordResetToken(token);
        if (passToken == null || passToken.isExpired()) {
            return "redirect:/login?error=invalidToken";
        }
        model.addAttribute("token", token);
        return "changePassword";
    }


    @PostMapping("/auth/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        PasswordResetToken passToken = userService.getPasswordResetToken(token);
        if (passToken == null || passToken.isExpired()) {
            return "redirect:/login?error=Invalid token!";
        }

        User user = passToken.getUser();
        userService.changeUserPassword(user, password);

        // 发送密码重置确认邮件
        emailService.sendSimpleMessage(
            user.getEmail(),
            "Password Reset Confirmation",
            "Your password has been successfully reset!"
        );

        return "redirect:/login?passwordResetSuccess";
    }
    
    private void deleteToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }
}