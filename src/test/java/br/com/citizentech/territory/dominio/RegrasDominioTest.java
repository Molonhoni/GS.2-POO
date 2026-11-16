package br.com.citizentech.territory.dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.com.citizentech.territory.excecao.DadosInvalidosException;

class RegrasDominioTest {

    private SensorChuva criarSensor() {
        return new SensorChuva(
            "S01", "Pluviometro Central", "Zona Norte"
        );
    }

    @Test
    void deveRejeitarMedicaoAcimaDoLimite() {

        // Arrange
        SensorChuva sensor = criarSensor();

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> new Medicao(sensor, 150.0)
        );
    }

    @Test
    void deveRejeitarMedicaoNaN() {

        // Arrange
        SensorChuva sensor = criarSensor();

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> new Medicao(sensor, Double.NaN)
        );
    }

    @Test
    void deveRejeitarAlertaComNivelNormal() {

        // Arrange
        SensorChuva sensor = criarSensor();

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> new Alerta(
                sensor,
                NivelAlerta.NORMAL,
                40.0,
                30.0,
                "MEDIA_MOVEL"
            )
        );
    }

    @Test
    void deveRejeitarAlertaComPrevisaoAbaixoDoLimiar() {

        // Arrange
        SensorChuva sensor = criarSensor();

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> new Alerta(
                sensor,
                NivelAlerta.ALERTA,
                50.0,
                60.0,
                "MEDIA_MOVEL"
            )
        );
    }

    @Test
    void deveRejeitarResultadoNormalComAlerta() {

        // Arrange
        SensorChuva sensor = criarSensor();

        ResultadoPrevisao previsao =
            new ResultadoPrevisao(
                "S01", "CHUVA", "mm/h",
                40.0, "MEDIA_MOVEL",
                "Media das ultimas cinco medicoes", 5
            );

        Alerta alerta = new Alerta(
            sensor,
            NivelAlerta.ATENCAO,
            40.0,
            30.0,
            "MEDIA_MOVEL"
        );

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> new ResultadoMonitoramento(
                previsao, NivelAlerta.NORMAL, alerta
            )
        );
    }

    @Test
    void deveRejeitarResultadoDeRiscoSemAlerta() {

        // Arrange
        ResultadoPrevisao previsao =
            new ResultadoPrevisao(
                "S01", "CHUVA", "mm/h",
                75.0, "MEDIA_MOVEL",
                "Media das ultimas cinco medicoes", 5
            );

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> new ResultadoMonitoramento(
                previsao, NivelAlerta.ALERTA, null
            )
        );
    }
}
