package com.ms.patient.serviceTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.Instant;
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

import com.ms.patient.dto.PersonCreationDTO;
import com.ms.patient.dto.PersonEmailSenderDto;
import com.ms.patient.enums.Habitation;
import com.ms.patient.exceptions.CpfAlreadyExistsException;
import com.ms.patient.exceptions.EmailAlreadyExistsException;
import com.ms.patient.mappers.PersonMapper;
import com.ms.patient.models.Address;
import com.ms.patient.models.Person;
import com.ms.patient.repositories.PersonRepository;
import com.ms.patient.service.PersonService;


@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository repository;

    @InjectMocks
    private PersonService service;

    @Mock
    private PersonMapper mapper;

    Person person;

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
        
        person = new Person();
        
        person.setId(1L);
        person.setEmail("teste@gmail.com");
        person.setCpf("11122233345");
        person.setGender("male");
        person.setName("Lucas Machado");
        person.setPhone("47984162849");
        person.setAddress(address);
        person.setBirthDate(Date.from(Instant.now()));
    }

    @Nested
    class ValidatePerson{

        @Test
        void ShouldReturnTrueInValidationOfCorrectDataPerson(){
            
            PersonCreationDTO dtoFake = new PersonCreationDTO();
            dtoFake.setCpf(person.getCpf());
            dtoFake.setEmail(person.getEmail());

            when(mapper.toDtoCreation(any())).thenReturn(dtoFake);
            when(repository.existsByEmail(any())).thenReturn(false);
            when(repository.existsByCpf(any())).thenReturn(false);

            PersonCreationDTO response = mapper.toDtoCreation(person); 
            boolean result = service.validatePersonInfo(response);

            assertTrue(result);

        }

        @Test
        void ShouldReturnFalseWhenEmailAlreadyExists(){
            
            PersonCreationDTO dtoFake = new PersonCreationDTO();
            dtoFake.setEmail(person.getEmail());
            dtoFake.setCpf(person.getCpf());
            when(repository.existsByEmail(any())).thenReturn(true);
            
            EmailAlreadyExistsException exception =
                assertThrows(EmailAlreadyExistsException.class, () -> service.validatePersonInfo(dtoFake));

            assertEquals("Email already registered", exception.getMessage());
        }

        @Test
        void ShouldReturnFalseWhenCpfAlreadyExists(){
            
            PersonCreationDTO dtoFake = new PersonCreationDTO();
            dtoFake.setEmail(person.getEmail());
            dtoFake.setCpf(person.getCpf());

            when(repository.existsByEmail(any())).thenReturn(false);
            when(repository.existsByCpf(any())).thenReturn(true);

            CpfAlreadyExistsException exception = 
                assertThrows(CpfAlreadyExistsException.class,() -> service.validatePersonInfo(dtoFake));

            assertEquals("Cpf already registered", exception.getMessage());

        }
    }

    @Nested
    class FindBy{

        @Test
        void shouldFindPersonById(){

            when(repository.findById(anyLong())).thenReturn(Optional.of(person));

            Person teste = service.findPersonById(1L);

            assertNotNull(teste);
            assertEquals("Lucas Machado", teste.getName());
        }

        @Test
        void shouldThrowIfFindPersonByIdDontReturn(){

            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            NoSuchElementException exception =
                assertThrows(NoSuchElementException.class,() -> service.findPersonById(1L));

            assertEquals("NOT FOUND", exception.getMessage());
        }

        @Test
        void shouldFindPersonByIdToSendEmail(){

            when(repository.findById(anyLong())).thenReturn(Optional.of(person));

            PersonEmailSenderDto dto = new PersonEmailSenderDto();
            dto.setId(1L);
            dto.setEmail(person.getEmail());
            dto.setNome(person.getName());

            PersonEmailSenderDto response = service.findPersonByIdToSendEmail(person.getId());

            assertEquals(person.getEmail(), response.getEmail());
            assertEquals(person.getName(), response.getNome());
        }

        @Test
        void shouldThrowWhenFindPersonByIdToSendEmailIsNotFound(){

            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            NoSuchElementException exception =
                assertThrows(NoSuchElementException.class,() -> service.findPersonByIdToSendEmail(1L));

            assertEquals("NOT FOUND",exception.getMessage());
        }

        @Test
        void shouldFindAll(){

            Person person2 = new Person();
            when(repository.findAll()).thenReturn(List.of(person, person2));

            List<Person> lista = service.findAll();

            assertEquals(2, lista.size());
        }

        @Test
        void shouldFindPersonByEmail(){

            when(repository.findPersonByEmail(any())).thenReturn(Optional.of(person));

            Person response = service.findPersonByEmail(person.getEmail());

            assertEquals(response.getName(), person.getName());
        }

        @Test
        void shouldThrowIfFindPersonByEmailReturnNothing(){

            when(repository.findPersonByEmail(any())).thenReturn(Optional.empty());

            NoSuchElementException exception = 
                assertThrows(NoSuchElementException.class,() -> service.findPersonByEmail(person.getEmail()));

            assertEquals("Email not found", exception.getMessage());
        }
    }
}
