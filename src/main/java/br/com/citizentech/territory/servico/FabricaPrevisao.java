package br.com.citizentech.territory.servico;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import br.com.citizentech.territory.dominio.EstrategiaPrevisao;
import br.com.citizentech.territory.dominio.PrevisaoComparacao;
import br.com.citizentech.territory.dominio.PrevisaoMediaMovel;
import br.com.citizentech.territory.excecao.DadosInvalidosException;

public class FabricaPrevisao {

    private final Map<String, Supplier<EstrategiaPrevisao>>
            estrategias;

    public FabricaPrevisao() {

        estrategias = Map.of(
            "MEDIA_MOVEL",
            PrevisaoMediaMovel::new,

            "COMPARACAO",
            PrevisaoComparacao::new
        );
    }

    public EstrategiaPrevisao criar(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            throw new DadosInvalidosException(
                "O tipo de previsao e obrigatorio."
            );
        }

        String tipoNormalizado = tipo.trim()
                .toUpperCase(Locale.ROOT);

        Supplier<EstrategiaPrevisao> criador =
                estrategias.get(tipoNormalizado);

        if (criador == null) {
            throw new DadosInvalidosException(
                "Estrategia de previsao desconhecida: "
                + tipoNormalizado
            );
        }

        return criador.get();
    }
}
