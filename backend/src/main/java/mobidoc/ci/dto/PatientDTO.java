package mobidoc.ci.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PatientDTO {
    private UUID id;
    
    private UserDTO user;
    
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group")
    private String bloodGroup;
    
    private String emergencyContactName;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String emergencyContactPhone;
    
    private String medicalHistory;
    
    private String allergies;
    
    private List<MedicalRecordDTO> medicalRecords;
    
    // Summary fields for quick access
    private long totalAppointments;
    private long pendingAppointments;
    private long completedAppointments;
    private String lastVisitDate;
}