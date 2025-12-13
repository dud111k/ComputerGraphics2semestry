package fool.game;

import java.util.*;

public class Deck36 {
    private List<Card> cards;
    private Suit trumpSuit;
    private Random random;

    public Deck36() {
        this.cards = new ArrayList<>();
        this.random = new Random();
        initializeDeck();
    }

    private void initializeDeck() {
        for (Suit suit : Suit.values()) { // Создаем все 36 карт
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(cards); // Перемешиваем и выбираем козырь
        trumpSuit = Suit.values()[random.nextInt(4)];
        for (Card card : cards) { // помечаем козыри
            if (card.getSuit() == trumpSuit) {
                card.setTrump(true);
            }
        }
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(cards.size() - 1);
    }

    public List<Card> drawCards(int count) {
        List<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < count && !cards.isEmpty(); i++) {
            Card card = drawCard();
            if (card != null) {
                drawnCards.add(card);
            }
        }
        return drawnCards;
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Suit getTrumpSuit() {
        return trumpSuit;
    }
}