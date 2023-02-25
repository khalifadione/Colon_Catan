import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class XFrame extends JFrame {
	
	/*
	 * à faire :
	 * -IA (x)
	 * -début du jeu normal (x)
	 * -ville qui ramène 2 ressources (x)
	 * -compter PV (x)
	 * -ports (x)
	 * -voleur (x)
	 * -progress card (x)
	 * -route la plus longue, 3 knights (x)
	 * 
	 * 
	 * optimisation :
	 * -déléguer l'aspect graphique aux classes au lieu de tout concentrer sur XFrame 
	 * 																					et obliger les classes à être juste des stockeurs de type/position
	 * -faire un design cohérent pour echangePanel (x)
	 * -avoir les PV + RPL + PGA qui s'actualisent en temps réel
	 * -implémenter toutes les fonctions du voleur
	 * -fonction pour load les icon plus intuitivement
	 * -déménager des fonctions player dans la classe plateau ou jeu
	 */

	private static final long serialVersionUID = 6310878760632829121L;
	private static final String PATH = "images\\";
	private static final String [] ORDRE = {"Forêt", "Colline", "Pré", "Champs", "Montagne"};
	private JPanel contentPane; //super-panel
	
	private JPanel initialisation;
	private JRadioButton[][] initRadio = new JRadioButton[4][2];
	
	private JPanel plateau; //plateau
	private JLabel [][] labelsPlateau;
	
	private JScrollPane jScrollRand; //rand scrollable
	private JPanel rand; //tout ce qu'il y a en dessous du plateau
	private JPanel [] cartesDecks;
	private JButton dé;
	private JButton finDuTour;
	private JButton auto;
	
	private JPanel echangePanel;
	private JButton echanger;
	private JPanel cartesJoueur; //cartes à échanger
	private JPanel cartesBanque; //cartes à recevoir
	private String [] tableEchange = new String[2];
	
	private JButton acheter;
//	private JScrollPane jsProgressCards;
	private JPanel [] allProgressCards;
	private JPanel [] progressCardsDevo;
	private JPanel [] progressCards;
	int used=0; //keep track of progress card

	boolean voleur=false;
	
	public Jeu jeu;
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//XFrame frame = new XFrame(new Jeu("n"));
					XFrame frame = new XFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	public XFrame() throws IOException {	
		/* // à décommenter plus tard
		
		int r = JOptionPane.showOptionDialog(this,"Voulez-vous jouer avec une IA ?", "Catan",
	    JOptionPane.YES_NO_OPTION,
	    JOptionPane.QUESTION_MESSAGE,
	    null, new String[]{"Oui","Non"}, "Non");
	    if(r == JOptionPane.YES_OPTION) this.jeu = new Jeu("o");
	    else if(r == JOptionPane.NO_OPTION) this.jeu = new Jeu("n");
	    else System.exit(0);
	    
	    */
		
		pack();
		
		// this.jeu = new Jeu("n"); //à supprimer plus tard
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(900, 50, 1000, 1000);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
			
		initialisation();
	}	
	
	public void initialisation() {
		initialisation = new JPanel();
		initialisation.setLayout(new BoxLayout(initialisation, BoxLayout.Y_AXIS));
		initialisation.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

		initialisation.add(new JLabel("Nombre de joueurs :"));
		
		initRadio[0][0] = new JRadioButton("3");
		initRadio[0][1] = new JRadioButton("4");
		
		ButtonGroup nombreJoueurs = new ButtonGroup();
		nombreJoueurs.add(initRadio[0][0]);
		nombreJoueurs.add(initRadio[0][1]);
		
		JPanel radioPanel = new JPanel();
		radioPanel.add(initRadio[0][0]);
		radioPanel.add(initRadio[0][1]);
		radioPanel.setPreferredSize(new Dimension(250,50));
		radioPanel.setMaximumSize(new Dimension(250,50));
		
		initialisation.add(radioPanel);
		
		JButton submitNombreJoueurs = new JButton("Confirmer");
		
		submitNombreJoueurs.addActionListener(e -> {
			if(initRadio[0][0].isSelected()) {
				initialisation2(3);
			} else {
				initialisation2(4);
			}
		});
		initialisation.add(submitNombreJoueurs);
		
		contentPane.add(initialisation,BorderLayout.WEST);
	}
	
	public void initialisation2(int n) {
		clear(initialisation);
		initRadio[0] = new JRadioButton[2];
		
		ButtonGroup[] buttonGroups = new ButtonGroup[n];
		
		for(int i=0; i<n; i++) {
			initRadio[i][0] = new JRadioButton("Humain");
			initRadio[i][1] = new JRadioButton("Machine");
			buttonGroups[i] = new ButtonGroup();
			buttonGroups[i].add(initRadio[i][0]);
			buttonGroups[i].add(initRadio[i][1]);
			
			initialisation.add(new JLabel("Joueur "+i+" :"));
			initialisation.add(initRadio[i][0]);
			initialisation.add(initRadio[i][1]);
			initialisation.add(Box.createVerticalStrut(15));
		}
		
		JButton submit = new JButton("Confirmer");
		submit.addActionListener(e -> {
			String [] joueurs = new String[n];
						
			for(int i=0; i<n; i++) {
				if(initRadio[i][0].isSelected()) joueurs[i] = "Humain";
				if(initRadio[i][1].isSelected()) joueurs[i] = "Machine";
				if(joueurs[i] == null) return;
			}
			
			jeu = new Jeu(joueurs);
			commencer();
		});
		
		initialisation.add(submit);
	}
	
	public void commencer() {
		clear(contentPane);
		
		plateau = new JPanel();
		plateau.setLayout(new GridBagLayout());
		
		JPanel superplateau = new JPanel();
		superplateau.setLayout(new BorderLayout());
		superplateau.add(plateau, BorderLayout.NORTH);
				
		contentPane.add(superplateau, BorderLayout.WEST);
		
		int [][] coordonnéesPorts = {
				{1,0}, {5,0},
				{10,1}, {10,5},
				{7,10}, {3,10},
				{0,7}, {0,3}
		};
		
		for(int i=0; i<coordonnéesPorts.length; i++) { //ajoute les ports
			ImageIcon icon = new ImageIcon(PATH+"port-"+Integer.toString(i)+".png");
			Image image = icon.getImage();
			Image newimg;
			if(image.getHeight(this) > image.getWidth(this)) { //check si le port est vertical
				newimg = image.getScaledInstance(50, 150,  Image.SCALE_SMOOTH); 
				icon = new ImageIcon(newimg); 
				plateau.add(new JLabel(icon), createPortV(coordonnéesPorts[i][0],coordonnéesPorts[i][1]));
			} else {
				newimg = image.getScaledInstance(150, 50,  Image.SCALE_SMOOTH); 
				icon = new ImageIcon(newimg); 
				plateau.add(new JLabel(icon), createPortH(coordonnéesPorts[i][0],coordonnéesPorts[i][1]));
			}
			
			
		}
		
		labelsPlateau = new JLabel[9][9]; //stocke toutes les icones (routes, maisons...) pour gérer les events
		GridBagConstraints [][] gdc = new GridBagConstraints[9][9];
		for(int i=0; i<9; i++) {
			for(int j=0; j<9; j++) {
				ImageIcon icon = new ImageIcon();
				if(i%2==0 && j%2==0) { //Maison
					icon = new ImageIcon(PATH+"colonie-vide.png");
					icon = scale(icon, 40, 40);
				}
				if(i%2!=0 && j%2==0) { //Route V
					icon = new ImageIcon(PATH+"routev-vide.png");
					icon = scale(icon, 35, 65);
				}
				if(i%2==0 && j%2!=0) { //Route H
					icon = new ImageIcon(PATH+"routeh-vide.png");
					icon = scale(icon, 65, 35);
				}
				if(i%2!=0 && j%2!=0) { //Case
					int [] p1 = gridToPlateau(new int[]{i,j});
					String ij = Integer.toString(p1[0]) + Integer.toString(p1[1]);					
					if(jeu.plateau.cases[p1[0]][p1[1]].voleur == false) {
						icon = new ImageIcon(PATH+"cases\\"+ij+".png");
					}
					else icon = new ImageIcon(PATH+"voleurs\\"+ij+".png");
					icon = scale(icon, 100, 100);
				}
				JLabel label = new JLabel(icon);
				gdc[i][j] = createElement(i,j);		
				plateau.add(label, gdc[i][j]);
				
				labelsPlateau[i][j] = label;
				labelsPlateau[i][j].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						Player joueur = joueur();
						JLabel label = (JLabel) e.getSource(); 
						for(int i=0; i<labelsPlateau.length; i++) {
							for(int j=0; j<labelsPlateau[i].length; j++) {
								if(label == labelsPlateau[i][j]) {
									int [] p1 = gridToPlateau(new int[] {i,j});
									ImageIcon icon = (ImageIcon) label.getIcon();
									String type = getType(icon);
									if(type == "Maison") type = colonieOuVille(p1);
									
									switch(joueur.nbElements) {
									case 0: //off
										if(type == "Colonie") buildFreeColonie(p1);
										break;
									case 1: 
											if(type == "Route") {
											buildFreeRoute(new int[] {p1[0],p1[1]},new int[] {p1[2],p1[3]});
											if(jeu.courant < jeu.joueurs.length-1 && joueur.nbElements == 2) jeu.courant++;
											if(joueur() instanceof Machine) {
												autoBuildDebut();
											}
										}
										break;
									case 2:
										if(type == "Colonie") buildFreeColonie(p1);
										break;
									case 3:
										if(type == "Route") {
											buildFreeRoute(new int[] {p1[0],p1[1]},new int[] {p1[2],p1[3]});
											if(joueur.nbElements == 4) {
												if(jeu.courant > 0) {
													jeu.courant--;
													if(joueur() instanceof Machine) {
														autoBuildFin();
													}
												} else {
													contentPane.add(jScrollRand, BorderLayout.SOUTH);
													contentPane.add(echangePanel, BorderLayout.CENTER);
													update(contentPane);
													}
												}
											}
										break;  //off
									default: //le jeu est lancé
										
									//	contentPane.add(jScrollRand, BorderLayout.SOUTH); //on
									//	contentPane.add(echangePanel, BorderLayout.CENTER); //on
									//	update(contentPane); //on
										
										
										
										if (finDuTour.getParent() == rand) { //on ne construit pas avant d'avoir lancé le dé
											if(joueur().currentProgressCard != null && joueur().currentProgressCard.type == "Route Gratuite" && used < 2) {
												if(type == "Route") {
													buildFreeRoute(new int[] {p1[0],p1[1]},new int[] {p1[2],p1[3]}); 
													used++;
												}
											} else if(voleur == true && type == "Case" && deplaceVoleur(p1)) {
												voleur = false; 
												finDuTour.setVisible(true);
											}
											else {
												switch(type) { //lbl
												   case "Colonie": buildColonie(p1); break; //build colonie
												   case "Ville": buildVille(p1); break; //build ville
												   case "Route": 
													   buildRoute(new int[] {p1[0],p1[1]},new int[] {p1[2],p1[3]}); 
													   break; //build route
												   case "Case": break; //build case
												   }
											}
										}
									}
								}
							}
						}
					}
				});
			}
		}
		
		rand = new JPanel(new FlowLayout(FlowLayout.LEFT,20,20));
		
		dé = new JButton(new ImageIcon(PATH+"de.png"));
		dé.setPreferredSize(new Dimension(100, 100));
		
		finDuTour = new JButton("Fin du tour");
		finDuTour.setPreferredSize(new Dimension(100, 50));
		
		auto = new JButton("AUTO");
		auto.setPreferredSize(new Dimension(100, 100));

		
		cartesDecks = new JPanel[jeu.joueurs.length];
		for(int k=0; k<cartesDecks.length; k++) {
			cartesDecks[k] = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
		}
		this.addCartes(); //ajoute les cartes des joueurs dans les différents decks
		
		rand.add(dé);
		jScrollRand = new JScrollPane(rand, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollRand.setMaximumSize(new Dimension(10,10));
	//	contentPane.add(jScrollRand, BorderLayout.SOUTH);
	//	contentPane.add(rand, BorderLayout.SOUTH);
		
		dé.addActionListener(e -> XFrameTour());
		finDuTour.addActionListener(e -> tourSuivant());
		auto.addActionListener(e -> { XFrameTour(); tourSuivant(); });
		
		echangePanel = new JPanel();
		echangePanel.setLayout(new BoxLayout(echangePanel, BoxLayout.Y_AXIS));
		echangePanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

		echanger = new JButton("Échanger");
		echanger.setPreferredSize(new Dimension(200, 100)); 
		echanger.setMaximumSize(new Dimension(200, 100)); 
		echanger.addActionListener(e -> fillEchangePanel());

	//	echangePanel.add(Box.createVerticalStrut(100));
	//	echangePanel.add(echanger); 
				
		
		acheter = new JButton("[?] Acheter");
		acheter.setPreferredSize(new Dimension(200, 100));
		acheter.setMaximumSize(new Dimension(200, 100)); 
		acheter.addActionListener(e -> buyProgressCard());
		
		progressCardsDevo = new JPanel[jeu.joueurs.length];
		progressCards = new JPanel[jeu.joueurs.length];
		
	//	JScrollPane jsProgressCardsDevo = new JScrollPane(progressCardsDevo, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	//	JScrollPane jsProgressCards = new JScrollPane(progressCards, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

				
		allProgressCards = new JPanel[jeu.joueurs.length];
		
		for(int i=0; i<jeu.joueurs.length; i++) {
			progressCardsDevo[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
			progressCardsDevo[i].setPreferredSize(new Dimension(0,90));
			progressCardsDevo[i].setMinimumSize(new Dimension(0,90));

			progressCards[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
			progressCards[i].setPreferredSize(new Dimension(0,90));
			progressCards[i].setMaximumSize(new Dimension(0,90));
			progressCards[i].setMinimumSize(new Dimension(0,90));
			
			allProgressCards[i] = new JPanel(new BorderLayout());
			allProgressCards[i].setAlignmentX(Component.LEFT_ALIGNMENT);
			allProgressCards[i].add(progressCardsDevo[i], BorderLayout.NORTH);
			allProgressCards[i].add(progressCards[i], BorderLayout.CENTER);
		}
		
		clearEchangePanel();
				
	//	contentPane.add(echangePanel, BorderLayout.CENTER); //à commenter

	//	echangePanel.add(Box.createHorizontalGlue());
		
		if(joueur() instanceof Machine) autoBuildDebut();
	 
	}
	
	//*************************************************************************************************
	//*************************************************************************************************
	
	public Player joueur() {
		return jeu.joueurs[jeu.courant];
	}
	
	public Machine machine() {
		if(joueur() instanceof Machine) return (Machine)joueur();
		else return null;
	}
	
	public void updatePV() {
		if (finDuTour.getParent() == rand) {
			JLabel r = (JLabel)rand.getComponent(0);
			String s = r.getText();
			int nbPV = joueur().nbPV;
			s = s.replaceAll("PV: [0-9]","PV: "+nbPV);
			r.setText(s);
		}
	}
	
	public void addCartes() { //ajoute toutes les cartes
		for(int j=0; j<cartesDecks.length; j++) {
			updateCartes(j);
		}
	}
	
	public void updateCartes() {
		updateCartes(jeu.courant);
	}
	
	public void updateCartes(int j) { //update les cartes d'un joueur j
		clear(cartesDecks[j]);
		Player joueur = jeu.joueurs[j];
		//System.out.println(joueur.cartes.size());
		for(int k=0; k<ORDRE.length; k++) {
			if(joueur.cartes.get(ORDRE[k]) != null) {
				for(Carte c : joueur.cartes.get(ORDRE[k])) {
					ImageIcon carte = new ImageIcon(PATH+"motifs\\"+c.type+".png");
					carte = scale(carte, 100, 100);
					cartesDecks[j].add(new JLabel(carte));
				}
			}
		}
	}
	
	public void addProgressCard() {
		ImageIcon icon = new ImageIcon(PATH+"progress-cards\\Default.png");
		icon = scale(icon, 80, 80);
		JLabel progressCard = new JLabel(icon);
		progressCard.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(joueur().currentProgressCard != null) return;
				if(dé.getParent() == rand) return;
				else {
					if(joueur().useProgressCard()) {
						addProgressCardDevo(joueur().currentProgressCard.type);
					}
				}
			}
		});
		progressCards[jeu.courant].add(progressCard);
		update(progressCards[jeu.courant]);
	}
	
	public void addProgressCardDevo(String type) {
		ImageIcon icon = new ImageIcon(PATH+"progress-cards\\"+type+".png");
		icon = scale(icon, 80, 80);
		JLabel progressCardDevo = new JLabel(icon);
		if(joueur().currentProgressCard.type == "Point de Victoire") {
			progressCardsDevo[jeu.courant].add(progressCardDevo,0);
		} else progressCardsDevo[jeu.courant].add(progressCardDevo);
		update(progressCardsDevo[jeu.courant]);
		
		Component progressCard = progressCards[jeu.courant].getComponent(0);
		progressCards[jeu.courant].remove(progressCard);
		update(progressCards[jeu.courant]);
		
		switch(joueur().currentProgressCard.type) {
		case "Chevalier": 
			joueur().nbChevaliers++;
			if( (jeu.plusGrandeArmee == null || joueur().nbChevaliers > jeu.plusGrandeArmee.nbChevaliers) && joueur().nbChevaliers >= 3) {
				if(jeu.plusGrandeArmee != null) jeu.plusGrandeArmee.nbPV -= 2;
				jeu.plusGrandeArmee = joueur();
				joueur().nbPV += 2;
			}
			break;
		case "Point de Victoire": joueur().nbPV++; break; 
		case "Route Gratuite": break; //traité dans mouseListener
		case "Abondance": 
			JPanel tableAbondance = new JPanel();
			setCardsEchangePanel(tableAbondance);
			allProgressCards[jeu.courant].add(tableAbondance, BorderLayout.SOUTH);
			update(allProgressCards[jeu.courant]);
			tableEchange = new String[2]; //reset par sécurité
			break; //à faire
		case "Monopole": 
			JPanel tableMonopole = new JPanel();
			setCardsEchangePanel(tableMonopole);
			allProgressCards[jeu.courant].add(tableMonopole, BorderLayout.SOUTH);
			update(allProgressCards[jeu.courant]);
			tableEchange = new String[2]; //reset par sécurité
			break;
		}
		
		updateCartes();
		updatePV();
	}
	
	public void fillEchangePanel() {
		//fill		
		clear(echangePanel);
		echangePanel.add(Box.createVerticalStrut(100));
		
		JButton retour = new JButton("Retour");
		retour.setPreferredSize(new Dimension(200,100));
		retour.setMaximumSize(new Dimension(200,100));
		retour.setAlignmentX(Component.LEFT_ALIGNMENT);		
		retour.addActionListener(e -> clearEchangePanel());
		echangePanel.add(retour);
		
		echangePanel.add(Box.createVerticalStrut(25));
		
		cartesJoueur = new JPanel();
		setCardsEchangePanel(cartesJoueur);
		cartesJoueur.setAlignmentX(Component.LEFT_ALIGNMENT);		
		echangePanel.add(cartesJoueur);
		
		cartesBanque =  new JPanel(); 
		setCardsEchangePanel(cartesBanque);
		cartesBanque.setAlignmentX(Component.LEFT_ALIGNMENT);		
		echangePanel.add(cartesBanque);
		
		JButton confirmer = new JButton("Confirmer");
		confirmer.setAlignmentX(Component.LEFT_ALIGNMENT);
		confirmer.setPreferredSize(new Dimension(200,100));
		confirmer.setMaximumSize(new Dimension(200,100));
		confirmer.addActionListener(e -> confirmEchangePanel());
		echangePanel.add(confirmer);
		
		JLabel error = new JLabel("Cet échange n'a pas pu être réalisé.");
		error.setAlignmentX(Component.LEFT_ALIGNMENT);
		error.setVisible(false);
		echangePanel.add(error);
	}
	
	public void setCardsEchangePanel(JPanel main) {
		 setCardsEchangePanel(main, "x");
	}
	
	public void setCardsEchangePanel(JPanel main, String typeS) {
		clear(main);
		main.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
		main.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
		for(String type : ORDRE) {
			ImageIcon carte;
			if(type != typeS) carte = new ImageIcon(PATH+"motifs\\"+type+".png");
			else carte = new ImageIcon(PATH+"motifs-selected\\"+type+".png");
			carte = scale(carte, 80, 80, type);
			JRadioButton radioCarte = new JRadioButton(carte);
			radioCarte.addActionListener(e -> {
				JRadioButton source = (JRadioButton)e.getSource();
				JPanel mainS = (JPanel)(source.getParent().getParent());
				String typeX = ((ImageIcon)(source.getIcon())).getDescription();
				if(mainS == cartesJoueur) tableEchange[0] = typeX;
				else if(mainS == cartesBanque) tableEchange[1] = typeX;
				else { //progress cards
					if(joueur().currentProgressCard.type == "Monopole") {
						jeu.monopole(typeX);
						allProgressCards[jeu.courant].remove(mainS);
						update(allProgressCards[jeu.courant]);
						return;
					} else { //Abondance
						if(tableEchange[0] != null) {
							joueur().abondance(typeS, typeX);
							allProgressCards[jeu.courant].remove(mainS);
							update(allProgressCards[jeu.courant]);
							return;
						}
						else tableEchange[0] = typeX;
					}
				}
				
				setCardsEchangePanel(mainS, typeX);
			}); 
			JLabel ratio = new JLabel("",SwingConstants.CENTER);
			
			if(main == cartesJoueur) {
				int quantite = joueur().quantiteEchange(type);
				String texteRatio = quantite + ":1";
				ratio.setText(texteRatio);
			}
			
			JPanel displayCarte = new JPanel(new BorderLayout());
			displayCarte.add(radioCarte, BorderLayout.NORTH);
			displayCarte.add(ratio, BorderLayout.SOUTH);
			
			main.add(displayCarte);
		}
		main.setPreferredSize(main.getMinimumSize());
		main.setMaximumSize(main.getMinimumSize());
	}
	
	
	public void confirmEchangePanel() {
		setCardsEchangePanel(cartesJoueur);
		setCardsEchangePanel(cartesBanque);
		boolean reussite = joueur().echange(tableEchange[0], tableEchange[1]);
		tableEchange = new String[2];
		JLabel error = (JLabel)echangePanel.getComponents()[echangePanel.getComponents().length-1];
		if(!reussite) error.setVisible(true);
		else {
			error.setVisible(false);
			updateCartes();
		}
	}
	
	public void clearEchangePanel() {
		clear(echangePanel);
		
		echangePanel.add(Box.createVerticalStrut(100));
		echanger.setAlignmentX(Component.LEFT_ALIGNMENT); //
		echangePanel.add(echanger);
		echangePanel.add(Box.createVerticalStrut(50));
		acheter.setAlignmentX(Component.LEFT_ALIGNMENT); //
		echangePanel.add(acheter);
		echangePanel.add(Box.createVerticalStrut(50));
		allProgressCards[jeu.courant].setAlignmentX(Component.LEFT_ALIGNMENT); //
		echangePanel.add(allProgressCards[jeu.courant]);
		tableEchange = new String[2];
		
	}
	
	public void clear(JPanel panel) { //évite de copier trois fct à chaque fois
		panel.removeAll();
		panel.revalidate();
		panel.repaint();
	}
	
	public void update(JPanel panel) {
		panel.revalidate();
		panel.repaint();
		updateCartes(); //des fois inutile mais revient souvent
	}
	
	public ImageIcon scale(ImageIcon icon, int width, int height) { //rescale une ImageIcon
		Image image = icon.getImage();
		Image newimg = image.getScaledInstance(width, height,  Image.SCALE_SMOOTH); 
		icon = new ImageIcon(newimg);
		return icon;
	}
	
	public ImageIcon scale(ImageIcon icon, int width, int height, String description) {
		icon = scale(icon, width, height);
		icon.setDescription(description);
		return icon;
	}
	
	public void XFrameRoll() { //ce qui se passe après avoir lancé le dé (tout le tour)
		clear(rand);
		int roll = Player.roll();
		if(roll == 7) voleur = true; //voleur
		jeu.distribution(roll);
		addCartes();
		JLabel r = new JLabel("<html><p>Joueur " + jeu.courant + "</p><p>Résultat: " + roll + "</p><p>PV - " + joueur().nbPV  
				+ "</p><p>Route - " + joueur().routeLaPlusLongue + "</p><p>Armée - " + joueur().nbChevaliers+ "</p></html>");
		rand.add(r);
	}
	
	public void XFrameMove() {
		rand.add(finDuTour);
		rand.add(cartesDecks[jeu.courant], BorderLayout.SOUTH);
		
		if(voleur == true) {
			JOptionPane.showMessageDialog(this, "Vous avez tiré le voleur ! Cliquez sur une case y placer le voleur.");
			finDuTour.setVisible(false);
		}
		
		//lbl
		//ajouter fonctions pour build et échanger 
	}
	
	public void XFrameTour() {
		XFrameRoll();
		if(joueur() instanceof Joueur) XFrameMove();
		else this.autoMove();
		//lbl
		//tour suivant -exception pour machine
	}
	
	public void tourSuivant() { //change de joueur et prépare le tour suivant
		used = 0;
		if(joueur().currentProgressCard != null && joueur().currentProgressCard.type != "Chevalier" && joueur().currentProgressCard.type != "Point de Victoire") {
			progressCardsDevo[jeu.courant].remove(progressCardsDevo[jeu.courant].getComponents().length-1); //remove last card
		}
		joueur().progressCardFinDeTour();
		boolean prevJoueur = joueur() instanceof Joueur ? true : false;
		if(joueur().nbPV >= 10) {
			JOptionPane.showMessageDialog(this, "Joueur " + jeu.courant + " gagne la partie"
										+ "\nPV: " + joueur().nbPV
										+ "\nRoute: " + joueur().routeLaPlusLongue + (jeu.routeLaPlusLongue == joueur() ? " (X) " : "")
										+ "\nArmée: " + joueur().nbChevaliers + (jeu.plusGrandeArmee == joueur() ? " (X) " : "")
										+ "\nCarte PV: " + (joueur().progressCardsDevo.size() - joueur().nbChevaliers)
					);
			return;
		}
		
		jeu.nextPlayer();
		clear(rand);
		clearEchangePanel();
		
		if(joueur() instanceof Joueur) {
			contentPane.add(echangePanel);
			update(contentPane);
		}
		
		
		if(joueur() instanceof Machine) {
			if(prevJoueur) rand.add(auto);
			else {
				XFrameTour();
				tourSuivant();
			}
			return;
		}
		rand.add(dé);
	}
	
	//**************************************************************************************
	//**************************************************************************************
	
	public void autoBuild() {
		int [] pBuild;
		pBuild = machine().getFreeMaison();
		buildFreeColonie(pBuild);
		pBuild = machine().getFreeRoute();
		buildFreeRoute(new int[] {pBuild[0], pBuild[1]}, new int[] {pBuild[2], pBuild[3]});
	}
	
	public void autoBuildDebut() {
		autoBuild();
		
		if(jeu.courant < jeu.joueurs.length-1) jeu.courant++;
		if(joueur() instanceof Machine) {
			if(joueur().nbElements == 0) autoBuildDebut();
			else autoBuildFin();
		}
	}
	
	public void autoBuildFin() {
		autoBuild();
		
		if(jeu.courant == 0) {
			clear(rand);
			rand.add(auto);
			contentPane.add(jScrollRand, BorderLayout.SOUTH);
			update(contentPane);
			return;
		}
		
		jeu.courant--;
		if(joueur() instanceof Machine) autoBuildFin();
	}
	
	public void autoMove() {
		//ordre de priorité : progress card - ville - colonie - route
		
		/* while(machine().defausseCartes("Pré", "Champs", "Montagne") != null) { //trop complexe
			this.buyProgressCard();
		} */
				
		if(voleur == true) { //déplace voleur
			if(jeu.plateau.cases[0][0].voleur != true) deplaceVoleur(new int[]{0,0});
			else deplaceVoleur(new int[]{0,1});
			voleur = false;
		}
		
		while(machine().defausseCartes("Champs", "Champs", "Montagne", "Montagne", "Montagne") != null && machine().getColonie() != null) {
			int [] p1 = machine().getColonie().position;
			this.buildVille(p1);
		}
		while(machine().defausseCartes("Forêt", "Colline", "Pré", "Champs") != null && machine().getFreeMaison().length > 0) {
			int [] p1 = machine().getFreeMaison();
			this.buildColonie(p1);
		}
		while(machine().defausseCartes("Forêt", "Colline") != null && machine().getFreeRoute().length > 0) {
			int [] p = machine().getFreeRoute();
			int [] p1 = {p[0],p[1]};
			int [] p2 = {p[2],p[3]};
			this.buildRoute(p1, p2);
		}
		
		for(String type : ORDRE) { //échange pour avoir des cartes qu'on a pas
			if(machine().defausseCartes(type) == null) { 
				machine().echangePour(type);
			}
		}
		
	/*  for(String type : ORDRE) {
			if(machine().cartes.get(type) == null) System.out.print(" " + type + ": 0");
			else System.out.print(" " + type + ": " + machine().cartes.get(type).size());
		}
		System.out.println("\n"); */
	}
	
	
	public String getType(ImageIcon icon) { //devine si l'élément cliqué est case, route ou maison
		if(icon.getIconHeight() != icon.getIconWidth()) return "Route";
		if(icon.getIconWidth() == 100) return "Case";
		return "Maison";
	}
	
	public String colonieOuVille(int [] p1) {
		if(jeu.plateau.maisons[p1[0]][p1[1]] == null) return "Colonie";
		else return "Ville";
	}
	
	public boolean deplaceVoleur(int [] p1) {
		Case c = jeu.plateau.deplaceVoleur(p1);
		if(c == null) return false;
		else {
			int [] pGrid1 = caseToGrid(c.position);	
			ImageIcon iconDepart = new ImageIcon(PATH + "cases\\" + c.position[0] + c.position[1] + ".png");
			iconDepart = scale(iconDepart, 100, 100);
			labelsPlateau[pGrid1[0]][pGrid1[1]].setIcon(iconDepart);
			
			int [] pGrid2 = caseToGrid(p1);
			ImageIcon iconDestination = new ImageIcon(PATH + "voleurs\\" + p1[0] + p1[1] + ".png");
			iconDestination = scale(iconDestination, 100, 100);
			labelsPlateau[pGrid2[0]][pGrid2[1]].setIcon(iconDestination);
			
			return true;
		}
	}
	
	public boolean buildFreeRoute(int [] p1, int [] p2) {
		joueur().add("Forêt", "Colline");
		boolean reussite = buildRoute(p1, p2);
		if(reussite) {
			joueur().nbElements++;
			return true;
		}
		else {
			joueur().cartes = joueur().defausseCartes("Forêt", "Colline");
			return false;
		}
	}
	
	public boolean buildFreeColonie(int [] p1) {
		joueur().add("Forêt", "Colline", "Pré", "Champs");
		boolean reussite = buildColonie(p1);
		if(reussite) {
			joueur().nbElements++;
			return true;
		}
		else {
			joueur().cartes = joueur().defausseCartes("Forêt", "Colline", "Pré", "Champs");
			return false;
		}
	}
	
	public boolean buildRoute(int [] p1, int [] p2) {
		if(p1[0] == p2[0]) return buildRoute(p1, p2, "RouteH");
		else return buildRoute(p1, p2, "RouteV");
		}
	
	public boolean buildRoute(int [] p1, int [] p2, String type) {//défaillant
		if(joueur().buildRoute(p1, p2)) {
			int [] pGrid = routeToGrid(p1,p2);
			ImageIcon route = type == "RouteV" ? new ImageIcon(PATH + "routev-" + jeu.courant + ".png") : new ImageIcon(PATH + "routeh-" + jeu.courant + ".png");
			route = type == "RouteV" ? scale(route, 35, 65) : scale(route, 65, 35);
			labelsPlateau[pGrid[0]][pGrid[1]].setIcon(route);
			updateCartes();
			updatePV();
			clearEchangePanel();
			return true;
		} else return false;
	}
	
	public boolean buildColonie(int [] p1) { //gère le build niveau design, délègue le côté technique à la classe joueur
		if(joueur().buildColonie(p1)) {
			this.buildMaison(p1, "colonie-");
			return true;
		} else return false;
	}
	
	public boolean buildVille(int [] p1) { //gère le build niveau design, délègue le côté technique à la classe joueur
		if(joueur().buildVille(p1)) {
		//	System.out.println("oui");
			this.buildMaison(p1, "ville-");
			return true;
		} else return false;
	}
	
	private void buildMaison(int [] p1, String fileName) {
		int [] pGrid = maisonToGrid(p1);
		ImageIcon maison = new ImageIcon(PATH + fileName + jeu.courant + ".png");
		maison = scale(maison, 40, 40);
		labelsPlateau[pGrid[0]][pGrid[1]].setIcon(maison);
		updateCartes();
		updatePV();
		clearEchangePanel();
	}
	
	public boolean buyProgressCard() {
		if(joueur().buyProgressCard() == false) {
			acheter.setText("/!\\ Acheter");
			return false;
		} else {
			acheter.setText("[?] Acheter");
			addProgressCard();
			return true;
		}
	}
	
	//
	
	public int [] caseToGrid(int [] p1) { //convertit coordonnées Case en coordonnées XFrame
		return new int[] {p1[0]*2+1,p1[1]*2+1};
	}
	
	public int [] maisonToGrid(int [] p1) { //convertit coordonnées Case en coordonnées XFrame
		return new int[] {p1[0]*2,p1[1]*2};
	}
	
	public int [] routeToGrid(int [] p1, int [] p2) { //convertit coordonnées Plateau en coordonnées XFrame
		int [] pGrid1 = maisonToGrid(p1);
		int [] pGrid2 = maisonToGrid(p2);
		if(pGrid1[0] == pGrid2[0]) { //route H
			return new int[] {pGrid1[0],pGrid1[1]+1};
		} else {
			return new int[] {pGrid1[0]+1,pGrid1[1]};
		}
	}
	
	public int [] gridToPlateau(int [] pGrid) { //convertit coordonnées XFrame en coordonnées Plateau
		if(pGrid[0] % 2 == pGrid[1] % 2) return new int[]{(pGrid[0]/2), (pGrid[1]/2)};
		int [] p1, p2;
		if(pGrid[0] % 2 == 0) { //route H
			p1 = new int[]{ (pGrid[0]/2) , (pGrid[1]-1)/2 };
			p2 = new int[]{ pGrid[0]/2 , (pGrid[1]+1)/2 };
		} else { //route V
			p1 = new int[]{ (pGrid[0]-1)/2 , pGrid[1]/2 };
			p2 = new int[]{ (pGrid[0]+1)/2 , pGrid[1]/2 };
		}	
		return new int[]{(p1[0]),(p1[1]),(p2[0]),(p2[1])};
	}
	
	//
	
	public GridBagConstraints createPortV(int i, int j) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = i;
		gbc.gridx = j;
		gbc.gridheight = 3;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10,10,10,10);
		return gbc;
	}
	
	public GridBagConstraints createPortH(int i, int j) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = i;
		gbc.gridx = j;
		gbc.gridheight = 1;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10,10,10,10);
		return gbc;
	}
	
	public GridBagConstraints createElement(int i, int j){ //crée une case, maison ou route
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = i+1;
		gbc.gridx = j+1;
		return gbc;
	}
	

}
