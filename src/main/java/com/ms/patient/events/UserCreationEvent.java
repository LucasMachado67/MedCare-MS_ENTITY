package com.ms.patient.events;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Evento assíncrono utilizado para notificar o Serviço de Autenticação (Auth Service)
 * sobre a criação de um novo registro de Pessoa.
 *
 * <p>O serviço de destino deve consumir este evento para criar as credenciais de 'login'
 * (usuário e papel/permissão) associadas à nova Pessoa.</p>
 *
 * @see com.ms.patient.producers.UserCreationProducer
 */
public class UserCreationEvent {

        /**
         * O identificador único da Pessoa recém-criada no sistema de Pacientes.
         * <p>Mapeado para "person_id" na payload JSON.</p>
         */
        @JsonProperty("person_id")
        private Long person_id;
        /**
         * O nome de usuário (username) que deve ser utilizado para a nova credencial de 'login'.
         * <pCorresponde ao Email da Pessoa.</p>
         */
        private String username;
        /**
         * O papel (role) ou nível de permissão assoc à credencial (ex: "PATIENT", "MEDIC").
         */
        private String role;

        private String tenantId;
        
        public Long getPerson_id() {
                return person_id;
        }
        public void setPerson_id(Long person_id) {
                this.person_id = person_id;
        }
        public String getUsername() {
                return username;
        }
        public void setUsername(String username) {
                this.username = username;
        }
        public String getRole() {
                return role;
        }
        public void setRole(String role) {
                this.role = role;
        }
        public String getTenantId(){return tenantId; }
        public void setTenantId(String tenantId){this.tenantId = tenantId; }

        
}
