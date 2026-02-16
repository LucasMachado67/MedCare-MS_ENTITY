package com.ms.patient.serviceTests;

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
import com.ms.patient.dto.MedicCreationDTO;
import com.ms.patient.dto.MedicResponseDTO;
import com.ms.patient.enums.Habitation;
import com.ms.patient.exceptions.BusinessException;
import com.ms.patient.exceptions.CrmInvalidException;
import com.ms.patient.mappers.AddressMapper;
import com.ms.patient.mappers.MedicMapper;
import com.ms.patient.models.Address;
import com.ms.patient.models.Medic;
import com.ms.patient.producers.UserCreationProducer;
import com.ms.patient.repositories.MedicRepository;
import com.ms.patient.service.MedicService;
import com.ms.patient.service.PersonService;

@ExtendWith(MockitoExtension.class)
public class MedicServiceTest {

    @Mock
    private MedicRepository repository;

    @InjectMocks
    private MedicService service;

    @Mock
    private UserCreationProducer userProducer;

    @Mock
    private PersonService personService;

    @Mock
    private MedicMapper mapper;
    
    @Mock
    private AddressMapper mapperAddress;

    MedicCreationDTO medicCreationDTO;
    MedicResponseDTO medicReponseDTO;
    Medic medic;

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

        medic = new Medic();
        medicCreationDTO = new MedicCreationDTO();
        
        
        medic.setName("João Silva");
        medic.setCpf("123.456.789-00");
        medic.setGender("Masculino");
        medic.setEmail("joao@email.com");
        medic.setPhone("11999999999");
        medic.setBirthDate(new Date()); 
        medic.setAddress(address); 

        medic.setCrm("123321123");
        medic.setMedicalSpeciality("Cardiologista");
        
        medicCreationDTO.setName("João Silva");
        medicCreationDTO.setCpf("123.456.789-00");
        medicCreationDTO.setGender("Masculino");
        medicCreationDTO.setEmail("joao@email.com");
        medicCreationDTO.setPhone("11999999999");
        medicCreationDTO.setBirthDate(new Date()); 
        medicCreationDTO.setAddress(mapperAddress.toDtoCreation(address)); 

        medic.setCrm("123321123");
        medic.setMedicalSpeciality("Cardiologista");
        
    }
    
    @Nested
    class createMedic{
        
        @Test
        void shouldCreateMedic() throws JsonProcessingException{
            
            when(repository.existsByCrm(any())).thenReturn(false);
            when(personService.validatePersonInfo(any())).thenReturn(true);

            when(mapper.toMedic(any())).thenReturn(medic);

            when(repository.save(any())).thenReturn(medic);

            doNothing().when(userProducer).publishUserCreationToMedicEvent(any());

            when(repository.save(medic)).thenReturn(medic);

            Medic result = service.createMedic(medicCreationDTO);

            assertNotNull(result);
            assertEquals(medic.getName(), result.getName());
            verify(repository).save(any(Medic.class));
            assertEquals(medic.getCrm(), result.getCrm());
        }

        @Test
        void shouldThrowWhenCRMIsInvalid(){

            when(repository.existsByCrm(any())).thenReturn(true);
            
            CrmInvalidException exception = 
            assertThrows(CrmInvalidException.class, () -> service.createMedic(medicCreationDTO));
            
            assertEquals("CRM already registered", exception.getMessage());
        }
        
        @Test
        void shouldThrowWhenInvalidMedicData(){
            
            when(repository.existsByCrm(any())).thenReturn(false);
            when(personService.validatePersonInfo(any())).thenReturn(false);
            
            BusinessException exception = 
                assertThrows(BusinessException.class, () -> service.createMedic(medicCreationDTO));

            assertEquals("Invalid medic data", exception.getMessage());
        }

    }

    @Nested
    class FindBy{

        @Test
        void shouldFindMedicById(){
            when(repository.findById(anyLong())).thenReturn(Optional.of(medic));

            Medic result = service.findById(anyLong());

            assertNotNull(result);
            assertEquals(medic.getName(), medic.getName());
        }

        @Test
        void shouldThrowIfNoMedicFound(){
            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            BusinessException exception = 
                assertThrows(BusinessException.class,() -> service.findById(anyLong()));

            assertEquals("MEDIC NOT FOUND", exception.getMessage());
        }

        @Test
        void shouldFindAllMedics(){
            Medic medic2 = new Medic();
            when(repository.findAll()).thenReturn(List.of(medic,medic2));

            List<Medic> result = service.findAll();

            assertEquals(2, result.size());
        }
    }

    @Nested
    class UpdateMedic{

        @Test
        void shouldUpdateMedic(){
            
            long idForTest = 1l;
            medic.setEmail("antigo@test.com"); 
            medicCreationDTO.setEmail("novo@test.com");

            when(repository.findById(anyLong())).thenReturn(Optional.of(medic));
            when(personService.validatePersonInfo(medicCreationDTO)).thenReturn(true);

            doAnswer(invocation -> {
                MedicCreationDTO dto = invocation.getArgument(0);
                Medic p = invocation.getArgument(1);
                p.setEmail(dto.getEmail());
                return null;
            }).when(mapper).updateMedicFromDto(any(MedicCreationDTO.class), any(Medic.class));

            when(repository.save(any(Medic.class))).thenReturn(medic);

            Medic result = service.updateMedic(medicCreationDTO, idForTest);

            assertNotNull(result);
            verify(repository).save(any(Medic.class));
            assertEquals("novo@test.com", result.getEmail());

        }

        @Test
        void shouldThrowIfMedicNotFound(){

            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> {
                service.updateMedic(medicCreationDTO, 1L);
            });
        }

        @Test
        void shouldThrowIfDtoIsInvalid(){


            when(repository.findById(anyLong())).thenReturn(Optional.of(medic));
            when(personService.validatePersonInfo(medicCreationDTO)).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class, () -> {
                service.updateMedic(medicCreationDTO, 1l);
            });

            assertEquals("Invalid medic data", exception.getMessage());
        }
    }

    @Nested
    class DeleteMedic{

        @Test
        void shouldDeleteMedicByid(){
            
            when(repository.findById(1l)).thenReturn(Optional.of(medic));

            doNothing().when(repository).deleteById(anyLong());

            service.deleteMedic(1L);

            verify(repository, times(1)).deleteById(1l);
        }

        @Test
        void shouldThrowIfMedicNotFound(){
            
            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            NoSuchElementException exception = 
                assertThrows(NoSuchElementException.class, () -> service.deleteMedic(99l));

            assertEquals("MEDIC NOT FOUND, nothing was deleted", exception.getMessage());
            verify(repository, never()).delete(any(Medic.class));
        }
    }  

}
