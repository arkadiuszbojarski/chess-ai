package org.bojarski.chess.board.map;

import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

public class Board {

    public static Board initialized() {
        return board(Side.WHITE, Map.of(), 0).initialize();
    }
    public static Board empty() {
        return board(Side.WHITE, Map.of(), 0);
    }
    private static Board board(Side side, Map<Field, Piece> pieces, Integer count) {
        return new Board(side, pieces, count);
    }

    private final Map<Field, Piece> pieces;
    private final Side movingside;

    private final Integer count;

    private List<Move> moves;

    private Board(Side movingside, Map<Field, Piece> pieces, Integer count) {
        this.movingside = movingside;

        this.pieces = pieces;
        this.count = count;
    }

    private Board initialize() {
        return placePiece(Side.WHITE, PieceKind.PAWN, Field.A2).
                placePiece(Side.WHITE, PieceKind.PAWN, Field.B2).
                placePiece(Side.WHITE, PieceKind.PAWN, Field.C2).
                placePiece(Side.WHITE, PieceKind.PAWN, Field.D2).
                placePiece(Side.WHITE, PieceKind.PAWN, Field.E2).
                placePiece(Side.WHITE, PieceKind.PAWN, Field.F2).
                placePiece(Side.WHITE, PieceKind.PAWN, Field.G2).
                placePiece(Side.WHITE, PieceKind.PAWN, Field.H2).

                placePiece(Side.WHITE, PieceKind.ROOK, Field.A1).
                placePiece(Side.WHITE, PieceKind.ROOK, Field.H1).

                placePiece(Side.WHITE, PieceKind.KNIGHT, Field.B1).
                placePiece(Side.WHITE, PieceKind.KNIGHT, Field.G1).

                placePiece(Side.WHITE, PieceKind.BISHOP, Field.C1).
                placePiece(Side.WHITE, PieceKind.BISHOP, Field.F1).

                placePiece(Side.WHITE, PieceKind.QUEEN, Field.D1).

                placePiece(Side.WHITE, PieceKind.KING, Field.E1).


                placePiece(Side.BLACK, PieceKind.PAWN, Field.A7).
                placePiece(Side.BLACK, PieceKind.PAWN, Field.B7).
                placePiece(Side.BLACK, PieceKind.PAWN, Field.C7).
                placePiece(Side.BLACK, PieceKind.PAWN, Field.D7).
                placePiece(Side.BLACK, PieceKind.PAWN, Field.E7).
                placePiece(Side.BLACK, PieceKind.PAWN, Field.F7).
                placePiece(Side.BLACK, PieceKind.PAWN, Field.G7).
                placePiece(Side.BLACK, PieceKind.PAWN, Field.H7).

                placePiece(Side.BLACK, PieceKind.ROOK, Field.A8).
                placePiece(Side.BLACK, PieceKind.ROOK, Field.H8).

                placePiece(Side.BLACK, PieceKind.KNIGHT, Field.B8).
                placePiece(Side.BLACK, PieceKind.KNIGHT, Field.G8).

                placePiece(Side.BLACK, PieceKind.BISHOP, Field.C8).
                placePiece(Side.BLACK, PieceKind.BISHOP, Field.F8).

                placePiece(Side.BLACK, PieceKind.QUEEN, Field.D8).

                placePiece(Side.BLACK, PieceKind.KING, Field.E8);
    }

    public Board removePiece(Field position) {
        final var piecesCopy = new HashMap<>(pieces);
        piecesCopy.remove(position);

        return board(movingside, piecesCopy, count);
    }

    public Board placePiece(Side side, PieceKind piece, Field position) {
        piece(position).ifPresent(previousPiece -> {
            throw new RuntimeException("There is already " + previousPiece + " in field " + position);
        });

        final var placedPiece = piece.of(side, position);
        final var piecesCopy = new HashMap<>(pieces);
        piecesCopy.put(placedPiece.position, placedPiece);

        return board(movingside, piecesCopy, count);
    }

    public Board perform(Move move) {
        final Piece piece = piece(move.from()).orElseThrow(() -> new RuntimeException("No piece in field " + move.from()));

        if (piece.side() != movingside) {
            throw new RuntimeException("Its " + movingside + "'s turn");
        }

        final var target = piece(move.to());

        if (target.filter(enemy -> !enemy.isEnemy(piece)).isPresent()) {
            throw new RuntimeException("Cannot capture " + target.get() + " with " + piece);
        }

        final var piecesCopy = new HashMap<>(pieces);
        piecesCopy.remove(piece.position);
        final var movedPiece = piece.perform(move, this);
        piecesCopy.put(movedPiece.position, movedPiece);

        return board(movingside.flip(), piecesCopy, count + 1);
    }

    public List<Move> moves() {
        if (moves == null) {
            moves = moves(movingside).stream()
                    .filter(move -> perform(move)
                            .moves(movingside.flip()).stream()
                            .map(Move::type)
                            .noneMatch(MoveType::check))
                    .collect(toUnmodifiableList());
        }

        return moves;
    }

    private List<Move> moves(Side side) {
        return pieces.values().stream()
                .filter(piece -> piece.side() == side)
                .flatMap(piece -> piece.moves(this).stream())
                .collect(toList());
    }

    public Side side() {
        return movingside;
    }

    public Integer count() {
        return count;
    }

    Optional<Piece> piece(Field field) {
        return Optional.ofNullable(pieces.get(field));
    }

    public boolean gameover() {
        return moves().isEmpty();
    }

    public Collection<Piece> pieces() {
        return pieces.values();
    }

    public String print() {
        var board = "   a  b  c  d  e  f  g  h   \n" +
                    "8 [a8][b8][c8][d8][e8][f8][g8][h8] 8\n" +
                    "7 [a7][b7][c7][d7][e7][f7][g7][h7] 7\n" +
                    "6 [a6][b6][c6][d6][e6][f6][g6][h6] 6\n" +
                    "5 [a5][b5][c5][d5][e5][f5][g5][h5] 5\n" +
                    "4 [a4][b4][c4][d4][e4][f4][g4][h4] 4\n" +
                    "3 [a3][b3][c3][d3][e3][f3][g3][h3] 3\n" +
                    "2 [a2][b2][c2][d2][e2][f2][g2][h2] 2\n" +
                    "1 [a1][b1][c1][d1][e1][f1][g1][h1] 1\n" +
                    "   a  b  c  d  e  f  g  h  \n";

        for (Piece piece : pieces.values()) {
            final var position = piece.position.name().toLowerCase();
            final var code = piece.type.code();
            final var representation = piece.side() == Side.WHITE ? code.toUpperCase() : code.toLowerCase();
            board = board.replace(position, representation);
        }

        for (Field field : Field.values()) {
            final var code = field.name().toLowerCase();
            board = board.replace(code, " ");
        }

        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        return pieces.equals(board.pieces);
    }

    @Override
    public int hashCode() {
        return pieces.hashCode();
    }
}
