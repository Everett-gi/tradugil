/*
 * Gera uma migracao Flyway a partir de um arquivo de termos.
 *
 *   node curadoria/gerar-migracao.mjs termos/gaming.mjs V9 "girias de jogos"
 *
 * POR QUE UM GERADOR, E NAO SQL ESCRITO A MAO
 *
 * Cada verbete precisa de duas chaves derivadas do termo: a forma
 * normalizada (minuscula, sem acento, sem pontuacao) e a forma com enfase
 * colapsada. Escrever isso a mao para centenas de termos erraria, e o erro e
 * silencioso: o verbete entra no banco e nunca e encontrado por ninguem,
 * porque a chave gravada nao corresponde ao que o cliente calcula na busca.
 *
 * Aqui as duas chaves saem da mesma regra que Normalizador.java aplica. O
 * teste ConsistenciaDoSeedIT confere linha a linha, no banco, que elas
 * batem: se este arquivo divergir do Java, a CI quebra.
 *
 * A migracao gerada e imutavel depois de aplicada, como qualquer outra.
 * Termo novo entra em migracao nova, nunca editando uma que ja rodou.
 */
import { readdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const AQUI = dirname(fileURLToPath(import.meta.url));

/* ------------------------------------------------------ normalizacao ----- */
/*
 * Porte de Normalizador.java. As tres implementacoes (Java, TypeScript,
 * Kotlin) e esta precisam concordar; ver docs/adr/0001.
 */

const ACENTOS = /[\u0300-\u036f]/g;
const RUIDO = /[^\p{Alphabetic}\p{Nd} ]/gu;
const ESPACOS = /\s+/g;
const REPETICOES = /(.)\1{2,}/g;

function normalizar(bruto) {
  if (!bruto || !bruto.trim()) return "";
  const semAcento = bruto.normalize("NFD").replace(ACENTOS, "");
  return semAcento.toLowerCase().replace(RUIDO, " ").replace(ESPACOS, " ").trim();
}

function colapsar(normalizado) {
  return normalizado ? normalizado.replace(REPETICOES, "$1$1") : "";
}

/* ------------------------------------------------------------- SQL ------- */

/** Escapa aspas simples. Nunca interpolar texto de curadoria sem passar por aqui. */
function texto(v) {
  if (v === null || v === undefined || v === "") return "NULL";
  return "'" + String(v).replace(/'/g, "''") + "'";
}

function bool(v) {
  return v ? "TRUE" : "FALSE";
}

/* --------------------------------------------------------- validacao ----- */

function validar(termos) {
  const problemas = [];
  const vistos = new Map();

  termos.forEach((e, i) => {
    const onde = `[${i}] ${e.termo}`;

    if (!e.termo || !e.termo.trim()) problemas.push(`${onde}: termo vazio`);
    if (!["pt-BR", "en"].includes(e.idioma)) problemas.push(`${onde}: idioma invalido`);

    const norm = normalizar(e.termo);
    if (!norm) problemas.push(`${onde}: normaliza para vazio, ninguem acharia`);
    if (norm.length > 80) problemas.push(`${onde}: passa de 80 caracteres`);

    const chave = `${norm}|${e.idioma}`;
    if (vistos.has(chave)) {
      problemas.push(`${onde}: duplicado de "${vistos.get(chave)}"`);
    } else {
      vistos.set(chave, e.termo);
    }

    /*
     * Variacao que normaliza para vazio quebra a migracao com violacao de
     * NOT NULL, porque a chave gerada vira nulo. Aconteceu de verdade com a
     * variacao "@" do verbete "arroba": so simbolo, nenhuma letra.
     *
     * Mesmo que passasse, seria inutil: chave vazia nao e encontrada por
     * busca nenhuma.
     */
    e.variacoes?.forEach((v) => {
      if (!normalizar(v)) {
        problemas.push(`${onde}: variacao "${v}" normaliza para vazio`);
      }
    });

    if (!Array.isArray(e.sentidos) || e.sentidos.length === 0) {
      problemas.push(`${onde}: sem nenhum sentido`);
    }
    e.sentidos?.forEach((s, j) => {
      if (!s.simples || s.simples.length < 15) {
        problemas.push(`${onde} sentido ${j}: explicacao curta demais`);
      }
      if (s.simples && s.simples.length > 400) {
        problemas.push(`${onde} sentido ${j}: explicacao longa demais`);
      }
      if (s.formal && s.formal.length > 160) {
        problemas.push(`${onde} sentido ${j}: equivalente formal longo demais`);
      }
      /*
       * "aliciamento" com equivalente formal "aliciamento" vira, na tela,
       * uma linha "Em outras palavras: aliciamento" logo abaixo da palavra
       * que a pessoa acabou de ler. Nao acrescenta nada e manda reler.
       * Entraram 41 assim antes desta checagem existir; a V20 os limpou.
       *
       * Deixar o campo de fora e a resposta certa: nulo quer dizer "nao ha
       * um jeito mais formal de dizer isto", e as interfaces ja escondem a
       * linha nesse caso.
       */
      if (s.formal && s.formal.toLowerCase().trim() === e.termo.toLowerCase().trim()) {
        problemas.push(
          `${onde} sentido ${j}: equivalente formal repete o termo; ` +
            `deixe sem formal em vez de repetir`,
        );
      }
    });
  });

  return problemas;
}

/*
 * Palavras que em portugues nunca estao certas sem acento.
 *
 * A primeira versao desta checagem so olhava se o texto tinha algum acento,
 * e disparava em 42 explicacoes corretas: "Aviso de que a pessoa volta em
 * poucos minutos" nao leva acento nenhum e esta certa. Comprimento nao diz
 * nada sobre acentuacao.
 *
 * Esta lista diz. Cada palavra aqui so existe em portugues na forma
 * acentuada, entao encontrar a forma sem acento e erro, nunca estilo. Ficam
 * de fora as ambiguas ("esta" e "esta", "so" e "so"), que gerariam
 * falso positivo.
 */
const SEMPRE_ACENTUADAS = new Set([
  "nao", "voce", "voces", "tambem", "entao", "porem", "alem", "apos", "atras",
  "ninguem", "alguem", "armazem", "refem", "sao", "tres", "mes", "pes",
  "portugues", "ingles", "japones", "frances", "chines",
  "video", "videos", "musica", "musicas", "pagina", "paginas", "maquina",
  "silaba", "gramatica", "matematica", "fisica", "quimica",
  "proprio", "propria", "proprios", "proprias", 
  "numero", "numeros", "nivel", "niveis", "ultimo", "ultima", "ultimos",
  "unico", "unica", "medico", "rapido", "solido", "valido", "invalido",
  "liquido", "minimo", "maximo", "otimo", "pessimo", "proximo", "anonimo",
  "sinonimo", "economico", "historico", "historia", "memoria", "seculo",
  "area", "epoca", "tecnica", "tecnico", "logica", "logico",
  "basico", "classico", "tipico", 
  "automatico", "especifico", "generico", "dinamico", "grafico", "tragico",
  "magico", "comico", "facil", "dificil", "possivel", "impossivel",
  "atencao", "explicacao", "explicacoes", "abreviacao", "informacao",
  "situacao", "relacao", "condicao", "funcao", "acao", "acoes", "opcao",
  "opcoes", "versao", "versoes", "expressao", "expressoes", "sensacao",
  "reacao", "traducao", "punicao", "competicao", "regulacao", 
  "referencia", "experiencia", "consequencia", "frequencia", "competencia",
  "ciencia", "paciencia", "tendencia", "distancia", "importancia", "familia",
  "orgao", "coracao", "irmao", "irmaos", "mao", "maos", "pao", "chao",
  "sera", "cafe", "aviao", "cao",
]);

/*
 * Procura essas palavras na forma sem acento. Bloqueia a geracao: aqui nao
 * ha caso legitimo, so erro de digitacao ou lote escrito sem acentuacao.
 *
 * Existe por causa de um erro real: o primeiro lote de termos foi escrito
 * inteiro sem acento, e passou despercebido ate o texto aparecer na tela.
 * Explicacao e o que o usuario le; "Abreviacao" no lugar de "Abreviacao" nao
 * e detalhe de codigo, e portugues errado.
 */
function avisarSemAcento(termos) {
  const suspeitos = [];

  termos.forEach((e) => {
    e.sentidos.forEach((s, j) => {
      for (const campo of ["simples", "detalhada", "formal"]) {
        const texto = s[campo];
        if (!texto) continue;

        /*
         * Tira o que esta entre aspas antes de conferir. As explicacoes
         * citam o termo original em ingles o tempo todo ("video on demand",
         * "public"), e ingles nao leva acento: sem esta linha, toda citacao
         * viraria falso positivo.
         */
        const semCitacao = texto.replace(/"[^"]*"/g, " ");
        const palavras = semCitacao.toLowerCase().match(/[\p{Alphabetic}]+/gu) || [];
        const erradas = [...new Set(palavras.filter((w) => SEMPRE_ACENTUADAS.has(w)))];
        if (erradas.length) {
          suspeitos.push(`${e.termo} (sentido ${j}, ${campo}): ${erradas.join(", ")}`);
        }
      }
    });
  });

  return suspeitos;
}

/* ------------------------------------------------ duplicata entre arquivos --- */

/**
 * Procura os termos deste arquivo nos outros arquivos de curadoria.
 *
 * POR QUE ISTO PRECISA FALHAR, E NAO SO AVISAR
 *
 * O INSERT de `giria` tem ON CONFLICT DO NOTHING e nao duplica o verbete. O
 * INSERT de `definicao` nao tem guarda nenhuma, porque acrescentar um sentido
 * novo a um termo que ja existe e justamente um caso legitimo. A consequencia
 * e que repetir um termo inteiro num arquivo novo grava a MESMA explicacao
 * duas vezes, e a tela mostra o verbete com o texto repetido.
 *
 * O erro e silencioso do jeito que mais custa: a migracao aplica sem
 * reclamar, os testes passam, e o defeito so aparece para quem abrir aquele
 * verbete especifico.
 *
 * Aconteceu ao escrever familia-casa.mjs: 11 dos 35 termos ja estavam em
 * outros arquivos, porque "coroa" e "cara de pau" sao obvios em mais de uma
 * categoria e ninguem lembra de 700 verbetes de cabeca.
 *
 * A comparacao e pela forma normalizada, e nao pela grafia: "véia" e "veia"
 * sao o mesmo verbete para o banco, e seriam duas linhas aqui se comparadas
 * como texto.
 */
async function procurarEmOutrosArquivos(termos, arquivoAtual) {
  const pasta = join(AQUI, "termos");
  const atual = resolve(AQUI, arquivoAtual);

  /*
   * A chave é só o termo normalizado, SEM o idioma.
   *
   * A chave única do banco é (termo_normalizado, idioma_id), então o mesmo
   * termo em dois idiomas são duas linhas legítimas. Só que o /traduzir não
   * filtra por idioma: ele resolve as duas e descarta a segunda por ocupar a
   * mesma posição no texto. O efeito é que um dos dois verbetes fica
   * inalcançável, e qual deles depende da ordem em que o banco devolveu.
   *
   * Aconteceu de verdade com "feed": o sentido de jogo (en) e o de rede
   * social (pt-BR) eram verbetes separados, e um sombreava o outro. A V31
   * juntou os dois num verbete com dois sentidos, que é como o esquema
   * representa isso.
   */
  const meus = new Map(termos.map((e) => [normalizar(e.termo), e]));
  const emArquivos = [];
  const noSeed = [];
  const contraVariacoes = [];

  /*
   * Termo novo que colide com VARIACAO de um verbete que ja existe.
   *
   * Foi o erro do "PogChamp": "pogchamp" ja era variacao de "pog", e criar um
   * verbete com esse nome fez a mesma palavra apontar para dois lugares. O
   * /traduzir resolve os dois e descarta o segundo por ocupar a mesma posicao
   * no texto, entao um deles fica inalcancavel e qual depende da ordem das
   * linhas.
   *
   * Aconteceu de novo depois: o termo "media" (a nota da escola) contra
   * "media" ja cadastrada como variacao de "pingado", o cafe com leite.
   *
   * POR QUE ISTO AVISA E NAO BLOQUEIA
   *
   * A primeira versao bloqueava, e apontou ~50 casos nos 18 arquivos. Quase
   * todos sao benignos: sao pares de sinonimos que se referenciam, como
   * "mutar" e "silenciar", ou "print" e "printar". Nesses, qual dos dois
   * responde nao importa, porque os dois dizem a mesma coisa.
   *
   * So uma pessoa distingue esses do caso ruim, que e a mesma palavra levando
   * a dois SIGNIFICADOS diferentes. Bloquear tornaria o gerador inutilizavel
   * e treinaria quem usa a ignorar o aviso, que e o pior resultado possivel
   * para uma checagem.
   */
  const conferirContraVariacoes = (entradas, onde) => {
    for (const e of entradas) {
      for (const v of e.variacoes ?? []) {
        const meu = meus.get(normalizar(v));
        if (meu && normalizar(meu.termo) !== normalizar(e.termo)) {
          contraVariacoes.push(
            `${meu.termo}  colide com a variacao "${v}" de "${e.termo}"` +
              ` em ${onde}`,
          );
        }
      }
    }
  };

  const registrar = (destino, encontrado, idiomaLa, onde) => {
    const meu = meus.get(normalizar(encontrado));
    if (!meu) return;
    const nota = meu.idioma === idiomaLa
      ? ""
      : ` (aqui como ${meu.idioma}, la como ${idiomaLa}: sao linhas`
        + ` diferentes no banco, mas o /traduzir nao filtra por idioma e uma`
        + ` sombreia a outra)`;
    destino.push(`${meu.termo}  ja esta em ${onde}${nota}`);
  };

  for (const nome of readdirSync(pasta).filter((n) => n.endsWith(".mjs"))) {
    const caminho = join(pasta, nome);
    if (resolve(caminho) === atual) continue;

    const outros = (await import("file://" + caminho)).default;
    for (const e of outros) {
      registrar(emArquivos, e.termo, e.idioma, `termos/${nome}`);
    }
    conferirContraVariacoes(outros, `termos/${nome}`);
  }

  // E dentro do proprio arquivo: um termo pode colidir com a variacao de
  // outro verbete do mesmo lote, e a checagem de duplicata do validador so
  // compara termo com termo.
  conferirContraVariacoes(termos, "este arquivo");

  for (const [termo, idioma, onde] of doSeedEscritoAMao()) {
    registrar(noSeed, termo, idioma, onde);
  }

  return {
    emArquivos: [...new Set(emArquivos)].sort(),
    noSeed: [...new Set(noSeed)].sort(),
    contraVariacoes: [...new Set(contraVariacoes)].sort(),
  };
}

/**
 * Termos que existem no banco mas em nenhum arquivo de curadoria.
 *
 * O seed inicial (V3) foi escrito a mao, antes de este gerador existir. Os
 * verbetes dele nao estao em termos/ nenhum, entao a varredura acima nao os
 * enxerga: e um ponto cego, e ele custou dois falsos "termo novo" neste lote
 * ("Poggers" e "EZ"), pegos so na conferencia manual contra o banco.
 *
 * A extracao le a lista de VALUES do INSERT de giria. E frouxa de proposito:
 * se o formato do SQL mudar e a regex parar de casar, ela devolve lista
 * vazia e a checagem volta a ter o ponto cego, em vez de quebrar a geracao
 * de todo mundo por causa de um arquivo antigo.
 */
function doSeedEscritoAMao() {
  const pasta = join(AQUI, "..", "api", "src", "main", "resources", "db", "migration");
  const encontrados = [];

  for (const nome of readdirSync(pasta).filter((n) => n.endsWith(".sql"))) {
    const sql = readFileSync(join(pasta, nome), "utf8");
    if (!/INSERT INTO giria\s*\(/.test(sql)) continue;

    // Linhas como:  ('pog', 'pog', 'en', false, false),
    const linhas = sql.matchAll(
      /\(\s*'((?:[^']|'')*)'\s*,\s*'((?:[^']|'')*)'\s*,\s*'(pt-BR|en)'/g,
    );
    for (const linha of linhas) {
      encontrados.push([linha[1].replace(/''/g, "'"), linha[3], nome]);
    }
  }
  return encontrados;
}

/* ------------------------------------------------------------ geracao ---- */

function gerar(termos, descricao) {
  const linhas = [];
  const p = (s = "") => linhas.push(s);

  const comSentidos = termos.flatMap((e) =>
    e.sentidos.map((s, ordem) => ({ ...e, sentido: s, ordem })),
  );
  const comVariacoes = termos.filter((e) => e.variacoes?.length);
  const comCategorias = termos.filter((e) => e.categorias?.length);
  const comExemplos = comSentidos.filter((e) => e.sentido.exemplos?.length);

  p("-- ---------------------------------------------------------------------------");
  p(`-- ${descricao}`);
  p("--");
  p(`-- ${termos.length} verbetes, ${comSentidos.length} sentidos.`);
  p("--");
  p("-- GERADO por curadoria/gerar-migracao.mjs. Nao edite este arquivo a mao:");
  p("-- mexa na fonte em curadoria/termos/ e gere de novo. Depois de aplicada,");
  p("-- a migracao e imutavel como qualquer outra, e termo novo entra em");
  p("-- migracao nova.");
  p("--");
  p("-- As chaves de busca (termo_normalizado, termo_colapsado) sao calculadas");
  p("-- pela mesma regra de Normalizador.java. ConsistenciaDoSeedIT confere no");
  p("-- banco que elas batem.");
  p("-- ---------------------------------------------------------------------------");
  p();

  /* Termos. ON CONFLICT DO NOTHING permite acrescentar um sentido novo a um
     termo que ja existe, sem duplicar o verbete. */
  p("INSERT INTO giria (termo, termo_normalizado, termo_colapsado, idioma_id, nsfw, risco_menor)");
  p("SELECT t.termo, t.norm, t.colapsado, i.id, t.nsfw, t.risco");
  p("FROM (VALUES");
  p(
    termos
      .map((e) => {
        const n = normalizar(e.termo);
        return `    (${texto(e.termo)}, ${texto(n)}, ${texto(colapsar(n))}, ` +
          `${texto(e.idioma)}, ${bool(e.nsfw)}, ${bool(e.risco)})`;
      })
      .join(",\n"),
  );
  p(") AS t(termo, norm, colapsado, idioma, nsfw, risco)");
  p("JOIN idioma i ON i.codigo = t.idioma");
  p("ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;");
  p();

  /* Sentidos. */
  p("INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,");
  p("                       equivalente_formal, fonte_id)");
  p("SELECT g.id, d.simples, d.detalhada, d.formal,");
  p("       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')");
  p("FROM (VALUES");
  p(
    comSentidos
      .map(
        (e) =>
          `    (${texto(normalizar(e.termo))}, ${texto(e.idioma)}, ` +
          `${texto(e.sentido.simples)}, ${texto(e.sentido.detalhada)}, ` +
          `${texto(e.sentido.formal)})`,
      )
      .join(",\n"),
  );
  p(") AS d(norm, idioma, simples, detalhada, formal)");
  p("JOIN idioma i ON i.codigo = d.idioma");
  p("JOIN giria g ON g.termo_normalizado = d.norm AND g.idioma_id = i.id");
  p("-- Guarda contra explicacao duplicada.");
  p("--");
  p("-- O INSERT de giria tem ON CONFLICT DO NOTHING; este nao pode ter, porque");
  p("-- acrescentar um SENTIDO NOVO a um termo existente e caso legitimo e uma");
  p("-- chave unica sobre o texto impediria isso.");
  p("--");
  p("-- Sem esta clausula, um termo que ja existe no banco (por outro lote ou");
  p("-- pelo seed escrito a mao) recebia a MESMA explicacao de novo. A migracao");
  p("-- aplicava sem reclamar, os testes passavam, e o verbete aparecia na tela");
  p("-- com a mesma frase escrita duas ou tres vezes. Foram 14 verbetes assim,");
  p("-- limpos pelas V29 e V33.");
  p("--");
  p("-- Compara pelo resumo, e nao pela linha inteira: e o resumo que a pessoa");
  p("-- le, e duas versoes do mesmo sentido com detalhes diferentes continuam");
  p("-- sendo repeticao aos olhos de quem consulta.");
  p("WHERE NOT EXISTS (");
  p("    SELECT 1 FROM definicao ja");
  p("    WHERE ja.giria_id = g.id");
  p("      AND ja.explicacao_simples = d.simples");
  p(");");
  p();

  if (comVariacoes.length) {
    p("INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,");
    p("                            variacao_colapsada)");
    p("SELECT g.id, v.variacao, v.norm, v.colapsado");
    p("FROM (VALUES");
    p(
      comVariacoes
        .flatMap((e) =>
          e.variacoes.map((x) => {
            const n = normalizar(x);
            return `    (${texto(normalizar(e.termo))}, ${texto(e.idioma)}, ` +
              `${texto(x)}, ${texto(n)}, ${texto(colapsar(n))})`;
          }),
        )
        .join(",\n"),
    );
    p(") AS v(termo_norm, idioma, variacao, norm, colapsado)");
    p("JOIN idioma i ON i.codigo = v.idioma");
    p("JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id");
    p("ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;");
    p();
  }

  if (comCategorias.length) {
    p("INSERT INTO giria_categoria (giria_id, categoria_id)");
    p("SELECT g.id, c.id");
    p("FROM (VALUES");
    p(
      comCategorias
        .flatMap((e) =>
          e.categorias.map(
            (cat) =>
              `    (${texto(normalizar(e.termo))}, ${texto(e.idioma)}, ${texto(cat)})`,
          ),
        )
        .join(",\n"),
    );
    p(") AS gc(termo_norm, idioma, categoria)");
    p("JOIN idioma i ON i.codigo = gc.idioma");
    p("JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id");
    p("JOIN categoria c ON c.slug = gc.categoria");
    p("ON CONFLICT DO NOTHING;");
    p();
  }

  if (comExemplos.length) {
    /* O exemplo prende na primeira definicao do verbete. Para o publico do
       produto, o exemplo costuma explicar melhor que a propria definicao. */
    p("INSERT INTO exemplo (definicao_id, frase, traducao)");
    p("SELECT d.id, e.frase, e.traducao");
    p("FROM (VALUES");
    p(
      comExemplos
        .flatMap((e) =>
          e.sentido.exemplos.map(
            (ex) =>
              `    (${texto(normalizar(e.termo))}, ${texto(e.idioma)}, ` +
              `${texto(ex.frase)}, ${texto(ex.traducao)})`,
          ),
        )
        .join(",\n"),
    );
    p(") AS e(termo_norm, idioma, frase, traducao)");
    p("JOIN idioma i ON i.codigo = e.idioma");
    p("JOIN giria g ON g.termo_normalizado = e.termo_norm AND g.idioma_id = i.id");
    p("JOIN definicao d ON d.giria_id = g.id");
    p("   AND d.id = (SELECT MIN(id) FROM definicao WHERE giria_id = g.id);");
    p();
  }

  return linhas.join("\n");
}

/* -------------------------------------------------------------- main ----- */

const [arquivo, versao, descricao] = process.argv.slice(2);
if (!arquivo || !versao) {
  console.error("uso: node gerar-migracao.mjs <termos/arquivo.mjs> <V9> [descricao]");
  process.exit(1);
}

const termos = (await import("file://" + resolve(AQUI, arquivo))).default;

const { emArquivos, noSeed, contraVariacoes } =
  await procurarEmOutrosArquivos(termos, arquivo);

/*
 * Duas severidades diferentes, por um motivo concreto.
 *
 * O SQL gerado agora ignora explicacao que ja existe (o WHERE NOT EXISTS do
 * INSERT de definicao), entao nenhum dos dois casos consegue mais duplicar
 * texto no banco. O que sobra e uma questao de organizacao da curadoria.
 *
 * BLOQUEIA quando o termo esta em outro arquivo de curadoria: duas pessoas
 * escreveram o mesmo verbete, e alguem precisa decidir qual fica. Deixar
 * passar mantem duas versoes divergindo em paralelo.
 *
 * AVISA quando o termo esta no seed escrito a mao (V3). Ali nao ha decisao a
 * tomar: o arquivo de curadoria tem a versao completa, o seed tem a antiga,
 * e o banco ja ficou com a completa. Bloquear tornaria impossivel acrescentar
 * um termo novo a gaming.mjs por causa de um "gg" cadastrado em 2026.
 */
if (emArquivos.length) {
  console.error("");
  console.error(`${emArquivos.length} termo(s) ja definidos em outro arquivo de curadoria:`);
  console.error("");
  emArquivos.slice(0, 30).forEach((x) => console.error("  " + x));
  if (emArquivos.length > 30) {
    console.error(`  ... e mais ${emArquivos.length - 30}`);
  }
  console.error("");
  console.error("  Duas versoes do mesmo verbete divergem com o tempo, e a que a");
  console.error("  pessoa recebe passa a depender da ordem das linhas no banco.");
  console.error("");
  console.error("  Escolha uma. Para acrescentar um sentido novo a um termo que");
  console.error("  ja existe, edite o arquivo onde ele ja esta.");
  console.error("");
  process.exit(1);
}

if (contraVariacoes.length) {
  console.warn("");
  console.warn(`aviso: ${contraVariacoes.length} termo(s) colidem com variacao de outro verbete:`);
  console.warn("");
  contraVariacoes.slice(0, 15).forEach((x) => console.warn("  " + x));
  if (contraVariacoes.length > 15) {
    console.warn(`  ... e mais ${contraVariacoes.length - 15}`);
  }
  console.warn("");
  console.warn("  A mesma palavra passa a levar a dois verbetes, e o /traduzir");
  console.warn("  descarta o segundo por ocupar a mesma posicao: um deles fica");
  console.warn("  inalcancavel, e qual depende da ordem das linhas no banco.");
  console.warn("");
  console.warn("  Nao bloqueia porque a maioria e par de sinonimo, onde tanto");
  console.warn("  faz qual responde. Vale olhar os que tem SIGNIFICADO diferente.");
  console.warn("");
}

if (noSeed.length) {
  console.warn("");
  console.warn(`aviso: ${noSeed.length} termo(s) tambem estao no seed escrito a mao:`);
  console.warn("");
  noSeed.slice(0, 15).forEach((x) => console.warn("  " + x));
  if (noSeed.length > 15) console.warn(`  ... e mais ${noSeed.length - 15}`);
  console.warn("");
  console.warn("  Nao bloqueia: o INSERT de definicao ignora explicacao que ja");
  console.warn("  existe, entao nada e duplicado. Fica registrado porque o seed");
  console.warn("  e historico e um dia deve sair de cena.");
  console.warn("");
}

const problemas = validar(termos);

if (problemas.length) {
  console.error(`${problemas.length} problema(s):\n`);
  problemas.slice(0, 30).forEach((x) => console.error("  " + x));
  if (problemas.length > 30) console.error(`  ... e mais ${problemas.length - 30}`);
  process.exit(1);
}

const semAcento = avisarSemAcento(termos);
if (semAcento.length) {
  console.error("");
  console.error(`${semAcento.length} texto(s) com palavra que exige acento:`);
  console.error("");
  semAcento.slice(0, 20).forEach((x) => console.error("  " + x));
  if (semAcento.length > 20) console.error(`  ... e mais ${semAcento.length - 20}`);
  console.error("");
  process.exit(1);
}

const nome = `${versao}__${(descricao || "girias")
  .toLowerCase()
  .normalize("NFD")
  .replace(ACENTOS, "")
  .replace(/[^a-z0-9]+/g, "_")
  .replace(/^_|_$/g, "")}.sql`;

const destino = join(AQUI, "..", "api", "src", "main", "resources", "db", "migration", nome);
writeFileSync(destino, gerar(termos, descricao || "Girias") + "\n");

const sentidos = termos.reduce((n, e) => n + e.sentidos.length, 0);
console.log(`${nome}: ${termos.length} verbetes, ${sentidos} sentidos`);
