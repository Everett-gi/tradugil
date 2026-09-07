-- ---------------------------------------------------------------------------
-- girias de rua e marcadores de tempo
--
-- 26 verbetes, 26 sentidos.
--
-- GERADO por curadoria/gerar-migracao.mjs. Nao edite este arquivo a mao:
-- mexa na fonte em curadoria/termos/ e gere de novo. Depois de aplicada,
-- a migracao e imutavel como qualquer outra, e termo novo entra em
-- migracao nova.
--
-- As chaves de busca (termo_normalizado, termo_colapsado) sao calculadas
-- pela mesma regra de Normalizador.java. ConsistenciaDoSeedIT confere no
-- banco que elas batem.
-- ---------------------------------------------------------------------------

INSERT INTO giria (termo, termo_normalizado, termo_colapsado, idioma_id, nsfw, risco_menor)
SELECT t.termo, t.norm, t.colapsado, i.id, t.nsfw, t.risco
FROM (VALUES
    ('quebrar o galho na rua', 'quebrar o galho na rua', 'quebrar o galho na rua', 'pt-BR', FALSE, FALSE),
    ('de bobeira', 'de bobeira', 'de bobeira', 'pt-BR', FALSE, FALSE),
    ('role da vida', 'role da vida', 'role da vida', 'pt-BR', FALSE, FALSE),
    ('pá', 'pa', 'pa', 'pt-BR', FALSE, FALSE),
    ('sangue bom', 'sangue bom', 'sangue bom', 'pt-BR', FALSE, FALSE),
    ('morou', 'morou', 'morou', 'pt-BR', FALSE, FALSE),
    ('pilantragem', 'pilantragem', 'pilantragem', 'pt-BR', FALSE, FALSE),
    ('fita', 'fita', 'fita', 'pt-BR', FALSE, FALSE),
    ('de responsa', 'de responsa', 'de responsa', 'pt-BR', FALSE, FALSE),
    ('trocar ideia', 'trocar ideia', 'trocar ideia', 'pt-BR', FALSE, FALSE),
    ('pegar a visão', 'pegar a visao', 'pegar a visao', 'pt-BR', FALSE, FALSE),
    ('tá ligado', 'ta ligado', 'ta ligado', 'pt-BR', FALSE, FALSE),
    ('meu chapa', 'meu chapa', 'meu chapa', 'pt-BR', FALSE, FALSE),
    ('no aperto', 'no aperto', 'no aperto', 'pt-BR', FALSE, FALSE),
    ('de última hora', 'de ultima hora', 'de ultima hora', 'pt-BR', FALSE, FALSE),
    ('fim de semana emendado', 'fim de semana emendado', 'fim de semana emendado', 'pt-BR', FALSE, FALSE),
    ('dia sim, dia não', 'dia sim dia nao', 'dia sim dia nao', 'pt-BR', FALSE, FALSE),
    ('toda hora', 'toda hora', 'toda hora', 'pt-BR', FALSE, FALSE),
    ('nunca mais', 'nunca mais', 'nunca mais', 'pt-BR', FALSE, FALSE),
    ('hoje em dia', 'hoje em dia', 'hoje em dia', 'pt-BR', FALSE, FALSE),
    ('de manhã cedo', 'de manha cedo', 'de manha cedo', 'pt-BR', FALSE, FALSE),
    ('altas horas', 'altas horas', 'altas horas', 'pt-BR', FALSE, FALSE),
    ('num piscar de olhos', 'num piscar de olhos', 'num piscar de olhos', 'pt-BR', FALSE, FALSE),
    ('empurrar com a barriga', 'empurrar com a barriga', 'empurrar com a barriga', 'pt-BR', FALSE, FALSE),
    ('virada de ano', 'virada de ano', 'virada de ano', 'pt-BR', FALSE, FALSE),
    ('fora de época', 'fora de epoca', 'fora de epoca', 'pt-BR', FALSE, FALSE)
) AS t(termo, norm, colapsado, idioma, nsfw, risco)
JOIN idioma i ON i.codigo = t.idioma
ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;

INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id, d.simples, d.detalhada, d.formal,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM (VALUES
    ('quebrar o galho na rua', 'pt-BR', 'Arrumar um jeito de ganhar dinheiro no dia a dia.', '"Corre" é a atividade informal de quem se vira. "Tô no meu corre" quer dizer que a pessoa está trabalhando, mesmo sem emprego formal.', NULL),
    ('de bobeira', 'pt-BR', 'Sem fazer nada, distraído.', '"Tava de bobeira" é a explicação de quem foi pego desatento.', NULL),
    ('role da vida', 'pt-BR', 'O jeito como a vida vai levando a pessoa.', '"O rolê da vida é assim" é conformismo bem-humorado com o que não dá para mudar.', NULL),
    ('pa', 'pt-BR', '"E coisa e tal", para encerrar uma lista sem terminar.', '"Fomos lá, comemos, e pá" resume o resto sem detalhar. Muito comum na fala do Rio de Janeiro.', NULL),
    ('sangue bom', 'pt-BR', 'Pessoa boa, de confiança.', 'Elogio forte e antigo, ainda muito usado. "Ele é sangue bom" atesta o caráter de alguém.', 'pessoa de confiança'),
    ('morou', 'pt-BR', '"Entendeu?", ou "combinado".', 'Serve como pergunta no fim da frase e como resposta de concordância.', NULL),
    ('pilantragem', 'pt-BR', 'Malandragem desonesta.', 'Diferente de "malandragem", que pode ser esperteza admirada. "Pilantragem" é sempre negativo.', NULL),
    ('fita', 'pt-BR', 'Assunto, situação ou confusão.', '"Que fita é essa?" pergunta o que está acontecendo. "Fita séria" é problema grande.', NULL),
    ('de responsa', 'pt-BR', 'De qualidade, ou de confiança.', 'Vale para pessoa e para coisa: "um lanche de responsa" é um lanche muito bom.', NULL),
    ('trocar ideia', 'pt-BR', 'Conversar com alguém, de bate-papo ou a sério.', '"Vamo trocar uma ideia" pode abrir uma conversa leve ou uma conversa difícil, e é o tom de quem convida que decide qual das duas.', NULL),
    ('pegar a visao', 'pt-BR', 'Entender como as coisas funcionam.', 'Mais que compreender um fato: é entender a lógica por trás de uma situação.', 'compreender'),
    ('ta ligado', 'pt-BR', '"Você sabe do que estou falando?"', 'Usado no fim da frase o tempo todo, quase como pontuação. Raramente espera resposta.', NULL),
    ('meu chapa', 'pt-BR', 'Jeito informal de chamar alguém, sem saber ou sem usar o nome.', '"Parça" é a forma mais jovem; "chapa" soa mais antiga e é mais comum entre homens mais velhos.', NULL),
    ('no aperto', 'pt-BR', 'Com o prazo quase estourando.', '"Em cima da hora" é a variante mais comum, e descreve tanto o prazo quanto quem chega atrasado.', NULL),
    ('de ultima hora', 'pt-BR', 'Feito ou decidido no fim do prazo.', '"Mudança de última hora" é a que desorganiza o plano de todo mundo.', NULL),
    ('fim de semana emendado', 'pt-BR', 'Fim de semana esticado por um feriado.', '"Feriadão" é como se chama o resultado: três ou quatro dias seguidos de folga.', NULL),
    ('dia sim dia nao', 'pt-BR', 'Um dia sim e o seguinte não, alternando.', 'Aparece muito em instrução de remédio e de exercício.', 'em dias alternados'),
    ('toda hora', 'pt-BR', 'Com muita frequência, sem parar.', 'Costuma vir com queixa: "ele manda mensagem toda hora".', NULL),
    ('nunca mais', 'pt-BR', 'Há muito tempo, num tom de exagero.', '"Nunca mais te vejo" não quer dizer nunca: quer dizer que faz tempo demais, e é uma cobrança afetuosa.', NULL),
    ('hoje em dia', 'pt-BR', 'Na época atual, em contraste com o passado.', 'Quase sempre abre uma comparação com "antigamente".', 'atualmente'),
    ('de manha cedo', 'pt-BR', 'No começo da manhã.', '"Cedinho" é ainda mais cedo, e costuma significar antes das sete.', NULL),
    ('altas horas', 'pt-BR', 'Muito tarde da noite ou de madrugada.', '"Chegou altas horas" é reclamação de quem estava esperando.', NULL),
    ('num piscar de olhos', 'pt-BR', 'Muito rápido, quase instantâneo.', '"Rapidinho" é a forma coloquial e, como "já já", costuma demorar bem mais do que promete.', NULL),
    ('empurrar com a barriga', 'pt-BR', 'Ir adiando um problema sem resolver.', 'Diferente de enrolar alguém: aqui a pessoa está adiando para si mesma, geralmente algo que sabe que precisa fazer.', NULL),
    ('virada de ano', 'pt-BR', 'A passagem de um ano para o outro, e a festa dela.', '"Onde você vai passar a virada?" é a pergunta de dezembro no Brasil inteiro.', NULL),
    ('fora de epoca', 'pt-BR', 'Em período diferente do habitual.', 'Vale para fruta, viagem e chuva. Viajar fora de época é o jeito de gastar menos.', NULL)
) AS d(norm, idioma, simples, detalhada, formal)
JOIN idioma i ON i.codigo = d.idioma
JOIN giria g ON g.termo_normalizado = d.norm AND g.idioma_id = i.id
-- Guarda contra explicacao duplicada.
--
-- O INSERT de giria tem ON CONFLICT DO NOTHING; este nao pode ter, porque
-- acrescentar um SENTIDO NOVO a um termo existente e caso legitimo e uma
-- chave unica sobre o texto impediria isso.
--
-- Sem esta clausula, um termo que ja existe no banco (por outro lote ou
-- pelo seed escrito a mao) recebia a MESMA explicacao de novo. A migracao
-- aplicava sem reclamar, os testes passavam, e o verbete aparecia na tela
-- com a mesma frase escrita duas ou tres vezes. Foram 14 verbetes assim,
-- limpos pelas V29 e V33.
--
-- Compara pelo resumo, e nao pela linha inteira: e o resumo que a pessoa
-- le, e duas versoes do mesmo sentido com detalhes diferentes continuam
-- sendo repeticao aos olhos de quem consulta.
WHERE NOT EXISTS (
    SELECT 1 FROM definicao ja
    WHERE ja.giria_id = g.id
      AND ja.explicacao_simples = d.simples
);

INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,
                            variacao_colapsada)
SELECT g.id, v.variacao, v.norm, v.colapsado
FROM (VALUES
    ('quebrar o galho na rua', 'pt-BR', 'fazer um corre', 'fazer um corre', 'fazer um corre'),
    ('de bobeira', 'pt-BR', 'bobeando', 'bobeando', 'bobeando'),
    ('de bobeira', 'pt-BR', 'na bobeira', 'na bobeira', 'na bobeira'),
    ('role da vida', 'pt-BR', 'o rolê da vida', 'o role da vida', 'o role da vida'),
    ('pa', 'pt-BR', 'e pá', 'e pa', 'e pa'),
    ('pa', 'pt-BR', 'e tal e pá', 'e tal e pa', 'e tal e pa'),
    ('sangue bom', 'pt-BR', 'sangue-bom', 'sangue bom', 'sangue bom'),
    ('sangue bom', 'pt-BR', 'sangue bom demais', 'sangue bom demais', 'sangue bom demais'),
    ('morou', 'pt-BR', 'moró', 'moro', 'moro'),
    ('morou', 'pt-BR', 'morou?', 'morou', 'morou'),
    ('pilantragem', 'pt-BR', 'pilantra', 'pilantra', 'pilantra'),
    ('pilantragem', 'pt-BR', 'pilantrar', 'pilantrar', 'pilantrar'),
    ('fita', 'pt-BR', 'que fita', 'que fita', 'que fita'),
    ('fita', 'pt-BR', 'fita séria', 'fita seria', 'fita seria'),
    ('de responsa', 'pt-BR', 'responsa', 'responsa', 'responsa'),
    ('de responsa', 'pt-BR', 'é responsa', 'e responsa', 'e responsa'),
    ('trocar ideia', 'pt-BR', 'trocar uma ideia', 'trocar uma ideia', 'trocar uma ideia'),
    ('trocar ideia', 'pt-BR', 'trocando ideia', 'trocando ideia', 'trocando ideia'),
    ('pegar a visao', 'pt-BR', 'pegou a visao', 'pegou a visao', 'pegou a visao'),
    ('pegar a visao', 'pt-BR', 'pegar a visao', 'pegar a visao', 'pegar a visao'),
    ('ta ligado', 'pt-BR', 'ta ligado', 'ta ligado', 'ta ligado'),
    ('ta ligado', 'pt-BR', 'tá ligado?', 'ta ligado', 'ta ligado'),
    ('ta ligado', 'pt-BR', 'cê tá ligado', 'ce ta ligado', 'ce ta ligado'),
    ('meu chapa', 'pt-BR', 'chapa', 'chapa', 'chapa'),
    ('meu chapa', 'pt-BR', 'parceiro', 'parceiro', 'parceiro'),
    ('meu chapa', 'pt-BR', 'parça', 'parca', 'parca'),
    ('no aperto', 'pt-BR', 'em cima da hora', 'em cima da hora', 'em cima da hora'),
    ('no aperto', 'pt-BR', 'no sufoco', 'no sufoco', 'no sufoco'),
    ('de ultima hora', 'pt-BR', 'de ultima hora', 'de ultima hora', 'de ultima hora'),
    ('de ultima hora', 'pt-BR', 'última hora', 'ultima hora', 'ultima hora'),
    ('fim de semana emendado', 'pt-BR', 'feriadão', 'feriadao', 'feriadao'),
    ('fim de semana emendado', 'pt-BR', 'feriadao', 'feriadao', 'feriadao'),
    ('dia sim dia nao', 'pt-BR', 'dia sim dia nao', 'dia sim dia nao', 'dia sim dia nao'),
    ('dia sim dia nao', 'pt-BR', 'em dias alternados', 'em dias alternados', 'em dias alternados'),
    ('toda hora', 'pt-BR', 'a toda hora', 'a toda hora', 'a toda hora'),
    ('toda hora', 'pt-BR', 'o tempo todo', 'o tempo todo', 'o tempo todo'),
    ('nunca mais', 'pt-BR', 'faz tempo', 'faz tempo', 'faz tempo'),
    ('nunca mais', 'pt-BR', 'faz um tempão', 'faz um tempao', 'faz um tempao'),
    ('hoje em dia', 'pt-BR', 'nos dias de hoje', 'nos dias de hoje', 'nos dias de hoje'),
    ('de manha cedo', 'pt-BR', 'de manha cedo', 'de manha cedo', 'de manha cedo'),
    ('de manha cedo', 'pt-BR', 'cedinho', 'cedinho', 'cedinho'),
    ('altas horas', 'pt-BR', 'a essas horas', 'a essas horas', 'a essas horas'),
    ('altas horas', 'pt-BR', 'essas horas', 'essas horas', 'essas horas'),
    ('num piscar de olhos', 'pt-BR', 'num piscar', 'num piscar', 'num piscar'),
    ('num piscar de olhos', 'pt-BR', 'rapidinho', 'rapidinho', 'rapidinho'),
    ('empurrar com a barriga', 'pt-BR', 'empurrando com a barriga', 'empurrando com a barriga', 'empurrando com a barriga'),
    ('virada de ano', 'pt-BR', 'réveillon', 'reveillon', 'reveillon'),
    ('virada de ano', 'pt-BR', 'reveillon', 'reveillon', 'reveillon'),
    ('virada de ano', 'pt-BR', 'virada', 'virada', 'virada'),
    ('fora de epoca', 'pt-BR', 'fora de epoca', 'fora de epoca', 'fora de epoca'),
    ('fora de epoca', 'pt-BR', 'fora de temporada', 'fora de temporada', 'fora de temporada')
) AS v(termo_norm, idioma, variacao, norm, colapsado)
JOIN idioma i ON i.codigo = v.idioma
JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT g.id, c.id
FROM (VALUES
    ('quebrar o galho na rua', 'pt-BR', 'rua'),
    ('quebrar o galho na rua', 'pt-BR', 'trabalho'),
    ('de bobeira', 'pt-BR', 'rua'),
    ('de bobeira', 'pt-BR', 'tempo'),
    ('role da vida', 'pt-BR', 'rua'),
    ('role da vida', 'pt-BR', 'emocao'),
    ('pa', 'pt-BR', 'rua'),
    ('sangue bom', 'pt-BR', 'rua'),
    ('sangue bom', 'pt-BR', 'elogio'),
    ('morou', 'pt-BR', 'rua'),
    ('pilantragem', 'pt-BR', 'rua'),
    ('pilantragem', 'pt-BR', 'critica'),
    ('fita', 'pt-BR', 'rua'),
    ('de responsa', 'pt-BR', 'rua'),
    ('de responsa', 'pt-BR', 'elogio'),
    ('trocar ideia', 'pt-BR', 'rua'),
    ('trocar ideia', 'pt-BR', 'acao'),
    ('pegar a visao', 'pt-BR', 'rua'),
    ('pegar a visao', 'pt-BR', 'acao'),
    ('ta ligado', 'pt-BR', 'rua'),
    ('meu chapa', 'pt-BR', 'rua'),
    ('no aperto', 'pt-BR', 'tempo'),
    ('no aperto', 'pt-BR', 'trabalho'),
    ('de ultima hora', 'pt-BR', 'tempo'),
    ('fim de semana emendado', 'pt-BR', 'tempo'),
    ('dia sim dia nao', 'pt-BR', 'tempo'),
    ('toda hora', 'pt-BR', 'tempo'),
    ('nunca mais', 'pt-BR', 'tempo'),
    ('nunca mais', 'pt-BR', 'humor'),
    ('hoje em dia', 'pt-BR', 'tempo'),
    ('de manha cedo', 'pt-BR', 'tempo'),
    ('altas horas', 'pt-BR', 'tempo'),
    ('num piscar de olhos', 'pt-BR', 'tempo'),
    ('empurrar com a barriga', 'pt-BR', 'tempo'),
    ('empurrar com a barriga', 'pt-BR', 'critica'),
    ('virada de ano', 'pt-BR', 'tempo'),
    ('virada de ano', 'pt-BR', 'familia'),
    ('fora de epoca', 'pt-BR', 'tempo')
) AS gc(termo_norm, idioma, categoria)
JOIN idioma i ON i.codigo = gc.idioma
JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id
JOIN categoria c ON c.slug = gc.categoria
ON CONFLICT DO NOTHING;

