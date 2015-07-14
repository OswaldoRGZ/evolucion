package modelo;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Imagen {

	private BufferedImage bigImg;

	public static final Rectangle RE_NAVE = new Rectangle(400, 400, 65, 65);
	public static final Rectangle RE_MISIL = new Rectangle(267, 267, 34, 34);
	// private final Rectangle NAVE_2 = new Rectangle(1,2,3,4);
	// private final Rectangle PORTAVIONES = new Rectangle(1,2,3,4);

	public static final int ID_NAVE = 0;
	public static final int ID_MISIL = 1;

	private Image[] partes;

	public Imagen() {
		partes = new Image[2];
		try {
			bigImg = ImageIO.read(new File("1945.png"));
			partes[ID_NAVE] = getSprite(RE_NAVE);
			partes[ID_MISIL] = getSprite(RE_MISIL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.print("No se encontro el archivo de imagen para las naves.");
			//e.printStackTrace();
			System.exit(0);
		}
	}

	private BufferedImage getParte(int x, int y, int ancho, int alto) {
		return bigImg.getSubimage(x, y, ancho, alto);
	}

	private Image getSprite(Rectangle seccion) {
		return getParte(seccion.x, seccion.y, seccion.width, seccion.height);
	}

	public Image getSprite(int id) {
		if (id < 0 || id > partes.length) {
			System.err.println("Id de imagen desconocido");
			System.exit(0);
		}
		return partes[id];
	}
}
