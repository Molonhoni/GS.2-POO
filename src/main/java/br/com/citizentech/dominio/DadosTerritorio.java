
package br.com.citizentech.territory.dominio;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import br.com.citizentech.territory.excecao.DadosInvalidosException;

public class DadosTerritorio {

    private final String regiao;
    private final String codigoAcesso;

    private final Map<String, LimiaresAlerta> limiares;

    public DadosTerritorio(
            String regiao,
            String codigoAcesso) {

        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException(
                "A regiao nao pode ser vazia."
            );
        }

        if (codigoAcesso == null
                || codigoAcesso.isBlank()) {
            throw new DadosInvalidosException(
                "O codigo de acesso e obrigatorio."
            );
        }

        this.regiao = regiao.trim();
        this.codigoAcesso = codigoAcesso;
        this.limiares = new HashMap<>();

        configurarLimiaresIniciais();
    }

    private void configurarLimiaresIniciais() {

        limiares.put(
            "CHUVA",
            new LimiaresAlerta(30.0, 60.0, 90.0)
        );

        limiares.put(
            "NIVEL_AGUA",
            new LimiaresAlerta(80.0, 150.0, 220.0)
        );

        limiares.put(
            "UMIDADE_SOLO",
            new LimiaresAlerta(50.0, 70.0, 90.0)
        );
    }

    private String normalizarTipo(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            throw new DadosInvalidosException(
                "O tipo do sensor e obrigatorio."
            );
        }

        return tipo.trim().toUpperCase(Locale.ROOT);
    }

    public LimiaresAlerta obterLimiares(String tipo) {

        String tipoNormalizado = normalizarTipo(tipo);

        LimiaresAlerta resultado =
                limiares.get(tipoNormalizado);

        if (resultado == null) {
            throw new DadosInvalidosException(
                "Tipo de sensor desconhecido: "
                + tipoNormalizado
            );
        }

        return resultado;
    }

    public void atualizarLimiares(
            String tipo,
            double atencao,
            double alerta,
            double critico,
            String codigoInformado) {

        if (!codigoAcesso.equals(codigoInformado)) {
            throw new DadosInvalidosException(
                "Codigo de acesso invalido."
            );
        }

        String tipoNormalizado = normalizarTipo(tipo);

        if (!limiares.containsKey(tipoNormalizado)) {
            throw new DadosInvalidosException(
                "Nao existe configuracao para: "
                + tipoNormalizado
            );
        }

        LimiaresAlerta novosLimiares =
                new LimiaresAlerta(
                    atencao,
                    alerta,
                    critico
                );

        limiares.put(tipoNormalizado, novosLimiares);
    }

    public String getRegiao() {
        return regiao;
    }
}
