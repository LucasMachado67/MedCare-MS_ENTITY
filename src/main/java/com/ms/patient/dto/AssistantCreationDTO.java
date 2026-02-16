package com.ms.patient.dto;


public class AssistantCreationDTO extends PersonCreationDTO{

    private String registrationNumber;
    private Boolean active;

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
