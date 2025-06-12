package Loja;

import static org.mockito.Mockito.*;

import Loja.LojaInterface;
import Loja.LojaSystem;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.io.*;

public class LojaInterfaceTest {

    private LojaInterface lojaInterface;
    private ByteArrayOutputStream saida;

    @BeforeEach
    public void setup() {
        saida = new ByteArrayOutputStream();
        lojaInterface = new LojaInterface(System.in, new PrintStream(saida));
    }

    @Test
    public void testExibirNotaLoja_LojaNaoEncontrada() {
        try (MockedStatic<LojaSystem> mocked = mockStatic(LojaSystem.class)) {
            mocked.when(() -> LojaSystem.buscarNotaMediaLoja("LojaInexistente")).thenReturn(null);

            lojaInterface.exibirNotaLoja("LojaInexistente");

            String output = saida.toString();
            Assertions.assertTrue(output.contains("Loja não encontrada."));
        }
    }

    @Test
    public void testExibirNotaLoja_SemAvaliacoes() {
        try (MockedStatic<LojaSystem> mocked = mockStatic(LojaSystem.class)) {
            mocked.when(() -> LojaSystem.buscarNotaMediaLoja("LojaSemAvaliacoes")).thenReturn(-1.0);

            lojaInterface.exibirNotaLoja("LojaSemAvaliacoes");

            String output = saida.toString();
            Assertions.assertTrue(output.contains("Esta loja não possui avaliações ainda."));
        }
    }

    @Test
    public void testExibirNotaLoja_ComAvaliacoes() {
        try (MockedStatic<LojaSystem> mocked = mockStatic(LojaSystem.class)) {
            mocked.when(() -> LojaSystem.buscarNotaMediaLoja("LojaBoa")).thenReturn(4.20);

            lojaInterface.exibirNotaLoja("LojaBoa");

            String output = saida.toString();
            Assertions.assertTrue(output.contains("Loja: LojaBoa"));
            Assertions.assertTrue(output.contains("Avaliação: Bom"));
        }
    }
}
