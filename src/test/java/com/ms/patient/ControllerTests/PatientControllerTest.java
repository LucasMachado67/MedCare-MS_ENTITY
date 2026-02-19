package com.ms.patient.ControllerTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.patient.config.ResourceServerConfig;
import com.ms.patient.controller.PatientController;
import com.ms.patient.dto.AddressDTO;
import com.ms.patient.dto.PatientCreationDTO;
import com.ms.patient.dto.PatientResponseDTO;
import com.ms.patient.enums.Habitation;
import com.ms.patient.enums.PatientSituation;
import com.ms.patient.exceptions.BusinessException;
import com.ms.patient.exceptions.GlobalExceptionHandler;
import com.ms.patient.mappers.AddressMapper;
import com.ms.patient.mappers.PatientMapper;
import com.ms.patient.models.Address;
import com.ms.patient.models.Patient;
import com.ms.patient.service.PatientService;

import jakarta.persistence.EntityNotFoundException;

@Import({GlobalExceptionHandler.class, ResourceServerConfig.class})
@WebMvcTest(controllers = PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService service;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientMapper mapper;

    @MockitoBean
    private AddressMapper addressMapper;

    Patient patient;
    Patient patient2;
    PatientCreationDTO patientCreationDTO;
    PatientResponseDTO patientResponseDTO;

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
        patientCreationDTO.setAddress(addressDTO); 

        patientCreationDTO.setHealthPlan("Plano VIP");
        patientCreationDTO.setDescription("Paciente com histórico de hipertensão");
        patientCreationDTO.setPatientSituation(PatientSituation.STABLE);
        
        patientCreationDTO.setSymptoms(new ArrayList<>(List.of("Dor de cabeça", "Febre")));
        patientCreationDTO.setAllergies(new ArrayList<>(List.of("Dipirona")));

        patientResponseDTO = new PatientResponseDTO();
        patientResponseDTO.setName("João Silva");
        patientResponseDTO.setCpf("123.456.789-00");
        patientResponseDTO.setGender("Masculino");
        patientResponseDTO.setEmail("joao@email.com");
        patientResponseDTO.setPhone("11999999999");
        patientResponseDTO.setBirthDate(new Date()); 
        patientResponseDTO.setAddress(addressDTO); 

        patientResponseDTO.setHealthPlan("Plano VIP");
        patientResponseDTO.setDescription("Paciente com histórico de hipertensão");
        patientResponseDTO.setPatientSituation(PatientSituation.STABLE);
        
        patientResponseDTO.setSymptoms(new ArrayList<>(List.of("Dor de cabeça", "Febre")));
        patientResponseDTO.setAllergies(new ArrayList<>(List.of("Dipirona")));
    }

    @Nested
    class CreatePatient{

        @Test
        void shouldCreatePatient() throws Exception{

            when(service.createPatient(any())).thenReturn(patient);

            when(mapper.toPatientResponseDTO(any()))
            .thenReturn(patientResponseDTO);

            mockMvc.perform(post("/patient/create")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patientCreationDTO)))
                    .andExpect(status().isCreated());
        }

        @Test
        void shouldReturnBadRequestWhenDtoIsInvalid() throws Exception {

            PatientCreationDTO dto = new PatientCreationDTO();

            mockMvc.perform(post("/patient/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldThrowIfValidatePersonReturnFalse() throws Exception {

            when(service.createPatient(any())).thenThrow(
                new BusinessException("Invalid patient data")
            );
            
            mockMvc.perform(post("/patient/create")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patientCreationDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class FindBy{

        @Test
        void shouldFindPatientById() throws Exception{

            when(service.findById(anyLong())).thenReturn(patient);

            mockMvc.perform(get("/patient/1")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldThrowUnauthorizedIfNotAuthenticated() throws Exception{

            mockMvc.perform(get("/patient/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrowIfPatientNotFound() throws Exception{

            when(service.findById(anyLong()))
                    .thenThrow(new EntityNotFoundException("NOT FOUND"));

            mockMvc.perform(get("/patient/1")
                            .with(jwt()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldFindAllPatients() throws Exception{
            Patient patient2 = new Patient();
            PatientResponseDTO dto2 = new PatientResponseDTO();
            when(service.findAll()).thenReturn(List.of(patient, patient2));

            when(mapper.toDtoResponse(any())).thenReturn(List.of(patientResponseDTO,dto2));

            mockMvc.perform(get("/patient/all")
                            .with(jwt().authorities(() -> "ROLE_ADMIN"))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void shouldThrow401InFindAllPatientsIfNotAuthenticated() throws Exception{

            mockMvc.perform(get("/patient/all"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrow403IfRoleIsNotPermitedInFindAll() throws Exception{

            mockMvc.perform(get("/patient/all")
                            .with(jwt().authorities(() -> "ROLE_USER"))
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class updatePatient{

        @Test
        void shouldUpdatePatient() throws JsonProcessingException, Exception{
            
            long id = 1L;

            when(service.updatePatient(any(PatientCreationDTO.class), eq(id))).thenReturn(patient);

            when(mapper.toPatientResponseDTO(any(Patient.class))).thenReturn(patientResponseDTO);

            mockMvc.perform(put("/patient/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patientCreationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(patientResponseDTO.getName()))
                .andExpect(jsonPath("$.cpf").value(patientResponseDTO.getCpf()));
        }

        @Test
        void shouldThrow401IfNotAuthenticated() throws JsonProcessingException, Exception{
            long id = 1L;
            mockMvc.perform(put("/patient/{id}", id) 
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patientCreationDTO)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrow403IfUserIsForbidden() throws JsonProcessingException, Exception{
            long id = 1L;
            mockMvc.perform(put("/patient/{id}", id)
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MEDIC")))
                    .with(csrf())   
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patientCreationDTO)))
                .andExpect(status().isForbidden());
        }

        @Test
        void shouldThrowInUpdatePatientIfNotFound() throws JsonProcessingException, Exception{
            
            long id = 1L;

            when(service.updatePatient(any(PatientCreationDTO.class), eq(id)))
                        .thenThrow(new EntityNotFoundException("Patient not found"));

            mockMvc.perform(put("/patient/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patientCreationDTO)))
                .andExpect(status().isNotFound());
        }

        @Test
        void shouldThrowInUpdatePatientIfInvalidPatientData() throws JsonProcessingException, Exception{
            
            long id = 1L;

            when(service.updatePatient(any(PatientCreationDTO.class), eq(id)))
                        .thenThrow(new BusinessException("Invalid patient data"));

            mockMvc.perform(put("/patient/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patientCreationDTO)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class deletePatient{

        @Test
        void shouldDeletePatient() throws JsonProcessingException, Exception{

            long id = 1L;
            doNothing().when(service).deletePatient(anyLong());

            mockMvc.perform(delete("/patient/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patientCreationDTO)))
                .andExpect(status().isNoContent());
        }

        @Test
        void shouldThrow401IfNotAuthenticated() throws JsonProcessingException, Exception{

            long id = 1L;

            mockMvc.perform(delete("/patient/{id}", id) 
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patientCreationDTO)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldThrow403IfForbidden() throws JsonProcessingException, Exception{

            long id = 1L;

            mockMvc.perform(delete("/patient/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MEDIC")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patientCreationDTO)))
                .andExpect(status().isForbidden());
        }

        @Test
        void shouldThrowIfPatientNotFound() throws JsonProcessingException, Exception{

            long id = 1L;
            doThrow(new EntityNotFoundException("PATIENT NOT FOUND, nothin gwas deleted"))
                    .when(service).deletePatient(id);

            mockMvc.perform(delete("/patient/{id}", id) 
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MEDIC")))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(patientCreationDTO)))
                .andExpect(status().isForbidden());
        }
    }
}
