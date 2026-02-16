package com.ms.patient.serviceTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.patient.dto.AssistantCreationDTO;
import com.ms.patient.dto.AssistantResponseDTO;
import com.ms.patient.enums.Habitation;
import com.ms.patient.exceptions.BusinessException;
import com.ms.patient.mappers.AddressMapper;
import com.ms.patient.mappers.AssistantMapper;
import com.ms.patient.models.Address;
import com.ms.patient.models.Assistant;
import com.ms.patient.producers.UserCreationProducer;
import com.ms.patient.repositories.AssistantRepository;
import com.ms.patient.service.AssistantService;
import com.ms.patient.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
public class AssistantServiceTest {

    @Mock
    private AssistantRepository repository;

    @InjectMocks
    private AssistantService service;

    @Mock
    private UserCreationProducer userProducer;

    @Mock
    private PersonService personService;

    @Mock
    private AssistantMapper mapper;

    @Mock
    private AddressMapper mapperAddress;

    AssistantCreationDTO assistantCreationDTO;
    AssistantResponseDTO assistantResponseDTO;
    Assistant assistant;

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

        assistant = new Assistant();
        assistantCreationDTO = new AssistantCreationDTO();


        assistant.setName("João Silva");
        assistant.setCpf("123.456.789-00");
        assistant.setGender("Masculino");
        assistant.setEmail("joao@email.com");
        assistant.setPhone("11999999999");
        assistant.setBirthDate(new Date());
        assistant.setAddress(address);

        assistant.setActive(true);
        assistant.setRegistrationNumber("123321");

        assistantCreationDTO.setName("João Silva");
        assistantCreationDTO.setCpf("123.456.789-00");
        assistantCreationDTO.setGender("Masculino");
        assistantCreationDTO.setEmail("joao@email.com");
        assistantCreationDTO.setPhone("11999999999");
        assistantCreationDTO.setBirthDate(new Date());
        assistantCreationDTO.setAddress(mapperAddress.toDtoCreation(address));

        assistantCreationDTO.setActive(true);
        assistantCreationDTO.setRegistrationNumber("123321");

    }

    @Nested
    class createAssistant{

        @Test
        void shouldCreateAssistant() throws JsonProcessingException {

            when(personService.validatePersonInfo(any())).thenReturn(true);

            when(mapper.toAssistant(any())).thenReturn(assistant);

            when(repository.save(any())).thenReturn(assistant);

            doNothing().when(userProducer).publishUserCreationToAssistantEvent(any());

            when(repository.save(assistant)).thenReturn(assistant);

            Assistant result = service.createAssistant(assistantCreationDTO);

            assertNotNull(result);
            assertEquals(assistant.getName(), result.getName());
            verify(repository).save(any(Assistant.class));
            assertEquals(assistant.getRegistrationNumber(), result.getRegistrationNumber());
        }


        @Test
        void shouldThrowWhenInvalidAssistantData(){

            when(personService.validatePersonInfo(any())).thenReturn(false);

            BusinessException exception =
                    assertThrows(BusinessException.class, () -> service.createAssistant(assistantCreationDTO));

            assertEquals("Invalid assistant data", exception.getMessage());
        }

    }

    @Nested
    class FindBy{

        @Test
        void shouldFindAssistantById(){
            when(repository.findById(anyLong())).thenReturn(Optional.of(assistant));

            Assistant result = service.findById(anyLong());

            assertNotNull(result);
            assertEquals(assistant.getName(), result.getName());
        }

        @Test
        void shouldThrowIfNoAssistantFound(){
            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            NoSuchElementException exception =
                    assertThrows(NoSuchElementException.class,() -> service.findById(anyLong()));

            assertEquals("ASSISTANT NOT FOUND", exception.getMessage());
        }

        @Test
        void shouldFindAllAssistants(){
            Assistant assistant2 = new Assistant();
            when(repository.findAll()).thenReturn(List.of(assistant,assistant2));

            List<Assistant> result = service.findAll();

            assertEquals(2, result.size());
        }
    }

    @Nested
    class UpdateAssistant{

        @Test
        void shouldUpdateAssistant(){

            long idForTest = 1L;
            assistant.setEmail("antigo@test.com");
            assistantCreationDTO.setEmail("novo@test.com");

            when(repository.findById(anyLong())).thenReturn(Optional.of(assistant));
            when(personService.validatePersonInfo(assistantCreationDTO)).thenReturn(true);

            doAnswer(invocation -> {
                AssistantCreationDTO dto = invocation.getArgument(0);
                Assistant p = invocation.getArgument(1);
                p.setEmail(dto.getEmail());
                return null;
            }).when(mapper).updateAssistantFromDto(any(AssistantCreationDTO.class), any(Assistant.class));

            when(repository.save(any(Assistant.class))).thenReturn(assistant);

            Assistant result = service.updateAssistant(assistantCreationDTO, idForTest);

            assertNotNull(result);
            verify(repository).save(any(Assistant.class));
            assertEquals("novo@test.com", result.getEmail());

        }

        @Test
        void shouldThrowIfAssistantNotFound(){

            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> {
                service.updateAssistant(assistantCreationDTO, 1L);
            });
        }

        @Test
        void shouldThrowIfDtoIsInvalid(){


            when(repository.findById(anyLong())).thenReturn(Optional.of(assistant));
            when(personService.validatePersonInfo(assistantCreationDTO)).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class, () -> {
                service.updateAssistant(assistantCreationDTO, 1L);
            });

            assertEquals("Invalid assistant data", exception.getMessage());
        }
    }

    @Nested
    class DeleteAssistant{

        @Test
        void shouldDeleteAssistantById(){

            when(repository.findById(1L)).thenReturn(Optional.of(assistant));

            doNothing().when(repository).deleteById(anyLong());

            service.deleteAssistant(1L);

            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        void shouldThrowIfAssistantNotFound(){

            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            NoSuchElementException exception =
                    assertThrows(NoSuchElementException.class, () -> service.deleteAssistant(99L));

            assertEquals("ASSISTANT NOT FOUND, nothing was deleted", exception.getMessage());
            verify(repository, never()).delete(any(Assistant.class));
        }
    }

}
