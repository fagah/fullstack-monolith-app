package mobidoc.ci.service;

import mobidoc.ci.dto.DoctorDTO;
import mobidoc.ci.dto.ScheduleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DoctorService {
    DoctorDTO createDoctor(DoctorDTO doctorDTO);
    DoctorDTO updateDoctor(UUID id, DoctorDTO doctorDTO);
    DoctorDTO getDoctorById(UUID id);
    DoctorDTO getDoctorByUserId(UUID userId);
    Page<DoctorDTO> getAllDoctors(Pageable pageable);
    Page<DoctorDTO> getDoctorsBySpeciality(String speciality, Pageable pageable);
    void deleteDoctor(UUID id);
    
    // Schedule management
    List<ScheduleDTO> getDoctorSchedule(UUID doctorId);
    ScheduleDTO addSchedule(UUID doctorId, ScheduleDTO scheduleDTO);
    void removeSchedule(UUID doctorId, UUID scheduleId);
    
    // Availability check
    boolean isDoctorAvailable(UUID doctorId, LocalDate date);
    List<String> getAvailableTimeSlots(UUID doctorId, LocalDate date);
    
    // Statistics
    long getCompletedAppointmentsCount(UUID doctorId);
    double getAverageRating(UUID doctorId);
    
    // Validation
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByEmail(String email);
}