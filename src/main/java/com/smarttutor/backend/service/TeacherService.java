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

    // ✅ Update teacher profile
    public TeacherProfile updateProfile(String email, TeacherProfileRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        TeacherProfile profile = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        profile.setSubject(request.getSubject());
        profile.setSkills(request.getSkills());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setHourlyRate(request.getHourlyRate());
        profile.setBio(request.getBio());
        profile.setCity(request.getCity());
        profile.setAvailable(request.isAvailable());

        return teacherRepo.save(profile);
    }
}
