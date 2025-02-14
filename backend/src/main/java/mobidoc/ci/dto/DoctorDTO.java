package mobidoc.ci.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class DoctorDTO {
    private UUID id;
    
    private UserDTO user;
    
    @NotBlank
    private String speciality;
    
    @NotBlank
    private String licenseNumber;
    
    @Positive
    private Integer experienceYears;
    
    private String biography;
    
    @NotNull
    @Positive
    private BigDecimal consultationFee;
    
    private List<ScheduleDTO> schedules;
    
    // Summary fields for quick access
    private long totalAppointments;
    private double averageRating;
    private long completedAppointments;
}