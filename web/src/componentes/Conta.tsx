import { useState } from 'react';
import { useConta } from '../sessao.js';

/**
 * Entrar e criar conta.
 *
 * <h2>Por que isto quase não aparece na tela</h2>
 *
 * Consultar o dicionário nunca exige cadastro, e essa é uma decisão de
 * produto, não um detalhe de implementação: exigir login para entender uma
 * mensagem afastaria exatamente o público que o Tradugil quer atender.
 *
 * Conta existe para contribuir e moderar. Por isso este bloco fica recolhido
 * no rodapé, e não num botão grande no topo: quem chegou para consultar não
 * pode nem chegar a pensar que precisa de conta.
 */
export function Conta() {
  const { autenticado, papel, podeModerar, erro, ocupado, entrar, registrar, sair } =
    useConta();
  const [aberto, setAberto] = useState(false);
  const [criando, setCriando] = useState(false);
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');

  async function enviar(evento: React.FormEvent) {
    evento.preventDefault();
    try {
      await (criando ? registrar : entrar)({ email, senha });
      setSenha('');
      setAberto(false);
    } catch {
      // A mensagem já está em `erro`. A senha fica no campo para a pessoa
      // corrigir um erro de digitação sem redigitar tudo.
    }
  }

  if (autenticado) {
    return (
      <div className="conta">
        <p className="conta-status">
          Você está na sua conta
          {podeModerar && (
            <>
              {' '}
              <span className="etiqueta">
                {papel === 'ADMIN' ? 'administração' : 'moderação'}
              </span>
            </>
          )}
        </p>
        <button type="button" className="botao" onClick={() => void sair()}>
          Sair
        </button>
      </div>
    );
  }

  if (!aberto) {
    return (
      <div className="conta">
        <p className="conta-status">
          Quer ajudar a melhorar o dicionário? Entre para sugerir uma gíria.
        </p>
        <button type="button" className="botao" onClick={() => setAberto(true)}>
          Entrar ou criar conta
        </button>
      </div>
    );
  }

  return (
    <form className="conta conta-formulario" onSubmit={enviar}>
      <h2 className="conta-titulo">{criando ? 'Criar conta' : 'Entrar'}</h2>

      <label className="rotulo" htmlFor="conta-email">
        E-mail
      </label>
      <input
        id="conta-email"
        className="campo campo-linha"
        type="email"
        autoComplete="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />

      <label className="rotulo" htmlFor="conta-senha">
        Senha
      </label>
      <input
        id="conta-senha"
        className="campo campo-linha"
        type="password"
        /*
          Diz ao gerenciador de senhas se é uma senha nova ou existente. Sem
          isto ele oferece a senha errada no cadastro e não oferece salvar a
          nova, que é onde a maioria das pessoas desiste de usar gerenciador.
        */
        autoComplete={criando ? 'new-password' : 'current-password'}
        minLength={8}
        value={senha}
        onChange={(e) => setSenha(e.target.value)}
        required
        aria-describedby={criando ? 'dica-senha' : undefined}
      />
      {criando && (
        <p className="dica" id="dica-senha">
          Pelo menos 8 caracteres. Evite as senhas mais comuns, como
          &quot;senha123&quot;: elas são as primeiras que um ataque tenta.
        </p>
      )}

      {erro && (
        <p className="mensagem mensagem-erro" role="alert">
          {erro}
        </p>
      )}

      <button type="submit" className="botao botao-principal" disabled={ocupado}>
        {ocupado ? 'Aguarde…' : criando ? 'Criar conta' : 'Entrar'}
      </button>

      <div className="conta-alternativas">
        <button type="button" className="link" onClick={() => setCriando(!criando)}>
          {criando ? 'Já tenho conta' : 'Criar uma conta'}
        </button>
        <button type="button" className="link" onClick={() => setAberto(false)}>
          Cancelar
        </button>
      </div>
    </form>
  );
}
