//Martin Deng 2013

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;

public class EnemyEntity extends Entity{

	//indicates if the enemy has been destroyed and is currently going through
	//a death animation
	public boolean destroyed = false;
	
	//Variables used for displaying models
	private Model explosion = new Model(ModelLoader.explosion);
	private Color color = Color.RED;
	private int animationCount = 100;
	
	private HashSet<ProjectileEntity> projectiles;
	
	//constants for speed and turning speed
	private static final double MAX_SPEED = 1;
	private static final double MAX_ROLL = 0.05;
	private static final double MAX_PITCH = 0.02;
	
	public EnemyEntity(double pos_x, double pos_y, double pos_z,
			double radius, HashSet<ProjectileEntity> projectiles) {
		
		super(pos_x, pos_y, pos_z, MAX_SPEED, radius, 
				Quaternion.createRotate(0, 1, 0, 0), 
				new Model(ModelLoader.flyer));

		this.projectiles = projectiles;		
	}

	public void draw(Graphics g, Matrix camera){
		super.draw(g, camera, color);
	}
	
	public void move(PlayerEntity player)
	{
		if(destroyed)
		{
			model = explosion;
			speed = 0;
			color = Color.YELLOW;
			animationCount --;
			
			//change scale of explosion model for animation
			model.scale = 0.005*(100-animationCount)*(100-animationCount);
			
			//if end of animation has been reached, indicate for game to
			//remove this Entity
			if(animationCount <= 0)
				remove = true;
		}
		else{
			//reinitialize turning speeds
			rollSpeed = 0;
			pitchSpeed = 0;
			
			//create a vector representing the location of the player with 
			//respect to this entity
			Vector playerPos = new Vector(player.pos_x-pos_x, 
					player.pos_y-pos_y, player.pos_z-pos_z);
			
			Vector playerPosTrans = 
					orient.invert().createMatrix().multiply(playerPos);
			
			double rollAngle = 
					Math.atan2(playerPosTrans.getZ(), playerPosTrans.getY());
			
			double pitchAngle = 
					Math.atan2(playerPosTrans.getZ(), playerPosTrans.getX());
			
			double firingAngle = 
					Math.atan2(Math.sqrt(Math.pow(playerPosTrans.getY(),2) +
					Math.pow(playerPosTrans.getZ(), 2)), playerPosTrans.getX());
			
			//use rollAngle and pitchAngle to determine in what direction
			//the plane needs to turn along it's x and y axis
			if(pitchAngle > 0.01)
				pitchSpeed = -MAX_PITCH;
			else if(pitchAngle < -0.01)
				pitchSpeed = MAX_PITCH;

			if(rollAngle > -Math.PI/2 + 0.01 && rollAngle < Math.PI/2 - 0.01)
				rollSpeed = -MAX_ROLL;
			else if(rollAngle < -Math.PI/2-0.01 || rollAngle > Math.PI/2 + 0.01)
				rollSpeed = MAX_ROLL;
			
			//if player is with 0.2 rad of the front of this entity, shoot
			if(firingAngle < 0.2)
				shoot();
			
		}		
		super.move();
	}
	
	//add a suitable bullet to the set of bullets in the tame
	public void shoot()
	{
		//create a new bullet in front of the plane, with the same orientation
		Vector spawnPoint = new Vector(10, 0, 0);
		spawnPoint = orient.createMatrix().multiply(spawnPoint);

		ProjectileEntity bullet = new ProjectileEntity(spawnPoint.getX()+pos_x, 
				spawnPoint.getY()+pos_y, spawnPoint.getZ()+pos_z, orient, true);

		projectiles.add(bullet);
	}
}
