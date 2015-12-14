package modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import controlador.RNA;
import controlador.Ser;

public class Cementerio extends Thread {
	private final int PERIODO_TRABAJO = 30000;// cada 30s verifica si puede hacer recolecta de cuerpos
	private final String RUTA = "rnas/";

	private ArrayList<Ser> principal, secundario;
	private int promedio_actual, conteo, generacion, mejor, total;
	private boolean principal_ocupado, trbajar;
	private Thread proceso;
	private DefaultListModel<String> registro;
	private ArrayList<String> ficheros;

	public Cementerio() {
		// TODO Auto-generated constructor stub
		this.principal = new ArrayList<Ser>();
		this.secundario = new ArrayList<Ser>();
		this.promedio_actual = 0;
		this.principal_ocupado = false;
		this.proceso = new Thread(this);
		this.trbajar = true;
		this.generacion = 0;
		this.ficheros = new ArrayList<String>();
		proceso.start();
	}

	/**
	 * Listar del directorio de archivos todos los que tienen informacion de cerebros
	 */
	public void listarSeres() {
		if (registro == null)
			return;
		String ruta = RUTA;
		File dir = new File(ruta);
		String[] fichs = dir.list();
		if (fichs == null) {
			registro.addElement("No existen ficheros");
		} else {
			for (int i = 0; i < fichs.length; i++) {
				if (fichs[i].contains(".rna"))
					ficheros.add(RUTA + fichs[i]);
				else
					registro.addElement("No es fichero :" + fichs[i]);
			}
		}
	}

	public void addMejor(Ser nuevo) {
		if (principal_ocupado) {
			secundario.add(nuevo);
		} else
			principal.add(nuevo);
	}

	@Override
	public void run() {
		while (trbajar) {
			// System.out.println("Esperando un buen tamano de seres " + (principal.size() + secundario.size()));
			while (principal.size() + secundario.size() < 100)
				try {
					Thread.sleep(PERIODO_TRABAJO);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			// System.out.println("A trabajar " + (principal.size() + secundario.size()));
			conteo = 0;
			if (principal_ocupado) {
				principal_ocupado = false;
				trabajar(secundario);
				secundario = new ArrayList<Ser>();
			} else {
				principal_ocupado = true;
				trabajar(principal);
				principal = new ArrayList<Ser>();
			}
		}
	}

	/**
	 * 
	 * @param lista
	 */
	private void trabajar(ArrayList<Ser> lista) {
		if (lista.size() == 0)
			return;
		borrarPrevios();
		mejor = 0;
		total = lista.size();
		for (Ser ser : lista) {
			promedio_actual += ser.getPuntos();
			if (ser.getPuntos() > mejor) {
				mejor = ser.getPuntos();
			}
		}
		promedio_actual = promedio_actual / lista.size();
		for (Ser ser : lista) {
			if (ser.getPuntos() > promedio_actual * 9 / 5) {
				guardarCopia(ser);
			}
		}
		informar();
		generacion++;
	}

	/**
	 * Crea el fichero con la ifno del cerebro
	 * 
	 * @param copia
	 */
	private void guardarCopia(Ser guardar) {
		String fichero = "Gen" + generacion + "Pt" + guardar.getPuntos() + "Id" + conteo + "adn" + guardar.getCerebro().adn() + ".rna";
		File folder = new File(RUTA);
		if (!folder.exists()) {
			folder.mkdirs();
		} else if (folder.isFile()) {
			System.out.println(RUTA + " era un archivo");
			registro.addElement(RUTA + " era un archivo");
			folder.mkdirs();
		}
		if (!folder.isDirectory()) {
			System.out.println(RUTA + " No es un directorio?");
			registro.addElement(RUTA + " No es un directorio?");
			return;
		}

		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(RUTA + fichero));
			oos.writeObject(guardar.getCerebro());
			oos.close();
			conteo++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setRegistro(DefaultListModel<String> lista) {
		this.registro = lista;
	}

	private void informar() {
		if (registro == null)
			return;
		String texto = "Generacion :" + generacion;
		texto += ", Total :" + total;
		texto += ", Promedio :" + promedio_actual;
		texto += ", Mejor :" + mejor;
		texto += ", Archivos :" + conteo;
		registro.add(0, texto);
	}

	/**
	 * Tomar los archivos leidos de la ruta y cargarlos sobre cerebros
	 * 
	 * @return
	 */
	public ArrayList<Ser> cargarSeres() {
		// TODO Auto-generated method stub
		listarSeres();
		ArrayList<Ser> seres = new ArrayList<Ser>();
		for (String fich : ficheros) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(fich));
				Object aux = ois.readObject();
				RNA cerebro = (RNA) aux;
				Ser nuevo = new Ser(0, 0, cerebro);
				seres.add(nuevo);
				ois.close();
				// File archivo = new File(fich);
				// archivo.delete();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		registro.addElement("Seres creados a partir de ficheros leidos " + ficheros.size());
		ficheros.clear();
		return seres;
	}

	private void borrarPrevios() {
		listarSeres();
		for (String fich : ficheros) {
			File archivo = new File(fich);
			archivo.delete();
		}
	}
}
