package mobidoc.ci.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobidoc.ci.dto.AppointmentDTO;
import mobidoc.ci.model.enums.AppointmentStatus;
import mobidoc.ci.service.AppointmentService;
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
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "APIs for managing appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'PATIENT')")
    @Operation(summary = "Schedule a new appointment")
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        return new ResponseEntity<>(appointmentService.createAppointment(appointmentDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @userSecurity.canAccessAppointment(#id)")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<AppointmentDTO> getAppointment(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping("/doctors/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @userSecurity.isCurrentDoctor(#doctorId)")
    @Operation(summary = "Get doctor's appointments")
    public ResponseEntity<Page<AppointmentDTO>> getDoctorAppointments(
            @PathVariable UUID doctorId,
            Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctorId, pageable));
    }

    @GetMapping("/patients/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @userSecurity.isCurrentPatient(#patientId)")
    @Operation(summary = "Get patient's appointments")
    public ResponseEntity<Page<AppointmentDTO>> getPatientAppointments(
            @PathVariable UUID patientId,
            Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId, pageable));
    }

    @GetMapping("/doctors/{doctorId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @userSecurity.isCurrentDoctor(#doctorId)")
    @Operation(summary = "Get doctor's appointments for a specific date")
    public ResponseEntity<List<AppointmentDTO>> getDoctorAppointmentsForDate(
            @PathVariable UUID doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointmentsForDate(doctorId, date));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @userSecurity.canManageAppointment(#id)")
    @Operation(summary = "Update appointment status")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(
            @PathVariable UUID id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @userSecurity.canAccessAppointment(#id)")
    @Operation(summary = "Cancel appointment")
    public ResponseEntity<AppointmentDTO> cancelAppointment(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, reason));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get appointments by status")
    public ResponseEntity<Page<AppointmentDTO>> getAppointmentsByStatus(
            @PathVariable AppointmentStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByStatus(status, pageable));
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get appointments for a specific date")
    public ResponseEntity<Page<AppointmentDTO>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDate(date, pageable));
    }

    @PostMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @userSecurity.canAccessAppointment(#id)")
    @Operation(summary = "Reschedule an appointment")
    public ResponseEntity<AppointmentDTO> rescheduleAppointment(
            @PathVariable UUID id,
            @Valid @RequestBody AppointmentDTO appointmentDTO) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, appointmentDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an appointment")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/conflicts")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Check for appointment conflicts")
    public ResponseEntity<Boolean> checkForConflicts(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        return ResponseEntity.ok(appointmentService.hasConflicts(appointmentDTO));
    }
}