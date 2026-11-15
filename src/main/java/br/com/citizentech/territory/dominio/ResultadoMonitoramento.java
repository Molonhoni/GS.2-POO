package br.com.citizentech.territory.dominio;

import br.com.citizentech.territory.excecao.DadosInvalidosException;

public class ResultadoMonitoramento {

    private final ResultadoPrevisao previsao;
    private final NivelAlerta nivel;
    private final Alerta alerta;

    public ResultadoMonitoramento(
            ResultadoPrevisao previsao,
            NivelAlerta nivel,
            Alerta alerta) {

        if (previsao == null || nivel == null) {
            throw new DadosInvalidosException(
                "O resultado e o nivel sao obrigatorios."
            );
        }

        if (nivel == NivelAlerta.NORMAL && alerta != null) {
            throw new DadosInvalidosException(
                "Uma previsao normal nao deve gerar alerta."
            );
        }

        if (nivel != NivelAlerta.NORMAL && alerta == null) {
            throw new DadosInvalidosException(
                "Um nivel de risco deve possuir alerta."
            );
        }

        this.previsao = previsao;
        this.nivel = nivel;
        this.alerta = alerta;
    }

    public ResultadoPrevisao getPrevisao() {
        return previsao;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public Alerta getAlerta() {
        return alerta;
    }

    public boolean possuiAlerta() {
        return alerta != null;
    }
}
