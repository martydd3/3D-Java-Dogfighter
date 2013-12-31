//Martin Deng 2013
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class ModelLoader {

	//models to be used in the game
	public static Polygon[] flyer, bullet, explosion;
	
	/** 
	 * Input file holds model data in the following format
	 * 
	 * 2
	 * -10 -10 -10
	 * -10 10 -10
	 * -10 10 10
	 * -10 -10 -10
	 * -10 10 10
	 * -10 -10 10
	 * 
	 * First line contains the number of faces
	 * Every 3 lines afterwards contains the coordinates of 3 vertices in 
	 * a face
	 */
	
	//load files at the beginning of the game
	public static void loadModels(){
		flyer = loadFile("flyer.txt");
		bullet = loadFile("bullet.txt");
		explosion = loadFile("explosion.txt");
	}
	
	//Load an array of polygons from a file with name fileName
	private static Polygon[] loadFile(String fileName)
	{
		try {
			//create a new reader and an array of polygons to store
			//the alues read
			BufferedReader reader = new BufferedReader(
					new FileReader(fileName));
			
			int faces = Integer.parseInt(reader.readLine());
			
			Polygon[] poly = new Polygon[faces];
			
			for(int i = 0; i < faces; i++)
			{
				//create a new Vector[] for a polygon
				Vector[] v = new Vector[3];

				//read in the values to create 3 Vectors
				for(int j = 0; j < 3; j++)
				{
					StringTokenizer components = new StringTokenizer(
							reader.readLine());
					
					double x = Double.parseDouble(components.nextToken());
					double y = Double.parseDouble(components.nextToken());
					double z = Double.parseDouble(components.nextToken());
					
					v[j] = new Vector(x, y, z);
				}
				
				poly[i] = new Polygon(v);
			}
			
			reader.close();
			
			return poly;
			
		} catch (IOException ex) {
			System.out.println("unable to load \"standard\" model");
			return new Polygon[0];
		}
	}
	
}
