public class Machine extends Player {
	private static final String [] ORDRE = {"Forêt", "Colline", "Pré", "Champs", "Montagne"};

	//joueur IA
	Machine(Plateau p, Jeu j){
		super(p,j);
	}
	
	public Colonie getColonie() { //retourne une colonie s'il y en a une
		for(Maison m : maisons) {
			if(m instanceof Colonie) return (Colonie)m;
		}
		return null;
	}
	
	public int [] getFreeRoute() { //retourne une route à construire (pas encore construite)
	//	System.out.println("e");
		
		if(debute()) {
			int [] p1 = derniereColonie.position;
			int i = p1[0];
			int j = p1[1];
			try { if(plateau.routes[i-1][j][i][j] == null) return new int[]{i-1,j,i,j}; } catch(Exception e) {}
			try { if(plateau.routes[i][j][i+1][j] == null) return new int[]{i,j,i+1,j}; } catch(Exception e) {}
		} else for(Route r : routes) {
			int i,j;
			//position1
			int [] p1 = r.position1;
			i = p1[0];
			j = p1[1];
			try { if(plateau.routes[i-1][j][i][j] == null) return new int[]{i-1,j,i,j}; } catch(Exception e) {}
			try { if(plateau.routes[i][j][i+1][j] == null) return new int[]{i,j,i+1,j}; } catch(Exception e) {}
			try { if(plateau.routes[i][j-1][i][j] == null) return new int[]{i,j-1,i,j}; } catch(Exception e) {}
			try { if(plateau.routes[i][j][i][j+1] == null) return new int[]{i,j,i,j+1}; } catch(Exception e) {}
			
			//position2
			int [] p2 = r.position2;
			i = p2[0];
			j = p2[1];
			
			try { if(plateau.routes[i-1][j][i][j] == null) return new int[]{i-1,j,i,j}; } catch(Exception e) {}
			try { if(plateau.routes[i][j][i+1][j] == null) return new int[]{i,j,i+1,j}; } catch(Exception e) {}
			try { if(plateau.routes[i][j-1][i][j] == null) return new int[]{i,j-1,i,j}; } catch(Exception e) {}
			try { if(plateau.routes[i][j][i][j+1] == null) return new int[]{i,j,i,j+1}; } catch(Exception e) {}
		}
		return new int[]{};
	}
	
	public int [] getFreeMaison() { 
		if(debute()) { //si le joueur débute
			for(int i=0; i<plateau.maisons.length; i++) {
				for(int j=0; j<plateau.maisons[i].length; j++) {
					if(this.estLibreMaison(new int[]{i,j})) return new int[]{i,j};
				}
			}
		} //else
		for(Route r : routes) {
			if(this.estLibreMaison(r.position1)) return r.position1;
			if(this.estLibreMaison(r.position2)) return r.position2;
		}
		return new int[]{};
	}

	public boolean echangePour(String typeFin) {
		for(String typeDebut : ORDRE) {
			if(echange(typeDebut, typeFin)) return true;
		}
		return false;
	}
	
}
