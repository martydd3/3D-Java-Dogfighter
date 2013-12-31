package main;
import java.awt.Color;
import java.awt.Graphics;

/** An object in the game. 
 *
 *	Adapted from GameObj.java
 *
 *  Entities all have a position position (x, y, z) and a quaternion containing
 *  its orientation
 *  
 *  For collision detection, objects have a bounding circle and radius.
 *  Collision detection compares the distance between two objects and the sum
 *  of their radii
 *  
 */
public class Entity {

	/** Current position of the object (in terms of Cartesian coordinates)
	 */
	public double pos_x; 
	public double pos_y;
	public double pos_z;
	
	/** Size of object's collision sphere */
	public double radius;
	
	/** Object's speed and turning speeds */
	public double speed;
	public double pitchSpeed = 0;
	public double rollSpeed = 0;
	public double yawSpeed = 0;
	
	/** Orientation of the object*/
	public Quaternion orient;
	
	public Model model;
	
	/** Indicates if the object needs to be removed in the future*/
	public boolean remove = false;
	
	/**
	 * Constructor;
	 */
	public Entity(double pos_x, double pos_y, double pos_z, 
			      double speed, double radius, Quaternion orient, Model model)
	{
		
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.pos_z = pos_z;

		this.speed = speed;
		this.radius = radius;

		//default orientation pointing in the x axis
		this.orient = orient;
		this.model = model;
	}

	/**
	 * updates the object's position and orientation depending on its
	 * speed, turnSpeed, and rollSpeed
	 * 
	 * speed determines how fast it moves along its tangent vector
	 * 
	 * turnSpeed determines how fast the object changes pitch
	 * 
	 * rollSpeed determines how fast the object rolls
	 */
	public void move(){
		
		//matrix holding default coordinates for 
		//tangent and binormal unit vector
		Vector tan = new Vector(1, 0, 0);
		Vector bin = new Vector(0, 1, 0);
		Vector nor = new Vector(0, 0, 1);
		
		//multiply unit vectors by a rotation matrix created from the 
		//orientation quaternion to get the new binormal and tangent
		//unit vectors
		tan = orient.createMatrix().multiply(tan);
		bin = orient.createMatrix().multiply(bin);
		nor = orient.createMatrix().multiply(nor);
		
		
		//increment the position in the direction of the tangent vector
		pos_x += speed*tan.getX();
		pos_y += speed*tan.getY();
		pos_z += speed*tan.getZ();
		
		//create a quaternion representing a rotation around the binormal
		//represents a change in object's pitch
		Quaternion pitchChange = Quaternion.createRotate(
				pitchSpeed, bin.getX(), bin.getY(), bin.getZ());
		
		//create a quaternion representing a rotation around the tangent
		//represents a change in object's roll
		Quaternion rollChange = Quaternion.createRotate(
				rollSpeed, tan.getX(), tan.getY(), tan.getZ());
		
		Quaternion yawChange = Quaternion.createRotate(
				yawSpeed, nor.getX(), nor.getY(), nor.getZ());
		
		//update the orientation by multiplying the orientations together
		orient = pitchChange.multiply(yawChange.multiply(rollChange.multiply(orient)));
	}

	/**
	 * Determine whether this game object is currently intersecting
	 * another object.
	 * 
	 * Intersection is determined by comparing object's hit circle. 
	 * If the distance between the two objects is less than the sum of their
	 * radii, a collision has occurred
	 */
	public boolean intersects(Entity obj){
		double distance = Math.sqrt(
				Math.pow(pos_x-obj.pos_x, 2) + Math.pow(pos_y-obj.pos_y, 2)
				+ Math.pow(pos_z - obj.pos_z, 2));
		
		return distance <= (radius + obj.radius);
	}
	
	/**
	 * Default draw method that provides how the object should be drawn 
	 * in the GUI. This method does not draw anything. Subclass should 
	 * override this method based on how their object should appear.
	 */
	public void draw(Graphics g, Matrix c, Color color) {
		
		Matrix translate = new Matrix(pos_x, pos_y, pos_z, Matrix.Type.T);
		Matrix rotate = orient.createMatrix();
		
		Matrix world = translate.multiply(rotate);
		
		Matrix trans = c.multiply(world);
		
		model.draw(g, trans, color);
	}
}