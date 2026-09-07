-- ---------------------------------------------------------------------------
-- termos que pedem atencao modo familia
--
-- 37 verbetes, 37 sentidos.
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
    ('vape', 'vape', 'vape', 'en', FALSE, TRUE),
    ('beck', 'beck', 'beck', 'pt-BR', FALSE, TRUE),
    ('bala', 'bala', 'bala', 'pt-BR', FALSE, TRUE),
    ('loló', 'lolo', 'lolo', 'pt-BR', FALSE, TRUE),
    ('brisar', 'brisar', 'brisar', 'pt-BR', FALSE, TRUE),
    ('chapado', 'chapado', 'chapado', 'pt-BR', FALSE, TRUE),
    ('mó onda', 'mo onda', 'mo onda', 'pt-BR', FALSE, TRUE),
    ('cair de boca', 'cair de boca', 'cair de boca', 'pt-BR', FALSE, TRUE),
    ('ficar de porre', 'ficar de porre', 'ficar de porre', 'pt-BR', FALSE, TRUE),
    ('apagar', 'apagar', 'apagar', 'pt-BR', FALSE, TRUE),
    ('se cortar', 'se cortar', 'se cortar', 'pt-BR', FALSE, TRUE),
    ('sh', 'sh', 'sh', 'en', FALSE, TRUE),
    ('gatilho', 'gatilho', 'gatilho', 'pt-BR', FALSE, TRUE),
    ('ana', 'ana', 'ana', 'pt-BR', FALSE, TRUE),
    ('jejum limpo', 'jejum limpo', 'jejum limpo', 'pt-BR', FALSE, TRUE),
    ('nudes', 'nudes', 'nudes', 'en', FALSE, TRUE),
    ('pack', 'pack', 'pack', 'en', FALSE, TRUE),
    ('aliciamento', 'aliciamento', 'aliciamento', 'pt-BR', FALSE, TRUE),
    ('sugar', 'sugar', 'sugar', 'en', FALSE, TRUE),
    ('sextar', 'sextar', 'sextar', 'pt-BR', FALSE, TRUE),
    ('cyberbullying', 'cyberbullying', 'cyberbullying', 'en', FALSE, TRUE),
    ('doxxing', 'doxxing', 'doxxing', 'en', FALSE, TRUE),
    ('catfish', 'catfish', 'catfish', 'en', FALSE, TRUE),
    ('golpe do pix', 'golpe do pix', 'golpe do pix', 'pt-BR', FALSE, TRUE),
    ('desafio', 'desafio', 'desafio', 'pt-BR', FALSE, TRUE),
    ('mule', 'mule', 'mule', 'en', FALSE, TRUE),
    ('hentai', 'hentai', 'hentai', 'en', TRUE, TRUE),
    ('onlyfans', 'onlyfans', 'onlyfans', 'en', FALSE, TRUE),
    ('aposta', 'aposta', 'aposta', 'pt-BR', FALSE, TRUE),
    ('hater', 'hater', 'hater', 'en', FALSE, TRUE),
    ('cancelar', 'cancelar', 'cancelar', 'pt-BR', FALSE, TRUE),
    ('grupo secreto', 'grupo secreto', 'grupo secreto', 'pt-BR', FALSE, TRUE),
    ('sumir do mapa', 'sumir do mapa', 'sumir do mapa', 'pt-BR', FALSE, TRUE),
    ('tô mal', 'to mal', 'to mal', 'pt-BR', FALSE, TRUE),
    ('cansado de tudo', 'cansado de tudo', 'cansado de tudo', 'pt-BR', FALSE, TRUE),
    ('cvv', 'cvv', 'cvv', 'pt-BR', FALSE, FALSE),
    ('disque 100', 'disque 100', 'disque 100', 'pt-BR', FALSE, FALSE)
) AS t(termo, norm, colapsado, idioma, nsfw, risco)
JOIN idioma i ON i.codigo = t.idioma
ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;

INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id, d.simples, d.detalhada, d.formal,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM (VALUES
    ('vape', 'en', 'Cigarro eletrônico, aparelho que a pessoa usa para inalar vapor com nicotina.', 'A venda é proibida no Brasil pela Anvisa desde 2009, mas o produto circula. Costuma ter sabores doces e mais nicotina que o cigarro comum, o que facilita o vício em quem começa jovem.', 'cigarro eletrônico'),
    ('beck', 'pt-BR', 'Cigarro de maconha.', 'Termo antigo e muito conhecido. Aparece com naturalidade em conversas de adolescentes, às vezes sem qualquer envolvimento real, só como referência cultural.', 'cigarro de maconha'),
    ('bala', 'pt-BR', 'Comprimido de droga sintética, geralmente ecstasy.', 'A palavra tem uso comum e inocente (doce, projétil), então o contexto é o que importa: menções a festa, noite e quantidade mudam o sentido.', 'comprimido de ecstasy'),
    ('lolo', 'pt-BR', 'Substância inalante usada em festas, de venda proibida.', 'Causa efeito de curta duração e risco cardíaco imediato, inclusive na primeira vez. Circula principalmente em carnaval e festas de rua.', 'inalante'),
    ('brisar', 'pt-BR', 'Ficar sob efeito de alguma substância.', 'Cuidado: "brisar" também é apenas ficar distraído ou pensativo, sem qualquer relação com drogas, e esse é o uso mais comum. O contexto separa os dois.', 'ficar sob efeito'),
    ('chapado', 'pt-BR', 'Sob forte efeito de álcool ou de outra substância.', 'Também usado com exagero e humor para dizer que a pessoa está muito cansada ou desligada, sem envolver nada. O tom da conversa indica qual dos dois.', 'sob efeito'),
    ('mo onda', 'pt-BR', 'Pode indicar o efeito de uma substância.', '"Onda" tem uso amplo e inocente, como "que onda boa" para um momento agradável. Só o contexto de festa ou de consumo muda o sentido.', 'efeito'),
    ('cair de boca', 'pt-BR', 'Consumir algo em grande quantidade, sem moderação.', 'Usado tanto para comida quanto para bebida. Quando o assunto é bebida, e em conversa de adolescente, merece atenção.', 'exagerar'),
    ('ficar de porre', 'pt-BR', 'Beber a ponto de perder o controle.', 'No Brasil, a venda de bebida alcoólica a menores de 18 anos é proibida. Beber em grande quantidade em pouco tempo traz risco imediato de intoxicação.', 'embriaguez'),
    ('apagar', 'pt-BR', 'Perder a consciência por excesso de bebida.', 'É sinal de intoxicação alcoólica, não de festa boa. Alguém que apagou precisa de acompanhamento, e não de ser deixado dormindo sozinho.', 'desmaiar'),
    ('se cortar', 'pt-BR', 'Machucar o próprio corpo de propósito.', 'É sinal de sofrimento que pede ajuda profissional, não castigo nem sermão. O CVV atende de graça pelo telefone 188, 24 horas por dia, e também por chat no site cvv.org.br.', 'automutilação'),
    ('sh', 'en', 'Sigla em inglês usada para falar de automutilação sem escrever a palavra.', 'De "self harm". Siglas assim circulam porque muitas redes bloqueiam os termos completos. Encontrar a sigla é motivo para uma conversa acolhedora, e o CVV atende pelo 188.', 'automutilação'),
    ('gatilho', 'pt-BR', 'Algo que desperta uma lembrança ou reação emocional forte em alguém.', '"TW" antes de um conteúdo é aviso de que ele pode ser pesado para quem viveu algo parecido. O uso do termo em si não indica risco: é cuidado, não sintoma.', 'estímulo emocional'),
    ('ana', 'pt-BR', 'Apelidos usados em comunidades que tratam transtornos alimentares como escolha, e não como doença.', '"Ana" vem de anorexia e "mia" de bulimia. São grupos que incentivam a doença, e a presença desses termos é sinal de alerta. Transtorno alimentar é doença tratável, com risco real de morte.', 'transtorno alimentar'),
    ('jejum limpo', 'pt-BR', 'Termo usado para descrever ficar sem comer por longos períodos.', 'Aparece tanto em contextos de dieta quanto em comunidades que incentivam transtorno alimentar. Em adolescente, restrição alimentar prolongada pede avaliação médica.', 'restrição alimentar'),
    ('nudes', 'en', 'Fotos íntimas, sem roupa, trocadas por mensagem.', 'Quando envolve menor de 18 anos, produzir, enviar ou guardar essas imagens é crime no Brasil, mesmo que a própria pessoa tenha feito a foto. A vítima não é culpada, e a lei protege quem foi exposto.', 'imagens íntimas'),
    ('pack', 'en', 'Conjunto de fotos íntimas vendido ou trocado.', '"Vender pack" aparece em conversas de adolescentes. Quando envolve menor de idade, configura crime grave, e quem compra também responde.', 'conjunto de imagens íntimas'),
    ('aliciamento', 'pt-BR', 'Quando um adulto se aproxima de uma criança ou adolescente pela internet para ganhar confiança com intenção sexual.', 'Do inglês "grooming". Começa com amizade, elogios e presentes, e evolui devagar para isolamento e segredo. É crime no Brasil, previsto no Estatuto da Criança e do Adolescente.', 'aliciamento'),
    ('sugar', 'en', 'Relação em que uma pessoa mais velha dá dinheiro ou presentes a uma mais jovem.', 'Quando envolve menor de 18 anos, é exploração sexual, e não relacionamento. É crime, independentemente de haver consentimento aparente.', 'relação por interesse financeiro'),
    ('sextar', 'pt-BR', 'Trocar mensagens de conteúdo sexual.', 'Do inglês "sexting". Cuidado: "sextou" é outra coisa completamente diferente, e apenas comemora a sexta-feira. As duas palavras são parecidas e não têm relação.', 'troca de mensagens sexuais'),
    ('cyberbullying', 'en', 'Perseguição e humilhação de alguém pela internet, de forma repetida.', 'É crime no Brasil desde 2023, com pena prevista em lei. Diferente de uma briga isolada: o que caracteriza é a repetição e a intenção de humilhar.', 'perseguição virtual'),
    ('doxxing', 'en', 'Publicar dados pessoais de alguém na internet para que outros a persigam.', 'Inclui endereço, escola, telefone e local de trabalho. Costuma ser o passo que transforma um ataque virtual em risco físico.', 'exposição de dados pessoais'),
    ('catfish', 'en', 'Pessoa que finge ser outra na internet, usando fotos e nome falsos.', 'Pode ser brincadeira, golpe financeiro ou aproximação de adulto que se passa por adolescente. O último caso é o mais grave.', 'perfil falso'),
    ('golpe do pix', 'pt-BR', 'Fraude em que alguém convence a vítima a transferir dinheiro.', 'Muito comum por WhatsApp, com o golpista se passando por parente que trocou de número. Idosos são o alvo preferido, e a orientação é sempre ligar para o número antigo antes de transferir.', 'fraude financeira'),
    ('desafio', 'pt-BR', 'Brincadeira que circula pedindo que a pessoa faça algo e grave.', 'A grande maioria é inofensiva e engraçada. Uma minoria envolve risco físico real, e essas costumam se espalhar mais rápido justamente pelo perigo.', 'desafio viral'),
    ('mule', 'en', 'Pessoa que empresta a própria conta bancária para movimentar dinheiro de outra.', 'Em português é "laranja". Oferecido a jovens como dinheiro fácil pela internet, mas quem empresta a conta responde criminalmente pela lavagem.', 'intermediário financeiro'),
    ('hentai', 'en', 'Desenho japonês de conteúdo adulto.', 'Termo comum em comunidades de animação japonesa. Marcado como conteúdo impróprio: some da resposta quando o Modo Família está ligado.', 'conteúdo adulto ilustrado'),
    ('onlyfans', 'en', 'Site de assinatura usado principalmente para conteúdo adulto.', 'Exige 18 anos para criar conta. Menções entre adolescentes costumam ser piada, mas também aparecem em conversas sobre ganhar dinheiro rápido.', 'plataforma de assinatura adulta'),
    ('aposta', 'pt-BR', 'Jogo de azar pela internet, em que a pessoa arrisca dinheiro.', 'Proibido para menores de 18 anos no Brasil. Os jogos de imagens giratórias, como o chamado "tigrinho", são desenhados para causar vício, e o prejuízo costuma crescer devagar.', 'jogo de azar'),
    ('hater', 'en', 'Pessoa que ataca alguém repetidamente na internet.', 'Quando os ataques são constantes e dirigidos à mesma pessoa, deixa de ser opinião e passa a ser perseguição, que tem consequência legal.', 'perseguidor virtual'),
    ('cancelar', 'pt-BR', 'Ataque coletivo a alguém na internet, geralmente por algo que a pessoa disse.', 'Entre adolescentes, um cancelamento na escola pode isolar completamente a pessoa. O efeito emocional costuma ser muito maior do que os adultos imaginam.', 'boicote coletivo'),
    ('grupo secreto', 'pt-BR', 'Grupo de conversa que a pessoa esconde das outras.', 'Ter espaço privado é normal e saudável na adolescência. O sinal de alerta é o segredo combinado com mudança de comportamento ou com um adulto participando.', 'grupo privado'),
    ('sumir do mapa', 'pt-BR', 'Parar de responder e de aparecer, por um período longo.', 'Na maioria das vezes é só cansaço ou vontade de ficar sozinho. Quando vem junto de desânimo persistente e perda de interesse pelo que a pessoa gostava, pede conversa.', 'isolamento'),
    ('to mal', 'pt-BR', 'Jeito comum de dizer que não se está bem emocionalmente.', 'Frase curta que muitas vezes é a única abertura que a pessoa consegue dar. Perguntar o que houve, sem julgar, costuma valer mais que qualquer conselho.', 'não estou bem'),
    ('cansado de tudo', 'pt-BR', 'Expressão de exaustão que às vezes vai além do cansaço comum.', 'Quase sempre é desabafo passageiro. Quando aparece junto de despedidas, doação de objetos queridos ou perda de interesse por tudo, é sinal sério: o CVV atende de graça pelo 188, a qualquer hora.', 'exaustão emocional'),
    ('cvv', 'pt-BR', 'Serviço gratuito de apoio emocional por telefone, no número 188, 24 horas por dia.', 'Centro de Valorização da Vida. Atende de forma anônima e sigilosa por telefone, chat e e-mail, pelo site cvv.org.br. Qualquer pessoa pode ligar, inclusive quem está preocupado com outra.', 'Centro de Valorização da Vida'),
    ('disque 100', 'pt-BR', 'Telefone gratuito para denunciar violência contra crianças e adolescentes.', 'Funciona 24 horas, é anônimo e nacional. Recebe denúncias de violência física, sexual, negligência e trabalho infantil.', 'Disque Direitos Humanos')
) AS d(norm, idioma, simples, detalhada, formal)
JOIN idioma i ON i.codigo = d.idioma
JOIN giria g ON g.termo_normalizado = d.norm AND g.idioma_id = i.id;

INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,
                            variacao_colapsada)
SELECT g.id, v.variacao, v.norm, v.colapsado
FROM (VALUES
    ('vape', 'en', 'pod', 'pod', 'pod'),
    ('vape', 'en', 'vapear', 'vapear', 'vapear'),
    ('vape', 'en', 'cigarro eletrônico', 'cigarro eletronico', 'cigarro eletronico'),
    ('beck', 'pt-BR', 'baseado', 'baseado', 'baseado'),
    ('beck', 'pt-BR', 'fininho', 'fininho', 'fininho'),
    ('bala', 'pt-BR', 'balinha', 'balinha', 'balinha'),
    ('lolo', 'pt-BR', 'lolo', 'lolo', 'lolo'),
    ('lolo', 'pt-BR', 'lança', 'lanca', 'lanca'),
    ('lolo', 'pt-BR', 'lança perfume', 'lanca perfume', 'lanca perfume'),
    ('chapado', 'pt-BR', 'chapada', 'chapada', 'chapada'),
    ('chapado', 'pt-BR', 'chapar', 'chapar', 'chapar'),
    ('mo onda', 'pt-BR', 'na onda', 'na onda', 'na onda'),
    ('mo onda', 'pt-BR', 'onda', 'onda', 'onda'),
    ('cair de boca', 'pt-BR', 'cair matando', 'cair matando', 'cair matando'),
    ('ficar de porre', 'pt-BR', 'porre', 'porre', 'porre'),
    ('ficar de porre', 'pt-BR', 'de porre', 'de porre', 'de porre'),
    ('ficar de porre', 'pt-BR', 'bêbado', 'bebado', 'bebado'),
    ('apagar', 'pt-BR', 'apagou', 'apagou', 'apagou'),
    ('se cortar', 'pt-BR', 'cutting', 'cutting', 'cutting'),
    ('se cortar', 'pt-BR', 'se machucar', 'se machucar', 'se machucar'),
    ('sh', 'en', 'self harm', 'self harm', 'self harm'),
    ('gatilho', 'pt-BR', 'trigger', 'trigger', 'trigger'),
    ('gatilho', 'pt-BR', 'tw', 'tw', 'tw'),
    ('ana', 'pt-BR', 'mia', 'mia', 'mia'),
    ('ana', 'pt-BR', 'pró-ana', 'pro ana', 'pro ana'),
    ('jejum limpo', 'pt-BR', 'restrição', 'restricao', 'restricao'),
    ('nudes', 'en', 'nude', 'nude', 'nude'),
    ('nudes', 'en', 'mandar nudes', 'mandar nudes', 'mandar nudes'),
    ('aliciamento', 'pt-BR', 'grooming', 'grooming', 'grooming'),
    ('sugar', 'en', 'sugar daddy', 'sugar daddy', 'sugar daddy'),
    ('sugar', 'en', 'sugar baby', 'sugar baby', 'sugar baby'),
    ('sextar', 'pt-BR', 'sexting', 'sexting', 'sexting'),
    ('cyberbullying', 'en', 'bullying virtual', 'bullying virtual', 'bullying virtual'),
    ('doxxing', 'en', 'doxar', 'doxar', 'doxar'),
    ('doxxing', 'en', 'dox', 'dox', 'dox'),
    ('catfish', 'en', 'catfishing', 'catfishing', 'catfishing'),
    ('golpe do pix', 'pt-BR', 'golpe', 'golpe', 'golpe'),
    ('golpe do pix', 'pt-BR', 'cair no golpe', 'cair no golpe', 'cair no golpe'),
    ('desafio', 'pt-BR', 'challenge', 'challenge', 'challenge'),
    ('desafio', 'pt-BR', 'desafio da internet', 'desafio da internet', 'desafio da internet'),
    ('mule', 'en', 'laranja', 'laranja', 'laranja'),
    ('mule', 'en', 'conta laranja', 'conta laranja', 'conta laranja'),
    ('onlyfans', 'en', 'of', 'of', 'of'),
    ('aposta', 'pt-BR', 'bet', 'bet', 'bet'),
    ('aposta', 'pt-BR', 'tigrinho', 'tigrinho', 'tigrinho'),
    ('aposta', 'pt-BR', 'casa de aposta', 'casa de aposta', 'casa de aposta'),
    ('cancelar', 'pt-BR', 'cancelamento em massa', 'cancelamento em massa', 'cancelamento em massa'),
    ('grupo secreto', 'pt-BR', 'grupo fechado', 'grupo fechado', 'grupo fechado'),
    ('grupo secreto', 'pt-BR', 'close friends secreto', 'close friends secreto', 'close friends secreto'),
    ('sumir do mapa', 'pt-BR', 'sumido', 'sumido', 'sumido'),
    ('to mal', 'pt-BR', 'to mal', 'to mal', 'to mal'),
    ('to mal', 'pt-BR', 'não tô bem', 'nao to bem', 'nao to bem'),
    ('cansado de tudo', 'pt-BR', 'cansei', 'cansei', 'cansei'),
    ('cansado de tudo', 'pt-BR', 'não aguento mais', 'nao aguento mais', 'nao aguento mais'),
    ('disque 100', 'pt-BR', 'disque denúncia', 'disque denuncia', 'disque denuncia'),
    ('disque 100', 'pt-BR', 'disque 100', 'disque 100', 'disque 100')
) AS v(termo_norm, idioma, variacao, norm, colapsado)
JOIN idioma i ON i.codigo = v.idioma
JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT g.id, c.id
FROM (VALUES
    ('vape', 'en', 'atencao'),
    ('beck', 'pt-BR', 'atencao'),
    ('bala', 'pt-BR', 'atencao'),
    ('lolo', 'pt-BR', 'atencao'),
    ('brisar', 'pt-BR', 'atencao'),
    ('chapado', 'pt-BR', 'atencao'),
    ('mo onda', 'pt-BR', 'atencao'),
    ('cair de boca', 'pt-BR', 'atencao'),
    ('ficar de porre', 'pt-BR', 'atencao'),
    ('apagar', 'pt-BR', 'atencao'),
    ('se cortar', 'pt-BR', 'atencao'),
    ('sh', 'en', 'atencao'),
    ('gatilho', 'pt-BR', 'atencao'),
    ('ana', 'pt-BR', 'atencao'),
    ('jejum limpo', 'pt-BR', 'atencao'),
    ('nudes', 'en', 'atencao'),
    ('pack', 'en', 'atencao'),
    ('aliciamento', 'pt-BR', 'atencao'),
    ('sugar', 'en', 'atencao'),
    ('sextar', 'pt-BR', 'atencao'),
    ('cyberbullying', 'en', 'atencao'),
    ('doxxing', 'en', 'atencao'),
    ('catfish', 'en', 'atencao'),
    ('golpe do pix', 'pt-BR', 'atencao'),
    ('desafio', 'pt-BR', 'atencao'),
    ('mule', 'en', 'atencao'),
    ('hentai', 'en', 'atencao'),
    ('onlyfans', 'en', 'atencao'),
    ('aposta', 'pt-BR', 'atencao'),
    ('hater', 'en', 'atencao'),
    ('cancelar', 'pt-BR', 'atencao'),
    ('grupo secreto', 'pt-BR', 'atencao'),
    ('sumir do mapa', 'pt-BR', 'atencao'),
    ('to mal', 'pt-BR', 'atencao'),
    ('cansado de tudo', 'pt-BR', 'atencao'),
    ('cvv', 'pt-BR', 'atencao'),
    ('disque 100', 'pt-BR', 'atencao')
) AS gc(termo_norm, idioma, categoria)
JOIN idioma i ON i.codigo = gc.idioma
JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id
JOIN categoria c ON c.slug = gc.categoria
ON CONFLICT DO NOTHING;

