package br.com.citizentech.territory.dominio;

import java.util.List;
import java.util.Locale;

public class PrevisaoComparacao
        extends EstrategiaPrevisao {

    private static final int JANELA = 5;
    private static final int TOTAL_LEITURAS = 10;

    public PrevisaoComparacao() {
        super("COMPARACAO");
    }

    @Override
    public ResultadoPrevisao prever(
            List<Medicao> historico) {

        List<Medicao> medicoes =
            super.prepararHistorico(
                historico,
                TOTAL_LEITURAS
            );

        List<Medicao> recentes =
            medicoes.subList(0, JANELA);

        List<Medicao> anteriores =
            medicoes.subList(JANELA, TOTAL_LEITURAS);

        double mediaRecente =
            super.calcularMedia(recentes);

        double mediaAnterior =
            super.calcularMedia(anteriores);

        double variacao =
            mediaRecente - mediaAnterior;

        double valorPrevisto =
            Math.max(0, mediaRecente + variacao);

        Medicao ultimaMedicao = medicoes.get(0);

        String justificativa = String.format(
            Locale.forLanguageTag("pt-BR"),
            "Media recente: %.2f %s. "
            + "Media anterior: %.2f %s. "
            + "Variacao: %+.2f %s.",
            mediaRecente,
            ultimaMedicao.getUnidade(),
            mediaAnterior,
            ultimaMedicao.getUnidade(),
            variacao,
            ultimaMedicao.getUnidade()
        );

        return new ResultadoPrevisao(
            ultimaMedicao.getSensorId(),
            ultimaMedicao.getTipoSensor(),
            ultimaMedicao.getUnidade(),
            valorPrevisto,
            getNome(),
            justificativa,
            TOTAL_LEITURAS
        );
    }
}
