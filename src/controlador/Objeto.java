package controlador;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

import vista.ArquitecturaJuego;

public class Objeto implements ArquitecturaJuego, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5686746330695311674L;
	protected int RADIO = 9;
	protected int DIAMETRO = RADIO * 2;
	protected float VELOCIDAD_MAX = 6f;
	private float x, y, vex, vey, vel, angulo;
	protected int alto_mundo, ancho_mundo;
	private boolean servivo;

	Objeto(int ancho, int alto, boolean esta_vivo) {
		this.alto_mundo = alto;
		this.ancho_mundo = ancho;
		x = (float) (Math.random() * ancho_mundo);
		y = (float) (Math.random() * alto_mundo);
		vex = 0;
		vey = 0;
		vel = 0;
		angulo = (float) (Math.random() * Math.PI * 2);
		this.servivo = esta_vivo;
	}

	public int getRadio() {
		return this.RADIO;
	}

	public boolean estaVivo() {
		return false;
	}

	public boolean esSerVivoNA() {
		return servivo;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getAngulo() {
		return angulo;
	}

	@Override
	public void dibujar(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.WHITE);
		g.fillOval((int) x - RADIO, (int) y - RADIO, DIAMETRO, DIAMETRO);
		/*
		 * g.drawLine((int) x, (int) y, (int) (x + Math.cos(angulo) * 500), (int) (y + Math.sin(angulo) * 500));
		 */
	}

	@Override
	public void actualizar() {
		x += vex * vel;
		y += vey * vel;
	}

	protected void girar(float valor) {
		if (valor == 0)
			return;
		if (valor < -1)
			valor = -1;
		if (valor > 1)
			valor = 1;
		angulo = (float) ((angulo + valor / 2) % (Math.PI * 2));
		vex = (float) Math.cos(angulo);
		vey = (float) Math.sin(angulo);
	}

	protected void acelerar(float valor) {
		if (valor < 0)
			valor = 0;
		if (valor > 1)
			valor = 1;
		valor = valor * VELOCIDAD_MAX;
		if (Math.abs(vel - valor) > 0.1) {
			if (vel < valor)
				vel += 0.1f;
			else
				vel -= 0.1f;
		}
		if (vel > VELOCIDAD_MAX)
			vel = VELOCIDAD_MAX;
		if (vel < -VELOCIDAD_MAX)
			vel = -VELOCIDAD_MAX;
	}

	protected float getVex() {
		return vex;
	}

	protected void setVex(float vex) {
		this.vex = vex;
	}

	protected float getVey() {
		return vey;
	}

	protected void setVey(float vey) {
		this.vey = vey;
	}

	protected float getVel() {
		return vel;
	}

	protected void setVel(float vel) {
		this.vel = vel;
	}

	protected void setX(float x) {
		this.x = x;
	}

	protected void setY(float y) {
		this.y = y;
	}

	protected void setAngulo(float a) {
		this.angulo = a;
	}

	protected int getAnchoMundo() {
		return this.ancho_mundo;
	}

	protected int getAltoMundo() {
		return this.alto_mundo;
	}

}
