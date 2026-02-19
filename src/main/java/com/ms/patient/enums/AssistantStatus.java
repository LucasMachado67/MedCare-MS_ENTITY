package com.ms.patient.enums;
/**
 * Representa os tipos de status de um Assistente.
 * <p>Usado para categorizar a natureza do stauts do indivíduo.</p>
 */
public enum AssistantStatus {

    //Situação normal
    ACTIVE,
    //período de férias
    VACATION,
    //para suspensão disciplinar/investigação
    SUSPENDED,
    //Afastamento autorizado por licença ou para atestados
    ON_LEAVE
}
