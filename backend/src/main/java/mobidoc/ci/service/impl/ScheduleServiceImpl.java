package mobidoc.ci.service.impl;

import lombok.RequiredArgsConstructor;
import mobidoc.ci.dto.ScheduleDTO;
import mobidoc.ci.exception.ResourceNotFoundException;
import mobidoc.ci.exception.ScheduleConflictException;
import mobidoc.ci.mapper.ScheduleMapper;
import mobidoc.ci.model.Doctor;
import mobidoc.ci.model.Schedule;
import mobidoc.ci.model.enums.ScheduleStatus;
import mobidoc.ci.repository.DoctorRepository;
import mobidoc.ci.repository.ScheduleRepository;
import mobidoc.ci.service.ScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final ScheduleMapper scheduleMapper;

    @Override
    public ScheduleDTO createSchedule(UUID doctorId, ScheduleDTO scheduleDTO) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        if (!isTimeSlotAvailable(doctorId, scheduleDTO.getDayOfWeek(), 
                               scheduleDTO.getStartTime(), scheduleDTO.getEndTime())) {
            throw new ScheduleConflictException("Time slot is not available");
        }

        Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
        schedule.setDoctor(doctor);
        schedule.setStatus(ScheduleStatus.ACTIVE);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(savedSchedule);
    }

    @Override
    public ScheduleDTO updateSchedule(UUID id, ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));

        if (!isTimeSlotAvailable(schedule.getDoctor().getId(), scheduleDTO.getDayOfWeek(),
                               scheduleDTO.getStartTime(), scheduleDTO.getEndTime())) {
            throw new ScheduleConflictException("Time slot is not available");
        }

        scheduleMapper.updateScheduleFromDto(scheduleDTO, schedule);
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(updatedSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleDTO getScheduleById(UUID id) {
        return scheduleRepository.findById(id)
                .map(scheduleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getDoctorSchedules(UUID doctorId) {
        return scheduleRepository.findByDoctorId(doctorId).stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getDoctorSchedulesByStatus(UUID doctorId, ScheduleStatus status) {
        return scheduleRepository.findByDoctorIdAndStatus(doctorId, status).stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getDoctorSchedulesByDay(UUID doctorId, Integer dayOfWeek) {
        return scheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek).stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSchedule(UUID id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Schedule not found with id: " + id);
        }
        scheduleRepository.deleteById(id);
    }

    @Override
    public void updateScheduleStatus(UUID id, ScheduleStatus status) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
        schedule.setStatus(status);
        scheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(UUID doctorId, Integer dayOfWeek, LocalTime startTime, LocalTime endTime) {
        List<Schedule> overlappingSchedules = scheduleRepository.findOverlappingSchedules(doctorId, dayOfWeek, startTime);
        return overlappingSchedules.isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableSlots(UUID doctorId, LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        List<Schedule> schedules = scheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek);
        
        List<String> availableSlots = new ArrayList<>();
        schedules.stream()
                .filter(s -> s.getStatus() == ScheduleStatus.ACTIVE)
                .forEach(schedule -> {
                    LocalTime currentTime = schedule.getStartTime();
                    while (currentTime.plusMinutes(30).isBefore(schedule.getEndTime()) || 
                           currentTime.plusMinutes(30).equals(schedule.getEndTime())) {
                        availableSlots.add(currentTime.toString());
                        currentTime = currentTime.plusMinutes(30);
                    }
                });
        
        return availableSlots;
    }

    @Override
    public void createWeeklySchedule(UUID doctorId, List<ScheduleDTO> weeklySchedule) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        List<Schedule> schedules = weeklySchedule.stream()
                .map(scheduleDTO -> {
                    Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
                    schedule.setDoctor(doctor);
                    schedule.setStatus(ScheduleStatus.ACTIVE);
                    return schedule;
                })
                .collect(Collectors.toList());

        scheduleRepository.saveAll(schedules);
    }

    @Override
    public void updateWeeklySchedule(UUID doctorId, List<ScheduleDTO> weeklySchedule) {
        // Delete existing schedules
        List<Schedule> existingSchedules = scheduleRepository.findByDoctorId(doctorId);
        scheduleRepository.deleteAll(existingSchedules);

        // Create new schedules
        createWeeklySchedule(doctorId, weeklySchedule);
    }
}