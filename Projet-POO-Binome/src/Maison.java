
public abstract class Maison {
	//parent de colonie et ville
	public int [] position;
	public Player joueur;
	
	Maison(int [] p1, Player joueur){
		this.position = p1;
		this.joueur = joueur;
	}
}
