package mobidoc.ci.service.impl;

import lombok.RequiredArgsConstructor;
import mobidoc.ci.dto.MedicalRecordDTO;
import mobidoc.ci.dto.PatientDTO;
import mobidoc.ci.exception.ResourceNotFoundException;
import mobidoc.ci.mapper.MedicalRecordMapper;
import mobidoc.ci.mapper.PatientMapper;
import mobidoc.ci.model.Document;
import mobidoc.ci.model.MedicalRecord;
import mobidoc.ci.model.Patient;
import mobidoc.ci.repository.DocumentRepository;
import mobidoc.ci.repository.MedicalRecordRepository;
import mobidoc.ci.repository.PatientRepository;
import mobidoc.ci.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final DocumentRepository documentRepository;
    private final PatientMapper patientMapper;
    private final MedicalRecordMapper medicalRecordMapper;

    @Override
    public PatientDTO createPatient(PatientDTO patientDTO) {
        Patient patient = patientMapper.toEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toDto(savedPatient);
    }

    @Override
    public PatientDTO updatePatient(UUID id, PatientDTO patientDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        
        patientMapper.updatePatientFromDto(patientDTO, existingPatient);
        Patient updatedPatient = patientRepository.save(existingPatient);
        return patientMapper.toDto(updatedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO getPatientById(UUID id) {
        return patientRepository.findById(id)
                .map(patientMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDTO getPatientByUserId(UUID userId) {
        return patientRepository.findByUserUsername(userId.toString())
                .map(patientMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for user: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(patientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientDTO> searchPatients(String searchTerm, Pageable pageable) {
        return patientRepository.searchByName(searchTerm, pageable)
                .map(patientMapper::toDto);
    }

    @Override
    public void deletePatient(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> getPatientMedicalRecords(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
        
        return patient.getMedicalRecords().stream()
                .map(medicalRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MedicalRecordDTO addMedicalRecord(UUID patientId, MedicalRecordDTO recordDTO) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
        
        MedicalRecord record = medicalRecordMapper.toEntity(recordDTO);
        record.setPatient(patient);
        patient.addMedicalRecord(record);
        
        patientRepository.save(patient);
        return medicalRecordMapper.toDto(record);
    }

    @Override
    public void deleteMedicalRecord(UUID patientId, UUID recordId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
        
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + recordId));
        
        patient.removeMedicalRecord(record);
        patientRepository.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllBloodGroups() {
        return patientRepository.findAllBloodGroups();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientDTO> getPatientsByBloodGroup(String bloodGroup, Pageable pageable) {
        return patientRepository.findByBloodGroup(bloodGroup, pageable)
                .map(patientMapper::toDto);
    }

    @Override
    public void addDocument(UUID patientId, String documentType, byte[] content) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
        
        Document document = new Document();
        document.setPatient(patient);
        document.setType(documentType);
        document.setContent(content);
        
        documentRepository.save(document);
    }

    @Override
    public void deleteDocument(UUID patientId, UUID documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new ResourceNotFoundException("Document not found with id: " + documentId);
        }
        documentRepository.deleteById(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
        return document.getContent();
    }
}