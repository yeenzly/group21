import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

enum CardSuit {
    clubs("c"),
    diamonds("d"),
    hearts("h"),
    spades("s");

    private final String symbol;

    CardSuit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}

enum CardRank {
    ace("A"),
    two("2"),
    three("3"),
    four("4"),
    five("5"),
    six("6"),
    seven("7"),
    eight("8"),
    nine("9"),
    ten("10"),
    jack("J"),
    queen("Q"),
    king("K");

    private final String symbol;

    CardRank(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}

class Card {
    private final CardRank rank;
    private final CardSuit suit;

    public Card(CardRank rank, CardSuit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public CardRank getRank() {
        return rank;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public static Card fromString(String cardString) {
        if (cardString.length() != 2) {
            throw new IllegalArgumentException("Invalid card format. Please enter a valid card (e.g., s3, dQ, hA).");
        }

        String rankSymbol = cardString.substring(1);
        String suitSymbol = cardString.substring(0, 1);

        CardRank rank = null;
        CardSuit suit = null;

        for (CardRank cardRank : CardRank.values()) {
            if (cardRank.getSymbol().equalsIgnoreCase(rankSymbol)) {
                rank = cardRank;
                break;
            }
        }

        for (CardSuit cardSuit : CardSuit.values()) {
            if (cardSuit.getSymbol().equalsIgnoreCase(suitSymbol)) {
                suit = cardSuit;
                break;
            }
        }

        if (rank == null || suit == null) {
            throw new IllegalArgumentException("Invalid card format. Please enter a valid card (e.g., s3, dQ, hA).");
        }

        return new Card(rank, suit);
    }

    @Override
    public String toString() {
        return suit.getSymbol() + rank.getSymbol();
    }
}

class Player {
    private final int playerIndex;
    private List<Card> hand;
    private List<List<Card>> tricks;

    public Player(int playerIndex) {
        this.playerIndex = playerIndex;
        this.hand = new ArrayList<>();
        this.tricks = new ArrayList<>();
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public Card playCard(Card card) {
        if (hand.contains(card)) {
            hand.remove(card);
            return card;
        }
        return null;
    }

    public void addTrick(List<Card> trick) {
        tricks.add(trick);
    }

    public int getTrickCount() {
        return tricks.size();
    }
}

public class GoBoomGame {
    private Player[] players;
    private List<Card> deck;
    private List<Card> centerCards;
    private int currentPlayerIndex;
    private boolean gameRunning;

    public GoBoomGame() {
        players = new Player[4];
        deck = new ArrayList<>();
        centerCards = new ArrayList<>();
        currentPlayerIndex = -1;
        gameRunning = true;
    }

    public void playGame() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("s = Start a new game.");
        System.out.println("x = Exit the game.");
        System.out.println("d = Draw cards from deck until a playable card is obtained. ");
        System.out.println("    If the deck is empty, skip to the next player.");
        System.out.println("Input command below:");
        while (gameRunning) {
            String input = scanner.nextLine().trim();

            if (input.equals("s")) {
                startNewGame();
                printGameStatus();
            } else if (input.equals("x")) {
                exitGame();
            } else if (input.equals("d")) {
                drawCard();
            } else {
                playCard(input);
            }
        }
        scanner.close();
    }

    private void startNewGame() {
        deck.clear();
        centerCards.clear();

        for (CardSuit suit : CardSuit.values()) {
            for (CardRank rank : CardRank.values()) {
                deck.add(new Card(rank, suit));
            }
        }

        Collections.shuffle(deck);

        for (int i = 0; i < 4; i++) {
            players[i] = new Player(i);
        }

        Card firstLeadCard = deck.remove(deck.size() - 1);
        centerCards.add(firstLeadCard);
        determineFirstPlayer(firstLeadCard);
        dealCards();
    }

    private void exitGame() {
        gameRunning = false;
    }

    private void determineFirstPlayer(Card firstLeadCard) {
        CardRank leadRank = firstLeadCard.getRank();

        if (leadRank == CardRank.ace || leadRank == CardRank.five || leadRank == CardRank.nine ||
                leadRank == CardRank.king) {
            currentPlayerIndex = 0;
        } else if (leadRank == CardRank.two || leadRank == CardRank.six || leadRank == CardRank.ten) {
            currentPlayerIndex = 1;
        } else if (leadRank == CardRank.three || leadRank == CardRank.seven || leadRank == CardRank.jack) {
            currentPlayerIndex = 2;
        } else if (leadRank == CardRank.four || leadRank == CardRank.eight || leadRank == CardRank.queen) {
            currentPlayerIndex = 3;
        }
    }

    private void dealCards() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                Card card = deck.remove(deck.size() - 1);
                players[i].addCard(card);
            }
        }
    }

    private void printGameStatus() {
        System.out.println("Trick #" + (centerCards.size() / 4 + 1));
        for (int i = 0; i < 4; i++) {
            System.out.print("Player" + (i + 1) + ": ");
            List<Card> hand = players[i].getHand();
            for (Card card : hand) {
                System.out.print("[" + card + "] ");
            }
            System.out.println();
        }
        System.out.print("Center : ");
        for (Card card : centerCards) {
            System.out.print("[" + card + "] ");
        }
        System.out.println();
        System.out.print("Deck : ");
        for (Card card : deck) {
            System.out.print("[" + card + "] ");
        }
        System.out.println();
        System.out.print("Score: ");
        for (int i = 0; i < 4; i++) {
            System.out.print("Player" + (i + 1) + " = " + players[i].getTrickCount() + " | ");
        }
        System.out.println();
        System.out.println("Turn : Player" + (currentPlayerIndex + 1));
        System.out.print("> ");
    }

    private void drawCard() {
        if (!deck.isEmpty()) {
            Card card = deck.remove(deck.size() - 1);
            players[currentPlayerIndex].addCard(card);
            System.out.println("Player" + (currentPlayerIndex + 1) + " drew a card: " + card);
        } else {
            System.out.println("The deck is empty. Skipping to the next player.");
        }
        nextTurn();
    }

    private void playCard(String cardString) {
        Card card = Card.fromString(cardString);
        Player currentPlayer = players[currentPlayerIndex];
        if (currentPlayer.getHand().contains(card)) {
            centerCards.add(card);
            currentPlayer.playCard(card);
            System.out.println("Player" + (currentPlayerIndex + 1) + " played " + card);
            if (centerCards.size() % 4 == 0) {
                determineTrickWinner();
                centerCards.clear();
                if (isGameOver()) {
                    endGame();
                    return;
                }
            }
            nextTurn();
        } else {
            System.out.println("Player" + (currentPlayerIndex + 1) + " doesn't have that card. Try again.");
            System.out.print("> ");
        }
    }

    private void determineTrickWinner() {
        CardSuit leadSuit = centerCards.get(0).getSuit();
        Card highestCard = null;
        int highestPlayerIndex = -1;
    
        for (int i = 0; i < 4; i++) {
            Card card = centerCards.get(i);
            if (card.getSuit() == leadSuit && (highestCard == null || card.getRank().ordinal() > highestCard.getRank().ordinal())) {
                highestCard = card;
                highestPlayerIndex = i;
            }
        }
    
        Player trickWinner = players[(currentPlayerIndex + highestPlayerIndex + 1) % 4];
        trickWinner.addTrick(centerCards);
        System.out.println("*** Player" + (trickWinner.getPlayerIndex() + 1) + " wins Trick #" + (centerCards.size() / 4) + " ***");
    }
    

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
        printGameStatus();
    }

    private boolean isGameOver() {
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void endGame() {
        System.out.println("Game Over");
        System.out.println("Final Scores:");
        for (int i = 0; i < 4; i++) {
            System.out.println("Player" + (i + 1) + ": " + players[i].getTrickCount());
        }
        exitGame();
    }

    public static void main(String[] args) {
        GoBoomGame game = new GoBoomGame();
        game.playGame();
    }
}
