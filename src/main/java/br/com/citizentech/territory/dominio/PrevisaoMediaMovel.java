package br.com.citizentech.territory.dominio;

import java.util.List;
import java.util.Locale;

public class PrevisaoMediaMovel
        extends EstrategiaPrevisao {

    private static final int JANELA = 5;

    public PrevisaoMediaMovel() {
        super("MEDIA_MOVEL");
    }

    @Override
    public ResultadoPrevisao prever(
            List<Medicao> historico) {

        List<Medicao> medicoes =
            super.prepararHistorico(historico, JANELA);

        List<Medicao> recentes =
            medicoes.subList(0, JANELA);

        double media = super.calcularMedia(recentes);

        Medicao ultimaMedicao = medicoes.get(0);

        String justificativa = String.format(
            Locale.forLanguageTag("pt-BR"),
            "Media das ultimas %d medicoes: %.2f %s.",
            JANELA,
            media,
            ultimaMedicao.getUnidade()
        );

        return new ResultadoPrevisao(
            ultimaMedicao.getSensorId(),
            ultimaMedicao.getTipoSensor(),
            ultimaMedicao.getUnidade(),
            media,
            getNome(),
            justificativa,
            JANELA
        );
    }
}
