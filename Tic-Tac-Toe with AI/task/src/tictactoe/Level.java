package tictactoe;

public enum Level {
    USER(true),
    EASY(false),
    MEDIUM(false),
    HARD(false);

    private boolean isHuman;

    Level(boolean isHuman) {
        this.isHuman = isHuman;
    }

    public boolean isHuman() {
        return isHuman;
    }
}
