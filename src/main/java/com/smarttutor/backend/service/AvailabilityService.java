package com.smarttutor.backend.service;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.model.User;
import com.smarttutor.backend.repository.AvailabilityRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import com.smarttutor.backend.repository.UserRepository;
import com.smarttutor.backend.repository.BookingRepository;
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
    private final BookingRepository bookingRepository; // <-- injected

    /**
     * ✅ Add a new availability slot for the logged-in teacher
     */
    @Transactional
    public Availability addAvailability(String email, Availability req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        TeacherProfile teacher = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found for user: " + email));

        LocalDate date = req.getDate();
        LocalTime startTime = req.getStartTime();
        LocalTime endTime = req.getEndTime();

        if (date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Date, start time, and end time are required.");
        }

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        // Prevent duplicate slots on same day & time
        boolean exists = availabilityRepo.existsByTeacherAndDateAndStartTimeAndEndTime(
                teacher, date, startTime, endTime);
        if (exists) {
            throw new IllegalStateException("Slot already exists for this time range.");
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
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        TeacherProfile teacher = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found"));

        return availabilityRepo.findByTeacher_User_Id(teacher.getUser().getId())
                .stream()
                .filter(a -> !a.isBooked()) // keep semantics consistent
                .toList();
    }

    /**
     * ✅ Fetch all open slots of a teacher (used by students)
     */
    public List<Availability> getAvailabilitiesByTeacherId(Long teacherId) {
        TeacherProfile teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        return availabilityRepo.findByTeacherAndBookedFalse(teacher);
    }

    /**
     * ✅ Delete a specific availability slot (only if owned by teacher)
     */
    @Transactional
    public void deleteAvailability(String email, Long id) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        TeacherProfile teacher = teacherRepo.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found"));

        Availability slot = availabilityRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        if (!slot.getTeacher().getId().equals(teacher.getId())) {
            throw new SecurityException("Unauthorized to delete this slot.");
        }

        // FINAL source-of-truth: check bookings table for references
        if (bookingRepository.existsByAvailabilityId(id) || slot.isBooked()) {
            // keep a clear message so front-end can show user-friendly toast
            throw new IllegalStateException("Cannot delete a slot that has an existing booking.");
        }

        availabilityRepo.delete(slot);
    }
}
