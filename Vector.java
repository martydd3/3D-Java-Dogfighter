
public class Vector {

	//holds the 3 components of the vector
	private double[] comps;
	
	//constructor taking in the 3 components of the vector
	public Vector(double x, double y, double z)
	{
		comps = new double[3];
		comps[0] = x;
		comps[1] = y;
		comps[2] = z;
	}
	
	//constructor that makes a copy of another vector
	public Vector(Vector other)
	{
		comps = other.getComps();
	}
	
	//getter methods for X, Y, and Z
	public double getX(){return comps[0];}
	
	public double getY(){return comps[1];}
	
	public double getZ(){return comps[2];}
	
	//returns a copy of the the Vector's components
	public double[] getComps()
	{
		double[] result = new double[3];
		for(int i = 0; i < 3; i++)
			result[i] = comps[i];
		
		return result;
	}
}
