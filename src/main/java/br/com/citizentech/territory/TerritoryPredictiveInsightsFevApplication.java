package br.com.citizentech.territory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import br.com.citizentech.territory.servico.ServicoMonitoramento;

@SpringBootApplication
public class TerritoryPredictiveInsightsFevApplication {

    public static void main(String[] args) {

        try {
            Files.createDirectories(Path.of("data"));

        } catch (IOException e) {

            System.err.println(
                "Erro ao preparar a pasta do banco de dados: "
                + e.getMessage()
            );

            return;
        }

        try (
            ConfigurableApplicationContext contexto =
                SpringApplication.run(
                    TerritoryPredictiveInsightsFevApplication.class,
                    args
                );

            Scanner entrada = new Scanner(System.in)
        ) {

            ServicoMonitoramento servico =
                contexto.getBean(ServicoMonitoramento.class);

            SistemaMonitoramento sistema =
                new SistemaMonitoramento(
                    servico,
                    entrada
                );

            sistema.iniciar();
        }
    }
}
