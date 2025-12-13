package fool.game;

import java.util.*;

public class TransferFoolGame {
    private Deck36 deck;
    private List<Player> players;
    private GameTable table;
    private Suit trumpSuit;
    private Player attacker;
    private Player defender;
    private Scanner scanner;
    private boolean gameOver;
    private Random random;

    public TransferFoolGame() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.deck = new Deck36();
        this.trumpSuit = deck.getTrumpSuit();
        this.table = new GameTable();
        this.players = new ArrayList<>();
        this.gameOver = false;

        initializeGame();
    }

    private void initializeGame() {
        System.out.print("Введите ваше имя: ");
        String humanName = scanner.nextLine();

        System.out.print("Сколько ботов добавить? (1-3): ");
        int botCount = scanner.nextInt();
        scanner.nextLine();

        if (botCount < 1 || botCount > 3) {
            botCount = 2;
            System.out.println("Установлено 2 бота");
        }

        // Создаем человеческого игрока
        players.add(new HumanPlayer(humanName, scanner));

        // Создаем ботов
        String[] botNames = {"Бот-Иван", "Бот-Мария", "Бот-Сергей"};
        for (int i = 0; i < botCount; i++) {
            players.add(new BotPlayer(botNames[i]));
        }

        // Раздаем карты
        for (Player player : players) {
            List<Card> cards = deck.drawCards(6);
            player.takeCards(cards);
        }

        // Определяем первого атакующего
        determineFirstAttacker();
    }

    private void determineFirstAttacker() {
        Player firstAttacker = null;
        Card lowestTrump = null;

        for (Player player : players) {
            for (Card card : player.getHand()) {
                if (card.isTrump()) {
                    if (lowestTrump == null || card.getWeight() < lowestTrump.getWeight()) {
                        lowestTrump = card;
                        firstAttacker = player;
                    }
                }
            }
        }

        if (firstAttacker == null) {
            firstAttacker = players.get(random.nextInt(players.size()));
        }

        this.attacker = firstAttacker;
        this.defender = getNextPlayer(firstAttacker);

        attacker.setAttacking(true);
        defender.setDefending(true);

        System.out.println("\nПервый атакующий: " + attacker.getName());
        System.out.println("Первый защитник: " + defender.getName());
    }

    private Player getNextPlayer(Player player) {
        int index = players.indexOf(player);
        return players.get((index + 1) % players.size());
    }

    public void startGame() {
        System.out.println("\n=== ИГРА 'ПЕРЕВОДНОЙ ДУРАК' ===");
        System.out.println("Козырь: " + trumpSuit.getSymbol());
        System.out.println("=".repeat(40));

        while (!gameOver) {
            playRound();

            if (!gameOver) {
                refillHands();
                checkGameEnd();

                if (!gameOver) {
                    determineNextRoundPlayers();
                }
            }
        }

        scanner.close();
    }

    private void playRound() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Атакующий: " + attacker.getName());
        System.out.println("Защитник: " + defender.getName());
        System.out.println("Карт в колоде: " + deck.size());
        System.out.println("=".repeat(40));

        // Очищаем стол в начале раунда
        table.clear();

        // Фаза атаки
        attackPhase();

        if (gameOver || table.isEmpty()) {
            return;
        }

        displayTable();

        // Фаза перевода
        boolean transferred = transferPhase();

        if (transferred) {
            // После перевода СРАЗУ переходим к защите
            defensePhase();
            finishRound();

            // После защиты определяем следующий раунд
            if (!gameOver) {
                refillHands();
                checkGameEnd();
                if (!gameOver) {
                    determineNextRoundPlayers();
                }
            }
            return;
        }

        // Если перевод не состоялся - обычная фаза защиты
        defensePhase();

        // Завершение раунда
        finishRound();
    }

    private void attackPhase() {
        System.out.println("\n--- ФАЗА АТАКИ ---");

        boolean continueAttack = true;
        boolean isFirstAttack = true;

        while (continueAttack && table.getCardCount() < 6) {
            // Получаем доступные карты для атаки
            List<Card> attackOptions = attacker.getCardsForAttack(
                    table.getAttackCards(),
                    isFirstAttack
            );

            if (attackOptions.isEmpty()) {
                System.out.println(attacker.getName() + " не может атаковать.");
                break;
            }

            // Выбор карты для атаки
            Card attackCard = null;
            if (attacker instanceof HumanPlayer) {
                attackCard = ((HumanPlayer) attacker).chooseCardFromList(
                        attackOptions,
                        "атаки"
                );

                if (attackCard == null) {
                    System.out.println(attacker.getName() + " завершает атаку.");
                    break;
                }
            } else if (attacker instanceof BotPlayer) {
                attackCard = ((BotPlayer) attacker).chooseAttackCard(
                        attackOptions,
                        table.getAttackCards(),
                        isFirstAttack
                );
            } else {
                // Для базового класса Player - берем первую доступную карту
                attackCard = attackOptions.get(0);
            }

            if (attackCard != null) {
                // Разыгрываем карту
                attacker.playCard(attackCard);
                table.addAttackCard(attackCard);

                System.out.println(attacker.getName() + " атакует картой: " + attackCard);
                isFirstAttack = false;

                // Проверяем лимит карт на столе
                if (table.getCardCount() >= 6) {
                    System.out.println("Достигнут лимит карт на столе (6).");
                    break;
                }

                // Проверяем, может ли защитник отбить все карты
                if (!defender.canDefendAll(table.getAttackCards(), trumpSuit)) {
                    System.out.println("Защитник не сможет отбить все карты. Атака завершена.");
                    break;
                }

                // Спрашиваем, продолжать ли атаку
                if (attacker instanceof HumanPlayer) {
                    if (table.getCardCount() < 6) {
                        continueAttack = ((HumanPlayer) attacker).wantToContinueAttack();
                    }
                } else if (attacker instanceof BotPlayer) {
                    continueAttack = ((BotPlayer) attacker).wantToContinueAttack(table.getCardCount());
                } else {
                    // Для базового класса - атакуем пока есть карты и место на столе
                    continueAttack = table.getCardCount() < 6 && !attackOptions.isEmpty();
                }
            } else {
                continueAttack = false;
            }
        }
    }

    private boolean transferPhase() {
        System.out.println("\n--- ФАЗА ПЕРЕВОДА ---");

        // Проверяем, может ли защитник перевести
        List<Card> transferOptions = defender.getCardsForTransfer(table.getAttackCards());

        if (transferOptions.isEmpty()) {
            System.out.println(defender.getName() + " не может перевести атаку.");
            return false;
        }

        System.out.println(defender.getName() + " может перевести атаку.");

        // Решение о переводе
        Card transferCard = null;
        if (defender instanceof HumanPlayer) {
            if (((HumanPlayer) defender).wantToTransfer()) {
                transferCard = ((HumanPlayer) defender).chooseCardFromList(
                        transferOptions,
                        "перевода"
                );
            }
        } else if (defender instanceof BotPlayer) {
            transferCard = ((BotPlayer) defender).chooseTransferCard(
                    transferOptions,
                    table.getAttackCards()
            );
        }

        if (transferCard != null) {
            // Играем карту перевода (она добавляется к атаке)
            defender.playCard(transferCard);
            table.addAttackCard(transferCard);

            System.out.println(defender.getName() + " переводит атаку картой: " + transferCard);

            // ВАЖНО: Меняем только защитника! Атакующий остается тем же!
            Player oldDefender = defender;

            // Новый защитник - следующий игрок после старого защитника
            defender = getNextPlayer(oldDefender);

            // Старый защитник выходит из раунда (он перевел)
            oldDefender.clearFlags();

            // Атакующий остается тем же!
            // defender становится новым защитником
            defender.setDefending(true);

            System.out.println("\n✓ Перевод выполнен!");
            System.out.println("Атакующий остается: " + attacker.getName());
            System.out.println("Новый защитник: " + defender.getName());
            System.out.println("На столе теперь " + table.getCardCount() + " карт.");

            displayTable();

            // После перевода СРАЗУ переходим к фазе защиты
            // НЕ начинаем новую атаку!
            return true;
        }

        return false;
    }

    private void defensePhase() {
        System.out.println("\n--- ФАЗА ЗАЩИТЫ ---");

        List<Card> undefendedCards = table.getUndefendedCards();

        for (Card attackCard : undefendedCards) {
            System.out.println("\nНужно отбить: " + attackCard);

            List<Card> defenseOptions = defender.getCardsForDefense(attackCard, trumpSuit);

            if (defenseOptions.isEmpty()) {
                System.out.println(defender.getName() + " не может отбить " + attackCard);
                takeAllCardsFromTable();
                return;
            }

            Card defenseCard = null;
            if (defender instanceof HumanPlayer) {
                defenseCard = ((HumanPlayer) defender).chooseCardFromList(
                        defenseOptions,
                        "защиты от " + attackCard
                );
            } else if (defender instanceof BotPlayer) {
                defenseCard = ((BotPlayer) defender).chooseDefenseCard(
                        defenseOptions,
                        attackCard
                );
            }

            if (defenseCard == null) {
                System.out.println(defender.getName() + " решает забрать карты.");
                takeAllCardsFromTable();
                return;
            }

            // Играем карту защиты
            defender.playCard(defenseCard);
            table.addDefenseCard(attackCard, defenseCard, trumpSuit);

            System.out.println(defender.getName() + " отбивает: " + attackCard + " → " + defenseCard);
        }
    }

    private void finishRound() {
        if (table.isAllDefended()) {
            System.out.println("\n✓ " + defender.getName() + " успешно отбился!");
            System.out.println("Карты уходят в отбой.");
        }
    }

    private void takeAllCardsFromTable() {
        List<Card> tableCards = table.takeAllCards();
        defender.takeCards(tableCards);
        System.out.println(defender.getName() + " забирает " + tableCards.size() + " карт со стола.");
    }

    private void refillHands() {
        // Порядок добора: атакующий, защитник, остальные
        List<Player> drawOrder = new ArrayList<>();
        drawOrder.add(attacker);
        drawOrder.add(defender);

        Player next = getNextPlayer(defender);
        while (next != attacker && drawOrder.size() < players.size()) {
            drawOrder.add(next);
            next = getNextPlayer(next);
        }

        for (Player player : drawOrder) {
            while (player.getHandSize() < 6 && !deck.isEmpty()) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.takeCard(card);
                }
            }
        }
    }

    private void determineNextRoundPlayers() {
        // Если защитник успешно отбился (включая переведенные карты)
        if (table.isAllDefended()) {
            // Успешный защитник становится атакующим
            attacker = defender;
        }
        // Если защитник забрал карты (не смог отбиться)
        // Атакующий остается тем же

        // Следующий защитник - игрок после текущего атакующего
        defender = getNextPlayer(attacker);

        // Обновляем флаги
        for (Player player : players) {
            player.clearFlags();
        }
        attacker.setAttacking(true);
        defender.setDefending(true);

        table.clear();
    }

    private void checkGameEnd() {
        // Проверяем игроков с картами
        List<Player> playersWithCards = new ArrayList<>();
        for (Player player : players) {
            if (player.hasCards()) {
                playersWithCards.add(player);
            } else {
                System.out.println(player.getName() + " вышел из игры!");
            }
        }

        if (playersWithCards.size() == 1) {
            gameOver = true;
            Player loser = playersWithCards.get(0);
            System.out.println("\n" + "=".repeat(40));
            System.out.println("ИГРА ОКОНЧЕНА!");
            System.out.println(loser.getName().toUpperCase() + " СТАЛ ДУРАКОМ!");
            System.out.println("=".repeat(40));
        } else if (playersWithCards.isEmpty()) {
            gameOver = true;
            System.out.println("\nВсе игроки вышли из игры - ничья!");
        }
    }

    private void displayTable() {
        System.out.println("\n" + table);
    }

    // Старт игры
    public static void main(String[] args) {
        System.out.println("=== ПЕРЕВОДНОЙ ДУРАК ===");
        System.out.println("Правила:");
        System.out.println("- Колода 36 карт (от 6 до туза)");
        System.out.println("- Максимум 6 карт на руках");
        System.out.println("- Первая атака: любая карта");
        System.out.println("- Дальнейшие атаки: карты тех же достоинств");
        System.out.println("- Защитник может перевести атаку следующему игроку");
        System.out.println("- Последний оставшийся с картами становится 'дураком'");
        System.out.println();

        TransferFoolGame game = new TransferFoolGame();
        game.startGame();
    }
}