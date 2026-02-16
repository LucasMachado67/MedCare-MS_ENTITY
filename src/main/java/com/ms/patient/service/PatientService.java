package com.ms.patient.service;

import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms.patient.dto.PatientCreationDTO;
import com.ms.patient.dto.PatientResponseDTO;
import com.ms.patient.enums.PatientSituation;
import com.ms.patient.exceptions.BusinessException;
import com.ms.patient.exceptions.CpfAlreadyExistsException;
import com.ms.patient.exceptions.InvalidCpfException;
import com.ms.patient.mappers.PatientMapper;
import com.ms.patient.models.Patient;
import com.ms.patient.producers.UserCreationProducer;
import com.ms.patient.repositories.PatientRepository;

import jakarta.validation.Valid;

/**
 * Serviço responsável por gerir toda a lógica de negócio (CRUD e validações)
 * para a entidade {@link Patient}.
 *
 * <p>Esta classe interage com o {@link PatientRepository} para persistência
 * e utiliza o {@link UserCreationProducer} para comunicação assíncrona.</p>
 *
 * @author Lucas Edson machado
 * @since 2025-11-17
 */
@Service
public class PatientService {


    private final PatientRepository repository;
    private final PatientMapper mapper;
    private final UserCreationProducer userProducer;
    private final PersonService personService;

    
    public PatientService(PatientRepository repository, PatientMapper mapper, UserCreationProducer userProducer, PersonService personService) {
        this.repository = repository;
        this.mapper = mapper;
        this.userProducer = userProducer;
        this.personService = personService;
    }
     /**
     * Cria e persiste um novo paciente no sistema, aplicando as regras de negócio.
     *
     * <p>O processo envolve: 1. Conversão do DTO para a entidade {@link Patient};
     * 2. Definição da situação inicial do paciente; 3. Persistência no banco de dados;
     * 4. Publicação de um evento de criação de usuário para o serviço de autenticação
     * (uso do {@link UserCreationProducer}).
     *
     * @param dto O DTO de criação contendo os dados brutos do paciente.
     * @return O {@link PatientResponseDTO} do paciente recém-criado.
     * @throws CpfAlreadyExistsException se o CPF do DTO já estiver cadastrado (descomentar validação).
     * @throws InvalidCpfException se o CPF for inválido (descomentar validação).
     */
    @Transactional(rollbackFor = Exception.class)
    public Patient createPatient(@Valid PatientCreationDTO dto) throws JsonProcessingException {

        // 1. VALIDAÇÃO DE REGRA DE NEGÓCIO

        //Validação dos campos de Person via personService
        boolean result = personService.validatePersonInfo(dto);
        if(!result){
            throw new BusinessException("Invalid patient data");
        }

        // 2. CONVERSÃO DTO ≥ ENTIDADE
        // O Mapper cuida da criação de Person, Address e Patient.
        var patient = mapper.toPatient(dto);
        //Coloca NOT_DEFINED automáticamente como situação
        patient.setPatientSituation(PatientSituation.NOT_DEFINED);
        // 3. PERSISTÊNCIA
        var savedPatient = repository.save(patient);
        // ---------------------------------------------
        // ENVIO DO EVENTO ASSÍNCRONO
        // ---------------------------------------------
        // 4. Criando o objeto de evento
        userProducer.publishUserCreationToPatientEvent(savedPatient);
        return savedPatient;
    }
    /**
     * Busca um paciente pelo seu identificador único.
     *
     * @param id O ID do paciente a ser procurado.
     * @return A entidade {@link Patient} encontrada.
     * @throws NoSuchElementException Se nenhum paciente for encontrado com o 'ID' fornecido.
     */
    public Patient findById(long id){
        return repository.findById(id).orElseThrow(() -> new BusinessException("PATIENT NOT FOUND"));
    }
    /**
     * Retorna uma lista contendo todos os pacientes cadastrados no sistema.
     *
     * @return Uma {@link List} de entidades {@link Patient}. Pode ser uma lista vazia,
     * mas nunca {@code null}.
     */
    public List<Patient> findAll(){
        return repository.findAll();
    }

    public Patient updatePatient(@Valid PatientCreationDTO newDto, long patientId){

        // 1. VALIDAÇÃO DE REGRA DE NEGÓCIO

        var existingPatient = repository.findById(patientId).orElseThrow();
        //Validação dos campos de Person via personService
        boolean result = personService.validatePersonInfo(newDto);
        if(!result)
            throw new BusinessException("Invalid patient data");
        
        mapper.updatePatientFromDto(newDto, existingPatient);

        return repository.save(existingPatient);

    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePatient(long patientId){
        if(repository.findById(patientId).isPresent())
            repository.deleteById(patientId);
        else
            throw new NoSuchElementException("PATIENT NOT FOUND, nothing was deleted");
    }
}
