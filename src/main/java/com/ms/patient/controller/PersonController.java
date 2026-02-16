package com.ms.patient.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.patient.service.PersonService;
import com.ms.patient.dto.PersonEmailSenderDto;
import com.ms.patient.dto.PersonResponseDTO;
import com.ms.patient.mappers.PersonMapper;
import com.ms.patient.models.Person;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador REST para gerir operações CRUD (Criação, Leitura, Atualização e Deleção)
 * da entidade Pessoa (Person).
 *
 * <p>Mapeado para o caminho base "/person". Lida com a criação, validação de CPF/e-mail
 * e manipulação dos dados básicos de uma pessoa.</p>
 *
 * @author Lucas Edson Machado
 * @since 2025-11-17
 */
@RestController
@RequestMapping("person")
public class PersonController {

    @Autowired
    private PersonService service;

    @Autowired
    private PersonMapper mapper;

    /**
     * Busca os detalhes de uma pessoa específica pelo seu 'ID'.
     *
     * @param id O identificador único da pessoa a ser buscada.
     * @return ResponseEntity contendo o {@link PersonResponseDTO} correspondente
     * e o status HTTP 200 (OK).
     * @throws NoSuchElementException Se nenhuma pessoa for encontrada com o 'ID' fornecido (mapeado para 404).
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> findPersonById(@PathVariable long id) {
        
        //Pegando o objeto person do service
        Person entity = service.findPersonById(id);
        //Transformando em DTO de resposta
        PersonResponseDTO responseDto = mapper.toDtoResponse(entity);
        //Mandando o DTO
        return ResponseEntity.ok(responseDto);
    }
    /**
     * Busca uma pessoa pelo 'ID' e retorna um DTO simplificado, contendo apenas
     * o Nome, 'ID' e e-mail, tipicamente usado por serviços de comunicação (envio de e-mail).
     *
     * @param id O identificador único da pessoa a ser buscada.
     * @return ResponseEntity contendo o {@link PersonEmailSenderDto} e o status HTTP 200 (OK).
     * @throws NoSuchElementException Se nenhuma pessoa for encontrada com o 'ID' fornecido (mapeado para 404).
     */
    @GetMapping("/email/{id}")
    public ResponseEntity<PersonEmailSenderDto> findPersonToSendEmail(@PathVariable long id) {
        //Pegando o objeto person do service
        PersonEmailSenderDto entity = service.findPersonByIdToSendEmail(id);
        return ResponseEntity.ok(entity);
    }
    /**
     * Lista todas as pessoas cadastradas no sistema.
     *
     * @return ResponseEntity contendo uma {@link List} de {@link PersonResponseDTO}s
     * e o status HTTP 200 (OK).
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PersonResponseDTO>> findAll() {
        //Pegando a lista de pessoas do service
        List<Person> persons = service.findAll();
        //Transformando a lista em responseDto
        List<PersonResponseDTO> personResponseDTOs = mapper.toDtoResponse(persons);
        //Retornando a lista
        return ResponseEntity.ok(personResponseDTOs);

    }

}
