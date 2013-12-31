package main;
//Martin Deng 2013

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;

public class PlayerEntity extends Entity{
	
	//booleans indicating what action the plane is taking
	public boolean left, right, climb, dive, shoot, accel, decel, yleft, yright;
	
	//set of projectiles where PlayerEntity adds new bullets when shooting
	private HashSet<ProjectileEntity> projectiles;
	
	//booleans indicating whether plane has been destroyed
	public boolean destroyed, control;
	
	//Variables for display purposes
	private int animationCount = 100;
	public Color color;
	
	public int lives = 100;
	
	//constants for speed and turning speed
	private static final double MAX_SPEED = 3;
	private static final double MIN_SPEED = 1;
	private static final double MAX_ROLL = 0.075;
	private static final double MAX_PITCH = 0.03;
	private static final double MAX_YAW = 0.015;
	
	public PlayerEntity(HashSet<ProjectileEntity> projectiles)
	{
		super(0, 0, 0, 1, 10, Quaternion.createRotate(0, 1, 0, 0), 
				new Model(ModelLoader.flyer));
		
		color = Color.blue;
		
		left = false;
		right = false;
		climb = false;
		dive = false;
		shoot = false;
		accel = false;
		decel = false;
		yleft = false;
		yright = false;
		
		destroyed = false;
		control = true;
		
		this.projectiles = projectiles;
	}
	
	public void move()
	{
		//controls rolling motion
		if(left && !right && control){
			rollSpeed = MAX_ROLL;
			model.roll = 0.3;
		}
		else if(!left && right && control){
			rollSpeed = -MAX_ROLL;
			model.roll = -0.3;
		}
		else{
			rollSpeed = 0;
			model.roll = 0;
		}
		
		//controls climbing and diving
		if(climb && !dive && control){
			pitchSpeed = -MAX_PITCH;
			model.pitch = -0.3;
		}
		else if(!climb && dive && control){
			pitchSpeed = MAX_PITCH;
			model.pitch = 0.3;
		}
		else{
			pitchSpeed = 0;
			model.pitch = 0;
		}
		
		//controls turning
		if(yleft && !yright && control){
			yawSpeed = -MAX_YAW;
			model.yaw = -0.1;
		}
		else if(yright && !yleft && control){
			yawSpeed = MAX_YAW;
			model.yaw = 0.1;
		}
		else{
			yawSpeed = 0;
			model.yaw = 0;
		}
		
		//controls acceleration and deceleration
		if(accel && !decel && control)
			speed += 0.1;
		else if(decel && !accel && control)
			speed -= 0.1;
		
		//ensures speed is within bounds
		if(speed > MAX_SPEED)
			speed = MAX_SPEED;
		if(speed < MIN_SPEED)
			speed = MIN_SPEED;
		
		//shoot
		if(shoot)
			shoot();

		//code for when plane has been destroyed
		if(destroyed)
		{
			model = new Model(ModelLoader.explosion);
			color = Color.YELLOW;
			animationCount --;
			control = false;
			
			model.scale = 0.01*(100-animationCount)*(100-animationCount);
			
			if(animationCount <= 0)
				remove = true;
		}	
		super.move();
	}
	
	//create a new bullet with the proper location and orientation and add
	//to the set of bullets
	public void shoot()
	{
		Vector spawnPoint = new Vector(10, 0, 0);
		spawnPoint = orient.createMatrix().multiply(spawnPoint);

		ProjectileEntity bullet = new ProjectileEntity(spawnPoint.getX()+pos_x, 
				spawnPoint.getY()+pos_y, spawnPoint.getZ()+pos_z, orient, false);

		projectiles.add(bullet);
	}
	
	public void draw(Graphics g, Matrix camera){
		super.draw(g, camera, color);
	}
}
