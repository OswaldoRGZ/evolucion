package controlador;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Ser extends Objeto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5723858004810235024L;

	private final int CANT_OJOS = 110;// se multiplica por 4

	private final int ENTRADAS_RNA = CANT_OJOS * 4 //
			+ 1// sensor vida
			+ 2// sensores vx y vy
			+ 1// sensor velocidad
			+ 4// fronteras
			+ 1// hambre
			+ 1// angulo actual
	;
	private float[][] sensores;
	private final int SALIDAS_RNA = 2 + // vx y vy
	+1// aceleracion
	;// total 3 //
	private final int TURNO = 5, CASTIGO = 10, VIDA_MAX = 100, MAX_HAMBRE = 1000, VISION = 500;
	private float aceleracion, golpevx, golpevy, golpeve;
	private float[] ruedas;
	private int puntos, frame, rojo, verde, azul, fit, mutaciones;// cuando supera el maximo, empieza a perder vida....
	private float hambre, vida;

	private ArrayList<Line2D> ojos;
	// private ArrayList<Line2D> fronteras;
	private RNA cerebro;
	private Color color;
	private boolean elegido;

	public Ser() {
		super(0, 0, true);
		valoresIniciales(null, null);
	}

	Ser(int ancho, int alto) {
		super(ancho, alto, true);
		valoresIniciales(null, null);
	}

	/**
	 * Crea ser nuevo mutado de padre
	 * 
	 * @param ancho
	 * @param alto
	 * @param padre
	 */
	Ser(int ancho, int alto, Ser padre) {
		super(ancho, alto, true);
		valoresIniciales(padre, null);
	}
	/**
	 * Simple copia de ser vivo con cerebro de otro ser.
	 * 
	 * @param cerebro
	 */
	public Ser(RNA cerebro) {
		super(0, 0, true);
		valoresIniciales(null, null);
	}
	/**
	 * Crea ser nuevo independiente con copia independiente de cerebro
	 * 
	 * @param ancho
	 * @param alto
	 * @param cerebro
	 */
	public Ser(int ancho, int alto, RNA cerebro) {
		super(ancho, alto, true);
		valoresIniciales(null, null);
		// crea una copia exacta del CEREBRO, INDEPENDIENTE
		this.cerebro = new RNA(cerebro);
	}

	Ser(int ancho, int alto, Ser padre, Ser madre) {
		super(ancho, alto, true);
		valoresIniciales(padre, madre);
	}

	private void valoresIniciales(Ser padre, Ser madre) {
		this.mutaciones = 0;
		this.golpeve = 0;
		this.golpevx = 0;
		this.golpevy = 0;
		this.hambre = 0;
		this.vida = VIDA_MAX;
		this.puntos = 0;
		this.frame = 1 + (int) (Math.random() * TURNO);
		this.ojos = new ArrayList<Line2D>();
		// this.fronteras = new ArrayList<Line2D>();
		// Caracterizticas mentales y fisicas
		this.ruedas = new float[2];
		this.rojo = 0;
		this.verde = 0;
		this.azul = 0;
		if (padre == null && madre == null) {
			this.cerebro = new RNA(ENTRADAS_RNA, SALIDAS_RNA);
			switch ((int) (Math.random() * 3)) {
				case 0 :
					rojo = 250;
					break;
				case 1 :
					verde = 250;
					break;
				case 2 :
					azul = 250;
					break;
				default :
					System.out.println("Color fuera de limites");
					break;
			}
		} else {
			if (madre == null) {
				// copia exacta del cerebro del padre, independiente
				this.cerebro = new RNA(padre.cerebro);
				do {
					this.cerebro.mutar();
					this.mutaciones++;
				} while (Math.random() < 0.2);
				heredarRasgos(padre);
			} else {
				// crea un cruce de los cerebros de padre y madre
				this.cerebro = new RNA(padre.cerebro, madre.cerebro);
				do {
					this.cerebro.mutar();
					this.mutaciones++;
				} while (Math.random() < 0.2);
				if (Math.random() < 0.5) {
					heredarRasgos(madre);
				} else {
					heredarRasgos(padre);
				}
			}
		}
		// el color depende de las caracterizticas del ser.
		try {
			this.color = new Color(rojo, verde, azul);
		} catch (Exception el) {
			System.out.println(rojo + " " + verde + " " + azul);
			this.color = new Color(255, 255, 255);
		}
	}

	private void heredarRasgos(Ser herenciador) {
		this.rojo = herenciador.getRojo();
		this.verde = herenciador.getVerde();
		this.azul = herenciador.getAzul();
	}

	public int[] mezclar(int[] vector) {
		Random rnd = new Random();
		for (int i = vector.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = vector[index];
			vector[index] = vector[i];
			vector[i] = a;
		}
		return vector;
	}

	public RNA getCerebro() {
		return cerebro;
	}

	public int getPuntos() {
		return puntos;
	}

	public boolean estaVivo() {
		return vida > 0;
	}

	public void sensores(float[][] sens) {
		this.sensores = sens;
		float[] entradas = new float[ENTRADAS_RNA];

		entradas[0] = vida / VIDA_MAX;
		entradas[1] = getVex();
		entradas[2] = getVey();
		entradas[3] = getVel() / VELOCIDAD_MAX;

		entradas[4] = getX() > getVision() ? 0 : (getVision() - getX()) / (getVision());
		entradas[5] = getY() > getVision() ? 0 : (getVision() - getY()) / (getVision());
		entradas[6] = ancho_mundo - getX() < getVision() ? 0 : (getVision() - (ancho_mundo - getX())) / (getVision());
		entradas[7] = alto_mundo - getY() < getVision() ? 0 : (getVision() - (alto_mundo - getY())) / (getVision());

		entradas[8] = 1.0f * hambre / MAX_HAMBRE;
		entradas[9] = (float) (this.getAngulo() / (Math.PI * 2));

		for (int i = 0; i < sens.length; i++) {
			entradas[10 + i * 4] = sens[i][0];
			entradas[10 + i * 4 + 1] = sens[i][1];
			entradas[10 + i * 4 + 2] = sens[i][2];
			entradas[10 + i * 4 + 3] = sens[i][3];
		}
		/*
		 * for (int i = 0; i < entradas.length; i++) { if (entradas[i] > 1.1) { System.out.println(i + " " + entradas[i]); System.exit(0); } }
		 */
		/*************************
		 ** FIN DE LAS ENTRADAS **
		 *************************/
		// la magia del cine
		float[] salidas = cerebro.reconocer(entradas);
		/*
		 * for (int i = 0; i < salidas.length; i++) { salidas[i] -= 0.5f; if (salidas[i] <= 0) {// realmente es una desicion o no salidas[i] = 0; } }
		 */
		// fin de la magia del cine
		this.ruedas = new float[2];
		ruedas[0] = salidas[0];
		ruedas[1] = salidas[1];
		aceleracion = salidas[2];
	}

	public float[] getPunta() {
		float[] ret = new float[2];
		ret[0] = (int) (getX() + Math.cos(getAngulo()) * DIAMETRO);
		ret[1] = (int) (getY() + Math.sin(getAngulo()) * DIAMETRO);
		return ret;
	}

	@Override
	public void dibujar(Graphics2D g) {
		if (estaVivo()) {
			// dibujamos el puntaje
			g.setColor(color);
			g.fillOval((int) getX() - RADIO, (int) getY() - RADIO, DIAMETRO, DIAMETRO);
			g.setColor(new Color(250, 250, 250));
			// dibujamos la naricita porq super ya dibuja el objeto
			g.drawLine((int) getX(), (int) getY(), (int) (getX() + Math.cos(getAngulo()) * DIAMETRO), (int) (getY() + Math.sin(getAngulo()) * DIAMETRO));
			// dibujamos la vida:
			// g.drawLine((int) getX() - RADIO, (int) getY() - DIAMETRO, (int) (getX() - RADIO + DIAMETRO * vida / VIDA_MAX), (int) getY() - DIAMETRO);
			// g.drawString(vida + "", (int) getX() - DIAMETRO, (int) getY() - DIAMETRO);// + hambre
			// dibujamos el habmre
			g.drawString(puntos + "", (int) getX() - DIAMETRO, (int) getY() + DIAMETRO);// + aceleracion + ""
			if (elegido) {// a sido seleccionado, le mostramos todos los ojos
				calcularOjos();
				// g.drawLine((int) getX() - RADIO, (int) getY() + DIAMETRO, (int) (getX() - RADIO + DIAMETRO * frame / TURNO), (int) getY() + DIAMETRO);
				if (sensores != null)
					for (int i = 0; i < ojos.size(); i++) {
						if (this.sensores[i][0] != 0) {
							g.setColor(new Color(this.sensores[i][1], this.sensores[i][2], this.sensores[i][3]));
							float fact = 1 - this.sensores[i][0];
							Line2D l = new Line2D.Float(this.getX(), this.getY(), (float) (this.getX() + (ojos.get(i).getX2() - ojos.get(i).getX1()) * fact),
									(float) (this.getY() + (ojos.get(i).getY2() - ojos.get(i).getY1()) * fact));
							g.draw(l);
						} else {
							g.setColor(new Color(150, 150, 150));
							g.draw(this.ojos.get(i));
						}
					}
				/*
				 * g.draw(ojos.get(0)); g.draw(ojos.get(ojos.size() - 1));
				 */
				/*
				 * fronteras.clear(); fronteras.add(new Line2D.Float(getX(), getY(), 0, getY())); fronteras.add(new Line2D.Float(getX(), getY(), getX(), 0)); fronteras.add(new
				 * Line2D.Float(getX(), getY(), ancho_mundo, getY())); fronteras.add(new Line2D.Float(getX(), getY(), getX(), alto_mundo)); for (Line2D l : fronteras) { g.draw(l);
				 * }
				 */
			}
		} else {
			g.setColor(color); // dibujamos el tipo de red
			g.drawString(puntos + " " + cerebro.adn(), getX() - DIAMETRO, getY() + DIAMETRO);
			// g.drawOval((int) getX() - RADIO, (int) getY() - RADIO, DIAMETRO, DIAMETRO);
		}

	}

	@Override
	public void actualizar() {
		girar(ruedas[0] - ruedas[1]);
		acelerar(aceleracion);
		super.actualizar();

		/* * Efecto del golpe */
		if (golpeve > 0) {
			setX(getX() + golpeve * golpevx);
			setY(getY() + golpeve * golpevy);
			golpeve -= 0.1f;
		}
		/*
		 * Rebotes
		 */
		if (getX() < 0) {
			vida -= CASTIGO;
			puntos -= 1;
			setX(DIAMETRO);
			// setAngulo((float) (getAngulo() + Math.PI));
		}

		if (getY() < 0) {
			vida -= CASTIGO;
			puntos -= 1;
			setY(DIAMETRO);
			// setAngulo((float) (getAngulo() + Math.PI));
		}
		if (getX() > ancho_mundo) {
			vida -= CASTIGO;
			puntos -= 1;
			setX(ancho_mundo - DIAMETRO);
			// setAngulo((float) (getAngulo() + Math.PI));
		}
		if (getY() > alto_mundo) {
			vida -= CASTIGO;
			puntos -= 1;
			setY(alto_mundo - DIAMETRO);
			// setAngulo((float) (getAngulo() + Math.PI));
		}
		if (frame == 0) {
			hambre = 0.1f + hambre * 1.1f;
			if (hambre > MAX_HAMBRE) {
				vida -= 1;
				hambre = MAX_HAMBRE;
			}
		}

	}

	/**
	 * Retorna el arreglo de lineas que representa el campo de visiion del ser
	 * 
	 * @return
	 */
	public ArrayList<Line2D> getOjos() {
		// TODO Auto-generated method stub
		calcularOjos();
		return ojos;
	}

	/**
	 * Calcular ojos crea el arreglo de lineas que representa el campo de vision del ser
	 */
	private void calcularOjos() {
		ojos = new ArrayList<Line2D>();
		float separador = 0.05f;
		float inf = (float) ((CANT_OJOS - 1) * separador / 2);
		for (int i = 0; i < CANT_OJOS; i++) {
			Line2D l = new Line2D.Float((float) (getX() + Math.cos(getAngulo() - inf + i * separador) * RADIO), (float) (getY() + Math.sin(getAngulo() - inf + i * separador)
					* RADIO), (float) (getX() + Math.cos(getAngulo() - inf + i * separador) * getVision()), (float) (getY() + Math.sin(getAngulo() - inf + i * separador)
					* getVision()));
			ojos.add(l);
		}
	}

	public Rectangle2D getBounds2D() {
		// TODO Auto-generated method stub
		Rectangle2D rec = new Rectangle2D.Float(getX() - RADIO, getY() - RADIO, DIAMETRO, DIAMETRO);
		return rec;
	}

	public boolean miTuro() {
		frame++;
		if (frame > TURNO) {
			frame = 0;
			return true;
		} else {
			return false;
		}
	}

	public Color miColor() {
		return this.color;
	}

	public void setColor(Color c) {
		this.color = c;
	}

	public void golpe(int dano) {
		vida -= dano;
		/* golpevx = m.getVex(); golpevy = m.getVey(); golpeve = m.getVel();// cuanto me empuja un misisl */
	}

	public void curar(int dano) {
		// TODO Auto-generated method stub
		vida += Math.abs(dano);
		hambre = 0;
		if (vida > VIDA_MAX) {
			vida = VIDA_MAX;
		}
	}

	public void addPuntos(int valor) {
		this.puntos += valor;
	}

	public void setElegido(boolean es_elegido) {
		this.elegido = es_elegido;
	}

	public int getVision() {
		return VISION;
	}

	public int getRojo() {
		// TODO Auto-generated method stub
		return this.rojo;
	}

	public int getVerde() {
		// TODO Auto-generated method stub
		return this.verde;
	}

	public int getAzul() {
		// TODO Auto-generated method stub
		return this.azul;
	}

	public void resetPuntos() {
		this.puntos = 0;
	}

	/**
	 * 0-Rojo, 1-Verde, 2-Azul
	 * 
	 * @return
	 */
	public int getTipo() {
		if (this.rojo > 0)
			return 0;
		if (this.verde > 0)
			return 1;
		return 2;// es azul
	}

	public void reset() {
		this.golpeve = 0;
		this.golpevx = 0;
		this.golpevy = 0;
		this.hambre = 0;
		this.vida = VIDA_MAX;
		this.puntos = 0;
		this.frame = 1 + (int) (Math.random() * TURNO);
		this.ojos = new ArrayList<Line2D>();
		// this.fronteras = new ArrayList<Line2D>();
		this.aceleracion = 0;
		this.ruedas = new float[2];
		this.elegido = false;
		this.sensores = new float[1][1];
	}

	public void setTipo(int r, int g, int b) {
		// TODO Auto-generated method stub
		this.rojo = r;
		this.verde = g;
		this.azul = b;
		this.color = new Color(rojo, verde, azul);
	}

	public void setTipo(int v) {
		this.rojo = 0;
		this.verde = 0;
		this.azul = 0;
		switch (v) {
			case 0 :
				this.rojo = 250;
				break;
			case 1 :
				this.verde = 250;
				break;
			case 2 :
				this.azul = 250;
				break;
			default :
				System.out.println("Tipo incorrecto");
				System.exit(0);
				break;
		}
		this.color = new Color(rojo, verde, azul);
	}

	public void setPuntaje(int puntos2) {
		// TODO Auto-generated method stub
		this.puntos = puntos2;
	}

	/**
	 * Guardar el puntaje actual del SER, si es menor que el fit se conserva el fit
	 */
	public void setFit() {
		this.fit = this.puntos > this.fit ? this.puntos : this.fit;
	}

	/*
	 * public int getFit() { // TODO Auto-generated method stub return this.fit; }
	 */

	public boolean estaQuieto() {
		// TODO Auto-generated method stub
		return this.aceleracion <= 0;
	}

	public int seComeA(Ser otro) {
		return ((this.getTipo() + 1) % 3 == otro.getTipo()) ? 1 : 0;
	}

	public int esComidoPor(Ser otro) {
		return ((otro.getTipo() + 1) % 3 == this.getTipo()) ? 1 : 0;
	}

	public int esMismaEspecie(Ser otro) {
		return (otro.getTipo() == this.getTipo()) ? 1 : 0;
	}

	public int getMutaciones() {
		return this.mutaciones;
	}

}
