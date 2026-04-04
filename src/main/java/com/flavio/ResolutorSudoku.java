package com.flavio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        paso = resolverPorParesDesnudos(tablero);
        if (paso.isResuelto()) {
            return paso;
        }

        paso = resolverPorParesOcultos(tablero);
        if (paso.isResuelto()) {
            return paso;
        }

        paso = resolverPorPointingPairs(tablero);
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

    private PasoResolucion resolverPorParesDesnudos(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> candidatos = construirMapaCandidatos(tablero);

        for (List<Coordenada> unidad : construirUnidades()) {
            Map<Set<Integer>, List<Coordenada>> pares = new HashMap<>();
            for (Coordenada coord : unidad) {
                Set<Integer> cands = candidatos.get(coord);
                if (cands == null || cands.size() != 2) {
                    continue;
                }
                Set<Integer> clave = new HashSet<>(cands);
                pares.computeIfAbsent(clave, k -> new ArrayList<>()).add(coord);
            }

            for (Map.Entry<Set<Integer>, List<Coordenada>> entry : pares.entrySet()) {
                if (entry.getValue().size() != 2) {
                    continue;
                }
                Set<Integer> par = entry.getKey();
                Map<Coordenada, Set<Integer>> removidos = new HashMap<>();

                for (Coordenada coord : unidad) {
                    if (entry.getValue().contains(coord)) {
                        continue;
                    }
                    Set<Integer> cands = candidatos.get(coord);
                    if (cands == null) {
                        continue;
                    }
                    Set<Integer> inter = new HashSet<>(cands);
                    inter.retainAll(par);
                    if (!inter.isEmpty()) {
                        removidos.put(coord, inter);
                    }
                }

                PasoResolucion paso = generarPasoPorEliminacion(
                        tablero,
                        candidatos,
                        removidos,
                        "Pares desnudos",
                        "Se detectó par desnudo " + par + " en " + entry.getValue()
                                + ", eliminando esos candidatos en la unidad.",
                        unidad
                );
                if (paso.isResuelto()) {
                    return paso;
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverPorParesOcultos(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> candidatos = construirMapaCandidatos(tablero);

        for (List<Coordenada> unidad : construirUnidades()) {
            for (int n1 = 1; n1 <= 8; n1++) {
                for (int n2 = n1 + 1; n2 <= 9; n2++) {
                    List<Coordenada> celdasN1 = celdasConNumero(unidad, candidatos, n1);
                    List<Coordenada> celdasN2 = celdasConNumero(unidad, candidatos, n2);

                    if (celdasN1.size() == 2 && celdasN2.size() == 2 && celdasN1.equals(celdasN2)) {
                        Map<Coordenada, Set<Integer>> removidos = new HashMap<>();
                        for (Coordenada coord : celdasN1) {
                            Set<Integer> cands = candidatos.get(coord);
                            if (cands == null) {
                                continue;
                            }
                            Set<Integer> quitar = new HashSet<>(cands);
                            quitar.remove(n1);
                            quitar.remove(n2);
                            if (!quitar.isEmpty()) {
                                removidos.put(coord, quitar);
                            }
                        }

                        PasoResolucion paso = generarPasoPorEliminacion(
                                tablero,
                                candidatos,
                                removidos,
                                "Pares ocultos",
                                "Se detectó par oculto [" + n1 + ", " + n2 + "] en " + celdasN1
                                        + ", restringiendo candidatos de esas celdas.",
                                unidad
                        );
                        if (paso.isResuelto()) {
                            return paso;
                        }
                    }
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverPorPointingPairs(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> candidatos = construirMapaCandidatos(tablero);

        for (int filaBloque = 0; filaBloque < 9; filaBloque += 3) {
            for (int colBloque = 0; colBloque < 9; colBloque += 3) {
                List<Coordenada> bloque = unidadBloque(filaBloque, colBloque);

                for (int numero = 1; numero <= 9; numero++) {
                    List<Coordenada> conNumero = celdasConNumero(bloque, candidatos, numero);
                    if (conNumero.size() < 2 || conNumero.size() > 3) {
                        continue;
                    }

                    boolean mismaFila = todosMismaFila(conNumero);
                    boolean mismaColumna = todosMismaColumna(conNumero);

                    if (!mismaFila && !mismaColumna) {
                        continue;
                    }

                    Map<Coordenada, Set<Integer>> removidos = new HashMap<>();
                    List<Coordenada> analizadas = new ArrayList<>(bloque);

                    if (mismaFila) {
                        int fila = conNumero.get(0).getFila();
                        for (int col = 0; col < 9; col++) {
                            if (col >= colBloque && col < colBloque + 3) {
                                continue;
                            }
                            Coordenada coord = new Coordenada(fila, col);
                            Set<Integer> cands = candidatos.get(coord);
                            if (cands != null && cands.contains(numero)) {
                                Set<Integer> quitar = new HashSet<>();
                                quitar.add(numero);
                                removidos.put(coord, quitar);
                                analizadas.add(coord);
                            }
                        }
                    } else {
                        int columna = conNumero.get(0).getColumna();
                        for (int fila = 0; fila < 9; fila++) {
                            if (fila >= filaBloque && fila < filaBloque + 3) {
                                continue;
                            }
                            Coordenada coord = new Coordenada(fila, columna);
                            Set<Integer> cands = candidatos.get(coord);
                            if (cands != null && cands.contains(numero)) {
                                Set<Integer> quitar = new HashSet<>();
                                quitar.add(numero);
                                removidos.put(coord, quitar);
                                analizadas.add(coord);
                            }
                        }
                    }

                    PasoResolucion paso = generarPasoPorEliminacion(
                            tablero,
                            candidatos,
                            removidos,
                            "Pointing pairs",
                            "En el bloque iniciado en (" + (filaBloque + 1) + ", " + (colBloque + 1)
                                    + ") el número " + numero + " quedó alineado, permitiendo eliminarlo fuera del bloque.",
                            analizadas
                    );
                    if (paso.isResuelto()) {
                        return paso;
                    }
                }
            }
        }

        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion generarPasoPorEliminacion(TableroSudoku tablero,
                                                     Map<Coordenada, Set<Integer>> candidatos,
                                                     Map<Coordenada, Set<Integer>> removidos,
                                                     String tecnica,
                                                     String detalle,
                                                     List<Coordenada> analizadas) {
        if (removidos.isEmpty()) {
            return PasoResolucion.sinResolucion();
        }

        for (Map.Entry<Coordenada, Set<Integer>> entry : removidos.entrySet()) {
            Coordenada coord = entry.getKey();
            Set<Integer> nuevos = new HashSet<>(candidatos.get(coord));
            nuevos.removeAll(entry.getValue());

            if (nuevos.size() == 1) {
                int numero = nuevos.iterator().next();
                tablero.setNumero(coord.getFila(), coord.getColumna(), numero);
                return new PasoResolucion(
                        true,
                        coord.getFila(),
                        coord.getColumna(),
                        numero,
                        tecnica,
                        detalle + " Tras la eliminación, la celda " + coord + " queda forzada a " + numero + ".",
                        analizadas
                );
            }
        }

        return PasoResolucion.sinResolucion();
    }

    private Map<Coordenada, Set<Integer>> construirMapaCandidatos(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> mapa = new HashMap<>();
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (tablero.getValor(fila, columna) == 0) {
                    Coordenada coord = new Coordenada(fila, columna);
                    mapa.put(coord, new HashSet<>(analizadorCandidatos.numerosPosiblesEnCelda(tablero, fila, columna)));
                }
            }
        }
        return mapa;
    }

    private List<List<Coordenada>> construirUnidades() {
        List<List<Coordenada>> unidades = new ArrayList<>();

        for (int fila = 0; fila < 9; fila++) {
            List<Coordenada> unidad = new ArrayList<>();
            for (int col = 0; col < 9; col++) {
                unidad.add(new Coordenada(fila, col));
            }
            unidades.add(unidad);
        }

        for (int col = 0; col < 9; col++) {
            List<Coordenada> unidad = new ArrayList<>();
            for (int fila = 0; fila < 9; fila++) {
                unidad.add(new Coordenada(fila, col));
            }
            unidades.add(unidad);
        }

        for (int fila = 0; fila < 9; fila += 3) {
            for (int col = 0; col < 9; col += 3) {
                unidades.add(unidadBloque(fila, col));
            }
        }

        return unidades;
    }

    private List<Coordenada> unidadBloque(int filaInicial, int colInicial) {
        List<Coordenada> bloque = new ArrayList<>();
        for (int fila = filaInicial; fila < filaInicial + 3; fila++) {
            for (int col = colInicial; col < colInicial + 3; col++) {
                bloque.add(new Coordenada(fila, col));
            }
        }
        return bloque;
    }

    private List<Coordenada> celdasConNumero(List<Coordenada> unidad,
                                             Map<Coordenada, Set<Integer>> candidatos,
                                             int numero) {
        List<Coordenada> resultado = new ArrayList<>();
        for (Coordenada coord : unidad) {
            Set<Integer> cands = candidatos.get(coord);
            if (cands != null && cands.contains(numero)) {
                resultado.add(coord);
            }
        }
        return resultado;
    }

    private boolean todosMismaFila(List<Coordenada> coords) {
        int fila = coords.get(0).getFila();
        for (Coordenada c : coords) {
            if (c.getFila() != fila) {
                return false;
            }
        }
        return true;
    }

    private boolean todosMismaColumna(List<Coordenada> coords) {
        int columna = coords.get(0).getColumna();
        for (Coordenada c : coords) {
            if (c.getColumna() != columna) {
                return false;
            }
        }
        return true;
    }
}
