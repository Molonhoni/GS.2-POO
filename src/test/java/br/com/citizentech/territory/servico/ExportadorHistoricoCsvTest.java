package br.com.citizentech.territory.servico;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import br.com.citizentech.territory.dominio.Medicao;
import br.com.citizentech.territory.dominio.SensorChuva;
import br.com.citizentech.territory.excecao.DadosInvalidosException;

class ExportadorHistoricoCsvTest {

    @TempDir
    Path pastaTemporaria;

    private final ExportadorHistoricoCsv exportador =
        new ExportadorHistoricoCsv();

    private Medicao criarMedicao(
            String nomeSensor,
            double valor) {

        SensorChuva sensor = new SensorChuva(
            "S01",
            nomeSensor,
            "Zona Norte"
        );

        return new Medicao(sensor, valor);
    }

    @Test
    void deveExportarHistoricoEmCsv() throws IOException {

        // Arrange
        List<Medicao> historico = List.of(
            criarMedicao("Pluviometro Central", 42.5)
        );

        Path destino =
            pastaTemporaria.resolve("historico.csv");

        // Act
        Path arquivo = exportador.exportar(
            historico, destino
        );

        String conteudo = Files.readString(
            arquivo,
            StandardCharsets.UTF_8
        );

        // Assert
        assertTrue(Files.exists(arquivo));
        assertTrue(conteudo.startsWith("\uFEFF"));
        assertTrue(conteudo.contains("sensor_id"));
        assertTrue(conteudo.contains("CHUVA"));
        assertTrue(conteudo.contains("42,50"));
    }

    @Test
    void deveRejeitarHistoricoVazio() {

        // Arrange
        Path destino =
            pastaTemporaria.resolve("vazio.csv");

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> exportador.exportar(
                List.of(), destino
            )
        );

        assertFalse(Files.exists(destino));
    }

    @Test
    void deveEscaparCamposComSeparadores() throws IOException {

        // Arrange
        List<Medicao> historico = List.of(
            criarMedicao(
                "Pluvio; \"Central\"",
                35.0
            )
        );

        Path destino =
            pastaTemporaria.resolve("especial.csv");

        // Act
        exportador.exportar(historico, destino);

        String conteudo = Files.readString(
            destino,
            StandardCharsets.UTF_8
        );

        // Assert
        assertTrue(
            conteudo.contains(
                "\"Pluvio; \"\"Central\"\"\""
            )
        );
    }

    @Test
    void naoDeveSobrescreverArquivoExistente()
            throws IOException {

        // Arrange
        Path destino =
            pastaTemporaria.resolve("existente.csv");

        Files.writeString(
            destino,
            "conteudo original",
            StandardCharsets.UTF_8
        );

        List<Medicao> historico = List.of(
            criarMedicao("Pluviometro", 20.0)
        );

        // Act e Assert
        assertThrows(
            IOException.class,
            () -> exportador.exportar(
                historico, destino
            )
        );

        assertEquals(
            "conteudo original",
            Files.readString(
                destino,
                StandardCharsets.UTF_8
            )
        );
    }
}
