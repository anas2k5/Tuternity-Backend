package com.smarttutor.backend.service;

import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.repository.AvailabilityRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    public Availability addAvailability(Long teacherId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        TeacherProfile teacher = teacherProfileRepository.findByUserId(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Availability availability = new Availability();
        availability.setTeacher(teacher);
        availability.setDate(date);
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        return availabilityRepository.save(availability);
    }

    public List<Availability> getAvailabilityByTeacher(Long teacherId) {
        return availabilityRepository.findByTeacher_User_Id(teacherId);
    }

    public void deleteAvailability(Long id) {
        availabilityRepository.deleteById(id);
    }

    public List<Availability> getAllAvailabilities() {
        return availabilityRepository.findAll();
    }
}
