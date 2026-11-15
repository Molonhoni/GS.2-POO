package br.com.citizentech.territory.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.citizentech.territory.dominio.Alerta;

public interface AlertaRepository
        extends JpaRepository<Alerta, Long> {

    List<Alerta> findBySensorIdOrderByDataHoraDesc(
        String sensorId
    );

    List<Alerta> findAllByOrderByDataHoraDesc();
}
