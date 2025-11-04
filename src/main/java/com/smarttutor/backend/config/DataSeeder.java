package com.smarttutor.backend.config;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.repository.AvailabilityRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TeacherProfileRepository teacherRepo;
    private final AvailabilityRepository availabilityRepo;

    @Override
    @Transactional  // ✅ Keeps session open during seeding
    public void run(String... args) {
        System.out.println("🔹 Running DataSeeder...");
        seedAvailabilityForTeachers();
    }

    private void seedAvailabilityForTeachers() {
        List<TeacherProfile> teachers = teacherRepo.findAll();

        if (teachers.isEmpty()) {
            System.out.println("⚠️ No teachers found in database. Please seed teachers first.");
            return;
        }

        for (TeacherProfile teacher : teachers) {
            addSlotsIfEmpty(teacher);
        }
    }

    private void addSlotsIfEmpty(TeacherProfile teacher) {
        List<Availability> existing = availabilityRepo.findByTeacherAndBookedFalse(teacher);

        if (!existing.isEmpty()) {
            System.out.println("✅ Teacher ID " + teacher.getId() + " already has slots. Skipping...");
            return;
        }

        System.out.println("🕒 Adding slots for teacher ID " + teacher.getId());

        LocalDate today = LocalDate.now();

        for (int dayOffset = 0; dayOffset < 3; dayOffset++) {
            LocalDate date = today.plusDays(dayOffset);

            for (int hour = 10; hour <= 14; hour++) {
                Availability slot = Availability.builder()
                        .teacher(teacher)
                        .date(date)
                        .startTime(LocalTime.of(hour, 0))
                        .endTime(LocalTime.of(hour + 1, 0))
                        .booked(false)
                        .build();

                availabilityRepo.save(slot);
            }
        }

        System.out.println("✅ Slots added successfully for teacher ID " + teacher.getId());
    }
}
