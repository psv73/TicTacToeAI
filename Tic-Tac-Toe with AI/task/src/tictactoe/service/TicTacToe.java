package tictactoe.service;

import tictactoe.Command;
import tictactoe.FreeCell;
import tictactoe.Level;

import java.util.*;

public class TicTacToe {

    public static final int FIELD_SIZE = 3;
    public static final String FIRST_PLAYER = "X";
    public static final String SECOND_PLAYER = "O";
    private Level firstPlayer;
    private Level secondPlayer;
    private final String[][] battleField = new String[FIELD_SIZE][FIELD_SIZE];
    private Scanner scanner;
    private final Random random;

    public TicTacToe() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    public void start() {
        while (isGameCommon()) {
            beginNewGame();
        }
    }

    private boolean isGameCommon() {
        boolean commonGame = true;
        boolean isCommandsRight = false;

        while (!isCommandsRight) {
            scanner = new Scanner(System.in);
            System.out.print("Input command: ");
            String[] commands = scanner.nextLine().toUpperCase().split(" ");

            try {
                switch (Command.valueOf(commands[0].trim())) {
                    case START: {
                        firstPlayer = Level.valueOf(commands[1].trim());
                        secondPlayer = Level.valueOf(commands[2].trim());
                        break;
                    }
                    case EXIT: {
                        commonGame = false;
                        break;
                    }
                }
                isCommandsRight = true;
            } catch (Exception e) {
                System.out.println("Bad parameters!");
            }
        }

        return commonGame;
    }

    private void beginNewGame() {
        fillTheBattlefield(null);
        printBattleField();

        while (!isGameOver()) {

            String side;
            boolean isHuman;
            Level level;

            if (isMoveX()) {
                side = FIRST_PLAYER;
                isHuman = firstPlayer.isHuman();
                level = firstPlayer;
            } else {
                side = SECOND_PLAYER;
                isHuman = secondPlayer.isHuman();
                level = secondPlayer;
            }

            if (isHuman) {
                playerMove(side);
            } else {
                computerMove(side, level);
            }

            printBattleField();
        }
    }

    private void enterTheCells() {
        System.out.print("Enter the cells: ");
        String[] str = scanner.nextLine().replaceAll("_", " ").split("");

        fillTheBattlefield(str);
    }

    private void fillTheBattlefield(String[] input) {
        int k = 0;

        for (int i = 0; i < battleField.length; i++) {
            for (int j = 0; j < battleField[i].length; j++) {
                battleField[i][j] = input != null ? input[k] : " ";
                k++;
            }
        }
    }

    private boolean isGameOver() {

        if (isWinner(this.battleField, FIRST_PLAYER)) {
            System.out.println("X wins\n");
            return true;
        } else if (isWinner(this.battleField, SECOND_PLAYER)) {
            System.out.println("O wins\n");
            return true;
        } else if (isDraw()) {
            System.out.println("Draw\n");
            return true;
        } else {
//            System.out.println("Game not finished\n");
            return false;
        }
    }

    private void computerMove(String side, Level level) {

        System.out.printf("Making move level \"%s\"\n", level.name().toLowerCase());

        switch (level) {
            case EASY: {
                easyLevel(side);
                break;
            }
            case MEDIUM: {
                if (!mediumLevel(side)) {
                    easyLevel(side);
                }
                break;
            }
            case HARD: {
                hardLevel(side);
                break;
            }
        }
    }

    private void easyLevel(String side) {
        List<FreeCell> freeCellsList = getFreeCells(this.battleField);

        int index;
        if (freeCellsList.size() > 1) {
            index = random.nextInt(freeCellsList.size());
        } else {
            index = 0;
        }
        int row = freeCellsList.get(index).getRow();
        int column = freeCellsList.get(index).getColumn();

        battleField[row][column] = side;
    }

    private boolean mediumLevel(String side) {
        FreeCell cell;

        // finish game
        cell = winingOrBlocking(side);
        if (cell != null) {
            battleField[cell.getRow()][cell.getColumn()] = side;
            return true;
        }

        // block user move
        cell = winingOrBlocking(FIRST_PLAYER.equals(side) ? SECOND_PLAYER : FIRST_PLAYER);
        if (cell != null) {
            battleField[cell.getRow()][cell.getColumn()] = side;
            return true;
        }

        return false;
    }

    private void hardLevel(String side) {
        FreeCell nextMove = bestMove(battleField, side);
        battleField[nextMove.getRow()][nextMove.getColumn()] = side;
    }

    private FreeCell bestMove(String[][] battleField, String side) {
        int bestScore = FIRST_PLAYER.equals(side) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        FreeCell nextMove = null;
        List<FreeCell> freeCells = getFreeCells(battleField);

        for (FreeCell cell : freeCells) {
            battleField[cell.getRow()][cell.getColumn()] = side;
            int score = minimax(battleField, FIRST_PLAYER.equals(side) ?
                    SECOND_PLAYER : FIRST_PLAYER);
            battleField[cell.getRow()][cell.getColumn()] = " ";
            if (FIRST_PLAYER.equals(side) && score > bestScore ||
                SECOND_PLAYER.equals(side) && score < bestScore) {
                bestScore = score;
                nextMove = cell;
            }
        }

        return nextMove;
    }

    private int minimax(String[][] newBattleField, String side) {

        int bestScore = FIRST_PLAYER.equals(side) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        List<FreeCell> freeCellList = getFreeCells(newBattleField);

        if (isWinner(newBattleField, side)) {
            return FIRST_PLAYER.equals(side) ? 10 : -10;
        } else if (freeCellList.size() == 0) {
            return 0;
        }

        for (FreeCell cell : freeCellList) {
            newBattleField[cell.getRow()][cell.getColumn()] = side;
            int score = minimax(newBattleField, FIRST_PLAYER.equals(side) ?
                    SECOND_PLAYER : FIRST_PLAYER);
            newBattleField[cell.getRow()][cell.getColumn()] = " ";
            bestScore = FIRST_PLAYER.equals(side) ? Math.max(score, bestScore) :
                    Math.min(score, bestScore);
        }

        return bestScore;
    }

    private List<FreeCell> getFreeCells(String[][] battleField) {
        List<FreeCell> indexesOfFreeCells = new ArrayList<>();

        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (" ".equals(battleField[i][j])) {
                    indexesOfFreeCells.add(new FreeCell(i, j));
                }
            }
        }
        return indexesOfFreeCells;
    }

    private void playerMove(String side) {
        boolean correctInput = false;

        while (!correctInput) {
            System.out.print("Enter the coordinates: ");
            try {
                int row = scanner.nextInt();
                int column = scanner.nextInt();
                correctInput = isCoordinatesRight(row, column);
                if (correctInput) {
                    battleField[row - 1][column - 1] = side;
                }
            } catch (InputMismatchException e) {
                scanner = new Scanner(System.in);
                System.out.println("You should enter numbers!");
            }
        }
    }

    private boolean isMoveX() {
        int countX = Arrays.deepToString(battleField).split("X").length;
        int countO = Arrays.deepToString(battleField).split("O").length;
        return countX <= countO;
    }

    private boolean isDraw() {
        for (String[] row : battleField) {
            for (String element : row) {
                if (" ".equals(element)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isCoordinatesRight(int row, int column) {

        if (row < 1 || row > FIELD_SIZE || column < 1 || column > FIELD_SIZE) {
            System.out.println("Coordinates should be from 1 to 3!");
            return false;
        }

        if (!" ".equals(battleField[row - 1][column - 1])) {
            System.out.println("This cell is occupied! Choose another one!");
            return false;
        }

        return true;
    }

    private void printBattleField() {
        System.out.println("-".repeat(FIELD_SIZE * 2 + 3));
        for (String[] row : battleField) {
            System.out.print("| ");
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println("| ");
        }
        System.out.println("-".repeat(FIELD_SIZE * 2 + 3));
    }

    private boolean isWinner(String[][] newBattleField, String side) {
        int diagonal = 0;
        int diagonal2 = 0;

        for (int i = 0; i < newBattleField.length; i++) {
            diagonal += side.equals(newBattleField[i][i]) ? 1 : 0;
            diagonal2 += side.equals(newBattleField[i][FIELD_SIZE - 1 - i]) ? 1 : 0;
        }

        if (diagonal == FIELD_SIZE || diagonal2 == FIELD_SIZE) return true;

        for (int i = 0; i < newBattleField.length; i++) {
            int countInRow = 0;
            int countInColumn = 0;

            for (int j = 0; j < newBattleField.length; j++) {
                countInRow += side.equals(newBattleField[i][j]) ? 1 : 0;
                countInColumn += side.equals(newBattleField[j][i]) ? 1 : 0;
            }

            if (countInColumn == FIELD_SIZE || countInRow == FIELD_SIZE) return true;
        }

        return false;
    }

    private FreeCell winingOrBlocking(String side) {
        FreeCell freeCell1 = null;
        FreeCell freeCell2 = null;
        int diagonal = 0;
        int diagonal2 = 0;

        for (int i = 0; i < battleField.length; i++) {
            if (side.equals(battleField[i][i])) {
                diagonal++;
            } else if (" ".equals(battleField[i][i])) {
                freeCell1 = new FreeCell(i, i);
            }

            if (side.equals(battleField[i][FIELD_SIZE - 1 - i])) {
                diagonal2++;
            } else if (" ".equals(battleField[i][FIELD_SIZE - 1 - i])) {
                freeCell2 = new FreeCell(i, FIELD_SIZE - 1 - i);
            }
        }

        if (diagonal == FIELD_SIZE - 1 && freeCell1 != null) return freeCell1;

        if (diagonal2 == FIELD_SIZE - 1 && freeCell2 != null) return freeCell2;

        for (int i = 0; i < battleField.length; i++) {
            int countInRow = 0;
            int countInColumn = 0;
            freeCell1 = null;
            freeCell2 = null;

            for (int j = 0; j < battleField.length; j++) {
                if (side.equals(battleField[i][j])) {
                    countInRow++;
                } else if (" ".equals(battleField[i][j])) {
                    freeCell1 = new FreeCell(i, j);
                }

                if (side.equals(battleField[j][i])) {
                    countInColumn++;
                } else if (" ".equals(battleField[j][i])) {
                    freeCell2 = new FreeCell(j, i);
                }
            }

            if (countInRow == FIELD_SIZE - 1 && freeCell1 != null) return freeCell1;
            if (countInColumn == FIELD_SIZE - 1 && freeCell2 != null) return freeCell2;
        }

        return null;
    }
}
