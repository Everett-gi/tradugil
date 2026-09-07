package br.com.tradugil.identidade;

import java.util.Set;

/**
 * As senhas que um ataque tenta primeiro.
 *
 * <h2>Por que uma lista, e não regras de complexidade</h2>
 *
 * <p>Exigir maiúscula, número e símbolo produz "Senha@123", que atende a
 * todas as regras e está no topo de qualquer lista de senhas vazadas. O NIST
 * deixou de recomendar essas regras em 2017 (SP 800-63B) e passou a
 * recomendar exatamente isto: comparar com uma lista de senhas conhecidas.</p>
 *
 * <h2>Por que uma lista curta</h2>
 *
 * <p>O ideal seria conferir contra uma base de senhas vazadas, via a API de
 * k-anonimato do Have I Been Pwned. Não está aqui por uma decisão de
 * dependência: significaria uma chamada de rede no meio do cadastro, com o
 * que fazer quando ela falha (deixar passar? recusar?), e a resposta certa
 * para esse dilema depende de um volume de usuários que este projeto ainda
 * não tem.</p>
 *
 * <p>Esta lista cobre o que um ataque tenta nas primeiras centenas de
 * tentativas, incluindo as variantes brasileiras que listas em inglês não
 * trazem. Combinada com o travamento de conta, que limita o ataque a algumas
 * tentativas por hora, é proporcional ao risco real.</p>
 *
 * <p>Comparar sem acento e sem caixa é deliberado: "SENHA123" e "senha123"
 * são a mesma senha para quem ataca.</p>
 */
final class SenhasProibidas {

    private SenhasProibidas() {
    }

    private static final Set<String> LISTA = Set.of(
            // Clássicas universais
            "password", "passw0rd", "password1", "password123", "qwerty",
            "qwerty123", "qwertyui", "asdfghjk", "zxcvbnm", "abc12345",
            "letmein", "welcome", "iloveyou", "admin123", "administrator",
            "monkey123", "dragon123", "sunshine", "princess", "football",
            "baseball", "starwars", "superman", "batman123", "trustno1",
            "master123", "shadow123", "michael1", "jennifer", "computer",

            // Só dígitos, o padrão mais comum do mundo
            "12345678", "123456789", "1234567890", "87654321", "11111111",
            "00000000", "12341234", "11223344", "10203040", "123123123",

            // Brasileiras: nenhuma lista em inglês traz estas
            "senha123", "senhasenha", "brasil123", "flamengo", "corinthians",
            "palmeiras", "saopaulo", "vasco123", "gremio123", "internacional",
            "cruzeiro", "atletico", "santos123", "botafogo", "fluminense",
            "familia1", "familia123", "amoreterno", "deusefiel", "deusnocomando",
            "teamointeiro", "meuamor1", "princesa1", "borboleta", "florzinha",
            "cachorro1", "saudade1", "gostosa1", "carnaval", "feijoada",

            // Nomes muito frequentes com sufixo numérico
            "maria123", "joao1234", "ana12345", "pedro123", "lucas123",
            "julia123", "carlos123", "bruno123", "felipe123", "gabriel1",

            // Datas e padrões de teclado
            "01012000", "31121999", "1q2w3e4r", "1qaz2wsx", "qazwsxedc",
            "asdf1234", "zaq12wsx", "poiuytre", "mnbvcxz1",

            // O nome do próprio produto, que é a primeira tentativa de quem
            // sonda um serviço específico
            "tradugil", "tradugil123", "tradugiria", "tradugil2026");

    static boolean contem(String senhaEmMinusculas) {
        return LISTA.contains(semAcento(senhaEmMinusculas));
    }

    private static String semAcento(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    /** Exposto só para o teste conferir que a lista não encolheu por acidente. */
    static int tamanho() {
        return LISTA.size();
    }
}
