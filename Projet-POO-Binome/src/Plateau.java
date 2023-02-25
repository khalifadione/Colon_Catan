public class Plateau {
	//plateau de jeu
	public Case [][] cases;
	public Port [] ports;
	public Maison [][] maisons;
	public Route [][][][] routes;

	Plateau() {
		
		this.cases = new Case[4][4];
		
		String [] [] types = {
				{"Forêt", "Pré", "Champs", "Pré"},
				{"Champs", "Colline", "Forêt", "Montagne"},
				{"Montagne", "Désert", "Champs", "Colline"},
				{"Colline", "Montagne", "Pré", "Forêt"},
		};
		
		int [] [] chiffres = {
				{6, 10, 11, 8},
				{4, 9, 5, 12},
				{3, 0, 10, 6},
				{9, 8, 5, 2},
		};
		
		for(int i = 0; i<4; i++) {
			for(int j = 0; j<4; j++) {
				String type = types[i][j];
				int chiffre = chiffres[i][j];
				if(type == "Désert") cases[i][j] = new Case(i,j, "Désert", 0, true);
				else cases[i][j]= new Case(i,j, type, chiffre);
			}
		}
		
		ports = new Port[]{
				new Port( new int[]{0,0}, new int[]{1,0}, "Général" ),
				new Port( new int[]{2,0}, new int[]{3,0}, "Champs" ),
				new Port( new int[]{4,0}, new int[]{4,1}, "Montagne" ),
				new Port( new int[]{4,2}, new int[]{4,3}, "Colline" ),
				new Port( new int[]{4,4}, new int[]{3,4}, "Général" ),
				new Port( new int[]{2,4}, new int[]{1,4}, "Forêt" ),
				new Port( new int[]{0,4}, new int[]{0,3}, "Général" ),
				new Port( new int[]{0,2}, new int[]{0,1}, "Pré" )
		};
		
		maisons = new Maison[5][5];
		routes = new Route[5][5][5][5];
		
	}
	
	public Case [] trouveCases(int k) { //trouve case activée par un lancer de dé k
		Case [] casesTrouvées = new Case[2]; 
		for(int i = 0; i<4; i++) {
			for(int j = 0; j<4; j++) {
				if(cases[i][j].chiffre == k && cases[i][j].voleur == false) {
					int index = casesTrouvées[0] == null ? 0 : 1;
					casesTrouvées[index] = cases[i][j];
				}
			}
		}
		return casesTrouvées;
	}

	
	public Maison [] trouveMaisons(Case c) {
		int i = c.position[0];
		int j = c.position[1];
		Maison [] ms = new Maison[4];
		
		ms[0] = maisons[i][j];
		ms[1] = maisons[i][j+1];
		ms[2] = maisons[i+1][j];
		ms[3] = maisons[i+1][j+1];
		
		return ms;
	}
	
	public Case trouveVoleur() {
		for(int i=0; i<cases.length; i++) {
			for(int j=0; j<cases[i].length; j++) {
				if(cases[i][j].voleur == true) return cases[i][j];
			}
		}
		return null;
	}
	
	public Case deplaceVoleur(int [] p1) {
		Case depart = trouveVoleur();
		Case destination;
		try {
			destination = cases[p1[0]][p1[1]];
		} catch (Exception e) {
			return null;
		}
		if(depart == null || destination == null) return null;
		if(depart == destination) return null;
		depart.voleur = false;
		destination.voleur = true;
		return depart;
	}
	
}	
	
	
	
	
	

