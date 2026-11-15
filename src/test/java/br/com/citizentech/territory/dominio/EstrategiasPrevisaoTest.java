package br.com.citizentech.territory.dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.citizentech.territory.excecao.DadosInvalidosException;

class EstrategiasPrevisaoTest {

    private List<Medicao> criarHistorico(double... valores) {

        SensorChuva sensor = new SensorChuva(
            "S01",
            "Pluviometro Central",
            "Zona Norte"
        );

        List<Medicao> historico = new ArrayList<>();

        LocalDateTime inicio =
            LocalDateTime.of(2026, 1, 1, 12, 0);

        for (int i = 0; i < valores.length; i++) {

            LocalDateTime horario =
                inicio.plusMinutes(i);

            Medicao medicao = new Medicao(
                sensor,
                valores[i],
                horario
            );

            historico.add(medicao);
        }

        return historico;
    }

    @Test
    void deveCalcularMediaMovelCorretamente() {

        // Arrange
        EstrategiaPrevisao estrategia =
            new PrevisaoMediaMovel();

        List<Medicao> historico =
            criarHistorico(10, 20, 30, 40, 50);

        // Act
        ResultadoPrevisao resultado =
            estrategia.prever(historico);

        // Assert
        assertEquals(
            30.0,
            resultado.getValorPrevisto(),
            0.001
        );

        assertEquals(
            "MEDIA_MOVEL",
            resultado.getEstrategia()
        );

        assertEquals(
            5,
            resultado.getQuantidadeLeituras()
        );
    }

    @Test
    void deveConsiderarSomenteAsCincoUltimasLeituras() {

        // Arrange
        EstrategiaPrevisao estrategia =
            new PrevisaoMediaMovel();

        List<Medicao> historico =
            criarHistorico(
                100, 100, 10, 20, 30, 40, 50
            );

        // Act
        ResultadoPrevisao resultado =
            estrategia.prever(historico);

        // Assert
        assertEquals(
            30.0,
            resultado.getValorPrevisto(),
            0.001
        );
    }

    @Test
    void deveProjetarTendenciaDeCrescimento() {

        // Arrange
        EstrategiaPrevisao estrategia =
            new PrevisaoComparacao();

        List<Medicao> historico = criarHistorico(
            20, 20, 20, 20, 20,
            30, 30, 30, 30, 30
        );

        // Act
        ResultadoPrevisao resultado =
            estrategia.prever(historico);

        // Assert
        assertEquals(
            40.0,
            resultado.getValorPrevisto(),
            0.001
        );

        assertEquals(
            "COMPARACAO",
            resultado.getEstrategia()
        );

        assertEquals(
            10,
            resultado.getQuantidadeLeituras()
        );
    }

    @Test
    void naoDeveProduzirPrevisaoNegativa() {

        // Arrange
        EstrategiaPrevisao estrategia =
            new PrevisaoComparacao();

        List<Medicao> historico = criarHistorico(
            30, 30, 30, 30, 30,
            10, 10, 10, 10, 10
        );

        // Act
        ResultadoPrevisao resultado =
            estrategia.prever(historico);

        // Assert
        assertEquals(
            0.0,
            resultado.getValorPrevisto(),
            0.001
        );
    }

    @Test
    void deveRejeitarHistoricoInsuficienteParaMedia() {

        // Arrange
        EstrategiaPrevisao estrategia =
            new PrevisaoMediaMovel();

        List<Medicao> historico =
            criarHistorico(10, 20, 30, 40);

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> estrategia.prever(historico)
        );
    }

    @Test
    void deveRejeitarHistoricoInsuficienteParaComparacao() {

        // Arrange
        EstrategiaPrevisao estrategia =
            new PrevisaoComparacao();

        List<Medicao> historico = criarHistorico(
            10, 20, 30, 40, 50,
            60, 70, 80, 90
        );

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> estrategia.prever(historico)
        );
    }

    @Test
    void deveRejeitarMedicoesDeSensoresDiferentes() {

        // Arrange
        EstrategiaPrevisao estrategia =
            new PrevisaoMediaMovel();

        List<Medicao> historico =
            criarHistorico(10, 20, 30, 40);

        SensorChuva outroSensor = new SensorChuva(
            "S02",
            "Pluviometro Secundario",
            "Zona Sul"
        );

        historico.add(
            new Medicao(outroSensor, 50)
        );

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> estrategia.prever(historico)
        );
    }
}
