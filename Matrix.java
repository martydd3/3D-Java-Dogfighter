package main;
/**
 * @author Martin
 *
 * Java class for creating matrixes and performing matrix operations
 */

public class Matrix {

	/**
	 * datatype invariant ensures that all matrixes are at least 1x1 large
	 */
	private double[][] comps;
	private int width;
	private int height;
	
	/**
	 * Creates an identity matrix that does not perform any transformations
	 * when multiplied with other transformation matrixes or vectors
	 */
	public Matrix(int width, int height){
		if(width <= 0 || height <= 0){
			throw new IllegalArgumentException();
		}
		
		this.width = width;
		this.height = height;
		
		comps = new double[width][height];
		
		for(int i = 0; i < width && i < height; i++)
			comps[i][i] = 1;
	}
	
	/**
	 * Creates a 4x4 matrix for translations or other transformations
	 * depending on the variable Type t
	 * 
	 * Returns an identity matrix by default
	 */
	public Matrix(double x, double y, double z, Type t)
	{
		this(4, 4);
		
		if(t.equals(Type.T)){
			comps[3][0] = x;
			comps[3][1] = y;
			comps[3][2] = z;
		}
	}
	
	/**
	 * Creates a 4x4 matrix for rotation about a coordinate axis or perspective
	 * scaling by a variable factor, depending on Type r
	 * 
	 * (with the addition of Quaternions for rotation computation, rotation
	 * matrixes are no longer used except for cosmetic purposes)
	 */
	public Matrix(double theta, Type r)
	{
		this(4, 4);
		
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		
		switch(r){
			case Z: comps[0][0] = cos;
			        comps[1][0] = -sin;
			        comps[0][1] = sin;
			        comps[1][1] = cos;
			        break;
			        
			case X: comps[1][1] = cos;
			        comps[2][1] = -sin;
			        comps[1][2] = sin;
			        comps[2][2] = cos;
			        break;
			        
			case Y: comps[0][0] = cos;
			        comps[2][0] = sin;
			        comps[0][2] = -sin;
			        comps[2][2] = cos;	
			        break;
			
			case P: comps[0][3] = theta;
					comps[3][3] = 0;
					break;
					
			case S: comps[0][0] = theta;
				    comps[1][1] = theta;
				    comps[2][2] = theta;
				    break;
				    
			default: break;
		}		
	}
	
	/**
	 * Enum holding the types of matrixes that can be created
	 * 
	 * X, Y, Z: rotation around X, Y, or Z axis
	 * T: translation 
	 * P: perspective scaling with an adjustment factor
	 */
	public enum Type{X, Y, Z, T, P, S;}
	
	/**
	 * Creates a matrix with the component array given
	 * 
	 * Creates a 1x1 identity matrix by default
	 */
	public Matrix(double[][] comps)
	{
		if(comps.length > 0 && comps[0].length > 0){
			this.comps = comps;
			this.width = comps.length;
			this.height = comps[0].length;
		}
		else{
			this.comps = new double[][]{{1.0}};
			this.width = 1;
			this.height = 1;
		}
	}
	
	/**
	 * Gets a copy of a matrix's component array
	 */
	public double[][] getComps()
	{
		double[][] result = new double[width][height];
		
		for(int x = 0; x < result.length; x++)
			for(int y = 0; y < result[x].length; y++)
				result[x][y] = comps[x][y];
		
		return result;
	}
	
	//methods to get matrix's dimensions
	public int getWidth(){return width;}
	
	public int getHeight(){return height;}
	
	/**
	 * Multiplies two matrixes together according to matrix operations rules
	 * and returns a new matrix
	 * 
	 * throws IllegalArgumentException of matrixes are of incompatible sizes
	 */
	public Matrix multiply(Matrix other)
	{	
		//checks to see if matrixes can be multiplied
		if(other == null)
			throw new IllegalArgumentException();
		
		if(width != other.getHeight())
			throw new IllegalArgumentException();
		
		double[][] otherComps = other.getComps();
		
		double[][] result = new double[other.getWidth()][height];
		
		//perform dot products over rows of this and collumns of other
		for(int x = 0; x < result.length; x++)
			for(int y = 0; y < result[x].length; y++){
				double product = 0;
				
				for(int i = 0; i < width; i++)
					product += comps[i][y]*otherComps[x][i];
				
				result[x][y] = product;
			}
		
		return new Matrix(result);
	}
	
	/**
	 * Multiplies a vector by this matrix in order to perform transformations
	 */
	public Vector multiply(Vector v)
	{
		double tempX = v.getX();
		double tempY = v.getY();
		double tempZ = v.getZ();
		
		double x = tempX*comps[0][0] + tempY*comps[1][0] + tempZ*comps[2][0] 
				+ comps[3][0];
		
		double y = tempX*comps[0][1] + tempY*comps[1][1] + tempZ*comps[2][1] 
				+ comps[3][1];
		
		double z = tempX*comps[0][2] + tempY*comps[1][2] + tempZ*comps[2][2] 
				+ comps[3][2];
		
		return new Vector(x, y, z);
	}
	
	//Used for debugging
	public String toString(){
		String output = "";
		
		for(int y = 0; y < comps[0].length; y++){
			for(int x = 0; x < comps.length; x++){
				output += comps[x][y] + " ";
			}
			output += "\n";
		}
		output += "\n";
		return output;
	}
}
