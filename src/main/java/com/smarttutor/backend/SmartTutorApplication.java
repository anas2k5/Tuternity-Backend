package com.smarttutor.backend;

import com.smarttutor.backend.model.Role;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmartTutorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTutorApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@demo.com").isEmpty()) {
                User user = User.builder()
                        .name("Admin User")
                        .email("admin@demo.com")
                        .password("password123")
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(user);
                System.out.println("✅ Sample user inserted!");
            } else {
                System.out.println("⚡ User already exists, skipping insert.");
            }
        };
    }
}
