package br.com.citizentech.territory.persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.com.citizentech.territory.dominio.Alerta;
import br.com.citizentech.territory.dominio.Medicao;
import br.com.citizentech.territory.dominio.NivelAlerta;
import br.com.citizentech.territory.dominio.SensorChuva;

@DataJpaTest
class PersistenciaRepositoryTest {

    @Autowired
    private MedicaoRepository medicaoRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Test
    void deveSalvarEConsultarMedicaoPorSensor() {

        // Arrange
        SensorChuva sensor = new SensorChuva(
            "S01",
            "Pluviometro Central",
            "Zona Norte"
        );

        Medicao medicao = new Medicao(sensor, 45.5);

        // Act
        medicaoRepository.saveAndFlush(medicao);

        List<Medicao> historico =
            medicaoRepository
                .findBySensorIdOrderByDataHoraDesc("S01");

        // Assert
        assertEquals(1, historico.size());
        assertEquals(45.5, historico.get(0).getValor());
        assertEquals("CHUVA", historico.get(0).getTipoSensor());
        assertNotNull(historico.get(0).getId());
    }

    @Test
    void deveSalvarEConsultarAlerta() {

        // Arrange
        SensorChuva sensor = new SensorChuva(
            "S02",
            "Pluviometro Norte",
            "Zona Norte"
        );

        Alerta alerta = new Alerta(
            sensor,
            NivelAlerta.ALERTA,
            75.0,
            60.0,
            "MEDIA_MOVEL"
        );

        // Act
        alertaRepository.saveAndFlush(alerta);

        List<Alerta> historico =
            alertaRepository
                .findBySensorIdOrderByDataHoraDesc("S02");

        // Assert
        assertEquals(1, historico.size());
        assertEquals(
            NivelAlerta.ALERTA,
            historico.get(0).getNivel()
        );
        assertEquals(
            75.0,
            historico.get(0).getValorPrevisto()
        );
        assertNotNull(historico.get(0).getId());

        assertTrue(
            historico.get(0).getMensagem()
                .contains("60,0")
        );
    }
}
