package hci;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.io.Serializable;
import java.util.ArrayList;

public class Selection implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4084887218739731384L;

	ArrayList<Point> points = new ArrayList<Point>();
	Polygon poly;
	
	Color c;
	String name;
	
	public Selection(ArrayList<Point> points, Color c) {
		int[] polygonXs = new int[points.size()];
		int[] polygonYs = new int[points.size()];
		
		for (int i = 0; i < points.size(); i++) {
			polygonXs[i] = points.get(i).x;
			polygonYs[i] = points.get(i).y;			
		}
		
		this.poly = new Polygon(polygonXs, polygonYs, points.size());
		this.points = points;
		this.c = c;
		
		
	}
	
	public void paint(Graphics g) {
		g.setColor(c);

		for (int i = 1; i < points.size(); i++) {
			Point lastVertex = points.get(i - 1);
			g.fillOval((int) lastVertex.getX() - 5, (int) lastVertex.getY() - 5, 10, 10);
			g.drawLine((int) lastVertex.getX(), (int) lastVertex.getY(), points.get(i).x, points.get(i).y);
		}
		g.fillOval((int) points.get(points.size()-1).getX() - 5, (int) points.get(points.size()-1).getY() - 5, 10, 10);
		g.drawLine((int) points.get(points.size()-1).getX(), (int) points.get(points.size()-1).getY(),
				points.get(0).x, points.get(0).y);
	}
	
	public boolean isPointInShape(Point p) {
		int intersections = 0;
		
		for (int i = 1; i < points.size(); i++) {
			Point lastVertex = points.get(i - 1);
			Point intersection = intersection(p, new Point(p.x, p.y + 10000), lastVertex, points.get(i));
			
			if ( intersection != null && intersection.y > p.y) {
				intersections++;
			}
			
		}

		Point intersection = intersection(p, new Point(p.x, p.y + 10000), points.get(points.size()-1), points.get(0));
		
		if ( intersection != null && intersection.y > p.y) {
			intersections++;
		}
		
		if (intersections % 2 == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public Point intersection(Point a1, Point a2, Point b1, Point b2){
			int d = (a1.x-a2.x)*(b1.y-b2.y) - (a1.y-a2.y)*(b1.x-b2.x);
			if (d == 0) return null;
			
			int xi = ((b1.x-b2.x)*(a1.x*a2.y-a1.y*a2.x)-(a1.x-a2.x)*(b1.x*b2.y-b1.y*b2.x))/d;
			int yi = ((b1.y-b2.y)*(a1.x*a2.y-a1.y*a2.x)-(a1.y-a2.y)*(b1.x*b2.y-b1.y*b2.x))/d;
			 
			return new Point(xi,yi);
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
