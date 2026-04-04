package com.flavio;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Vista extends JFrame {

    private static final Color FONDO_APP = new Color(232, 244, 253);
    private static final Color FONDO_TABLERO = new Color(240, 250, 255);
    private static final Color FONDO_CELDA = new Color(250, 254, 255);
    private static final Color FONDO_CELDA_FIJA = new Color(210, 232, 245);
    private static final Color FONDO_CELDA_SELECCIONADA = new Color(183, 222, 244);
    private static final Color BORDE_CELDA = new Color(142, 184, 214);
    private static final Color COLOR_BOTON = new Color(187, 222, 251);
    private static final Color COLOR_BOTON_TEXTO = new Color(27, 72, 102);
    private static final Color FONDO_MENSAJES = new Color(230, 246, 255);

    private final JTextField[][] celdas = new JTextField[9][9];
    private final JTextArea areaMensajes = new JTextArea(10, 40);
    private final TableroSudoku tablero = new TableroSudoku();
    private final AnalizadorCandidatos analizador = new AnalizadorCandidatos();
    private final ResolutorSudoku resolutor = new ResolutorSudoku();

    private final JComboBox<Integer> selectorFila = new JComboBox<>();
    private final JComboBox<Integer> selectorColumna = new JComboBox<>();

    public Vista() {
        setTitle("Sudoku - Editor y Resolutor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(FONDO_APP);

        add(crearPanelTablero(), BorderLayout.CENTER);
        add(crearPanelAcciones(), BorderLayout.EAST);
        add(crearPanelMensajes(), BorderLayout.SOUTH);

        inicializarSelectores();
        actualizarResaltadoSeleccion();

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
                campo.setBorder(crearBordeCelda(fila, columna));

                final int filaActual = fila;
                final int columnaActual = columna;
                campo.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        selectorFila.setSelectedIndex(filaActual);
                        selectorColumna.setSelectedIndex(columnaActual);
                        actualizarResaltadoSeleccion();
                    }
                });

                celdas[fila][columna] = campo;
                panel.add(campo);
            }
        }
        panel.setBorder(BorderFactory.createTitledBorder("Tablero 9x9"));
        return panel;
    }


    private javax.swing.border.Border crearBordeCelda(int fila, int columna) {
        int grosorSuperior = (fila % 3 == 0) ? 3 : 1;
        int grosorIzquierdo = (columna % 3 == 0) ? 3 : 1;
        int grosorInferior = (fila == 8 || (fila + 1) % 3 == 0) ? 3 : 1;
        int grosorDerecho = (columna == 8 || (columna + 1) % 3 == 0) ? 3 : 1;

        return BorderFactory.createMatteBorder(
                grosorSuperior,
                grosorIzquierdo,
                grosorInferior,
                grosorDerecho,
                BORDE_CELDA
        );
    }

    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(FONDO_APP);

        JPanel panelSeleccion = new JPanel(new GridLayout(3, 2, 6, 6));
        panelSeleccion.setBackground(FONDO_APP);
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Seleccionar"));

        panelSeleccion.add(new JLabel("Fila:"));
        panelSeleccion.add(selectorFila);
        panelSeleccion.add(new JLabel("Columna:"));
        panelSeleccion.add(selectorColumna);

        JButton btnIrSeleccion = crearBoton("Ir a celda");
        btnIrSeleccion.addActionListener(e -> enfocarCeldaSeleccionada());
        panelSeleccion.add(new JLabel(""));
        panelSeleccion.add(btnIrSeleccion);

        JPanel panelBotones = new JPanel(new GridLayout(0, 1, 6, 6));
        panelBotones.setBackground(FONDO_APP);

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
            Coordenada sel = obtenerSeleccionActual();
            List<Integer> candidatos = analizador.numerosPosiblesEnCelda(tablero, sel.getFila(), sel.getColumna());
            escribirMensaje(formatearCandidatosCelda(sel, candidatos));
            enfocarCeldaSeleccionada();
        });

        btnPosiblesFila.addActionListener(e -> {
            if (!actualizarTableroDesdeVista()) {
                return;
            }
            Coordenada sel = obtenerSeleccionActual();
            Map<Coordenada, List<Integer>> mapa = analizador.numerosPosiblesEnFila(tablero, sel.getFila());
            escribirMensaje(formatearMapaCandidatos("CANDIDATOS EN FILA " + (sel.getFila() + 1), mapa));
            enfocarCeldaSeleccionada();
        });

        btnPosiblesColumna.addActionListener(e -> {
            if (!actualizarTableroDesdeVista()) {
                return;
            }
            Coordenada sel = obtenerSeleccionActual();
            Map<Coordenada, List<Integer>> mapa = analizador.numerosPosiblesEnColumna(tablero, sel.getColumna());
            escribirMensaje(formatearMapaCandidatos("CANDIDATOS EN COLUMNA " + (sel.getColumna() + 1), mapa));
            enfocarCeldaSeleccionada();
        });

        btnPosiblesBloque.addActionListener(e -> {
            if (!actualizarTableroDesdeVista()) {
                return;
            }
            Coordenada sel = obtenerSeleccionActual();
            Map<Coordenada, List<Integer>> mapa = analizador.numerosPosiblesEnBloque(tablero, sel.getFila(), sel.getColumna());
            int bloqueFila = (sel.getFila() / 3) + 1;
            int bloqueColumna = (sel.getColumna() / 3) + 1;
            escribirMensaje(formatearMapaCandidatos("CANDIDATOS EN BLOQUE (" + bloqueFila + "," + bloqueColumna + ")", mapa));
            enfocarCeldaSeleccionada();
        });

        btnResolver.addActionListener(e -> {
            if (!actualizarTableroDesdeVista()) {
                return;
            }
            PasoResolucion paso = resolutor.resolverSiguienteNumero(tablero);
            refrescarVistaDesdeTablero();
            if (!paso.isResuelto()) {
                escribirMensaje(paso.getExplicacion());
                enfocarCeldaSeleccionada();
                return;
            }

            selectorFila.setSelectedIndex(paso.getFila());
            selectorColumna.setSelectedIndex(paso.getColumna());
            actualizarResaltadoSeleccion();

            escribirMensaje(formatearPasoResolucion(paso));
            enfocarCeldaSeleccionada();
        });

        panelBotones.add(btnCargar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnPosiblesCelda);
        panelBotones.add(btnPosiblesFila);
        panelBotones.add(btnPosiblesColumna);
        panelBotones.add(btnPosiblesBloque);
        panelBotones.add(btnResolver);
        panelBotones.setBorder(BorderFactory.createTitledBorder("Acciones"));

        panel.add(panelSeleccion, BorderLayout.NORTH);
        panel.add(panelBotones, BorderLayout.CENTER);
        return panel;
    }

    private void inicializarSelectores() {
        for (int i = 1; i <= 9; i++) {
            selectorFila.addItem(i);
            selectorColumna.addItem(i);
        }

        selectorFila.setSelectedIndex(0);
        selectorColumna.setSelectedIndex(0);

        selectorFila.addActionListener(e -> actualizarResaltadoSeleccion());
        selectorColumna.addActionListener(e -> actualizarResaltadoSeleccion());
    }

    private Coordenada obtenerSeleccionActual() {
        int fila = selectorFila.getSelectedIndex();
        int columna = selectorColumna.getSelectedIndex();
        return new Coordenada(fila, columna);
    }

    private void enfocarCeldaSeleccionada() {
        Coordenada coordenada = obtenerSeleccionActual();
        celdas[coordenada.getFila()][coordenada.getColumna()].requestFocusInWindow();
        actualizarResaltadoSeleccion();
    }

    private void actualizarResaltadoSeleccion() {
        Coordenada seleccion = obtenerSeleccionActual();
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                if (fila == seleccion.getFila() && columna == seleccion.getColumna()) {
                    celdas[fila][columna].setBackground(FONDO_CELDA_SELECCIONADA);
                } else if (tablero.esCeldaFija(fila, columna)) {
                    celdas[fila][columna].setBackground(FONDO_CELDA_FIJA);
                } else {
                    celdas[fila][columna].setBackground(FONDO_CELDA);
                }
            }
        }
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
        areaMensajes.setFont(new Font("Monospaced", Font.PLAIN, 12));
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
        Coordenada seleccion = obtenerSeleccionActual();
        for (int fila = 0; fila < 9; fila++) {
            for (int columna = 0; columna < 9; columna++) {
                int valor = datos[fila][columna];
                celdas[fila][columna].setText(valor == 0 ? "" : String.valueOf(valor));
                if (fila == seleccion.getFila() && columna == seleccion.getColumna()) {
                    celdas[fila][columna].setBackground(FONDO_CELDA_SELECCIONADA);
                } else {
                    celdas[fila][columna].setBackground(tablero.esCeldaFija(fila, columna) ? FONDO_CELDA_FIJA : FONDO_CELDA);
                }
            }
        }
    }

    private String formatearCandidatosCelda(Coordenada celda, List<Integer> candidatos) {
        StringBuilder sb = new StringBuilder();
        sb.append("CONSULTA DE CELDA\n");
        sb.append("----------------------------------------\n");
        sb.append("Celda seleccionada: ").append(celda).append("\n");
        sb.append("Candidatos: ").append(candidatos).append("\n");
        sb.append("----------------------------------------\n");
        return sb.toString();
    }

    private String formatearMapaCandidatos(String titulo, Map<Coordenada, List<Integer>> mapa) {
        StringBuilder sb = new StringBuilder();
        sb.append(titulo).append("\n");
        sb.append("----------------------------------------\n");

        List<Map.Entry<Coordenada, List<Integer>>> entradas = new ArrayList<>(mapa.entrySet());
        entradas.sort(Comparator
                .comparingInt((Map.Entry<Coordenada, List<Integer>> e) -> e.getKey().getFila())
                .thenComparingInt(e -> e.getKey().getColumna()));

        if (entradas.isEmpty()) {
            sb.append("No hay celdas vacías en esta unidad.\n");
        } else {
            for (Map.Entry<Coordenada, List<Integer>> entry : entradas) {
                sb.append("Celda ").append(entry.getKey())
                        .append(" -> ").append(entry.getValue())
                        .append("\n");
            }
        }

        sb.append("----------------------------------------\n");
        return sb.toString();
    }

    private String formatearPasoResolucion(PasoResolucion paso) {
        StringBuilder sb = new StringBuilder();
        sb.append("RESOLVER SIGUIENTE MOVIMIENTO\n");
        sb.append("----------------------------------------\n");

        if (!paso.isResuelto()) {
            sb.append("No se encontró movimiento con técnicas actuales.\n");
            sb.append("Sugerencia: implementar técnicas avanzadas (pares desnudos, pares ocultos, pointing pairs).\n");
            sb.append("----------------------------------------\n");
            return sb.toString();
        }

        sb.append("Método: ").append(paso.getMetodo()).append("\n");
        sb.append("Número colocado: ").append(paso.getNumero()).append("\n");
        sb.append("Celda destino: ").append(new Coordenada(paso.getFila(), paso.getColumna())).append("\n");
        sb.append("Motivo: ").append(paso.getExplicacion()).append("\n");
        sb.append("Celdas analizadas: ").append(paso.getCeldasAnalizadas()).append("\n");
        sb.append("----------------------------------------\n");
        return sb.toString();
    }

    private void escribirMensaje(String mensaje) {
        areaMensajes.setText(mensaje);
        areaMensajes.setCaretPosition(0);
    }

    public static void iniciar() {
        SwingUtilities.invokeLater(Vista::new);
    }

    private void escribirMensaje(String mensaje) {
        areaMensajes.setText(mensaje);
    }

    public static void iniciar() {
        SwingUtilities.invokeLater(Vista::new);
    }
}
