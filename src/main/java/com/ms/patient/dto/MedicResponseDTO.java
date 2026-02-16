package com.ms.patient.dto;


/**
 * DTO (Data Transfer Object) utilizado para representar um Médico nos retornos
 * de requisições GET; POST e PUT da API.
 *
 * <p>Inclui o identificador principal (ID), dados herdados de Pessoa, o endereço
 * mapeado como {@link AddressResponseDTO}, e os campos específicos do Médico.</p>
 *
 */
public class MedicResponseDTO extends PersonResponseDTO {

        // --- CAMPOS ESPECÍFICOS DE MEDIC ---
        private String crm;
        private String medicalSpeciality;

        public String getCrm() {
                return crm;
        }
        public void setCrm(String crm) {
                this.crm = crm;
        }
        public String getMedicalSpeciality() {
                return medicalSpeciality;
        }
        public void setMedicalSpeciality(String medicalSpeciality) {
                this.medicalSpeciality = medicalSpeciality;
        }
}