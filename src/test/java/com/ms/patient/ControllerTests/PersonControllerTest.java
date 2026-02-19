package com.ms.patient.ControllerTests;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ms.patient.config.ResourceServerConfig;
import com.ms.patient.controller.PersonController;
import com.ms.patient.dto.AddressDTO;
import com.ms.patient.dto.PersonCreationDTO;
import com.ms.patient.dto.PersonEmailSenderDto;
import com.ms.patient.dto.PersonResponseDTO;
import com.ms.patient.enums.Habitation;
import com.ms.patient.exceptions.GlobalExceptionHandler;
import com.ms.patient.mappers.AddressMapper;
import com.ms.patient.mappers.PersonMapper;
import com.ms.patient.models.Address;
import com.ms.patient.models.Person;
import com.ms.patient.service.PersonService;

import jakarta.persistence.EntityNotFoundException;

@Import({GlobalExceptionHandler.class, ResourceServerConfig.class})
@WebMvcTest(controllers = PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService service;

    @MockitoBean
    private PersonMapper mapper;

    @MockitoBean
    private AddressMapper addressMapper;

    Person person;
    Person person2;
    PersonCreationDTO personCreationDTO;
    PersonResponseDTO personResponseDTO;

    @BeforeEach
    void setup(){

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("Rua das Flores");
        addressDTO.setNumber(123);
        addressDTO.setNeighborhood("Centro");
        addressDTO.setCity("São Paulo");
        addressDTO.setState("SP");
        addressDTO.setZipCode("01234-567");
        addressDTO.setComplement("Apto 101");
        addressDTO.setHabitation(Habitation.APARTMENT.toString());

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
        personCreationDTO = new PersonCreationDTO();
        
        
        person.setName("João Silva");
        person.setCpf("123.456.789-00");
        person.setGender("Masculino");
        person.setEmail("joao@email.com");
        person.setPhone("11999999999");
        person.setBirthDate(new Date()); 
        person.setAddress(address);

        personCreationDTO.setName("João Silva");
        personCreationDTO.setCpf("123.456.789-00");
        personCreationDTO.setGender("Masculino");
        personCreationDTO.setEmail("joao@email.com");
        personCreationDTO.setPhone("11999999999");
        personCreationDTO.setBirthDate(new Date()); 
        personCreationDTO.setAddress(addressDTO);

        personResponseDTO = new PersonResponseDTO();
        personResponseDTO.setName("João Silva");
        personResponseDTO.setCpf("123.456.789-00");
        personResponseDTO.setGender("Masculino");
        personResponseDTO.setEmail("joao@email.com");
        personResponseDTO.setPhone("11999999999");
        personResponseDTO.setBirthDate(new Date()); 
        personResponseDTO.setAddress(addressDTO); 
    }
    
    @Nested
    class FindBy{
        
        @Test
        void shouldFindPersonById() throws Exception{

            when(service.findPersonById(anyLong())).thenReturn(person);
            mockMvc.perform(get("/person/1")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldThrow401WhenFindPersonByIdWhileNotAuthenticated() throws Exception{

            mockMvc.perform(get("/person/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrowNotFoundInFindPersonById() throws Exception{

            when(service.findPersonById(anyLong())).thenThrow(new EntityNotFoundException("NOT FOUND"));
            mockMvc.perform(get("/person/1")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldFindPersonToSendEmail() throws Exception{
            PersonEmailSenderDto dto = new PersonEmailSenderDto();
            when(service.findPersonByIdToSendEmail(anyLong())).thenReturn(dto);
            mockMvc.perform(get("/person/email/1")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldThrow401InFindPersonToSendEmailIfNotAuthenticated() throws Exception{
            PersonEmailSenderDto dto = new PersonEmailSenderDto();
            when(service.findPersonByIdToSendEmail(anyLong())).thenReturn(dto);
            mockMvc.perform(get("/person/email/1"))
                    .andExpect(status().isUnauthorized());
        }
        
        @Test
        void shouldThrowInFindPersonToSendEmailIfNotFound() throws Exception{
            when(service.findPersonByIdToSendEmail(anyLong())).thenThrow(new EntityNotFoundException("NOT FOUND"));

            mockMvc.perform(get("/person/email/1")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldFindAll() throws Exception{

            Person person2 = new Person();
            when(service.findAll()).thenReturn(List.of(person, person2));

            List<PersonResponseDTO> listaDtos = List.of(new PersonResponseDTO(), new PersonResponseDTO());
            when(mapper.toDtoResponse(anyList())).thenReturn(listaDtos);

            mockMvc.perform(get("/person/all")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void shouldThrow403FindAllIfForbiddenByRole() throws Exception{

            mockMvc.perform(get("/person/all")
                            .with(jwt().authorities(() -> "ROLE_USER"))
                            .with(csrf()))
                    .andExpect(status().isForbidden());

        }

        @Test
        void shouldThrow401FindAllIfUnauthorized() throws Exception{

            mockMvc.perform(get("/person/all"))
                    .andExpect(status().isUnauthorized());

        }
    }
}
