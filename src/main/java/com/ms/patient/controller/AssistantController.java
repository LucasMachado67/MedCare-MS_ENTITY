package com.ms.patient.controller;

import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.patient.dto.AssistantCreationDTO;
import com.ms.patient.dto.AssistantResponseDTO;
import com.ms.patient.mappers.AssistantMapper;
import com.ms.patient.models.Assistant;
import com.ms.patient.service.AssistantService;

import jakarta.validation.Valid;

/**
 * Controlador REST para gerenciar operações relacionadas à entidade Assitente (Assistant).
 *
 * <p>Mapeado para o caminho base "/assistants". Lida com a criação e consulta de registros
 * de assistant no sistema.</p>
 *
 * @author Lucas Edson Machado
 * @since 2025-11-17
 */
@RestController
@RequestMapping("assistant")
public class AssistantController {

    private final AssistantService service;
    private final AssistantMapper mapper;


    public AssistantController(AssistantService service, AssistantMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Registra um novo assistente no sistema.
     *
     * <p>Recebe o DTO de criação no corpo da requisição, delega ao serviço para persistência
     * e retorna o recurso criado.</p>
     *
     * @param dto Os dados do assistente, incluindo dados da Pessoa e Endereço.
     * @return ResponseEntity contendo o {@link AssistantResponseDTO} do assistente criado
     * e o status HTTP 201 (Created).
     * @throws jakarta.validation.ValidationException Se o DTO não for válido.
     * @throws RuntimeException (ou exceção de negócio específica, como CRM já existe)
     * se a regra de negócio for violada (mapeada para 4xx ou 500).
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN')")
    public ResponseEntity<AssistantResponseDTO> registerAssistant(@RequestBody @Valid AssistantCreationDTO dto) throws JsonProcessingException {

        AssistantResponseDTO responseDTO = service.createAssistant(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDTO);
    }

    /**
     * Busca os detalhes de um assistente específico pelo seu ID.
     *
     * @param id O identificador único do assistente a ser buscado.
     * @return ResponseEntity contendo o {@link AssistantResponseDTO} correspondente
     * e o status HTTP 200 (OK).
     * @throws NoSuchElementException Se nenhum assistente for encontrado com o ID fornecido (mapeado para 404 Not Found).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN')")
    public ResponseEntity<AssistantResponseDTO> findById(@PathVariable long id){
        
        Assistant foundAssistant = service.findById(id);

        if(foundAssistant == null){
            ResponseEntity.notFound().build();
        }
        
        AssistantResponseDTO responseDTO = mapper.toAssistantResponseDTO(foundAssistant);
        return ResponseEntity.ok(responseDTO);
    }
    /**
     * Retorna os dados de todos os pacientes
     * 
     * @return ResponseEntity contendo a lista de AssistantDTO e o status
     * HTTP 200 (OK).
     */
    @GetMapping("/all")
    public ResponseEntity<List<AssistantResponseDTO>> findAll(){
        
        List<AssistantResponseDTO> assistants = service.findAll();

        return ResponseEntity.ok(assistants);
    }
}
