-- ---------------------------------------------------------------------------
-- girias de comida esporte e escola
--
-- 31 verbetes, 31 sentidos.
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
    ('pf', 'pf', 'pf', 'pt-BR', FALSE, FALSE),
    ('café completo', 'cafe completo', 'cafe completo', 'pt-BR', FALSE, FALSE),
    ('salgado', 'salgado', 'salgado', 'pt-BR', FALSE, FALSE),
    ('pingado', 'pingado', 'pingado', 'pt-BR', FALSE, FALSE),
    ('tirar o pé da jaca', 'tirar o pe da jaca', 'tirar o pe da jaca', 'pt-BR', FALSE, FALSE),
    ('vaquinha do lanche', 'vaquinha do lanche', 'vaquinha do lanche', 'pt-BR', FALSE, FALSE),
    ('bebum', 'bebum', 'bebum', 'pt-BR', FALSE, FALSE),
    ('levar o doce', 'levar o doce', 'levar o doce', 'pt-BR', FALSE, FALSE),
    ('pelada', 'pelada', 'pelada', 'pt-BR', FALSE, FALSE),
    ('racha', 'racha', 'racha', 'pt-BR', FALSE, FALSE),
    ('bater falta', 'bater falta', 'bater falta', 'pt-BR', FALSE, FALSE),
    ('pintura', 'pintura', 'pintura', 'pt-BR', FALSE, FALSE),
    ('frango', 'frango', 'frango', 'pt-BR', FALSE, FALSE),
    ('cair de pé', 'cair de pe', 'cair de pe', 'pt-BR', FALSE, FALSE),
    ('escalação', 'escalacao', 'escalacao', 'pt-BR', FALSE, FALSE),
    ('artilheiro', 'artilheiro', 'artilheiro', 'pt-BR', FALSE, FALSE),
    ('tabelinha', 'tabelinha', 'tabelinha', 'pt-BR', FALSE, FALSE),
    ('amarelar', 'amarelar', 'amarelar', 'pt-BR', FALSE, FALSE),
    ('de bicicleta', 'de bicicleta', 'de bicicleta', 'pt-BR', FALSE, FALSE),
    ('vestiário', 'vestiario', 'vestiario', 'pt-BR', FALSE, FALSE),
    ('chamada oral', 'chamada oral', 'chamada oral', 'pt-BR', FALSE, FALSE),
    ('média', 'media', 'media', 'pt-BR', FALSE, FALSE),
    ('bimestre', 'bimestre', 'bimestre', 'pt-BR', FALSE, FALSE),
    ('conselho de classe', 'conselho de classe', 'conselho de classe', 'pt-BR', FALSE, FALSE),
    ('colinha', 'colinha', 'colinha', 'pt-BR', FALSE, FALSE),
    ('lousa', 'lousa', 'lousa', 'pt-BR', FALSE, FALSE),
    ('carteira', 'carteira', 'carteira', 'pt-BR', FALSE, FALSE),
    ('grêmio', 'gremio', 'gremio', 'pt-BR', FALSE, FALSE),
    ('reforço', 'reforco', 'reforco', 'pt-BR', FALSE, FALSE),
    ('prova substitutiva', 'prova substitutiva', 'prova substitutiva', 'pt-BR', FALSE, FALSE),
    ('formatura', 'formatura', 'formatura', 'pt-BR', FALSE, FALSE)
) AS t(termo, norm, colapsado, idioma, nsfw, risco)
JOIN idioma i ON i.codigo = t.idioma
ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;

INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id, d.simples, d.detalhada, d.formal,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM (VALUES
    ('pf', 'pt-BR', 'O prato padrão de restaurante popular: arroz, feijão, carne e salada.', 'Sigla de "prato feito". Em algumas regiões se chama "comercial" ou "executivo".', NULL),
    ('cafe completo', 'pt-BR', 'Café da manhã com pão, queijo, bolo, frutas e mais.', 'Comum em Minas Gerais e no interior. Não é lanche: é refeição.', NULL),
    ('salgado', 'pt-BR', 'Coxinha, esfiha, pastel e afins, vendidos em padaria.', '"Salgadinho" tem dois sentidos que confundem: o de padaria e o de pacote de milho industrializado.', NULL),
    ('pingado', 'pt-BR', 'Café com um pouco de leite, servido em copo.', '"Pingado" em São Paulo, "média" no Rio, que também é o nome do pão com manteiga acompanhado. Pedido de balcão de padaria.', NULL),
    ('tirar o pe da jaca', 'pt-BR', 'Voltar a se cuidar depois de um período de exageros.', 'É o movimento contrário de "enfiar o pé na jaca", e costuma vir em janeiro.', NULL),
    ('vaquinha do lanche', 'pt-BR', 'Juntar dinheiro entre colegas para comprar comida.', 'Cena clássica de intervalo escolar e de escritório.', NULL),
    ('bebum', 'pt-BR', 'Pessoa embriagada.', 'Palavra antiga e informal. Dita a sério é ofensa; entre amigos, costuma ser brincadeira.', NULL),
    ('levar o doce', 'pt-BR', 'Contribuir com um prato quando se é convidado.', 'Regra não escrita de festa brasileira: quem é convidado pergunta o que pode levar.', NULL),
    ('pelada', 'pt-BR', 'Jogo de futebol informal entre amigos.', 'Sem árbitro, sem uniforme e sem hora certa para acabar. É como quase todo brasileiro joga bola.', NULL),
    ('racha', 'pt-BR', 'Outro nome para o futebol informal.', 'Mais comum no Sul e no Sudeste. Cuidado: "racha" também é corrida ilegal de carro, e o contexto separa.', NULL),
    ('bater falta', 'pt-BR', 'Chutar a bola parada depois de uma infração.', 'A "barreira" é a fileira de jogadores que se posta na frente para atrapalhar.', NULL),
    ('pintura', 'pt-BR', 'Gol muito bonito.', '"Golaço" é o mais comum. "Pintura" acrescenta a ideia de obra de arte.', NULL),
    ('frango', 'pt-BR', 'Falha boba do goleiro num chute fácil.', '"Frangueiro" é o goleiro que erra assim com frequência. Fora do futebol, virou sinônimo de erro grosseiro.', NULL),
    ('cair de pe', 'pt-BR', 'Perder, mas com dignidade.', 'Dito quando o time joga bem e é eliminado. Vale muito além do esporte.', NULL),
    ('escalacao', 'pt-BR', 'A lista de quem começa jogando.', 'Sai algumas horas antes da partida e é o assunto do dia entre torcedores.', NULL),
    ('artilheiro', 'pt-BR', 'Quem faz mais gols numa competição.', '"Artilharia" é a lista dos maiores goleadores.', 'goleador'),
    ('tabelinha', 'pt-BR', 'Jogada em que dois jogadores trocam passes rápidos para passar pelo adversário.', 'Fora do campo, "fazer tabelinha" é combinar algo com outra pessoa antecipadamente.', NULL),
    ('amarelar', 'pt-BR', 'Perder a coragem na hora decisiva.', 'Usado muito além do esporte, para quem desistiu de um confronto ou de uma decisão.', NULL),
    ('de bicicleta', 'pt-BR', 'Chute dado com o corpo no ar, de costas para o gol.', 'Uma das jogadas mais difíceis e mais celebradas do futebol.', NULL),
    ('vestiario', 'pt-BR', 'O lugar onde o time se troca, e por extensão o clima interno do grupo.', '"Perdeu o vestiário" quer dizer que o técnico perdeu a autoridade sobre os jogadores.', NULL),
    ('chamada oral', 'pt-BR', 'Avaliação em que o aluno responde falando, na frente da turma.', 'É a mais temida por quem tem vergonha de falar em público.', NULL),
    ('media', 'pt-BR', 'A nota necessária para passar de ano.', '"Fechei a média" é ter alcançado o mínimo. Também se diz "passei na média".', NULL),
    ('bimestre', 'pt-BR', 'O período em que o ano letivo é dividido.', 'Cada escola usa um: dois meses, três meses ou seis. As notas fecham no fim de cada um.', NULL),
    ('conselho de classe', 'pt-BR', 'Reunião dos professores para decidir quem passa e quem repete.', 'É onde um aluno na nota limite pode ser aprovado por avaliação do conjunto dos professores.', NULL),
    ('colinha', 'pt-BR', 'O papelzinho escondido com as respostas da prova.', 'Hoje o mais comum é o celular, o que mudou a fiscalização mais do que mudou a prática.', NULL),
    ('lousa', 'pt-BR', 'O quadro em que o professor escreve.', '"Lousa" em São Paulo, "quadro" na maior parte do país.', NULL),
    ('carteira', 'pt-BR', 'A mesa individual do aluno.', 'Em algumas regiões se chama "classe", o que confunde com a turma.', NULL),
    ('gremio', 'pt-BR', 'A organização de alunos que representa a turma diante da escola.', 'Tem eleição, chapa e campanha. Para muita gente, é o primeiro contato com política.', NULL),
    ('reforco', 'pt-BR', 'Aula extra para quem está com dificuldade numa matéria.', 'Pode ser oferecida pela escola ou contratada por fora.', NULL),
    ('prova substitutiva', 'pt-BR', 'Prova aplicada a quem faltou na data original.', 'Diferente da recuperação: aqui não houve nota ruim, houve ausência.', NULL),
    ('formatura', 'pt-BR', 'A cerimônia de conclusão do curso.', '"Colação de grau" é a parte oficial; o baile é a festa. As duas costumam ser em dias diferentes.', NULL)
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
    ('pf', 'pt-BR', 'prato feito', 'prato feito', 'prato feito'),
    ('pf', 'pt-BR', 'pê efe', 'pe efe', 'pe efe'),
    ('pf', 'pt-BR', 'comercial', 'comercial', 'comercial'),
    ('cafe completo', 'pt-BR', 'cafe completo', 'cafe completo', 'cafe completo'),
    ('cafe completo', 'pt-BR', 'café reforçado', 'cafe reforcado', 'cafe reforcado'),
    ('salgado', 'pt-BR', 'salgadinho', 'salgadinho', 'salgadinho'),
    ('salgado', 'pt-BR', 'salgadinhos', 'salgadinhos', 'salgadinhos'),
    ('pingado', 'pt-BR', 'cafe com leite', 'cafe com leite', 'cafe com leite'),
    ('pingado', 'pt-BR', 'café com leite', 'cafe com leite', 'cafe com leite'),
    ('tirar o pe da jaca', 'pt-BR', 'voltar pra dieta', 'voltar pra dieta', 'voltar pra dieta'),
    ('vaquinha do lanche', 'pt-BR', 'rachar o lanche', 'rachar o lanche', 'rachar o lanche'),
    ('bebum', 'pt-BR', 'bêbum', 'bebum', 'bebum'),
    ('bebum', 'pt-BR', 'bebo', 'bebo', 'bebo'),
    ('levar o doce', 'pt-BR', 'levar alguma coisa', 'levar alguma coisa', 'levar alguma coisa'),
    ('pelada', 'pt-BR', 'peladinha', 'peladinha', 'peladinha'),
    ('pelada', 'pt-BR', 'jogar uma pelada', 'jogar uma pelada', 'jogar uma pelada'),
    ('racha', 'pt-BR', 'rachão', 'rachao', 'rachao'),
    ('racha', 'pt-BR', 'rachao', 'rachao', 'rachao'),
    ('bater falta', 'pt-BR', 'cobrar falta', 'cobrar falta', 'cobrar falta'),
    ('bater falta', 'pt-BR', 'na barreira', 'na barreira', 'na barreira'),
    ('pintura', 'pt-BR', 'golaço', 'golaco', 'golaco'),
    ('pintura', 'pt-BR', 'golaco', 'golaco', 'golaco'),
    ('pintura', 'pt-BR', 'que pintura', 'que pintura', 'que pintura'),
    ('frango', 'pt-BR', 'tomou frango', 'tomou frango', 'tomou frango'),
    ('frango', 'pt-BR', 'frangueiro', 'frangueiro', 'frangueiro'),
    ('cair de pe', 'pt-BR', 'cair de pe', 'cair de pe', 'cair de pe'),
    ('cair de pe', 'pt-BR', 'sair de cabeça erguida', 'sair de cabeca erguida', 'sair de cabeca erguida'),
    ('escalacao', 'pt-BR', 'escalacao', 'escalacao', 'escalacao'),
    ('escalacao', 'pt-BR', 'escalar o time', 'escalar o time', 'escalar o time'),
    ('artilheiro', 'pt-BR', 'artilharia', 'artilharia', 'artilharia'),
    ('artilheiro', 'pt-BR', 'goleador', 'goleador', 'goleador'),
    ('tabelinha', 'pt-BR', 'dar uma tabela', 'dar uma tabela', 'dar uma tabela'),
    ('tabelinha', 'pt-BR', 'tabela', 'tabela', 'tabela'),
    ('amarelar', 'pt-BR', 'amarelou', 'amarelou', 'amarelou'),
    ('amarelar', 'pt-BR', 'amarelão', 'amarelao', 'amarelao'),
    ('de bicicleta', 'pt-BR', 'bicicleta', 'bicicleta', 'bicicleta'),
    ('de bicicleta', 'pt-BR', 'gol de bicicleta', 'gol de bicicleta', 'gol de bicicleta'),
    ('vestiario', 'pt-BR', 'vestiario', 'vestiario', 'vestiario'),
    ('vestiario', 'pt-BR', 'clima no vestiário', 'clima no vestiario', 'clima no vestiario'),
    ('chamada oral', 'pt-BR', 'arguição', 'arguicao', 'arguicao'),
    ('chamada oral', 'pt-BR', 'prova oral', 'prova oral', 'prova oral'),
    ('media', 'pt-BR', 'media', 'media', 'media'),
    ('media', 'pt-BR', 'fechar a média', 'fechar a media', 'fechar a media'),
    ('media', 'pt-BR', 'tirar média', 'tirar media', 'tirar media'),
    ('bimestre', 'pt-BR', 'trimestre', 'trimestre', 'trimestre'),
    ('bimestre', 'pt-BR', 'semestre', 'semestre', 'semestre'),
    ('conselho de classe', 'pt-BR', 'conselho', 'conselho', 'conselho'),
    ('colinha', 'pt-BR', 'cola', 'cola', 'cola'),
    ('colinha', 'pt-BR', 'cola eletrônica', 'cola eletronica', 'cola eletronica'),
    ('lousa', 'pt-BR', 'quadro', 'quadro', 'quadro'),
    ('lousa', 'pt-BR', 'quadro-negro', 'quadro negro', 'quadro negro'),
    ('lousa', 'pt-BR', 'quadro branco', 'quadro branco', 'quadro branco'),
    ('carteira', 'pt-BR', 'carteira escolar', 'carteira escolar', 'carteira escolar'),
    ('carteira', 'pt-BR', 'classe', 'classe', 'classe'),
    ('gremio', 'pt-BR', 'gremio', 'gremio', 'gremio'),
    ('gremio', 'pt-BR', 'grêmio estudantil', 'gremio estudantil', 'gremio estudantil'),
    ('reforco', 'pt-BR', 'reforco', 'reforco', 'reforco'),
    ('reforco', 'pt-BR', 'aula de reforço', 'aula de reforco', 'aula de reforco'),
    ('prova substitutiva', 'pt-BR', 'substitutiva', 'substitutiva', 'substitutiva'),
    ('prova substitutiva', 'pt-BR', 'segunda chamada', 'segunda chamada', 'segunda chamada'),
    ('formatura', 'pt-BR', 'colação', 'colacao', 'colacao'),
    ('formatura', 'pt-BR', 'colação de grau', 'colacao de grau', 'colacao de grau'),
    ('formatura', 'pt-BR', 'baile de formatura', 'baile de formatura', 'baile de formatura')
) AS v(termo_norm, idioma, variacao, norm, colapsado)
JOIN idioma i ON i.codigo = v.idioma
JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT g.id, c.id
FROM (VALUES
    ('pf', 'pt-BR', 'comida'),
    ('cafe completo', 'pt-BR', 'comida'),
    ('cafe completo', 'pt-BR', 'regional'),
    ('salgado', 'pt-BR', 'comida'),
    ('pingado', 'pt-BR', 'comida'),
    ('pingado', 'pt-BR', 'regional'),
    ('tirar o pe da jaca', 'pt-BR', 'comida'),
    ('tirar o pe da jaca', 'pt-BR', 'humor'),
    ('vaquinha do lanche', 'pt-BR', 'comida'),
    ('vaquinha do lanche', 'pt-BR', 'escolar'),
    ('bebum', 'pt-BR', 'comida'),
    ('bebum', 'pt-BR', 'critica'),
    ('levar o doce', 'pt-BR', 'comida'),
    ('levar o doce', 'pt-BR', 'familia'),
    ('pelada', 'pt-BR', 'esporte'),
    ('racha', 'pt-BR', 'esporte'),
    ('racha', 'pt-BR', 'regional'),
    ('bater falta', 'pt-BR', 'esporte'),
    ('pintura', 'pt-BR', 'esporte'),
    ('pintura', 'pt-BR', 'elogio'),
    ('frango', 'pt-BR', 'esporte'),
    ('frango', 'pt-BR', 'critica'),
    ('cair de pe', 'pt-BR', 'esporte'),
    ('cair de pe', 'pt-BR', 'emocao'),
    ('escalacao', 'pt-BR', 'esporte'),
    ('artilheiro', 'pt-BR', 'esporte'),
    ('artilheiro', 'pt-BR', 'elogio'),
    ('tabelinha', 'pt-BR', 'esporte'),
    ('amarelar', 'pt-BR', 'esporte'),
    ('amarelar', 'pt-BR', 'critica'),
    ('de bicicleta', 'pt-BR', 'esporte'),
    ('vestiario', 'pt-BR', 'esporte'),
    ('chamada oral', 'pt-BR', 'escolar'),
    ('media', 'pt-BR', 'escolar'),
    ('bimestre', 'pt-BR', 'escolar'),
    ('bimestre', 'pt-BR', 'tempo'),
    ('conselho de classe', 'pt-BR', 'escolar'),
    ('colinha', 'pt-BR', 'escolar'),
    ('lousa', 'pt-BR', 'escolar'),
    ('lousa', 'pt-BR', 'regional'),
    ('carteira', 'pt-BR', 'escolar'),
    ('gremio', 'pt-BR', 'escolar'),
    ('reforco', 'pt-BR', 'escolar'),
    ('prova substitutiva', 'pt-BR', 'escolar'),
    ('formatura', 'pt-BR', 'escolar'),
    ('formatura', 'pt-BR', 'familia')
) AS gc(termo_norm, idioma, categoria)
JOIN idioma i ON i.codigo = gc.idioma
JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id
JOIN categoria c ON c.slug = gc.categoria
ON CONFLICT DO NOTHING;

