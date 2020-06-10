package org.bojarski.chess;

public enum Side {
    WHITE(1),
    BLACK(-1);

    private final int advanceDirection;

    Side(int advanceDirection) {
        this.advanceDirection = advanceDirection;
    }

    public int rankAdvanceDirection() {
        return advanceDirection;
    }

    public Side flip() {
        return WHITE == this ? BLACK : WHITE;
    }
}
