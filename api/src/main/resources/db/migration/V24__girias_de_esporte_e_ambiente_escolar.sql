-- ---------------------------------------------------------------------------
-- girias de esporte e ambiente escolar
--
-- 30 verbetes, 30 sentidos.
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
    ('pendurado', 'pendurado', 'pendurado', 'pt-BR', FALSE, FALSE),
    ('var', 'var', 'var', 'pt-BR', FALSE, FALSE),
    ('dar chapéu', 'dar chapeu', 'dar chapeu', 'pt-BR', FALSE, FALSE),
    ('matar no peito', 'matar no peito', 'matar no peito', 'pt-BR', FALSE, FALSE),
    ('craque', 'craque', 'craque', 'pt-BR', FALSE, FALSE),
    ('perna de pau', 'perna de pau', 'perna de pau', 'pt-BR', FALSE, FALSE),
    ('gol contra', 'gol contra', 'gol contra', 'pt-BR', FALSE, FALSE),
    ('virar o jogo', 'virar o jogo', 'virar o jogo', 'pt-BR', FALSE, FALSE),
    ('tirar o time de campo', 'tirar o time de campo', 'tirar o time de campo', 'pt-BR', FALSE, FALSE),
    ('pendura no travessão', 'pendura no travessao', 'pendura no travessao', 'pt-BR', FALSE, FALSE),
    ('clássico', 'classico', 'classico', 'pt-BR', FALSE, FALSE),
    ('zebra', 'zebra', 'zebra', 'pt-BR', FALSE, FALSE),
    ('camisa 10', 'camisa 10', 'camisa 10', 'pt-BR', FALSE, FALSE),
    ('banco', 'banco', 'banco', 'pt-BR', FALSE, FALSE),
    ('treta de arquibancada', 'treta de arquibancada', 'treta de arquibancada', 'pt-BR', FALSE, FALSE),
    ('bater um bolão', 'bater um bolao', 'bater um bolao', 'pt-BR', FALSE, FALSE),
    ('prova relâmpago', 'prova relampago', 'prova relampago', 'pt-BR', FALSE, FALSE),
    ('recuperação', 'recuperacao', 'recuperacao', 'pt-BR', FALSE, FALSE),
    ('trabalho em grupo', 'trabalho em grupo', 'trabalho em grupo', 'pt-BR', FALSE, FALSE),
    ('nerd', 'nerd', 'nerd', 'en', FALSE, FALSE),
    ('cdf', 'cdf', 'cdf', 'pt-BR', FALSE, FALSE),
    ('pé de sala', 'pe de sala', 'pe de sala', 'pt-BR', FALSE, FALSE),
    ('chamada', 'chamada', 'chamada', 'pt-BR', FALSE, FALSE),
    ('aula vaga', 'aula vaga', 'aula vaga', 'pt-BR', FALSE, FALSE),
    ('ficar de dp', 'ficar de dp', 'ficar de dp', 'pt-BR', FALSE, FALSE),
    ('passar raspando', 'passar raspando', 'passar raspando', 'pt-BR', FALSE, FALSE),
    ('tirar de letra', 'tirar de letra', 'tirar de letra', 'pt-BR', FALSE, FALSE),
    ('queimar o filme', 'queimar o filme', 'queimar o filme', 'pt-BR', FALSE, FALSE),
    ('grupo da sala', 'grupo da sala', 'grupo da sala', 'pt-BR', FALSE, FALSE),
    ('intervalo', 'intervalo', 'intervalo', 'pt-BR', FALSE, FALSE)
) AS t(termo, norm, colapsado, idioma, nsfw, risco)
JOIN idioma i ON i.codigo = t.idioma
ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;

INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id, d.simples, d.detalhada, d.formal,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM (VALUES
    ('pendurado', 'pt-BR', 'Jogador que leva mais um cartão amarelo e fica fora do próximo jogo.', '"Está pendurado" avisa que ele precisa se cuidar em campo. Fora do futebol, virou jeito de dizer que a pessoa está por um fio.', NULL),
    ('var', 'pt-BR', 'O árbitro de vídeo, que revê lances pela televisão.', 'Sigla de "video assistant referee". "Foi pro VAR" quer dizer que o lance está sendo revisto e o jogo parou.', NULL),
    ('dar chapeu', 'pt-BR', 'Passar a bola por cima do adversário e pegá-la do outro lado.', 'Drible clássico. Fora do campo, "dar um chapéu" em alguém é enganar essa pessoa, e esse uso é bem mais comum hoje.', NULL),
    ('matar no peito', 'pt-BR', 'Parar a bola usando o peito para dominá-la.', '"Matar" a bola é fazer ela parar. Vale também no peito, na coxa e no pé.', NULL),
    ('craque', 'pt-BR', 'Jogador muito bom.', 'Palavra antiga e ainda corrente. Fora do esporte, elogia quem é excelente em qualquer coisa.', 'grande jogador'),
    ('perna de pau', 'pt-BR', 'Jogador ruim, sem habilidade.', 'Crítica direta, e o oposto exato de craque.', NULL),
    ('gol contra', 'pt-BR', 'Gol marcado pelo jogador no próprio time.', 'Fora do futebol, virou expressão para quando alguém se prejudica sozinho.', NULL),
    ('virar o jogo', 'pt-BR', 'Estar perdendo e passar a ganhar.', 'Muito usada fora do esporte, para qualquer situação que se inverte a favor de alguém.', NULL),
    ('tirar o time de campo', 'pt-BR', 'Desistir de uma situação para não se prejudicar.', '"Tirei meu time de campo" é dizer que abandonou uma disputa ou um relacionamento antes de piorar.', NULL),
    ('pendura no travessao', 'pt-BR', 'Chute que bate na trave e não entra.', '"Foi na trave" também descreve qualquer coisa que quase deu certo.', NULL),
    ('classico', 'pt-BR', 'Jogo entre dois times rivais da mesma cidade ou estado.', 'Tem peso maior que um jogo comum, independentemente da posição na tabela.', NULL),
    ('zebra', 'pt-BR', 'Resultado inesperado, em que o pior ganha do favorito.', '"Deu zebra" saiu do esporte e hoje descreve qualquer surpresa ruim.', NULL),
    ('camisa 10', 'pt-BR', 'O jogador mais habilidoso e criativo do time.', 'Fora do campo, chamar alguém de camisa 10 é dizer que é a pessoa mais importante do grupo.', NULL),
    ('banco', 'pt-BR', 'O lugar dos jogadores que não estão jogando.', '"Ficou no banco" quer dizer que não foi escalado. Fora do esporte, é ser deixado de fora de alguma coisa.', NULL),
    ('treta de arquibancada', 'pt-BR', 'Confusão entre torcidas durante um jogo.', '"Treta" sozinho é briga ou confusão em qualquer contexto.', NULL),
    ('bater um bolao', 'pt-BR', 'Jogar futebol muito bem.', 'Expressão de quem assiste, dita como elogio depois de uma boa partida.', NULL),
    ('prova relampago', 'pt-BR', 'Prova aplicada sem aviso prévio.', 'O professor anuncia na hora, justamente para ninguém estudar só na véspera.', NULL),
    ('recuperacao', 'pt-BR', 'Prova extra para quem ficou abaixo da nota necessária.', '"Ficou de rec" é a forma curta, e é a notícia que ninguém quer dar em casa.', NULL),
    ('trabalho em grupo', 'pt-BR', 'Tarefa que vários alunos fazem juntos.', 'Virou piada recorrente na internet, pela queixa de que sempre uma pessoa faz tudo sozinha.', NULL),
    ('nerd', 'en', 'Pessoa muito interessada em estudo ou em algum assunto específico.', 'Já foi ofensa e hoje é quase sempre neutro ou até elogio, dito pela própria pessoa.', NULL),
    ('cdf', 'pt-BR', 'Aluno muito dedicado aos estudos.', 'Sigla de "cabeça de ferro". Diferente de "nerd", ainda costuma vir com uma ponta de deboche.', NULL),
    ('pe de sala', 'pt-BR', 'Aluno que passa a aula toda conversando.', 'Expressão regional, comum no Nordeste, para quem atrapalha a turma.', NULL),
    ('chamada', 'pt-BR', 'O momento em que o professor confere quem está presente.', '"Já deu a chamada?" é a pergunta de quem chegou atrasado.', NULL),
    ('aula vaga', 'pt-BR', 'Horário sem aula, porque o professor faltou.', 'É a melhor notícia possível no meio da manhã.', NULL),
    ('ficar de dp', 'pt-BR', 'Passar de ano devendo uma matéria, que precisa ser refeita.', 'Sigla de "dependência". Comum no ensino médio e na faculdade.', NULL),
    ('passar raspando', 'pt-BR', 'Ser aprovado com a nota mínima.', 'Dito com alívio, não com orgulho. Vale também para qualquer coisa que deu certo por pouco.', NULL),
    ('tirar de letra', 'pt-BR', 'Fazer alguma coisa difícil com facilidade.', '"Tirei a prova de letra" quer dizer que foi tranquila. Vale bem além da escola.', NULL),
    ('queimar o filme', 'pt-BR', 'Estragar a reputação de alguém, ou a própria.', '"Queimei meu filme" é ter passado vergonha de um jeito que os outros vão lembrar.', NULL),
    ('grupo da sala', 'pt-BR', 'A conversa em grupo onde a turma combina tudo.', 'É onde circulam as datas de prova, as fotos do quadro e as brincadeiras. Também é onde nasce boa parte das brigas da turma.', NULL),
    ('intervalo', 'pt-BR', 'A pausa entre as aulas.', '"Recreio" é a palavra do fundamental; "intervalo" é a do médio em diante.', NULL)
) AS d(norm, idioma, simples, detalhada, formal)
JOIN idioma i ON i.codigo = d.idioma
JOIN giria g ON g.termo_normalizado = d.norm AND g.idioma_id = i.id;

INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,
                            variacao_colapsada)
SELECT g.id, v.variacao, v.norm, v.colapsado
FROM (VALUES
    ('pendurado', 'pt-BR', 'pendurada', 'pendurada', 'pendurada'),
    ('pendurado', 'pt-BR', 'pendurados', 'pendurados', 'pendurados'),
    ('var', 'pt-BR', 'o var', 'o var', 'o var'),
    ('var', 'pt-BR', 'vaar', 'vaar', 'vaar'),
    ('dar chapeu', 'pt-BR', 'dar chapeu', 'dar chapeu', 'dar chapeu'),
    ('dar chapeu', 'pt-BR', 'deu um chapéu', 'deu um chapeu', 'deu um chapeu'),
    ('matar no peito', 'pt-BR', 'matou no peito', 'matou no peito', 'matou no peito'),
    ('craque', 'pt-BR', 'craquinho', 'craquinho', 'craquinho'),
    ('perna de pau', 'pt-BR', 'pernas de pau', 'pernas de pau', 'pernas de pau'),
    ('gol contra', 'pt-BR', 'gol-contra', 'gol contra', 'gol contra'),
    ('virar o jogo', 'pt-BR', 'virou o jogo', 'virou o jogo', 'virou o jogo'),
    ('tirar o time de campo', 'pt-BR', 'tirei meu time de campo', 'tirei meu time de campo', 'tirei meu time de campo'),
    ('pendura no travessao', 'pt-BR', 'na trave', 'na trave', 'na trave'),
    ('pendura no travessao', 'pt-BR', 'pegou na trave', 'pegou na trave', 'pegou na trave'),
    ('classico', 'pt-BR', 'classico', 'classico', 'classico'),
    ('classico', 'pt-BR', 'clássicos', 'classicos', 'classicos'),
    ('zebra', 'pt-BR', 'deu zebra', 'deu zebra', 'deu zebra'),
    ('camisa 10', 'pt-BR', 'camisa dez', 'camisa dez', 'camisa dez'),
    ('banco', 'pt-BR', 'ficar no banco', 'ficar no banco', 'ficar no banco'),
    ('banco', 'pt-BR', 'banco de reservas', 'banco de reservas', 'banco de reservas'),
    ('treta de arquibancada', 'pt-BR', 'treta na arquibancada', 'treta na arquibancada', 'treta na arquibancada'),
    ('bater um bolao', 'pt-BR', 'bateu um bolao', 'bateu um bolao', 'bateu um bolao'),
    ('prova relampago', 'pt-BR', 'prova relampago', 'prova relampago', 'prova relampago'),
    ('prova relampago', 'pt-BR', 'prova surpresa', 'prova surpresa', 'prova surpresa'),
    ('recuperacao', 'pt-BR', 'recuperacao', 'recuperacao', 'recuperacao'),
    ('recuperacao', 'pt-BR', 'recupera', 'recupera', 'recupera'),
    ('recuperacao', 'pt-BR', 'rec', 'rec', 'rec'),
    ('trabalho em grupo', 'pt-BR', 'trampo em grupo', 'trampo em grupo', 'trampo em grupo'),
    ('nerd', 'en', 'nerds', 'nerds', 'nerds'),
    ('nerd', 'en', 'nerdola', 'nerdola', 'nerdola'),
    ('cdf', 'pt-BR', 'cê dê efe', 'ce de efe', 'ce de efe'),
    ('pe de sala', 'pt-BR', 'pe de sala', 'pe de sala', 'pe de sala'),
    ('chamada', 'pt-BR', 'fazer a chamada', 'fazer a chamada', 'fazer a chamada'),
    ('aula vaga', 'pt-BR', 'janela', 'janela', 'janela'),
    ('aula vaga', 'pt-BR', 'horário vago', 'horario vago', 'horario vago'),
    ('ficar de dp', 'pt-BR', 'dp', 'dp', 'dp'),
    ('ficar de dp', 'pt-BR', 'dependência', 'dependencia', 'dependencia'),
    ('passar raspando', 'pt-BR', 'passei raspando', 'passei raspando', 'passei raspando'),
    ('tirar de letra', 'pt-BR', 'tirou de letra', 'tirou de letra', 'tirou de letra'),
    ('queimar o filme', 'pt-BR', 'queimou o filme', 'queimou o filme', 'queimou o filme'),
    ('queimar o filme', 'pt-BR', 'queimar o filme de alguém', 'queimar o filme de alguem', 'queimar o filme de alguem'),
    ('grupo da sala', 'pt-BR', 'grupo da turma', 'grupo da turma', 'grupo da turma'),
    ('intervalo', 'pt-BR', 'recreio', 'recreio', 'recreio')
) AS v(termo_norm, idioma, variacao, norm, colapsado)
JOIN idioma i ON i.codigo = v.idioma
JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT g.id, c.id
FROM (VALUES
    ('pendurado', 'pt-BR', 'esporte'),
    ('var', 'pt-BR', 'esporte'),
    ('dar chapeu', 'pt-BR', 'esporte'),
    ('dar chapeu', 'pt-BR', 'humor'),
    ('matar no peito', 'pt-BR', 'esporte'),
    ('craque', 'pt-BR', 'esporte'),
    ('craque', 'pt-BR', 'elogio'),
    ('perna de pau', 'pt-BR', 'esporte'),
    ('perna de pau', 'pt-BR', 'critica'),
    ('gol contra', 'pt-BR', 'esporte'),
    ('virar o jogo', 'pt-BR', 'esporte'),
    ('virar o jogo', 'pt-BR', 'acao'),
    ('tirar o time de campo', 'pt-BR', 'esporte'),
    ('tirar o time de campo', 'pt-BR', 'acao'),
    ('pendura no travessao', 'pt-BR', 'esporte'),
    ('classico', 'pt-BR', 'esporte'),
    ('zebra', 'pt-BR', 'esporte'),
    ('camisa 10', 'pt-BR', 'esporte'),
    ('camisa 10', 'pt-BR', 'elogio'),
    ('banco', 'pt-BR', 'esporte'),
    ('treta de arquibancada', 'pt-BR', 'esporte'),
    ('treta de arquibancada', 'pt-BR', 'critica'),
    ('bater um bolao', 'pt-BR', 'esporte'),
    ('bater um bolao', 'pt-BR', 'elogio'),
    ('prova relampago', 'pt-BR', 'escolar'),
    ('recuperacao', 'pt-BR', 'escolar'),
    ('trabalho em grupo', 'pt-BR', 'escolar'),
    ('trabalho em grupo', 'pt-BR', 'humor'),
    ('nerd', 'en', 'escolar'),
    ('nerd', 'en', 'descricao'),
    ('cdf', 'pt-BR', 'escolar'),
    ('cdf', 'pt-BR', 'descricao'),
    ('pe de sala', 'pt-BR', 'escolar'),
    ('pe de sala', 'pt-BR', 'humor'),
    ('chamada', 'pt-BR', 'escolar'),
    ('aula vaga', 'pt-BR', 'escolar'),
    ('ficar de dp', 'pt-BR', 'escolar'),
    ('passar raspando', 'pt-BR', 'escolar'),
    ('passar raspando', 'pt-BR', 'humor'),
    ('tirar de letra', 'pt-BR', 'escolar'),
    ('tirar de letra', 'pt-BR', 'elogio'),
    ('queimar o filme', 'pt-BR', 'escolar'),
    ('queimar o filme', 'pt-BR', 'critica'),
    ('grupo da sala', 'pt-BR', 'escolar'),
    ('grupo da sala', 'pt-BR', 'redes'),
    ('intervalo', 'pt-BR', 'escolar')
) AS gc(termo_norm, idioma, categoria)
JOIN idioma i ON i.codigo = gc.idioma
JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id
JOIN categoria c ON c.slug = gc.categoria
ON CONFLICT DO NOTHING;

