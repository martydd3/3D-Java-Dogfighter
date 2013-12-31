import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 
 * Holds the vertices and pairs of vertices needed to draw a wire frame display
 * of an entity
 *
 * Also contains static methods to create matrixes and use them transform 
 * vertices and lines for display
 */

public class Model {

	//holds the lines to be drawn in the model
	private List<Polygon> poly;
	
	//holds variables for cosmetic and animation modifications to the model
	public double scale;
	public double pitch;
	public double yaw;
	public double roll;
	
	public Model(Polygon[] poly){
		this.poly = new ArrayList<Polygon>();
		
		for(Polygon p: poly)
			this.poly.add(new Polygon(p));
		
		scale = 1;
		pitch = 0;
		yaw = 0;
		roll = 0;
	}
	
	public void draw(Graphics g, Matrix trans, Color color){
		ArrayList<Polygon> temp = new ArrayList<Polygon>();
		
		//create matrixes to apply cosmetic transformations
		Matrix s = new Matrix(scale, Matrix.Type.S);
		Matrix pit = new Matrix(pitch, Matrix.Type.Y);
		Matrix rol = new Matrix(roll, Matrix.Type.X);
		Matrix y = new Matrix(yaw, Matrix.Type.Z);
		
		Matrix mat = y.multiply(pit.multiply(rol.multiply(s)));
		
		//create transformed polygon
		for(Polygon p : poly)
		{	
			Vector[] vertices = new Vector[3];
			
			for(int i = 0; i < 3; i++)
			{
				vertices[i] = new Vector(p.points[i]);
				vertices[i] = trans.multiply(mat.multiply(vertices[i]));
			}
				
			temp.add(new Polygon(vertices));
		}
		
		//sort polygons according to distance from camera at (0, 0, 0)
		Collections.sort(temp);
		
		for(Polygon p : temp){

			Vector v1 = p.points[0];
		    Vector v2 = p.points[1];
			Vector v3 = p.points[2];
			
			//draw the vectors in the polygon if the points they represent are
			//not behind the plane x = 0
			//divide y and z by x in order to obtain a perspective effect
			if(!(v1.getX() < 0 || v2.getX() < 0 || v3.getX() < 0))
			{
				int[] xPoints = {(int)(100*v1.getY()/v1.getX()), 
						(int)(100*v2.getY()/v2.getX()), 
						(int)(100*v3.getY()/v3.getX())};
				
				int[] yPoints = {(int)(100*v1.getZ()/v1.getX()), 
						(int)(100*v2.getZ()/v2.getX()), 
						(int)(100*v3.getZ()/v3.getX())};
				
				//draw outline for polygon
				g.setColor(color);
				g.drawPolygon(xPoints, yPoints, 3);
			}
		}
	}
}
