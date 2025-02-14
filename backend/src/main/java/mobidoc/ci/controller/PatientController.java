package mobidoc.ci.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mobidoc.ci.dto.MedicalRecordDTO;
import mobidoc.ci.dto.PatientDTO;
import mobidoc.ci.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "APIs for managing patients")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Register a new patient")
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        return new ResponseEntity<>(patientService.createPatient(patientDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @userSecurity.isCurrentPatient(#id)")
    @Operation(summary = "Update patient information")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable UUID id, @Valid @RequestBody PatientDTO patientDTO) {
        return ResponseEntity.ok(patientService.updatePatient(id, patientDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF') or @userSecurity.isCurrentPatient(#id)")
    @Operation(summary = "Get patient by ID")
    public ResponseEntity<PatientDTO> getPatient(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get all patients with pagination")
    public ResponseEntity<Page<PatientDTO>> getAllPatients(Pageable pageable) {
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF')")
    @Operation(summary = "Search patients")
    public ResponseEntity<Page<PatientDTO>> searchPatients(
            @RequestParam String searchTerm,
            Pageable pageable) {
        return ResponseEntity.ok(patientService.searchPatients(searchTerm, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/medical-records")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF') or @userSecurity.isCurrentPatient(#id)")
    @Operation(summary = "Get patient's medical records")
    public ResponseEntity<List<MedicalRecordDTO>> getMedicalRecords(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientMedicalRecords(id));
    }

    @PostMapping("/{id}/medical-records")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Add medical record")
    public ResponseEntity<MedicalRecordDTO> addMedicalRecord(
            @PathVariable UUID id,
            @Valid @RequestBody MedicalRecordDTO recordDTO) {
        return ResponseEntity.ok(patientService.addMedicalRecord(id, recordDTO));
    }

    @DeleteMapping("/{id}/medical-records/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete medical record")
    public ResponseEntity<Void> deleteMedicalRecord(
            @PathVariable UUID id,
            @PathVariable UUID recordId) {
        patientService.deleteMedicalRecord(id, recordId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/blood-groups")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF')")
    @Operation(summary = "Get all blood groups")
    public ResponseEntity<List<String>> getAllBloodGroups() {
        return ResponseEntity.ok(patientService.getAllBloodGroups());
    }

    @GetMapping("/blood-group/{bloodGroup}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF')")
    @Operation(summary = "Get patients by blood group")
    public ResponseEntity<Page<PatientDTO>> getPatientsByBloodGroup(
            @PathVariable String bloodGroup,
            Pageable pageable) {
        return ResponseEntity.ok(patientService.getPatientsByBloodGroup(bloodGroup, pageable));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF')")
    @Operation(summary = "Upload patient document")
    public ResponseEntity<Void> uploadDocument(
            @PathVariable UUID id,
            @RequestParam("type") String documentType,
            @RequestParam("file") MultipartFile file) {
        try {
            patientService.addDocument(id, documentType, file.getBytes());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/documents/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF') or @userSecurity.isCurrentPatient(#id)")
    @Operation(summary = "Get patient document")
    public ResponseEntity<byte[]> getDocument(
            @PathVariable UUID id,
            @PathVariable UUID documentId) {
        return ResponseEntity.ok(patientService.getDocument(documentId));
    }
}