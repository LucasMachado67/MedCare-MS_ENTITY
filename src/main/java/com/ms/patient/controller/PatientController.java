package com.ms.patient.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.patient.dto.PatientCreationDTO;
import com.ms.patient.dto.PatientResponseDTO;
import com.ms.patient.mappers.PatientMapper;
import com.ms.patient.models.Patient;
import com.ms.patient.service.PatientService;

import jakarta.validation.Valid;
/**
 * Controller REST para gerenciar operações relacionadas ao paciente.
 * Mapeado para caminho base "/patient"
 * 
 * <p>Esta classe lida com requisições HTTP para criar, buscar e atualizar
 * informações de pacientes no sistema.
 * 
 * @author Lucas Edson Machado
 * @version 1.0.0
 */
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("patient")
public class PatientController {

    @Autowired
    private PatientService service;
    @Autowired
    private PatientMapper mapper;
    /**
     * Cria um paciente no sistema
     * 
     * <p>Recebe os dados do paciente no corpo da requisição (payload) e os
     * persiste no banco de dados.
     * 
     * @param dto O objeto de transferência de dados (DTO) contendo as
     * informações do novo paciente. O corpo deve ser formatado
     * como JSON.
     * @return ResponseEntity contendo o paciente recém-criado (DTO) com o 'ID' gerado,
     * e o status HTTP 201 (Created)
     * 
     * @throws ValidationException se o DTO for inválido.
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSISTANT') or hasRole('PATIENT')")
    public ResponseEntity<PatientResponseDTO> registerPatient(@RequestBody @Valid PatientCreationDTO dto) throws JsonProcessingException {

        Patient savedPatient = service.createPatient(dto);

        PatientResponseDTO response = mapper.toPatientResponseDTO(savedPatient);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    /**
     * Retorna os dados do paciente através do id
     * 
     * <p>O 'ID' é esperado como uma variável de caminho (path variable).
     * 
     * @param id O identificador único do paciente (geralmente um Long ou UUID)
     * a ser buscado no sistema.
     * @return ResponseEntity contendo o PacienteDTO correspondente e o status ou ResponseEntity.notFound()
     * HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> findById(@PathVariable long id){
        
        Patient foundPatient = service.findById(id);

        if(foundPatient == null){
            return ResponseEntity.notFound().build();
        }
        
        PatientResponseDTO responseDTO = mapper.toPatientResponseDTO(foundPatient);

        return ResponseEntity.ok(responseDTO);
    }
    
    /**
     * Retorna os dados de todos os pacientes
     * 
     * @return ResponseEntity contendo a lista de PacienteDTO e o status
     * HTTP 200 (OK).
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT')")
    public ResponseEntity<List<PatientResponseDTO>> findAll(){
        
        List<Patient> patients = service.findAll();
        List<PatientResponseDTO> responseDTO = mapper.toDtoResponse(patients);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT', 'PATIENT')")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable long patientId, @Valid @RequestBody PatientCreationDTO entity) {

        
        Patient updatedPatient = service.updatePatient(entity, patientId);
        PatientResponseDTO response = mapper.toPatientResponseDTO(updatedPatient);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT', 'ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable long patientId){
        
        service.deletePatient(patientId);
        return ResponseEntity.noContent().build();
    }
}
