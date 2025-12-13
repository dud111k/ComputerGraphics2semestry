package fool.game;

import java.util.*;

public class Player {
    private final String name;
    private List<Card> hand;
    private boolean isAttacking;
    private boolean isDefending;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void takeCard(Card card) {
        if (card != null) {
            hand.add(card);
            sortHand();
        }
    }

    public void takeCards(List<Card> cards) {
        hand.addAll(cards);
        sortHand();
    }

    private void sortHand() {
        hand.sort(Comparator
                .comparing(Card::isTrump).reversed()
                .thenComparing(Card::getSuit)
                .thenComparing(Card::getWeight)
        );
    }

    public Card playCard(Card card) {
        boolean removed = hand.remove(card);
        return removed ? card : null;
    }

    // Карты для атаки (в начале атаки можно ходить любой картой)
    public List<Card> getCardsForAttack(List<Card> tableCards, boolean isFirstAttack) {
        if (tableCards.isEmpty() || isFirstAttack) {
            return new ArrayList<>(hand);
        }

        // Иначе можно подкидывать только карты тех же достоинств
        Set<Rank> tableRanks = new HashSet<>();
        for (Card card : tableCards) {
            tableRanks.add(card.getRank());
        }

        List<Card> availableCards = new ArrayList<>();
        for (Card card : hand) {
            if (tableRanks.contains(card.getRank())) {
                availableCards.add(card);
            }
        }

        return availableCards;
    }

    // Карты для защиты
    public List<Card> getCardsForDefense(Card attackCard, Suit trumpSuit) {
        List<Card> availableCards = new ArrayList<>();

        for (Card card : hand) {
            if (card.canBeat(attackCard, trumpSuit)) {
                availableCards.add(card);
            }
        }

        return availableCards;
    }

    // Карты для перевода (карты того же достоинства, что и на столе)
    public List<Card> getCardsForTransfer(List<Card> tableCards) {
        if (tableCards.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Rank> tableRanks = new HashSet<>();
        for (Card card : tableCards) {
            tableRanks.add(card.getRank());
        }

        List<Card> transferCards = new ArrayList<>();
        for (Card card : hand) {
            if (tableRanks.contains(card.getRank())) {
                transferCards.add(card);
            }
        }

        return transferCards;
    }

    // Может ли отбить все карты на столе
    public boolean canDefendAll(List<Card> tableCards, Suit trumpSuit) {
        if (tableCards.isEmpty()) return true;

        List<Card> handCopy = new ArrayList<>(hand);
        List<Card> attacksCopy = new ArrayList<>(tableCards);

        for (Card attackCard : tableCards) {
            boolean found = false;
            for (Card defenseCard : handCopy) {
                if (defenseCard.canBeat(attackCard, trumpSuit)) {
                    handCopy.remove(defenseCard);
                    attacksCopy.remove(attackCard);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return attacksCopy.isEmpty();
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public List<Card> getHand() { return new ArrayList<>(hand); }
    public int getHandSize() { return hand.size(); }
    public boolean hasCards() { return !hand.isEmpty(); }

    public boolean isAttacking() { return isAttacking; }
    public void setAttacking(boolean attacking) {
        this.isAttacking = attacking;
        if (attacking) this.isDefending = false;
    }

    public boolean isDefending() { return isDefending; }
    public void setDefending(boolean defending) {
        this.isDefending = defending;
        if (defending) this.isAttacking = false;
    }

    public void clearFlags() {
        isAttacking = false;
        isDefending = false;
    }

    @Override
    public String toString() {
        return name + " (карт: " + hand.size() + ")";
    }
}