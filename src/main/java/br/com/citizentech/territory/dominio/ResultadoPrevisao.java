package br.com.citizentech.territory.dominio;

import br.com.citizentech.territory.excecao.DadosInvalidosException;

public class ResultadoPrevisao {

    private final String sensorId;
    private final String tipoSensor;
    private final String unidade;
    private final double valorPrevisto;
    private final String estrategia;
    private final String justificativa;
    private final int quantidadeLeituras;

    public ResultadoPrevisao(
            String sensorId,
            String tipoSensor,
            String unidade,
            double valorPrevisto,
            String estrategia,
            String justificativa,
            int quantidadeLeituras) {

        if (!Double.isFinite(valorPrevisto)
                || valorPrevisto < 0) {
            throw new DadosInvalidosException(
                "O valor previsto deve ser valido e nao negativo."
            );
        }

        if (quantidadeLeituras <= 0) {
            throw new DadosInvalidosException(
                "A previsao precisa utilizar medicoes."
            );
        }

        if (sensorId == null || sensorId.isBlank()
                || tipoSensor == null || tipoSensor.isBlank()
                || unidade == null || unidade.isBlank()
                || estrategia == null || estrategia.isBlank()
                || justificativa == null
                || justificativa.isBlank()) {

            throw new DadosInvalidosException(
                "Os dados do resultado da previsao sao obrigatorios."
            );
        }

        this.sensorId = sensorId;
        this.tipoSensor = tipoSensor;
        this.unidade = unidade;
        this.valorPrevisto = valorPrevisto;
        this.estrategia = estrategia;
        this.justificativa = justificativa;
        this.quantidadeLeituras = quantidadeLeituras;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public int getQuantidadeLeituras() {
        return quantidadeLeituras;
    }
}
