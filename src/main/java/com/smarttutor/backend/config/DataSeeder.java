package com.smarttutor.backend.config;

import com.smarttutor.backend.model.Role;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.repository.UserRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   TeacherProfileRepository teacherProfileRepository) {
        return args -> {

            addTeacherIfNotExists(userRepository, teacherProfileRepository,
                    "Alice", "alice@gmail.com",
                    "Expert in Java and Spring Boot", "Java", 500.0);

            addTeacherIfNotExists(userRepository, teacherProfileRepository,
                    "Bob", "bob@gmail.com",
                    "Specialist in Python & Django", "Python", 300.0);

            addTeacherIfNotExists(userRepository, teacherProfileRepository,
                    "Charlie", "charlie@gmail.com",
                    "C++ and Data Structures Mentor", "C++", 200.0);

            addTeacherIfNotExists(userRepository, teacherProfileRepository,
                    "Diana", "diana@gmail.com",
                    "English teacher specialized in spoken English", "English", 180.0);

            addTeacherIfNotExists(userRepository, teacherProfileRepository,
                    "Eve", "eve@gmail.com",
                    "Computer Science PhD, 10+ years teaching experience", "Computer Science", 300.0);

            System.out.println("✅ Seeded demo teachers into DB");
        };
    }

    private void addTeacherIfNotExists(UserRepository userRepository,
                                       TeacherProfileRepository teacherProfileRepository,
                                       String name, String email,
                                       String bio, String subject, Double rate) {

        if (userRepository.existsByEmail(email)) {
            System.out.println("⚠️ Teacher with email " + email + " already exists, skipping...");
            return;
        }

        User teacher = User.builder()
                .name(name)
                .email(email)
                .password("$2a$10$KK6umvOTW0UGf.issNOXe.0Oouf1z1Rkn9tXdTTb5PchmKc1Dqwq") // "password"
                .role(Role.TEACHER)
                .build();
        userRepository.save(teacher);

        TeacherProfile profile = TeacherProfile.builder()
                .bio(bio)
                .subject(subject)
                .hourlyRate(rate)
                .user(teacher)
                .build();
        teacherProfileRepository.save(profile);

        System.out.println("✅ Inserted teacher: " + name);
    }
}
