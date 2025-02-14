package mobidoc.ci.repository;

import mobidoc.ci.model.Schedule;
import mobidoc.ci.model.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByDoctorId(UUID doctorId);
    
    List<Schedule> findByDoctorIdAndStatus(UUID doctorId, ScheduleStatus status);
    
    List<Schedule> findByDoctorIdAndDayOfWeek(UUID doctorId, Integer dayOfWeek);
    
    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = ?1 AND s.dayOfWeek = ?2 AND s.startTime <= ?3 AND s.endTime >= ?3")
    List<Schedule> findOverlappingSchedules(UUID doctorId, Integer dayOfWeek, LocalTime time);
    
    boolean existsByDoctorIdAndDayOfWeekAndStartTimeBetween(
        UUID doctorId, 
        Integer dayOfWeek, 
        LocalTime startTime, 
        LocalTime endTime
    );
}