package fool.game;

import java.util.*;

public class HumanPlayer extends Player {
    private Scanner scanner;

    public HumanPlayer(String name, Scanner scanner) {
        super(name);
        this.scanner = scanner;
    }

    public Card chooseCardFromList(List<Card> cards, String purpose) {
        if (cards.isEmpty()) {
            return null;
        }

        System.out.println("\nВаши карты:");
        displayCardsWithNumbers(getHand());

        System.out.println("\nДоступные карты для " + purpose + ":");
        for (int i = 0; i < cards.size(); i++) {
            System.out.println((i + 1) + ". " + cards.get(i));
        }

        System.out.print("Выберите карту (1-" + cards.size() + ") или 0 чтобы пропустить: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 0) {
                return null;
            }

            if (choice > 0 && choice <= cards.size()) {
                return cards.get(choice - 1);
            } else {
                System.out.println("Неверный выбор!");
                return chooseCardFromList(cards, purpose);
            }
        } catch (NumberFormatException e) {
            System.out.println("Введите число!");
            return chooseCardFromList(cards, purpose);
        }
    }

    private void displayCardsWithNumbers(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            System.out.println((i + 1) + ". " + cards.get(i));
        }
    }

    public boolean wantToContinueAttack() {
        System.out.print("Продолжить атаку? (да/нет): ");
        String answer = scanner.nextLine().toLowerCase();
        return answer.equals("да") || answer.equals("д");
    }

    public boolean wantToTransfer() {
        System.out.print("Хотите перевести атаку? (да/нет): ");
        String answer = scanner.nextLine().toLowerCase();
        return answer.equals("да") || answer.equals("д");
    }
}