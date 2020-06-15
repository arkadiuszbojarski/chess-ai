package org.bojarski.chess;

import org.bojarski.player.ChessPlayer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.InstanceOfAssertFactories.array;
import static org.bojarski.chess.Field.*;
import static org.bojarski.chess.PieceKind.*;
import static org.bojarski.chess.Side.BLACK;
import static org.bojarski.chess.Side.WHITE;

public class BoardTest {

    @Test
    public void shouldnotfallforgambit() {
        var board = Board.initialized();
        board = board.perform(Move.move(D2, D4));
        board = board.perform(Move.move(D7, D5));
        board = board.perform(Move.move(C2, C4));

        final var player = new ChessPlayer();
        final var move = player.findMove(board, 4).next();

        board.perform(move);
    }

    @Test
    public void shouldNotHaveLegalMovesWhenCheckmated() {
        final var board = Board.empty()
                .placePiece(WHITE, KING, H1)
                .placePiece(BLACK, BISHOP, E3)
                .placePiece(BLACK, BISHOP, F3)
                .placePiece(BLACK, QUEEN, H3);

        then(board.gameover()).isTrue();
    }

    @Test
    public void shouldPromoteThroughCapture() {
        final var board = Board.empty()
                .placePiece(BLACK, PAWN, G8)
                .placePiece(WHITE, PAWN, H7);

        then(board.moves()).contains(Move.queenPromotion(H7, G8, PAWN.of(BLACK, G8)));
    }

    @Test
    public void speedtest() {
        final var initial = Board.initialized();
        var count = 0;

        List<Board> extended = new ArrayList<>();
        extended.add(initial);

        final var start = System.nanoTime();
        for (int i = 0; i < 6; i++) {
            final List<Board> children = new ArrayList<>();
            for (Board board : extended) {
                for (Move move : board.moves()) {
                    final var child = board.perform(move);
                    children.add(child);
                }
            }
            extended.clear();
            extended.addAll(children);
            count += extended.size();
        }
        final var end = System.nanoTime();

        System.out.println(count + " nodes extended in " + (end - start) / 1000000 + "ms");
    }

    @Test
    public void evaluationtest() {
        var board = Board.empty()
                .placePiece(WHITE, ROOK, C2)
                .placePiece(BLACK, PAWN, C3)
                .placePiece(BLACK, KNIGHT, D5);

        final var player = new ChessPlayer();
        var move = player.findMove(board, 5).next();

        board.perform(move);
    }

    @Test
    public void test() {
        var board = Board.empty()
                .placePiece(WHITE, KING, H1)
                .placePiece(WHITE, ROOK, B3)
                .placePiece(WHITE, ROOK, C3)
                .placePiece(WHITE, PAWN, C4)
                .placePiece(BLACK, KING, A8);

        final var player = new ChessPlayer();

        for (int moves = 1; moves <= 5; moves++) {
            final var search = player.findMove(board);
            final var start = System.nanoTime();

            Move move = null;

            for (int i = 0; i < 3 && search.hasNext(); i++) {
                move = search.next();
            }

            board = board.perform(move);
            System.out.println(board.print());
            if (board.gameover()) break;
            final var end = System.nanoTime();
            System.out.println((end - start) / 1000000);
        }

        System.out.println(board.print());
        then(board.gameover()).isTrue();
    }

    @Test
    public void profiling() {
        var board = Board.initialized();
        final var player = new ChessPlayer();

        for (int moves = 1; moves <= 5; moves++) {
            final var search = player.findMove(board, 2);
            board = board.perform(search.next());
        }

        final var samples = 25;
        var averange = 0;
        for (int i = 0; i < samples; i++) {
            final var start = System.nanoTime();
            final var move = player.findMove(board, 6).next();
            final var end = System.nanoTime();

            final var duration = (end - start) / 1000000;
            averange += duration;
        }
        System.out.println(averange / samples + "ms");
    }

}