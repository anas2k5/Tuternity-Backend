package com.smarttutor.backend.service;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.AvailabilityRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import com.smarttutor.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepo;
    private final TeacherProfileRepository teacherRepo;
    private final UserRepository userRepo;

    // ✅ Add slot for teacher (authenticated)
    public Availability addAvailability(String email, Availability req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        TeacherProfile teacher = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for user: " + email));

        Availability slot = Availability.builder()
                .teacher(teacher)
                .date(req.getDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .booked(false)
                .build();

        return availabilityRepo.save(slot);
    }

    // ✅ Get all slots of current teacher
    public List<Availability> getAvailabilitiesByTeacherEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        TeacherProfile teacher = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));
        return availabilityRepo.findByTeacherAndBookedFalse(teacher);
    }

    // ✅ Public: Get by teacher ID (used by students)
    public List<Availability> getAvailabilitiesByTeacherId(Long teacherId) {
        TeacherProfile teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return availabilityRepo.findByTeacherAndBookedFalse(teacher);
    }

    // ✅ Delete slot
    public void deleteAvailability(String email, Long id) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        TeacherProfile teacher = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));
        Availability slot = availabilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!slot.getTeacher().equals(teacher)) {
            throw new RuntimeException("Unauthorized to delete this slot");
        }

        availabilityRepo.delete(slot);
    }
}
