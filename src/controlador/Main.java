/**
 * Archivo principal, inicia la aplicacion en un hilo encolado
 */
package controlador;

import java.awt.EventQueue;

import vista.Menu;

/**
 * Inicio Aplicacion
 *
 * @author Oz
 */
public class Main {

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
}
