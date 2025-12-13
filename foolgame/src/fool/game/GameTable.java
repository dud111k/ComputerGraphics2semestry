package fool.game;

import java.util.*;

public class GameTable {
    private List<Card> attackCards;
    private Map<Card, Card> defenseMap;

    public GameTable() {
        this.attackCards = new ArrayList<>();
        this.defenseMap = new HashMap<>();
    }

    public void addAttackCard(Card card) {
        attackCards.add(card);
    }

    public boolean addDefenseCard(Card attackCard, Card defenseCard, Suit trumpSuit) {
        if (attackCards.contains(attackCard) &&
                !defenseMap.containsKey(attackCard) &&
                defenseCard.canBeat(attackCard, trumpSuit)) {
            defenseMap.put(attackCard, defenseCard);
            return true;
        }
        return false;
    }

    public List<Card> getUndefendedCards() {
        List<Card> undefended = new ArrayList<>();
        for (Card attackCard : attackCards) {
            if (!defenseMap.containsKey(attackCard)) {
                undefended.add(attackCard);
            }
        }
        return undefended;
    }

    public List<Card> takeAllCards() {
        List<Card> allCards = new ArrayList<>(attackCards);
        allCards.addAll(defenseMap.values());
        clear();
        return allCards;
    }

    public boolean isAllDefended() {
        return defenseMap.size() == attackCards.size();
    }

    public void clear() {
        attackCards.clear();
        defenseMap.clear();
    }

    public List<Card> getAttackCards() {
        return new ArrayList<>(attackCards);
    }

    public List<Card> getDefenseCards() {
        return new ArrayList<>(defenseMap.values());
    }

    public boolean isEmpty() {
        return attackCards.isEmpty() && defenseMap.isEmpty();
    }

    public int getCardCount() {
        return attackCards.size() + defenseMap.size();
    }

    @Override
    public String toString() {
        if (attackCards.isEmpty()) {
            return "Стол пуст\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("На столе:\n");

        for (Card attackCard : attackCards) {
            sb.append("  ").append(attackCard);
            if (defenseMap.containsKey(attackCard)) {
                sb.append(" → ").append(defenseMap.get(attackCard));
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}