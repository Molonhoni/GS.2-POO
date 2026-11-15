
package br.com.citizentech.territory.dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.com.citizentech.territory.excecao.DadosInvalidosException;
import br.com.citizentech.territory.excecao.FonteIndisponivelException;

class DadosTerritorioTest {

    @Test
    void deveIniciarComLimiaresPadrao() {

        // Arrange
        DadosTerritorio territorio =
                new DadosTerritorio("Zona Norte", "codigo123");

        // Act
        LimiaresAlerta chuva =
                territorio.obterLimiares("CHUVA");

        // Assert
        assertEquals(30.0, chuva.getAtencao());
        assertEquals(60.0, chuva.getAlerta());
        assertEquals(90.0, chuva.getCritico());
    }

    @Test
    void deveAtualizarLimiaresComCodigoCorreto() {

        // Arrange
        DadosTerritorio territorio =
                new DadosTerritorio("Zona Norte", "codigo123");

        // Act
        territorio.atualizarLimiares(
            "CHUVA", 20, 40, 80, "codigo123"
        );

        // Assert
        LimiaresAlerta chuva =
                territorio.obterLimiares("CHUVA");

        assertEquals(20.0, chuva.getAtencao());
        assertEquals(40.0, chuva.getAlerta());
        assertEquals(80.0, chuva.getCritico());
    }

    @Test
    void deveRejeitarCodigoIncorreto() {

        // Arrange
        DadosTerritorio territorio =
                new DadosTerritorio("Zona Norte", "codigo123");

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> territorio.atualizarLimiares(
                "CHUVA", 20, 40, 80, "errado"
            )
        );

        assertEquals(
            30.0,
            territorio.obterLimiares("CHUVA").getAtencao()
        );
    }

    @Test
    void deveRejeitarLimiaresInvalidos() {

        // Arrange
        DadosTerritorio territorio =
                new DadosTerritorio("Zona Norte", "codigo123");

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> territorio.atualizarLimiares(
                "CHUVA", 90, 60, 120, "codigo123"
            )
        );
    }

    @Test
    void deveClassificarCorretamenteOsNiveis() {

        // Arrange
        LimiaresAlerta limites =
                new LimiaresAlerta(30, 60, 90);

        // Act e Assert
        assertEquals(
            NivelAlerta.NORMAL,
            limites.classificar(10)
        );

        assertEquals(
            NivelAlerta.ATENCAO,
            limites.classificar(30)
        );

        assertEquals(
            NivelAlerta.ALERTA,
            limites.classificar(60)
        );

        assertEquals(
            NivelAlerta.CRITICO,
            limites.classificar(90)
        );
    }

    @Test
    void deveRejeitarMedicaoNegativa() {

        // Arrange
        LimiaresAlerta limites =
                new LimiaresAlerta(30, 60, 90);

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> limites.classificar(-10)
        );
    }

    @Test
    void deveRejeitarSensorIndisponivel() {

        // Arrange
        SensorChuva sensor =
                new SensorChuva(
                    "S01",
                    "Pluviometro Central",
                    "Zona Norte"
                );

        sensor.alterarStatus(false);

        // Act e Assert
        assertThrows(
            FonteIndisponivelException.class,
            sensor::lerValor
        );
    }

    @Test
    void deveRejeitarRegiaoVazia() {

        // Arrange, Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> new DadosTerritorio("", "codigo123")
        );
    }
}
