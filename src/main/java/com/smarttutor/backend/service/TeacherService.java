package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.TeacherProfileRequest;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import com.smarttutor.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {
    private final TeacherProfileRepository teacherRepo;
    private final UserRepository userRepo;

    public TeacherService(TeacherProfileRepository teacherRepo, UserRepository userRepo) {
        this.teacherRepo = teacherRepo;
        this.userRepo = userRepo;
    }

    public TeacherProfile createOrUpdateProfile(Long userId, TeacherProfileRequest request) {
        User user = userRepo.findById(userId).orElseThrow();
        TeacherProfile profile = teacherRepo.findByUser(user).orElse(new TeacherProfile());
        profile.setBio(request.getBio());
        profile.setSubject(request.getSubject());
        profile.setHourlyRate(request.getHourlyRate());
        profile.setUser(user);
        return teacherRepo.save(profile);
    }

    public TeacherProfile getProfile(Long teacherId) {
        return teacherRepo.findById(teacherId).orElseThrow();
    }
}
