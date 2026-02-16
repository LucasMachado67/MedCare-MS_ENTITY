package com.ms.patient.dto;

import com.ms.patient.enums.PatientSituation;

import java.util.List;

/**
 * DTO (Data Transfer Object) utilizado para representar um Paciente nos retornos
 * de requisições GET, POST e PUT da API.
 *
 * <p>Inclui o identificador principal 'ID', dados herdados de Pessoa, o endereço
 * mapeado como {@link AddressResponseDTO}, e todos os campos específicos do Paciente.</p>
 *
*/
public class PatientResponseDTO extends PersonResponseDTO{


        //campos da classe patient
        private String healthPlan;
        private String description;
        private List<String> symptoms;
        private List<String> allergies;
        private PatientSituation patientSituation;

        public String getHealthPlan() {
                return healthPlan;
        }
        public void setHealthPlan(String healthPlan) {
                this.healthPlan = healthPlan;
        }
        public String getDescription() {
                return description;
        }
        public void setDescription(String description) {
                this.description = description;
        }
        public List<String> getSymptoms() {
                return symptoms;
        }
        public void setSymptoms(List<String> symptoms) {
                this.symptoms = symptoms;
        }
        public List<String> getAllergies() {
                return allergies;
        }
        public void setAllergies(List<String> allergies) {
                this.allergies = allergies;
        }
        public PatientSituation getPatientSituation() {
                return patientSituation;
        }
        public void setPatientSituation(PatientSituation patientSituation) {
                this.patientSituation = patientSituation;
        }

        
}