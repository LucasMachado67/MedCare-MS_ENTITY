package com.ms.patient.ControllerTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.patient.config.ResourceServerConfig;
import com.ms.patient.controller.AssistantController;
import com.ms.patient.dto.AddressDTO;
import com.ms.patient.dto.AssistantCreationDTO;
import com.ms.patient.dto.AssistantResponseDTO;
import com.ms.patient.enums.AssistantStatus;
import com.ms.patient.enums.Habitation;
import com.ms.patient.exceptions.BusinessException;
import com.ms.patient.exceptions.GlobalExceptionHandler;
import com.ms.patient.mappers.AddressMapper;
import com.ms.patient.mappers.AssistantMapper;
import com.ms.patient.models.Address;
import com.ms.patient.models.Assistant;
import com.ms.patient.service.AssistantService;

import jakarta.persistence.EntityNotFoundException;

@Import({GlobalExceptionHandler.class, ResourceServerConfig.class})
@WebMvcTest(controllers = AssistantController.class)
public class AssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssistantService service;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AssistantMapper mapper;

    @MockitoBean
    private AddressMapper addressMapper;

    Assistant assistant;
    Assistant assistant2;
    AssistantCreationDTO assistantCreationDTO;
    AssistantResponseDTO assistantResponseDTO;

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

        assistant = new Assistant();
        assistantCreationDTO = new AssistantCreationDTO();
        
        
        assistant.setName("João Silva");
        assistant.setCpf("123.456.789-00");
        assistant.setGender("Masculino");
        assistant.setEmail("joao@email.com");
        assistant.setPhone("11999999999");
        assistant.setBirthDate(new Date()); 
        assistant.setAddress(address);
        assistant.setStatus(AssistantStatus.ACTIVE);
        assistant.setRegistrationNumber("123321123");

        assistantCreationDTO.setName("João Silva");
        assistantCreationDTO.setCpf("123.456.789-00");
        assistantCreationDTO.setGender("Masculino");
        assistantCreationDTO.setEmail("joao@email.com");
        assistantCreationDTO.setPhone("11999999999");
        assistantCreationDTO.setBirthDate(new Date()); 
        assistantCreationDTO.setAddress(addressDTO);
        assistant.setStatus(AssistantStatus.ACTIVE);
        assistant.setRegistrationNumber("123321123");

        assistantResponseDTO = new AssistantResponseDTO();
        assistantResponseDTO.setName("João Silva");
        assistantResponseDTO.setCpf("123.456.789-00");
        assistantResponseDTO.setGender("Masculino");
        assistantResponseDTO.setEmail("joao@email.com");
        assistantResponseDTO.setPhone("11999999999");
        assistantResponseDTO.setBirthDate(new Date()); 
        assistantResponseDTO.setAddress(addressDTO); 
        assistant.setStatus(AssistantStatus.ACTIVE);
        assistant.setRegistrationNumber("123321123");   
    }

    @Nested
    class CreateAssistant{

        @Test
        void shouldCreateAssistant() throws Exception{

            when(service.createAssistant(any())).thenReturn(assistant);

            when(mapper.toAssistantResponseDTO(any()))
            .thenReturn(assistantResponseDTO);
            mockMvc.perform(post("/assistant/create")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(assistantCreationDTO)))
                    .andExpect(status().isCreated());
        }

        @Test
        void shouldReturnBadRequestWhenDtoIsInvalid() throws Exception {

            AssistantCreationDTO dto = new AssistantCreationDTO();

            mockMvc.perform(post("/assistant/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldThrowIfValidatePersonReturnFalse() throws Exception {

            when(service.createAssistant(any())).thenThrow(
                new BusinessException("Invalid assistant data")
            );
            
            mockMvc.perform(post("/assistant/create")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(assistantCreationDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class FindBy{

        @Test
        void shouldFindAssistantById() throws Exception{

            when(service.findById(anyLong())).thenReturn(assistant);

            mockMvc.perform(get("/assistant/1")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldThrowUnauthorizedIfNotAuthenticated() throws Exception{

            mockMvc.perform(get("/assistant/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrowIfAssistantNotFound() throws Exception{

            when(service.findById(anyLong()))
                    .thenThrow(new EntityNotFoundException("ASSISTANT NOT FOUND"));

            mockMvc.perform(get("/assistant/1")
                            .with(jwt().authorities(() -> "ROLE_ADMIN")))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldThrow403IfForbidden() throws Exception{

            mockMvc.perform(get("/assistant/1")
                            .with(jwt().authorities(() -> "ROLE_USER")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void shouldFindAllAssistants() throws Exception{
            Assistant assistant2 = new Assistant();
            AssistantResponseDTO dto2 = new AssistantResponseDTO();
            when(service.findAll()).thenReturn(List.of(assistant, assistant2));

            when(mapper.toDtoResponse(any())).thenReturn(List.of(assistantResponseDTO,dto2));

            mockMvc.perform(get("/assistant/all")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void shouldThrow401InFindAllAssistantsIfNotAuthenticated() throws Exception{

            mockMvc.perform(get("/assistant/all"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrow403InFindAllAssistantsIfForbidden() throws Exception{

            mockMvc.perform(get("/assistant/all")
                            .with(jwt().authorities(() -> "ROLE_USER")))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class updateAssistant{

        @Test
        void shouldUpdateAssistant() throws JsonProcessingException, Exception{
            
            long id = 1L;

            when(service.updateAssistant(any(), eq(id))).thenReturn(assistant);

            when(mapper.toAssistantResponseDTO(any())).thenReturn(assistantResponseDTO);

            mockMvc.perform(put("/assistant/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(assistantCreationDTO)))
                .andExpect(status().isCreated());
        }

        @Test
        void shouldThrow401IfNotAuthenticated() throws JsonProcessingException, Exception{
            long id = 1L;
            mockMvc.perform(put("/assistant/{id}", id) 
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(assistantCreationDTO)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrow403IfUserIsForbidden() throws JsonProcessingException, Exception{
            long id = 1L;
            mockMvc.perform(put("/assistant/{id}", id)
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                    .with(csrf())   
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(assistantCreationDTO)))
                .andExpect(status().isForbidden());
        }

        @Test
        void shouldThrowInUpdateAssistantIfNotFound() throws JsonProcessingException, Exception{
            
            long id = 1L;

            when(service.updateAssistant(any(AssistantCreationDTO.class), eq(id)))
                        .thenThrow(new EntityNotFoundException("Assistant not found"));

            mockMvc.perform(put("/assistant/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(assistantCreationDTO)))
                .andExpect(status().isNotFound());
        }

        @Test
        void shouldThrowInUpdateAssistantIfInvalidAssistantData() throws JsonProcessingException, Exception{
            
            long id = 1L;

            when(service.updateAssistant(any(AssistantCreationDTO.class), eq(id)))
                        .thenThrow(new BusinessException("Invalid assistant data"));

            mockMvc.perform(put("/assistant/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(assistantCreationDTO)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class deleteAssistant{

        @Test
        void shouldDeleteAssistant() throws JsonProcessingException, Exception{

            long id = 1L;
            doNothing().when(service).deleteAssistant(anyLong());

            mockMvc.perform(delete("/assistant/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf()))
                .andExpect(status().isNoContent());
        }

        @Test
        void shouldThrow401IfNotAuthenticated() throws JsonProcessingException, Exception{

            long id = 1L;

            mockMvc.perform(delete("/assistant/{id}", id) 
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(assistantCreationDTO)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrow403IfForbidden() throws JsonProcessingException, Exception{

            long id = 1L;

            mockMvc.perform(delete("/assistant/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MEDIC")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(assistantCreationDTO)))
                .andExpect(status().isForbidden());
        }

        @Test
        void shouldThrowIfAssistantNotFound() throws JsonProcessingException, Exception{

            long id = 1L;
            doThrow(new EntityNotFoundException("ASSISTANT NOT FOUND, nothin gwas deleted"))
                    .when(service).deleteAssistant(id);

            mockMvc.perform(delete("/assistant/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(assistantCreationDTO)))
                .andExpect(status().isNotFound());
        }
    }
}
