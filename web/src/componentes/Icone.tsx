/**
 * Os poucos ícones da interface, como SVG inline.
 *
 * Eram emoji. Emoji parece prático e tem três problemas concretos aqui:
 *
 * 1. Cada sistema desenha o seu. O "⚠️" do Android é um triângulo amarelo
 *    gordo; o do Windows é fino e cinza. O aviso mais importante do produto
 *    não pode ter peso visual decidido pelo aparelho.
 * 2. Leitor de tela lê o nome do emoji, em inglês e fora de contexto
 *    ("warning sign"), a menos que se esconda com aria-hidden, e aí ele
 *    vira enfeite puro.
 * 3. Emoji marcando seção é a assinatura mais reconhecível de interface
 *    gerada por IA.
 *
 * Os que sobraram são funcionais: um alto-falante ao lado de "Ouvir" e um
 * triângulo ao lado do aviso de risco ajudam quem lê com dificuldade a achar
 * o controle antes de ler o rótulo. Os decorativos foram embora.
 *
 * `currentColor` em tudo: o ícone acompanha a cor do texto ao redor, inclusive
 * no alto contraste e no modo escuro, sem uma única regra a mais.
 */

interface Props {
  /** Tamanho em em, para acompanhar a escala de fonte do usuário. */
  tamanho?: number;
}

export function IconeSom({ tamanho = 1.15 }: Props) {
  return (
    <svg
      aria-hidden="true"
      focusable="false"
      width={`${tamanho}em`}
      height={`${tamanho}em`}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M11 5 6 9H3v6h3l5 4z" fill="currentColor" stroke="none" />
      <path d="M15.5 8.5a5 5 0 0 1 0 7" />
      <path d="M18.5 5.5a9 9 0 0 1 0 13" />
    </svg>
  );
}

export function IconeAtencao({ tamanho = 1.15 }: Props) {
  return (
    <svg
      aria-hidden="true"
      focusable="false"
      width={`${tamanho}em`}
      height={`${tamanho}em`}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      style={{ flexShrink: 0 }}
    >
      <path d="M12 3.5 22 20H2z" />
      <path d="M12 9.5v4.5" />
      <path d="M12 17.2h.01" />
    </svg>
  );
}

export function IconeSemRede({ tamanho = 1.15 }: Props) {
  return (
    <svg
      aria-hidden="true"
      focusable="false"
      width={`${tamanho}em`}
      height={`${tamanho}em`}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      style={{ flexShrink: 0 }}
    >
      <path d="M2 8.5a15 15 0 0 1 8-3.9" />
      <path d="M14.4 4.9a15 15 0 0 1 7.6 3.6" />
      <path d="M5 12.3a10 10 0 0 1 3.6-2.2" />
      <path d="M15.6 10.2a10 10 0 0 1 3.4 2.1" />
      <path d="M12 20h.01" />
      <path d="M3 3l18 18" />
    </svg>
  );
}

/** Seta de expandir. Substitui o ▲▼, que muda de forma conforme a fonte. */
export function IconeSeta({ aberta }: { aberta: boolean }) {
  return (
    <svg
      aria-hidden="true"
      focusable="false"
      width="0.85em"
      height="0.85em"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="3"
      strokeLinecap="round"
      strokeLinejoin="round"
      style={{
        flexShrink: 0,
        transform: aberta ? 'rotate(180deg)' : 'none',
        transition: 'transform 0.15s ease',
      }}
    >
      <path d="m5 9 7 7 7-7" />
    </svg>
  );
}
