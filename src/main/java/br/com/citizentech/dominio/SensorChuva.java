
package br.com.citizentech.territory.dominio;

public class SensorChuva extends SensorSimulado {

    public SensorChuva(String id, String nome, String regiao) {
        super(id, nome, regiao);
    }

    @Override
    public double lerValor() {
        return gerarLeitura(120.0);
    }

    @Override
    public String retornarTipo() {
        return "CHUVA";
    }

    @Override
    public String retornarUnidade() {
        return "mm/h";
    }
}
