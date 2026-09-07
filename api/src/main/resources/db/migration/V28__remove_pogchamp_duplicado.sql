-- ---------------------------------------------------------------------------
-- Remove o verbete "PogChamp", que colide com o verbete "pog".
--
-- O ERRO
--
-- A V25 criou "PogChamp" como verbete proprio. So que "pogchamp" ja era uma
-- VARIACAO de "pog", cadastrada no seed inicial. As duas coisas normalizam
-- para a mesma chave, e a partir da V25 a mesma palavra passou a apontar para
-- dois verbetes diferentes.
--
-- Quem pegou foi o teste variacaoApontaParaOVerbete, que existia desde o
-- comeco justamente para garantir que "pogchamp" leva a "pog" e a mais nada.
-- Ele esperava ["pog"] e recebeu ["pog", "PogChamp"].
--
-- Nao e um teste chato: um dicionario com duas entradas para a mesma palavra
-- responde de forma imprevisivel, e a explicacao que a pessoa recebe passa a
-- depender da ordem em que o banco devolveu as linhas.
--
-- A checagem de duplicata do gerador nao pegou porque ela compara termo com
-- termo, e aqui a colisao e entre um termo novo e uma VARIACAO antiga. Fica
-- registrado como limitacao conhecida: cobrir isso exigiria varrer tambem as
-- variacoes das migracoes ja aplicadas.
--
-- O QUE ACONTECE COM O CONTEUDO
--
-- A explicacao que a V25 trazia (a imagem, a retirada em 2021, o "PogU") nao
-- se perde: entra como um segundo sentido de "pog", que e onde ela deveria
-- estar desde o comeco. E tambem o que a mensagem de erro do gerador manda
-- fazer: para acrescentar um sentido a um termo que ja existe, edite o
-- verbete que ja existe.
--
-- O DELETE limpa sozinho definicao, variacao e categoria: as tres tabelas
-- referenciam giria com ON DELETE CASCADE.
-- ---------------------------------------------------------------------------

-- O segundo sentido entra ANTES da remocao, para nao existir instante em que
-- a informacao esteja fora do banco.
INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id,
       'O nome do emote que deu origem a esta palavra.',
       'A imagem era um rosto de espanto e foi o emote mais famoso da Twitch. '
         || 'A plataforma a retirou em 2021 e trocou de desenho varias vezes; '
         || '"PogU" e uma das versoes que o chat passou a usar no lugar.',
       NULL,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM giria g
JOIN idioma i ON i.id = g.idioma_id
WHERE g.termo_normalizado = 'pog' AND i.codigo = 'en';

DELETE FROM giria g
USING idioma i
WHERE i.id = g.idioma_id
  AND g.termo_normalizado = 'pogchamp'
  AND i.codigo = 'en'
  AND g.termo = 'PogChamp';
