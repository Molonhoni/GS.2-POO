package br.com.citizentech.territory.servico;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.citizentech.territory.dominio.Alerta;
import br.com.citizentech.territory.dominio.DadosTerritorio;
import br.com.citizentech.territory.dominio.EstrategiaPrevisao;
import br.com.citizentech.territory.dominio.LimiaresAlerta;
import br.com.citizentech.territory.dominio.Medicao;
import br.com.citizentech.territory.dominio.NivelAlerta;
import br.com.citizentech.territory.dominio.ResultadoMonitoramento;
import br.com.citizentech.territory.dominio.ResultadoPrevisao;
import br.com.citizentech.territory.dominio.SensorSimulado;
import br.com.citizentech.territory.excecao.DadosInvalidosException;
import br.com.citizentech.territory.excecao.FonteIndisponivelException;
import br.com.citizentech.territory.persistencia.AlertaRepository;
import br.com.citizentech.territory.persistencia.MedicaoRepository;

@Service
public class ServicoMonitoramento {

    private final MedicaoRepository medicaoRepository;
    private final AlertaRepository alertaRepository;
    private final FabricaPrevisao fabricaPrevisao;

    public ServicoMonitoramento(
            MedicaoRepository medicaoRepository,
            AlertaRepository alertaRepository) {

        this.medicaoRepository = medicaoRepository;
        this.alertaRepository = alertaRepository;
        this.fabricaPrevisao = new FabricaPrevisao();
    }

    private void validarSensor(SensorSimulado sensor) {

        if (sensor == null) {
            throw new DadosInvalidosException(
                "O sensor e obrigatorio."
            );
        }

        if (!sensor.verificarFuncionamento()) {
            throw new FonteIndisponivelException(
                "O sensor " + sensor.getNome()
                + " esta indisponivel."
            );
        }
    }

    private void validarTerritorio(
            SensorSimulado sensor,
            DadosTerritorio territorio) {

        if (territorio == null) {
            throw new DadosInvalidosException(
                "O territorio e obrigatorio."
            );
        }

        if (!sensor.getRegiao().equalsIgnoreCase(
                territorio.getRegiao())) {

            throw new DadosInvalidosException(
                "O sensor nao pertence ao territorio configurado."
            );
        }
    }

    @Transactional
    public Medicao coletarLeitura(SensorSimulado sensor) {

        validarSensor(sensor);

        double valor = sensor.lerValor();

        Medicao medicao = new Medicao(sensor, valor);

        return medicaoRepository.save(medicao);
    }

    @Transactional
    public ResultadoMonitoramento executarPrevisao(
            SensorSimulado sensor,
            String tipoEstrategia) {

        DadosTerritorio territorio =
            ConfiguracaoSistema
                .getInstancia()
                .obterDadosTerritorio();

        return executarPrevisao(
            sensor,
            tipoEstrategia,
            territorio
        );
    }

    @Transactional
    public ResultadoMonitoramento executarPrevisao(
            SensorSimulado sensor,
            String tipoEstrategia,
            DadosTerritorio territorio) {

        validarSensor(sensor);
        validarTerritorio(sensor, territorio);

        EstrategiaPrevisao estrategia =
            fabricaPrevisao.criar(tipoEstrategia);

        List<Medicao> historico =
            medicaoRepository
                .findTop10BySensorIdOrderByDataHoraDesc(
                    sensor.getId()
                );

        ResultadoPrevisao previsao =
            estrategia.prever(historico);

        LimiaresAlerta limiares =
            territorio.obterLimiares(
                sensor.retornarTipo()
            );

        NivelAlerta nivel =
            limiares.classificar(
                previsao.getValorPrevisto()
            );

        if (nivel == NivelAlerta.NORMAL) {
            return new ResultadoMonitoramento(
                previsao,
                nivel,
                null
            );
        }

        double limiteAtingido =
            obterLimiteAtingido(limiares, nivel);

        Alerta alerta = new Alerta(
            sensor,
            nivel,
            previsao.getValorPrevisto(),
            limiteAtingido,
            previsao.getEstrategia()
        );

        Alerta alertaSalvo =
            alertaRepository.save(alerta);

        return new ResultadoMonitoramento(
            previsao,
            nivel,
            alertaSalvo
        );
    }

    private double obterLimiteAtingido(
            LimiaresAlerta limiares,
            NivelAlerta nivel) {

        return switch (nivel) {

            case ATENCAO -> limiares.getAtencao();

            case ALERTA -> limiares.getAlerta();

            case CRITICO -> limiares.getCritico();

            case NORMAL -> throw new DadosInvalidosException(
                "Nivel NORMAL nao possui limite de alerta."
            );
        };
    }

    @Transactional(readOnly = true)
    public List<Medicao> consultarHistorico(
            String sensorId) {

        validarSensorId(sensorId);

        return medicaoRepository
            .findBySensorIdOrderByDataHoraDesc(
                sensorId.trim()
            );
    }

    @Transactional(readOnly = true)
    public List<Alerta> consultarAlertas() {

        return alertaRepository
            .findAllByOrderByDataHoraDesc();
    }

    @Transactional(readOnly = true)
    public List<Alerta> consultarAlertasPorSensor(
            String sensorId) {

        validarSensorId(sensorId);

        return alertaRepository
            .findBySensorIdOrderByDataHoraDesc(
                sensorId.trim()
            );
    }

    private void validarSensorId(String sensorId) {

        if (sensorId == null || sensorId.isBlank()) {
            throw new DadosInvalidosException(
                "O identificador do sensor e obrigatorio."
            );
        }
    }
}
