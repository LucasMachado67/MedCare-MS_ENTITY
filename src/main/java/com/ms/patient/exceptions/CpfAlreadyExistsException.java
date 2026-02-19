package com.ms.patient.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando uma tentativa de criação ou atualização de um registro de Pessoa
 * utiliza um **CPF que já existe** no banco de dados, violando a regra de unicidade.
 *
 * <p>Esta exceção é mapeada para o status HTTP **409 Conflict** (Conflito)
 * através da anotação {@code @ResponseStatus(HttpStatus.CONFLICT)}.</p>
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CpfAlreadyExistsException extends RuntimeException{

    /**
     * Construtor que cria a exceção.
     *
     * <p>Normalmente, a string passada é usada como a mensagem de erro que
     * será exibida ao usuário (ou o valor do CPF que causou o conflito).</p>
     *
     * @param cpf O CPF que causou a violação de unicidade ou a mensagem de erro detalhada.
     */
    public CpfAlreadyExistsException(String message) {
        super(message);
    }
}