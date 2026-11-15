package br.com.citizentech.territory.servico;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.com.citizentech.territory.dominio.EstrategiaPrevisao;
import br.com.citizentech.territory.dominio.PrevisaoComparacao;
import br.com.citizentech.territory.dominio.PrevisaoMediaMovel;
import br.com.citizentech.territory.excecao.DadosInvalidosException;

class FabricaPrevisaoTest {

    @Test
    void deveCriarPrevisaoMediaMovel() {

        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao();

        // Act
        EstrategiaPrevisao estrategia =
                fabrica.criar("MEDIA_MOVEL");

        // Assert
        assertInstanceOf(
            PrevisaoMediaMovel.class,
            estrategia
        );

        assertEquals(
            "MEDIA_MOVEL",
            estrategia.getNome()
        );
    }

    @Test
    void deveCriarPrevisaoComparacao() {

        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao();

        // Act
        EstrategiaPrevisao estrategia =
                fabrica.criar("comparacao");

        // Assert
        assertInstanceOf(
            PrevisaoComparacao.class,
            estrategia
        );

        assertEquals(
            "COMPARACAO",
            estrategia.getNome()
        );
    }

    @Test
    void deveRejeitarEstrategiaDesconhecida() {

        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao();

        // Act e Assert
        assertThrows(
            DadosInvalidosException.class,
            () -> fabrica.criar("DESCONHECIDA")
        );
    }

    @Test
    void deveCriarInstanciasIndependentes() {

        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao();

        // Act
        EstrategiaPrevisao primeira =
                fabrica.criar("MEDIA_MOVEL");

        EstrategiaPrevisao segunda =
                fabrica.criar("MEDIA_MOVEL");

        // Assert
        assertNotSame(primeira, segunda);
    }
}
