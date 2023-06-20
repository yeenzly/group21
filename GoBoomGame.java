import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class GoBoomGame {
    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final String[] SUITS = {"c", "d", "h", "s"};
    private static final String[] PLAYERS = {"Player1", "Player2", "Player3", "Player4"};

    private List<String> deck;
    private List<String>[] playerHands;
    private List<String> centerCards;
    private int[] playerScores;
    private int currentPlayerIndex;
    private String leadCard;
    private int trickNumber;

    public GoBoomGame() {
        deck = new ArrayList<>();
        playerHands = new List[4];
        for (int i = 0; i < 4; i++) {
            playerHands[i] = new ArrayList<>();
        }
        centerCards = new ArrayList<>();
        playerScores = new int[4];
        currentPlayerIndex = 0;
        leadCard = "";
        trickNumber = 1;
    }

    public void startGame() {
        System.out.println("Go Boom Game");

        // Check if a saved game file exists
        if (isSavedGameAvailable()) {
            System.out.println("Saved game found. Do you want to resume? (y/n)");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("y") || response.equals("yes")) {
                loadSavedGame();
                printGameState();
                gameLoop();
                return;
            }
        }

        // Generate and shuffle the deck
        generateDeck();
        shuffleDeck();

        // Deal 7 cards to each player
        dealCards();

        // Determine the lead card and first player
        determineFirstPlayer();

        // Print the initial game state
        printGameState();

        // Start the game loop
        gameLoop();
    }

    private void generateDeck() {
        deck.clear();
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(suit + rank);
            }
        }
    }

    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    private void dealCards() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 4; j++) {
                playerHands[j].add(deck.remove(0));
            }
        }
    }

    private void determineFirstPlayer() {
        String firstLeadCard = deck.remove(0);
        centerCards.add(firstLeadCard);
        System.out.println("The first lead card " + firstLeadCard + " is placed at the center.");

        char suit = firstLeadCard.charAt(0);
        char rank = firstLeadCard.charAt(1);

        if (rank == 'A' || rank == '5' || rank == '9' || rank == 'K') {
            currentPlayerIndex = 0;
        } else if (rank == '2' || rank == '6' || rank == 'X') {
            currentPlayerIndex = 1;
        } else if (rank == '3' || rank == '7' || rank == 'J') {
            currentPlayerIndex = 2;
        } else if (rank == '4' || rank == '8' || rank == 'Q') {
            currentPlayerIndex = 3;
        }

        System.out.println("The first player is " + PLAYERS[currentPlayerIndex] + ".");
    }

    private void printGameState() {
        System.out.println("\n--- Game State ---");
        System.out.println("Trick Number: " + trickNumber);
        System.out.println("Current Player: " + PLAYERS[currentPlayerIndex]);
        System.out.println("Player Scores: " + Arrays.toString(playerScores));

        System.out.println("\nPlayer Hands:");
        for (int i = 0; i < 4; i++) {
            System.out.println(PLAYERS[i] + ": " + playerHands[i]);
        }

        System.out.println("\nCenter Cards: " + centerCards);

        displayDeck(); // Display the deck
    }

    private void displayDeck() {
        System.out.println("Deck: " + deck);
    }

    private void gameLoop() {
        Scanner scanner = new Scanner(System.in);
        String command;
        boolean gameFinished = false;

        while (!gameFinished) {
            command = scanner.nextLine().trim();

            switch (command) {
                case "s":
                    saveGame();
                    System.out.println("Game saved.");
                    break;
                case "x":
                    gameFinished = true;
                    deleteSavedGame();
                    break;
                case "d":
                    drawCard();
                    break;
                case "r":
                    resetGame();
                    break;
                default:
                    playCard(command);
                    break;
            }

            if (deck.isEmpty()) {
                System.out.println("The deck is empty. Skipping to the next player.");
                currentPlayerIndex = (currentPlayerIndex + 1) % 4;
            }

            printGameState();

            if (isGameOver()) {
                gameFinished = true;
                System.out.println("Game over!");
                displayPlayerScores();
                deleteSavedGame();
            }
        }
    }

    private void drawCard() {
        if (deck.isEmpty()) {
            System.out.println("The deck is empty. Cannot draw a card.");
            return;
        }

        String drawnCard = deck.remove(0);
        playerHands[currentPlayerIndex].add(drawnCard);
        System.out.println(PLAYERS[currentPlayerIndex] + " drew a card: " + drawnCard);
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
    }

    private void playCard(String card) {
        if (!playerHands[currentPlayerIndex].contains(card)) {
            System.out.println("Invalid card. Please try again.");
            return;
        }

        playerHands[currentPlayerIndex].remove(card);
        centerCards.add(card);
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
        System.out.println(PLAYERS[currentPlayerIndex] + " played a card: " + card);

        if (centerCards.size() == 4) {
            determineTrickWinner();
            centerCards.clear();
            trickNumber++;
        }
    }

    private void determineTrickWinner() {
        String winningCard = centerCards.get(0);
        char winningSuit = winningCard.charAt(0);
        char winningRank = winningCard.charAt(1);
        int winningPlayerIndex = 0;

        for (int i = 1; i < 4; i++) {
            String card = centerCards.get(i);
            char suit = card.charAt(0);
            char rank = card.charAt(1);

            if (suit == winningSuit && rank > winningRank) {
                winningCard = card;
                winningSuit = suit;
                winningRank = rank;
                winningPlayerIndex = i;
            }
        }

        currentPlayerIndex = (currentPlayerIndex + winningPlayerIndex) % 4;
        System.out.println(PLAYERS[currentPlayerIndex] + " won the trick with the card " + winningCard);
        playerScores[currentPlayerIndex]++;
    }

    private boolean isGameOver() {
        for (int score : playerScores) {
            if (score >= 10) {
                return true;
            }
        }
        return false;
    }

    private void displayPlayerScores() {
        System.out.println("Player Scores:");
        for (int i = 0; i < 4; i++) {
            System.out.println(PLAYERS[i] + ": " + playerScores[i]);
        }
    }

    private boolean isSavedGameAvailable() {
        File file = new File("saved_game.txt");
        return file.exists();
    }

    private void saveGame() {
        try {
            FileWriter writer = new FileWriter("saved_game.txt");
            writer.write(Integer.toString(currentPlayerIndex) + "\n");
            writer.write(Integer.toString(trickNumber) + "\n");

            for (List<String> hand : playerHands) {
                for (String card : hand) {
                    writer.write(card + " ");
                }
                writer.write("\n");
            }

            for (String card : centerCards) {
                writer.write(card + " ");
            }
            writer.write("\n");

            for (int score : playerScores) {
                writer.write(Integer.toString(score) + " ");
            }
            writer.write("\n");

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving the game.");
        }
    }

    private void loadSavedGame() {
        try {
            FileReader reader = new FileReader("saved_game.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            currentPlayerIndex = Integer.parseInt(bufferedReader.readLine());
            trickNumber = Integer.parseInt(bufferedReader.readLine());

            for (int i = 0; i < 4; i++) {
                String[] cards = bufferedReader.readLine().trim().split(" ");
                playerHands[i].clear();
                playerHands[i].addAll(Arrays.asList(cards));
            }

            String[] cards = bufferedReader.readLine().trim().split(" ");
            centerCards.clear();
            centerCards.addAll(Arrays.asList(cards));

            String[] scores = bufferedReader.readLine().trim().split(" ");
            for (int i = 0; i < 4; i++) {
                playerScores[i] = Integer.parseInt(scores[i]);
            }

            bufferedReader.close();
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred while loading the saved game.");
        }
    }

    private void deleteSavedGame() {
        File file = new File("saved_game.txt");
        file.delete();
    }

    private void resetGame() {
        currentPlayerIndex = 0;
        trickNumber = 1;
        playerScores = new int[4];
        deck.clear();
        for (List<String> hand : playerHands) {
            hand.clear();
        }
        centerCards.clear();
        generateDeck();
        shuffleDeck();
        dealCards();
        determineFirstPlayer();
        printGameState();
    }

    public static void main(String[] args) {
        GoBoomGame game = new GoBoomGame();
        game.startGame();
    }
}
