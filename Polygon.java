package main;
/**
 * Holds an array of coordinates in 3d, as well as the average location of
 * these coordinates to be used for ordering depending on their distance
 * from x = 0
 * 
 * @author Martin
 *
 */
public class Polygon implements Comparable<Polygon>{

	public Vector[] points;
	public Vector center;
	
	//default constructor from a list of vectors
	public Polygon(Vector[] points)
	{
		this.points = points;		
		center = getCenter();
	}
	
	//makes a copy of a polygon, with its own non-aliased list of vectors
	public Polygon(Polygon other)
	{
		Vector[] thisPoints = new Vector[other.points.length];
		for(int i = 0; i < thisPoints.length; i++)
			thisPoints[i] = new Vector(other.points[i]);
		
		points = thisPoints;	
		center = getCenter();
	}
	
	//get center of polygon by approximating the coordinates of the polygon's
	//vectors
	private Vector getCenter()
	{
		double[] temp = new double[3];
		
		for(Vector v: points)
		{	
			temp[0] += v.getX();
			temp[1] += v.getY();
			temp[2] += v.getZ();
		}
		
		for(int i = 0; i < temp.length; i++)
		{
			temp[i] /= points.length;
		}
		
		return new Vector(temp[0], temp[1], temp[2]);
	}
	
	@Override
	//orders the polygon depending on their distance from (0,0), closest
	//to farthest
	public int compareTo(Polygon other) {
		Vector vOther = other.center;
		
		double disThis = center.getX()*center.getX() + 
				center.getY()*center.getY() + center.getZ()*center.getZ();
		
		double disOther = vOther.getX()*vOther.getX() + 
				vOther.getY()*vOther.getY() + vOther.getZ()*vOther.getZ();
		
		if(disThis > disOther)
			return -1;
		else if(disThis < disOther)
			return 1;
		else
			return 0;
	}
}
