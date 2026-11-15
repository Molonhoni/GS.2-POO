package br.com.citizentech.territory.servico;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.com.citizentech.territory.dominio.DadosTerritorio;
import br.com.citizentech.territory.excecao.DadosInvalidosException;

class ConfiguracaoSistemaTest {

    @Test
    void deveRetornarSempreAMesmaInstancia() {

        // Arrange e Act
        ConfiguracaoSistema primeira =
                ConfiguracaoSistema.getInstancia();

        ConfiguracaoSistema segunda =
                ConfiguracaoSistema.getInstancia();

        // Assert
        assertSame(primeira, segunda);
    }

    @Test
    void deveRejeitarTerritorioNulo() {

        // Arrange
        ConfiguracaoSistema configuracao =
                ConfiguracaoSistema.getInstancia();

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> configuracao.inicializarTerritorio(null)
        );
    }

    @Test
    void deveInicializarTerritorioUmaUnicaVez() {

        // Arrange
        ConfiguracaoSistema configuracao =
                ConfiguracaoSistema.getInstancia();

        DadosTerritorio territorio = new DadosTerritorio(
            "Zona Norte",
            "codigo-de-teste"
        );

        // Act
        configuracao.inicializarTerritorio(territorio);

        // Assert
        assertSame(
            territorio,
            configuracao.obterDadosTerritorio()
        );

        assertEquals(
            "Zona Norte",
            configuracao.obterRegiao()
        );

        assertThrows(
            IllegalStateException.class,
            () -> configuracao.inicializarTerritorio(
                new DadosTerritorio(
                    "Zona Sul",
                    "outro-codigo-de-teste"
                )
            )
        );
    }
}
