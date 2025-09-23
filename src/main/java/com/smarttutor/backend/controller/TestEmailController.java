package com.smarttutor.backend.controller;

import com.smarttutor.backend.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    private final NotificationService notificationService;

    public TestEmailController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/test-email")
    public String sendTestEmail(@RequestParam String to) {
        notificationService.sendEmail(
                to,
                "Test Email",
                "✅ This is a test email from SmartTutor system!"
        );
        return "Email sent successfully to " + to;
    }
}
