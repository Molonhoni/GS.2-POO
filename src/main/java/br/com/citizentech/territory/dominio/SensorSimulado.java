
package br.com.citizentech.territory.dominio;

import java.util.concurrent.ThreadLocalRandom;

import br.com.citizentech.territory.excecao.FonteIndisponivelException;

public abstract class SensorSimulado
        extends ComponenteTerritorial
        implements Sensor {

    protected SensorSimulado(
            String id,
            String nome,
            String regiao) {

        super(id, nome, regiao);
    }

    @Override
    public boolean verificarFuncionamento() {
        return isAtivo();
    }

    @Override
    public String verificarStatus() {
        return verificarFuncionamento()
                ? "OPERACIONAL"
                : "INDISPONIVEL";
    }

    protected double gerarLeitura(double valorMaximo) {

        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException(
                "O sensor " + getNome()
                + " esta indisponivel."
            );
        }

        double valor = ThreadLocalRandom.current()
                .nextDouble(0.0, valorMaximo);

        return Math.round(valor * 10.0) / 10.0;
    }
}
