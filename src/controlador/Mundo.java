package controlador;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import modelo.Cementerio;
import vista.ArquitecturaJuego;
import vista.Camara;
import vista.Simulador;

public class Mundo implements ArquitecturaJuego {
	public int ANCHO = Simulador.ANCHO;
	public int ALTO = Simulador.ALTO;
	private int SERES_MIN = 10, SERES_MAX = 30;// valores iuniciasl

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
			System.out.println("Elegido" + elegido);
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
		// TODO Auto-generated method stub
		int tmpvivos = 0;
		int[] especies = new int[3];
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
			if (tmpvivos > SERES_MAX)
				break;
			seres.add(externos.get(i));
			externos.remove(i);
			tmpvivos++;
		}
		/* * Miramos si existe un espacio para crear un hijo */
		if (tmpvivos < SERES_MAX) {
			if (rapido != null) {
				// soy visual, pido a rapido el mejor
				while (seres.size() > SERES_MAX + 20) {
					for (Ser serele : seres) {
						if (!serele.estaVivo()) {
							seres.remove(serele);
							break;
						}
					}
				}
				Ser ser = rapido.getMejor();
				if (ser != null) {
					seres.add(ser);
					tmpvivos++;
				}
				for (int j = 0; j < especies.length; j++) {
					Ser extra = new Ser(ANCHO, ALTO);
					extra.setTipo(j);
					seres.add(extra);
					tmpvivos++;
				}
				/*
				 * Ser mejor = null; int max = 0; for (Ser busca : seres) { if (busca.estaVivo() && busca.getPuntos() > max) { mejor = busca; max = busca.getPuntos(); } busca.setElegido(false); } if (mejor != null) { mejor.setElegido(true); }
				 */
			} else {
				// soy el rapido, envio al cementerio copia del mejor y repoblo
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
					puntos_max = max;
					// vamos a crear copia exacta del mejor, INDEPENDIETNE
					Ser copiaMejor = new Ser(0, 0, mejor.getCerebro());
					copiaMejor.setPuntaje(mejor.getPuntos() + mejor.getFit());// puntaje absoluto obtenido
					// lo guardamos para historial y cruces
					this.cement.addMejor(copiaMejor);
					this.cruza.add(copiaMejor);
					// reiniciamos y recordamos los puntajes del mejor
					mejor.setFit();
					mejor.resetPuntos();
					// crea un ser mutado del padre, pero con el mismo tipo
					Ser nuevo = new Ser(ANCHO, ALTO, mejor);
					nuevos.add(nuevo);
				}
				// Ahora mantener la minima cantidad de seres de cada especie
				for (int j = 0; j < especies.length; j++) {
					if (especies[j] < SERES_MAX / 3) {
						Ser extra = new Ser(ANCHO, ALTO);
						extra.setTipo(j);
						nuevos.add(extra);
					}
					Ser extra = new Ser(ANCHO, ALTO);
					extra.setTipo(j);
					nuevos.add(extra);
				}
				// ahora agregar a los cruzados
				for (int i = 0; i < cruza.size() - 1; i++) {
					for (int j = i + 1; j < cruza.size(); j++) {
						Ser nuevo = new Ser(ANCHO, ALTO, cruza.get(i), cruza.get(j));
						nuevos.add(nuevo);
						cruza.remove(j);
						cruza.remove(i);
					}
				}
				// agregarlos al mundo
				for (Ser ser : nuevos) {
					seres.add(ser);
					tmpvivos++;
				}
				// eliminar de memoria algunos de los peores muertos
				for (int i = seres.size() - 1; i >= 0; i--) {
					if (!seres.get(i).estaVivo() && seres.get(i).getPuntos() < prom * 4 / 3)
						seres.remove(i);
				}
			}
		}
		vivos = tmpvivos;
		// SERES_MAX = 30;
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
					// System.out.println(a.getX() + "Choque " + b.getX());
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
			// else System.exit(0);
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
		for (int i = seres.size() - 1; i >= 0; i--) {
			if (seres.get(i).getPuntos() + seres.get(i).getFit() > max) {
				mejor = seres.get(i);
				max = seres.get(i).getPuntos() + seres.get(i).getFit();
			}
		}
		if (mejor != null) {
			Ser nuevo = new Ser(ANCHO, ALTO, mejor.getCerebro());
			return nuevo;
		}
		return null;
	}
}