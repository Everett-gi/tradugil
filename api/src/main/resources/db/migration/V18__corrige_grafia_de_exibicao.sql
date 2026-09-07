-- ---------------------------------------------------------------------------
-- Corrige a grafia de exibicao de dois verbetes.
--
-- O termo tem duas formas no banco: a de exibicao (giria.termo), que o
-- usuario le, e a normalizada (termo_normalizado), que a busca usa. So a
-- primeira muda aqui, entao nada quebra: quem procurava "role" continua
-- achando, e agora ve escrito "role" com acento.
--
-- Os dois entraram sem acento por descuido na fonte de curadoria, e a fonte
-- tambem foi corrigida. Como as migracoes que os inseriram ja estao
-- aplicadas, a correcao vem numa migracao nova.
--
-- UMA CHECAGEM QUE PRECISOU DE JULGAMENTO
--
-- A varredura que encontrou estes dois tambem apontou "joia" e "bora", e nos
-- dois casos ela estava errada: a reforma ortografica de 1990 tirou o acento
-- de "joia", e "bora" (de "embora") nunca teve. Verificacao automatica de
-- acentuacao levanta candidatos; quem decide e uma pessoa.
-- ---------------------------------------------------------------------------

UPDATE giria SET termo = 'rolê'   WHERE termo_normalizado = 'role'   AND termo = 'role';
UPDATE giria SET termo = 'amanhã' WHERE termo_normalizado = 'amanha' AND termo = 'amanha';
