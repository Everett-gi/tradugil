/**
 * Leitura em voz alta (RF16). Usa a Web Speech API, que é do navegador — o
 * texto não sai do dispositivo para ser sintetizado.
 *
 * Para a Marlene, ouvir costuma ser mais fácil que ler, então a função é
 * central e não um extra. Mas o suporte varia bastante entre navegadores, e
 * a interface precisa saber se pode oferecer o botão antes de mostrá-lo.
 */

export function vozDisponivel(): boolean {
  return typeof window !== 'undefined' && 'speechSynthesis' in window;
}

export function falar(texto: string, idioma = 'pt-BR'): void {
  if (!vozDisponivel() || !texto.trim()) return;

  // Cancela o que estiver falando: dois textos sobrepostos são ininteligíveis,
  // e tocar em outra gíria enquanto a anterior fala é o caso comum.
  window.speechSynthesis.cancel();

  const fala = new SpeechSynthesisUtterance(texto);
  fala.lang = idioma;
  // Um pouco mais devagar que o padrão. A explicação tem termos estranhos
  // para quem escuta, e a velocidade normal atropela.
  fala.rate = 0.95;
  window.speechSynthesis.speak(fala);
}

export function pararDeFalar(): void {
  if (vozDisponivel()) {
    window.speechSynthesis.cancel();
  }
}
