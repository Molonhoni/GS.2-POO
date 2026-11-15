
package br.com.citizentech.territory.dominio;

public class SensorNivelAgua extends SensorSimulado {

    public SensorNivelAgua(String id, String nome, String regiao) {
        super(id, nome, regiao);
    }

    @Override
    public double lerValor() {
        return gerarLeitura(300.0);
    }

    @Override
    public String retornarTipo() {
        return "NIVEL_AGUA";
    }

    @Override
    public String retornarUnidade() {
        return "cm";
    }
}
