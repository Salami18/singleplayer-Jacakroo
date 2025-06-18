package gui;

import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;
import java.util.*;

import engine.Game;
import engine.GameManager;
import engine.board.Board;
import engine.board.Cell;
import engine.board.SafeZone;
import model.Colour;
import model.card.Card;
import model.card.standard.Standard;
import model.card.wild.Burner;
import model.card.wild.Saver;
import model.player.CPU;
import model.player.Marble;
import model.player.Player;
import exception.*;

public class MarbleGame extends Application {
	 private Image CLUBS_1, CLUBS_2, CLUBS_3, CLUBS_4, CLUBS_5, CLUBS_6, CLUBS_7, CLUBS_8, CLUBS_9, CLUBS_10, CLUBS_11, CLUBS_12, CLUBS_13;
	    private Image DIAMONDS_1, DIAMONDS_2, DIAMONDS_3, DIAMONDS_4, DIAMONDS_5, DIAMONDS_6, DIAMONDS_7, DIAMONDS_8, DIAMONDS_9, DIAMONDS_10, DIAMONDS_11, DIAMONDS_12, DIAMONDS_13;
	    private Image HEARTS_1, HEARTS_2, HEARTS_3, HEARTS_4, HEARTS_5, HEARTS_6, HEARTS_7, HEARTS_8, HEARTS_9, HEARTS_10, HEARTS_11, HEARTS_12, HEARTS_13;
	    private Image SPADES_1, SPADES_2, SPADES_3, SPADES_4, SPADES_5, SPADES_6, SPADES_7, SPADES_8, SPADES_9, SPADES_10, SPADES_11, SPADES_12, SPADES_13;
	    private Image MARBLE_BURNER, MARBLE_SAVER;
	    private Image CARD_BACK;  
	    
	    //bonus HEHEHHEHEHE
	    private Image PATRICK_NORMAL, PATRICK_THINKING;
	    private Image SPONGEBOB_NORMAL, SPONGEBOB_THINKING;
	    private Image SQUIDWARD_NORMAL, SQUIDWARD_THINKING;
	    private Image GARY_NORMAL, GARY_THINKING;
	    private Map<Integer, StackPane> playerCharacterPanes = new HashMap<>();

	    
	private String playerName = "Player 1";
	private Card selectedCard;
	private Timeline cpuTurnTimeline;//here
	private int currentCpuIndex;//here
	
	private ImageView firePitCardView;//firepit

    // our game colors (green, red, yellow, blue)
    private final Color[] guiColours = {
        Color.rgb(0, 128, 0),    // dark green
        Color.rgb(255, 0, 0),     // stop sign red
        Color.rgb(255, 255, 0),   // taxi yellow
        Color.rgb(0, 0, 255)      // deep ocean blue
    };
    
    // keeps guiTrack of which color each player gets (randomized)
    private ArrayList<Colour> colourOrder = new ArrayList<>();
    private Map<Integer, Label> cpuInfoLabels = new HashMap<>();

    
    // the game rules and logic handler
    private Game gameEngine ;
    private Player playerengine;
    private Board boardengine;
    private GameManager gamemanager;
    // UI elements for game info
    private Label errorLabel = new Label();
    private Label statusLabel = new Label();  // current turn
    private Label firePitLabel = new Label(); // last played card
    private Label winnerLabel = new Label();  // winner announcement
    private Label humanInfoLabel= new Label();
    private Label next = new Label();
    
    // card selection stuff
    private Button selectedCardButton;  // currently chosen card
    private ArrayList<Circle> selectedMarble = new ArrayList<Circle>();      // currently chosen marble
    private HBox humanCards;           // player's hand
    private BorderPane gameLayout = new BorderPane();
    private GridPane guiTrack = new GridPane();
    
    private ArrayList<Circle> marbles0;
    private ArrayList<Circle> marbles1;
    private ArrayList<Circle> marbles2; 
    private ArrayList<Circle> marbles3;
    private ArrayList<Circle> marbles;
    
    
    ArrayList<int[]> top = new ArrayList<int[]>();
    ArrayList<int[]> botttom1 = new ArrayList<int[]>();
    ArrayList<int[]> botttom2 = new ArrayList<int[]>();
    ArrayList<int[]> right = new ArrayList<int[]>();
    ArrayList<int[]> left = new ArrayList<int[]>();
    ArrayList<int[]> all = new ArrayList<int[]>();
    // this is where the magic starts
    @Override
    public void start(Stage stage) {
    	
    	loadCardImages();
        // setup the start screen
        VBox startScreen = new VBox(20);
        startScreen.setAlignment(Pos.CENTER);
        startScreen.setPadding(new Insets(20));
        
        // big colorful title
        Text title = new Text("JACKAROOOOO");
        Text sub = new Text("write your name please");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        sub.setFont(Font.font("Arial", FontPosture.ITALIC, 20));
        title.setFill(new LinearGradient(0,0,1,0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.RED), new Stop(0.33, Color.BLUE),
            new Stop(0.66, Color.GREEN), new Stop(1, Color.YELLOW)));
        
        // name input field
        TextField nameInput = new TextField();
        nameInput.setPromptText("type your name here");//i have to press lets play for it to appear so not promt i think
        nameInput.setMaxWidth(200);
        
        // start game button
        Button startBtn = new Button("LET'S PLAY!");
        startBtn.setStyle("-fx-font-weight: bold; -fx-padding: 10 20;");
        
        
        // arrange start screen items
        startScreen.getChildren().addAll(title, sub, nameInput, startBtn);//add write name
        
        // main game layout container
        
        
        // when start is clicked
        startBtn.setOnAction(e -> {
        	    try {
        	        if (!nameInput.getText().isEmpty()) {
        	            playerName = nameInput.getText();
        	            // create new game with player's name
        	            gameEngine = new Game(playerName);
        	            // shuffle player colors
        	            initializeColours();
        	            playerengine = gameEngine.getPlayers().get(0); 
        	            boardengine = new Board(colourOrder, gameEngine);
        	            marbles0 = createHomeZone(getColour(0));
        	            marbles1 = createHomeZone(getColour(1));
        	            marbles2 = createHomeZone(getColour(2));
        	            marbles3 = createHomeZone(getColour(3));
        	            
        	            marbles = new ArrayList<Circle>() {{
        	                addAll(marbles0);
        	                addAll(marbles1);
        	                addAll(marbles2);
        	                addAll(marbles3);
        	            }};
        	            // build the game interface
        	            setupGameUI(gameLayout);
        	            // switch to game view
        	            stage.setScene(new Scene(gameLayout, 1400, 1000));
        	            // update initial game state
        	            updateGameState();
        	            printArrayList();
        	            
        	        } 
        	    } catch (Exception ex) {
        	        showError("game start failed: " + ex.getMessage());
        	    }
        	});

        
        // show the start screen
        stage.setScene(new Scene(startScreen, 400, 300));//change size
        stage.setTitle("Jackroo");
        stage.setResizable(true);
        adjustGameScale(0.6);
        stage.show();
       
    }
    private void loadCardImages() {
        try {
            // SPADES
            SPADES_1 = new Image(getClass().getResourceAsStream("/cards/Spade1.png"));
            SPADES_2 = new Image(getClass().getResourceAsStream("/cards/Spade2.png"));
            SPADES_3 = new Image(getClass().getResourceAsStream("/cards/Spade3.png"));
            SPADES_4 = new Image(getClass().getResourceAsStream("/cards/Spade4.png"));
            SPADES_5 = new Image(getClass().getResourceAsStream("/cards/Spade5.png"));
            SPADES_6 = new Image(getClass().getResourceAsStream("/cards/Spade6.png"));
            SPADES_7 = new Image(getClass().getResourceAsStream("/cards/Spade7.png"));
            SPADES_8 = new Image(getClass().getResourceAsStream("/cards/Spade8.png"));
            SPADES_9 = new Image(getClass().getResourceAsStream("/cards/Spade9.png"));
            SPADES_10 = new Image(getClass().getResourceAsStream("/cards/Spade10.png"));
            SPADES_11 = new Image(getClass().getResourceAsStream("/cards/Spade11.png")); // Jack
            SPADES_12 = new Image(getClass().getResourceAsStream("/cards/Spade12.png")); // Queen
            SPADES_13 = new Image(getClass().getResourceAsStream("/cards/Spade13.png")); // King
            // HEARTS
            HEARTS_1 = new Image(getClass().getResourceAsStream("/cards/Heart1.png"));
            HEARTS_2 = new Image(getClass().getResourceAsStream("/cards/Heart2.png"));
            HEARTS_3 = new Image(getClass().getResourceAsStream("/cards/Heart3.png"));
            HEARTS_4 = new Image(getClass().getResourceAsStream("/cards/Heart4.png"));
            HEARTS_5 = new Image(getClass().getResourceAsStream("/cards/Heart5.png"));
            HEARTS_6 = new Image(getClass().getResourceAsStream("/cards/Heart6.png"));
            HEARTS_7 = new Image(getClass().getResourceAsStream("/cards/Heart7.png"));
            HEARTS_8 = new Image(getClass().getResourceAsStream("/cards/Heart8.png"));
            HEARTS_9 = new Image(getClass().getResourceAsStream("/cards/Heart9.png"));
            HEARTS_10 = new Image(getClass().getResourceAsStream("/cards/Heart10.png"));
            HEARTS_11 = new Image(getClass().getResourceAsStream("/cards/Heart11.png"));
            HEARTS_12 = new Image(getClass().getResourceAsStream("/cards/Heart12.png"));
            HEARTS_13 = new Image(getClass().getResourceAsStream("/cards/Heart13.png"));
            // CLUBS
            CLUBS_1 = new Image(getClass().getResourceAsStream("/cards/Club1.png"));
            CLUBS_2 = new Image(getClass().getResourceAsStream("/cards/Club2.png"));
            CLUBS_3 = new Image(getClass().getResourceAsStream("/cards/Club3.png"));
            CLUBS_4 = new Image(getClass().getResourceAsStream("/cards/Club4.png"));
            CLUBS_5 = new Image(getClass().getResourceAsStream("/cards/Club5.png"));
            CLUBS_6 = new Image(getClass().getResourceAsStream("/cards/Club6.png"));
            CLUBS_7 = new Image(getClass().getResourceAsStream("/cards/Club7.png"));
            CLUBS_8 = new Image(getClass().getResourceAsStream("/cards/Club8.png"));
            CLUBS_9 = new Image(getClass().getResourceAsStream("/cards/Club9.png"));
            CLUBS_10 = new Image(getClass().getResourceAsStream("/cards/Club10.png"));
            CLUBS_11 = new Image(getClass().getResourceAsStream("/cards/Club11.png"));
            CLUBS_12 = new Image(getClass().getResourceAsStream("/cards/Club12.png"));
            CLUBS_13 = new Image(getClass().getResourceAsStream("/cards/Club13.png"));
            // DIAMONDS
            DIAMONDS_1 = new Image(getClass().getResourceAsStream("/cards/Diamond1.png"));
            DIAMONDS_2 = new Image(getClass().getResourceAsStream("/cards/Diamond2.png"));
            DIAMONDS_3 = new Image(getClass().getResourceAsStream("/cards/Diamond3.png"));
            DIAMONDS_4 = new Image(getClass().getResourceAsStream("/cards/Diamond4.png"));
            DIAMONDS_5 = new Image(getClass().getResourceAsStream("/cards/Diamond5.png"));
            DIAMONDS_6 = new Image(getClass().getResourceAsStream("/cards/Diamond6.png"));
            DIAMONDS_7 = new Image(getClass().getResourceAsStream("/cards/Diamond7.png"));
            DIAMONDS_8 = new Image(getClass().getResourceAsStream("/cards/Diamond8.png"));
            DIAMONDS_9 = new Image(getClass().getResourceAsStream("/cards/Diamond9.png"));
            DIAMONDS_10 = new Image(getClass().getResourceAsStream("/cards/Diamond10.png"));
            DIAMONDS_11 = new Image(getClass().getResourceAsStream("/cards/Diamond11.png"));
            DIAMONDS_12 = new Image(getClass().getResourceAsStream("/cards/Diamond12.png"));
            DIAMONDS_13 = new Image(getClass().getResourceAsStream("/cards/Diamond13.png"));
            CARD_BACK = new Image(getClass().getResourceAsStream("/cards/Back.png"));
            // Wild Cards
            MARBLE_BURNER = new Image(getClass().getResourceAsStream("/cards/Burner.png"));
            MARBLE_SAVER = new Image(getClass().getResourceAsStream("/cards/Saver.png"));

            PATRICK_NORMAL = new Image(getClass().getResourceAsStream("/characters/patrick_normal.png"));
            PATRICK_THINKING = new Image(getClass().getResourceAsStream("/characters/patrick_thinking.png"));
            SPONGEBOB_NORMAL = new Image(getClass().getResourceAsStream("/characters/spongebob_normal.png"));
            SPONGEBOB_THINKING = new Image(getClass().getResourceAsStream("/characters/spongebob_thinking.png"));
            SQUIDWARD_NORMAL = new Image(getClass().getResourceAsStream("/characters/squidward_normal.png"));
            SQUIDWARD_THINKING = new Image(getClass().getResourceAsStream("/characters/squidward_thinking.png"));
            GARY_NORMAL = new Image(getClass().getResourceAsStream("/characters/gary_normal.png"));
            GARY_THINKING = new Image(getClass().getResourceAsStream("/characters/gary_thinking.png"));
            
        } catch (Exception e) {
            showError("Failed to load card images: " + e.getMessage());
            e.printStackTrace();
        }
    }  
    private StackPane createCharacterPane(Colour color) {
        ImageView character = new ImageView();
        character.setFitWidth(200);
        character.setFitHeight(200);
        character.setPreserveRatio(true);

        Rectangle border = new Rectangle(210, 210);
        border.setArcWidth(20);
        border.setArcHeight(20);
        border.setFill(Color.TRANSPARENT);
        border.setStrokeWidth(3);
        border.setStroke(new LinearGradient(0,0,1,1,true,CycleMethod.NO_CYCLE,
            new Stop(0, Color.GOLD), new Stop(1, Color.WHITE)));
        border.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.7)));

        StackPane pane = new StackPane();
        pane.getChildren().addAll(border, character);
        
        switch(color) {
            case RED: character.setImage(PATRICK_NORMAL); break;
            case YELLOW: character.setImage(SPONGEBOB_NORMAL); break;
            case BLUE: character.setImage(SQUIDWARD_NORMAL); break;
            case GREEN: character.setImage(GARY_NORMAL); break;
        }

        return pane;
    }
    void adjustGameScale(double factor) {
        gameLayout.getTransforms().clear();
        gameLayout.getTransforms().add(new Scale(1, factor));}
      

    // mix up the colors for random assignments
    private void initializeColours() {
    	for(int i=0; i<4;i++)
    	colourOrder.add(gameEngine.getPlayers().get(i).getColour());
       
    }

    // get color for specific player index
    private Colour getColour(int index) {
        return colourOrder.get(index);
    }

    // convert our color enum to javaFX color object
    private Color convertColour(Colour colour) {
        switch(colour) {
            case GREEN: return guiColours[0];
            case RED: return guiColours[1];
            case YELLOW: return guiColours[2];
            case BLUE: return guiColours[3];
            default: throw new IllegalArgumentException("unknown color, uh oh");
        }
    }

    // arrange the game screen layout
    private void setupGameUI(BorderPane layout) {
        
    	layout.setTop(createCPUZone(gameEngine.getPlayers().get(2),2));    // CPU 2 at top
    	layout.setLeft(createCPUZone(gameEngine.getPlayers().get(1),1));     // CPU 3 at left
    	layout.setRight(createCPUZone(gameEngine.getPlayers().get(3), 3));   // CPU 1 at right
    	layout.setBottom(createHumanPlayerZone(0)); // you at bottom
    	layout.setCenter(createMainBoardArea()); // game board in center
    }

    // creates the main game board area
    private VBox createMainBoardArea() {
    	
        VBox boardArea = new VBox(10);
        boardArea.setAlignment(Pos.CENTER);
       
        
        // status bar at top
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10;");
        
        // show human player's color
        Colour humanColor = getColour(0);
        Label playerColorLabel = new Label("your color: " + humanColor);
        playerColorLabel.setTextFill(convertColour(humanColor));
        playerColorLabel.setStyle("-fx-font-weight: bold;");
        
        
        // style the info labels
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 16;");
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        firePitLabel.setStyle("-fx-font-weight: bold;");
        winnerLabel.setStyle("-fx-font-size: 16; -fx-text-fill: green; -fx-font-weight: bold;");
        next.setStyle("-fx-font-weight: bold;");
        // add elements to status bar
        statusBar.getChildren().addAll( playerColorLabel, statusLabel,next, firePitLabel, winnerLabel, errorLabel);
        
        // main board with home zones
        
        
        // load the game board image
        int boxSize = 25;       
       
        
        //Board b = new Board(colourOrder,gamemanager);
        
        //guiTrack.setStyle("-fx-grid-lines-visible: true;");//comment when not debugging DO NOT REMOVE
        guiTrack.setPadding(new Insets(10));
        guiTrack.setAlignment(Pos.CENTER);  // This centers the entire grid in its container
        guiTrack.setHgap(5); // Horizontal spacing between columns
        guiTrack.setVgap(5); // Vertical spacing between rows
        
        //SAFEZONE
        refreshSafeZone();
//        for (int col = 1; col < 5; col++) {
//            guiTrack.add(createfillcircle(getColour(1)), col, 1+boxSize/2); // LEFT
//            guiTrack.add(createfillcircle(getColour(3)), boxSize-col+1, 1 + boxSize/2); // RIGHT
//            
//            
//        }
//        for (int row = 1; row < 5; row++) {
//        	guiTrack.add(createfillcircle(getColour(2)),1+boxSize/2, row);//TOP
//        	guiTrack.add(createfillcircle(getColour(0)),1+boxSize/2, boxSize-row+1);// BOTTOM
//        }

        makeTrack();
     
        //BASECELL
        
        guiTrack.add(createfillcircle(getColour(1)), 0, boxSize/2-1); // LEFT
        guiTrack.add(createfillcircle(getColour(0)), boxSize+1,2+boxSize/2+1); // RIGHT
        guiTrack.add(createfillcircle(getColour(3)),boxSize/2+3, 0);//TOP
    	guiTrack.add(createfillcircle(getColour(2)),boxSize/2-1, boxSize+1);// BOTTOM
    	
    	//FIREPITTTTTTTTTT AHAHAHAHHAHAHA....added for firepit
    	StackPane firePit = createFirePit();
    	StackPane firePitWrapper = new StackPane(firePit);
        firePitWrapper.setMouseTransparent(true);
        firePitWrapper.setPickOnBounds(false);
        StackPane boardWithOverlay = new StackPane();
        boardWithOverlay.getChildren().addAll(guiTrack, firePitWrapper);
        boardWithOverlay.setMouseTransparent(false);  
        boardWithOverlay.setPickOnBounds(true);  

  
        	
    	redrawHomeZones();
    	StackPane.setAlignment(firePitWrapper, Pos.TOP_CENTER); //added for firepit
        boardArea.getChildren().addAll(boardWithOverlay, statusBar);
        return boardArea;        
    }
    //added for firepit(comments for debugging :) )
    private StackPane createFirePit() {
        Rectangle brown = new Rectangle(60, 60, Color.SADDLEBROWN);
        Rectangle red = new Rectangle(45, 45, Color.RED);
        Rectangle orange = new Rectangle(30, 30, Color.ORANGE);
        Rectangle yellow = new Rectangle(15, 15, Color.YELLOW);

        firePitCardView = new ImageView();
        firePitCardView.setFitWidth(80);  
        firePitCardView.setFitHeight(140);
        firePitCardView.setVisible(false);
        
        StackPane firePit = new StackPane();
        // Add the card view LAST so it appears on top
        firePit.getChildren().addAll(brown, red, orange, yellow, firePitCardView); // FIXED HERE
        
        // Rest of the fire pit setup
        firePit.setAlignment(Pos.CENTER);
        DropShadow glow = new DropShadow(20, Color.GOLD);
        glow.setInput(new DropShadow(40, Color.ORANGE));
        firePit.setEffect(glow);

        Timeline glowPulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.radiusProperty(), 20)),
            new KeyFrame(Duration.seconds(1), new KeyValue(glow.radiusProperty(), 40)),
            new KeyFrame(Duration.seconds(2), new KeyValue(glow.radiusProperty(), 20))
        );
        glowPulse.setCycleCount(Timeline.INDEFINITE);
        glowPulse.play();

        return firePit;
    }

    private void updateFirePitDisplay() { //firepit
        if (!gameEngine.getFirePit().isEmpty()) {
            Card topCard = gameEngine.getFirePit().get(gameEngine.getFirePit().size() - 1);
            firePitCardView.setImage(getCardImage(topCard));
            firePitCardView.setVisible(true);
            
            // Add animation for new cards
            ScaleTransition scale = new ScaleTransition(Duration.seconds(0.2), firePitCardView);
            scale.setFromX(0.5); scale.setFromY(0.5);
            scale.setToX(1); scale.setToY(1);
            scale.play();
        } else {
            firePitCardView.setVisible(false);
        }
    }

    private Image getCardImage(Card card) { //firepit
        if (card instanceof Standard) {
            Standard s = (Standard) card;
            switch(s.getSuit()) {
                case CLUB: return getClubsImage(s.getRank());
                case DIAMOND: return getDiamondsImage(s.getRank());
                case HEART: return getHeartsImage(s.getRank());
                case SPADE: return getSpadesImage(s.getRank());
            }
        } else if (card instanceof Burner) {
            return MARBLE_BURNER;
        } else if (card instanceof Saver) {
            return MARBLE_SAVER;
        }
        return null;
    }
    private void makeTrack(){
    	int boxSize = 25;
    	//TRACK
        for (int col = 1; col < boxSize+1; col++) {
            guiTrack.add(new Circle(10, Color.rgb(140, 90, 60) ) , col, 0); // Top
            guiTrack.add(new Circle(10, Color.rgb(140, 90, 60) ), col, boxSize+1); // Bottom
           	top.add(new int[]{col,0});
            if(col<=11){
            	botttom1.add(new int[]{12-col, boxSize+1});}
            if(col<15){
            	botttom2.add(new int[]{26-col, boxSize+1});}
        }
        for (int row = 1; row < boxSize+1; row++) {
            guiTrack.add(new Circle(10, Color.rgb(140, 90, 60)), 0, row); // Left
            guiTrack.add(new Circle(10, Color.rgb(140, 90, 60)), boxSize+1, row); // Right
            right.add( new int[]{boxSize+1, row});
            left.add(new int[]{0,26-row});
        }
        all.addAll(botttom1);
        all.addAll(left);
        all.addAll(top);
        all.addAll(right);
        all.addAll(botttom2);
    }
    public static int[] getNodeCoordinates(GridPane grid, Node target) {
        for (Node node : grid.getChildren()) {
            if (node.equals(target)) {
                Integer row = GridPane.getRowIndex(node);
                Integer col = GridPane.getColumnIndex(node);
                return new int[]{
                	    row == null ? 0 : row,
                	    col == null ? 0 : col
                	};
            }
        }
        return null; // Not found
    }
    
    private int find(int[] x,ArrayList<int[]> l ){
    	int[] c;
    	for(int i = 0; i < l.size(); i++){
    		c = l.get(i);
    		if(x == c)
    			return i;
    	}
    	return -1;
    }
    
    private void selectyMarable(ArrayList<Circle> homemarbles2){
    	for(int i=0; i< homemarbles2.size(); i++){
    		int[] index = getNodeCoordinates(guiTrack,homemarbles2.get(i));
    		if(index == null){
    			return;
    		}
    		int pos = find(index,all);
    		
    		try {
    		if(pos == -1){
    			gameEngine.selectMarble(gameEngine.getPlayers().get(0).getOneMarble());
    			return;//try safezone
    		}
    		
    		
				gameEngine.selectMarble(gameEngine.getBoard().getTrack().get(pos).getMarble()); // selects the marble
			} catch (InvalidMarbleException e) {
				// TODO Auto-generated catch block
				showError("slectymarble errorred yayyyyyy");
				e.printStackTrace();
			}}
    }
    //deepseek
    private void redrawHomeZones() {
        // Clear existing home zone marbles
       ArrayList<Marble> homemar ;
       
       int[] index;
        // Redraw home zones for all players
        for (int playerIndex = 0; playerIndex < 4; playerIndex++) {
            ArrayList<Circle> playerMarbles;
            switch (playerIndex) {
                case 0: playerMarbles = marbles0; 
                homemar = gameEngine.getPlayers().get(0).getMarbles();
             	break;
                case 1: playerMarbles = marbles1; 
        		homemar = gameEngine.getPlayers().get(1).getMarbles();
        		break;
                case 2: playerMarbles = marbles2;  
        		homemar = gameEngine.getPlayers().get(2).getMarbles();
        		break;
                case 3: playerMarbles = marbles3;  
        		homemar = gameEngine.getPlayers().get(3).getMarbles();
        		break;
                default: continue;
            }
          

            // Determine base coordinates based on player position
            int boxSize = 25;
            int baseX, baseY;
            
            switch (playerIndex) {
                case 0: // Bottom player (human)
                    baseX = boxSize - 2;
                    baseY = boxSize - 3;
                    break;
                case 1: // Left CPU
                    baseX = 3;
                    baseY = boxSize - 3;
                    break;
                case 2: // Top CPU
                    baseX = 3;
                    baseY = 2;
                    break;
                case 3: // Right CPU
                    baseX = boxSize - 2;
                    baseY = 2;
                    break;
                default:
                    continue;
            }
            

            for(int i = 0; i<playerMarbles.size(); i++){
            	index = getNodeCoordinates(guiTrack, playerMarbles.get(i));
            	if(all.contains(index)){
            		playerMarbles.remove(i);
            		i--;
            	}
            }
            guiTrack.getChildren().removeAll(playerMarbles);
            // Place marbles in diamond pattern
            if(homemar.size()>0){
            
            guiTrack.add(playerMarbles.get(0), baseX, baseY);
            playerMarbles.add(playerMarbles.remove(0));}
            if(homemar.size()>1){
            guiTrack.add(playerMarbles.get(0), baseX, baseY + 2);
            playerMarbles.add(playerMarbles.remove(0));}
            if(homemar.size()>2){
            guiTrack.add(playerMarbles.get(0), baseX - 1, baseY + 1);
            playerMarbles.add(playerMarbles.remove(0));}
            if(homemar.size()>3){
            guiTrack.add(playerMarbles.get(0), baseX + 1, baseY + 1);
             playerMarbles.add(playerMarbles.remove(0));
        }}
    }
  
    private void refreshTrack(){
        //POSITION OF MARBLE
        
        ArrayList<Cell> tracky = gameEngine.getBoard().getTrack();
        Marble m = null;
        refreshSafeZone();
        redrawHomeZones();
        //re-put all marbles
        for(int i = 0; i<tracky.size(); i++){
     	   
        		m = tracky.get(i).getMarble();
        		if(m== null){
        			if(tracky.get(i).isTrap()){
        	
        				guiTrack.add(new Circle(10,Color.BLACK),all.get(i)[1],all.get(i)[0]);
        			}
        			
        			}
        		else if(m.getColour().equals(getColour(0))){
        			guiTrack.getChildren().remove(marbles0.get(0));
        			guiTrack.add(marbles0.get(0),all.get(i)[0],all.get(i)[1]);
        			marbles0.add(marbles0.remove(0));
        		}
        		
        		else if(m.getColour().equals(getColour(1))){
        			guiTrack.getChildren().remove(marbles1.get(0));
        			guiTrack.add(marbles1.get(0),all.get(i)[0],all.get(i)[1]);
        			marbles1.add(marbles1.remove(0));
        		}
        		else if(m.getColour().equals(getColour(2))){
        			guiTrack.getChildren().remove(marbles2.get(0));
        			guiTrack.add(marbles2.get(0),all.get(i)[0],all.get(i)[1]);
        			marbles2.add(marbles2.remove(0));
        		}
        		else if(m.getColour().equals(getColour(3))){
        			guiTrack.getChildren().remove(marbles3.get(0));
        			guiTrack.add(marbles3.get(0),all.get(i)[0],all.get(i)[1]);
        			marbles3.add(marbles3.remove(0));
        		}
        }
       
    }
    private Circle createfillcircle(Colour colour){
    	Circle circle = new Circle(10); // radius only
    	circle.setStroke(Color.rgb(140, 90, 60));
    	circle.setStrokeWidth(2);
        circle.setFill(convertColour(colour)); // set color separately
        return circle;
    }

   
    // creates a home zone with clickable marbles
    //not all are hbox and try making it a square or diamond using grid
    private ArrayList<Circle> createHomeZone(Colour colour) {
    	ArrayList<Circle> homemarbles = new ArrayList<Circle>();
        
        // create 4 marbles for the home zone
        for (int i = 0; i < 4; i++) {
            Circle marble = new Circle(10, convertColour(colour));
            marble.setStroke(Color.BLACK);
            marble.setPickOnBounds(true);//added for firepit
            marble.setMouseTransparent(false);//added for firepit
            // make marbles clickable if it's the human's zone
            if (colour == getColour(0)) {
                marble.setOnMouseClicked(e -> {
                    // deselect previous marble forgot case sevennnnn
                	if(selectedMarble.contains(marble)){
    	        		marble.setStrokeWidth(1);
    	        		marble.setRadius(10);
    	                selectedMarble.remove((marble));
    	                
    	                
    	                return;
                	} 
                	else if (selectedMarble.size() < 2){
                		selectedMarble.add(marble);
                		marble.setStrokeWidth(2);
                        marble.setRadius(9.5);
                		
                	}
                	else if (selectedMarble.size() == 2){
                	
                		selectedMarble.get(0).setStrokeWidth(1);
                		selectedMarble.remove(0);
                		selectedMarble.get(0).setRadius(10);
                		selectedMarble.add(marble);
                		marble.setStrokeWidth(2);
                        marble.setRadius(9.5);
                	     	                       
                    }
                    // select new marble  
                });
            }
            homemarbles.add(marble); 
        }
        return homemarbles;
    }

	// creates the human player's control area
    private VBox createHumanPlayerZone(int playerIndex) {
        VBox playerZone = new VBox(20);
        playerZone.setAlignment(Pos.CENTER);
        playerZone.setPadding(new Insets(20));
        
        try {
            Player humanPlayer = gameEngine.getPlayers().get(playerIndex);
            Colour humanColor = getColour(playerIndex);
            
            // set zone color
            StackPane characterPane = createCharacterPane(humanColor);
            playerCharacterPanes.put(playerIndex, characterPane);
            
            playerZone.setStyle("-fx-background-color: " + toPastelHex(convertColour(humanColor)) + "; -fx-background-radius: 10;");
            
            // player info display
            humanInfoLabel = new Label(
                    playerName + "\n" +
                    "color: " + humanColor + "\n" +
                    "cards left: " + humanPlayer.getHand().size()
                );
                humanInfoLabel.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");
            
            // card display area
            humanCards = new HBox(10);
            humanCards.setAlignment(Pos.CENTER);
            refreshCPUZone();
            
            // action buttons
            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER);
            
            Button playBtn = new Button("play card");
            Button endBtn = new Button("discard card");
           
            
            // style buttons
            playBtn.setStyle("-fx-font-weight: bold; -fx-padding: 8 16;");
            endBtn.setStyle("-fx-font-weight: bold; -fx-padding: 8 16;");
            endBtn.setOnAction(e -> endTurn());
            
            
            // button actions
            playBtn.setOnAction(e -> {
                try {
                    if (selectedCard == null) {
                        showError("Please select a card first!");
                        return;
                    } 
                    Marble m;
                   selectyMarable(selectedMarble); //idk why its not working
                    for(Circle c: selectedMarble){
                    	 m = gameEngine.getPlayers().get(0).getOneMarble();
                    	gameEngine.getPlayers().get(0).selectMarble(m);
                    }
                    playerengine.play();
                    
                    playerengine.getHand().remove(selectedCard);
                    gameEngine.getFirePit().add(selectedCard); 
                    updateFirePitDisplay(); //firepit
                    refreshHumanCards(playerengine);
                    updateHumanInfo();
        
                    gameEngine.deselectAll();
                    gameEngine.endPlayerTurn();
                   
                    refreshCPUZone();
                    
                   
                    if (selectedCardButton != null) {
                        selectedCardButton.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");
                        selectedCardButton = null;
                    }
                    
                    // Let updateGameState handle the turn transition
                    updateGameState();
                } catch (Exception ex) {
                    showError("Error playing card: " + ex.getMessage());
                    
                }
            });
             


            // arrange elements
            buttons.getChildren().addAll(playBtn,endBtn);
            playerZone.getChildren().addAll(characterPane,humanInfoLabel, humanCards, buttons);
            
        } catch (Exception e) {
            showError("problem creating your area: " + e.getMessage());
        }
        return playerZone;
    }
    private void updateHumanInfo() {
        int cardsLeft = playerengine.getHand().size();
        humanInfoLabel.setText(
            playerName + "\n" +
            "color: " + getColour(0) + "\n" +
            "cards left: " + cardsLeft
        );
    }
//    for (int col = 1; col < 5; col++) {
//        guiTrack.add(createfillcircle(getColour(1)), col, 1+boxSize/2); // LEFT
//        guiTrack.add(createfillcircle(getColour(3)), boxSize-col+1, 1 + boxSize/2); // RIGHT
//        
//        
//    }
//    for (int row = 1; row < 5; row++) {
//    	guiTrack.add(createfillcircle(getColour(2)),1+boxSize/2, row);//TOP
//    	guiTrack.add(createfillcircle(getColour(0)),1+boxSize/2, boxSize-row+1);// BOTTOM
//    }
    private void refreshSafeZone(){
    	ArrayList<SafeZone> safeZoneall = gameEngine.getBoard().getSafeZones();
    	ArrayList<Cell> celly;
    	Marble m;
    		celly = safeZoneall.get(0).getCells();
    		for (int row = 0; row < 4; row++) {
    			m = celly.get(row).getMarble();
    			if(m== null){
    				guiTrack.add(createfillcircle(getColour(0)),1+25/2, 25-row);
        			}
        			
        			
        		else {
        			guiTrack.getChildren().remove(marbles0.get(0));
        			guiTrack.add(marbles0.get(0),1+25/2, 25-row);
        			marbles0.add(marbles0.remove(0));
        		}
    		}
            celly = safeZoneall.get(1).getCells();
            for (int row = 0; row < 4; row++) {
    			m = celly.get(row).getMarble();
    			if(m== null){
    				guiTrack.add(createfillcircle(getColour(1)), row+1, 1+25/2);
        			}
        			
        			
        		else {
        			guiTrack.getChildren().remove(marbles1.get(0));
        			guiTrack.add(marbles1.get(0), row+1, 1+25/2);
        			marbles1.add(marbles1.remove(0));
        		}
    		}
            
            celly = safeZoneall.get(2).getCells();
            for (int row = 0; row < 4; row++) {
    			m = celly.get(row).getMarble();
    			if(m== null){
    				guiTrack.add(createfillcircle(getColour(2)),1+25/2, row+1);
        			}
        			
        			
        		else {
        			guiTrack.getChildren().remove(marbles2.get(0));
        			guiTrack.add(marbles2.get(0),1+25/2, row+1);
        			marbles2.add(marbles2.remove(0));
        		}
    		}
           
            celly = safeZoneall.get(3).getCells();
            for (int row = 0; row < 4; row++) {
    			m = celly.get(row).getMarble();
    			if(m== null){
    				guiTrack.add(createfillcircle(getColour(3)), 25-row, 1 + 25/2);
        			}
        			
        			
        		else {
        			guiTrack.getChildren().remove(marbles3.get(0));
        			guiTrack.add(marbles3.get(0), 25-row, 1 + 25/2);
        			marbles3.add(marbles3.remove(0));
        		}
    		}
    	
    	
    	
    }
    
    private void refreshCPUZone() {
    	refreshHumanCards(gameEngine.getPlayers().get(0));
        VBox cpuZone;
        for(int cpuIndex=1; cpuIndex<4; cpuIndex++ ){
        	
	        if (cpuIndex == 3) {
	            cpuZone = (VBox) gameLayout.getRight();
	        } else if (cpuIndex == 2) {
	            cpuZone = (VBox) gameLayout.getTop();
	        } else if (cpuIndex == 1) {
	            cpuZone = (VBox) gameLayout.getLeft();
	        } else {
	            return; // Human player
	        }
	
	        // Clear and rebuild CPU card display
	        HBox cards = (HBox) cpuZone.getChildren().get(1);
	        cards.getChildren().clear();
	        
	        Player cpuPlayer = gameEngine.getPlayers().get(cpuIndex);
	        for (Card card : cpuPlayer.getHand()) {
	            Button cardBtn = new Button("?");
	            cardBtn.setDisable(true);
	            cardBtn.setStyle("-fx-opacity: 1; -fx-background-color: #dddddd;");
	            cards.getChildren().add(cardBtn);
	        }
        

        // Clear and rebuild CPU card display
         cards = (HBox) cpuZone.getChildren().get(1);
        cards.getChildren().clear();
        
        Player cpuPlayer1 = gameEngine.getPlayers().get(cpuIndex);
        for (Card card : cpuPlayer1.getHand()) {
            Button cardBtn = new Button();
            ImageView cardBack = new ImageView(CARD_BACK);
            cardBack.setFitWidth(50);     
            cardBack.setFitHeight(80);     
            cardBtn.setGraphic(cardBack);
            cardBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            cardBtn.setDisable(true);
            cardBtn.setStyle("-fx-opacity: 1;");
            cards.getChildren().add(cardBtn);
        }}
    }
    // creates a CPU player's display area
    private VBox createCPUZone(Player cpu,int cpuIndex) {
        VBox cpuZone = new VBox(10);
        cpuZone.setPadding(new Insets(20));
        cpuZone.setAlignment(Pos.CENTER);
        Colour cpuColour = getColour(cpuIndex);
        
        StackPane characterPane = createCharacterPane(cpuColour);
        playerCharacterPanes.put(cpuIndex, characterPane);
        
        cpuZone.setStyle("-fx-background-color: " + toPastelHex(convertColour(cpuColour)) + 
                "; -fx-background-radius: 10; -fx-border-color: #aaaaaa; -fx-border-radius: 10;");

        
        try {
            Player cpuPlayer = cpu;
            
            // set background color
            cpuZone.setStyle("-fx-background-color: " + toPastelHex(convertColour(cpuColour)) + "; -fx-background-radius: 10;");
            
            // CPU info display
            Label info = new Label(
                "CPU " + (cpuIndex) + "\n" +
                "color: " + cpuColour + "\n" +
                "cards left: " + cpuPlayer.getHand().size()
            );
            info.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");
            cpuInfoLabels.put(cpuIndex, info);
            
            // show CPU's cards (face down)
            HBox cards = new HBox(10);
            cards.setAlignment(Pos.CENTER);
            for (Card card : cpuPlayer.getHand()) {
                Button cardBtn = new Button();
                ImageView cardBack = new ImageView(CARD_BACK);
                cardBack.setFitWidth(80);       // Match human card width
                cardBack.setFitHeight(120);     // Match human card height
                cardBtn.setGraphic(cardBack);
                cardBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                cardBtn.setDisable(true);
                cardBtn.setStyle("-fx-opacity: 1;");
                cards.getChildren().add(cardBtn);
            }
            
            cpuZone.getChildren().addAll(info, cards);
        } catch (Exception e) {
            showError("problem creating CPU area: " + e.getMessage());
        }
        return cpuZone;
    }
 // Make sure this import is at the top

    private void showError(String message) {
        if (!message.equals("Update error: null")) {

            // 🔊 Play error sound
        	
        	String filePath = "C:/Users/salmo/Downloads/JackarooBonusNato/JackarooM2Solution-Koki/src/gui/erro.wav";
            Media media = new Media(Paths.get(filePath).toUri().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(0.1);
            mediaPlayer.play();
 

            Stage newWindow = new Stage();
            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);
            layout.setStyle("-fx-padding: 20; -fx-background-color: navy;");

            Label errorLabel = new Label(message);
            errorLabel.setTextFill(Color.MAGENTA);
            errorLabel.setStyle("-fx-font-weight: bold;");

            Button close = new Button("Continue playing");
            close.setOnAction(e -> newWindow.close());

            layout.getChildren().addAll(close, errorLabel);

            Scene popupScene = new Scene(layout, 300, 200);
            newWindow.setScene(popupScene);
            newWindow.setTitle("Error :( ");
            newWindow.initModality(Modality.APPLICATION_MODAL);
            newWindow.showAndWait();
        }
    }



	    private void updateCPUInfo(int cpuIndex) {
	        Player cpu = gameEngine.getPlayers().get(cpuIndex);
	        int cardsLeft = cpu.getHand().size();
	        Label label = cpuInfoLabels.get(cpuIndex);
	        
	        if (label != null) {
	            label.setText(
	                "CPU " + cpuIndex + "\n" +
	                "color: " + getColour(cpuIndex) + "\n" +
	                "cards left: " + cardsLeft
	            );
	        }
	    }
    
    private void refreshHumanCards(Player player) {
        humanCards.getChildren().clear();
        // Iterate over COPY of hand to prevent disappearance bugs
        new ArrayList<>(player.getHand()).forEach(card -> {
            humanCards.getChildren().add(createCardButton(card));
        });
    }
		
    private Button createCardButton(Card card) {
        Button btn = new Button();
        btn.setPickOnBounds(true);
        btn.setMouseTransparent(false);
        btn.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 2;");
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setPadding(Insets.EMPTY);

        ImageView cardImage = new ImageView();
        cardImage.setFitWidth(100);
        cardImage.setFitHeight(140);

        if (card instanceof Standard) {
            Standard s = (Standard) card;
            switch(s.getSuit()) {
                case CLUB:
                    cardImage.setImage(getClubsImage(s.getRank()));
                    break;
                case DIAMOND:
                    cardImage.setImage(getDiamondsImage(s.getRank()));
                    break;
                case HEART:
                    cardImage.setImage(getHeartsImage(s.getRank()));
                    break;
                case SPADE:
                    cardImage.setImage(getSpadesImage(s.getRank()));
                    break;
            }
        } else if (card instanceof Burner) {
            cardImage.setImage(MARBLE_BURNER);
        } else if (card instanceof Saver) {
            cardImage.setImage(MARBLE_SAVER);
        }

        btn.setGraphic(cardImage);

        btn.setOnAction(e -> {
            if (gameEngine.getActivePlayerColour() != getColour(0)) {
                showError("Not your turn!");
                return;
            }

            if (selectedCardButton != null) {
                selectedCardButton.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");
                if(selectedCardButton == btn){
                    selectedCardButton = null;
                    selectedCard = null;
                    return;
                }
                selectedCard = null;
            }

            selectedCard = card;
            selectedCardButton = btn;
            btn.setStyle("-fx-background-color: white; -fx-border-color: gold; -fx-border-width: 3;");

            try {
                playerengine.selectCard(card);
            } catch (InvalidCardException ex) {
                showError(ex.getMessage());
            }
        });

        return btn;
    }
 // image helper methods
    private Image getClubsImage(int rank) {
        switch(rank) {
            case 1: return CLUBS_1;
            case 2: return CLUBS_2;
            case 3: return CLUBS_3;
            case 4: return CLUBS_4;
            case 5: return CLUBS_5;
            case 6: return CLUBS_6;
            case 7: return CLUBS_7;
            case 8: return CLUBS_8;
            case 9: return CLUBS_9;
            case 10: return CLUBS_10;
            case 11: return CLUBS_11;
            case 12: return CLUBS_12;
            case 13: return CLUBS_13;
            default: return CLUBS_1;
        }
    }

    private Image getDiamondsImage(int rank) {
        switch(rank) {
            case 1: return DIAMONDS_1;
            case 2: return DIAMONDS_2;
            case 3: return DIAMONDS_3;
            case 4: return DIAMONDS_4;
            case 5: return DIAMONDS_5;
            case 6: return DIAMONDS_6;
            case 7: return DIAMONDS_7;
            case 8: return DIAMONDS_8;
            case 9: return DIAMONDS_9;
            case 10: return DIAMONDS_10;
            case 11: return DIAMONDS_11;
            case 12: return DIAMONDS_12;
            case 13: return DIAMONDS_13;
            default: return DIAMONDS_1;
        }
    }

    private Image getHeartsImage(int rank) {
        switch(rank) {
            case 1: return HEARTS_1;
            case 2: return HEARTS_2;
            case 3: return HEARTS_3;
            case 4: return HEARTS_4;
            case 5: return HEARTS_5;
            case 6: return HEARTS_6;
            case 7: return HEARTS_7;
            case 8: return HEARTS_8;
            case 9: return HEARTS_9;
            case 10: return HEARTS_10;
            case 11: return HEARTS_11;
            case 12: return HEARTS_12;
            case 13: return HEARTS_13;
            default: return HEARTS_1;
        }
    }

    private Image getSpadesImage(int rank) {
        switch(rank) {
            case 1: return SPADES_1;
            case 2: return SPADES_2;
            case 3: return SPADES_3;
            case 4: return SPADES_4;
            case 5: return SPADES_5;
            case 6: return SPADES_6;
            case 7: return SPADES_7;
            case 8: return SPADES_8;
            case 9: return SPADES_9;
            case 10: return SPADES_10;
            case 11: return SPADES_11;
            case 12: return SPADES_12;
            case 13: return SPADES_13;
            default: return SPADES_1;
        }
    }
		
	private void updateGameState() {
		try {
        // Update fire pit display
//        if (!gameEngine.getFirePit().isEmpty()) {
//            Card topCard = gameEngine.getFirePit().get(gameEngine.getFirePit().size() - 1);
//            firePitLabel.setText("Fire Pit: " + topCard.getName());
//        }

			  for(int i = 0; i< selectedMarble.size(); i++){
                  if (selectedMarble.get(i) != null) {
                  	selectedMarble.get(i).setStrokeWidth(1);
                  	selectedMarble.get(i).setRadius(10);;
                  }}
                  
                  selectedMarble.clear();
                  
    	
        // Update current player
        Colour currentColour = gameEngine.getActivePlayerColour();
        statusLabel.setText("Current Player: " + currentColour);
        next.setText("Next Player: " + gameEngine.getNextPlayerColour());

        // Check if human's turn
        boolean isHumanTurn = (currentColour == getColour(0));
        humanCards.getChildren().forEach(button -> button.setDisable(!isHumanTurn));

        refreshTrack();
        refreshCPUZone();
        updateFirePitDisplay();
      
        // Check for winner winner method in game
        checkWinner();
        
        for (int i = 0; i < 4; i++) {
            StackPane pane = playerCharacterPanes.get(i);
            ImageView imgView = (ImageView) pane.getChildren().get(1);
            boolean isActive = (getColour(i) == currentColour);

            ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.5), pane);
            if (isActive) {
                switch(getColour(i)) {
                    case RED: imgView.setImage(PATRICK_THINKING); break;
                    case YELLOW: imgView.setImage(SPONGEBOB_THINKING); break;
                    case BLUE: imgView.setImage(SQUIDWARD_THINKING); break;
                    case GREEN: imgView.setImage(GARY_THINKING); break;
                }
                pulse.setFromX(1);
                pulse.setFromY(1);
                pulse.setToX(1.1);
                pulse.setToY(1.1);
                pulse.setAutoReverse(true);
                pulse.setCycleCount(ScaleTransition.INDEFINITE);
                pulse.play();
            } else {
                switch(getColour(i)) {
                    case RED: imgView.setImage(PATRICK_NORMAL); break;
                    case YELLOW: imgView.setImage(SPONGEBOB_NORMAL); break;
                    case BLUE: imgView.setImage(SQUIDWARD_NORMAL); break;
                    case GREEN: imgView.setImage(GARY_NORMAL); break;
                }
                pulse.stop();
                pane.setScaleX(1);
                pane.setScaleY(1);
            }
        }
        // Handle CPU turn if needed
            	if (!isHumanTurn) {
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(event -> {
                        Platform.runLater(() -> {
                            try {
                                // Execute CPU turn
                                cputurn();
                              
                                // Refresh CPU display
                                int cpuIndex = colourOrder.indexOf(currentColour);
                                refreshCPUZone();
                                // End turn and update state
                                gameEngine.endPlayerTurn();
                                updateGameState();
                            } catch (Exception e) {
                                showError("CPU turn error: " + e.getMessage());
                            }
                        });
                    });
                    pause.play();
                }
            	
            } catch (Exception e) {
                showError("Update error: " + e.getMessage());
            }
        }
			
			private void endTurn() {
		        	  try {
		        		  

		        		
		                    if (selectedCard == null) {
		                        showError("Please select a card first!");
		                        return;
		                    }
		                  //  selectyMarable(selectedMarble); //idk why its not working
		                   
		                    
		                    playerengine.getHand().remove(selectedCard);
		                    gameEngine.getFirePit().add(selectedCard); 
		                    updateFirePitDisplay(); //firepit
		                    gameEngine.deselectAll();
		                   
		                    trapCellAlert(gameEngine.getCurrent());
		                    gameEngine.endPlayerTurn();

		                    refreshCPUZone();
		                  
		             
		                    if (selectedCardButton != null) {
		                        selectedCardButton.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");
		                        selectedCardButton = null;
		                    }
		                    
		                    // Let updateGameState handle the turn transition
		                    updateGameState();
		                } catch (Exception ex) {
		                    showError("Error playing card: " + ex.getMessage());
		                }
		        	 if (selectedCard == null) {
	                        showError("Please select a card first!");
	                        return;
	                    }
		        	
		        	
		             if (selectedCardButton != null) {
	                        selectedCardButton.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");
	                        selectedCardButton = null;
	                    }
		            
		           
		       
		    }
private void cputurn() {
    int cpuIndex = colourOrder.indexOf(gameEngine.getActivePlayerColour());
    Player cpu = gameEngine.getPlayers().get(cpuIndex);
    try {
        cpu.play();
    } catch (Exception e) {
        showError("CPU play problem: " + e.getMessage());
    }
}


private void showWinnerScene(Colour winner, ImageView winnerImage) {
    VBox root = new VBox(20);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(20));

    // Winner label
    Label winnerText = new Label("Winner: " + winner + "!");
    winnerText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

    // Message label based on winner
    Label messageLabel;
    if (winner == gameEngine.getPlayers().get(0).getColour()) {
        messageLabel = new Label("Congrats! You won!!!");
    } else {
        messageLabel = new Label("Wasted :( ");
        String filePath = "C:/Users/salmo/Downloads/JackarooBonusNato/JackarooM2Solution-Koki/src/gui/emotional-damage-meme.mp3";
        Media media = new Media(Paths.get(filePath).toUri().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.6);
        mediaPlayer.play();
        
    }
    messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));

    // Image view

    root.getChildren().addAll(winnerText, messageLabel, winnerImage);

    Scene winnerScene = new Scene(root, 400, 300);

    Stage winnerStage = new Stage();
    winnerStage.setTitle("Game Over");
    winnerStage.setScene(winnerScene);
    winnerStage.initModality(Modality.APPLICATION_MODAL);

    winnerStage.show();
}

private void checkWinner() {
    try {
        Colour winner = gameEngine.checkWin();
        if (winner != null) {
            // Load an image for winner scene
        	ImageView winnerImage = new ImageView(
        		    new Image("file:/C:/Users/salmo/Downloads/JackarooBonusNato/JackarooM2Solution-Koki/src/gui/winner.jpg")
        		);


            // Show the new winner scene with the message and image
            showWinnerScene(winner, winnerImage);
        }
    } catch (Exception e) {
        showError("Win check error: " + e.getMessage());
    }
}

		    private String toPastelHex(Color color) {
		        if (color.equals(guiColours[0])) return "#B3FFB3";
		        if (color.equals(guiColours[1])) return "#FFB3BA";
		        if (color.equals(guiColours[2])) return "#FFFFB3";
		        return "#B3D9FF";
		    }
		
		    
		   
		    public void printArrayList() {
		        System.out.println("ArrayList Contents:");
		        int i=0;
		        for (int[] array : all) {
		        	i++;
		            System.out.println(i+":"+Arrays.toString(array));
		        }
		    }
		    private void trapCellAlert(Player p){
		    	
	    		//ArrayList<Cell> leTrack = gameEngine.getBoard().getTrack();
	    		//Label trap = new Label( "Holy trap cell, "  + p.getName());
	    		ArrayList<Marble> validMarbles = gameEngine.getBoard().getActionableMarbles();
	    		ArrayList<Marble> selected = p.getMarbles(); 
	    		for(int i=0;i<selected .size();i++){
	    			if(!validMarbles.contains(selected.get(i))){
	    				showAutoClosingError("Holy trap cell, "  + p.getName()+"!");
	    			}
	    			return;
	    			
	    		}
	    	
	    }
	    
	    public void showAutoClosingError(String message) {
	        Stage popup = new Stage();
	        popup.initModality(Modality.APPLICATION_MODAL); // optional: block parent interaction

	        Label errorLabel = new Label(message);
	        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16; -fx-font-weight: bold;");

	        StackPane root = new StackPane(errorLabel);
	        root.setPrefSize(300, 100);
	        root.setAlignment(Pos.CENTER);

	        Scene scene = new Scene(root);
	        popup.setScene(scene);
	        popup.setTitle("Error");

	        popup.show();

	        // Close after 2 seconds
	        PauseTransition delay = new PauseTransition(Duration.seconds(2));
	        delay.setOnFinished(e -> popup.close());
	        delay.play();
	    }
		
		    public static void main(String[] args) {
		        launch(args);
		        
		    }
		    
		}