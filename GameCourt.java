package main;
/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.*;

/**
 * GameCourt
 * 
 * This class holds the primary game logic of how different objects 
 * interact with one another.  Take time to understand how the timer 
 * interacts with the different methods and how it repaints the GUI 
 * on every tick().
 *
 * Adapted from CIS 120 UPENN
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

	private PlayerEntity player;
	
	//hashsets holding enemies and projectiles in game
	private HashSet<EnemyEntity> targets;
	private HashSet<ProjectileEntity> projectiles;
	
	//whether game is paused or instructions are being shown
	public boolean playing = false;  
	public boolean instructions = false;
	
	public int points = 0;

	//Size of game window
	public static final int COURT_WIDTH = 600;
	public static final int COURT_HEIGHT = 600;

	public static final int INTERVAL = 15; 

	public GameCourt(){
		ModelLoader.loadModels();
        
		Timer timer = new Timer(INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				tick();
			}
		});
		timer.start(); // MAKE SURE TO START THE TIMER!

		setFocusable(true);

		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){		
				//set boolean flags for what player is doing
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					player.left = true;
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					player.right = true;
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					player.dive = true;
				else if (e.getKeyCode() == KeyEvent.VK_UP)
					player.climb = true;
				else if(e.getKeyCode() == KeyEvent.VK_W)
					player.accel = true;
				else if(e.getKeyCode() == KeyEvent.VK_S)
					player.decel = true;
				else if(e.getKeyCode() == KeyEvent.VK_A)
					player.yleft = true;
				else if(e.getKeyCode() == KeyEvent.VK_D)
					player.yright = true;
				else if(e.getKeyCode() == KeyEvent.VK_SPACE)
					player.shoot = true;
				
				//reset game if player has decided to continue
				if(e.getKeyCode() == KeyEvent.VK_ENTER && playing == false){
					playing = true;			
					reset();
				}	
				
				//pause the game to show instructions
				if(e.getKeyCode() == KeyEvent.VK_H)
				{
					if(instructions)
						playing = true;
					
					instructions = !instructions;
				}
			}
			public void keyReleased(KeyEvent e){
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					player.left = false;
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					player.right = false;
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					player.dive = false;
				else if (e.getKeyCode() == KeyEvent.VK_UP)
					player.climb = false;
				else if(e.getKeyCode() == KeyEvent.VK_W)
					player.accel = false;
				else if(e.getKeyCode() == KeyEvent.VK_S)
					player.decel = false;
				else if(e.getKeyCode() == KeyEvent.VK_A)
					player.yleft = false;
				else if(e.getKeyCode() == KeyEvent.VK_D)
					player.yright = false;
				else if(e.getKeyCode() == KeyEvent.VK_SPACE)
					player.shoot = false;
			}
		});
	}

	/** (Re-)set the state of the game to its initial state.
	 */
	public void reset() {
        targets = new HashSet<EnemyEntity>();
        projectiles = new HashSet<ProjectileEntity>();

        EnemyEntity target = new EnemyEntity(Math.random()*400-200, 
        		Math.random()*400-200, Math.random()*400-200, 20, projectiles);
        
    	targets.add(target);
         
		player = new PlayerEntity(projectiles);
		
		playing = true;
		points = 0;

		requestFocusInWindow();
	}

    /**
     * This method is called every time the timer defined
     * in the constructor triggers.
     */
	void tick(){
		if (playing) {

			player.move();
			
			//iterate through all bullets in the game
			for(ProjectileEntity t: projectiles)
			{
				t.move();

				//remove bullet if it has struck an enemy, and indicate that
				//the enemy has been destroyed
				for(EnemyEntity tar: targets)
				{
					if(tar.intersects(t) && !t.isEnemy)
					{
						t.remove = true;
						tar.destroyed = true;
					}
				}
					
				//remove bullet if it has struck the player and decrement
				//player's lives
				if(player.intersects(t) && t.isEnemy && !player.destroyed)
				{
					t.remove = true;
						player.lives --;
				}
			}
			
			for(EnemyEntity t: targets)
			{
				t.move(player);
			}
			
			//Iterate through projectiles and targets to remove entities
			//that have been flagged
			for(Iterator<ProjectileEntity> t = projectiles.iterator(); t.hasNext();)
			{
				if(t.next().remove)
					t.remove();
			}
			
			for(Iterator<EnemyEntity> t = targets.iterator(); t.hasNext();)
			{
				if(t.next().remove){
					t.remove();
					
					//increase points and add a new enemy
					points += 100;
					
			        EnemyEntity target = new EnemyEntity(Math.random()*400-200, 
			        		Math.random()*400-200, Math.random()*400-200, 
			        		20, projectiles);
			    	targets.add(target);
				}
					
			}
			
			//code for player death
			if(player.lives < 0)
				player.destroyed = true;
			
			if(player.remove)
			{
				playing = false;
			}
			
			// update the display
			repaint();
		} 
	}

	@Override 
	public void paintComponent(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, COURT_WIDTH, COURT_HEIGHT);
		
		//displaced graphics context at center of screen, with y axis inverted
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(COURT_WIDTH/2, COURT_HEIGHT/2-1);
		g2.scale(1, -1);
		
		//location of the camera with respect to the player location and model
		Vector camLoc = new Vector(-30, 0, 20);
		camLoc = player.orient.createMatrix().multiply(camLoc);

		//Matrix indicating how all polygons in the world should be transated
		//so the camera is at (0, 0, 0)
		Matrix flyerTrans = new Matrix(-(player.pos_x + camLoc.getX()), 
				-(player.pos_y + camLoc.getY()), 
				-(player.pos_z + camLoc.getZ()), Matrix.Type.T);
		
		//Matrix indicating how all polygons should be rotated so player model
		//is pointing straight
		Matrix camRot = player.orient.invert().createMatrix();

		Matrix cam = camRot.multiply(flyerTrans);
		
		//draw all targets and projectiles
		for(EnemyEntity t: targets)
		{
			t.draw(g2, cam);
		}
		
		for(ProjectileEntity t: projectiles)
		{
			t.draw(g2, cam);
		}
		
		player.draw(g2, cam);
		
		//draws oval for firing circle
		g2.setColor(Color.WHITE);
		g2.drawOval(-5, -8, 10, 10);
		
		//translate graphics context to draw radar
		Graphics2D gr = (Graphics2D) g.create();
		gr.translate(COURT_WIDTH/2-200, COURT_HEIGHT/2+200);
		gr.scale(1, -1);
		
		paintRadar(gr);
		
		//translate graphics context to draw roll gauge
		Graphics2D gRoll = (Graphics2D) g.create();
		gRoll.translate(COURT_WIDTH/2+200, COURT_HEIGHT/2+200);
		gRoll.scale(1, -1);
		
		paintRollGauge(gRoll);
		
		//translate graphics context to draw pitch gauge
		Graphics2D gPitch = (Graphics2D) g.create();
		gPitch.translate(COURT_WIDTH/2+200, 90);
		gPitch.scale(1, -1);
		
		paintPitchGauge(gPitch);
		
		//translate graphics context to draw health bar
		Graphics2D gHealth = (Graphics2D) g.create();
		gHealth.translate(10, 20);
		
		paintHealthBar(gHealth);
		
		//translate graphics context to draw speed bar
		Graphics2D gSpeed = (Graphics2D) g.create();
		gSpeed.translate(10, 100);
				
		paintSpeedBar(gSpeed);
		
		//drawing code for score screen and instruction screen
		if(!playing){
			g.setColor(Color.black);
			g.fillRect(200, 200, 200, 100);
			g.setColor(Color.white);
			g.drawRect(200, 200, 200, 100);
			g.drawString("Points: " + points, 220, 250);
			g.drawString("Press <Enter> to continue", 220, 270);
		}
		
		if(instructions)
		{
			
			g.setColor(Color.black);
			g.fillRect(100, 100, 400, 300);

			g.setColor(Color.white);
			g.drawString("Instructions:", 120, 125);
			g.drawString("Use <A> and <D> to turn left or right", 120, 150);
			g.drawString("Use <W> and <S> to speed up or slow down", 120, 175);
			g.drawString("Use <Left> and <Right> to roll left or right", 120, 200);
			g.drawString("Use <Up> and <Down> to climb up or down", 120, 225);
			g.drawString("Use <Space> to shoot", 120, 250);
			g.drawString("This is a 3D-dogfighting simulation with 3D graphics", 120, 275);
			g.drawString("and movement. Enemy AI will spawn randomly and try", 120, 300);
			g.drawString("to track and shoot you down.", 120, 325);
			g.drawRect(100, 100, 400, 300);
			
			playing = false;
		}
	}

	//code for drawing radar
	private void paintRadar(Graphics g){
		int radius = 75;
		int objRadius = 2;
		
		//draw radar circle and player location
		g.setColor(Color.WHITE);		
		g.drawOval(-radius, -radius, 2*radius, 2*radius);
		
		g.setColor(Color.GREEN);
		g.drawOval(-objRadius, -objRadius, objRadius*2, objRadius*2);
		
		g.setColor(Color.RED);
		
		//iterate through all the enemies and draw in radar
		for(Entity i: targets){
			double distance = Math.sqrt(Math.pow(i.pos_x-player.pos_x, 2) + 
					Math.pow(i.pos_y - player.pos_y, 2));
			double theta = Math.atan2(-i.pos_y+player.pos_y, 
					i.pos_x - player.pos_x);
			
			if(distance > 750)
				distance = 750;
			
			double xc = distance*Math.cos(theta)/10;
			double yc = distance*Math.sin(theta)/10;
				
			//rotate radar screen depending on player's orientation
			double dtheta = -player.orient.getPsi()-Math.PI/2;
			
			double rxc = xc*Math.cos(dtheta)-yc*Math.sin(dtheta);
			double ryc = xc*Math.sin(dtheta)+yc*Math.cos(dtheta);
			
			g.drawOval((int)rxc-objRadius, (int)ryc-objRadius, 
					2*objRadius, 2*objRadius);
		}
	}

	private void paintRollGauge(Graphics g){
		int radius = 75;
		
		g.setColor(Color.WHITE);		
		g.drawOval(-radius, -radius, 2*radius, 2*radius);
		
		double theta = player.orient.getPhi();
		
		double x1 = -60*Math.cos(theta);
		double x2 = 60*Math.cos(theta);
		
		double y1 = -60*Math.sin(theta);
		double y2 = 60*Math.sin(theta);
		
		double x3 = -20*Math.sin(theta);
		double y3 = 20*Math.cos(theta);
		
		g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
		g.drawLine(0, 0, (int)x3, (int)y3);
	}
	
	private void paintPitchGauge(Graphics g)
	{
		int radius = 75;
		
		g.setColor(Color.WHITE);
		g.drawOval(-radius, -radius, 2*radius, 2*radius);
		
		double theta = -player.orient.getTheta();
		
		double x1 = -60*Math.cos(theta);
		double x2 = 60*Math.cos(theta);
		
		double y1 = -60*Math.sin(theta);
		double y2 = 60*Math.sin(theta);
		
		double x3;
		double y3;
		
		if(player.orient.getPhi() < Math.PI/2 && player.orient.getPhi() > -Math.PI/2){
			x3 = -60*Math.cos(theta) - 15*Math.sin(theta);
			y3 = -60*Math.sin(theta) + 15*Math.cos(theta);
		}
		else{
			x3 = -60*Math.cos(theta) + 15*Math.sin(theta);
			y3 = -60*Math.sin(theta) - 15*Math.cos(theta);
		}
		
		g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
		g.drawLine((int)x1, (int)y1, (int)x3, (int)y3);
	}
	
	private void paintHealthBar(Graphics g){
		g.setColor(Color.white);
		g.drawString("Health", 0, 0);
		
		g.drawString("Points: " + points, 0, 35);
		
		g.drawString("Press <H> for instructions", 0, 50);
		
		g.setColor(Color.green);
		g.fillRect(0, 10, player.lives*2, 10);
	}
	
	private void paintSpeedBar(Graphics g)
	{
		g.setColor(Color.white);
		g.drawString("Speed", 0, 0);
		
		g.fillRect(0, 15, 15, (int)(player.speed*100));
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(COURT_WIDTH,COURT_HEIGHT);
	}
}
