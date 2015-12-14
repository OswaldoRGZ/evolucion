package controlador;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import modelo.Cementerio;
import vista.ArquitecturaJuego;
import vista.Camara;
import vista.Simulador;
/*
 * Clase que controla la dinamica evolutiva
 */
public class Mundo implements ArquitecturaJuego {
	public int ANCHO = Simulador.ANCHO;
	public int ALTO = Simulador.ALTO;
	private int SERES_MIN = 9, SERES_MAX = 30;// valores iuniciasl

	private ArrayList<Ser> seres, cruza, externos;
	// private ArrayList<Point2D> cortes;
	private Camara camara;
	private int elegido, puntos_max, vivos;
	private Cementerio cement;
	private Mundo rapido;

	public Mundo(Camara cam) {
		this.camara = cam;
		if (this.camara != null)
			this.camara.setDimMundo(ANCHO, ALTO);
		seres = new ArrayList<Ser>();
		cruza = new ArrayList<Ser>();
		externos = new ArrayList<Ser>();
		/* * Creamos los seres iniciales */
		for (int i = 0; i < SERES_MIN; i++) {
			Ser s = new Ser(ANCHO, ALTO);
			seres.add(s);
		}
		this.elegido = 0;
		this.puntos_max = 0;
	}

	public void crearSeres(ArrayList<Ser> nuevos) {
		for (Ser nuevo : nuevos) {
			Ser agrega = new Ser(ANCHO, ALTO, nuevo.getCerebro());
			agrega.setTipo(nuevo.getRojo(), nuevo.getVerde(), nuevo.getAzul());
			externos.add(agrega);
		}
	}

	public void setValores(int alto, int ancho, int max_seres) {
		try {
			this.ALTO = alto;
			this.ANCHO = ancho;
			for (int i = seres.size() - 1; i >= 0; i--) {
				seres.get(i).alto_mundo = alto;
				seres.get(i).ancho_mundo = ancho;
			}
			SERES_MAX = max_seres;
			if (this.camara != null)
				this.camara.setDimensiones(ANCHO, ALTO);
		} catch (Exception e) {
			System.out.println("No se pudo actualizar valores");
		}
	}

	public Ser siguienteElegido() {
		elegido++;
		if (elegido == seres.size())
			elegido = 0;
		try {
			seres.get(elegido).setElegido(true);
			return seres.get(elegido);
		} catch (IndexOutOfBoundsException e) {
			elegido = 0;
			return null;
		}

	}

	public Ser anteriorElegido() {
		elegido--;
		if (elegido < 0)
			elegido = 0;
		try {
			seres.get(elegido).setElegido(true);
			return seres.get(elegido);
		} catch (IndexOutOfBoundsException e) {
			elegido = 0;
			return null;
		}
	}

	@Override
	public void dibujar(Graphics2D g) {
		// TODO Auto-generated method stub
		// g.clearRect(0, 0, ANCHO, ALTO);
		g.setColor(Color.WHITE);
		g.fillRect(-500, -500, ANCHO + 1000, ALTO + 1000);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, ANCHO, ALTO);
		g.setColor(Color.WHITE);
		g.drawString("Seres    : " + vivos + "[" + seres.size() + "]", camara.getPosx() + 60, camara.getPosy() + 10);
		g.drawString("Vienen   : " + rapido.getVivos(), camara.getPosx() + 60, camara.getPosy() + 20);
		// g.drawString("Mutados  : " + clones, camara.getPosx() + 60, camara.getPosy() + 20);
		// g.drawString("Cruzados : " + cruzados, camara.getPosx() + 60, camara.getPosy() + 30);
		g.drawString("Externos : " + externos.size(), camara.getPosx() + 60, camara.getPosy() + 40);
		g.drawString("Pts      : " + puntos_max, camara.getPosx() + 60, camara.getPosy() + 50);
		for (Ser s : seres) {
			if (camara.enPantalla(s))
				s.dibujar(g);
		}
		/*
		 * for (Point2D p: cortes) { g.drawOval((int)p.getX()-2,(int)p.getY()-2, 4, 4); }if(cortes.size() > 1000) cortes = new ArrayList<Point2D>();
		 */
	}

	private String getVivos() {
		// TODO Auto-generated method stub
		return this.vivos + " [" + this.seres.size() + "]";
	}

	/**
	 * Ciclo principal del simulador
	 */
	@Override
	public void actualizar() {
		int tmpvivos = 0;
		int[] especies = new int[3];// contiene la cantidad de vivos por cada especie
		colisiones();
		for (int i = seres.size() - 1; i >= 0; i--) {
			Ser s = seres.get(i);
			if (s.estaVivo()) {
				tmpvivos++;
				if (s.miTuro()) {
					// recojemos la infor que sus sensores detectan
					float[][] sens = sensores(s);
					// se la enviamos para que analice
					s.sensores(sens);
				}
				// recibimos sus ordenes
				s.actualizar();
				especies[s.getTipo()]++;
			}
		}
		/* * agregar seres creados externamente */
		for (int i = externos.size() - 1; i >= 0; i--) {
			seres.add(externos.get(i));
			externos.remove(i);
			tmpvivos++;
			if (tmpvivos > SERES_MAX)
				break;
		}
		/* * Miramos si existe un espacio para crear un hijo */
		if (tmpvivos < SERES_MAX) {
			if (rapido != null) {
				// soy el visualizador
				regenera_visualizador(especies);
			} else {
				// soy el rapido, envio al cementerio copia del mejor y repoblo
				regenera_simulador(especies);
			}
		}
		vivos = tmpvivos;
	}
	private void regenera_simulador(int[] especies) {
		Ser mejor = null;
		int max = 0;
		float prom = 0;
		ArrayList<Ser> nuevos = new ArrayList<Ser>();
		for (Ser ser : seres) {
			prom += ser.getPuntos();
			if (ser.getPuntos() > max) {// se tienen en cuenta los muertos tambien
				mejor = ser;
				max = ser.getPuntos();
			}
		}
		prom = prom / seres.size();
		if (mejor != null) {
			// vamos a crear copia exacta del mejor, INDEPENDIETNE
			Ser copiaMejor1 = new Ser(mejor.getCerebro());
			Ser copiaMejor2 = new Ser(mejor.getCerebro());
			// lo guardamos para historial y cruces, asi este vivo
			copiaMejor1.setPuntaje(mejor.getPuntos());
			copiaMejor2.setPuntaje(mejor.getPuntos());
			this.cement.addMejor(copiaMejor1);
			this.cruza.add(copiaMejor2);
			// crea un ser mutado del padre, pero con el mismo tipo, independiente
			Ser nuevo = new Ser(ANCHO, ALTO, mejor);
			nuevos.add(nuevo);
			// reiniciamos y guardamos los puntajes del mejor
			mejor.resetPuntos();
		}
		// Ahora mantener la minima cantidad de seres de cada especie
		for (int j = 0; j < especies.length; j++) {
			while (especies[j] < SERES_MAX / 3) {
				Ser extra = new Ser(ANCHO, ALTO);
				extra.setTipo(j);
				nuevos.add(extra);
				especies[j]++;
			}
		}
		// ahora agregar a los cruzados, los dos mejores
		int maxcru1 = 0;
		int maxcru2 = 0;
		int imaxcru1 = -1;
		int imaxcru2 = -1;
		for (int i = 0; i < cruza.size() - 1; i++) {
			if (cruza.get(i).getPuntos() > maxcru1) {
				maxcru2 = maxcru1;
				imaxcru2 = imaxcru1;
				maxcru1 = cruza.get(i).getPuntos();
				imaxcru1 = i;
			} else {
				if (cruza.get(i).getPuntos() > maxcru2) {
					maxcru2 = cruza.get(i).getPuntos();
					imaxcru2 = i;
				}
			}
		}
		if (imaxcru1 > -1 && imaxcru2 > -1) {
			Ser nuevo = new Ser(ANCHO, ALTO, cruza.get(imaxcru1), cruza.get(imaxcru2));
			cruza.get(imaxcru1).setPuntaje(cruza.get(imaxcru1).getPuntos() / 2);
			cruza.get(imaxcru2).setPuntaje(cruza.get(imaxcru2).getPuntos() / 2);
			nuevos.add(nuevo);
		}
		// agregarlos al mundo
		for (Ser ser : nuevos) {
			seres.add(ser);
		}
		// eliminar de memoria algunos de los peores muertos
		for (int i = seres.size() - 1; i >= 0; i--) {
			if (!seres.get(i).estaVivo() && seres.get(i).getPuntos() < prom)
				seres.remove(i);
		}
		// eliminar los cruzados con poco puntos
		for (int i = cruza.size() - 1; i >= 0; i--) {
			if (cruza.get(i).getPuntos() < prom) {
				cruza.remove(i);
			}

		}
	}

	private void regenera_visualizador(int[] especies) {
		// eliminar los cadaveres...
		while (seres.size() > SERES_MAX + 20) {
			for (Ser serele : seres) {
				if (!serele.estaVivo()) {
					seres.remove(serele);
					break;
				}
			}
		}
		// consultar el mejor del momento
		Ser ser = rapido.getMejor();
		if (ser != null) {
			puntos_max = ser.getPuntos();
			seres.add(ser);
			// copiar el mejor para las especies con menos seres
			for (int j = 0; j < especies.length; j++) {
				while (especies[j] < SERES_MAX / 3) {
					Ser extra = new Ser(ANCHO, ALTO, ser.getCerebro());
					extra.setTipo(j);
					seres.add(extra);
					especies[j]++;
				}
			}
		}
	}

	/**
	 * Colisiones de seres con otros seres
	 */
	private void colisiones() {
		// proemro cuando se cohcan los cuerpos, alejar
		float dirx, diry;
		for (int i = 0; i < seres.size() - 1; i++) {
			Ser a = seres.get(i);
			if (!a.estaVivo())
				continue;
			for (int j = i + 1; j < seres.size(); j++) {
				Ser b = seres.get(j);
				if (!b.estaVivo())
					continue;
				if (distancia(a, b) < a.RADIO + b.RADIO) {
					dirx = (a.getX() - b.getX()) / (a.RADIO);
					diry = (a.getY() - b.getY()) / (b.RADIO);
					a.setX(a.getX() + dirx);
					a.setY(a.getY() + diry);
					b.setX(b.getX() - dirx);
					b.setY(b.getY() - diry);
				}
			}
		}
		// punal contra otros
		for (int i = 0; i < seres.size(); i++) {
			Ser a = seres.get(i);
			if (!a.estaVivo() || a.estaQuieto())
				continue;
			float[] punta = a.getPunta();
			for (int j = 0; j < seres.size(); j++) {
				Ser b = seres.get(j);
				if (!b.estaVivo() || i == j)
					continue;
				if (distancia(punta[0], b.getX(), punta[1], b.getY()) < b.RADIO) {
					if (a.seComeA(b) == 1) {
						b.golpe(20);
						a.curar(20);
						a.addPuntos(20);
						break;
					}
					if (a.esComidoPor(b) == 1) {
						a.golpe(5);// envenenamiento
						a.addPuntos(-5);
						break;
					}
					// misma especie
					b.golpe(1);
					a.curar(1);
					a.addPuntos(1);
				}
			}
		}
	}

	/**
	 * Retorna un arreglo de float con las distancias de los rivales, el mas cercano por ojo
	 * 
	 * @param protagonista
	 * @return
	 */
	private float[][] sensores(Ser protagonista) {
		// TODO Auto-generated method stub
		ArrayList<Line2D> ojos = protagonista.getOjos();
		int ojo = 0, cont_corte = 0;
		float[][] sens = new float[ojos.size()][4];
		for (Ser enemigo : seres) {
			if (enemigo.estaVivo() && !enemigo.equals(protagonista)) {
				float dist = distancia(protagonista, enemigo);
				if (dist <= protagonista.getVision()) {
					ojo = 0;
					cont_corte = 0;
					for (Line2D line2d : ojos) {
						if (line2d.intersects(enemigo.getBounds2D())) {
							// distancia de un punto a la recta
							float m = (float) (line2d.getY2() - line2d.getY1());
							m = (float) (m / (line2d.getX2() - line2d.getX1()));
							float px = (float) ((m * m * line2d.getX1()) - m * (line2d.getY1() - enemigo.getY()) + enemigo.getX());
							px = px / (m * m + 1);
							float py = (float) (m * (px - line2d.getX1()) + line2d.getY1());

							if (distancia(enemigo.getX(), px, enemigo.getY(), py) < enemigo.RADIO) {
								// ahora mirar si se conserva o existe otro objetivo mas cercano
								if (sens[ojo][0] == 0) {
									sens[ojo][0] = (protagonista.getVision() - dist) / protagonista.getVision();
									sens[ojo][2] = protagonista.seComeA(enemigo);
									sens[ojo][1] = protagonista.esComidoPor(enemigo);
									sens[ojo][3] = protagonista.esMismaEspecie(enemigo);
								} else {
									float temp = (protagonista.getVision() - dist) / protagonista.getVision();
									if (temp > sens[ojo][0]) {
										sens[ojo][0] = temp;
										sens[ojo][2] = protagonista.seComeA(enemigo);
										sens[ojo][1] = protagonista.esComidoPor(enemigo);
										sens[ojo][3] = protagonista.esMismaEspecie(enemigo);
									}
								}
								// Point2D p = new Point2D.Float(px, py);
								// cortes.add(p);
								cont_corte++;// si este sensor lo detecta, los sensores lejanos no
							} else {
								if (cont_corte > 0)
									// como ya fue detectado por otro sensor, no sera detectado de nuevo
									break;
							}
						}
						ojo++;
					}
				}
			}
		}
		return sens;
	}

	/**
	 * Retorna raiz ( (s1.x-s2.x)^2 + (s1.y-s2.y)^2 )
	 */
	private float distancia(Objeto s1, Objeto s2) {
		return (float) (Math.sqrt(Math.pow(s1.getX() - s2.getX(), 2) + Math.pow(s1.getY() - s2.getY(), 2)));
	}

	/**
	 * Retorna raiz ( (s1.x-s2.x)^2 + (s1.y-s2.y)^2 )
	 */
	private float distancia(float x1, float x2, float y1, float y2) {
		return (float) (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
	}

	public void setCementerio(Cementerio c) {
		this.cement = c;
	}

	public void setRapido(Mundo rapid) {
		this.rapido = rapid;
	}

	public Ser getMejor() {
		Ser mejor = null;
		int max = 0;
		// se tienen en cuenta los muertos
		for (int i = cruza.size() - 1; i >= 0; i--) {
			if (cruza.get(i).getPuntos() > max) {
				mejor = cruza.get(i);
				max = cruza.get(i).getPuntos();
			}
		}
		if (mejor != null) {
			// ennviar una copia del ser, no el mejor como tal
			Ser nuevo = new Ser(ANCHO, ALTO, mejor.getCerebro());
			nuevo.setPuntaje(max);
			return nuevo;
		}
		return null;
	}
}