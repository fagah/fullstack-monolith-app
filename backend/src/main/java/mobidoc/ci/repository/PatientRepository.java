package mobidoc.ci.repository;

import mobidoc.ci.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByUserUsername(String username);
    Optional<Patient> findByUserEmail(String email);
    
    @Query("SELECT p FROM Patient p WHERE p.user.profile.firstName LIKE %?1% OR p.user.profile.lastName LIKE %?1%")
    Page<Patient> searchByName(String name, Pageable pageable);
    
    @Query("SELECT DISTINCT p.bloodGroup FROM Patient p WHERE p.bloodGroup IS NOT NULL")
    List<String> findAllBloodGroups();
    
    Page<Patient> findByBloodGroup(String bloodGroup, Pageable pageable);
    
    @Query("SELECT p FROM Patient p WHERE p.medicalHistory LIKE %?1%")
    Page<Patient> searchByMedicalHistory(String keyword, Pageable pageable);
}