package com.flavio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnalizadorCandidatos {

    public List<Integer> numerosPosiblesEnCelda(TableroSudoku tablero, int fila, int columna) {
        if (!ValidadorSudoku.posicionValida(fila, columna) || tablero.getValor(fila, columna) != 0) {
            return Collections.emptyList();
        }

        int[][] valores = tablero.getCuadricula();
        List<Integer> candidatos = new ArrayList<>();
        for (int numero = 1; numero <= 9; numero++) {
            if (ValidadorSudoku.puedeColocar(valores, fila, columna, numero)) {
                candidatos.add(numero);
            }
        }
        return candidatos;
    }

    public Map<Coordenada, List<Integer>> numerosPosiblesEnFila(TableroSudoku tablero, int fila) {
        if (fila < 0 || fila >= 9) {
            return Collections.emptyMap();
        }

        Map<Coordenada, List<Integer>> resultado = new LinkedHashMap<>();
        for (int columna = 0; columna < 9; columna++) {
            if (tablero.getValor(fila, columna) == 0) {
                resultado.put(new Coordenada(fila, columna), numerosPosiblesEnCelda(tablero, fila, columna));
            }
        }
        return resultado;
    }

    public Map<Coordenada, List<Integer>> numerosPosiblesEnColumna(TableroSudoku tablero, int columna) {
        if (columna < 0 || columna >= 9) {
            return Collections.emptyMap();
        }

        Map<Coordenada, List<Integer>> resultado = new LinkedHashMap<>();
        for (int fila = 0; fila < 9; fila++) {
            if (tablero.getValor(fila, columna) == 0) {
                resultado.put(new Coordenada(fila, columna), numerosPosiblesEnCelda(tablero, fila, columna));
            }
        }
        return resultado;
    }

    public Map<Coordenada, List<Integer>> numerosPosiblesEnBloque(TableroSudoku tablero, int fila, int columna) {
        if (!ValidadorSudoku.posicionValida(fila, columna)) {
            return Collections.emptyMap();
        }

        int filaInicial = (fila / 3) * 3;
        int columnaInicial = (columna / 3) * 3;
        Map<Coordenada, List<Integer>> resultado = new LinkedHashMap<>();

        for (int i = filaInicial; i < filaInicial + 3; i++) {
            for (int j = columnaInicial; j < columnaInicial + 3; j++) {
                if (tablero.getValor(i, j) == 0) {
                    resultado.put(new Coordenada(i, j), numerosPosiblesEnCelda(tablero, i, j));
                }
            }
        }
        return resultado;
    }
}
