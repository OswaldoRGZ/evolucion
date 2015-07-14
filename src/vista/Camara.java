package vista;

import java.util.ArrayList;

import controlador.Objeto;
import controlador.Ser;

public class Camara {
	private int ancho, alto, posx, posy, alto_mundo, ancho_mundo, vex, vey;
	private Ser actor;
	private ArrayList<Ser> actores;

	public Camara(int ancho, int alto) {
		this.alto = alto;
		this.ancho = ancho;
		this.actor = null;
		this.actores = new ArrayList<Ser>();
		setPosicion(0, 0);
	}

	public void setDimMundo(int mancho, int malto) {
		this.alto_mundo = malto;
		this.ancho_mundo = mancho;
	}

	public void setPosicion(int x, int y) {
		this.posx = x;
		this.posy = y;
	}

	public int getPosx() {
		return posx;
	}

	public int getPosy() {
		return posy;
	}

	public void mover(int movex, int movey) {
		this.vex = movex;
		this.vey = movey;
		// this.posx += movex;
		// this.posy += movey;
		verificar();
		// System.out.println(posx+" - "+posy);
	}

	private void verificar() {
		if (this.posx + this.ancho > this.ancho_mundo) {
			this.posx = this.ancho_mundo - this.ancho;// this.ancho_mundo-this.ancho;
			vex = 0;
		}
		if (this.posy + this.alto > this.alto_mundo) {
			this.posy = this.alto_mundo - this.alto;// this.alto_mundo-this.alto;
			vey = 0;
		}
		if (posx < 0) {
			this.posx = 0;// 0;
			vex = 0;
		}
		if (posy < 0) {
			this.posy = 0;// 0;
			vey = 0;
		}
	}

	public boolean enPantalla(float x, float y) {
		return posx < x & posx + ancho > x & posy < y & posy + alto > y;
	}

	public boolean enPantalla(double x, double y) {
		return posx < x & posx + ancho > x & posy < y & posy + alto > y;
	}

	public boolean enPantalla(Objeto obj) {
		return posx < obj.getX() + obj.getRadio() && posx + ancho > obj.getX() - obj.getRadio() && posy < obj.getY() + obj.getRadio() && posy + alto > obj.getY() - obj.getRadio();
	}

	public void actualizar() {
		// TODO Auto-generated method stub
		if (actor != null) {
			// if ( Math.abs(posx - ((int)actor.getX()-ancho/2) ) > ancho/10)
			this.posx = (int) actor.getX() - ancho / 2;
			// if ( Math.abs(posy -((int)actor.getY()-alto/2) ) > alto/10)
			this.posy = (int) actor.getY() - alto / 2;
			if (!actor.estaVivo()) {
				System.out.println("Siguiendo a un muerto");
				actor = null;
			}
		} else {
			if (actores.size() > 0) {
				actor = actores.get(0);
				actores.remove(0);
			}
			this.posx += vex;
			this.posy += vey;
			vex = 0;
			vey = 0;
			/*
			 * if (vex > 0) vex--; else if (vex < 0) vex++; if (vey > 0) vey--; else if (vey < 0) vey++;
			 */
			verificar();
		}
	}

	public void seguir(Ser elactor) {
		if (this.actor != null) {
			this.actor.setElegido(false);
		}
		this.actor = elactor;
	}

	public void addSeguir(Ser elactor) {
		actores.add(elactor);
	}

	public void setDimensiones(int aNCHO2, int aLTO2) {
		// TODO Auto-generated method stub
		this.alto_mundo = aLTO2;
		this.ancho_mundo = aNCHO2;
	}
}
