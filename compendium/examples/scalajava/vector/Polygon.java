package week10.vector;

import se.lth.cs.pt.window.SimpleWindow;

public class Polygon {
	private Point[] vertices; // vektor med hörnpunkter
	private int n; // antalet hörnpunkter

	/** Skapar en polygon */
	public Polygon() {
		vertices = new Point[1];
		n = 0;
	}
	
	private void extend(){
		Point[] oldVertices = vertices;
		vertices = new Point[2 * vertices.length]; // skapa dubbel plats
		for (int i = 0; i < oldVertices.length; i++) {  // kopiera
			vertices[i] = oldVertices[i];
		}		
	}

	/** Definierar en ny punkt med koordinaterna x,y */
	public void addVertex(int x, int y) {
		if (n == vertices.length) extend();
		vertices[n] = new Point(x, y);
		n++;
	}

	/**
	 * Flyttar polygonen avståndet dx i x-led, dy i y-led
	 */
	public void move(int dx, int dy) {
		for (int i = 0; i < n; i++) {
			vertices[i].move(dx, dy);
		}
	}

	/** Ritar polygonen i fönstret w */
	public void draw(SimpleWindow w) {
		if (n == 0) {
			return;
		}
		Point start = vertices[0];
		w.moveTo(start.getX(), start.getY());
		for (int i = 1; i < n; i++) {
			w.lineTo(vertices[i].getX(), vertices[i].getY());
		}
		w.lineTo(start.getX(), start.getY());
	}

	/**
	 * Lägger in en ny punkt med koordinaterna x,y på plats pos. Efterföljande
	 * element flyttas
	 */
	public void insertVertex(int pos, int x, int y) {
		if (n == vertices.length) extend();
		for (int i = n; i > pos; i--) {
			vertices[i] = vertices[i - 1];
		}
		vertices[pos] = new Point(x, y);
		n++;
	}

	/**
	 * Tar bort punkten på plats pos. Efterföljande element flyttas
	 */
	public void removeVertex(int pos) {
		for (int i = pos; i < n - 1; i++) {
			vertices[i] = vertices[i + 1];
		}
		vertices[n - 1] = null;
		n--;
	}
}
