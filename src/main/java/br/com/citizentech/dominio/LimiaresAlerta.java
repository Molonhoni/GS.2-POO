
package br.com.citizentech.territory.dominio;

import br.com.citizentech.territory.excecao.DadosInvalidosException;

public class LimiaresAlerta {

    private final double atencao;
    private final double alerta;
    private final double critico;

    public LimiaresAlerta(
            double atencao,
            double alerta,
            double critico) {

        if (!Double.isFinite(atencao)
                || !Double.isFinite(alerta)
                || !Double.isFinite(critico)
                || atencao < 0
                || atencao >= alerta
                || alerta >= critico) {

            throw new DadosInvalidosException(
                "Limiares invalidos: os valores devem ser "
                + "finitos, nao negativos e crescentes."
            );
        }

        this.atencao = atencao;
        this.alerta = alerta;
        this.critico = critico;
    }

    public NivelAlerta classificar(double valor) {

        if (!Double.isFinite(valor) || valor < 0) {
            throw new DadosInvalidosException(
                "O valor da medicao ou previsao e invalido."
            );
        }

        if (valor >= critico) {
            return NivelAlerta.CRITICO;
        }

        if (valor >= alerta) {
            return NivelAlerta.ALERTA;
        }

        if (valor >= atencao) {
            return NivelAlerta.ATENCAO;
        }

        return NivelAlerta.NORMAL;
    }

    public double getAtencao() {
        return atencao;
    }

    public double getAlerta() {
        return alerta;
    }

    public double getCritico() {
        return critico;
    }
}
