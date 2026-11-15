
package br.com.citizentech.territory.dominio;

public interface Sensor {

    double lerValor();

    boolean verificarFuncionamento();

    String retornarTipo();

    String retornarUnidade();
}
