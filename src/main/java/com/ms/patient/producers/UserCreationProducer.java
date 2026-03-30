package com.ms.patient.producers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.patient.events.UserCreationEvent;
import com.ms.patient.models.Assistant;
import com.ms.patient.models.Medic;
import com.ms.patient.models.Patient;

import io.awspring.cloud.sqs.operations.SqsTemplate;

/**
 * Componente responsável por produzir e enviar eventos de criação de usuário
 * para o broker RabbitMQ de forma assíncrona.
 *
 * <p>Estes eventos são consumidos pelo Serviço de Autenticação (Auth Service)
 * para criar as credenciais de login para novos Médicos e Pacientes.</p>
 */
@Component
public class UserCreationProducer {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Construtor para injeção de dependência do sqsTemplate.
     *
     * @param sqsTemplate A instância do 'template' do Amazon SQS para envio de mensagens.
     */
    public UserCreationProducer(SqsTemplate sqsTemplate, ObjectMapper objectMapper) {
        this.sqsTemplate = sqsTemplate;
        this.objectMapper = objectMapper;
    }

    //É necessário ter três filas, pois cada usuário irá receber um tratamento diferente no service (role)
    @Value(value = "${medcare.aws.sqs.queue.entity.register}")
    private String entityQueue;

    /**
     * Constrói e publica um evento de criação de usuário para um novo {@link Medic}.
     *
     * <p>O evento é enviado para a fila definida pela {@code routingKeyMedic} com o papel "MEDIC".</p>
     *
     * @param medic A entidade Medic recém-criada, contendo 'ID' e e-mail necessários para o evento.
     */
    public void publishUserCreationToMedicEvent(Medic medic, String tenantId) throws JsonProcessingException {
        //Usando o mesmo ID para ambas as entidades (Medic = UserMedic)
        var event = new UserCreationEvent();
        event.setPerson_id(medic.getId());
        event.setUsername(medic.getEmail());
        event.setRole(("MEDIC"));
        event.setTenantId(tenantId);
        //Passando os dados como 'string'
        String json = objectMapper.writeValueAsString(event);

        //Enviando a mensagem(medicEventDto) para fila entityQueue
        sqsTemplate.send(to -> to
                .queue(entityQueue)
                .payload(json));
            System.out.println("Message sent!");
    }

    /**
     * Constrói e publica um evento de criação de usuário para um novo {@link Assistant}.
     *
     * <p>O evento é enviado para a fila definida pela {@code routingKeyAssistant} com o papel "Assistant".</p>
     *
     * @param assistant A entidade Assistant recém-criada, contendo 'ID' e Endereço eletrónico necessários para o evento.
     */
    public void publishUserCreationToAssistantEvent(Assistant assistant, String tenantId) throws JsonProcessingException{
        var event = new UserCreationEvent();
        event.setPerson_id(assistant.getId());
        event.setUsername(assistant.getEmail());
        event.setRole(("ASSISTANT"));
        event.setTenantId(tenantId);
        //Passando os dados como 'string'
        String json = objectMapper.writeValueAsString(event);

        //Enviando a mensagem(medicEventDto) para fila entityQueue
        sqsTemplate.send(to -> to
                .queue(entityQueue)
                .payload(json));
        System.out.println("Message sent!");
    }

    /**
     * Constrói e publica um evento de criação de usuário para um novo {@link Patient}.
     *
     * <p>O evento é enviado para a fila definida pela {@code routingKeyPatient} com o papel "USER".</p>
     *
     * @param patient A entidade Patient recém-criada, contendo 'ID' e 'e-mail' necessários para o evento.
     */
    public void publishUserCreationToPatientEvent(Patient patient) throws JsonProcessingException{
        //Usando o mesmo ID para ambas as entidades (Medic = UserMedic)
        var event = new UserCreationEvent();
        event.setPerson_id(patient.getId());
        event.setUsername(patient.getEmail());
        event.setRole(("USER"));
        //Passando os dados como 'string'
        String json = objectMapper.writeValueAsString(event);

        //Enviando a mensagem(medicEventDto) para fila entityQueue
        sqsTemplate.send(to -> to
                .queue(entityQueue)
                .payload(json));
        System.out.println("Message sent!");
    }
}
