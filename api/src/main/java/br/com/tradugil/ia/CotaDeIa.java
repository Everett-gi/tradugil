package br.com.tradugil.ia;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Teto diário de chamadas à IA.
 *
 * <p>É a defesa contra a ameaça de negação de serviço do threat model
 * (seção 8.3): o {@code /traduzir} é público, e sem teto alguém pode
 * automatizar consultas de termos inventados para gerar custo de IA. O
 * projeto não tem verba para descobrir isso pela fatura.</p>
 *
 * <p>O contador vive em memória, e não no banco, porque a infraestrutura
 * alvo é uma instância só no Always Free. Com mais de uma instância, cada
 * uma teria seu próprio teto e o gasto real seria o teto vezes o número de
 * instâncias: nesse dia isto precisa virar um contador compartilhado.</p>
 *
 * <p>A virada do dia é em UTC, não no fuso local: a única coisa que importa
 * é que a janela seja consistente, e depender do fuso do servidor faria a
 * cota reiniciar em horas diferentes conforme onde ele estivesse rodando.</p>
 */
public class CotaDeIa {

    private final int limiteDiario;
    private final AtomicInteger usadas = new AtomicInteger();
    private volatile LocalDate diaVigente = hoje();

    public CotaDeIa(int limiteDiario) {
        this.limiteDiario = limiteDiario;
    }

    /** Se ainda há saldo, sem consumir. */
    public boolean temSaldo() {
        virarODiaSePreciso();
        return usadas.get() < limiteDiario;
    }

    /**
     * Consome uma unidade se houver saldo.
     *
     * @return {@code true} se a chamada está autorizada
     */
    public boolean consumir() {
        virarODiaSePreciso();
        // incrementAndGet e não "checar depois somar": duas requisições
        // simultâneas no limite passariam as duas se a checagem e a soma
        // fossem passos separados.
        return usadas.incrementAndGet() <= limiteDiario;
    }

    public int usadasHoje() {
        virarODiaSePreciso();
        return Math.min(usadas.get(), limiteDiario);
    }

    public int limiteDiario() {
        return limiteDiario;
    }

    private synchronized void virarODiaSePreciso() {
        LocalDate agora = hoje();
        if (!agora.equals(diaVigente)) {
            diaVigente = agora;
            usadas.set(0);
        }
    }

    private static LocalDate hoje() {
        return LocalDate.now(ZoneOffset.UTC);
    }
}
