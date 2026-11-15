package br.com.citizentech.territory.servico;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.citizentech.territory.dominio.Alerta;
import br.com.citizentech.territory.dominio.DadosTerritorio;
import br.com.citizentech.territory.dominio.Medicao;
import br.com.citizentech.territory.dominio.NivelAlerta;
import br.com.citizentech.territory.dominio.ResultadoMonitoramento;
import br.com.citizentech.territory.dominio.SensorChuva;
import br.com.citizentech.territory.excecao.DadosInvalidosException;
import br.com.citizentech.territory.excecao.FonteIndisponivelException;
import br.com.citizentech.territory.persistencia.AlertaRepository;
import br.com.citizentech.territory.persistencia.MedicaoRepository;

class ServicoMonitoramentoTest {

    private MedicaoRepository medicaoRepository;
    private AlertaRepository alertaRepository;

    private ServicoMonitoramento servico;
    private SensorChuva sensor;
    private DadosTerritorio territorio;

    @BeforeEach
    void configurar() {

        medicaoRepository = mock(MedicaoRepository.class);
        alertaRepository = mock(AlertaRepository.class);

        servico = new ServicoMonitoramento(
            medicaoRepository,
            alertaRepository
        );

        sensor = new SensorChuva(
            "S01",
            "Pluviometro Central",
            "Zona Norte"
        );

        territorio = new DadosTerritorio(
            "Zona Norte",
            "codigo-teste"
        );
    }

    private List<Medicao> criarHistorico(
            double valor,
            int quantidade) {

        List<Medicao> historico = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            historico.add(
                new Medicao(sensor, valor)
            );
        }

        return historico;
    }

    @Test
    void deveSalvarLeituraNoRepositorio() {

        // Arrange
        when(medicaoRepository.save(any(Medicao.class)))
            .thenAnswer(invocacao ->
                invocacao.getArgument(0)
            );

        // Act
        Medicao medicao = servico.coletarLeitura(sensor);

        // Assert
        assertNotNull(medicao);
        assertEquals("S01", medicao.getSensorId());
        assertTrue(medicao.getValor() >= 0);
        assertTrue(medicao.getValor() <= 120);

        verify(medicaoRepository, times(1))
            .save(any(Medicao.class));
    }

    @Test
    void deveGerarAlertaAutomaticamente() {

        // Arrange
        when(medicaoRepository
                .findTop10BySensorIdOrderByDataHoraDesc("S01"))
            .thenReturn(criarHistorico(75, 5));

        when(alertaRepository.save(any(Alerta.class)))
            .thenAnswer(invocacao ->
                invocacao.getArgument(0)
            );

        // Act
        ResultadoMonitoramento resultado =
            servico.executarPrevisao(
                sensor,
                "MEDIA_MOVEL",
                territorio
            );

        // Assert
        assertEquals(
            NivelAlerta.ALERTA,
            resultado.getNivel()
        );

        assertTrue(resultado.possuiAlerta());

        assertEquals(
            75.0,
            resultado.getPrevisao().getValorPrevisto(),
            0.001
        );

        assertEquals(
            60.0,
            resultado.getAlerta().getLimiarAtingido(),
            0.001
        );

        verify(alertaRepository, times(1))
            .save(any(Alerta.class));
    }

    @Test
    void naoDeveGerarAlertaQuandoRiscoNormal() {

        // Arrange
        when(medicaoRepository
                .findTop10BySensorIdOrderByDataHoraDesc("S01"))
            .thenReturn(criarHistorico(10, 5));

        // Act
        ResultadoMonitoramento resultado =
            servico.executarPrevisao(
                sensor,
                "MEDIA_MOVEL",
                territorio
            );

        // Assert
        assertEquals(
            NivelAlerta.NORMAL,
            resultado.getNivel()
        );

        assertFalse(resultado.possuiAlerta());

        verify(alertaRepository, never())
            .save(any(Alerta.class));
    }

    @Test
    void deveRejeitarHistoricoInsuficiente() {

        // Arrange
        when(medicaoRepository
                .findTop10BySensorIdOrderByDataHoraDesc("S01"))
            .thenReturn(criarHistorico(20, 4));

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> servico.executarPrevisao(
                sensor,
                "MEDIA_MOVEL",
                territorio
            )
        );

        verify(alertaRepository, never())
            .save(any(Alerta.class));
    }

    @Test
    void deveRejeitarSensorIndisponivel() {

        // Arrange
        sensor.alterarStatus(false);

        // Act e Assert
        assertThrows(
            FonteIndisponivelException.class,
            () -> servico.coletarLeitura(sensor)
        );

        verify(medicaoRepository, never())
            .save(any(Medicao.class));
    }
}
