package com.ms.patient.service;

import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.patient.exceptions.BusinessException;
import com.ms.patient.utils.RegistrationNumber;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import com.ms.patient.dto.AssistantCreationDTO;
import com.ms.patient.dto.AssistantResponseDTO;
import com.ms.patient.exceptions.CpfAlreadyExistsException;
import com.ms.patient.mappers.AssistantMapper;
import com.ms.patient.models.Assistant;
import com.ms.patient.models.Medic;
import com.ms.patient.producers.UserCreationProducer;
import com.ms.patient.repositories.AssistantRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssistantService {

    private final AssistantRepository repository;
    private final AssistantMapper mapper;
    private final UserCreationProducer assistantProducer;
    private final PersonService personService;

    /**
     * Construtor para injeção de dependências dos componentes de persistência,
     * mapeamento e produção de eventos.
     *
     * @param repository O repositório para acesso a dados de {@link Medic}.
     * @param mapper O mapper para conversão entre DTOs e entidades.
     * @param assistantProducer O produtor de eventos para criação de usuários.
     */
    public AssistantService(AssistantRepository repository, AssistantMapper mapper, UserCreationProducer assistantProducer, PersonService personService) {
        this.repository = repository;
        this.mapper = mapper;
        this.assistantProducer = assistantProducer;
        this.personService = personService;
    }

    /**
     * Cria e persiste um novo assistente no sistema, aplicando as regras de negócio.
     *
     *
     * @param dto O DTO de criação contendo os dados do novo assistente.
     * @return O {@link AssistantResponseDTO} do assistente recém-criado.
     * @throws RuntimeException se o CRM já estiver cadastrado.
     * @throws CpfAlreadyExistsException se o Cpf já estiver cadastrado.
     */
    public Assistant createAssistant(@Valid AssistantCreationDTO dto) throws JsonProcessingException {

        RegistrationNumber registrationNumber = new RegistrationNumber();
        // 1. VALIDAÇÃO DE REGRA DE NEGÓCIO

        //Validação dos campos de Person via personService
        boolean result = personService.validatePersonInfo(dto);
        if(!result){
            throw new BusinessException("Invalid assistant data");
        }

        do {
            dto.setRegistrationNumber(registrationNumber.generateNumber());
        } while (repository.existsByRegistrationNumber(dto.getRegistrationNumber()));

        // 2. CONVERSÃO DTO ≥ ENTIDADE
        // O Mapper cuida da criação de Person, Address e Assistant.
        Assistant assistant = mapper.toAssistant(dto);

        // 3. PERSISTÊNCIA
        Assistant savedAssistant = repository.save(assistant);

        // ---------------------------------------------
        // ENVIO DO EVENTO ASSÍNCRONO
        // ---------------------------------------------

        // 4. Criando o objeto de evento
        assistantProducer.publishUserCreationToAssistantEvent(savedAssistant);
        
        return savedAssistant;
    }

    /**
     * Retorna uma lista de todos os assistentes cadastrados no sistema.
     *
     * @return Uma {@link List} de {@link AssistantResponseDTO}s. Pode ser uma lista vazia,
     * mas nunca {@code null}.
     */
    public List<Assistant> findAll(){
        return repository.findAll();
    }

    /**
     * Busca um assistente pelo seu identificador único.
     *
     * @param id O 'ID' do assistente a ser procurado.
     * @return A entidade {@link Medic} encontrada.
     * @throws NoSuchElementException Se nenhum assistente for encontrado com o 'ID' fornecido.
     */
    public Assistant findById(long id){
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("ASSISTANT NOT FOUND"));
    }


    public Assistant updateAssistant(@Valid AssistantCreationDTO newDto, long assistantId){

        // 1. VALIDAÇÃO DE REGRA DE NEGÓCIO

        var existingAssistant = repository.findById(assistantId).orElseThrow();
        //Validação dos campos de Person via personService
        boolean result = personService.validatePersonInfo(newDto);
        if(!result)
            throw new BusinessException("Invalid assistant data");

        mapper.updateAssistantFromDto(newDto, existingAssistant);

        return repository.save(existingAssistant);

    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAssistant(long assistantId){
        if(repository.findById(assistantId).isPresent())
            repository.deleteById(assistantId);
        else
            throw new NoSuchElementException("ASSISTANT NOT FOUND, nothing was deleted");
    }
}
