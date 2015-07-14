package vista;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import modelo.Cementerio;
import controlador.Mundo;
import entrada.EntradaUsuario;
/**
 * Clase que une el Mundo, Hilos, Entrada desde usuario y es lanzado desde el Main del programa (Menu.java)
 * @author Oz
 *
 */
public class Simulador extends Canvas implements Runnable {

	private static final long serialVersionUID = 7498342999599172958L;
	public static final int ANCHO = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int ALTO = Toolkit.getDefaultToolkit().getScreenSize().height - 40;

	private Mundo juego;
	private Camara camara;
	private Thread hilo;
	private EntradaUsuario entrada;
	private boolean ejecutar, mostrar;
	private BufferStrategy strategy;
	private JFrame frame;

	/**
	 * Constructor, recibe parametro que determina si se trata de un HILO para visualizar o un HILO para simular rapido.
	 * @param rapido
	 */
	Simulador(boolean rapido) {
		if (rapido) {
			ejecutar = false;
			mostrar = false;
			juego = new Mundo(null);
			frame = null;
		} else {
			ejecutar = false;
			mostrar = true;
			camara = new Camara(ANCHO, ALTO);
			juego = new Mundo(camara);
			entrada = new EntradaUsuario();
			frame = null;
			addKeyListener(entrada);
			addFocusListener(entrada);
			addMouseListener(entrada);
			addMouseMotionListener(entrada);
		}
	}

	public void setCementerio(Cementerio c) {
		juego.setCementerio(c);
	}

	public void parar() {
		if (!ejecutar)
			return;
		ejecutar = false;
		try {
			hilo.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void iniciar() {
		if (ejecutar)
			return;
		ejecutar = true;
		hilo = new Thread(this);
		hilo.start();
	}

	@Override
	public void run() {
		if (mostrar) {
			System.out.println("Visual corriendo" + this.getName());
			while (ejecutar) {
				juego.actualizar();
				actualizarControles();
				dibujar();
				// 50 FPS-20 millis
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Visual FIN");
		} else {
			System.out.println("Rapido corriendo" + this.getName());
			while (ejecutar)
				juego.actualizar();
			System.out.println("Rapido FIN");
		}
	}

	private void actualizarControles() {
		// Controles
		if (entrada.tecla[KeyEvent.VK_W])
			camara.mover(0, -15);
		if (entrada.tecla[KeyEvent.VK_A])
			camara.mover(-15, 0);
		if (entrada.tecla[KeyEvent.VK_D])
			camara.mover(+15, 0);
		if (entrada.tecla[KeyEvent.VK_S])
			camara.mover(0, +15);
		if (entrada.tecla[KeyEvent.VK_Q]) {
			camara.seguir(juego.anteriorElegido());
		}
		if (entrada.tecla[KeyEvent.VK_E]) {
			camara.seguir(juego.siguienteElegido());
		}
		if (entrada.tecla[KeyEvent.VK_ESCAPE]) {
			camara.seguir(null);
		}
		camara.actualizar();
	}

	private void dibujar() {
		// TODO Auto-generated method stub
		Graphics2D g = null;
		try {
			g = (Graphics2D) strategy.getDrawGraphics();
			g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
			g.translate(-camara.getPosx(), -camara.getPosy());
			juego.dibujar(g);
		} finally {
			if (g != null)
				g.dispose();
		}
		strategy.show();
	}

	/*
	 * INICIADOR DEL PROGRAMA
	 */
	public void mostrar() {
		// TODO Auto-generated method stub
		mostrar = true;
		if (frame != null) {
			// frame.setVisible(true);
			return;
		}
		Simulador mip = this;
		frame = new JFrame();
		frame.add(mip);
		frame.pack();
		// frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Evolucion");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(ANCHO, ALTO);
		frame.setVisible(true);
		frame.setLocation(0, 0);
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		iniciar();
	}

	public void ocultar() {
		// TODO Auto-generated method stub
		mostrar = false;
		if (frame == null)
			return;
	}

	public Mundo getJuego() {
		return this.juego;
	}

	public boolean isMostrando() {
		return mostrar;
	}

	public void setRapido(Mundo rapid) {
		// TODO Auto-generated method stub
		juego.setRapido(rapid);
	}
}
