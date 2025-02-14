package mobidoc.ci.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobidoc.ci.dto.DoctorDTO;
import mobidoc.ci.dto.ScheduleDTO;
import mobidoc.ci.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "APIs for managing doctors")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new doctor")
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        return new ResponseEntity<>(doctorService.createDoctor(doctorDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentDoctor(#id)")
    @Operation(summary = "Update doctor information")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable UUID id, @Valid @RequestBody DoctorDTO doctorDTO) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctorDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID")
    public ResponseEntity<DoctorDTO> getDoctor(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping
    @Operation(summary = "Get all doctors with pagination")
    public ResponseEntity<Page<DoctorDTO>> getAllDoctors(Pageable pageable) {
        return ResponseEntity.ok(doctorService.getAllDoctors(pageable));
    }

    @GetMapping("/speciality/{speciality}")
    @Operation(summary = "Get doctors by speciality")
    public ResponseEntity<Page<DoctorDTO>> getDoctorsBySpeciality(
            @PathVariable String speciality,
            Pageable pageable) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpeciality(speciality, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a doctor")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/schedule")
    @Operation(summary = "Get doctor's schedule")
    public ResponseEntity<List<ScheduleDTO>> getDoctorSchedule(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctorSchedule(id));
    }

    @PostMapping("/{id}/schedule")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentDoctor(#id)")
    @Operation(summary = "Add schedule for doctor")
    public ResponseEntity<ScheduleDTO> addSchedule(
            @PathVariable UUID id,
            @Valid @RequestBody ScheduleDTO scheduleDTO) {
        return ResponseEntity.ok(doctorService.addSchedule(id, scheduleDTO));
    }

    @DeleteMapping("/{id}/schedule/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentDoctor(#id)")
    @Operation(summary = "Remove schedule from doctor")
    public ResponseEntity<Void> removeSchedule(
            @PathVariable UUID id,
            @PathVariable UUID scheduleId) {
        doctorService.removeSchedule(id, scheduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Check doctor's availability for a specific date")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(doctorService.isDoctorAvailable(id, date));
    }

    @GetMapping("/{id}/available-slots")
    @Operation(summary = "Get available time slots for a specific date")
    public ResponseEntity<List<String>> getAvailableTimeSlots(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(doctorService.getAvailableTimeSlots(id, date));
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get doctor's statistics")
    public ResponseEntity<DoctorDTO> getDoctorStats(@PathVariable UUID id) {
        DoctorDTO stats = DoctorDTO.builder()
                .id(id)
                .completedAppointments(doctorService.getCompletedAppointmentsCount(id))
                .averageRating(doctorService.getAverageRating(id))
                .build();
        return ResponseEntity.ok(stats);
    }
}