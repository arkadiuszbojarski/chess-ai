package org.bojarski.chess.board.map;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Queen extends Piece {
    public Queen(Side side, Field position) {
        super(PieceKind.QUEEN, side, position);
    }

    @Override
    public List<Move> moves(Board board) {
        final var moves = new ArrayList<Move>();

        for (int i = 1; i < 8; i++) if (!generateMove(moves, 0, +i, board)) break;
        for (int i = 1; i < 8; i++) if (!generateMove(moves, +i, +i, board)) break;
        for (int i = 1; i < 8; i++) if (!generateMove(moves, +i, 0, board)) break;
        for (int i = 1; i < 8; i++) if (!generateMove(moves, +i, -i, board)) break;
        for (int i = 1; i < 8; i++) if (!generateMove(moves, 0, -i, board)) break;
        for (int i = 1; i < 8; i++) if (!generateMove(moves, -i, -i, board)) break;
        for (int i = 1; i < 8; i++) if (!generateMove(moves, -i, 0, board)) break;
        for (int i = 1; i < 8; i++) if (!generateMove(moves, -i, +i, board)) break;

        return unmodifiableList(moves);
    }

}
