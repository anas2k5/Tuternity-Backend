package com.smarttutor.backend.service;

import com.smarttutor.backend.dto.AvailabilityRequest;
import com.smarttutor.backend.model.Availability;
import com.smarttutor.backend.model.TeacherProfile;
import com.smarttutor.backend.repository.AvailabilityRepository;
import com.smarttutor.backend.repository.TeacherProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepo;
    private final TeacherProfileRepository teacherRepo;

    public AvailabilityService(AvailabilityRepository availabilityRepo, TeacherProfileRepository teacherRepo) {
        this.availabilityRepo = availabilityRepo;
        this.teacherRepo = teacherRepo;
    }

    public Availability addAvailability(Long teacherId, AvailabilityRequest request) {
        TeacherProfile teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Availability availability = Availability.builder()
                .teacherProfile(teacher)
                .date(request.getStartTime().toLocalDate())          // extract date
                .startTime(request.getStartTime().toLocalTime())    // extract start time
                .endTime(request.getEndTime().toLocalTime())        // extract end time
                .booked(false)
                .build();

        return availabilityRepo.save(availability);
    }

    public List<Availability> getTeacherAvailability(Long teacherId) {
        TeacherProfile teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return availabilityRepo.findByTeacherProfile(teacher);
    }
}
