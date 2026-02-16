package com.ms.patient.serviceTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.patient.dto.PatientCreationDTO;
import com.ms.patient.dto.PatientResponseDTO;
import com.ms.patient.enums.Habitation;
import com.ms.patient.enums.PatientSituation;
import com.ms.patient.exceptions.BusinessException;
import com.ms.patient.mappers.AddressMapper;
import com.ms.patient.mappers.PatientMapper;
import com.ms.patient.models.Address;
import com.ms.patient.models.Patient;
import com.ms.patient.producers.UserCreationProducer;
import com.ms.patient.repositories.PatientRepository;
import com.ms.patient.service.PatientService;
import com.ms.patient.service.PersonService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {


    @Mock
    private PatientRepository repository;

    @InjectMocks
    private PatientService service;

    @Mock
    private UserCreationProducer userProducer;

    @Mock
    private PersonService personService;

    @Mock
    private PatientMapper mapperPatient;
    @Mock
    private AddressMapper mapperAddress;

    PatientCreationDTO patientCreationDTO;
    PatientResponseDTO patientReponseDTO;
    Patient patient;

    @BeforeEach
    void setup(){
        Address address = new Address();
        address.setStreet("Rua das Flores");
        address.setNumber(123);
        address.setNeighborhood("Centro");
        address.setCity("São Paulo");
        address.setState("SP");
        address.setZipCode("01234-567");
        address.setComplement("Apto 101");
        address.setHabitation(Habitation.APARTMENT);

        patient = new Patient();
        patientCreationDTO = new PatientCreationDTO();
        
        
        patient.setName("João Silva");
        patient.setCpf("123.456.789-00");
        patient.setGender("Masculino");
        patient.setEmail("joao@email.com");
        patient.setPhone("11999999999");
        patient.setBirthDate(new Date()); 
        patient.setAddress(address); 

        patient.setHealthPlan("Plano VIP");
        patient.setDescription("Paciente com histórico de hipertensão");
        patient.setPatientSituation(PatientSituation.STABLE);
        
        patientCreationDTO.setSymptoms(new ArrayList<>(List.of("Dor de cabeça", "Febre")));
        patientCreationDTO.setAllergies(new ArrayList<>(List.of("Dipirona")));
        patientCreationDTO.setName("João Silva");
        patientCreationDTO.setCpf("123.456.789-00");
        patientCreationDTO.setGender("Masculino");
        patientCreationDTO.setEmail("joao@email.com");
        patientCreationDTO.setPhone("11999999999");
        patientCreationDTO.setBirthDate(new Date()); 
        patientCreationDTO.setAddress(mapperAddress.toDtoCreation(address)); 

        patientCreationDTO.setHealthPlan("Plano VIP");
        patientCreationDTO.setDescription("Paciente com histórico de hipertensão");
        patientCreationDTO.setPatientSituation(PatientSituation.STABLE);
        
        patientCreationDTO.setSymptoms(new ArrayList<>(List.of("Dor de cabeça", "Febre")));
        patientCreationDTO.setAllergies(new ArrayList<>(List.of("Dipirona")));
    }

    @Nested
    class createPatient{

        @Test
        void shouldCreatePatient() throws JsonProcessingException{

            when(personService.validatePersonInfo(any())).thenReturn(true);

            when(mapperPatient.toPatient(any())).thenReturn(patient);

            when(repository.save(any())).thenReturn(patient);

            doNothing().when(userProducer).publishUserCreationToPatientEvent(any());

            when(repository.save(patient)).thenReturn(patient);

            Patient result = service.createPatient(patientCreationDTO);

            assertNotNull(result);
            assertEquals(patient.getName(), result.getName());
            verify(repository).save(any(Patient.class));
            assertEquals(PatientSituation.NOT_DEFINED, result.getPatientSituation());
        }

        @Test
        void shouldThrowWhenInvalidPatientData(){

            when(personService.validatePersonInfo(any())).thenReturn(false);

            BusinessException exception = 
                assertThrows(BusinessException.class, () -> service.createPatient(patientCreationDTO));

            assertEquals("Invalid patient data", exception.getMessage());
        }

    }

    @Nested
    class FindBy{

        @Test
        void shouldFindPatientById(){
            when(repository.findById(anyLong())).thenReturn(Optional.of(patient));

            Patient result = service.findById(anyLong());

            assertNotNull(result);
            assertEquals(patient.getName(), patient.getName());
        }

        @Test
        void shouldThrowIfNoPatientFound(){
            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            BusinessException exception = 
                assertThrows(BusinessException.class,() -> service.findById(anyLong()));

            assertEquals("PATIENT NOT FOUND", exception.getMessage());
        }

        @Test
        void shouldFindAllPatients(){
            Patient patient2 = new Patient();
            when(repository.findAll()).thenReturn(List.of(patient,patient2));

            List<Patient> result = service.findAll();

            assertEquals(2, result.size());
        }
    }

    @Nested
    class UpdatePatient{

        @Test
        void shouldUpdatePatient(){
            
            long idForTest = 1l;
            patient.setEmail("antigo@test.com"); // Email antigo
            patientCreationDTO.setEmail("novo@test.com");

            when(repository.findById(anyLong())).thenReturn(Optional.of(patient));
            when(personService.validatePersonInfo(patientCreationDTO)).thenReturn(true);

            doAnswer(invocation -> {
                PatientCreationDTO dto = invocation.getArgument(0);
                Patient p = invocation.getArgument(1);
                p.setEmail(dto.getEmail()); // Simula a atualização do campo
                return null;
            }).when(mapperPatient).updatePatientFromDto(any(PatientCreationDTO.class), any(Patient.class));

            when(repository.save(any(Patient.class))).thenReturn(patient);

            Patient result = service.updatePatient(patientCreationDTO, idForTest);

            assertNotNull(result);
            verify(repository).save(any(Patient.class));
            assertEquals("novo@test.com", result.getEmail());

        }

        @Test
        void shouldThrowIfPatientNotFound(){

            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> {
                service.updatePatient(patientCreationDTO, 1L);
            });
        }

        @Test
        void shouldThrowIfDtoIsInvalid(){


            when(repository.findById(anyLong())).thenReturn(Optional.of(patient));
            when(personService.validatePersonInfo(patientCreationDTO)).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class, () -> {
                service.updatePatient(patientCreationDTO, 1l);
            });

            assertEquals("Invalid patient data", exception.getMessage());
        }
    }

    @Nested
    class DeletePatient{

        @Test
        void shouldDeletePatientByid(){
            
            when(repository.findById(1l)).thenReturn(Optional.of(patient));

            doNothing().when(repository).deleteById(anyLong());

            service.deletePatient(1L);

            verify(repository, times(1)).deleteById(1l);
        }

        @Test
        void shouldThrowIfPatientNotFound(){
            
            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            NoSuchElementException exception = 
                assertThrows(NoSuchElementException.class, () -> service.deletePatient(99l));

            assertEquals("PATIENT NOT FOUND, nothing was deleted", exception.getMessage());
            verify(repository, never()).delete(any(Patient.class));
        }
    }    
}
