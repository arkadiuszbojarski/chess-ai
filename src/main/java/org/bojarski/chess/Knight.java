package org.bojarski.chess;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Knight extends Piece {
    public Knight(Side side, Field position) {
        super(PieceKind.KNIGHT, side, position);
    }

    @Override
    public List<Move> moves(Board board) {
        final List<Move> moves = new ArrayList<>();

        generateMove(moves, +1, +2, board);
        generateMove(moves, +2, +1, board);
        generateMove(moves, +2, -1, board);
        generateMove(moves, +1, -2, board);
        generateMove(moves, -1, -2, board);
        generateMove(moves, -2, -1, board);
        generateMove(moves, -2, +1, board);
        generateMove(moves, -1, +2, board);

        return unmodifiableList(moves);
    }

}
