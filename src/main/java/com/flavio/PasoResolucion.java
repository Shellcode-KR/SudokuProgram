package com.flavio;

<<<<<<< codex/create-sudoku-application-with-validation-methods-mcan4l
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PasoResolucion {

    public enum TipoRazonamiento {
        LOGICA_BASICA,
        LOGICA_INTERMEDIA,
        LOGICA_AVANZADA,
        HIPOTESIS_CONTRADICCION,
        SIN_RESOLUCION
    }

=======
import java.util.Collections;
import java.util.List;

public class PasoResolucion {
>>>>>>> main
    private final boolean resuelto;
    private final int fila;
    private final int columna;
    private final int numero;
    private final String metodo;
    private final String explicacion;
    private final List<Coordenada> celdasAnalizadas;

<<<<<<< codex/create-sudoku-application-with-validation-methods-mcan4l
    private final TipoRazonamiento tipoRazonamiento;
    private final List<Coordenada> celdasPatron;
    private final List<Coordenada> celdasAfectadas;
    private final Map<Coordenada, List<Integer>> candidatosEliminadosPorCelda;
    private final String unidadClave;
    private final List<String> explicacionPasoAPaso;

    public PasoResolucion(boolean resuelto, int fila, int columna, int numero,
                          String metodo, String explicacion, List<Coordenada> celdasAnalizadas) {
        this(resuelto, fila, columna, numero, metodo, explicacion, celdasAnalizadas,
                inferirTipo(metodo),
                celdasAnalizadas,
                Collections.singletonList(new Coordenada(fila, columna)),
                Collections.emptyMap(),
                "No aplica",
                construirPasosBase(metodo, explicacion));
    }

    public PasoResolucion(boolean resuelto, int fila, int columna, int numero,
                          String metodo, String explicacion, List<Coordenada> celdasAnalizadas,
                          TipoRazonamiento tipoRazonamiento,
                          List<Coordenada> celdasPatron,
                          List<Coordenada> celdasAfectadas,
                          Map<Coordenada, List<Integer>> candidatosEliminadosPorCelda,
                          String unidadClave,
                          List<String> explicacionPasoAPaso) {
=======
    public PasoResolucion(boolean resuelto, int fila, int columna, int numero,
                          String metodo, String explicacion, List<Coordenada> celdasAnalizadas) {
>>>>>>> main
        this.resuelto = resuelto;
        this.fila = fila;
        this.columna = columna;
        this.numero = numero;
        this.metodo = metodo;
        this.explicacion = explicacion;
<<<<<<< codex/create-sudoku-application-with-validation-methods-mcan4l
        this.celdasAnalizadas = celdasAnalizadas == null ? Collections.emptyList() : celdasAnalizadas;
        this.tipoRazonamiento = tipoRazonamiento;
        this.celdasPatron = celdasPatron == null ? Collections.emptyList() : celdasPatron;
        this.celdasAfectadas = celdasAfectadas == null ? Collections.emptyList() : celdasAfectadas;
        this.candidatosEliminadosPorCelda = candidatosEliminadosPorCelda == null ? Collections.emptyMap() : candidatosEliminadosPorCelda;
        this.unidadClave = unidadClave == null ? "No aplica" : unidadClave;
        this.explicacionPasoAPaso = explicacionPasoAPaso == null ? Collections.emptyList() : explicacionPasoAPaso;
    }

    public static PasoResolucion sinResolucion() {
        List<String> pasos = new ArrayList<>();
        pasos.add("No se encontró un patrón aplicable con las técnicas activas.");
        pasos.add("Intenta permitir técnicas de hipótesis o mostrar deducción intermedia.");

        return new PasoResolucion(false, -1, -1, -1,
                "Sin resolución",
                "No se encontró una jugada con las técnicas implementadas.",
                Collections.emptyList(),
                TipoRazonamiento.SIN_RESOLUCION,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyMap(),
                "Sin unidad clave",
                pasos);
=======
        this.celdasAnalizadas = celdasAnalizadas;
    }

    public static PasoResolucion sinResolucion() {
        return new PasoResolucion(false, -1, -1, -1,
                "Sin resolución", "No se encontró una jugada con las técnicas implementadas.", Collections.emptyList());
>>>>>>> main
    }

    public boolean isResuelto() {
        return resuelto;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public int getNumero() {
        return numero;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getExplicacion() {
        return explicacion;
    }

    public List<Coordenada> getCeldasAnalizadas() {
        return celdasAnalizadas;
    }
<<<<<<< codex/create-sudoku-application-with-validation-methods-mcan4l

    public TipoRazonamiento getTipoRazonamiento() {
        return tipoRazonamiento;
    }

    public List<Coordenada> getCeldasPatron() {
        return celdasPatron;
    }

    public List<Coordenada> getCeldasAfectadas() {
        return celdasAfectadas;
    }

    public Map<Coordenada, List<Integer>> getCandidatosEliminadosPorCelda() {
        return candidatosEliminadosPorCelda;
    }

    public String getUnidadClave() {
        return unidadClave;
    }

    public List<String> getExplicacionPasoAPaso() {
        return explicacionPasoAPaso;
    }

    private static TipoRazonamiento inferirTipo(String metodo) {
        if (metodo == null) return TipoRazonamiento.SIN_RESOLUCION;
        if (metodo.equalsIgnoreCase("Cadenas forzadas")) return TipoRazonamiento.HIPOTESIS_CONTRADICCION;
        if (metodo.startsWith("Único")) return TipoRazonamiento.LOGICA_BASICA;
        if (metodo.equalsIgnoreCase("Pares desnudos")
                || metodo.equalsIgnoreCase("Pares ocultos")
                || metodo.equalsIgnoreCase("Pointing pairs")) {
            return TipoRazonamiento.LOGICA_INTERMEDIA;
        }
        if (metodo.equalsIgnoreCase("X-Wing")
                || metodo.equalsIgnoreCase("Swordfish")
                || metodo.equalsIgnoreCase("XY-Wing")
                || metodo.equalsIgnoreCase("Rectángulo único")) {
            return TipoRazonamiento.LOGICA_AVANZADA;
        }
        return TipoRazonamiento.SIN_RESOLUCION;
    }

    private static List<String> construirPasosBase(String metodo, String explicacion) {
        List<String> pasos = new ArrayList<>();
        pasos.add("Se evaluó la técnica: " + metodo + ".");
        pasos.add(explicacion);
        return pasos;
    }
=======
>>>>>>> main
}
