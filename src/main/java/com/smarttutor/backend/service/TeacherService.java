package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.TeacherProfileRequest; // Ensure this DTO exists
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import com.smarttutor.backend.repository.UserRepository; // CRITICAL: Import UserRepository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherService {

    // Correct field declarations
    private final TeacherProfileRepository teacherProfileRepository;
    private final UserRepository userRepository;

    // Correct Constructor
    public TeacherService(TeacherProfileRepository teacherProfileRepository, UserRepository userRepository) {
        this.teacherProfileRepository = teacherProfileRepository;
        this.userRepository = userRepository;
    }

    // --- Profile Management for Logged-In Teacher (Secure Methods) ---

    @Transactional(readOnly = true)
    public TeacherProfile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return teacherProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for user: " + email));
    }

    @Transactional
    public TeacherProfile updateProfile(String email, TeacherProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        TeacherProfile profile = teacherProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for user: " + email));

        // 1. Update User details (if name is provided)
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
            userRepository.save(user);
        }

        // 2. Update TeacherProfile details
        // These getters MUST exist in your TeacherProfileRequest DTO!
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getSubject() != null) profile.setSubject(request.getSubject());
        if (request.getSkills() != null) profile.setSkills(request.getSkills());
        if (request.getHourlyRate() != null && request.getHourlyRate() >= 0) {
            profile.setHourlyRate(request.getHourlyRate());
        }

        return teacherProfileRepository.save(profile);
    }

    // --- Existing/Required Methods (Placeholder structure) ---

    @Transactional
    public TeacherProfile createOrUpdateProfile(Long userId, TeacherProfileRequest request) {
        // Implementation using userRepository to find user by ID
        return new TeacherProfile(); // Placeholder
    }

    @Transactional(readOnly = true)
    public TeacherProfile getProfile(Long teacherId) {
        return teacherProfileRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found with id: " + teacherId));
    }

    @Transactional(readOnly = true)
    public List<TeacherProfile> getAllTeachers() {
        return teacherProfileRepository.findAll();
    }
}