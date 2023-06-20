import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class GoBoomGameGUI extends Application {
    private static final int NUM_PLAYERS = 4;
    private static final int NUM_CARDS_PER_HAND = 5;

    private List<Player> players;
    private int currentPlayerIndex;
    private Deck deck;
    private List<Card> centerCards;

    private Button startButton;
    private Button drawButton;
    private Button playButton;
    private Button resetButton;
    private Label roundLabel;
    private Label trickLabel;
    private VBox playerPanel;
    private HBox centerPanel;
    private Label[] playerLabels;
    private Rectangle[] cardRectangles;

    private int roundNumber;
    private int trickNumber;

    private boolean gameStarted;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Go Boom Game");

        // Create UI elements
        startButton = new Button("Start Game");
        drawButton = new Button("Draw Card");
        playButton = new Button("Play Card");
        resetButton = new Button("Reset Game");
        roundLabel = new Label("Round: 1");
        trickLabel = new Label("Trick: 1");
        playerPanel = new VBox();
        centerPanel = new HBox();
        playerLabels = new Label[NUM_PLAYERS];
        cardRectangles = new Rectangle[NUM_CARDS_PER_HAND];

        // Configure UI elements
        startButton.setOnAction(event -> startGame());
        drawButton.setOnAction(event -> drawCard());
        playButton.setOnAction(event -> playCard());
        resetButton.setOnAction(event -> resetGame());
        resetButton.setDisable(true);
        drawButton.setDisable(true);
        playButton.setDisable(true);

        for (int i = 0; i < NUM_PLAYERS; i++) {
            playerLabels[i] = new Label("Player " + (i + 1));
            playerLabels[i].setPadding(new Insets(5));
            playerPanel.getChildren().add(playerLabels[i]);
        }

        centerPanel.setAlignment(Pos.CENTER);

        for (int i = 0; i < NUM_CARDS_PER_HAND; i++) {
            cardRectangles[i] = createCardRectangle();
            centerPanel.getChildren().add(cardRectangles[i]);
        }

        // Create the main layout
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        layout.setTop(roundLabel);
        layout.setCenter(centerPanel);
        layout.setBottom(trickLabel);

        VBox buttonPanel = new VBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.getChildren().addAll(startButton, drawButton, playButton, resetButton);
        layout.setRight(buttonPanel);

        layout.setLeft(playerPanel);

        // Set up the scene
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private class Deck {
        private List<Card> cards;

        public Deck() {
            cards = new ArrayList<>();
            for (CardSuit suit : CardSuit.values()) {
                for (CardRank rank : CardRank.values()) {
                    Card card = new Card(suit, rank);
                    cards.add(card);
                }
            }
        }

        public void shuffle() {
            Random random = new Random();
            for (int i = cards.size() - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                Card temp = cards.get(i);
                cards.set(i, cards.get(j));
                cards.set(j, temp);
            }
        }

        public Card drawCard() {
            if (cards.isEmpty()) {
                return null;
            }
            return cards.remove(cards.size() - 1);
        }

        public boolean isEmpty() {
            return cards.isEmpty();
        }
    }

    private class Card {
        private CardSuit suit;
        private CardRank rank;

        public Card(CardSuit suit, CardRank rank) {
            this.suit = suit;
            this.rank = rank;
        }

        public CardSuit getSuit() {
            return suit;
        }

        public CardRank getRank() {
            return rank;
        }

        @Override
        public String toString() {
            return rank + " of " + suit;
        }
    }

    private enum CardSuit {
        SPADES, HEARTS, DIAMONDS, CLUBS
    }

    private enum CardRank {
        ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING
    }

    private class Player {
        private String name;
        private List<Card> hand;

        public Player() {
        // Default constructor
            this.name = "";
            this.hand = new ArrayList<>();
        }
        public Player(String name) {
            this.name = name;
            this.hand = new ArrayList<>();
        }

        public void addCardToHand(Card card) {
            hand.add(card);
        }

        public int getHandSize() {
            return hand.size();
        }

        public List<Card> getHand() {
            return hand;
        }

        public Card playCard() {
            if (hand.isEmpty()) {
                return null;
            }
            return hand.remove(0);
        }

        public boolean hasPlayableCard() {
            // Implement the logic to check if the player has a playable card
            // ...
            return false; // Replace with actual condition
        }

        public boolean hasWon() {
            // Implement the logic to check if the player has won the game
            // ...
            return false; // Replace with actual condition
        }

        public void removeCardFromHand(Card card) {
            hand.remove(card);
        }


    }
    
    private void startGame() {
        // Enable/disable appropriate buttons
        startButton.setDisable(true);
        drawButton.setDisable(false);
        playButton.setDisable(false);
        resetButton.setDisable(false);

        gameStarted = true;

        System.out.println("Game Started");

        // Implement the logic for starting the game
        players = createPlayers();
        currentPlayerIndex = 0;
        deck = new Deck();
        centerCards = new ArrayList<>();
        roundNumber = 1;
        trickNumber = 1;

        // Shuffle the deck
        deck.shuffle();

        // Deal cards to players
        dealCards();

        // Update UI
        updatePlayerLabels();
        updateRoundLabel();
        updateTrickLabel();
        updateCardRectangles();
    }

    private void drawCard() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Card card = deck.drawCard();

        // Add the drawn card to the player's hand
        currentPlayer.addCardToHand(card);

        // Update UI
        updateCardRectangles();

        // Check if the drawn card is playable
        if (isPlayable(card)) {
            playButton.setDisable(false);
        } else {
            // Move to the next player's turn
            nextTurn();
        }

        // Disable the draw button if the deck is empty
        if (deck.isEmpty()) {
            drawButton.setDisable(true);
        }
    }

    private void playCard() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Card card = currentPlayer.playCard();

        // Remove the played card from the player's hand
        currentPlayer.removeCardFromHand(card);

        // Add the played card to the center cards
        centerCards.add(card);

        // Update UI
        updateCardRectangles();

        // Check if the trick is complete
        if (centerCards.size() % NUM_PLAYERS == 0) {
            determineTrickWinner();
            updateTrickLabel();
            centerCards.clear();

            // Check if the round is complete
            if (currentPlayer.getHandSize() == 0) {
                // Update scores and display round scores
                updateScores();
                displayRoundScores();

                // Check if the game is over
                if (isGameOver()) {
                    endGame();
                    return;
                }

                // Increment round number and update UI
                roundNumber++;
                updateRoundLabel();

                // Reset trick number and update UI
                trickNumber = 1;
                updateTrickLabel();

                // Reset players and deal new cards
                // for (Player player : players) {
                //     player.reset();
                // }

                dealCards();

                // Update UI
                updatePlayerLabels();
                updateCardRectangles();
            }
        }

        // Move to the next player's turn
        nextTurn();
    }

    private void resetGame() {
        // Reset game state
        players = null;
        currentPlayerIndex = 0;
        deck = null;
        centerCards = null;
        roundNumber = 1;
        trickNumber = 1;

        // Reset UI elements
        startButton.setDisable(false);
        drawButton.setDisable(true);
        playButton.setDisable(true);
        resetButton.setDisable(true);
        roundLabel.setText("Round: 1");
        trickLabel.setText("Trick: 1");
        playerPanel.getChildren().clear();
        centerPanel.getChildren().clear();

        // Reset player labels
        for (int i = 0; i < NUM_PLAYERS; i++) {
            Label label = new Label("Player " + (i + 1));
            label.setPadding(new Insets(5));
            playerPanel.getChildren().add(label);
        }
    }

    private void dealCards() {
        for (Player player : players) {
            for (int i = 0; i < NUM_CARDS_PER_HAND; i++) {
                Card card = deck.drawCard();
                player.addCardToHand(card);
            }
        }
    }

    private void updatePlayerLabels() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            Player player = players.get(i);
            Label label = playerLabels[i];
            label.setText("Player " + (i + 1) + " (" + player.getHandSize() + " cards)");
        }
    }

    private void updateRoundLabel() {
        roundLabel.setText("Round: " + roundNumber);
    }

    private void updateTrickLabel() {
        trickLabel.setText("Trick: " + trickNumber);
    }

    private void updateCardRectangles() {
        for (int i = 0; i < NUM_CARDS_PER_HAND; i++) {
            Rectangle rectangle = cardRectangles[i];
            Card card = getPlayerCard(i);
            if (card != null) {
                rectangle.setFill(getCardColor(card));
            } else {
                rectangle.setFill(Color.LIGHTGRAY);
            }
        }
    }

    private Card getPlayerCard(int index) {
        if (currentPlayerIndex < players.size()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            List<Card> hand = currentPlayer.getHand();
            if (index < hand.size()) {
                return hand.get(index);
            }
        }
        return null;
    }

    private Color getCardColor(Card card) {
        // Implement your own logic for assigning colors to cards
        // For example, use different colors for different ranks or suits
        // You can define a mapping of colors based on card properties
        return Color.DARKGRAY;
    }

    private Rectangle createCardRectangle() {
        Rectangle rectangle = new Rectangle(80, 120);
        rectangle.setFill(Color.LIGHTGRAY);
        rectangle.setStroke(Color.BLACK);
        return rectangle;
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % NUM_PLAYERS;
    }

    private void determineTrickWinner() {
        // Implement your own logic for determining the trick winner
        // For example, compare the ranks or suits of the cards in the center
        // You can define rules to determine which card wins the trick
    }

    private void updateScores() {
        // Implement your own logic for updating scores
        // For example, increment scores based on the round winner
        // You can define rules to calculate scores
    }

    private boolean isGameOver() {
        // Implement your own logic to check if the game is over
        // For example, check if a certain score threshold is reached
        // You can define rules to determine when the game is over
        return false;
    }

    private void displayRoundScores() {
        // Implement your own logic to display the round scores
        // For example, show the scores of each player for the current round
    }

    private void endGame() {
        // Implement your own logic for ending the game
        // For example, display the final scores and declare the winner
    }

    private List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < NUM_PLAYERS; i++) {
            players.add(new Player());
        }
        return players;
    }

    private boolean isPlayable(Card card) {
        // Implement your own logic to check if a card is playable
        // For example, check if the card matches the rank or suit of the top card in the center
        return true;
    }
}
