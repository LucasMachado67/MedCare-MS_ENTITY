package com.ms.patient.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ms.patient.enums.AssistantStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "employees")
@PrimaryKeyJoinColumn(name = "person_id")
public class Assistant extends Person{

    private String registrationNumber;
    @NotNull
    @JsonProperty("active")
    private AssistantStatus status = AssistantStatus.ACTIVE;

    public Assistant(String name, Date birthDate, String cpf, String gender, String email, String phone, Address address,
            String registrationNumber) {
        super(name, birthDate, cpf, gender, email, phone, address);
        this.setRegistrationNumber(registrationNumber);
    }

    public Assistant() {
        super();
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    public AssistantStatus getStatus() {
        return status;
    }
    public void setStatus(AssistantStatus status) {
        this.status = status;
    }

    

    
}
