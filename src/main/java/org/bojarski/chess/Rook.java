package org.bojarski.chess;

import java.util.ArrayList;
import java.util.List;

// TODO implement castling
public class Rook extends Piece {
    public Rook(Side side, Field position) {
        super(PieceKind.ROOK, side, position);
    }

    @Override
    public List<Move> moves(Board board) {
        final var moves = new ArrayList<Move>();

        for (int i = 1; i < 8; i++) if (!generateMove(moves, 0, +i, board)) break;
        for (int i = 1; i < 8; i++) if (!generateMove(moves, +i, 0, board)) break;
        for (int i = 1; i < 8; i++) if (!generateMove(moves, 0, -i, board)) break;
        for (int i = 1; i < 8; i++) if (!generateMove(moves, -i, 0, board)) break;

        return moves;
    }

}
