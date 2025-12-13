package fool.game;

public class Card implements Comparable<Card> {
    private final Suit suit;
    private final Rank rank;
    private boolean isTrump;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.isTrump = false;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean isTrump() {
        return isTrump;
    }

    public void setTrump(boolean isTrump) {
        this.isTrump = isTrump;
    }

    public int getWeight() {
        int baseWeight = rank.getValue();
        return isTrump ? baseWeight + 100 : baseWeight;
    }

    public boolean canBeat(Card attackingCard, Suit trumpSuit) {
        if (this.suit == attackingCard.suit) {
            return this.getWeight() > attackingCard.getWeight();
        } else if (this.suit == trumpSuit && attackingCard.suit != trumpSuit) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Card other) {
        return Integer.compare(this.getWeight(), other.getWeight());
    }

    @Override
    public String toString() {
        String trumpMark = isTrump ? "*" : "";
        return rank.getName() + suit.getSymbol() + trumpMark;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return 31 * suit.hashCode() + rank.hashCode();
    }
}