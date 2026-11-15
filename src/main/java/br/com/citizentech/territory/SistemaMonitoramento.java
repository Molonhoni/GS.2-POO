package br.com.citizentech.territory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.springframework.dao.DataAccessException;

import br.com.citizentech.territory.dominio.Alerta;
import br.com.citizentech.territory.dominio.DadosTerritorio;
import br.com.citizentech.territory.dominio.LimiaresAlerta;
import br.com.citizentech.territory.dominio.Medicao;
import br.com.citizentech.territory.dominio.ResultadoMonitoramento;
import br.com.citizentech.territory.dominio.ResultadoPrevisao;
import br.com.citizentech.territory.dominio.SensorChuva;
import br.com.citizentech.territory.dominio.SensorNivelAgua;
import br.com.citizentech.territory.dominio.SensorSimulado;
import br.com.citizentech.territory.dominio.SensorUmidadeSolo;
import br.com.citizentech.territory.excecao.DadosInvalidosException;
import br.com.citizentech.territory.excecao.FonteIndisponivelException;
import br.com.citizentech.territory.servico.ConfiguracaoSistema;
import br.com.citizentech.territory.servico.ServicoMonitoramento;

public class SistemaMonitoramento {

    private static final String REGIAO = "Zona Norte";

    private static final Locale IDIOMA =
            Locale.forLanguageTag("pt-BR");

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ServicoMonitoramento servico;
    private final Scanner entrada;
    private final List<SensorSimulado> sensores;
    private final DadosTerritorio territorio;

    public SistemaMonitoramento(
            ServicoMonitoramento servico,
            Scanner entrada) {

        this.servico = servico;
        this.entrada = entrada;

        this.sensores = List.of(
            new SensorChuva(
                "S01", "Pluviometro Central", REGIAO
            ),
            new SensorNivelAgua(
                "S02", "Sensor Nivel do Rio", REGIAO
            ),
            new SensorUmidadeSolo(
                "S03", "Sensor Umidade do Solo", REGIAO
            )
        );

        System.out.println("\nCONFIGURACAO INICIAL");
        System.out.println("Territorio: " + REGIAO);

        String codigo = solicitarCodigoInicial();

        this.territorio = new DadosTerritorio(
            REGIAO, codigo
        );

        ConfiguracaoSistema.getInstancia()
            .inicializarTerritorio(territorio);
    }

    private String solicitarCodigoInicial() {

        while (true) {
            String codigo = perguntar(
                "Defina um codigo de acesso (min. 4 caracteres): "
            );

            if (codigo.length() >= 4) {
                return codigo;
            }

            System.out.println(
                "O codigo precisa ter pelo menos 4 caracteres."
            );
        }
    }

    public void iniciar() {

        System.out.println(
            "\nSistema inicializado com sucesso!"
        );

        while (true) {

            exibirMenu();

            if (!entrada.hasNextLine()) {
                System.out.println("Entrada encerrada.");
                return;
            }

            String opcao = entrada.nextLine().trim();

            try {
                switch (opcao) {

                    case "1" -> verificarSensores();

                    case "2" -> coletarMedicoes();

                    case "3" -> executarPrevisoes();

                    case "4" -> consultarHistorico();

                    case "5" -> consultarAlertas();

                    case "6" -> gerenciarTerritorio();

                    case "7" -> visualizarStatusCompleto();

                    case "8" -> alterarStatusSensor();

                    case "0" -> {
                        System.out.println(
                            "\nSistema encerrado com seguranca."
                        );
                        return;
                    }

                    default -> System.out.println(
                        "Opcao invalida. Tente novamente."
                    );
                }

            } catch (DadosInvalidosException
                    | FonteIndisponivelException
                    | IllegalStateException e) {

                System.out.println(
                    "\nAVISO: " + e.getMessage()
                );

            } catch (DataAccessException e) {

                System.out.println(
                    "\nERRO: Nao foi possivel acessar o banco."
                );

                System.out.println(
                    "Detalhes: " + e.getMessage()
                );
            }
        }
    }

    private void exibirMenu() {

        System.out.println(
            "\n========================================"
        );
        System.out.println(
            "     TERRITORY PREDICTIVE INSIGHTS"
        );
        System.out.println(
            "========================================"
        );

        System.out.println("1 - Verificar sensores");
        System.out.println("2 - Coletar medicoes");
        System.out.println("3 - Executar previsoes");
        System.out.println("4 - Consultar historico");
        System.out.println("5 - Consultar alertas");
        System.out.println("6 - Gerenciar dados do territorio");
        System.out.println("7 - Visualizar status completo");
        System.out.println("8 - Ativar/desativar sensor");
        System.out.println("0 - Encerrar sistema");

        System.out.println(
            "========================================"
        );
        System.out.print("Escolha uma opcao: ");
    }

    private String perguntar(String mensagem) {

        System.out.print(mensagem);

        if (!entrada.hasNextLine()) {
            throw new IllegalStateException(
                "A entrada de dados foi encerrada."
            );
        }

        return entrada.nextLine().trim();
    }

    private int lerInteiro(
            String mensagem,
            int minimo,
            int maximo) {

        String texto = perguntar(mensagem);

        try {
            int numero = Integer.parseInt(texto);

            if (numero < minimo || numero > maximo) {
                throw new DadosInvalidosException(
                    "Informe um numero entre "
                    + minimo + " e " + maximo + "."
                );
            }

            return numero;

        } catch (NumberFormatException e) {
            throw new DadosInvalidosException(
                "Informe um numero inteiro valido."
            );
        }
    }

    private double lerDecimal(String mensagem) {

        String texto = perguntar(mensagem)
            .replace(',', '.');

        try {
            return Double.parseDouble(texto);

        } catch (NumberFormatException e) {
            throw new DadosInvalidosException(
                "Informe um valor numerico valido."
            );
        }
    }

    private List<SensorSimulado> selecionarSensores(
            boolean permitirTodos) {

        System.out.println("\nSENSORES DISPONIVEIS");

        for (int i = 0; i < sensores.size(); i++) {

            SensorSimulado sensor = sensores.get(i);

            System.out.println(
                (i + 1) + " - " + sensor.getNome()
                + " (" + sensor.retornarTipo() + ")"
            );
        }

        if (permitirTodos) {
            System.out.println("0 - Todos os sensores");
        }

        int escolha = lerInteiro(
            "Selecione um sensor: ",
            permitirTodos ? 0 : 1,
            sensores.size()
        );

        if (escolha == 0) {
            return sensores;
        }

        return List.of(sensores.get(escolha - 1));
    }

    private void verificarSensores() {

        System.out.println("\nSTATUS DOS SENSORES");

        for (SensorSimulado sensor : sensores) {

            System.out.println(
                sensor.getId() + " | "
                + sensor.getNome() + " | "
                + sensor.verificarStatus()
            );
        }
    }

    private void coletarMedicoes() {

        List<SensorSimulado> selecionados =
            selecionarSensores(true);

        int quantidade = lerInteiro(
            "Quantidade de leituras por sensor (1 a 24): ",
            1, 24
        );

        System.out.println("\nCOLETANDO MEDICOES");

        for (SensorSimulado sensor : selecionados) {

            System.out.println(
                "\nSensor: " + sensor.getNome()
            );

            for (int i = 0; i < quantidade; i++) {

                try {
                    Medicao medicao =
                        servico.coletarLeitura(sensor);

                    System.out.printf(
                        IDIOMA,
                        "Leitura %d: %.1f %s%n",
                        i + 1,
                        medicao.getValor(),
                        medicao.getUnidade()
                    );

                } catch (FonteIndisponivelException e) {

                    System.out.println(
                        "Leitura interrompida: "
                        + e.getMessage()
                    );

                    break;
                }
            }
        }

        System.out.println(
            "\nColeta finalizada. As leituras validas foram salvas."
        );
    }

    private void executarPrevisoes() {

        List<SensorSimulado> selecionados =
            selecionarSensores(true);

        System.out.println("\nESTRATEGIAS");
        System.out.println("1 - Media movel (5 medicoes)");
        System.out.println("2 - Comparacao (10 medicoes)");

        int escolha = lerInteiro(
            "Selecione a estrategia: ", 1, 2
        );

        String estrategia = escolha == 1
            ? "MEDIA_MOVEL"
            : "COMPARACAO";

        for (SensorSimulado sensor : selecionados) {

            System.out.println(
                "\nPREVISAO: " + sensor.getNome()
            );

            try {
                ResultadoMonitoramento resultado =
                    servico.executarPrevisao(
                        sensor, estrategia
                    );

                ResultadoPrevisao previsao =
                    resultado.getPrevisao();

                System.out.printf(
                    IDIOMA,
                    "Valor previsto: %.2f %s%n",
                    previsao.getValorPrevisto(),
                    previsao.getUnidade()
                );

                System.out.println(
                    "Nivel: " + resultado.getNivel()
                );

                System.out.println(
                    "Justificativa: "
                    + previsao.getJustificativa()
                );

                if (resultado.possuiAlerta()) {
                    System.out.println(
                        "ALERTA REGISTRADO: "
                        + resultado.getAlerta().getMensagem()
                    );

                } else {
                    System.out.println(
                        "Nenhum alerta necessario."
                    );
                }

            } catch (DadosInvalidosException
                    | FonteIndisponivelException e) {

                System.out.println(
                    "Previsao nao realizada: "
                    + e.getMessage()
                );
            }
        }
    }

    private void consultarHistorico() {

        SensorSimulado sensor =
            selecionarSensores(false).get(0);

        List<Medicao> historico =
            servico.consultarHistorico(sensor.getId());

        System.out.println(
            "\nHISTORICO: " + sensor.getNome()
        );

        if (historico.isEmpty()) {
            System.out.println(
                "Nenhuma medicao encontrada."
            );
            return;
        }

        for (Medicao medicao : historico) {

            System.out.printf(
                IDIOMA,
                "#%d | %s | %.2f %s%n",
                medicao.getId(),
                medicao.getDataHora().format(DATA),
                medicao.getValor(),
                medicao.getUnidade()
            );
        }

        System.out.println(
            "Total de medicoes: " + historico.size()
        );
    }

    private void consultarAlertas() {

        List<Alerta> alertas =
            servico.consultarAlertas();

        System.out.println("\nHISTORICO DE ALERTAS");

        if (alertas.isEmpty()) {
            System.out.println(
                "Nenhum alerta registrado."
            );
            return;
        }

        for (Alerta alerta : alertas) {

            System.out.println(
                "\n#" + alerta.getId()
                + " | " + alerta.getDataHora().format(DATA)
                + " | " + alerta.getNivel()
            );

            System.out.println(
                alerta.getMensagem()
            );
        }

        System.out.println(
            "\nTotal de alertas: " + alertas.size()
        );
    }

    private void mostrarLimiares() {

        System.out.println(
            "\nLIMIARES DO TERRITORIO: "
            + territorio.getRegiao()
        );

        for (SensorSimulado sensor : sensores) {

            LimiaresAlerta limites =
                territorio.obterLimiares(
                    sensor.retornarTipo()
                );

            System.out.printf(
                IDIOMA,
                "%s | Atencao: %.1f | Alerta: %.1f"
                + " | Critico: %.1f %s%n",
                sensor.retornarTipo(),
                limites.getAtencao(),
                limites.getAlerta(),
                limites.getCritico(),
                sensor.retornarUnidade()
            );
        }
    }

    private void gerenciarTerritorio() {

        mostrarLimiares();

        String resposta = perguntar(
            "\nDeseja alterar os limites? (S/N): "
        );

        if (!resposta.equalsIgnoreCase("S")) {
            return;
        }

        SensorSimulado sensor =
            selecionarSensores(false).get(0);

        double atencao =
            lerDecimal("Novo limite ATENCAO: ");

        double alerta =
            lerDecimal("Novo limite ALERTA: ");

        double critico =
            lerDecimal("Novo limite CRITICO: ");

        String codigo =
            perguntar("Codigo de acesso: ");

        territorio.atualizarLimiares(
            sensor.retornarTipo(),
            atencao,
            alerta,
            critico,
            codigo
        );

        System.out.println(
            "\nLimiares atualizados com sucesso!"
        );

        mostrarLimiares();
    }

    private void visualizarStatusCompleto() {

        System.out.println(
            "\nSTATUS COMPLETO DO TERRITORIO"
        );

        System.out.println(
            "Regiao monitorada: " + territorio.getRegiao()
        );

        List<Alerta> alertas =
            servico.consultarAlertas();

        for (SensorSimulado sensor : sensores) {

            List<Medicao> historico =
                servico.consultarHistorico(
                    sensor.getId()
                );

            long quantidadeAlertas = alertas.stream()
                .filter(a ->
                    a.getSensorId().equals(sensor.getId())
                )
                .count();

            System.out.println(
                "\nSensor: " + sensor.getNome()
            );

            System.out.println(
                "Status: " + sensor.verificarStatus()
            );

            System.out.println(
                "Medicoes registradas: " + historico.size()
            );

            System.out.println(
                "Alertas registrados: " + quantidadeAlertas
            );

            if (!historico.isEmpty()) {

                Medicao ultima = historico.get(0);

                System.out.printf(
                    IDIOMA,
                    "Ultima leitura: %.2f %s%n",
                    ultima.getValor(),
                    ultima.getUnidade()
                );
            }
        }

        mostrarLimiares();

        System.out.println(
            "\nTotal de alertas: " + alertas.size()
        );
    }

    private void alterarStatusSensor() {

        SensorSimulado sensor =
            selecionarSensores(false).get(0);

        sensor.alterarStatus(!sensor.isAtivo());

        System.out.println(
            "\nNovo status de " + sensor.getNome()
            + ": " + sensor.verificarStatus()
        );
    }
}
