package com.ms.patient.dto;

public class StatusResponse {
    private String status;

    public StatusResponse(String status){
        this.setStatus(status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
