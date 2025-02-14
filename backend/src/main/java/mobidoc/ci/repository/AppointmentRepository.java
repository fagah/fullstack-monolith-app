package mobidoc.ci.repository;

import mobidoc.ci.model.Appointment;
import mobidoc.ci.model.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
    Page<Appointment> findByDoctorId(UUID doctorId, Pageable pageable);
    Page<Appointment> findByPatientId(UUID patientId, Pageable pageable);
    
    Page<Appointment> findByDoctorIdAndStatus(UUID doctorId, AppointmentStatus status, Pageable pageable);
    Page<Appointment> findByPatientIdAndStatus(UUID patientId, AppointmentStatus status, Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = ?1 AND a.appointmentDateTime BETWEEN ?2 AND ?3")
    List<Appointment> findDoctorAppointmentsForDay(UUID doctorId, ZonedDateTime start, ZonedDateTime end);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = ?1 AND a.status = ?2 AND a.appointmentDateTime BETWEEN ?3 AND ?4")
    List<Appointment> findDoctorAppointmentsForDayByStatus(UUID doctorId, AppointmentStatus status, ZonedDateTime start, ZonedDateTime end);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = ?1 AND a.appointmentDateTime BETWEEN ?2 AND ?3")
    long countDoctorAppointmentsForDay(UUID doctorId, ZonedDateTime start, ZonedDateTime end);
    
    @Query("SELECT a FROM Appointment a WHERE a.status = 'SCHEDULED' AND a.appointmentDateTime < ?1")
    List<Appointment> findOverdueAppointments(ZonedDateTime currentTime);
}