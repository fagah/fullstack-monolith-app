package mobidoc.ci.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mobidoc.ci.model.enums.AppointmentStatus;
import mobidoc.ci.model.enums.AppointmentType;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class AppointmentDTO {
    private UUID id;
    
    @NotNull
    private UUID patientId;
    
    @NotNull
    private UUID doctorId;
    
    @NotNull
    @Future
    private ZonedDateTime appointmentDateTime;
    
    @NotNull
    private AppointmentType type;
    
    private AppointmentStatus status;
    
    private String notes;
    
    private String cancelReason;
    
    // Additional fields for response
    private String doctorName;
    private String patientName;
    private String doctorSpeciality;
    private String patientPhone;
    
    // Payment related fields
    private boolean isPaid;
    private String paymentStatus;
    private UUID paymentId;
}