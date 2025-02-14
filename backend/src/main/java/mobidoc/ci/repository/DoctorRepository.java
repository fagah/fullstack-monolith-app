package mobidoc.ci.repository;

import mobidoc.ci.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Optional<Doctor> findByUserUsername(String username);
    Optional<Doctor> findByUserEmail(String email);
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    List<Doctor> findBySpeciality(String speciality);
    Page<Doctor> findBySpeciality(String speciality, Pageable pageable);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByUserEmail(String email);
}