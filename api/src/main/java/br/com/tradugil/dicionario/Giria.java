package br.com.tradugil.dicionario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import br.com.tradugil.traducao.Normalizador;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * O termo. Guarda só o que é do termo em si: o significado mora em
 * {@link Definicao}, porque a mesma gíria carrega sentidos diferentes que
 * são aprovados, rejeitados e votados separadamente.
 */
@Entity
@Table(name = "giria")
public class Giria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Grafia de exibição, com acento e maiúsculas como devem aparecer. */
    @Column(nullable = false, length = 80)
    private String termo;

    /** Chave de busca. Ver {@code Normalizador}: a regra é compartilhada. */
    @Column(name = "termo_normalizado", nullable = false, length = 80)
    private String termoNormalizado;

    /**
     * Chave de busca com a ênfase repetida colapsada ({@code kkkkkkk} vira
     * {@code kk}). Existe como coluna, e não como cálculo na consulta, porque
     * precisa de índice: sem ele, cada consulta com repetição varreria a
     * tabela inteira.
     */
    @Column(name = "termo_colapsado", nullable = false, length = 80)
    private String termoColapsado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idioma_id", nullable = false)
    private Idioma idioma;

    /** Conteúdo impróprio: some da resposta quando o Modo Família está ligado. */
    @Column(nullable = false)
    private boolean nsfw;

    /**
     * Termo associado a comportamento de risco para menores. Diferente de
     * {@link #nsfw}: não é ocultado, é <i>destacado</i>. Esconder da Marlene
     * ou do Roberto justamente o termo que eles precisam entender inverteria
     * a finalidade do Modo Família.
     */
    @Column(name = "risco_menor", nullable = false)
    private boolean riscoMenor;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @OneToMany(mappedBy = "giria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Definicao> definicoes = new ArrayList<>();

    @OneToMany(mappedBy = "giria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GiriaVariacao> variacoes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "giria_categoria",
            joinColumns = @JoinColumn(name = "giria_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private List<Categoria> categorias = new ArrayList<>();

    protected Giria() {
    }

    /**
     * Cria um verbete a partir de uma proposta aprovada na moderação.
     *
     * <p>As duas chaves de busca são <b>calculadas aqui</b>, e não recebidas.
     * É o mesmo cuidado que existe no gerador de migrações, e pelo mesmo
     * motivo: chave escrita à mão erra em silêncio, o verbete entra no banco
     * e nunca é encontrado por ninguém, porque não corresponde ao que o
     * cliente calcula na busca.</p>
     *
     * <p>Nasce sem marca de conteúdo impróprio nem de risco. Quem aprovou não
     * tem como classificar isso pelo formulário atual, e o padrão precisa ser
     * o que não esconde nada indevidamente: um termo delicado que passe por
     * aqui é problema da moderação, não do valor inicial de uma coluna.</p>
     */
    public static Giria daComunidade(String termo, Idioma idioma) {
        Giria nova = new Giria();
        nova.termo = termo.trim();
        nova.termoNormalizado = Normalizador.normalizar(nova.termo);
        nova.termoColapsado = Normalizador.colapsarRepeticoes(nova.termoNormalizado);
        nova.idioma = idioma;
        nova.nsfw = false;
        nova.riscoMenor = false;
        return nova;
    }

    public void acrescentar(Definicao definicao) {
        definicoes.add(definicao);
    }

    /**
     * Põe o verbete numa prateleira do catálogo.
     *
     * <p>Idempotente de propósito. A moderação escolhe a categoria a cada
     * aprovação, e um termo que já existe recebe sentidos novos ao longo do
     * tempo: sem a checagem, o segundo sentido aprovado na mesma prateleira
     * inseriria a linha de ligação duas vezes e o verbete apareceria
     * repetido dentro da própria categoria.</p>
     */
    public void entrarNaPrateleira(Categoria categoria) {
        boolean jaEsta = categorias.stream()
                .anyMatch(c -> c.getId().equals(categoria.getId()));
        if (!jaEsta) {
            categorias.add(categoria);
        }
    }

    /**
     * Definições publicáveis, da mais útil para a menos útil.
     *
     * <p>A ordenação por votos é o que faz o dicionário melhorar sozinho: o
     * sentido que as pessoas marcam como útil sobe e passa a ser o primeiro
     * que aparece na consulta rápida.</p>
     */
    public List<Definicao> definicoesAprovadas() {
        return definicoes.stream()
                .filter(Definicao::estaAprovada)
                .sorted((a, b) -> Integer.compare(b.getVotosUteis(), a.getVotosUteis()))
                .toList();
    }

    public Long getId() {
        return id;
    }

    public String getTermo() {
        return termo;
    }

    public String getTermoNormalizado() {
        return termoNormalizado;
    }

    public String getTermoColapsado() {
        return termoColapsado;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public boolean isRiscoMenor() {
        return riscoMenor;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public List<Definicao> getDefinicoes() {
        return definicoes;
    }

    public List<GiriaVariacao> getVariacoes() {
        return variacoes;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }
}
