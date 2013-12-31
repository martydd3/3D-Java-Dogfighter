/**
 * Quaternion datatype for holding orientation information
 * 
 * @author Martin
 *
 */

public class Quaternion {

	//4 components present in a quaternion
	private double w, x, y, z;
	
	//default constructor
	public Quaternion(double w, double x, double y, double z)
	{
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//multiplies two quaternions together according to multipliction rules
	//and returns a new quaternion
	public Quaternion multiply(Quaternion other)
	{
		this.normalize();
		other.normalize();
		
		double w1 = w*other.w - x*other.x - y*other.y - z*other.z;
		double x1 = w*other.x + x*other.w + y*other.z - z*other.y;
		double y1 = w*other.y - x*other.z + y*other.w + z*other.x;
		double z1 = w*other.z + x*other.y - y*other.x + z*other.w;
		
		return new Quaternion(w1, x1, y1, z1);
	}
	
	//creates a rotation matrix from a quaternion
	public Matrix createMatrix()
	{
		double[][] c = new double[4][4];
		
		this.normalize();
		
		c[0][0] = 1-2*y*y-2*z*z;
		c[1][0] = 2*x*y-2*w*z;
		c[2][0] = 2*x*z+2*w*y;
		
		c[0][1] = 2*x*y+2*w*z;
		c[1][1] = 1-2*x*x-2*z*z;
		c[2][1] = 2*y*z-2*w*x;
		
		c[0][2] = 2*x*z-2*w*y;
		c[1][2] = 2*y*z+2*w*x;
		c[2][2] = 1-2*x*x-2*y*y;
		
		c[3][3] = 1;
		
		return new Matrix(c);
	}
	
	//creates a quaternion representing a rotation theta about vector (x, y, z)
	//rotation is counterclockwise facing the direction of the vector
	public static Quaternion createRotate(double theta, double x, double y, double z)
	{
		double w1 = Math.cos(theta/2);
		double x1 = x*Math.sin(theta/2);
		double y1 = y*Math.sin(theta/2);
		double z1 = z*Math.sin(theta/2);
		
		return new Quaternion(w1, x1, y1, z1);
	}
	
	//rescales the quaternion to length 1 in order to account for 
	//floating point calculation errors
	public void normalize()
	{
		double mag = Math.sqrt(w*w + x*x + y*y + z*z);
		w = w/mag;
		x = x/mag;
		y = y/mag;
		z = z/mag;
	}
	
	//returns a quaternion representing a rotation in the opposite direction
	//as the original
	public Quaternion invert()
	{
		return new Quaternion(w, -x, -y, -z);
	}
	
	//returns the euler angles resulting from applying the quaternion
	public double getTheta()
	{
		return Math.asin(2*(w*y-x*z));
	}
	
	public double getPsi()
	{
		return Math.atan2(2*(w*z+x*y), 1-2*(w*w+x*x));
	}
	
	public double getPhi()
	{
		return Math.atan2(2*(w*x+y*z), 1-2*(x*x+y*y));
	}
}
