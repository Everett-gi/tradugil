# A API em contêiner.
#
# Duas etapas: uma compila, outra roda. A imagem final não leva Maven, nem o
# código-fonte, nem o cache de dependências. São centenas de megabytes de
# ferramenta de compilação que, num servidor, só servem para aumentar a
# superfície de ataque e o tempo de download.

# --------------------------------------------------------------- compilacao

# A tag traz a versao do Java e a do Maven juntas. Sem fixar as duas, uma
# reconstrucao daqui a seis meses compila com outro compilador e a diferenca
# so aparece em producao.
FROM maven:3.9-eclipse-temurin-21 AS compilacao

WORKDIR /origem

# O pom primeiro, e o codigo depois, de proposito. O Docker guarda cada etapa
# em cache e so refaz da primeira que mudou para baixo: assim trocar uma linha
# de Java nao rebaixa as dependencias inteiras de novo.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src

# Sem testes aqui. Os testes de integracao precisam de PostgreSQL, que nao
# existe dentro desta etapa, e rodar so os de unidade daria uma falsa sensacao
# de conferido. Quem roda a suite inteira e a CI, contra um Postgres de
# verdade, antes de o commit virar imagem.
RUN mvn -B -q -DskipTests package

# ------------------------------------------------------------------ execucao

# JRE, e nao JDK: a imagem final nao precisa de compilador.
#
# O Always Free da Oracle e ARM (VM.Standard.A1.Flex, aarch64). A temurin
# publica as duas arquiteturas sob a mesma tag, entao isto funciona tanto no
# servidor quanto numa maquina x86 de desenvolvimento.
FROM eclipse-temurin:21-jre AS execucao

# Usuario proprio, sem shell e sem casa.
#
# Contêiner que roda como root e root da maquina hospedeira se alguem escapar
# dele. Nao ha nada aqui que precise de privilegio: a aplicacao abre uma porta
# alta, le variaveis de ambiente e fala com o banco.
RUN groupadd --system tradugil \
    && useradd --system --gid tradugil --no-create-home --shell /usr/sbin/nologin tradugil

WORKDIR /app

COPY --from=compilacao --chown=tradugil:tradugil /origem/target/tradugil-api-*.jar app.jar

USER tradugil

EXPOSE 8080

# O contentor nao publica esta porta para fora: quem fala com a API e o Caddy,
# pela rede interna do compose. Ver infra/docker-compose.yml.

# A JVM le o limite de memoria do cgroup desde o Java 10 e dimensiona a heap a
# partir dele. MaxRAMPercentage explicito porque o padrao (25%) e conservador
# demais quando o contêiner ja tem um teto: sobrar 75% da memoria reservada
# sem uso nao ajuda ninguem.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

# ExitOnOutOfMemoryError acima e deliberado: uma JVM sem memoria fica viva
# respondendo erro a tudo, e o orquestrador nao percebe. Morrer deixa o
# restart do compose fazer o trabalho.

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
