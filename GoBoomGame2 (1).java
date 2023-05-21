import java.util.ArrayList;
import java.util.Collections;

public class GoBoomGame2 {
    private ArrayList<Card> deck;
    private Player[] players;
    private ArrayList<Card> centerCards;
    private int trickNumber;

    public GoBoomGame2() {
        deck = new ArrayList<>();
        players = new Player[4];
        centerCards = new ArrayList<>();
        trickNumber = 1;

        // Create a deck of 52 cards
        for (CardSuit suit : CardSuit.values()) {
            for (CardRank rank : CardRank.values()) {
                deck.add(new Card(rank, suit));
            }
        }

        // Shuffle the deck
        Collections.shuffle(deck);

        // Set the lead card
        Card leadCard = deck.remove(0);
        centerCards.add(leadCard);

        // Determine the first player based on the lead card
        int firstPlayerIndex = leadCard.getFirstPlayerIndex();
        players[firstPlayerIndex] = new Player(firstPlayerIndex);

        // Deal 7 cards to each player
        int currentPlayerIndex = firstPlayerIndex;
        for (int i = 0; i < 28; i++) {
            if (players[currentPlayerIndex] == null) {
                players[currentPlayerIndex] = new Player(currentPlayerIndex);
            }
            players[currentPlayerIndex].addCard(deck.remove(0));

            currentPlayerIndex = (currentPlayerIndex + 1) % 4;
        }
    }

    public void playGame() {
        while (!gameOver()) {
            System.out.println("Trick #" + trickNumber);
            printPlayerHands();
            printCenterCards();
            printDeck();
            printScore();
            System.out.println("Turn: Player " + getCurrentPlayerIndex());

            Player currentPlayer = getCurrentPlayer();
            Card leadCard = getLeadCard();
            Card playedCard = currentPlayer.playCard(leadCard);

            System.out.println("> " + playedCard);
            centerCards.add(playedCard);

            if (centerCards.size() == 4) {
                int trickWinnerIndex = getTrickWinnerIndex();
                Player trickWinner = players[trickWinnerIndex];

                System.out.println();
                System.out.println("*** Player " + trickWinnerIndex + " wins Trick #" + trickNumber + " ***");
                System.out.println();

                trickWinner.addTrick(centerCards);
                centerCards.clear();

                trickNumber++;
            }

            System.out.println();
        }

        System.out.println("Game over!");
    }

    private Player getCurrentPlayer() {
        return players[getCurrentPlayerIndex()];
    }

    private int getCurrentPlayerIndex() {
        return centerCards.size() % 4;
    }

    private Card getLeadCard() {
        return centerCards.get(0);
    }

    private int getTrickWinnerIndex() {
        Card leadCard = getLeadCard();
        int trickWinnerIndex = 0;
        Card trickWinnerCard = centerCards.get(0);

        for (int i = 1; i < 4; i++) {
            Card currentCard = centerCards.get(i);

            if (currentCard.isHigherThan(trickWinnerCard, leadCard)) {
                trickWinnerIndex = i;
                trickWinnerCard = currentCard;
            }
        }

        return (getCurrentPlayerIndex() + trickWinnerIndex) % 4;
    }

    private boolean gameOver() {
        // Check if any player has no cards left
        for (Player player : players) {
            if (player != null && player.getCardCount() == 0) {
                return true;
            }
        }
        return false;
    }

    private void printPlayerHands() {
        for (int i = 0; i < 4; i++) {
            Player player = players[i];
            System.out.print("Player" + (i + 1) + ": ");
            if (player != null) {
                System.out.print(player.getHand());
            }
            System.out.println();
        }
    }

    private void printCenterCards() {
        System.out.print("Center: ");
        for (Card card : centerCards) {
            System.out.print(card + " ");
        }
        System.out.println();
    }

    private void printDeck() {
        System.out.print("Deck: ");
        for (Card card : deck) {
            System.out.print(card + " ");
        }
        System.out.println();
    }

    private void printScore() {
        System.out.print("Score: ");
        for (int i = 0; i < 4; i++) {
            Player player = players[i];
            if (player != null) {
                System.out.print("Player" + (i + 1) + " = " + player.getTrickCount() + " | ");
            }
        }
        System.out.println();
    }

    
    public static void main(String[] args) {
        GoBoomGame2 game = new GoBoomGame2();
        game.playGame();
    }
}

enum CardSuit {
    CLUBS("c"), DIAMONDS("d"), HEARTS("h"), SPADES("s");

    private String symbol;

    CardSuit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}

enum CardRank {
    ACE("A"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"),
    EIGHT("8"), NINE("9"), TEN("10"), JACK("J"), QUEEN("Q"), KING("K");

    private String symbol;

    CardRank(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}

class Card {
    private CardRank rank;
    private CardSuit suit;

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

    public int getFirstPlayerIndex() {
        if (rank == CardRank.ACE || rank == CardRank.FIVE ||
                rank == CardRank.NINE || rank == CardRank.KING) {
            return 0;
        } else if (rank == CardRank.TWO || rank == CardRank.SIX ||
                rank == CardRank.TEN) {
            return 1;
        } else if (rank == CardRank.THREE || rank == CardRank.SEVEN ||
                rank == CardRank.JACK) {
            return 2;
        } else if (rank == CardRank.FOUR || rank == CardRank.EIGHT ||
                rank == CardRank.QUEEN) {
            return 3;
        }
        return -1;
    }

    public boolean isHigherThan(Card card, Card leadCard) {
        if (suit == card.suit) {
            return rank.ordinal() > card.rank.ordinal();
        } else if (suit == leadCard.suit && card.suit != leadCard.suit) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return suit.getSymbol() + rank.getSymbol();
    }
}

class Player {
    private ArrayList<Card> hand;
    private ArrayList<Card> tricks;

    public Player(int playerIndex) {
        hand = new ArrayList<>();
        tricks = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public Card playCard(Card leadCard) {
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card.getSuit() == leadCard.getSuit() || card.getRank() == leadCard.getRank()) {
                return hand.remove(i);
            }
        }
        return null; // Shouldn't happen if the game rules are followed
    }

    public void addTrick(ArrayList<Card> trick) {
        tricks.addAll(trick);
    }

    public int getCardCount() {
        return hand.size();
    }

    public int getTrickCount() {
        return tricks.size() / 4;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }
}
