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
import com.ms.patient.controller.MedicController;
import com.ms.patient.dto.AddressDTO;
import com.ms.patient.dto.MedicCreationDTO;
import com.ms.patient.dto.MedicResponseDTO;
import com.ms.patient.enums.Habitation;
import com.ms.patient.exceptions.BusinessException;
import com.ms.patient.exceptions.GlobalExceptionHandler;
import com.ms.patient.mappers.AddressMapper;
import com.ms.patient.mappers.MedicMapper;
import com.ms.patient.models.Address;
import com.ms.patient.models.Medic;
import com.ms.patient.service.MedicService;

import jakarta.persistence.EntityNotFoundException;

@Import({GlobalExceptionHandler.class, ResourceServerConfig.class})
@WebMvcTest(controllers = MedicController.class)
public class MedicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicService service;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MedicMapper mapper;

    @MockitoBean
    private AddressMapper addressMapper;

    Medic medic;
    Medic medic2;
    MedicCreationDTO medicCreationDTO;
    MedicResponseDTO medicResponseDTO;

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

        medic = new Medic();
        medicCreationDTO = new MedicCreationDTO();
        
        
        medic.setName("João Silva");
        medic.setCpf("123.456.789-00");
        medic.setGender("Masculino");
        medic.setEmail("joao@email.com");
        medic.setPhone("11999999999");
        medic.setBirthDate(new Date()); 
        medic.setAddress(address); 

        medic.setCrm("SP 123321");
        medic.setMedicalSpeciality("Ortopedista");
        
        medicCreationDTO.setName("João Silva");
        medicCreationDTO.setCpf("123.456.789-00");
        medicCreationDTO.setGender("Masculino");
        medicCreationDTO.setEmail("joao@email.com");
        medicCreationDTO.setPhone("11999999999");
        medicCreationDTO.setBirthDate(new Date()); 
        medicCreationDTO.setAddress(addressDTO); 

        medicCreationDTO.setCrm("SP 123321");
        medicCreationDTO.setMedicalSpeciality("Ortopedista");

        medicResponseDTO = new MedicResponseDTO();
        medicResponseDTO.setName("João Silva");
        medicResponseDTO.setCpf("123.456.789-00");
        medicResponseDTO.setGender("Masculino");
        medicResponseDTO.setEmail("joao@email.com");
        medicResponseDTO.setPhone("11999999999");
        medicResponseDTO.setBirthDate(new Date()); 
        medicResponseDTO.setAddress(addressDTO); 

        medicResponseDTO.setCrm("SP 123321");
        medicResponseDTO.setMedicalSpeciality("Ortopedista");
    }

    @Nested
    class CreateMedic{

        @Test
        void shouldCreateMedic() throws Exception{

            when(service.createMedic(any())).thenReturn(medic);

            when(mapper.toMedicResponseDTO(any()))
            .thenReturn(medicResponseDTO);

            mockMvc.perform(post("/medic/create")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(medicCreationDTO)))
                    .andExpect(status().isCreated());
        }

        @Test
        void shouldReturnBadRequestWhenDtoIsInvalid() throws Exception {

            MedicCreationDTO dto = new MedicCreationDTO();

            mockMvc.perform(post("/medic/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldThrowIfValidatePersonReturnFalse() throws Exception {

            when(service.createMedic(any())).thenThrow(
                new BusinessException("Invalid medic data")
            );
            
            mockMvc.perform(post("/medic/create")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(medicCreationDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class FindBy{

        @Test
        void shouldFindMedicById() throws Exception{

            when(service.findById(anyLong())).thenReturn(medic);

            mockMvc.perform(get("/medic/1")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldThrowUnauthorizedIfNotAuthenticated() throws Exception{

            mockMvc.perform(get("/medic/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrowIfMedicNotFound() throws Exception{

            when(service.findById(anyLong()))
                    .thenThrow(new EntityNotFoundException("NOT FOUND"));

            mockMvc.perform(get("/medic/1")
                            .with(jwt()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldFindAllMedics() throws Exception{
            Medic medic2 = new Medic();
            MedicResponseDTO dto2 = new MedicResponseDTO();
            when(service.findAll()).thenReturn(List.of(medic, medic2));

            when(mapper.toDtoResponse(any())).thenReturn(List.of(medicResponseDTO,dto2));

            mockMvc.perform(get("/medic/all")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void shouldThrow401InFindAllMedicsIfNotAuthenticated() throws Exception{

            mockMvc.perform(get("/medic/all"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class updateMedic{

        @Test
        void shouldUpdateMedic() throws JsonProcessingException, Exception{
            
            long id = 1L;

            when(service.updateMedic(any(), eq(id))).thenReturn(medic);

            when(mapper.toMedicResponseDTO(any())).thenReturn(medicResponseDTO);

            mockMvc.perform(put("/medic/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicCreationDTO)))
                .andExpect(status().isCreated());
        }

        @Test
        void shouldThrow401IfNotAuthenticated() throws JsonProcessingException, Exception{
            long id = 1L;
            mockMvc.perform(put("/medic/{id}", id) 
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicCreationDTO)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrow403IfUserIsForbidden() throws JsonProcessingException, Exception{
            long id = 1L;
            mockMvc.perform(put("/medic/{id}", id)
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                    .with(csrf())   
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicCreationDTO)))
                .andExpect(status().isForbidden());
        }

        @Test
        void shouldThrowInUpdateMedicIfNotFound() throws JsonProcessingException, Exception{
            
            long id = 1L;

            when(service.updateMedic(any(MedicCreationDTO.class), eq(id)))
                        .thenThrow(new EntityNotFoundException("Medic not found"));

            mockMvc.perform(put("/medic/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicCreationDTO)))
                .andExpect(status().isNotFound());
        }

        @Test
        void shouldThrowInUpdateMedicIfInvalidMedicData() throws JsonProcessingException, Exception{
            
            long id = 1L;

            when(service.updateMedic(any(MedicCreationDTO.class), eq(id)))
                        .thenThrow(new BusinessException("Invalid medic data"));

            mockMvc.perform(put("/medic/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicCreationDTO)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class deleteMedic{

        @Test
        void shouldDeleteMedic() throws JsonProcessingException, Exception{

            long id = 1L;
            doNothing().when(service).deleteMedic(anyLong());

            mockMvc.perform(delete("/medic/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf()))
                .andExpect(status().isNoContent());
        }

        @Test
        void shouldThrow401IfNotAuthenticated() throws JsonProcessingException, Exception{

            long id = 1L;

            mockMvc.perform(delete("/medic/{id}", id) 
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicCreationDTO)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrow403IfForbidden() throws JsonProcessingException, Exception{

            long id = 1L;

            mockMvc.perform(delete("/medic/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MEDIC")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicCreationDTO)))
                .andExpect(status().isForbidden());
        }

        @Test
        void shouldThrowIfMedicNotFound() throws JsonProcessingException, Exception{

            long id = 1L;
            doThrow(new EntityNotFoundException("MEDIC NOT FOUND, nothin gwas deleted"))
                    .when(service).deleteMedic(id);

            mockMvc.perform(delete("/medic/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(medicCreationDTO)))
                .andExpect(status().isNotFound());
        }
    }

}
