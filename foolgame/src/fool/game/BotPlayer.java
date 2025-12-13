package fool.game;

import java.util.*;

public class BotPlayer extends Player {
    private Random random;

    public BotPlayer(String name) {
        super(name);
        this.random = new Random();
    }

    public Card chooseAttackCard(List<Card> attackCards, List<Card> tableCards, boolean isFirstAttack) {
        if (attackCards.isEmpty()) {
            return null;
        }
        attackCards.sort(Comparator.comparingInt(Card::getWeight)); // для аттаки бот выбирает самую слабую карту
        return attackCards.get(0);
    }

    public Card chooseDefenseCard(List<Card> defenseCards, Card attackCard) {
        if (defenseCards.isEmpty()) {
            return null;
        }
        defenseCards.sort(Comparator.comparingInt(Card::getWeight)); // Бот выбирает самую слабую карту для защиты
        return defenseCards.get(0);
    }

    public Card chooseTransferCard(List<Card> transferCards, List<Card> tableCards) {
        if (transferCards.isEmpty()) {
            return null; // бот решает перевести ли, основываясь на 1 - кол-во карт на столе, 2 - кол-во карт в руке, 3 - сила карт для перевода
        }
        if (tableCards.size() >= 5) { // Если на столе уже много карт, не переводить - следующему будет сложно отбить
            return null;
        }
        if (getHandSize() <= 2) { // Если у бота мало карт, лучше перевести
            transferCards.sort(Comparator.comparingInt(Card::getWeight));
            return transferCards.get(0); // Самая слабая карта
        }// Иначе случайное решение
        if (random.nextDouble() < 0.4) { // 40% вероятность перевода
            transferCards.sort(Comparator.comparingInt(Card::getWeight));
            return transferCards.get(0);
        }
        return null;
    }

    public boolean wantToContinueAttack(int tableSize) {
        if (tableSize >= 5) return false;
        if (tableSize <= 2) return random.nextBoolean(); // бот продолжает атаку с вероятностью, зависящей от количества карт на столе
        return random.nextDouble() < 0.3;
    }
}