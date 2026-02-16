package com.ms.patient.dto;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO (Data Transfer Object) utilizado para enviar dados de criação ou atualização
 * de uma Pessoa na API.
 *
 * <p>Contém os campos básicos de identificação e contato, e um objeto de endereço aninhado.</p>
 */
public class PersonCreationDTO {

    @NotBlank
    private String name;
    private Date birthDate;
    @NotBlank
    private String cpf;
    @NotBlank
    private String gender;
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O formato do e-mail é inválido.")
    private String email;
    @NotBlank(message = "O número do telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}[\\s-]?\\d{4}$", 
             message = "O telefone não está no formato correto (Ex: (11) 99999-9999 ou 11999999999).")
    private String phone;

    private AddressDTO address;

    public PersonCreationDTO(){}

    public PersonCreationDTO(String name, Date birthDate, String cpf, String gender, String email, String phone, AddressDTO address){
        setName(name);
        setBirthDate(birthDate);
        setCpf(cpf);
        setGender(gender);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    
}
