package br.com.citizentech.territory.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.citizentech.territory.dominio.Medicao;

public interface MedicaoRepository
        extends JpaRepository<Medicao, Long> {

    List<Medicao> findBySensorIdOrderByDataHoraDesc(
        String sensorId
    );

    List<Medicao> findTop5BySensorIdOrderByDataHoraDesc(
        String sensorId
    );

    List<Medicao> findTop10BySensorIdOrderByDataHoraDesc(
        String sensorId
    );
}
