package com.example.nagoyameshi.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.event.SignupEventPublisher;
import com.example.nagoyameshi.form.ResetForm;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.service.PasswordResetService;
import com.example.nagoyameshi.service.UserService;
import com.example.nagoyameshi.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
	private final UserService userService; 
	private final SignupEventPublisher signupEventPublisher;
	private final VerificationTokenService verificationTokenService;
	private final PasswordResetService passwordResetService; 
    
	public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, VerificationTokenService verificationTokenService, PasswordResetService passwordResetService) {       
        this.userService = userService;  
        this.signupEventPublisher = signupEventPublisher;
        this.verificationTokenService = verificationTokenService;
        this.passwordResetService = passwordResetService;
    }
    
	@GetMapping("/login")
    public String login() {        
        return "auth/login";
    }
	
	@GetMapping("/signup")
    public String signup(Model model) {        
        model.addAttribute("signupForm", new SignupForm());
        return "auth/signup";
    } 
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute @Validated SignupForm signupForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {      
        // メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (userService.isEmailRegistered(signupForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);                       
        }    
        
        // パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
        if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }        
        
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }
        
        User createdUser = userService.create(signupForm);
        String requestUrl = new String(httpServletRequest.getRequestURL());
        signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
        redirectAttributes.addFlashAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");

        return "redirect:/";
    }
	
		
	@GetMapping("/signup/verify")
    public String verify(@RequestParam(name = "token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        
        if (verificationToken != null) {
            User user = verificationToken.getUser();  
            userService.enableUser(user);
            String successMessage = "会員登録が完了しました。";
            model.addAttribute("successMessage", successMessage);            
        } else {
            String errorMessage = "トークンが無効です。";
            model.addAttribute("errorMessage", errorMessage);
        }
        
        return "auth/verify";         
    } 
	
    @GetMapping("/reset")
    public String reset(Model model) {
        model.addAttribute("resetForm", new ResetForm());
        return "auth/reset"; 
    }
    
    @PostMapping("/reset")
    public String reset(HttpServletRequest request, @RequestParam("email") String userEmail, RedirectAttributes redirectAttributes) {
        User user = userService.findUserByEmail(userEmail);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが見つかりません。");
            return "redirect:/auth/reset";
        }

        String token = UUID.randomUUID().toString();
        passwordResetService.createPasswordResetToken(user, token);

        String resetUrl = request.getRequestURL().toString().replace(request.getServletPath(), "") + "/reset/verify?token=" + token;
        passwordResetService.sendPasswordResetEmail(userEmail, resetUrl);

        redirectAttributes.addFlashAttribute("successMessage", "パスワードリセットリンクが送信されました。");
        return "redirect:/auth/reset";
    }
    
    @GetMapping("/reset/verify")
    public String resetVerify(@RequestParam(name = "token") String token, Model model) {
        try {
            passwordResetService.validatePasswordResetToken(token);
            model.addAttribute("token", token);
            return "auth/reset-password";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/verify";
        }
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        PasswordResetToken passwordResetToken = passwordResetService.getPasswordResetToken(token);
        
        if (passwordResetToken != null && !passwordResetToken.isExpired()) {
            User user = passwordResetToken.getUser();
            userService.changeUserPassword(user, password);
            redirectAttributes.addFlashAttribute("successMessage", "パスワードが正常にリセットされました。");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "トークンが無効です。");
            return "redirect:/reset";
        }
    }

}
