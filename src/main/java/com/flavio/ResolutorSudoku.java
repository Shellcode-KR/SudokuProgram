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
        if (paso.isResuelto()) return paso;

        paso = resolverUnicoLugarFila(tablero);
        if (paso.isResuelto()) return paso;

        paso = resolverUnicoLugarColumna(tablero);
        if (paso.isResuelto()) return paso;

        paso = resolverUnicoLugarBloque(tablero);
        if (paso.isResuelto()) return paso;

        paso = resolverPorParesDesnudos(tablero);
        if (paso.isResuelto()) return paso;

        paso = resolverPorParesOcultos(tablero);
        if (paso.isResuelto()) return paso;

        paso = resolverPorPointingPairs(tablero);
        if (paso.isResuelto()) return paso;

        paso = resolverPorXWing(tablero);
        if (paso.isResuelto()) return paso;

        paso = resolverPorSwordfish(tablero);
        if (paso.isResuelto()) return paso;

        paso = resolverPorXYWing(tablero);
        if (paso.isResuelto()) return paso;

        paso = resolverPorRectanguloUnico(tablero);
        if (paso.isResuelto()) return paso;

        paso = resolverPorCadenasForzadas(tablero);
        if (paso.isResuelto()) return paso;

        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverUnicoCandidatoCelda(TableroSudoku tablero) {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (tablero.getValor(fila, columna) != 0) continue;
                List<Integer> posibles = analizadorCandidatos.numerosPosiblesEnCelda(tablero, fila, columna);
                if (posibles.size() == 1) {
                    int numero = posibles.get(0);
                    tablero.setNumero(fila, columna, numero);
                    List<Coordenada> analizadas = new ArrayList<>();
                    analizadas.add(new Coordenada(fila, columna));
                    return new PasoResolucion(true, fila, columna, numero,
                            "Único candidato en celda",
                            "La celda " + new Coordenada(fila, columna) + " solo permite el número " + numero + ".",
                            analizadas);
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
                            "En la fila " + (fila + 1) + " el número " + numero + " solo podía ir en la celda " + objetivo + ".",
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
                            "En la columna " + (columna + 1) + " el número " + numero + " solo podía ir en la celda " + objetivo + ".",
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
                if (cands == null || cands.size() != 2) continue;
                Set<Integer> clave = new HashSet<>(cands);
                pares.computeIfAbsent(clave, k -> new ArrayList<>()).add(coord);
            }
            for (Map.Entry<Set<Integer>, List<Coordenada>> entry : pares.entrySet()) {
                if (entry.getValue().size() != 2) continue;
                Set<Integer> par = entry.getKey();
                Map<Coordenada, Set<Integer>> removidos = new HashMap<>();
                for (Coordenada coord : unidad) {
                    if (entry.getValue().contains(coord)) continue;
                    Set<Integer> cands = candidatos.get(coord);
                    if (cands == null) continue;
                    Set<Integer> inter = new HashSet<>(cands);
                    inter.retainAll(par);
                    if (!inter.isEmpty()) removidos.put(coord, inter);
                }
                PasoResolucion paso = generarPasoPorEliminacion(tablero, candidatos, removidos,
                        "Pares desnudos",
                        "Se detectó par desnudo " + par + " en " + entry.getValue() + ", eliminando candidatos en la unidad.",
                        unidad);
                if (paso.isResuelto()) return paso;
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverPorParesOcultos(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> candidatos = construirMapaCandidatos(tablero);
        for (List<Coordenada> unidad : construirUnidades()) {
            for (int n1 = 1; n1 <= 8; n1++) {
                for (int n2 = n1 + 1; n2 <= 9; n2++) {
                    List<Coordenada> c1 = celdasConNumero(unidad, candidatos, n1);
                    List<Coordenada> c2 = celdasConNumero(unidad, candidatos, n2);
                    if (c1.size() == 2 && c2.size() == 2 && c1.equals(c2)) {
                        Map<Coordenada, Set<Integer>> removidos = new HashMap<>();
                        for (Coordenada coord : c1) {
                            Set<Integer> q = new HashSet<>(candidatos.get(coord));
                            q.remove(n1);
                            q.remove(n2);
                            if (!q.isEmpty()) removidos.put(coord, q);
                        }
                        PasoResolucion paso = generarPasoPorEliminacion(tablero, candidatos, removidos,
                                "Pares ocultos",
                                "Par oculto [" + n1 + ", " + n2 + "] detectado en " + c1 + ".",
                                unidad);
                        if (paso.isResuelto()) return paso;
                    }
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverPorPointingPairs(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> candidatos = construirMapaCandidatos(tablero);
        for (int fb = 0; fb < 9; fb += 3) {
            for (int cb = 0; cb < 9; cb += 3) {
                List<Coordenada> bloque = unidadBloque(fb, cb);
                for (int numero = 1; numero <= 9; numero++) {
                    List<Coordenada> conNumero = celdasConNumero(bloque, candidatos, numero);
                    if (conNumero.size() < 2 || conNumero.size() > 3) continue;
                    boolean mismaFila = todosMismaFila(conNumero);
                    boolean mismaCol = todosMismaColumna(conNumero);
                    if (!mismaFila && !mismaCol) continue;

                    Map<Coordenada, Set<Integer>> removidos = new HashMap<>();
                    List<Coordenada> analizadas = new ArrayList<>(bloque);
                    if (mismaFila) {
                        int fila = conNumero.get(0).getFila();
                        for (int c = 0; c < 9; c++) {
                            if (c >= cb && c < cb + 3) continue;
                            Coordenada coord = new Coordenada(fila, c);
                            Set<Integer> cand = candidatos.get(coord);
                            if (cand != null && cand.contains(numero)) {
                                removidos.computeIfAbsent(coord, k -> new HashSet<>()).add(numero);
                                analizadas.add(coord);
                            }
                        }
                    } else {
                        int col = conNumero.get(0).getColumna();
                        for (int f = 0; f < 9; f++) {
                            if (f >= fb && f < fb + 3) continue;
                            Coordenada coord = new Coordenada(f, col);
                            Set<Integer> cand = candidatos.get(coord);
                            if (cand != null && cand.contains(numero)) {
                                removidos.computeIfAbsent(coord, k -> new HashSet<>()).add(numero);
                                analizadas.add(coord);
                            }
                        }
                    }
                    PasoResolucion paso = generarPasoPorEliminacion(tablero, candidatos, removidos,
                            "Pointing pairs",
                            "En bloque (" + (fb + 1) + ", " + (cb + 1) + ") el número " + numero + " quedó alineado.",
                            analizadas);
                    if (paso.isResuelto()) return paso;
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverPorXWing(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> candidatos = construirMapaCandidatos(tablero);

        for (int numero = 1; numero <= 9; numero++) {
            // patrón por filas
            for (int r1 = 0; r1 < 8; r1++) {
                List<Integer> colsR1 = columnasConNumero(candidatos, r1, numero);
                if (colsR1.size() != 2) continue;
                for (int r2 = r1 + 1; r2 < 9; r2++) {
                    List<Integer> colsR2 = columnasConNumero(candidatos, r2, numero);
                    if (!colsR1.equals(colsR2)) continue;

                    Map<Coordenada, Set<Integer>> removidos = new HashMap<>();
                    List<Coordenada> analizadas = new ArrayList<>();
                    analizadas.add(new Coordenada(r1, colsR1.get(0)));
                    analizadas.add(new Coordenada(r1, colsR1.get(1)));
                    analizadas.add(new Coordenada(r2, colsR1.get(0)));
                    analizadas.add(new Coordenada(r2, colsR1.get(1)));

                    for (int r = 0; r < 9; r++) {
                        if (r == r1 || r == r2) continue;
                        for (Integer c : colsR1) {
                            Coordenada coord = new Coordenada(r, c);
                            Set<Integer> cand = candidatos.get(coord);
                            if (cand != null && cand.contains(numero)) {
                                removidos.computeIfAbsent(coord, k -> new HashSet<>()).add(numero);
                            }
                        }
                    }

                    PasoResolucion paso = generarPasoPorEliminacion(tablero, candidatos, removidos,
                            "X-Wing",
                            "X-Wing detectado para número " + numero + " en filas " + (r1 + 1) + " y " + (r2 + 1)
                                    + " columnas " + (colsR1.get(0) + 1) + " y " + (colsR1.get(1) + 1) + ".",
                            analizadas);
                    if (paso.isResuelto()) return paso;
                }
            }

            // patrón por columnas
            for (int c1 = 0; c1 < 8; c1++) {
                List<Integer> filasC1 = filasConNumero(candidatos, c1, numero);
                if (filasC1.size() != 2) continue;
                for (int c2 = c1 + 1; c2 < 9; c2++) {
                    List<Integer> filasC2 = filasConNumero(candidatos, c2, numero);
                    if (!filasC1.equals(filasC2)) continue;

                    Map<Coordenada, Set<Integer>> removidos = new HashMap<>();
                    List<Coordenada> analizadas = new ArrayList<>();
                    analizadas.add(new Coordenada(filasC1.get(0), c1));
                    analizadas.add(new Coordenada(filasC1.get(1), c1));
                    analizadas.add(new Coordenada(filasC1.get(0), c2));
                    analizadas.add(new Coordenada(filasC1.get(1), c2));

                    for (int c = 0; c < 9; c++) {
                        if (c == c1 || c == c2) continue;
                        for (Integer f : filasC1) {
                            Coordenada coord = new Coordenada(f, c);
                            Set<Integer> cand = candidatos.get(coord);
                            if (cand != null && cand.contains(numero)) {
                                removidos.computeIfAbsent(coord, k -> new HashSet<>()).add(numero);
                            }
                        }
                    }

                    PasoResolucion paso = generarPasoPorEliminacion(tablero, candidatos, removidos,
                            "X-Wing",
                            "X-Wing detectado para número " + numero + " en columnas " + (c1 + 1) + " y " + (c2 + 1)
                                    + " filas " + (filasC1.get(0) + 1) + " y " + (filasC1.get(1) + 1) + ".",
                            analizadas);
                    if (paso.isResuelto()) return paso;
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverPorSwordfish(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> candidatos = construirMapaCandidatos(tablero);
        for (int numero = 1; numero <= 9; numero++) {
            // swordfish por filas
            for (int r1 = 0; r1 < 7; r1++) {
                for (int r2 = r1 + 1; r2 < 8; r2++) {
                    for (int r3 = r2 + 1; r3 < 9; r3++) {
                        Set<Integer> unionCols = new HashSet<>();
                        List<Integer> c1 = columnasConNumero(candidatos, r1, numero);
                        List<Integer> c2 = columnasConNumero(candidatos, r2, numero);
                        List<Integer> c3 = columnasConNumero(candidatos, r3, numero);
                        if (c1.size() < 2 || c1.size() > 3 || c2.size() < 2 || c2.size() > 3 || c3.size() < 2 || c3.size() > 3) continue;
                        unionCols.addAll(c1); unionCols.addAll(c2); unionCols.addAll(c3);
                        if (unionCols.size() != 3) continue;

                        Map<Coordenada, Set<Integer>> removidos = new HashMap<>();
                        List<Coordenada> analizadas = new ArrayList<>();
                        for (Integer col : unionCols) {
                            analizadas.add(new Coordenada(r1, col));
                            analizadas.add(new Coordenada(r2, col));
                            analizadas.add(new Coordenada(r3, col));
                            for (int r = 0; r < 9; r++) {
                                if (r == r1 || r == r2 || r == r3) continue;
                                Coordenada coord = new Coordenada(r, col);
                                Set<Integer> cand = candidatos.get(coord);
                                if (cand != null && cand.contains(numero)) {
                                    removidos.computeIfAbsent(coord, k -> new HashSet<>()).add(numero);
                                }
                            }
                        }

                        PasoResolucion paso = generarPasoPorEliminacion(tablero, candidatos, removidos,
                                "Swordfish",
                                "Swordfish por filas detectado para número " + numero + " en filas " + (r1 + 1) + ", " + (r2 + 1) + ", " + (r3 + 1) + ".",
                                analizadas);
                        if (paso.isResuelto()) return paso;
                    }
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverPorXYWing(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> candidatos = construirMapaCandidatos(tablero);
        List<Coordenada> bivalue = new ArrayList<>();
        for (Map.Entry<Coordenada, Set<Integer>> e : candidatos.entrySet()) {
            if (e.getValue().size() == 2) bivalue.add(e.getKey());
        }

        for (Coordenada pivot : bivalue) {
            List<Integer> pv = new ArrayList<>(candidatos.get(pivot));
            int x = pv.get(0), y = pv.get(1);
            for (Coordenada w1 : bivalue) {
                if (w1.equals(pivot) || !sonPares(pivot, w1)) continue;
                Set<Integer> c1 = candidatos.get(w1);
                if (!(c1.contains(x) && !c1.contains(y))) continue;
                int z = otroNumero(c1, x);

                for (Coordenada w2 : bivalue) {
                    if (w2.equals(pivot) || w2.equals(w1) || !sonPares(pivot, w2)) continue;
                    Set<Integer> c2 = candidatos.get(w2);
                    if (!(c2.contains(y) && !c2.contains(x) && c2.contains(z))) continue;

                    Map<Coordenada, Set<Integer>> removidos = new HashMap<>();
                    List<Coordenada> analizadas = new ArrayList<>();
                    analizadas.add(pivot); analizadas.add(w1); analizadas.add(w2);

                    for (Map.Entry<Coordenada, Set<Integer>> e : candidatos.entrySet()) {
                        Coordenada c = e.getKey();
                        if (c.equals(pivot) || c.equals(w1) || c.equals(w2)) continue;
                        if (sonPares(c, w1) && sonPares(c, w2) && e.getValue().contains(z)) {
                            removidos.computeIfAbsent(c, k -> new HashSet<>()).add(z);
                        }
                    }

                    PasoResolucion paso = generarPasoPorEliminacion(tablero, candidatos, removidos,
                            "XY-Wing",
                            "XY-Wing detectado con pivote " + pivot + " y alas " + w1 + " / " + w2 + ".",
                            analizadas);
                    if (paso.isResuelto()) return paso;
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverPorRectanguloUnico(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> candidatos = construirMapaCandidatos(tablero);

        for (int r1 = 0; r1 < 8; r1++) {
            for (int r2 = r1 + 1; r2 < 9; r2++) {
                for (int c1 = 0; c1 < 8; c1++) {
                    for (int c2 = c1 + 1; c2 < 9; c2++) {
                        Coordenada a = new Coordenada(r1, c1);
                        Coordenada b = new Coordenada(r1, c2);
                        Coordenada c = new Coordenada(r2, c1);
                        Coordenada d = new Coordenada(r2, c2);

                        Set<Integer> ca = candidatos.get(a);
                        Set<Integer> cb = candidatos.get(b);
                        Set<Integer> cc = candidatos.get(c);
                        Set<Integer> cd = candidatos.get(d);
                        if (ca == null || cb == null || cc == null || cd == null) continue;

                        Set<Integer> inter = new HashSet<>(ca);
                        inter.retainAll(cb); inter.retainAll(cc); inter.retainAll(cd);
                        if (inter.size() != 2) continue;

                        List<Coordenada> cells = new ArrayList<>();
                        cells.add(a); cells.add(b); cells.add(c); cells.add(d);
                        for (Coordenada target : cells) {
                            Set<Integer> cand = candidatos.get(target);
                            if (cand.size() > 2 && cand.containsAll(inter)) {
                                Map<Coordenada, Set<Integer>> removidos = new HashMap<>();
                                removidos.put(target, new HashSet<>(inter));
                                PasoResolucion paso = generarPasoPorEliminacion(tablero, candidatos, removidos,
                                        "Rectángulo único",
                                        "Rectángulo único detectado en filas " + (r1 + 1) + "/" + (r2 + 1)
                                                + " y columnas " + (c1 + 1) + "/" + (c2 + 1) + ".",
                                        cells);
                                if (paso.isResuelto()) return paso;
                            }
                        }
                    }
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private PasoResolucion resolverPorCadenasForzadas(TableroSudoku tablero) {
        int[][] base = tablero.getCuadricula();

        for (int fila = 0; fila < 9; fila++) {
            for (int col = 0; col < 9; col++) {
                if (base[fila][col] != 0) continue;
                List<Integer> cands = candidatosEnMatriz(base, fila, col);
                if (cands.size() != 2) continue;

                int a = cands.get(0);
                int b = cands.get(1);

                boolean contradiceA = generaContradiccion(base, fila, col, a);
                boolean contradiceB = generaContradiccion(base, fila, col, b);

                if (contradiceA && !contradiceB) {
                    tablero.setNumero(fila, col, b);
                    List<Coordenada> analizadas = new ArrayList<>();
                    analizadas.add(new Coordenada(fila, col));
                    return new PasoResolucion(true, fila, col, b,
                            "Cadenas forzadas",
                            "Asumir " + a + " en " + new Coordenada(fila, col)
                                    + " produce contradicción, por lo tanto se fuerza " + b + ".",
                            analizadas);
                }
                if (contradiceB && !contradiceA) {
                    tablero.setNumero(fila, col, a);
                    List<Coordenada> analizadas = new ArrayList<>();
                    analizadas.add(new Coordenada(fila, col));
                    return new PasoResolucion(true, fila, col, a,
                            "Cadenas forzadas",
                            "Asumir " + b + " en " + new Coordenada(fila, col)
                                    + " produce contradicción, por lo tanto se fuerza " + a + ".",
                            analizadas);
                }
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private boolean generaContradiccion(int[][] base, int fila, int col, int valor) {
        int[][] sim = copiarMatriz(base);
        sim[fila][col] = valor;
        if (!ValidadorSudoku.tableroConsistente(sim)) return true;

        boolean cambio;
        do {
            cambio = false;
            for (int f = 0; f < 9; f++) {
                for (int c = 0; c < 9; c++) {
                    if (sim[f][c] != 0) continue;
                    List<Integer> cands = candidatosEnMatriz(sim, f, c);
                    if (cands.isEmpty()) return true;
                    if (cands.size() == 1) {
                        sim[f][c] = cands.get(0);
                        cambio = true;
                    }
                }
            }
            if (!ValidadorSudoku.tableroConsistente(sim)) return true;
        } while (cambio);

        return false;
    }

    private List<Integer> candidatosEnMatriz(int[][] tablero, int fila, int col) {
        List<Integer> candidatos = new ArrayList<>();
        for (int n = 1; n <= 9; n++) {
            if (ValidadorSudoku.puedeColocar(tablero, fila, col, n)) candidatos.add(n);
        }
        return candidatos;
    }

    private int[][] copiarMatriz(int[][] origen) {
        int[][] copia = new int[9][9];
        for (int i = 0; i < 9; i++) System.arraycopy(origen[i], 0, copia[i], 0, 9);
        return copia;
    }

    private PasoResolucion generarPasoPorEliminacion(TableroSudoku tablero,
                                                     Map<Coordenada, Set<Integer>> candidatos,
                                                     Map<Coordenada, Set<Integer>> removidos,
                                                     String tecnica,
                                                     String detalle,
                                                     List<Coordenada> analizadas) {
        if (removidos.isEmpty()) return PasoResolucion.sinResolucion();
        for (Map.Entry<Coordenada, Set<Integer>> entry : removidos.entrySet()) {
            Coordenada coord = entry.getKey();
            Set<Integer> nuevos = new HashSet<>(candidatos.get(coord));
            nuevos.removeAll(entry.getValue());
            if (nuevos.size() == 1) {
                int numero = nuevos.iterator().next();
                tablero.setNumero(coord.getFila(), coord.getColumna(), numero);
                return new PasoResolucion(true, coord.getFila(), coord.getColumna(), numero,
                        tecnica,
                        detalle + " Tras la eliminación, la celda " + coord + " queda forzada a " + numero + ".",
                        analizadas);
            }
        }
        return PasoResolucion.sinResolucion();
    }

    private Map<Coordenada, Set<Integer>> construirMapaCandidatos(TableroSudoku tablero) {
        Map<Coordenada, Set<Integer>> mapa = new HashMap<>();
        for (int fila = 0; fila < 9; fila++) {
            for (int col = 0; col < 9; col++) {
                if (tablero.getValor(fila, col) == 0) {
                    mapa.put(new Coordenada(fila, col), new HashSet<>(analizadorCandidatos.numerosPosiblesEnCelda(tablero, fila, col)));
                }
            }
        }
        return mapa;
    }

    private List<List<Coordenada>> construirUnidades() {
        List<List<Coordenada>> unidades = new ArrayList<>();
        for (int fila = 0; fila < 9; fila++) {
            List<Coordenada> unidad = new ArrayList<>();
            for (int col = 0; col < 9; col++) unidad.add(new Coordenada(fila, col));
            unidades.add(unidad);
        }
        for (int col = 0; col < 9; col++) {
            List<Coordenada> unidad = new ArrayList<>();
            for (int fila = 0; fila < 9; fila++) unidad.add(new Coordenada(fila, col));
            unidades.add(unidad);
        }
        for (int fila = 0; fila < 9; fila += 3) {
            for (int col = 0; col < 9; col += 3) unidades.add(unidadBloque(fila, col));
        }
        return unidades;
    }

    private List<Coordenada> unidadBloque(int filaInicial, int colInicial) {
        List<Coordenada> bloque = new ArrayList<>();
        for (int fila = filaInicial; fila < filaInicial + 3; fila++) {
            for (int col = colInicial; col < colInicial + 3; col++) bloque.add(new Coordenada(fila, col));
        }
        return bloque;
    }

    private List<Coordenada> celdasConNumero(List<Coordenada> unidad, Map<Coordenada, Set<Integer>> candidatos, int numero) {
        List<Coordenada> out = new ArrayList<>();
        for (Coordenada coord : unidad) {
            Set<Integer> cands = candidatos.get(coord);
            if (cands != null && cands.contains(numero)) out.add(coord);
        }
        return out;
    }

    private boolean todosMismaFila(List<Coordenada> coords) {
        int fila = coords.get(0).getFila();
        for (Coordenada c : coords) if (c.getFila() != fila) return false;
        return true;
    }

    private boolean todosMismaColumna(List<Coordenada> coords) {
        int col = coords.get(0).getColumna();
        for (Coordenada c : coords) if (c.getColumna() != col) return false;
        return true;
    }

    private List<Integer> columnasConNumero(Map<Coordenada, Set<Integer>> candidatos, int fila, int numero) {
        List<Integer> cols = new ArrayList<>();
        for (int col = 0; col < 9; col++) {
            Set<Integer> c = candidatos.get(new Coordenada(fila, col));
            if (c != null && c.contains(numero)) cols.add(col);
        }
        return cols;
    }

    private List<Integer> filasConNumero(Map<Coordenada, Set<Integer>> candidatos, int columna, int numero) {
        List<Integer> filas = new ArrayList<>();
        for (int fila = 0; fila < 9; fila++) {
            Set<Integer> c = candidatos.get(new Coordenada(fila, columna));
            if (c != null && c.contains(numero)) filas.add(fila);
        }
        return filas;
    }

    private boolean sonPares(Coordenada a, Coordenada b) {
        return a.getFila() == b.getFila()
                || a.getColumna() == b.getColumna()
                || ((a.getFila() / 3) == (b.getFila() / 3) && (a.getColumna() / 3) == (b.getColumna() / 3));
    }

    private int otroNumero(Set<Integer> par, int conocido) {
        for (Integer n : par) {
            if (n != conocido) return n;
        }
        return -1;
    }
}
