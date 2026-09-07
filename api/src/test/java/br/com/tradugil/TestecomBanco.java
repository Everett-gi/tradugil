package br.com.tradugil;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca um teste que precisa de PostgreSQL de verdade.
 *
 * <p>H2 não serve: a busca tolerante a erro de digitação depende de
 * {@code pg_trgm}, que só existe no PostgreSQL. Um teste verde em H2 não
 * provaria nada sobre o nível 2 da cascata — que é justamente o que precisa
 * ser provado.</p>
 *
 * <p>A anotação liga os testes apenas quando {@code TRADUGIL_TEST_DB_URL}
 * existe no ambiente. Na CI ela é definida pelo workflow e tudo roda; na
 * máquina de quem não tem Postgres instalado, estes testes são pulados com
 * aviso, e os de unidade continuam valendo. É melhor pular avisando do que
 * quebrar o {@code mvn test} de quem só quer mexer no tokenizador.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@ActiveProfiles("teste")
@EnabledIfEnvironmentVariable(
        named = "TRADUGIL_TEST_DB_URL",
        matches = ".+",
        disabledReason = "Sem banco de teste configurado. "
                + "Defina TRADUGIL_TEST_DB_URL, TRADUGIL_TEST_DB_USUARIO "
                + "e TRADUGIL_TEST_DB_SENHA para rodar os testes de integração.")
public @interface TestecomBanco {
}
