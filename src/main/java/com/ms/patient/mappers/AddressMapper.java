package com.ms.patient.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.ComponentScan;

import com.ms.patient.dto.AddressDTO;
import com.ms.patient.models.Address;

@ComponentScan
@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true) 
    Address toEntityCreation(AddressDTO dto);
    AddressDTO toDtoCreation(Address entity);

    @Mapping(target = "id", ignore = true)
    void updateAddressFromDto(AddressDTO dto, @MappingTarget Address target);
}

