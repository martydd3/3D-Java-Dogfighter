//Martin Deng 2013
import java.awt.Color;
import java.awt.Graphics;


public class ProjectileEntity extends Entity{

	//speed and size of every bullet
	private static double speed = 10;
	private static double radius = 1;

	//bullets disappear after 50 loops of the tame
	public int lifeTime = 50;
	
	//indicates if bullet is enemy or friendly
	public boolean isEnemy;
	
	public ProjectileEntity(double x, double y, double z, Quaternion orient, boolean isEnemy)
	{
		super(x, y, z, speed, radius, orient, new Model(ModelLoader.bullet));
		this.isEnemy = isEnemy;
	}
	
	public void draw(Graphics g, Matrix camera){
		if(isEnemy)
			super.draw(g, camera, Color.red);
		else
			super.draw(g, camera, Color.green);
	}
	
	public void move()
	{
		//remove bullet once lifetime has run out
		lifeTime--;
		if(lifeTime <= 0)
			remove = true;
		super.move();
	}
	
}
