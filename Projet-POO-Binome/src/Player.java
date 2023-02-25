import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
/*
 * 
 * ATTENTION A UTILISER ARRAY.EQUALS() POUR COMPARER DES INT[] ET PAS P1 == P2
 * 
 */

public abstract class Player {
	//parent de joueur humain et joueur IA
	public Plateau plateau;
	public Jeu jeu;
	public ArrayList<Maison> maisons = new ArrayList<>();
	public ArrayList<Route> routes = new ArrayList<>();
	public HashMap<String,ArrayList<Carte>> cartes = new HashMap<>();
	public ArrayList<ProgressCard> progressCards = new ArrayList<>();
	public ArrayList<ProgressCard> progressCardsDevo = new ArrayList<>(); //chevalier et PV sortis
	public int nbChevaliers = 0;
	public int routeLaPlusLongue = 0;
	public ProgressCard currentProgressCard;
	public int [] etatProgressCards = new int[2]; //permet d'éviter qu'une progress card achetée le tour même soit utilisée
	public int nbPV = 0;
	public int nbElements = 0; //nombre d'éléments (route, ville) posés
	public Colonie derniereColonie;

	
	Player(Plateau p, Jeu j){
		this.plateau=p;
		this.jeu=j;
		this.add("Forêt", "Colline", "Pré", "Champs", "Montagne");
		this.add("Forêt", "Colline", "Forêt", "Colline", "Pré", "Champs", "Champs", "Montagne", "Montagne", "Montagne"); // à supprimer lbl
		
	}
	
	public boolean check(int [] p1) {//check si les coordonnées ont un sens
		if(p1.length != 2) return false;
		if(p1[0] < 0 || p1[0] > 4) return false;
		if(p1[1] < 0 || p1[1] > 4) return false;
		return true;
	}
	
	public boolean check(int [] p1, int [] p2) {
		if(!this.check(p1) || !this.check(p2)) return false;
		if(p1[0] == p2[0]) { //Route H
			if(Math.abs(p1[1]-p2[1]) == 1) return true;
		}
		if(p1[1] == p2[1]) { //Route V
			if(Math.abs(p1[0]-p2[0]) == 1) return true;
		}
		return false;
	}
	
	public boolean checkProx(int [] p1) {//check si le build d'une colonie est possible
		int x = p1[0]; //~~~~~ inverse x et y
		int y = p1[1]; //~~~~~
		
		try {
			if(plateau.maisons[x-1][y] != null) return false;
		} catch (Exception e) {} //out of bounds
		try {
			if(plateau.maisons[x+1][y] != null) return false;
		} catch (Exception e) {}
		try {
			if(plateau.maisons[x][y-1] != null) return false;
		} catch (Exception e) {}
		try {
			if(plateau.maisons[x][y+1] != null) return false;
		} catch (Exception e) {}
		
		return true;
	}
	
	public void add(String...types) {
		for(String type : types) {
			this.add(type);
		}
	}
	
	public void add(String type) {
		if(cartes.get(type)!=null) {
			ArrayList<Carte> cs = cartes.get(type); //Chope array carte sous le label "Forêt" (etc.)
			cs.add(new Carte(type));
		} else {
			ArrayList<Carte> cs = new ArrayList<>();
			cs.add(new Carte(type));
			cartes.put(type, cs);
		}
	}
	
	//

	public boolean debute() { //teste si un joueur n'a pas placé tous ses éléments de départ
		for(int i = 0; i<5; i++) {
			for(int j = 0; j<5; j++) {
				if(nbElements < 4) {
						return true;
				}
			}
		}
		return false;
	}
	
	public static int roll() { //jette un dé
		Random rand = new Random();
		return rand.nextInt(11)+1;
	}
	
	public boolean estLibreMaison(int [] p1) { //vérifie si on peut construire une colonie à cet endroit
		if(!this.check(p1)) return false; //check si les coordonnées c'est pas -2,9
		if(plateau.maisons[p1[0]][p1[1]] != null) return false; //check si y a pas une maison construite à cet endroit
		if(!checkProx(p1)) return false; //check si y a pas de maison à côté
		if(this.debute()) return true; //check si le joueur débute (pas besoin d'avoir une route adjacente)
		for(Route r : routes) {
			if(Arrays.equals(r.position1, p1) || Arrays.equals(r.position2, p1)) return true;
		}
		return false;
	}
	
	public boolean estLibreVille(int [] p1) {
		for(Maison m : maisons) {
			if(Arrays.equals(m.position, p1)) {
				if(m instanceof Colonie) return true;
				else return false;
			}
		}
		return false;
	}
	
	public boolean estLibreRoute(int [] p1, int [] p2) {
		if(!this.check(p1,p2)) return false;
		if(plateau.routes[p1[0]][p1[1]][p2[0]][p2[1]] != null) return false; //check si l'espace est déjà utilisé
		for(Maison m : maisons) { //adjacent à une maison
			if(Arrays.equals(m.position, p1) || Arrays.equals(m.position, p2)) return true;
		}
		for(Route r : routes) { //adjacent à une route
			if(Arrays.equals(r.position1, p1) || Arrays.equals(r.position1, p2) || Arrays.equals(r.position2, p1) || Arrays.equals(r.position2, p2) ) return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String,ArrayList<Carte>> defausseCartes(String... types) { //retire cartes demandées
		HashMap<String,ArrayList<Carte>> cartes0 = (HashMap<String,ArrayList<Carte>>) cartes.clone();
		for(String type : types) {
			try {
				cartes0.put(type, (ArrayList<Carte>)(cartes.get(type).clone()));
			} catch (Exception e) {}
		}
		for(String type : types) {
			if(cartes0.get(type) == null || cartes0.get(type).isEmpty()) return null;
			else cartes0.get(type).remove(0);
		}
		return cartes0; //renvoie copie défaussée, null si défaussage impossible
	}
	
	public boolean buildColonie(int [] p1) { //construit une colonie
		if(!estLibreMaison(p1)) return false;
		
		HashMap<String,ArrayList<Carte>> cartes0 = this.defausseCartes("Forêt", "Colline", "Pré", "Champs");
		if(cartes0 == null) return false;
		else {
			cartes = cartes0;
			Colonie c = new Colonie(p1, this);
			maisons.add(c);
			plateau.maisons[p1[0]][p1[1]] = c;
			derniereColonie = c;
			nbPV++;
			return true;
		}
	}
	
	public boolean buildVille(int [] p1) {
		if(!estLibreVille(p1)) return false;
		HashMap<String,ArrayList<Carte>> cartes0 = this.defausseCartes("Champs", "Champs", "Montagne", "Montagne", "Montagne");
		if(cartes0 == null) return false;
		else {
			cartes = cartes0;
			Maison oldColonie = null;
			for(Maison m : maisons) {
				if(Arrays.equals(m.position, p1)) oldColonie = m;
			}
			maisons.remove(oldColonie);
			Ville v = new Ville(p1, this);
			maisons.add(v);
			plateau.maisons[p1[0]][p1[1]] = v;
			nbPV++;
			return true;
		}
	}
	
	public boolean buildRoute(int [] p1, int [] p2) {
		if(!estLibreRoute(p1, p2)) return false;	
		HashMap<String,ArrayList<Carte>> cartes0 = this.defausseCartes("Forêt", "Colline");
		if(cartes0 == null) return false;
		else {
			cartes = cartes0;
			Route r = new Route(p1, p2, this);
			routes.add(r);
			plateau.routes[p1[0]][p1[1]][p2[0]][p2[1]] = r;
		//	System.out.println("XXXXXXXXXXXXXXXXXXXXXXX"); //ww
			routeLaPlusLongue = routeLaPlusLongue();
			return true;
		}
	}
	
	public ArrayList<Route> routesQuiTouchent(int [] p1) {
	//	System.out.println(p1[0] + " " + p1[1]); //ww
		ArrayList<Route> routesQuiTouchent = new ArrayList<>();
		for(Route r : routes) {
			if(Arrays.equals(r.position1, p1) || Arrays.equals(r.position2, p1)) {
		//		System.out.println(r.position1[0] + " " + r.position1[1] + " " + r.position2[0]+ " " + r.position2[1]); //ww
				routesQuiTouchent.add(r);
			}
		}
	//	System.out.println(routesQuiTouchent.size() + "-----------------"); //ww
		
		return routesQuiTouchent;
	}
	
	public int routeLaPlusLongue() {
		int max = 0;
		int longueurRoute1;
		int longueurRoute2;
		for(Route r : routes) {
			longueurRoute1 = routeLaPlusLongue(r, r.position1, new ArrayList<Route>());
			longueurRoute2 = routeLaPlusLongue(r, r.position2, new ArrayList<Route>());
			
			if(longueurRoute1 > max) max = longueurRoute1;
			if(longueurRoute2 > max) max = longueurRoute2;
		}
		
		if(max >= 5 && (jeu.routeLaPlusLongue == null || max > jeu.routeLaPlusLongue.routeLaPlusLongue)) {
			if(jeu.routeLaPlusLongue != null) jeu.routeLaPlusLongue.nbPV -= 2;
			jeu.routeLaPlusLongue = this;
			this.nbPV += 2;
		}
		
		return max;
	}
	

	public int routeLaPlusLongue(Route r, int [] p1, ArrayList<Route> routesParcourues) {
		if(routesParcourues.contains(r)) return 0;
		ArrayList<Route> routesQuiTouchent = routesQuiTouchent(p1);
		int max = 0;
		int current;
		routesParcourues.add(r);
		int [] p2 = new int[2];
		for(Route routeQuiTouche : routesQuiTouchent) {
			if(Arrays.equals(routeQuiTouche.position1, p1)) p2 = routeQuiTouche.position2;
			else p2 = routeQuiTouche.position1;  
			current = 1+routeLaPlusLongue(routeQuiTouche, p2, routesParcourues);
			if(current > max) max = current;
		}
		return max;
	}
	

	
	public void abondance(String type1, String type2) {
		this.add(type1, type2);
	}
	
	public boolean buyProgressCard() { //achète une progress card
		HashMap<String,ArrayList<Carte>> cartes0 = this.defausseCartes("Pré", "Champs", "Montagne");
		if(cartes0 == null) return false;
		else {
			cartes = cartes0;
			progressCards.add(ProgressCard.getProgressCard());
			etatProgressCards[1]++;
			return true;
		}
	}
	
	public boolean useProgressCard() {
		if(currentProgressCard != null  || etatProgressCards[0] == 0) return false;
		else {
			currentProgressCard = progressCards.get(0);
			progressCards.remove(0);
			etatProgressCards[0]--;
			return true;
		}
	}
	
	public void progressCardFinDeTour() {
		etatProgressCards[0] += etatProgressCards[1];
		etatProgressCards[1] = 0;
		if(currentProgressCard == null) return;
		if(currentProgressCard.type == "Chevalier" || currentProgressCard.type == "Point de Victoire") progressCardsDevo.add(currentProgressCard);
		currentProgressCard = null;
	}
	
	public boolean possedePort(String type) {
		for(Maison m : maisons) {
			for(Port p : plateau.ports) {
				if( ( Arrays.equals(m.position, p.position1) || Arrays.equals(m.position, p.position2) ) && p.type == type) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int quantiteEchange(String type) {
		if(this.possedePort(type)) return 2;
		if(this.possedePort("Général")) return 3;
		else return 4;
	}
	
	public boolean echange(String type1, String type2) { //échange des ressources avec la banque
		if(type1 == null || type2 == null) return false;
		if(type1 == type2) return false;
		String [] tableauCartes = new String[quantiteEchange(type1)];
		Arrays.fill(tableauCartes, type1);
		HashMap<String,ArrayList<Carte>> cartes0 = this.defausseCartes(tableauCartes);
		if(cartes0 == null) return false;
		else {
			cartes = cartes0;
			this.add(type2);
			return true;
		}
	}
	
}
