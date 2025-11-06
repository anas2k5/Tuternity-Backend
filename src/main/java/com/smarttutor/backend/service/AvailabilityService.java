package com.smarttutor.backend.service;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.AvailabilityRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import com.smarttutor.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepo;
    private final TeacherProfileRepository teacherRepo;
    private final UserRepository userRepo;

    /**
     * ✅ Add a new availability slot for the logged-in teacher
     */
    @Transactional
    public Availability addAvailability(String email, Availability req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        TeacherProfile teacher = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for user: " + email));

        LocalDate date = req.getDate();
        LocalTime startTime = req.getStartTime();
        LocalTime endTime = req.getEndTime();

        if (date == null || startTime == null || endTime == null) {
            throw new RuntimeException("Date, start time, and end time are required.");
        }

        if (endTime.isBefore(startTime)) {
            throw new RuntimeException("End time must be after start time.");
        }

        // ✅ Prevent duplicate slots on same day & time
        boolean exists = availabilityRepo.existsByTeacherAndDateAndStartTimeAndEndTime(
                teacher, date, startTime, endTime);
        if (exists) {
            throw new RuntimeException("Slot already exists for this time range.");
        }

        Availability slot = Availability.builder()
                .teacher(teacher)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .booked(false)
                .build();

        return availabilityRepo.save(slot);
    }

    /**
     * ✅ Fetch all available (non-booked) slots for a teacher (self-view)
     */
    public List<Availability> getAvailabilitiesByTeacherEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        TeacherProfile teacher = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        return availabilityRepo.findByTeacherAndBookedFalse(teacher);
    }

    /**
     * ✅ Fetch all open slots of a teacher (used by students)
     */
    public List<Availability> getAvailabilitiesByTeacherId(Long teacherId) {
        TeacherProfile teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return availabilityRepo.findByTeacherAndBookedFalse(teacher);
    }

    /**
     * ✅ Delete a specific availability slot (only if owned by teacher)
     */
    @Transactional
    public void deleteAvailability(String email, Long id) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        TeacherProfile teacher = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        Availability slot = availabilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!slot.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Unauthorized to delete this slot.");
        }

        if (slot.isBooked()) {
            throw new RuntimeException("Cannot delete a booked slot.");
        }

        availabilityRepo.delete(slot);
    }
}
