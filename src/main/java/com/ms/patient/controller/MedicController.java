package com.ms.patient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.patient.dto.MedicCreationDTO;
import com.ms.patient.dto.MedicResponseDTO;
import com.ms.patient.mappers.MedicMapper;
import com.ms.patient.models.Medic;
import com.ms.patient.service.MedicService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para gerir operações relacionadas à entidade Médico (Medic).
 *
 * <p>Mapeado para o caminho base "/medics". Lida com a criação e consulta de registros
 * de médicos no sistema.</p>
 *
 * @author Lucas Edson Machado
 * @since 2025-11-17
 */
@RestController
@RequestMapping("medic")
public class MedicController {

    private final MedicService service;
    private final MedicMapper mapper;


    public MedicController(MedicService service, MedicMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }
    /**
     * Registry um novo médico no sistema.
     *
     * <p>Recebe o DTO de criação no corpo da requisição, delega ao serviço para persistência
     * e retorna o recurso criado.</p>
     *
     * @param dto Os dados do médico, incluindo dados da Pessoa e Endereço.
     * @return ResponseEntity contendo o {@link MedicResponseDTO} do médico criado
     * e o status HTTP 201 (Created).
     * @throws jakarta.validation.ValidationException Se o DTO for inválido.
     * @throws RuntimeException (ou exceção de negócio específica, como CRM já existe)
     * se a regra de negócio for violada (mapeada para 4xx ou 500).
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDIC') or hasRole('ASSISTANT')")
    public ResponseEntity<MedicResponseDTO> registerMedic(@RequestBody @Valid MedicCreationDTO dto) throws JsonProcessingException {

        Medic responseDTO = service.createMedic(dto);

        MedicResponseDTO response = mapper.toMedicResponseDTO(responseDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Busca os detalhes de um médico específico pelo seu 'ID'.
     *
     * @param id O identificador único do médico a ser buscado.
     * @return ResponseEntity contendo o {@link MedicResponseDTO} correspondente
     * e o status HTTP 200 (OK).
     * @throws NoSuchElementException Se nenhum médico for encontrado com o 'ID' fornecido (mapeado para 404 Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicResponseDTO> findById(@PathVariable long id){
        
        Medic foundMedic = service.findById(id);

        if(foundMedic == null){
            ResponseEntity.notFound().build();
        }
        
        MedicResponseDTO responseDTO = mapper.toMedicResponseDTO(foundMedic);
        return ResponseEntity.ok(responseDTO);
    }
    /**
     * Retorna os dados de todos os pacientes
     * 
     * @return ResponseEntity contendo a lista de PacienteDTO e o status
     * HTTP 200 (OK).
     */
    @GetMapping("/all")
    public ResponseEntity<List<MedicResponseDTO>> findAll(){
        
        List<Medic> medics = service.findAll();
        List<MedicResponseDTO> response = mapper.toDtoResponse(medics);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT', 'MEDIC')")
    public ResponseEntity<MedicResponseDTO> updateMedic(@PathVariable long medicId, @Valid @RequestBody MedicCreationDTO entity) {

        
        Medic medicUpdated = service.updateMedic(entity, medicId);
        MedicResponseDTO response = mapper.toMedicResponseDTO(medicUpdated);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ASSISTANT', 'ADMIN')")
    public ResponseEntity<Void> deleteMedic(@PathVariable long medicId){
        
        service.deleteMedic(medicId);
        return ResponseEntity.noContent().build();
    }
}
