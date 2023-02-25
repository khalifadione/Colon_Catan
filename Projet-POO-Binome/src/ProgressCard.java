import java.util.Random;

public class ProgressCard {
	//carte de progrès (chevalier, etc)
	public String type;
	
	ProgressCard(String type){
		this.type = type;
	}
	
	private static boolean isBetween(int r, int lower, int upper) {
		  return lower <= r && r <= upper;
	}
	
	public static ProgressCard getProgressCard() {
		 // return new ProgressCard("Abondance");
		
		 
		
		Random rand = new Random();
		Integer r = rand.nextInt(25);
		if(isBetween(r,0,13)) return new ProgressCard("Chevalier");
		if(isBetween(r,14,18)) return new ProgressCard("Point de Victoire");
		if(isBetween(r,19,20)) return new ProgressCard("Route Gratuite");
		if(isBetween(r,21,22)) return new ProgressCard("Abondance");
		return new ProgressCard("Monopole"); 
		
		 
		
	}
}
