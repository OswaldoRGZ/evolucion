package vista;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import modelo.Cementerio;
import controlador.Ser;

public class Menu extends JFrame {

	/**
     * 
     */
	private static final long serialVersionUID = 3092820725324053647L;
	private JPanel contentPane;
	private DefaultListModel<String> lista;
	private Simulador visual, rapido;
	private Cementerio cementerio;
	private JTextField textAlto;
	private JTextField textAncho;
	private JTextField textPromClon;
	private JTextField textMaxSeres;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Menu frame = new Menu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Menu() {
		setTitle("Evolucion Celulas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 555, 354);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblConfiguracion = new JLabel("Configuracion");
		lblConfiguracion.setFont(new Font("Arial", Font.PLAIN, 12));
		lblConfiguracion.setBounds(10, 11, 146, 14);
		contentPane.add(lblConfiguracion);

		final JButton btnVerSimulacion = new JButton("Ver/Ocultar");
		btnVerSimulacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// verSimulacion();
			}
		});
		btnVerSimulacion.setBounds(177, 285, 114, 23);
		btnVerSimulacion.setEnabled(false);
		contentPane.add(btnVerSimulacion);

		final JButton btnAplicarParametros = new JButton("Aplicar parametros");
		btnAplicarParametros.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aplicarParametros();
			}
		});
		btnAplicarParametros.setEnabled(false);
		btnAplicarParametros.setBounds(10, 285, 146, 23);
		contentPane.add(btnAplicarParametros);

		final JButton btnCargar = new JButton("Cargar RNAs");
		btnCargar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cargarRedes();
			}
		});
		btnCargar.setEnabled(false);
		btnCargar.setBounds(301, 285, 123, 23);
		contentPane.add(btnCargar);

		final JButton btnSimularRapido = new JButton("Simular");
		btnSimularRapido.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				iniciarSimulacion();
				btnSimularRapido.setEnabled(false);
				btnVerSimulacion.setEnabled(true);
				btnAplicarParametros.setEnabled(true);
				btnCargar.setEnabled(true);
				aplicarParametros();
			}
		});
		btnSimularRapido.setBounds(443, 285, 86, 23);
		contentPane.add(btnSimularRapido);

		JLabel lblInformacion = new JLabel("Informacion");
		lblInformacion.setFont(new Font("Arial", Font.PLAIN, 12));
		lblInformacion.setBounds(10, 93, 180, 14);
		contentPane.add(lblInformacion);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 118, 519, 156);
		contentPane.add(scrollPane);

		JList<String> listHistoria = new JList<String>();
		scrollPane.setViewportView(listHistoria);
		lista = new DefaultListModel<String>();
		listHistoria.setModel(lista);

		JLabel lblAlto = new JLabel("Alto");
		lblAlto.setFont(new Font("Arial", Font.PLAIN, 12));
		lblAlto.setBounds(10, 43, 74, 14);
		contentPane.add(lblAlto);

		JLabel lblAncho = new JLabel("Ancho");
		lblAncho.setFont(new Font("Arial", Font.PLAIN, 12));
		lblAncho.setBounds(10, 68, 74, 14);
		contentPane.add(lblAncho);

		textAlto = new JTextField();
		textAlto.setText("2000");
		textAlto.setBounds(88, 38, 86, 20);
		contentPane.add(textAlto);
		textAlto.setColumns(10);

		textAncho = new JTextField();
		textAncho.setText("2000");
		textAncho.setBounds(88, 66, 86, 20);
		contentPane.add(textAncho);
		textAncho.setColumns(10);

		JLabel lblPromedioParaClonar = new JLabel("Promedio para clonar");
		lblPromedioParaClonar.setFont(new Font("Arial", Font.PLAIN, 12));
		lblPromedioParaClonar.setBounds(199, 41, 129, 14);
		contentPane.add(lblPromedioParaClonar);

		textPromClon = new JTextField();
		textPromClon.setText("5");
		textPromClon.setBounds(338, 37, 86, 20);
		contentPane.add(textPromClon);
		textPromClon.setColumns(10);

		textMaxSeres = new JTextField();
		textMaxSeres.setText("45");
		textMaxSeres.setColumns(10);
		textMaxSeres.setBounds(338, 66, 86, 20);
		contentPane.add(textMaxSeres);

		JLabel lblMaxSeresinfinito = new JLabel("Max Seres(0:infinito)");
		lblMaxSeresinfinito.setFont(new Font("Arial", Font.PLAIN, 12));
		lblMaxSeresinfinito.setBounds(199, 68, 123, 14);
		contentPane.add(lblMaxSeresinfinito);

		lista.addElement("Inicio todo...");

		visual = null;
		rapido = null;
		cementerio = new Cementerio();
	}

	private void iniciarSimulacion() {
		if (this.rapido != null)
			return;
		this.cementerio.setRegistro(lista);

		this.rapido = new Simulador(true);
		this.rapido.setCementerio(cementerio);
		this.rapido.iniciar();

		this.visual = new Simulador(false);
		this.visual.setRapido(this.rapido.getJuego());
		this.visual.mostrar();
	}

	private void aplicarParametros() {
		if (this.rapido != null) {
			this.rapido.getJuego().setValores(Integer.valueOf(textAlto.getText()), Integer.valueOf(textAncho.getText()), Integer.valueOf(textMaxSeres.getText()));
			this.visual.getJuego().setValores(Integer.valueOf(textAlto.getText()), Integer.valueOf(textAncho.getText()), Integer.valueOf(textMaxSeres.getText()));
		}
	}

	private void cargarRedes() {
		if (cementerio != null && this.rapido.getJuego() != null) {
			ArrayList<Ser> seres = cementerio.cargarSeres();
			this.rapido.getJuego().crearSeres(seres);
			this.visual.getJuego().crearSeres(seres);
		}
	}
}
