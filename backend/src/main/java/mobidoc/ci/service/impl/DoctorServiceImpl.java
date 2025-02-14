package mobidoc.ci.service.impl;

import lombok.RequiredArgsConstructor;
import mobidoc.ci.dto.DoctorDTO;
import mobidoc.ci.dto.ScheduleDTO;
import mobidoc.ci.exception.ResourceNotFoundException;
import mobidoc.ci.mapper.DoctorMapper;
import mobidoc.ci.mapper.ScheduleMapper;
import mobidoc.ci.model.Doctor;
import mobidoc.ci.model.Schedule;
import mobidoc.ci.repository.DoctorRepository;
import mobidoc.ci.repository.ScheduleRepository;
import mobidoc.ci.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorMapper doctorMapper;
    private final ScheduleMapper scheduleMapper;

    @Override
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = doctorMapper.toEntity(doctorDTO);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toDto(savedDoctor);
    }

    @Override
    public DoctorDTO updateDoctor(UUID id, DoctorDTO doctorDTO) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        
        doctorMapper.updateDoctorFromDto(doctorDTO, existingDoctor);
        Doctor updatedDoctor = doctorRepository.save(existingDoctor);
        return doctorMapper.toDto(updatedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDTO getDoctorById(UUID id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDTO getDoctorByUserId(UUID userId) {
        return doctorRepository.findByUserUsername(userId.toString())
                .map(doctorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found for user: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorDTO> getAllDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable)
                .map(doctorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorDTO> getDoctorsBySpeciality(String speciality, Pageable pageable) {
        return doctorRepository.findBySpeciality(speciality, pageable)
                .map(doctorMapper::toDto);
    }

    @Override
    public void deleteDoctor(UUID id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getDoctorSchedule(UUID doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        
        return doctor.getSchedules().stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleDTO addSchedule(UUID doctorId, ScheduleDTO scheduleDTO) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        
        Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
        schedule.setDoctor(doctor);
        doctor.addSchedule(schedule);
        
        doctorRepository.save(doctor);
        return scheduleMapper.toDto(schedule);
    }

    @Override
    public void removeSchedule(UUID doctorId, UUID scheduleId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + scheduleId));
        
        doctor.removeSchedule(schedule);
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDoctorAvailable(UUID doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        
        // Implement availability logic based on schedule and existing appointments
        return true; // Placeholder implementation
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableTimeSlots(UUID doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        
        // Implement time slot calculation logic based on schedule and existing appointments
        return List.of(); // Placeholder implementation
    }

    @Override
    @Transactional(readOnly = true)
    public long getCompletedAppointmentsCount(UUID doctorId) {
        // Implement appointment counting logic
        return 0; // Placeholder implementation
    }

    @Override
    @Transactional(readOnly = true)
    public double getAverageRating(UUID doctorId) {
        // Implement rating calculation logic
        return 0.0; // Placeholder implementation
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByLicenseNumber(String licenseNumber) {
        return doctorRepository.existsByLicenseNumber(licenseNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return doctorRepository.existsByUserEmail(email);
    }
}