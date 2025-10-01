package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.TeacherProfileRequest;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

    private final TeacherProfileRepository teacherRepository;

    public TeacherService(TeacherProfileRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    // ✅ Create or Update Teacher Profile
    public TeacherProfile createOrUpdateProfile(Long userId, TeacherProfileRequest request) {
        TeacherProfile profile = teacherRepository.findById(userId).orElse(new TeacherProfile());
        profile.setId(userId);
        profile.setBio(request.getBio());
        profile.setSubject(request.getSubject());       // ✅ fixed
        profile.setHourlyRate(request.getHourlyRate()); // ✅ fixed
        return teacherRepository.save(profile);
    }

    // ✅ Get single teacher by ID
    public TeacherProfile getProfile(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));
    }

    // ✅ Get all teachers
    public List<TeacherProfile> getAllTeachers() {
        return teacherRepository.findAll();
    }
}
