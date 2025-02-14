package mobidoc.ci.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobidoc.ci.dto.ScheduleDTO;
import mobidoc.ci.model.enums.ScheduleStatus;
import mobidoc.ci.service.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule Management", description = "APIs for managing doctor schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/doctors/{doctorId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentDoctor(#doctorId)")
    @Operation(summary = "Create a new schedule for a doctor")
    public ResponseEntity<ScheduleDTO> createSchedule(
            @PathVariable UUID doctorId,
            @Valid @RequestBody ScheduleDTO scheduleDTO) {
        return new ResponseEntity<>(scheduleService.createSchedule(doctorId, scheduleDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isScheduleOwner(#id)")
    @Operation(summary = "Update an existing schedule")
    public ResponseEntity<ScheduleDTO> updateSchedule(
            @PathVariable UUID id,
            @Valid @RequestBody ScheduleDTO scheduleDTO) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, scheduleDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get schedule by ID")
    public ResponseEntity<ScheduleDTO> getSchedule(@PathVariable UUID id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @GetMapping("/doctors/{doctorId}")
    @Operation(summary = "Get all schedules for a doctor")
    public ResponseEntity<List<ScheduleDTO>> getDoctorSchedules(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(scheduleService.getDoctorSchedules(doctorId));
    }

    @GetMapping("/doctors/{doctorId}/status/{status}")
    @Operation(summary = "Get doctor schedules by status")
    public ResponseEntity<List<ScheduleDTO>> getDoctorSchedulesByStatus(
            @PathVariable UUID doctorId,
            @PathVariable ScheduleStatus status) {
        return ResponseEntity.ok(scheduleService.getDoctorSchedulesByStatus(doctorId, status));
    }

    @GetMapping("/doctors/{doctorId}/day/{dayOfWeek}")
    @Operation(summary = "Get doctor schedules by day of week")
    public ResponseEntity<List<ScheduleDTO>> getDoctorSchedulesByDay(
            @PathVariable UUID doctorId,
            @PathVariable Integer dayOfWeek) {
        return ResponseEntity.ok(scheduleService.getDoctorSchedulesByDay(doctorId, dayOfWeek));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isScheduleOwner(#id)")
    @Operation(summary = "Delete a schedule")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isScheduleOwner(#id)")
    @Operation(summary = "Update schedule status")
    public ResponseEntity<Void> updateScheduleStatus(
            @PathVariable UUID id,
            @RequestParam ScheduleStatus status) {
        scheduleService.updateScheduleStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/doctors/{doctorId}/available")
    @Operation(summary = "Check if time slot is available")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @PathVariable UUID doctorId,
            @RequestParam Integer dayOfWeek,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {
        return ResponseEntity.ok(scheduleService.isTimeSlotAvailable(doctorId, dayOfWeek, startTime, endTime));
    }

    @GetMapping("/doctors/{doctorId}/slots")
    @Operation(summary = "Get available slots for a specific date")
    public ResponseEntity<List<String>> getAvailableSlots(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getAvailableSlots(doctorId, date));
    }

    @PostMapping("/doctors/{doctorId}/weekly")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentDoctor(#doctorId)")
    @Operation(summary = "Create weekly schedule for a doctor")
    public ResponseEntity<Void> createWeeklySchedule(
            @PathVariable UUID doctorId,
            @Valid @RequestBody List<ScheduleDTO> weeklySchedule) {
        scheduleService.createWeeklySchedule(doctorId, weeklySchedule);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/doctors/{doctorId}/weekly")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentDoctor(#doctorId)")
    @Operation(summary = "Update weekly schedule for a doctor")
    public ResponseEntity<Void> updateWeeklySchedule(
            @PathVariable UUID doctorId,
            @Valid @RequestBody List<ScheduleDTO> weeklySchedule) {
        scheduleService.updateWeeklySchedule(doctorId, weeklySchedule);
        return ResponseEntity.ok().build();
    }
}