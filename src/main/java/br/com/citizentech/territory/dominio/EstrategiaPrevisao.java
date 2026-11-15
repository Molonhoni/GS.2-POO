package br.com.citizentech.territory.dominio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.com.citizentech.territory.excecao.DadosInvalidosException;

public abstract class EstrategiaPrevisao {

    private final String nome;

    protected EstrategiaPrevisao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    protected List<Medicao> prepararHistorico(
            List<Medicao> historico,
            int minimoLeituras) {

        if (historico == null
                || historico.size() < minimoLeituras) {

            throw new DadosInvalidosException(
                "Historico insuficiente. Sao necessarias "
                + minimoLeituras + " medicoes."
            );
        }

        List<Medicao> medicoes =
                new ArrayList<>(historico);

        String sensorReferencia = null;
        String tipoReferencia = null;

        for (Medicao medicao : medicoes) {

            if (medicao == null
                    || medicao.getSensorId() == null
                    || medicao.getTipoSensor() == null
                    || medicao.getDataHora() == null
                    || !Double.isFinite(medicao.getValor())
                    || medicao.getValor() < 0) {

                throw new DadosInvalidosException(
                    "O historico contem medicoes invalidas."
                );
            }

            if (sensorReferencia == null) {
                sensorReferencia = medicao.getSensorId();
                tipoReferencia = medicao.getTipoSensor();
            }

            if (!sensorReferencia.equals(
                    medicao.getSensorId())
                    || !tipoReferencia.equals(
                    medicao.getTipoSensor())) {

                throw new DadosInvalidosException(
                    "O historico deve pertencer ao mesmo sensor."
                );
            }
        }

        medicoes.sort(
            Comparator.comparing(Medicao::getDataHora)
                .reversed()
        );

        return medicoes;
    }

    protected double calcularMedia(
            List<Medicao> medicoes) {

        if (medicoes == null || medicoes.isEmpty()) {
            throw new DadosInvalidosException(
                "Nao existem medicoes para calcular a media."
            );
        }

        double soma = 0;

        for (Medicao medicao : medicoes) {
            soma += medicao.getValor();
        }

        return soma / medicoes.size();
    }

    public abstract ResultadoPrevisao prever(
            List<Medicao> historico);
}
