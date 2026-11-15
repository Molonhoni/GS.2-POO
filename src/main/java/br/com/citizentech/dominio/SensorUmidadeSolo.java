
package br.com.citizentech.territory.dominio;

public class SensorUmidadeSolo extends SensorSimulado {

    public SensorUmidadeSolo(String id, String nome, String regiao) {
        super(id, nome, regiao);
    }

    @Override
    public double lerValor() {
        return gerarLeitura(100.0);
    }

    @Override
    public String retornarTipo() {
        return "UMIDADE_SOLO";
    }

    @Override
    public String retornarUnidade() {
        return "%";
    }
}
