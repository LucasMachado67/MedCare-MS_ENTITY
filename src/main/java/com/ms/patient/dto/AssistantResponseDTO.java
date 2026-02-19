package com.ms.patient.dto;

import com.ms.patient.enums.AssistantStatus;

public class AssistantResponseDTO extends PersonResponseDTO{


    private String registrationNumber;
    private AssistantStatus status;

    public AssistantResponseDTO(){}

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public AssistantStatus getStatus() {
        return this.status;
    }

    public void setStatus(AssistantStatus status) {
        this.status = status;
    }
}
