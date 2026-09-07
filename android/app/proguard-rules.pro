# O cliente da API monta o JSON na mao com org.json, sem reflexao, entao nao
# ha modelo que precise sobreviver a ofuscacao. Se um serializador por
# reflexao entrar depois, as regras de keep vem para ca.

# Mantem os nomes das constantes de enum: elas viajam no JSON como texto
# ("DICIONARIO", "IA") e a ofuscacao quebraria a leitura da resposta.
-keepclassmembers enum br.com.tradugiria.android.dominio.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
