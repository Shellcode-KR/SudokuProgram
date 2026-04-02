package com.flavio;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SudokuTest {

    @Test
    void noPermiteDuplicadosEnFilaColumnaYBloque() {
        TableroSudoku tablero = new TableroSudoku();
        assertTrue(tablero.setNumero(0, 0, 5));

        assertFalse(tablero.setNumero(0, 1, 5));
        assertFalse(tablero.setNumero(1, 0, 5));
        assertFalse(tablero.setNumero(1, 1, 5));
    }

    @Test
    void candidatosCorrectosEnCelda() {
        TableroSudoku tablero = new TableroSudoku();
        int[][] datos = {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        assertTrue(tablero.cargarTableroInicial(datos));

        AnalizadorCandidatos analizador = new AnalizadorCandidatos();
        List<Integer> posibles = analizador.numerosPosiblesEnCelda(tablero, 0, 2);
        assertEquals(Arrays.asList(1, 2, 4), posibles);
    }

    @Test
    void candidatosDetalladosPorFila() {
        TableroSudoku tablero = new TableroSudoku();
        int[][] datos = {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        assertTrue(tablero.cargarTableroInicial(datos));

        AnalizadorCandidatos analizador = new AnalizadorCandidatos();
        Map<Coordenada, List<Integer>> mapa = analizador.numerosPosiblesEnFila(tablero, 0);
        assertTrue(mapa.get(new Coordenada(0, 2)).containsAll(Arrays.asList(1, 2, 4)));
    }

    @Test
    void resolverSiguienteNumeroConExplicacion() {
        TableroSudoku tablero = new TableroSudoku();
        int[][] datos = {
                {5, 3, 4, 6, 7, 8, 9, 1, 0},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
        tablero.limpiarTablero();
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                int valor = datos[fila][columna];
                if (valor != 0) {
                    assertTrue(tablero.setNumero(fila, columna, valor));
                }
            }
        }

        ResolutorSudoku resolutor = new ResolutorSudoku();
        PasoResolucion paso = resolutor.resolverSiguienteNumero(tablero);

        assertTrue(paso.isResuelto());
        assertEquals(2, paso.getNumero());
        assertEquals("Único candidato en celda", paso.getMetodo());
        assertFalse(paso.getExplicacion().isEmpty());
    }
}
