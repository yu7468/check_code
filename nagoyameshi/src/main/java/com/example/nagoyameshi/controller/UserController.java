package com.example.nagoyameshi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UpgradeForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/general/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final StripeService stripeService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository, UserService userService, StripeService stripeService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.stripeService = stripeService;
    }
    
    private void logoutUser(HttpServletRequest request) {
        try {
            // 清除安全上下文
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext != null) {
                SecurityContextHolder.clearContext();
            }

            // 销毁会话
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        model.addAttribute("user", user);
        return "general/user/index";
    }

    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getFurigana(), user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail());
        model.addAttribute("userEditForm", userEditForm);
        return "general/user/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        if (bindingResult.hasErrors()) {
            return "general/user/edit";
        }

        userService.update(userEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        return "redirect:/general/user";
    }

    @GetMapping("/upgrade")
    public String upgrade(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userDetailsImpl.getUser();
        UpgradeForm upgradeForm = new UpgradeForm(user.getId(), "ROLE_VIP", 500L);
        model.addAttribute("user", user);
        model.addAttribute("upgradeForm", upgradeForm);
        logger.info("User with ID {} is accessing the VIP upgrade page.", user.getId());
        return "general/user/upgrade";
    }

    @GetMapping("/checkout")
    public String checkout(@ModelAttribute("sessionId") String sessionId, Model model) {
        model.addAttribute("sessionId", sessionId);
        return "general/user/checkout";
    }

    @GetMapping("/upgrade/success")
    public String upgradeSuccess(@RequestParam String session_id,
                                 @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {
        boolean paymentSuccess = stripeService.checkPaymentCompletion(session_id);

        if (paymentSuccess) {
            User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
            Role vipRole = new Role(); 
            vipRole.setId(2); 
            user.setRole(vipRole);
            userRepository.save(user);
           
            // 强制退出登录
            logoutUser(request);
            redirectAttributes.addFlashAttribute("successMessage", "VIP会員へのアップグレードが完了しました。再ログインしてください。");
            
            // 重定向到登录页面
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "支払いの確認に失敗しました。");
            return "redirect:/general/user";
        }
    }

    @GetMapping("/upgrade/cancel")
    public String upgradeCancel(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "支払いがキャンセルされました。");
        return "redirect:/general/user";
    }

    @PostMapping("/upgrade")
    public String vip(@ModelAttribute @Validated UpgradeForm upgradeForm,
                      BindingResult bindingResult,
                      @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                      HttpServletRequest request,
                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/general/user/upgrade";
        }

        try {
            User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
            String sessionId = stripeService.createStripeSession(user.getEmail(), upgradeForm, request);

            if (sessionId == null || sessionId.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "支払いセッションの作成に失敗しました。");
                return "redirect:/general/user";
            }

            redirectAttributes.addFlashAttribute("sessionId", sessionId);
            return "redirect:/general/user/checkout"; // 重定向到新的页面
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "エラーが発生しました。");
            return "redirect:/general/user";
        }
    }
    
    @GetMapping("/cancel-vip")
    public String cancelVIP(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
                            RedirectAttributes redirectAttributes, 
                            HttpServletRequest request) {
        try {
            User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
            
            // 获取 GENERAL 角色
            Role generalRole = new Role();
            generalRole.setId(1); // GENERAL 角色的 ID 是 1
            
            // 更新用户角色
            user.setRole(generalRole);
            userRepository.save(user); // 保存更新后的用户信息
            
            // 强制退出登录
            logoutUser(request);
            redirectAttributes.addFlashAttribute("successMessage", "VIP会員の退订が完了しました。再ログインしてください。");
            
            // 重定向到登录页面
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "VIP会員の退订に失敗しました。");
            return "redirect:/general/user";
        }
    }
    
    
    
}