package com.ms.patient.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.ComponentScan;

import com.ms.patient.dto.PersonCreationDTO;
import com.ms.patient.dto.PersonResponseDTO;
import com.ms.patient.models.Person;

@ComponentScan
@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface PersonMapper {
    
    //A primeira linha passa o telefone para apenas digitos e a segundo linha passa o cpf
    @Mapping(target = "phone", expression = "java(dto.getPhone().replaceAll(\"[^0-9]\", \"\"))")
    @Mapping(target = "cpf", expression = "java(dto.getCpf().replaceAll(\"[^0-9]\", \"\"))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    Person toEntityCreation(PersonCreationDTO dto);
    PersonCreationDTO toDtoCreation(Person person);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    Person toEntityResponse(PersonResponseDTO dto);
    PersonResponseDTO toDtoResponse(Person person);
    
    List<Person> toEntityResponse(List<PersonResponseDTO> dtos);
    List<PersonResponseDTO> toDtoResponse(List<Person> entities);
    
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updatePersonFromDto(PersonCreationDTO dto, @MappingTarget Person person);
}


