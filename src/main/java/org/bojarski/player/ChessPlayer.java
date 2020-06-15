package org.bojarski.player;

import org.bojarski.chess.board.map.Board;
import org.bojarski.chess.board.map.Field;
import org.bojarski.chess.board.map.Move;
import org.bojarski.chess.board.map.Piece;
import org.bojarski.negamax.Domain;
import org.bojarski.negamax.NegaMax;
import org.bojarski.negamax.Search;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static org.bojarski.chess.board.map.Side.WHITE;

public class ChessPlayer {
    private static final Domain<Board, Move> CHESS = Domain.<Board, Move>builder()
            .actionPerformer((b, m) -> b.perform(m))
            .actionsProducer(b -> b.moves())
            .build();
    private static final ToDoubleFunction<Board> HEURISTIC = b -> evaluate(b);
    private static final NegaMax<Board, Move> ALGORITHM = NegaMax.of(HEURISTIC, CHESS);
    private static final Predicate<Board> GOAL = b -> b.gameover();
    private Search<Board, Move> search;

    public Iterator<Move> findMove(Board board, int depth) {
        search = ALGORITHM.search(board, GOAL, depth);
        final var iterator = search.iterator();

        return iterator;
    }

    public Iterator<Move> findMove(Board board) {
        return findMove(board, 2);
    }

    public static Double evaluate(Board board) {
        var result = 0.0;
        for (Piece piece : board.pieces()) {
            switch (piece.type()) {
                case PAWN:
                    result += piece.side().rankAdvanceDirection() * (10 + scorepawn(piece));
                    break;
                case KNIGHT:
                    result += piece.side().rankAdvanceDirection() * (30 + scorehorse(piece));
                    break;
                case BISHOP:
                    result += piece.side().rankAdvanceDirection() * (30 + scorebishop(piece));
                    break;
                case ROOK:
                    result += piece.side().rankAdvanceDirection() * 40.0;
                    break;
                case QUEEN:
                    result += piece.side().rankAdvanceDirection() * 90.0;
                    break;
                case KING:
                    result += piece.side().rankAdvanceDirection() * 1500.0;
                    break;
            }
        }

        return board.side().rankAdvanceDirection() * result;
    }

    private static double scorebishop(Piece piece) {
        final var side = piece.side();
        final var position = piece.position();

        return side == WHITE ? score(whitebishopscores, position) : score(blackbishopscores, position);
    }

    private static double scorepawn(Piece piece) {
        final var side = piece.side();
        final var position = piece.position();

        return side == WHITE ? score(whitepawnsscores, position) : score(blackpawnsscores, position);
    }

    private static double scorehorse(Piece piece) {
        final var position = piece.position();
        final var side = piece.side();

        return side == WHITE ? score(whitehorsescores, position) : score(blackhorsescores, position);
    }

    private static Double score(Double[][] scores, Field position) {
        return scores[position.getRank()][position.getFile()];
    }

    private static final Double[][] whitebishopscores = {
            {-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0},
            {-1.0,  0.5,  0.0,  0.0,  0.0,  0.0,  0.5, -1.0},
            {-1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0, -1.0},
            {-1.0,  0.0,  1.0,  1.0,  1.0,  1.0,  0.0, -1.0},
            {-1.0,  0.5,  0.5,  1.0,  1.0,  0.5,  0.5, -1.0},
            {-1.0,  0.0,  0.5,  1.0,  1.0,  0.5,  0.0, -1.0},
            {-1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
            {-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0},
    };

    private static final Double[][] blackbishopscores = rotate(whitebishopscores);

    private static final Double[][] whitehorsescores = {
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
            {-4.0, -2.0,  0.0,  0.5,  0.5,  0.0, -2.0, -4.0},
            {-3.0,  0.5,  1.0,  1.5,  1.5,  1.0,  0.5, -3.0},
            {-3.0,  0.0,  1.5,  2.0,  2.0,  1.5,  0.0, -3.0},
            {-3.0,  0.5,  1.5,  2.0,  2.0,  1.5,  0.5, -3.0},
            {-3.0,  0.0,  1.0,  1.5,  1.5,  1.0,  0.0, -3.0},
            {-4.0, -2.0,  0.0,  0.0,  0.0,  0.0, -2.0, -4.0},
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
    };

    private static final Double[][] blackhorsescores = rotate(whitehorsescores);

    private static final Double[][] whitepawnsscores = {
            {0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, 0.0},
            {0.5,  1.0,  1.0, -2.0, -2.0,  1.0,  1.0, 0.5},
            {0.5, -0.5, -1.0,  0.0,  0.0, -1.0, -0.5, 0.5},
            {0.0,  0.0,  0.0,  2.5,  2.5,  0.0,  0.0, 0.0},
            {0.5,  0.5,  1.0,  2.5,  2.5,  1.0,  0.5, 0.5},
            {1.0,  1.0,  2.0,  3.0,  3.0,  2.0,  1.0, 1.0},
            {5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0, 5.0},
            {0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, 0.0},
    };

    private static final Double[][] blackpawnsscores = rotate(whitepawnsscores);

    private static Double[][] rotate(Double[][] table) {
        final var doubles = new Double[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                doubles[7 - i][7 - j] = table[i][j];
            }
        }

        return doubles;
    }

}
