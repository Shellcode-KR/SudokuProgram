package com.flavio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResolutorSudoku {

    private final AnalizadorCandidatos analizadorCandidatos = new AnalizadorCandidatos();

    public PasoResolucion resolverSiguienteNumero(TableroSudoku tablero) {
        PasoResolucion paso = resolverUnicoCandidatoCelda(tablero);
        if (paso.isResuelto()) {
            return paso;
        }

        paso = resolverUnicoLugarFila(tablero);
        if (paso.isResuelto()) {
            return paso;
        }

        paso = resolverUnicoLugarColumna(tablero);
        if (paso.isResuelto()) {
            return paso;
        }

        paso = resolverUnicoLugarBloque(tablero);
        if (paso.isResuelto()) {
            return paso;
        }

        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverUnicoCandidatoCelda(TableroSudoku tablero) {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (tablero.getValor(fila, columna) != 0) {
                    continue;
                }

                List<Integer> posibles = analizadorCandidatos.numerosPosiblesEnCelda(tablero, fila, columna);
                if (posibles.size() == 1) {
                    int numero = posibles.get(0);
                    tablero.setNumero(fila, columna, numero);
                    List<Coordenada> analizadas = new ArrayList<>();
                    analizadas.add(new Coordenada(fila, columna));
                    return new PasoResolucion(true, fila, columna, numero,
                            "Único candidato en celda",
                            "La celda " + new Coordenada(fila, columna)
                                    + " solo permite el número " + numero + ".", analizadas);
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverUnicoLugarFila(TableroSudoku tablero) {
        for (int fila = 0; fila < 9; fila++) {
            Map<Coordenada, List<Integer>> candidatos = analizadorCandidatos.numerosPosiblesEnFila(tablero, fila);
            for (int numero = 1; numero <= 9; numero++) {
                Coordenada objetivo = null;
                int conteo = 0;
                for (Map.Entry<Coordenada, List<Integer>> entry : candidatos.entrySet()) {
                    if (entry.getValue().contains(numero)) {
                        objetivo = entry.getKey();
                        conteo++;
                    }
                }
                if (conteo == 1 && objetivo != null) {
                    tablero.setNumero(objetivo.getFila(), objetivo.getColumna(), numero);
                    return new PasoResolucion(true, objetivo.getFila(), objetivo.getColumna(), numero,
                            "Único lugar en fila",
                            "En la fila " + (fila + 1) + " el número " + numero
                                    + " solo podía ir en la celda " + objetivo + ".",
                            new ArrayList<>(candidatos.keySet()));
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverUnicoLugarColumna(TableroSudoku tablero) {
        for (int columna = 0; columna < 9; columna++) {
            Map<Coordenada, List<Integer>> candidatos = analizadorCandidatos.numerosPosiblesEnColumna(tablero, columna);
            for (int numero = 1; numero <= 9; numero++) {
                Coordenada objetivo = null;
                int conteo = 0;
                for (Map.Entry<Coordenada, List<Integer>> entry : candidatos.entrySet()) {
                    if (entry.getValue().contains(numero)) {
                        objetivo = entry.getKey();
                        conteo++;
                    }
                }
                if (conteo == 1 && objetivo != null) {
                    tablero.setNumero(objetivo.getFila(), objetivo.getColumna(), numero);
                    return new PasoResolucion(true, objetivo.getFila(), objetivo.getColumna(), numero,
                            "Único lugar en columna",
                            "En la columna " + (columna + 1) + " el número " + numero
                                    + " solo podía ir en la celda " + objetivo + ".",
                            new ArrayList<>(candidatos.keySet()));
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverUnicoLugarBloque(TableroSudoku tablero) {
        for (int fila = 0; fila < 9; fila += 3) {
            for (int columna = 0; columna < 9; columna += 3) {
                Map<Coordenada, List<Integer>> candidatos = analizadorCandidatos.numerosPosiblesEnBloque(tablero, fila, columna);
                for (int numero = 1; numero <= 9; numero++) {
                    Coordenada objetivo = null;
                    int conteo = 0;
                    for (Map.Entry<Coordenada, List<Integer>> entry : candidatos.entrySet()) {
                        if (entry.getValue().contains(numero)) {
                            objetivo = entry.getKey();
                            conteo++;
                        }
                    }
                    if (conteo == 1 && objetivo != null) {
                        tablero.setNumero(objetivo.getFila(), objetivo.getColumna(), numero);
                        return new PasoResolucion(true, objetivo.getFila(), objetivo.getColumna(), numero,
                                "Único lugar en bloque 3x3",
                                "En el bloque que inicia en fila " + (fila + 1) + " y columna " + (columna + 1)
                                        + ", el número " + numero + " solo podía ir en " + objetivo + ".",
                                new ArrayList<>(candidatos.keySet()));
                    }
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }
}
