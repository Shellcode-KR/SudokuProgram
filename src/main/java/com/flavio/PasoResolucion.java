package com.flavio;

import java.util.Collections;
import java.util.List;

public class PasoResolucion {
    private final boolean resuelto;
    private final int fila;
    private final int columna;
    private final int numero;
    private final String metodo;
    private final String explicacion;
    private final List<Coordenada> celdasAnalizadas;

    public PasoResolucion(boolean resuelto, int fila, int columna, int numero,
                          String metodo, String explicacion, List<Coordenada> celdasAnalizadas) {
        this.resuelto = resuelto;
        this.fila = fila;
        this.columna = columna;
        this.numero = numero;
        this.metodo = metodo;
        this.explicacion = explicacion;
        this.celdasAnalizadas = celdasAnalizadas;
    }

    public static PasoResolucion sinResolucion() {
        return new PasoResolucion(false, -1, -1, -1,
                "Sin resolución", "No se encontró una jugada con las técnicas implementadas.", Collections.emptyList());
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
}
