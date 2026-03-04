package com.ms.patient.tenant;


/**
 * Classe utilitária responsável por armazenar e gerenciar o tenant atual
 * no contexto da thread em execução.
 *
 * <p>Utiliza {@link ThreadLocal} para manter o identificador do tenant
 * isolado por requisição, garantindo que cada thread tenha seu próprio
 * contexto independente.</p>
 *
 * <p>Geralmente, o tenant é definido em um filtro (por exemplo, a partir
 * de um header HTTP ou do JWT) no início da requisição e removido ao final,
 * evitando vazamento de contexto entre requisições.</p>
 *
 * <p><b>Fluxo comum de uso:</b></p>
 * <ol>
 *   <li>Extrair o tenant do JWT ou header.</li>
 *   <li>Chamar {@code setCurrentTenant()} no início da requisição.</li>
 *   <li>Utilizar {@code getCurrentTenant()} durante o processamento.</li>
 *   <li>Chamar {@code clear()} ao final da requisição (ex: em um filter).</li>
 * </ol>
 *
 * <p><b>Atenção:</b> Sempre limpar o contexto ao final da execução para evitar
 * vazamento de dados entre usuários em ambientes com thread pool.</p>
 */
public class TenantContext {

    /**
     * Armazena o identificador do tenant por thread.
     */
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * Define o tenant atual no contexto da thread.
     *
     * @param tenantId identificador único do tenant
     */
    public static void setCurrentTenant(String tenantId){
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Retorna o tenant associado à thread atual.
     *
     * @return identificador do tenant ou {@code null} caso não esteja definido
     */
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }
    /**
     * Remove o tenant do contexto da thread atual.
     *
     * <p>Deve ser chamado ao final da requisição para evitar vazamento
     * de contexto em ambientes com reutilização de threads.</p>
     */
    public static void clear(){
        CURRENT_TENANT.remove();
    }
}
