
public class Case {
	//case du plateau--------
	public int [] position;
	public int chiffre;
	public String type;
	public Maison [] maisons = new Maison[4]; //jamais utilisé
	public Route [] routes = new Route[4]; //jamais utilisé
	public boolean voleur = false;
	
	Case(int i, int j, String type, int chiffre){
		this.position = new int[]{i,j};
		this.type = type;
		this.chiffre = chiffre;
	}
	
	Case(int i, int j, String type, int chiffre, boolean voleur){
		this(i,j,type,chiffre);
		this.voleur=voleur;
	}
}