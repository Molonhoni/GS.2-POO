package br.com.citizentech.territory.dominio;

import java.time.LocalDateTime;
import java.util.Locale;

import br.com.citizentech.territory.excecao.DadosInvalidosException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String sensorId;

    @Column(nullable = false, length = 40)
    private String tipoSensor;

    @Column(nullable = false, length = 100)
    private String regiao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelAlerta nivel;

    @Column(nullable = false)
    private double valorPrevisto;

    @Column(nullable = false)
    private double limiarAtingido;

    @Column(nullable = false, length = 60)
    private String estrategia;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected Alerta() {
        // Construtor exigido pelo JPA.
    }

    public Alerta(
            SensorSimulado sensor,
            NivelAlerta nivel,
            double valorPrevisto,
            double limiarAtingido,
            String estrategia) {

        if (sensor == null) {
            throw new DadosInvalidosException(
                "O sensor do alerta e obrigatorio."
            );
        }

        if (nivel == null || nivel == NivelAlerta.NORMAL) {
            throw new DadosInvalidosException(
                "O alerta precisa ter um nivel de risco valido."
            );
        }

        if (!Double.isFinite(valorPrevisto)
                || valorPrevisto < 0
                || !Double.isFinite(limiarAtingido)
                || limiarAtingido < 0
                || valorPrevisto < limiarAtingido) {

            throw new DadosInvalidosException(
                "Valores invalidos para o alerta."
            );
        }

        if (estrategia == null || estrategia.isBlank()) {
            throw new DadosInvalidosException(
                "A estrategia de previsao e obrigatoria."
            );
        }

        this.sensorId = sensor.getId();
        this.tipoSensor = sensor.retornarTipo();
        this.regiao = sensor.getRegiao();
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limiarAtingido = limiarAtingido;
        this.estrategia = estrategia.trim();
        this.dataHora = LocalDateTime.now();

        this.mensagem = String.format(
            Locale.forLanguageTag("pt-BR"),
            "Sensor %s (%s): previsao de %.1f %s "
            + "atingiu o limite de %.1f %s. "
            + "Nivel: %s. Estrategia: %s.",
            sensor.getNome(),
            sensor.retornarTipo(),
            valorPrevisto,
            sensor.retornarUnidade(),
            limiarAtingido,
            sensor.retornarUnidade(),
            nivel,
            this.estrategia
        );
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public String getRegiao() {
        return regiao;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimiarAtingido() {
        return limiarAtingido;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
