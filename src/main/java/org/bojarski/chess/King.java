package org.bojarski.chess;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

// TODO implement castling
public class King extends Piece {
    public King(Side side, Field position) {
        super(PieceKind.KING, side, position);
    }

    @Override
    public List<Move> moves(Board board) {
        final var moves = new ArrayList<Move>();

        generateMove(moves, 0, +1, board);
        generateMove(moves, +1, +1, board);
        generateMove(moves, +1, 0, board);
        generateMove(moves, +1, -1, board);
        generateMove(moves, 0, -1, board);
        generateMove(moves, -1, -1, board);
        generateMove(moves, -1, 0, board);
        generateMove(moves, -1, +1, board);

        return unmodifiableList(moves);
    }

}
