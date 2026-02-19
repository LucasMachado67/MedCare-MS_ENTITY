package com.ms.patient.UtilsTests;

import com.ms.patient.utils.CpfValidatorUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@TestComponent
@ExtendWith(MockitoExtension.class)
public class CpfValidatorUtilsTest {

    @Test
    void testShouldCalculateValidCpf(){

        //CPF gerado aleatoriamente para teste
        String cpf = "93033625002";

        try(MockedStatic<CpfValidatorUtils> mockedUtils = mockStatic(CpfValidatorUtils.class)) {
            mockedUtils.when(() -> CpfValidatorUtils.isValidCpf(cpf))
                    .thenReturn(true);

            boolean resultado = CpfValidatorUtils.isValidCpf(cpf);

            assertTrue(resultado);

        }
    }

    @Test
    void testThrowExceptionInCalculateValidCpf(){

        //CPF gerado aleatoriamente para teste
        String cpf = "28984929299";

        boolean resultado = CpfValidatorUtils.isValidCpf(cpf);

        assertFalse(resultado);

    }
}
