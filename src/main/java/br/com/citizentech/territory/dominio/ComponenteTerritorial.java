
package br.com.citizentech.territory.dominio;

import br.com.citizentech.territory.excecao.DadosInvalidosException;

public abstract class ComponenteTerritorial {

    private final String id;
    private final String nome;
    private final String regiao;
    private boolean ativo;

    protected ComponenteTerritorial(
            String id,
            String nome,
            String regiao) {

        this.id = validarTexto(id, "ID");
        this.nome = validarTexto(nome, "Nome");
        this.regiao = validarTexto(regiao, "Regiao");
        this.ativo = true;
    }

    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(
                campo + " nao pode ser vazio."
            );
        }

        return valor.trim();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void alterarStatus(boolean ativo) {
        this.ativo = ativo;
    }

    public abstract String verificarStatus();
}
