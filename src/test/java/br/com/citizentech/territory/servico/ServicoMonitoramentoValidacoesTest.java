package br.com.citizentech.territory.servico;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.citizentech.territory.dominio.Alerta;
import br.com.citizentech.territory.dominio.DadosTerritorio;
import br.com.citizentech.territory.dominio.Medicao;
import br.com.citizentech.territory.dominio.NivelAlerta;
import br.com.citizentech.territory.dominio.SensorChuva;
import br.com.citizentech.territory.excecao.DadosInvalidosException;
import br.com.citizentech.territory.excecao.FonteIndisponivelException;
import br.com.citizentech.territory.persistencia.AlertaRepository;
import br.com.citizentech.territory.persistencia.MedicaoRepository;

class ServicoMonitoramentoValidacoesTest {

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
            medicaoRepository, alertaRepository
        );

        sensor = new SensorChuva(
            "S01", "Pluviometro Central", "Zona Norte"
        );

        territorio = new DadosTerritorio(
            "Zona Norte", "codigo-teste"
        );
    }

    @Test
    void naoDeveConsultarBancoComSensorNulo() {

        // Arrange, Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> servico.executarPrevisao(
                null, "MEDIA_MOVEL", territorio
            )
        );

        verifyNoInteractions(
            medicaoRepository, alertaRepository
        );
    }

    @Test
    void naoDeveConsultarBancoComSensorInativo() {

        // Arrange
        sensor.alterarStatus(false);

        // Act e Assert
        assertThrows(
            FonteIndisponivelException.class,
            () -> servico.executarPrevisao(
                sensor, "MEDIA_MOVEL", territorio
            )
        );

        verifyNoInteractions(
            medicaoRepository, alertaRepository
        );
    }

    @Test
    void deveRejeitarSensorDeOutraRegiao() {

        // Arrange
        DadosTerritorio outraRegiao =
            new DadosTerritorio(
                "Zona Sul", "codigo-teste"
            );

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> servico.executarPrevisao(
                sensor, "MEDIA_MOVEL", outraRegiao
            )
        );

        verifyNoInteractions(
            medicaoRepository, alertaRepository
        );
    }

    @Test
    void deveRejeitarEstrategiaDesconhecida() {

        // Arrange, Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> servico.executarPrevisao(
                sensor, "DESCONHECIDA", territorio
            )
        );

        verifyNoInteractions(
            medicaoRepository, alertaRepository
        );
    }

    @Test
    void deveConsultarHistoricoPeloSensor() {

        // Arrange
        Medicao medicao = new Medicao(sensor, 25.0);

        when(medicaoRepository
            .findBySensorIdOrderByDataHoraDesc("S01"))
            .thenReturn(List.of(medicao));

        // Act
        List<Medicao> resultado =
            servico.consultarHistorico("S01");

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(25.0, resultado.get(0).getValor());

        verify(medicaoRepository, times(1))
            .findBySensorIdOrderByDataHoraDesc("S01");
    }

    @Test
    void deveConsultarAlertasRegistrados() {

        // Arrange
        Alerta alerta = new Alerta(
            sensor,
            NivelAlerta.ALERTA,
            75.0,
            60.0,
            "MEDIA_MOVEL"
        );

        when(alertaRepository.findAllByOrderByDataHoraDesc())
            .thenReturn(List.of(alerta));

        // Act
        List<Alerta> resultado =
            servico.consultarAlertas();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(
            NivelAlerta.ALERTA,
            resultado.get(0).getNivel()
        );

        verify(alertaRepository, times(1))
            .findAllByOrderByDataHoraDesc();
    }
}
