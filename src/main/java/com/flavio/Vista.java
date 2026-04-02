package com.flavio;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

public class Vista extends JFrame {

    private static final Color FONDO_APP = new Color(232, 244, 253);
    private static final Color FONDO_TABLERO = new Color(240, 250, 255);
    private static final Color FONDO_CELDA = new Color(250, 254, 255);
    private static final Color FONDO_CELDA_FIJA = new Color(210, 232, 245);
    private static final Color BORDE_CELDA = new Color(142, 184, 214);
    private static final Color COLOR_BOTON = new Color(187, 222, 251);
    private static final Color COLOR_BOTON_TEXTO = new Color(27, 72, 102);
    private static final Color FONDO_MENSAJES = new Color(230, 246, 255);

    private final JTextField[][] celdas = new JTextField[9][9];
    private final JTextArea areaMensajes = new JTextArea(8, 40);
    private final TableroSudoku tablero = new TableroSudoku();
    private final AnalizadorCandidatos analizador = new AnalizadorCandidatos();
    private final ResolutorSudoku resolutor = new ResolutorSudoku();

    public Vista() {
        setTitle("Sudoku - Editor y Resolutor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(FONDO_APP);

        add(crearPanelTablero(), BorderLayout.CENTER);
        add(crearPanelAcciones(), BorderLayout.EAST);
        add(crearPanelMensajes(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel crearPanelTablero() {
        JPanel panel = new JPanel(new GridLayout(9, 9));
        panel.setBackground(FONDO_TABLERO);
        Font fuente = new Font("SansSerif", Font.BOLD, 20);

        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                JTextField campo = new JTextField();
                campo.setHorizontalAlignment(SwingConstants.CENTER);
                campo.setFont(fuente);
                campo.setBackground(FONDO_CELDA);
                campo.setBorder(BorderFactory.createLineBorder(BORDE_CELDA));
                celdas[fila][columna] = campo;
                panel.add(campo);
            }
        }
        panel.setBorder(BorderFactory.createTitledBorder("Tablero 9x9"));
        return panel;
    }

    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.setBackground(FONDO_APP);

        JButton btnCargar = crearBoton("Cargar ejemplo");
        JButton btnLimpiar = crearBoton("Limpiar tablero");
        JButton btnPosiblesCelda = crearBoton("Posibles en celda");
        JButton btnPosiblesFila = crearBoton("Posibles en fila");
        JButton btnPosiblesColumna = crearBoton("Posibles en columna");
        JButton btnPosiblesBloque = crearBoton("Posibles en bloque");
        JButton btnResolver = crearBoton("Resolver siguiente");

        btnCargar.addActionListener(e -> cargarEjemplo());
        btnLimpiar.addActionListener(e -> {
            tablero.limpiarTablero();
            refrescarVistaDesdeTablero();
            escribirMensaje("Tablero limpio.");
        });

        btnPosiblesCelda.addActionListener(e -> {
            if (!actualizarTableroDesdeVista()) {
                return;
            }
            Coordenada sel = obtenerCeldaSeleccionada();
            if (sel == null) {
                escribirMensaje("Selecciona una celda para consultar candidatos.");
                return;
            }
            List<Integer> candidatos = analizador.numerosPosiblesEnCelda(tablero, sel.getFila(), sel.getColumna());
            escribirMensaje("Posibles en celda " + sel + ": " + candidatos);
        });

        btnPosiblesFila.addActionListener(e -> {
            if (!actualizarTableroDesdeVista()) {
                return;
            }
            Coordenada sel = obtenerCeldaSeleccionada();
            if (sel == null) {
                escribirMensaje("Selecciona una celda de la fila a analizar.");
                return;
            }
            Map<Coordenada, List<Integer>> mapa = analizador.numerosPosiblesEnFila(tablero, sel.getFila());
            escribirMensaje("Candidatos por celda en fila " + (sel.getFila() + 1) + ":\n" + mapa);
        });

        btnPosiblesColumna.addActionListener(e -> {
            if (!actualizarTableroDesdeVista()) {
                return;
            }
            Coordenada sel = obtenerCeldaSeleccionada();
            if (sel == null) {
                escribirMensaje("Selecciona una celda de la columna a analizar.");
                return;
            }
            Map<Coordenada, List<Integer>> mapa = analizador.numerosPosiblesEnColumna(tablero, sel.getColumna());
            escribirMensaje("Candidatos por celda en columna " + (sel.getColumna() + 1) + ":\n" + mapa);
        });

        btnPosiblesBloque.addActionListener(e -> {
            if (!actualizarTableroDesdeVista()) {
                return;
            }
            Coordenada sel = obtenerCeldaSeleccionada();
            if (sel == null) {
                escribirMensaje("Selecciona una celda del bloque 3x3 a analizar.");
                return;
            }
            Map<Coordenada, List<Integer>> mapa = analizador.numerosPosiblesEnBloque(tablero, sel.getFila(), sel.getColumna());
            escribirMensaje("Candidatos por celda en bloque de " + sel + ":\n" + mapa);
        });

        btnResolver.addActionListener(e -> {
            if (!actualizarTableroDesdeVista()) {
                return;
            }
            PasoResolucion paso = resolutor.resolverSiguienteNumero(tablero);
            refrescarVistaDesdeTablero();
            if (!paso.isResuelto()) {
                escribirMensaje(paso.getExplicacion());
                return;
            }
            escribirMensaje("Método: " + paso.getMetodo()
                    + "\nNúmero: " + paso.getNumero()
                    + " en celda " + new Coordenada(paso.getFila(), paso.getColumna())
                    + "\nExplicación: " + paso.getExplicacion()
                    + "\nCeldas analizadas: " + paso.getCeldasAnalizadas());
        });

        panel.add(btnCargar);
        panel.add(btnLimpiar);
        panel.add(btnPosiblesCelda);
        panel.add(btnPosiblesFila);
        panel.add(btnPosiblesColumna);
        panel.add(btnPosiblesBloque);
        panel.add(btnResolver);
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));

        return panel;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(COLOR_BOTON);
        boton.setForeground(COLOR_BOTON_TEXTO);
        boton.setFocusPainted(false);
        return boton;
    }

    private JScrollPane crearPanelMensajes() {
        areaMensajes.setEditable(false);
        areaMensajes.setLineWrap(true);
        areaMensajes.setWrapStyleWord(true);
        areaMensajes.setBackground(FONDO_MENSAJES);
        JScrollPane scroll = new JScrollPane(areaMensajes);
        scroll.setBorder(BorderFactory.createTitledBorder("Explicación"));
        return scroll;
    }

    private void cargarEjemplo() {
        int[][] ejemplo = {
                {0, 0, 0, 2, 6, 0, 7, 0, 1},
                {6, 8, 0, 0, 7, 0, 0, 9, 0},
                {1, 9, 0, 0, 0, 4, 5, 0, 0},
                {8, 2, 0, 1, 0, 0, 0, 4, 0},
                {0, 0, 4, 6, 0, 2, 9, 0, 0},
                {0, 5, 0, 0, 0, 3, 0, 2, 8},
                {0, 0, 9, 3, 0, 0, 0, 7, 4},
                {0, 4, 0, 0, 5, 0, 0, 3, 6},
                {7, 0, 3, 0, 1, 8, 0, 0, 0}
        };

        tablero.cargarTableroInicial(ejemplo);
        refrescarVistaDesdeTablero();
        escribirMensaje("Ejemplo cargado. Las celdas dadas del puzzle quedan bloqueadas.");
    }

    private boolean actualizarTableroDesdeVista() {
        int[][] valores = new int[9][9];

        try {
            for (int fila = 0; fila < 9; fila++) {
                for (int columna = 0; columna < 9; columna++) {
                    String texto = celdas[fila][columna].getText().trim();
                    if (texto.isEmpty()) {
                        valores[fila][columna] = 0;
                    } else {
                        int numero = Integer.parseInt(texto);
                        if (!ValidadorSudoku.esNumeroPermitido(numero)) {
                            throw new IllegalArgumentException("Solo se permiten números del 1 al 9.");
                        }
                        valores[fila][columna] = numero;
                    }
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa solo dígitos del 1 al 9 o deja vacío.");
            return false;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return false;
        }

        if (!tablero.cargarTableroInicial(valores)) {
            JOptionPane.showMessageDialog(this, "El tablero es inconsistente (hay duplicados en fila, columna o bloque).");
            return false;
        }

        refrescarVistaDesdeTablero();
        return true;
    }

    private void refrescarVistaDesdeTablero() {
        int[][] datos = tablero.getCuadricula();
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                int valor = datos[fila][columna];
                celdas[fila][columna].setText(valor == 0 ? "" : String.valueOf(valor));
                celdas[fila][columna].setBackground(tablero.esCeldaFija(fila, columna) ? FONDO_CELDA_FIJA : FONDO_CELDA);
            }
        }
    }

    private Coordenada obtenerCeldaSeleccionada() {
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (celdas[fila][columna].isFocusOwner()) {
                    return new Coordenada(fila, columna);
                }
            }
        }
        return null;
    }

    private void escribirMensaje(String mensaje) {
        areaMensajes.setText(mensaje);
    }

    public static void iniciar() {
        SwingUtilities.invokeLater(Vista::new);
    }
}
