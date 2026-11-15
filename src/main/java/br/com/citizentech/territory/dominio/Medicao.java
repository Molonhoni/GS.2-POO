package br.com.citizentech.territory.dominio;

import java.time.LocalDateTime;

import br.com.citizentech.territory.excecao.DadosInvalidosException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_medicao")
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String sensorId;

    @Column(nullable = false, length = 100)
    private String nomeSensor;

    @Column(nullable = false, length = 40)
    private String tipoSensor;

    @Column(nullable = false, length = 100)
    private String regiao;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false, length = 12)
    private String unidade;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected Medicao() {
        // Construtor exigido pelo JPA.
    }

    public Medicao(SensorSimulado sensor, double valor) {
        this(sensor, valor, LocalDateTime.now());
    }

    // Permite definir horarios controlados nos testes.
    Medicao(
            SensorSimulado sensor,
            double valor,
            LocalDateTime dataHora) {

        if (sensor == null) {
            throw new DadosInvalidosException(
                "O sensor nao pode ser nulo."
            );
        }

        if (!Double.isFinite(valor) || valor < 0) {
            throw new DadosInvalidosException(
                "A medicao deve ser um numero valido "
                + "e nao negativo."
            );
        }

        if (dataHora == null) {
            throw new DadosInvalidosException(
                "A data e hora da medicao sao obrigatorias."
            );
        }

        double valorMaximo;

        switch (sensor.retornarTipo()) {
            case "CHUVA":
                valorMaximo = 120.0;
                break;

            case "NIVEL_AGUA":
                valorMaximo = 300.0;
                break;

            case "UMIDADE_SOLO":
                valorMaximo = 100.0;
                break;

            default:
                throw new DadosInvalidosException(
                    "Tipo de sensor desconhecido."
                );
        }

        if (valor > valorMaximo) {
            throw new DadosInvalidosException(
                "Medicao fora da faixa permitida para "
                + sensor.retornarTipo()
            );
        }

        this.sensorId = sensor.getId();
        this.nomeSensor = sensor.getNome();
        this.tipoSensor = sensor.retornarTipo();
        this.regiao = sensor.getRegiao();
        this.valor = valor;
        this.unidade = sensor.retornarUnidade();
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getNomeSensor() {
        return nomeSensor;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public String getRegiao() {
        return regiao;
    }

    public double getValor() {
        return valor;
    }

    public String getUnidade() {
        return unidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
