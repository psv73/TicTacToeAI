package tictactoe;

import java.util.Objects;

public final class FreeCell {
    private final int row;
    private final int column;
    private int score = 0;

    public FreeCell(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FreeCell) obj;
        return this.row == that.row &&
                this.column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "FreeCell[" +
                "row=" + row + ", " +
                "column=" + column + ']';
    }

}
