package br.com.citizentech.territory.servico;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import br.com.citizentech.territory.dominio.Medicao;
import br.com.citizentech.territory.excecao.DadosInvalidosException;

public class ExportadorHistoricoCsv {

    private static final Locale IDIOMA =
            Locale.forLanguageTag("pt-BR");

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern(
                "dd/MM/yyyy HH:mm:ss"
            );

    public Path exportar(
            List<Medicao> historico,
            Path arquivo) throws IOException {

        if (historico == null || historico.isEmpty()) {
            throw new DadosInvalidosException(
                "Nao existem medicoes para exportar."
            );
        }

        if (arquivo == null) {
            throw new DadosInvalidosException(
                "O destino do arquivo e obrigatorio."
            );
        }

        for (Medicao medicao : historico) {

            if (medicao == null
                    || medicao.getDataHora() == null
                    || !Double.isFinite(medicao.getValor())) {

                throw new DadosInvalidosException(
                    "O historico possui dados invalidos."
                );
            }
        }

        Path destino =
            arquivo.toAbsolutePath().normalize();

        Files.createDirectories(destino.getParent());

        try (BufferedWriter escritor =
                Files.newBufferedWriter(
                    destino,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
                )) {

            // BOM UTF-8 para compatibilidade com Excel.
            escritor.write('\uFEFF');

            escritor.write(
                "id;sensor_id;nome_sensor;tipo_sensor;"
                + "regiao;valor;unidade;data_hora"
            );

            escritor.newLine();

            for (Medicao medicao : historico) {

                String id = medicao.getId() == null
                    ? ""
                    : medicao.getId().toString();

                String valor = String.format(
                    IDIOMA,
                    "%.2f",
                    medicao.getValor()
                );

                String linha = String.join(
                    ";",
                    formatarCelula(id),
                    formatarCelula(medicao.getSensorId()),
                    formatarCelula(medicao.getNomeSensor()),
                    formatarCelula(medicao.getTipoSensor()),
                    formatarCelula(medicao.getRegiao()),
                    formatarCelula(valor),
                    formatarCelula(medicao.getUnidade()),
                    formatarCelula(
                        medicao.getDataHora().format(DATA)
                    )
                );

                escritor.write(linha);
                escritor.newLine();
            }
        }

        return destino;
    }

    private String formatarCelula(String valor) {

        String texto = valor == null ? "" : valor;

        // Impede interpretacao de formulas em planilhas.
        if (!texto.isEmpty()
                && "=+-@\t\r".indexOf(texto.charAt(0)) >= 0) {

            texto = "'" + texto;
        }

        // Escapa aspas conforme o formato CSV.
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }
}
