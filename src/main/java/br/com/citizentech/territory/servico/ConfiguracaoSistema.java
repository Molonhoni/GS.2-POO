package br.com.citizentech.territory.servico;

import br.com.citizentech.territory.dominio.DadosTerritorio;
import br.com.citizentech.territory.excecao.DadosInvalidosException;

public final class ConfiguracaoSistema {

    private static final ConfiguracaoSistema INSTANCIA =
            new ConfiguracaoSistema();

    private volatile DadosTerritorio dadosTerritorio;

    private ConfiguracaoSistema() {
        // Impede instanciacao externa.
    }

    public static ConfiguracaoSistema getInstancia() {
        return INSTANCIA;
    }

    public synchronized void inicializarTerritorio(
            DadosTerritorio territorio) {

        if (territorio == null) {
            throw new DadosInvalidosException(
                "Os dados do territorio sao obrigatorios."
            );
        }

        if (this.dadosTerritorio != null) {
            throw new IllegalStateException(
                "O territorio ja foi inicializado."
            );
        }

        this.dadosTerritorio = territorio;
    }

    public DadosTerritorio obterDadosTerritorio() {

        DadosTerritorio territorio = dadosTerritorio;

        if (territorio == null) {
            throw new IllegalStateException(
                "O territorio ainda nao foi configurado."
            );
        }

        return territorio;
    }

    public String obterRegiao() {
        return obterDadosTerritorio().getRegiao();
    }
}
