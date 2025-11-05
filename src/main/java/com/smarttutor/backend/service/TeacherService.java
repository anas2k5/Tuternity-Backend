package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.TeacherProfileRequest;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import com.smarttutor.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherProfileRepository teacherRepo;
    private final UserRepository userRepo;

    // ✅ Fetch all teachers
    public List<TeacherProfile> getAllTeachers() {
        return teacherRepo.findAll();
    }

    // ✅ Fetch teacher profile by ID
    public TeacherProfile getProfile(Long id) {
        return teacherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + id));
    }

    // ✅ Fetch teacher profile by user email
    public TeacherProfile getProfileByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        return teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for: " + email));
    }

    // ✅ Update teacher profile safely (supports partial updates)
    public TeacherProfile updateProfile(String email, TeacherProfileRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        TeacherProfile profile = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        // Update username if changed
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
            userRepo.save(user);
        }

        // Update teacher-specific fields if provided
        if (request.getSubject() != null) profile.setSubject(request.getSubject());
        if (request.getSkills() != null) profile.setSkills(request.getSkills());
        if (request.getExperienceYears() != null) profile.setExperienceYears(request.getExperienceYears());
        if (request.getHourlyRate() != null) profile.setHourlyRate(request.getHourlyRate());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getAvailable() != null) profile.setAvailable(request.getAvailable());

        return teacherRepo.save(profile);
    }
}
