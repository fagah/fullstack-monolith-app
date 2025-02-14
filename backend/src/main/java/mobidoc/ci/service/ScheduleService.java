package mobidoc.ci.service;

import mobidoc.ci.dto.ScheduleDTO;
import mobidoc.ci.model.enums.ScheduleStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ScheduleService {
    ScheduleDTO createSchedule(UUID doctorId, ScheduleDTO scheduleDTO);
    ScheduleDTO updateSchedule(UUID id, ScheduleDTO scheduleDTO);
    ScheduleDTO getScheduleById(UUID id);
    List<ScheduleDTO> getDoctorSchedules(UUID doctorId);
    List<ScheduleDTO> getDoctorSchedulesByStatus(UUID doctorId, ScheduleStatus status);
    List<ScheduleDTO> getDoctorSchedulesByDay(UUID doctorId, Integer dayOfWeek);
    void deleteSchedule(UUID id);
    void updateScheduleStatus(UUID id, ScheduleStatus status);
    
    // Validation and checks
    boolean isTimeSlotAvailable(UUID doctorId, Integer dayOfWeek, LocalTime startTime, LocalTime endTime);
    List<String> getAvailableSlots(UUID doctorId, LocalDate date);
    
    // Bulk operations
    void createWeeklySchedule(UUID doctorId, List<ScheduleDTO> weeklySchedule);
    void updateWeeklySchedule(UUID doctorId, List<ScheduleDTO> weeklySchedule);
}