
import java.util.Arrays;

public class Jeu {
	//gère le déroulement du jeu
	public Player [] joueurs;
	public int courant;
	public Plateau plateau;
	public Player routeLaPlusLongue;
	public Player plusGrandeArmee;
	
	Jeu(String [] joueurs){
		this.plateau = new Plateau();
		this.joueurs = new Player[joueurs.length];
		for(int i=0; i<joueurs.length; i++) {
			if(joueurs[i] == "Humain") this.joueurs[i] = new Joueur(plateau,this);
			else this.joueurs[i] = new Machine(plateau,this);
		}
	}
	
	Jeu(String s){ 
		this.plateau = new Plateau();
		if(s == "o") { //Joueur Joueur Joueur IA
			joueurs = new Player[]{new Joueur(plateau,this), new Joueur(plateau,this), new Joueur(plateau,this), new Machine(plateau,this)};
		} else { //Joueur Joueur Joueur Joueur
			joueurs = new Player[]{new Joueur(plateau,this), new Joueur(plateau,this), new Joueur(plateau,this), new Joueur(plateau,this)};
		}
	}
	
	public void nextPlayer() { //actualise le joueur suivant
		if(courant != joueurs.length-1) courant++;
		else courant = 0;
	}
	
	public void distribution(int roll) {
		Case [] cases = plateau.trouveCases(roll); //cases trouvées
		
		/* if(cases[0] != null) { //test case trouvées
			System.out.println(cases[0].type +" "+ cases[0].chiffre);
		}
		if(cases[1] != null) {
			System.out.println(cases[1].type +" "+ cases[1].chiffre);
		} */
		
		Maison [] m1s = cases[0] == null ? new Maison[4] : plateau.trouveMaisons(cases[0]); //maisons qui touchent la case 1
		Maison [] m2s = cases[1] == null ? new Maison[4] : plateau.trouveMaisons(cases[1]); //maisons qui touchent la case 2
		
		for(Maison m : m1s) {
			if(m != null) {
				m.joueur.add(cases[0].type);
				if(m instanceof Ville) m.joueur.add(cases[0].type);
			}
		}
		
		for(Maison m : m2s) {
			if(m != null) {
				m.joueur.add(cases[1].type);
				if(m instanceof Ville) m.joueur.add(cases[1].type);
			}
		}
	}
	
	public void monopole(String type) {
		int compteur=0;
		for(int i=0; i<joueurs.length; i++) {
			if(i != courant) {
				while(joueurs[i].defausseCartes(type) != null) {
					joueurs[i].cartes = joueurs[i].defausseCartes(type);
					compteur++;
				}
			}
		}
		String [] recolte = new String[compteur];
		Arrays.fill(recolte, type);
		joueurs[courant].add(recolte);
	}
}

/*

à faire:

	gérer le système de tours (x)
	
	faire quelque chose de la classe carte (x)
	-build route, maisons (x)
	-recevoir des cartes quand on gagne (x)
	
	gérer les ports (x)
	gérer l'IA



*/

