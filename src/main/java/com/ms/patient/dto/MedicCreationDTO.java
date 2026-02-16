package com.ms.patient.dto;

/**
 * DTO (Data Transfer Object) utilizado para a criação de um novo registro de Médico.
 *
 * <p>Este DTO combina dados herdados de Pessoa (Person); um objeto de endereço aninhado;
 * e campos específicos da entidade Médico, aplicando validações rigorosas.</p>
 *
 * */
public class MedicCreationDTO extends PersonCreationDTO{


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