-- ---------------------------------------------------------------------------
-- girias do tiktok
--
-- 17 verbetes, 17 sentidos.
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
    ('dueto', 'dueto', 'dueto', 'pt-BR', FALSE, FALSE),
    ('costura', 'costura', 'costura', 'pt-BR', FALSE, FALSE),
    ('som', 'som', 'som', 'pt-BR', FALSE, FALSE),
    ('floptok', 'floptok', 'floptok', 'en', FALSE, FALSE),
    ('algoritmado', 'algoritmado', 'algoritmado', 'pt-BR', FALSE, FALSE),
    ('bed rot', 'bed rot', 'bed rot', 'en', FALSE, FALSE),
    ('6-7', '6 7', '6 7', 'en', FALSE, FALSE),
    ('aura', 'aura', 'aura', 'pt-BR', FALSE, FALSE),
    ('cooked', 'cooked', 'cooked', 'en', FALSE, FALSE),
    ('lock in', 'lock in', 'lock in', 'en', FALSE, FALSE),
    ('glazing', 'glazing', 'glazing', 'en', FALSE, FALSE),
    ('clout', 'clout', 'clout', 'en', FALSE, FALSE),
    ('the ick', 'the ick', 'the ick', 'en', FALSE, FALSE),
    ('left on read', 'left on read', 'left on read', 'en', FALSE, FALSE),
    ('looksmaxxing', 'looksmaxxing', 'looksmaxxing', 'en', FALSE, TRUE),
    ('crash out', 'crash out', 'crash out', 'en', FALSE, FALSE),
    ('vibe check', 'vibe check', 'vibe check', 'en', FALSE, FALSE)
) AS t(termo, norm, colapsado, idioma, nsfw, risco)
JOIN idioma i ON i.codigo = t.idioma
ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;

INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id, d.simples, d.detalhada, d.formal,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM (VALUES
    ('dueto', 'pt-BR', 'Vídeo gravado ao lado do vídeo de outra pessoa, na mesma tela.', 'A tela fica dividida em duas. Serve para responder, completar ou debochar do vídeo original.', NULL),
    ('costura', 'pt-BR', 'Pegar um pedaço do vídeo de outra pessoa e continuar a partir dele.', 'Diferente do dueto: aqui o trecho aparece primeiro e o seu vídeo vem depois, e não lado a lado.', NULL),
    ('som', 'pt-BR', 'O áudio de um vídeo, que outras pessoas podem reaproveitar.', '"Usar esse som" é gravar o próprio vídeo com aquele áudio. É assim que uma música ou uma frase se espalha pela plataforma.', NULL),
    ('floptok', 'en', 'A fase em que os vídeos de alguém quase não são vistos.', '"Flopar" é fracassar. "Tô no floptok" é reclamar, com humor, de que o sistema parou de mostrar seus vídeos.', NULL),
    ('algoritmado', 'pt-BR', 'Conteúdo que parece existir só para agradar ao sistema de recomendação.', 'É crítica: diz que aquilo foi calculado para prender atenção, e não para ter alguma coisa a dizer.', NULL),
    ('bed rot', 'en', 'Passar horas na cama no celular, sem fazer nada.', 'Descrito como descanso por quem faz e como sinal de desânimo por quem observa. Quando vira rotina de todo dia, vale conversar sem repreender.', NULL),
    ('6 7', 'en', 'Bordão que não significa nada, dito para interromper e fazer graça.', 'Vem acompanhado de um gesto com as duas mãos viradas para cima, subindo e descendo alternadamente. A graça está justamente em não ter sentido, e responder "o que isso quer dizer?" é parte da brincadeira. Foi eleita a palavra do ano de 2025 pelo Dictionary.com.', NULL),
    ('aura', 'pt-BR', 'A presença e o carisma de alguém, contados como se fossem pontos.', '"Ganhou aura" é ter feito algo impressionante; "perdeu aura" é ter passado vergonha. "Farmar aura" é agir de propósito para parecer interessante, e é quase sempre dito com deboche.', NULL),
    ('cooked', 'en', 'Ferrado, sem saída.', '"Tô cooked" é dizer que se deu mal, geralmente antes de uma prova ou de uma conversa difícil.', NULL),
    ('lock in', 'en', 'Focar de verdade em alguma coisa.', '"Preciso lockar" é anunciar que vai parar de se distrair e estudar ou trabalhar para valer.', NULL),
    ('glazing', 'en', 'Elogiar alguém de forma exagerada, a ponto de constranger.', 'É acusação: quem faz glazing está defendendo alguém além do razoável, e o chat cobra por isso.', NULL),
    ('clout', 'en', 'Fama e influência na internet.', '"Fazer por clout" acusa alguém de agir só para ganhar atenção, e não por acreditar no que faz.', NULL),
    ('the ick', 'en', 'Quando um detalhe bobo faz a atração por alguém desaparecer de repente.', 'Pode ser qualquer coisa mínima, e a pessoa mesma costuma achar o motivo irracional.', NULL),
    ('left on read', 'en', 'Alguém leu a sua mensagem e não respondeu.', 'No Brasil se diz "deu vácuo" ou "me deixou no vácuo", com o mesmo sentido.', NULL),
    ('looksmaxxing', 'en', 'Tentar melhorar ao máximo a própria aparência.', 'Vai de cuidados com a pele e postura até dietas e procedimentos. Circula em comunidades que cobram padrões muito rígidos, e por isso costuma vir junto de comparação e insatisfação com o corpo. Vale prestar atenção quando aparece com frequência.', 'busca por aparência ideal'),
    ('crash out', 'en', 'Perder o controle emocional e agir por impulso.', '"Vou crashar" avisa que a pessoa está no limite. Costuma ser dito com humor, mas quando se repete é sinal de que algo não está bem.', NULL),
    ('vibe check', 'en', 'Avaliar rapidamente o clima de uma pessoa ou de um lugar.', '"Passou no vibe check" é ter causado boa impressão.', NULL)
) AS d(norm, idioma, simples, detalhada, formal)
JOIN idioma i ON i.codigo = d.idioma
JOIN giria g ON g.termo_normalizado = d.norm AND g.idioma_id = i.id;

INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,
                            variacao_colapsada)
SELECT g.id, v.variacao, v.norm, v.colapsado
FROM (VALUES
    ('dueto', 'pt-BR', 'duetar', 'duetar', 'duetar'),
    ('dueto', 'pt-BR', 'duet', 'duet', 'duet'),
    ('dueto', 'pt-BR', 'duetou', 'duetou', 'duetou'),
    ('costura', 'pt-BR', 'stitch', 'stitch', 'stitch'),
    ('costura', 'pt-BR', 'costurar', 'costurar', 'costurar'),
    ('som', 'pt-BR', 'usar o som', 'usar o som', 'usar o som'),
    ('som', 'pt-BR', 'áudio original', 'audio original', 'audio original'),
    ('floptok', 'en', 'flopar', 'flopar', 'flopar'),
    ('floptok', 'en', 'flopou', 'flopou', 'flopou'),
    ('floptok', 'en', 'flop', 'flop', 'flop'),
    ('algoritmado', 'pt-BR', 'algoritmada', 'algoritmada', 'algoritmada'),
    ('algoritmado', 'pt-BR', 'feito pro algoritmo', 'feito pro algoritmo', 'feito pro algoritmo'),
    ('bed rot', 'en', 'bedrot', 'bedrot', 'bedrot'),
    ('bed rot', 'en', 'apodrecer na cama', 'apodrecer na cama', 'apodrecer na cama'),
    ('6 7', 'en', '67', '67', '67'),
    ('6 7', 'en', 'six seven', 'six seven', 'six seven'),
    ('6 7', 'en', 'seis sete', 'seis sete', 'seis sete'),
    ('aura', 'pt-BR', 'farmar aura', 'farmar aura', 'farmar aura'),
    ('aura', 'pt-BR', 'aura points', 'aura points', 'aura points'),
    ('aura', 'pt-BR', 'perdeu aura', 'perdeu aura', 'perdeu aura'),
    ('cooked', 'en', 'we''re cooked', 'we re cooked', 'we re cooked'),
    ('cooked', 'en', 'tá cooked', 'ta cooked', 'ta cooked'),
    ('cooked', 'en', 'cozinhado', 'cozinhado', 'cozinhado'),
    ('lock in', 'en', 'lockin', 'lockin', 'lockin'),
    ('lock in', 'en', 'lockado', 'lockado', 'lockado'),
    ('lock in', 'en', 'vou lockar', 'vou lockar', 'vou lockar'),
    ('glazing', 'en', 'glazar', 'glazar', 'glazar'),
    ('glazing', 'en', 'glazando', 'glazando', 'glazando'),
    ('glazing', 'en', 'glaze', 'glaze', 'glaze'),
    ('clout', 'en', 'cloutzeiro', 'cloutzeiro', 'cloutzeiro'),
    ('clout', 'en', 'atrás de clout', 'atras de clout', 'atras de clout'),
    ('the ick', 'en', 'ick', 'ick', 'ick'),
    ('the ick', 'en', 'deu ick', 'deu ick', 'deu ick'),
    ('the ick', 'en', 'bateu o ick', 'bateu o ick', 'bateu o ick'),
    ('left on read', 'en', 'visualizou e não respondeu', 'visualizou e nao respondeu', 'visualizou e nao respondeu'),
    ('left on read', 'en', 'vácuo', 'vacuo', 'vacuo'),
    ('looksmaxxing', 'en', 'looksmax', 'looksmax', 'looksmax'),
    ('looksmaxxing', 'en', 'maxxing', 'maxxing', 'maxxing'),
    ('crash out', 'en', 'crashou', 'crashou', 'crashou'),
    ('crash out', 'en', 'crashando', 'crashando', 'crashando'),
    ('crash out', 'en', 'crash-out', 'crash out', 'crash out'),
    ('vibe check', 'en', 'checar a vibe', 'checar a vibe', 'checar a vibe')
) AS v(termo_norm, idioma, variacao, norm, colapsado)
JOIN idioma i ON i.codigo = v.idioma
JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT g.id, c.id
FROM (VALUES
    ('dueto', 'pt-BR', 'redes'),
    ('costura', 'pt-BR', 'redes'),
    ('som', 'pt-BR', 'redes'),
    ('som', 'pt-BR', 'musica'),
    ('floptok', 'en', 'redes'),
    ('floptok', 'en', 'humor'),
    ('algoritmado', 'pt-BR', 'redes'),
    ('algoritmado', 'pt-BR', 'critica'),
    ('bed rot', 'en', 'redes'),
    ('bed rot', 'en', 'emocao'),
    ('6 7', 'en', 'redes'),
    ('6 7', 'en', 'humor'),
    ('aura', 'pt-BR', 'redes'),
    ('aura', 'pt-BR', 'elogio'),
    ('cooked', 'en', 'redes'),
    ('cooked', 'en', 'emocao'),
    ('lock in', 'en', 'redes'),
    ('lock in', 'en', 'acao'),
    ('glazing', 'en', 'redes'),
    ('glazing', 'en', 'critica'),
    ('clout', 'en', 'redes'),
    ('clout', 'en', 'critica'),
    ('the ick', 'en', 'redes'),
    ('the ick', 'en', 'relacionamento'),
    ('left on read', 'en', 'redes'),
    ('left on read', 'en', 'relacionamento'),
    ('looksmaxxing', 'en', 'redes'),
    ('looksmaxxing', 'en', 'descricao'),
    ('looksmaxxing', 'en', 'atencao'),
    ('crash out', 'en', 'redes'),
    ('crash out', 'en', 'emocao'),
    ('vibe check', 'en', 'redes'),
    ('vibe check', 'en', 'emocao')
) AS gc(termo_norm, idioma, categoria)
JOIN idioma i ON i.codigo = gc.idioma
JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id
JOIN categoria c ON c.slug = gc.categoria
ON CONFLICT DO NOTHING;

