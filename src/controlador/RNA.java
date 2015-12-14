package controlador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class RNA implements Serializable {
	public static final int MAX_FUNCIONES = 4, FSIG = 0, FTANH = 1, FSIN = 2, FSIG2 = 3;
	private final float DELTA_INI = 0.1f, PASO_POSTIVO = 1.2f, MAX_PASO = 50, PASO_NEGATIVO = 0.5f, MIN_PASO = 0.000001f;
	/**
     * 
     */
	private static final long serialVersionUID = 2964196787564384140L;
	private ArrayList<RNA.Neurona> neuronas;
	private ArrayList<float[]> entradas_conocidas, salidas_conocidas;
	private int entradas, salidas, ocultas, rango_azar, funcion;
	private float coef, tol;

	public RNA(int cant_entradas, int cant_salidas) {
		neuronas = new ArrayList<RNA.Neurona>();
		this.entradas = cant_entradas;
		this.salidas = cant_salidas;
		this.ocultas = (int) (Math.random() * (cant_entradas + cant_salidas) / 2);
		this.rango_azar = (int) (Math.random() * 10);
		this.funcion = (int) (Math.random() * MAX_FUNCIONES);// FSIG;
		// aparte
		this.coef = 0.5f;
		this.tol = 0.1f;
		this.entradas_conocidas = new ArrayList<float[]>();
		this.salidas_conocidas = new ArrayList<float[]>();
		crearNeuronas();
		crearConexiones();
	}

	/**
	 * Crear un clon de padre, retorna nuevo Objeto Independiente
	 * 
	 * @param padre
	 */
	public RNA(RNA padre) {
		neuronas = new ArrayList<RNA.Neurona>();
		this.entradas = padre.entradas;
		this.salidas = padre.salidas;
		this.ocultas = padre.ocultas;
		this.rango_azar = padre.rango_azar;
		this.funcion = padre.funcion;
		for (Neurona neu_padre : padre.neuronas) {
			Neurona n = new Neurona(neu_padre.id, neu_padre.tipo);
			neuronas.add(n);
		}
		for (Neurona neu_padre : padre.neuronas) {
			Neurona n = neuronas.get(neu_padre.id);
			for (controlador.RNA.Neurona.Conexion con_padre : neu_padre.conexiones) {
				n.addConexion(neuronas.get(con_padre.destino.id), con_padre.peso);
			}
		}
	}

	/**
	 * Se crea hijo con parte del fenotipo del padre y de la madre
	 * 
	 * @param padre
	 * @param madre
	 */
	public RNA(RNA padre, RNA madre) {

		neuronas = new ArrayList<RNA.Neurona>();
		this.rango_azar = (padre.rango_azar + madre.rango_azar) / 2;
		this.entradas = padre.entradas;
		this.salidas = padre.salidas;
		if (Math.random() < 0.5) {
			this.funcion = padre.funcion;
			this.ocultas = padre.ocultas;
			for (Neurona neu_padre : padre.neuronas) {
				Neurona n = new Neurona(neu_padre.id, neu_padre.tipo);
				neuronas.add(n);
			}
		} else {
			this.funcion = madre.funcion;
			this.ocultas = madre.ocultas;
			for (Neurona neu_madre : madre.neuronas) {
				Neurona n = new Neurona(neu_madre.id, neu_madre.tipo);
				neuronas.add(n);
			}
		}
		Iterator<Neurona> iter_padre = padre.neuronas.iterator();
		Iterator<Neurona> iter_madre = madre.neuronas.iterator();
		int id = 0;
		while (iter_madre.hasNext() & iter_padre.hasNext() & id < neuronas.size()) {
			Neurona neu_padre = iter_padre.next();
			Neurona neu_madre = iter_madre.next();
			Neurona n = neuronas.get(id++);
			Iterator<RNA.Neurona.Conexion> iter_con_padre = neu_padre.conexiones.iterator();
			Iterator<RNA.Neurona.Conexion> iter_con_madre = neu_madre.conexiones.iterator();
			while (iter_con_padre.hasNext() & iter_con_madre.hasNext()) {
				controlador.RNA.Neurona.Conexion con_padre = iter_con_padre.next();
				controlador.RNA.Neurona.Conexion con_madre = iter_con_madre.next();
				if (Math.random() < 0.5) {
					if (con_padre.destino.id < neuronas.size()) {
						if (!n.addConexion(neuronas.get(con_padre.destino.id), con_padre.peso))
							if (con_madre.destino.id < neuronas.size())
								n.addConexion(neuronas.get(con_madre.destino.id), con_madre.peso);
					} else if (con_madre.destino.id < neuronas.size())
						n.addConexion(neuronas.get(con_madre.destino.id), con_madre.peso);
				} else {
					if (con_madre.destino.id < neuronas.size()) {
						if (!n.addConexion(neuronas.get(con_madre.destino.id), con_madre.peso))
							if (con_padre.destino.id < neuronas.size())
								n.addConexion(neuronas.get(con_padre.destino.id), con_padre.peso);
					} else if (con_padre.destino.id < neuronas.size())
						n.addConexion(neuronas.get(con_padre.destino.id), con_padre.peso);
				}
			}
			while (iter_con_padre.hasNext()) {
				controlador.RNA.Neurona.Conexion con_padre = iter_con_padre.next();
				if (Math.random() < 0.5)
					if (con_padre.destino.id < neuronas.size())
						n.addConexion(neuronas.get(con_padre.destino.id), con_padre.peso);
			}
			while (iter_con_madre.hasNext()) {
				controlador.RNA.Neurona.Conexion con_madre = iter_con_madre.next();
				if (Math.random() < 0.5)
					if (con_madre.destino.id < neuronas.size())
						n.addConexion(neuronas.get(con_madre.destino.id), con_madre.peso);
			}
		}
		while (iter_padre.hasNext() & id < neuronas.size()) {
			Neurona neu_padre = iter_padre.next();
			Neurona n = neuronas.get(id++);
			Iterator<RNA.Neurona.Conexion> iter_con_padre = neu_padre.conexiones.iterator();

			while (iter_con_padre.hasNext()) {
				controlador.RNA.Neurona.Conexion con_padre = iter_con_padre.next();
				if (Math.random() < 0.5)
					if (con_padre.destino.id < neuronas.size())
						n.addConexion(neuronas.get(con_padre.destino.id), con_padre.peso);
			}
		}
		while (iter_madre.hasNext() & id < neuronas.size()) {
			Neurona neu_madre = iter_madre.next();
			Neurona n = neuronas.get(id++);
			Iterator<RNA.Neurona.Conexion> iter_con_madre = neu_madre.conexiones.iterator();

			while (iter_con_madre.hasNext()) {
				controlador.RNA.Neurona.Conexion con_madre = iter_con_madre.next();
				if (Math.random() < 0.5)
					if (con_madre.destino.id < neuronas.size())
						n.addConexion(neuronas.get(con_madre.destino.id), con_madre.peso);
			}
		}
		verificarConexiones();
	}

	private void crearNeuronas() {
		for (int i = 0; i < this.entradas; i++) {
			Neurona n = new Neurona(neuronas.size(), Neurona.ENTRADA);
			this.neuronas.add(n);
		}
		for (int i = 0; i < this.ocultas; i++) {
			Neurona n = new Neurona(neuronas.size(), Neurona.OCULTA);
			this.neuronas.add(n);
		}
		for (int i = 0; i < this.salidas; i++) {
			Neurona n = new Neurona(neuronas.size(), Neurona.SALIDA);
			this.neuronas.add(n);
		}
	}

	private void crearConexiones() {
		/*
		 * Se crean las conexiones iniciales...
		 */
		Iterator<Neurona> iterador = this.neuronas.iterator();
		while (iterador.hasNext()) {
			Neurona n = iterador.next();
			crearConexion(n);
			// System.out.println("");
		}
		/*
		 * Verificamos que todas las neuronas queden conectadas...
		 */
		verificarConexiones();
	}

	private void crearConexion(Neurona n) {
		Random ran = new Random();
		if (n.tipo != Neurona.SALIDA)
			do {
				int id = 0;
				switch (n.tipo) {
				case Neurona.ENTRADA:
					id = this.entradas + (int) (Math.random() * (this.ocultas + this.salidas));
					break;
				case Neurona.OCULTA:
					id = n.id + 1 + (int) (Math.random() * (this.salidas + this.ocultas + this.entradas - n.id - 1));
					break;
				default:
					id = -1;
					break;
				}
				if (id != -1) {
					n.addConexion(neuronas.get(id));
					// System.out.print(" ->" + id);
				}
				// System.out.println("Cons +"+n.conexiones.size());
			} while (ran.nextInt(10) > 2 && n.id + n.conexiones.size() < neuronas.size() - 1);
	}

	private void verificarConexiones() {
		int id = 0;
		Iterator<Neurona> iterador = this.neuronas.iterator();
		ArrayList<Integer> conectadas = new ArrayList<Integer>();
		while (iterador.hasNext()) {
			Neurona n = iterador.next();
			if (n.conexiones.size() == 0) {
				crearConexion(n);
			}
			for (controlador.RNA.Neurona.Conexion con : n.conexiones) {
				if (!conectadas.contains(con.destino.id))
					conectadas.add(con.destino.id);
			}

		}
		while (conectadas.size() < neuronas.size() - this.entradas) {
			// System.out.println("falta por conectar neuronas " + conectadas + "->" +
			// (neuronas.size() - this.entradas));
			for (Neurona n : neuronas) {
				if (n.tipo != Neurona.ENTRADA) {
					if (!conectadas.contains(n.id)) {
						id = (int) (Math.random() * n.id);
						neuronas.get(id).addConexion(n);
						conectadas.add(n.id);
						// System.out.println("Neurona " + id + " : ->" + n.id);
					}
				}
			}
		}
	}

	/**
	 * Tendremos 6 tipos de mutaciones 0:Cambio en el peso de una conexion 1:Cambio en el destino de una conexion 2:Se elimina una conexion 3:Se agrega una conexion 4:Se agrega una neurona 5:Se elimina una neurona
	 */
	public void mutar() {
		int tipo = (int) (Math.random() * 10);
		int limite = 0;// evita ciclos infinitos
		int n = 0;// id neurona al azar
		int c = 0;// conexion al azar
		// System.out.println("Inicio Mutacion tipo " + tipo);
		switch (tipo) {
		case 0:// Cambio peso de conexion
		case 6:
			n = (int) (Math.random() * (neuronas.size() - this.salidas));
			c = neuronas.get(n).conexiones.size();
			if (c == 0) {
				System.out.println("Neurona tipo " + neuronas.get(n).tipo + " Sin conexiones");
				return;
			}
			c = (int) (Math.random() * c);
			neuronas.get(n).conexiones.get(c).peso = neuronas.get(n).getPesoAzar();
			break;
		case 1:// Cambio de neurona de conexion
		case 7:
			n = (int) (Math.random() * (neuronas.size() - this.salidas));
			c = neuronas.get(n).conexiones.size();
			if (c == 0) {
				System.out.println("Neurona tipo " + neuronas.get(n).tipo + " Sin conexiones");
				return;
			}
			c = (int) (Math.random() * c);
			// al azar ya elegimos cual conexion cambiaremos
			int id_ant = neuronas.get(n).conexiones.get(c).destino.id;
			int azar_neurona = 0;
			Neurona n_cambio;
			do {
				do {
					// buscamos una nueva neurona que no sea la misma anterior
					azar_neurona = n + 1 + (int) (Math.random() * (this.neuronas.size() - n - 1));
					n_cambio = this.neuronas.get(azar_neurona);
				} while (n_cambio.tipo == Neurona.ENTRADA | azar_neurona == id_ant);
				// ponemos la nueva
				limite++;
			} while (!neuronas.get(n).addConexion(n_cambio) & limite < 10);
			// quitamos la anterior;
			if (limite < 100)
				neuronas.get(n).conexiones.remove(c);
			break;
		case 2:// se elimina una conexion
		case 8:
			do {
				// buscamos una neurona que tenga mas de una conexion
				n = (int) (Math.random() * (neuronas.size() - this.salidas));
				limite++;
			} while (neuronas.get(n).conexiones.size() <= 1 & limite < 10);
			if (limite >= 10) {// fallo la mutacion
				mutar();
			} else {
				neuronas.get(n).conexiones.remove(0);
				// verificamos las conexiones restantes por si acaso eliminamos una
				// conexion unica a una neurona
				verificarConexiones();
			}
			break;
		case 3:// se agrega una conexion
		case 9:
			n = (int) (Math.random() * (neuronas.size() - this.salidas));
			Neurona n_nueva;
			do {
				c = n + 1 + (int) (Math.random() * (this.neuronas.size() - n - 1));
				n_nueva = this.neuronas.get(c);
				limite++;
			} while (!neuronas.get(n).addConexion(n_nueva) & limite < 10);
			if (limite >= 10) {// fallo la mutacion
				mutar();
			}
			break;
		case 4:// Se agrega una neurona en la capa oculta
			if (ocultas > entradas + salidas)
				return;
			n = entradas + (int) (Math.random() * ocultas);
			Neurona nueva = new Neurona(n, Neurona.OCULTA);
			neuronas.add(n, nueva);
			this.ocultas++;
			// ahora debemos actualizar todos los numeros de las neuronas...
			c = 0;
			for (Neurona una_n : this.neuronas) {
				una_n.id = c++;
			}
			// ahora creamos las nuevas conexiones para esta neurona
			do {
				c = n + 1 + (int) (Math.random() * (this.neuronas.size() - n - 1));
				nueva.addConexion(neuronas.get(c));
			} while (Math.random() < 0.5 | nueva.conexiones.size() == 0);
			// verificamos para que se creen nuevas conexiones hacia esta
			// neurona
			verificarConexiones();
			break;
		case 5:// Se elimina una neurona en la capa oculta
			if (ocultas <= 1) {// fallo la mutacion
				mutar();
				return;
			} else {
				n = entradas + (int) (Math.random() * ocultas);
				Neurona eliminame = this.neuronas.get(n);
				neuronas.remove(n);
				this.ocultas--;
				// ahora debemos actualizar todos los numeros de las neuronas...
				c = 0;
				for (Neurona una_n : this.neuronas) {
					una_n.id = c++;
				}
				for (Neurona una_n : this.neuronas) {
					for (RNA.Neurona.Conexion conex : una_n.conexiones) {
						if (conex.destino.id == eliminame.id) {
							una_n.conexiones.remove(conex);
							while (una_n.conexiones.size() == 0) {
								n = una_n.id + 1 + (int) (Math.random() * (this.neuronas.size() - una_n.id - 1));
								una_n.addConexion(neuronas.get(n));
							}
							break;
						}
					}
				}
				// verificamos para que se creen nuevas conexiones hacia las
				// neuronas que estaba conectadas con esta neurona eliminada
				verificarConexiones();
			}
			break;
		default:
			System.out.println("Un nuevo tipo de mutacion?");
			break;
		}
	}

	/**
	 * Propagar la entrada para obtener la salida
	 * 
	 * @param sensores
	 * @return
	 */
	public float[] reconocer(float[] sensores) {
		/* * Controles previos */
		if (sensores.length != this.entradas) {
			System.out.println("la cantidad de entradas no coincide con salida");
			return null;
		}
		/* * Limpiamos los valores previos */
		for (Neurona n : this.neuronas) {
			n.suma = 0;
		}
		/* * Movemos los valores a las neuronas de entrada */
		int i = 0;
		for (Neurona n : this.neuronas) {
			if (n.tipo == Neurona.ENTRADA) {
				n.suma = sensores[i++];
				if (n.suma > 1) {
					n.suma = 1;
				}
				if (n.suma < 0) {
					n.suma = 0;
				}
			} else
				break;
		}
		/* * Propagamos hasta las neuronas de salida */
		Iterator<Neurona> iter_n = this.neuronas.iterator();
		while (iter_n.hasNext()) {
			Neurona n = iter_n.next();
			if (n.tipo != Neurona.ENTRADA)
				n.suma = activacion(n.suma + n.bias_peso * n.bias_val);
			if (n.tipo != Neurona.SALIDA)
				for (controlador.RNA.Neurona.Conexion con : n.conexiones) {
					con.destino.suma += con.peso * n.suma;
				}
		}
		/* * Tomamos los valores que quedaron en la salida */
		float[] actuadores = new float[this.salidas];
		iter_n = this.neuronas.iterator();
		i = 0;
		while (iter_n.hasNext()) {
			Neurona n = iter_n.next();
			if (n.tipo == Neurona.SALIDA)
				actuadores[i++] = (n.suma);
		}
		return actuadores;
	}

	public float retropropagarElastica(float[] ent, float[] sal, boolean reiniciar) {
		/*
		 * Algoritmo de rertopropagacion elastica
		 */
		if (ent.length != this.entradas) {
			System.err.println("Para aprender las entradas no coinciden");
			return 0;
		}
		if (sal.length != this.salidas) {
			System.err.println("Para aprender las salidas no coinciden");
			return 0;
		}
		/*
		 * Limpiamos los valores previos de error
		 */
		float valores = 0;
		for (Neurona n : this.neuronas) {
			n.error = 0;
			valores += n.suma;
		}
		/*
		 * Para alimentar las neuronas de salida
		 */
		if (valores == 0 || reiniciar) {
			float[] entrada = new float[ent.length];
			for (int i = 0; i < ent.length; i++) {
				entrada[i] = ent[i];
			}
			reconocer(entrada);
		}
		/*
		 * Comprobar que no sea necesario aprender...
		 */
		int i = 0;
		boolean ok = true;
		for (Neurona n : neuronas) {
			if (n.tipo == Neurona.SALIDA) {
				if (sal[i] < 0 | sal[i] > 1) {
					System.err.println("Para aprender las salidas no son 0 o 1");
					return 0;
				}

				if (Math.abs(n.suma - sal[i++]) > tol) {
					ok = false;
					break;
				}
			}
		}
		if (ok) {
			float error = 0;
			i = 0;
			for (Neurona n : neuronas) {
				if (n.tipo == Neurona.SALIDA) {
					error += Math.abs(n.suma - sal[i++]);
				}
			}
			error /= salidas;
			return error;
		}

		i = 0;
		for (int j = neuronas.size() - 1; j >= 0; j--) {
			Neurona n = neuronas.get(j);
			/*
			 * Obteniendo el error en la salida
			 */
			if (reiniciar) {
				n.bias_delta = DELTA_INI;
				n.bias_gradiente = 0;
			}
			if (n.tipo == Neurona.SALIDA) {
				n.error += n.suma * (1 - n.suma) * (n.suma - sal[i++]);
				for (Neurona atras : neuronas) {
					for (Neurona.Conexion con : atras.conexiones) {
						if (con.destino.id == n.id) {
							atras.error += n.error * con.peso;
							if (reiniciar) {
								con.delta = DELTA_INI;
								con.gradiente_ant = 0;
							}
							break;
						}
					}
				}
			}
			/*
			 * Propagamos el error hasta las neuronas ocultas y entrada
			 */
			else {
				n.error = n.suma * (1 - n.suma) * n.error;
				for (Neurona atras : neuronas) {
					for (Neurona.Conexion con : atras.conexiones) {
						if (con.destino.id == n.id) {
							atras.error += n.error * con.peso;
							if (reiniciar) {
								con.delta = DELTA_INI;
								con.gradiente_ant = 0;
							}
						}
					}
				}
			}
		}
		/*
		 * Ahora se actualizan los pesos
		 */
		Iterator<Neurona> iter_n = this.neuronas.iterator();
		while (iter_n.hasNext()) {
			Neurona n = iter_n.next();
			/*
			 * Implementacion del algoritmo de propagacion elástica
			 */
			for (Neurona.Conexion con : n.conexiones) {
				// con.peso = con.peso - coef*n.suma*con.destino.error;
				float cambio_pesos = 0;
				float gradiente = n.suma * con.destino.error;
				float cambio_signo = Math.signum(gradiente * con.gradiente_ant);

				if (cambio_signo > 0) {// no cambio de signo
					con.delta = min(con.delta * PASO_POSTIVO, MAX_PASO);
					cambio_pesos = -Math.signum(gradiente) * con.delta;
					con.gradiente_ant = gradiente;
				} else if (cambio_signo < 0) {// si cambio de signo
					con.delta = max(con.delta * PASO_NEGATIVO, MIN_PASO);
					con.gradiente_ant = 0;
				} else {// algun gradiente es 0
					cambio_pesos = -Math.signum(gradiente) * con.delta;
					con.gradiente_ant = gradiente;
				}

				con.peso += cambio_pesos;

			}

			/*
			 * Actualizamos el Bias
			 */
			float cambio_pesos = 0;
			float gradiente = n.bias_val * n.error;
			float cambio_signo = Math.signum(gradiente * n.bias_gradiente);

			if (cambio_signo > 0) {// no cambio de signo
				n.bias_delta = min(n.bias_delta * PASO_POSTIVO, MAX_PASO);
				cambio_pesos = -Math.signum(gradiente) * n.bias_delta;
				n.bias_gradiente = gradiente;
			} else if (cambio_signo < 0) {// si cambio de signo
				n.bias_delta = max(n.bias_delta * PASO_NEGATIVO, MIN_PASO);
				n.bias_delta = 0;
			} else {// algun gradiente es 0
				cambio_pesos = -Math.signum(gradiente) * n.bias_delta;
				n.bias_delta = gradiente;
			}

			n.bias_peso += cambio_pesos;
		}
		/*
		 * Obtener el error resultante
		 */
		float[] entrada = new float[ent.length];
		for (int idx = 0; idx < ent.length; idx++) {
			entrada[idx] = ent[idx];
		}
		reconocer(entrada);
		float error = 0;
		i = 0;
		for (Neurona n : neuronas) {
			if (n.tipo == Neurona.SALIDA) {
				error += Math.abs(n.suma - sal[i++]);
			}
		}
		error /= salidas;
		error *= 100; // para dar un porcentaje
		return error;
	}

	public boolean aprender(float ent[], float sal[]) {
		boolean ok = false;
		int idx = 0;
		for (float[] item : entradas_conocidas) {
			int i = 0;
			ok = true;
			for (float f : item) {
				if (f != ent[i++]) {
					ok = false;
					break;
				}
			}
			if (ok)
				break;
			idx++;
		}
		if (!ok) {
			// No conocemos ese set de entrenamiento, lo agregamos
			entradas_conocidas.add(ent);
			salidas_conocidas.add(sal);
		} else {
			// ya conocemos las entradas, actualizamos las salidas.
			salidas_conocidas.set(idx, sal);
		}
		/****
		 * Empezamos el ciclo de entrenamiento
		 ****/
		float error = 0;
		int contador = 0, iteraciones = 0;

		for (int i = 0; i < entradas_conocidas.size(); i++) {
			float[] entradas = entradas_conocidas.get(i);
			float[] salidas = salidas_conocidas.get(i);
			contador = 0;
			error = 0;
			do {
				error = retropropagarElastica(entradas, salidas, contador == 0);
				contador++;
			} while (error > tol && contador < 20000);
			if (contador > 1)
				i = -1;
			iteraciones += contador;
			if (iteraciones > 20000 || contador >= 20000) {
				System.out.println("Iter : " + iteraciones);
				return false;
			}
		}
		System.out.println("Iter : " + iteraciones);
		return true;
	}

	public float[] valoresSalida() {
		float[] actuadores = new float[this.salidas];
		Iterator<Neurona> iter_n = this.neuronas.iterator();
		iter_n = this.neuronas.iterator();
		int i = 0;
		while (iter_n.hasNext()) {
			Neurona n = iter_n.next();
			if (n.tipo == Neurona.SALIDA)
				actuadores[i++] = (n.suma);
		}
		return actuadores;
	}

	private float activacion(float x) {
		switch (funcion) {
		case 0:// Sigmoide
			return (float) (1 / (1 + Math.pow(Math.E, -x)));
		case 1:// Tangh
			return (float) (Math.tanh(x));
		case 2:// Seno
			return (float) (Math.sin(x));
		case 3:// Sigmoide extendida
			return (float) (2 / (1 + Math.pow(Math.E, -x)) - 1);
		default:
			activacion(x - 1);
			break;
		}
		return 0;
	}

	public String toString() {
		String ret = "";
		ret = "E:" + entradas + " O:" + ocultas + " S:" + salidas + " C:" + coef + " F:" + funcion + " T:" + tol;
		ret += " >";
		float[] sal = valoresSalida();
		for (float f : sal) {
			ret += f + " ";
		}

		return ret;
	}

	public String adn() {
		return ocultas + "-" + rango_azar + "-" + funcion;
	}

	public void setCoef(float val) {
		this.coef = val;
	}

	public void setTolerancia(float val) {
		this.tol = val / 100.0f;
	}

	private float min(float a, float b) {
		if (a < b)
			return a;
		else
			return b;
	}

	private float max(float a, float b) {
		if (a > b)
			return a;
		else
			return b;
	}

	/**
	 * 
	 * @author Bestia2
	 *
	 */
	private class Neurona implements Serializable {
		private static final long serialVersionUID = -4477566009556865140L;
		private ArrayList<Neurona.Conexion> conexiones;
		private int id, tipo;
		public static final int ENTRADA = 0;
		public static final int OCULTA = 1;
		public static final int SALIDA = 2;
		private float suma, error, bias_peso, bias_gradiente, bias_delta, bias_val;

		Neurona(int numero, int tipo) {
			conexiones = new ArrayList<RNA.Neurona.Conexion>();
			this.id = numero;
			this.tipo = tipo;
			this.suma = 0;
			this.error = 0;
			this.bias_peso = getPesoAzar();
			this.bias_gradiente = 0;
			this.bias_delta = DELTA_INI;
			this.bias_val = 1.0f;
		}

		public boolean addConexion(Neurona n) {
			// TODO Auto-generated method stub
			float valor = getPesoAzar();
			return addConexion(n, valor);
		}

		private boolean addConexion(Neurona destino, float valor) {
			// TODO Auto-generated method stub
			if (id >= destino.id) {
				System.out.println("No se puede conectar" + id + "->" + destino.id);
				return false;
			}
			boolean ok = true;
			Conexion c = new Conexion(destino, valor);
			Iterator<Conexion> iter_co = conexiones.iterator();
			while (iter_co.hasNext()) {
				Conexion mic = iter_co.next();
				if (mic.destino.id == destino.id) {
					ok = false;
					break;
				}
			}
			if (ok)
				this.conexiones.add(c);
			return ok;
		}

		private float getPesoAzar() {
			if (Math.random() < 0.5)
				return (float) (Math.random() * rango_azar);
			else
				return (float) (-Math.random() * rango_azar);
		}

		/**
		 * 
		 * @author Bestia2
		 *
		 */
		private class Conexion implements Serializable {
			private static final long serialVersionUID = -329127179917404151L;
			private Neurona destino;
			private float peso, delta, gradiente_ant;

			Conexion(Neurona neurona, float valor) {
				this.destino = neurona;
				this.peso = valor;
				this.delta = DELTA_INI;
			}
		}
	}
}
