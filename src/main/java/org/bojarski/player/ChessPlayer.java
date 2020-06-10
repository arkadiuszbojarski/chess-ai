package org.bojarski.player;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bojarski.chess.*;
import org.bojarski.negamax.Domain;
import org.bojarski.negamax.NegaMax;
import org.bojarski.negamax.PrincipalVariation;
import org.bojarski.negamax.Search;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static org.bojarski.chess.PieceKind.*;
import static org.bojarski.chess.Side.WHITE;
import static org.bojarski.negamax.PrincipalVariation.head;

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

        return new MoveIterator(iterator);
    }

    public Iterator<Move> findMove(Board board) {
        return findMove(board, 2);
    }

    public static Double evaluate(Board board) {
        var result = 0.0;
        for (Piece piece : board.pieces()) {
            if (piece.type() == PAWN) result += (board.side() == WHITE ? 1 : -1) * (10 + scorepawn(piece));
            if (piece.type() == KNIGHT) result += (board.side() == WHITE ? 1 : -1) * (30 + scorehorse(piece));
            if (piece.type() == BISHOP) result += (board.side() == WHITE ? 1 : -1) * 30.0;
            if (piece.type() == ROOK) result += (board.side() == WHITE ? 1 : -1) * 50.0;
            if (piece.type() == QUEEN) result += (board.side() == WHITE ? 1 : -1) * 90.0;
            if (piece.type() == KING) result += (board.side() == WHITE ? 1 : -1) * 900.0;
        }

        if (board.gameover()) result += (board.side() == WHITE ? 1 : -1) * 1000.0;
        result += (board.side() == WHITE ? 1 : -1) * 10 * board.count();

        return result;
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

    private Move extract(PrincipalVariation<Board, Move> variation) {
        var move = variation.action();
        while (!variation.parent().equals(head())) {
            variation = variation.parent();
            move = variation.action();
        }

        return move;
    }

    @RequiredArgsConstructor
    private class MoveIterator implements Iterator<Move> {

        @NonNull
        private final Iterator<PrincipalVariation<Board, Move>> iterator;

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Move next() {
            return extract(iterator.next());
        }
    }

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
