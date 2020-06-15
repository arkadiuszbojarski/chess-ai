package org.bojarski;

import org.bojarski.chess.board.map.Board;
import org.bojarski.chess.board.map.Field;
import org.bojarski.chess.board.map.Move;
import org.bojarski.chess.board.map.PieceKind;
import org.bojarski.player.ChessPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import static java.lang.Integer.parseInt;
import static org.bojarski.chess.board.map.Move.move;
import static org.bojarski.chess.board.map.Side.BLACK;
import static org.bojarski.chess.board.map.Side.WHITE;

public class Application {
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    public static final String SEPARATOR = " ";
    private static final List<Move> moves = new ArrayList<>();
    private static ChessPlayer player = new ChessPlayer();
    private static Board board = Board.initialized();
    private static int defaultDepth = 4;
    private static int waittime = 5;

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        System.out.println(board.print());

        String input = "";

        while (true) {
            try {
                input = scanner.nextLine();
                final var command = input.split(SEPARATOR, 2);

                final var code = command[0];
                final var arguments = command.length > 1 ? command[1] : "";
                switch (code) {
                    case "pp":
                        placePiece(arguments);
                        break;
                    case "rp":
                        removePiece(arguments);
                        break;
                    case "mm":
                        makeMove(arguments);
                        break;
                    case "fm":
                        findMove();
                        break;
                    case "dd":
                        defaultDepth(arguments);
                        break;
                    case "rb":
                        resetBoard();
                        break;
                    case "wt":
                        waittime(arguments);
                        break;
                    case "ap":
                        autoplay();
                        break;
                    case "pm":
                        printmoves();
                        break;
                }

                System.out.println(board.print());
                if (board.gameover()) System.out.println("gameover");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printmoves() {
        moves.forEach(System.out::println);
    }

    private static void autoplay() {
        while (!board.gameover()) {
            final var move = player.findMove(board, defaultDepth).next();
            board = board.perform(move);
            moves.add(move);
            System.out.println(move);
            System.out.println(board.print());
        }
    }

    private static void waittime(String arguments) {
        waittime = parseInt(arguments);
    }

    private static void resetBoard() {
        board = Board.initialized();
    }

    private static void defaultDepth(String arguments) {
        defaultDepth = parseInt(arguments);
    }

    private static void findMove() throws Exception {
        final var start = System.nanoTime();
        final var move = EXECUTOR.submit(() -> {
            final var search = player.findMove(board, defaultDepth);
            if (search.hasNext()) {
                return search.next();
            }
            return null;
        }).get(waittime, TimeUnit.MINUTES);
        final var end = System.nanoTime();
        System.out.println(move + " found in " + ((end - start) / 1000000) + "ms");
        board = board.perform(move);
        moves.add(move);
    }

    private static void makeMove(String arguments) {
        final var sections = arguments.split(SEPARATOR);
        final var from = Field.byCode(sections[0].toUpperCase()).orElseThrow(() -> new RuntimeException("No field " + sections[0]));
        final var to = Field.byCode(sections[1].toUpperCase()).orElseThrow(() -> new RuntimeException("No field " + sections[1]));

        final var move = move(from, to);
        board = board.perform(move);
        System.out.println(move);
        moves.add(move);
    }

    private static void placePiece(String arguments) {
        final var sections = arguments.split(SEPARATOR);

        final var side = sections[0].equals("w") ? WHITE : BLACK;
        final var kind = PieceKind.byCode(sections[1]).orElseThrow(() -> new RuntimeException("No piece " + sections[2]));
        final var field = Field.byCode(sections[2].toUpperCase()).orElseThrow(() -> new RuntimeException("No field " + sections[3]));

        board = board.placePiece(side, kind, field);
    }

    private static void removePiece(String code) {
        final var field = Field.byCode(code.toUpperCase()).orElseThrow(() -> new RuntimeException("No field " + code));
        board = board.removePiece(field);
    }
}
