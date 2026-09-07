-- ---------------------------------------------------------------------------
-- recursos e girias do instagram
--
-- 18 verbetes, 18 sentidos.
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
    ('story', 'story', 'story', 'en', FALSE, FALSE),
    ('reels', 'reels', 'reels', 'en', FALSE, FALSE),
    ('bio', 'bio', 'bio', 'pt-BR', FALSE, FALSE),
    ('seguir de volta', 'seguir de volta', 'seguir de volta', 'pt-BR', FALSE, FALSE),
    ('silenciar', 'silenciar', 'silenciar', 'pt-BR', FALSE, FALSE),
    ('restringir', 'restringir', 'restringir', 'pt-BR', FALSE, FALSE),
    ('shadowban', 'shadowban', 'shadowban', 'en', FALSE, FALSE),
    ('melhores amigos', 'melhores amigos', 'melhores amigos', 'pt-BR', FALSE, FALSE),
    ('print do story', 'print do story', 'print do story', 'pt-BR', FALSE, FALSE),
    ('close amigo', 'close amigo', 'close amigo', 'pt-BR', FALSE, FALSE),
    ('engajar no story', 'engajar no story', 'engajar no story', 'pt-BR', FALSE, FALSE),
    ('melhor foto do rolo', 'melhor foto do rolo', 'melhor foto do rolo', 'pt-BR', FALSE, FALSE),
    ('carrossel', 'carrossel', 'carrossel', 'pt-BR', FALSE, FALSE),
    ('prints e recibos', 'prints e recibos', 'prints e recibos', 'pt-BR', FALSE, FALSE),
    ('trazer o feed de volta', 'trazer o feed de volta', 'trazer o feed de volta', 'pt-BR', FALSE, FALSE),
    ('hard launch', 'hard launch', 'hard launch', 'en', FALSE, FALSE),
    ('spam de story', 'spam de story', 'spam de story', 'pt-BR', FALSE, FALSE),
    ('arquivar', 'arquivar', 'arquivar', 'pt-BR', FALSE, FALSE)
) AS t(termo, norm, colapsado, idioma, nsfw, risco)
JOIN idioma i ON i.codigo = t.idioma
ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;

INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id, d.simples, d.detalhada, d.formal,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM (VALUES
    ('story', 'en', 'Publicação que some sozinha depois de 24 horas.', 'Fica no topo da tela, em círculos com a foto de cada pessoa. É onde se publica o dia a dia, justamente porque não fica guardado no perfil.', NULL),
    ('reels', 'en', 'Vídeos curtos na vertical, que passam um depois do outro.', 'É a resposta do Instagram ao TikTok, com o mesmo formato. Diferente do story, o reels fica salvo e pode ser mostrado a quem não segue o perfil.', NULL),
    ('bio', 'pt-BR', 'O texto curto que descreve o perfil, logo abaixo do nome.', '"Link na bio" manda procurar ali o endereço que a pessoa não pode colocar no post.', NULL),
    ('seguir de volta', 'pt-BR', 'Passar a seguir quem começou a seguir você.', 'Entre adolescentes, não seguir de volta é lido como recado, e vira assunto.', NULL),
    ('silenciar', 'pt-BR', 'Continuar seguindo alguém, mas parar de ver as publicações dessa pessoa.', 'A pessoa silenciada não fica sabendo. É a saída de quem não quer ver o conteúdo e nem criar constrangimento deixando de seguir.', NULL),
    ('restringir', 'pt-BR', 'Limitar alguém sem bloquear: os comentários dessa pessoa só aparecem para ela mesma.', 'Foi criado como ferramenta contra perseguição: permite conter alguém insistente sem que essa pessoa perceba, o que evita a reação que um bloqueio provocaria.', NULL),
    ('shadowban', 'en', 'Quando a plataforma reduz o alcance de um perfil sem avisar.', 'A pessoa continua publicando normalmente e quase ninguém vê. Como não há aviso nem confirmação, é impossível ter certeza, e o termo é usado tanto para casos reais quanto como desculpa.', NULL),
    ('melhores amigos', 'pt-BR', 'Estar na lista restrita de alguém no Instagram.', '"Me põe no close" é pedir para entrar. Entre adolescentes, entrar e sair dessa lista é um sinal social forte.', NULL),
    ('print do story', 'pt-BR', 'Salvar a imagem de um story antes de ele sumir.', 'Muita discussão nasce daí: o story some em 24 horas, o print não.', NULL),
    ('close amigo', 'pt-BR', 'Alguém que está na sua lista de amigos próximos.', '"Saiu do close" virou expressão para amizade que esfriou, mesmo fora do aplicativo.', NULL),
    ('engajar no story', 'pt-BR', 'Usar enquete, pergunta ou figurinha para o público interagir.', 'Não é só brincadeira: quanto mais gente responde, mais a plataforma mostra os próximos stories daquele perfil.', NULL),
    ('melhor foto do rolo', 'pt-BR', 'A foto escolhida entre dezenas parecidas para ser publicada.', '"Rolo" aqui é a galeria do celular. A piada é a distância entre a foto publicada e as outras trinta iguais que ficaram para trás.', NULL),
    ('carrossel', 'pt-BR', 'Publicação com várias fotos, que se vê deslizando o dedo para o lado.', '"Arrasta pro lado" é o convite para ver as próximas. Serve para contar uma sequência num post só.', NULL),
    ('prints e recibos', 'pt-BR', 'Publicar capturas de tela de conversas para provar alguma coisa.', 'É o formato mais comum de briga pública na internet. Vale lembrar que print é fácil de recortar e de forjar.', NULL),
    ('trazer o feed de volta', 'pt-BR', 'Voltar a publicar depois de um tempo sem aparecer.', '"Sumiu do insta" é notado e cobrado, principalmente entre quem publicava com frequência.', NULL),
    ('hard launch', 'en', 'Assumir publicamente um namoro numa publicação.', '"Soft launch" é o contrário: mostrar só uma mão ou um pedaço da pessoa, deixando o público adivinhar antes de assumir de vez.', NULL),
    ('spam de story', 'pt-BR', 'Publicar muitos stories seguidos.', 'Reclamação comum: quem faz ocupa a fila inteira de quem só queria ver os stories dos outros.', NULL),
    ('arquivar', 'pt-BR', 'Tirar uma publicação do perfil sem apagá-la.', 'Ela some para todo mundo e continua guardada só para você. É o que se faz depois de um término, antes de decidir se apaga.', NULL)
) AS d(norm, idioma, simples, detalhada, formal)
JOIN idioma i ON i.codigo = d.idioma
JOIN giria g ON g.termo_normalizado = d.norm AND g.idioma_id = i.id;

INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,
                            variacao_colapsada)
SELECT g.id, v.variacao, v.norm, v.colapsado
FROM (VALUES
    ('story', 'en', 'stories', 'stories', 'stories'),
    ('story', 'en', 'storyzinho', 'storyzinho', 'storyzinho'),
    ('story', 'en', 'postar story', 'postar story', 'postar story'),
    ('reels', 'en', 'reel', 'reel', 'reel'),
    ('reels', 'en', 'reelzinho', 'reelzinho', 'reelzinho'),
    ('reels', 'en', 'reelz', 'reelz', 'reelz'),
    ('bio', 'pt-BR', 'biografia', 'biografia', 'biografia'),
    ('bio', 'pt-BR', 'na bio', 'na bio', 'na bio'),
    ('seguir de volta', 'pt-BR', 'follow back', 'follow back', 'follow back'),
    ('seguir de volta', 'pt-BR', 'seguiu de volta', 'seguiu de volta', 'seguiu de volta'),
    ('seguir de volta', 'pt-BR', 'não seguiu de volta', 'nao seguiu de volta', 'nao seguiu de volta'),
    ('silenciar', 'pt-BR', 'mutar', 'mutar', 'mutar'),
    ('silenciar', 'pt-BR', 'silenciei', 'silenciei', 'silenciei'),
    ('silenciar', 'pt-BR', 'mute', 'mute', 'mute'),
    ('restringir', 'pt-BR', 'restrito', 'restrito', 'restrito'),
    ('restringir', 'pt-BR', 'restringiu', 'restringiu', 'restringiu'),
    ('shadowban', 'en', 'shadow ban', 'shadow ban', 'shadow ban'),
    ('shadowban', 'en', 'shadowbanado', 'shadowbanado', 'shadowbanado'),
    ('melhores amigos', 'pt-BR', 'lista de melhores amigos', 'lista de melhores amigos', 'lista de melhores amigos'),
    ('melhores amigos', 'pt-BR', 'tá no close?', 'ta no close', 'ta no close'),
    ('print do story', 'pt-BR', 'printar o story', 'printar o story', 'printar o story'),
    ('print do story', 'pt-BR', 'deu print', 'deu print', 'deu print'),
    ('close amigo', 'pt-BR', 'closezinho', 'closezinho', 'closezinho'),
    ('close amigo', 'pt-BR', 'sair do close', 'sair do close', 'sair do close'),
    ('engajar no story', 'pt-BR', 'caixinha de perguntas', 'caixinha de perguntas', 'caixinha de perguntas'),
    ('engajar no story', 'pt-BR', 'enquete', 'enquete', 'enquete'),
    ('melhor foto do rolo', 'pt-BR', 'foto do rolo', 'foto do rolo', 'foto do rolo'),
    ('melhor foto do rolo', 'pt-BR', 'rolo da câmera', 'rolo da camera', 'rolo da camera'),
    ('carrossel', 'pt-BR', 'post em carrossel', 'post em carrossel', 'post em carrossel'),
    ('carrossel', 'pt-BR', 'arrasta pro lado', 'arrasta pro lado', 'arrasta pro lado'),
    ('prints e recibos', 'pt-BR', 'mostrar os prints', 'mostrar os prints', 'mostrar os prints'),
    ('prints e recibos', 'pt-BR', 'expor os prints', 'expor os prints', 'expor os prints'),
    ('trazer o feed de volta', 'pt-BR', 'voltar a postar', 'voltar a postar', 'voltar a postar'),
    ('trazer o feed de volta', 'pt-BR', 'sumido do insta', 'sumido do insta', 'sumido do insta'),
    ('hard launch', 'en', 'soft launch', 'soft launch', 'soft launch'),
    ('hard launch', 'en', 'lançar o namoro', 'lancar o namoro', 'lancar o namoro'),
    ('spam de story', 'pt-BR', 'spamar story', 'spamar story', 'spamar story'),
    ('spam de story', 'pt-BR', 'storyzada', 'storyzada', 'storyzada'),
    ('arquivar', 'pt-BR', 'arquivei', 'arquivei', 'arquivei'),
    ('arquivar', 'pt-BR', 'post arquivado', 'post arquivado', 'post arquivado')
) AS v(termo_norm, idioma, variacao, norm, colapsado)
JOIN idioma i ON i.codigo = v.idioma
JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT g.id, c.id
FROM (VALUES
    ('story', 'en', 'redes'),
    ('reels', 'en', 'redes'),
    ('bio', 'pt-BR', 'redes'),
    ('seguir de volta', 'pt-BR', 'redes'),
    ('seguir de volta', 'pt-BR', 'relacionamento'),
    ('silenciar', 'pt-BR', 'redes'),
    ('silenciar', 'pt-BR', 'relacionamento'),
    ('restringir', 'pt-BR', 'redes'),
    ('restringir', 'pt-BR', 'atencao'),
    ('shadowban', 'en', 'redes'),
    ('shadowban', 'en', 'critica'),
    ('melhores amigos', 'pt-BR', 'redes'),
    ('melhores amigos', 'pt-BR', 'relacionamento'),
    ('print do story', 'pt-BR', 'redes'),
    ('close amigo', 'pt-BR', 'redes'),
    ('close amigo', 'pt-BR', 'humor'),
    ('engajar no story', 'pt-BR', 'redes'),
    ('engajar no story', 'pt-BR', 'trabalho'),
    ('melhor foto do rolo', 'pt-BR', 'redes'),
    ('melhor foto do rolo', 'pt-BR', 'humor'),
    ('carrossel', 'pt-BR', 'redes'),
    ('prints e recibos', 'pt-BR', 'redes'),
    ('prints e recibos', 'pt-BR', 'critica'),
    ('trazer o feed de volta', 'pt-BR', 'redes'),
    ('trazer o feed de volta', 'pt-BR', 'humor'),
    ('hard launch', 'en', 'redes'),
    ('hard launch', 'en', 'relacionamento'),
    ('spam de story', 'pt-BR', 'redes'),
    ('spam de story', 'pt-BR', 'critica'),
    ('arquivar', 'pt-BR', 'redes')
) AS gc(termo_norm, idioma, categoria)
JOIN idioma i ON i.codigo = gc.idioma
JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id
JOIN categoria c ON c.slug = gc.categoria
ON CONFLICT DO NOTHING;

