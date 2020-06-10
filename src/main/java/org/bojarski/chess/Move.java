package org.bojarski.chess;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

import static java.lang.String.format;
import static org.bojarski.chess.MoveType.*;
import static org.bojarski.chess.PieceKind.*;

@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Move {
    public static Move move(Field from, Field to) {
        return new Move(MOVE, from, to, null, null);
    }
    public static Move capture(Field from, Field to, Piece piece) {
        return new Move(CAPTURE, from, to, null, piece);
    }
    public static Move check(Field from, Field to, Piece king) {
        return new Move(CHECK, from, to, null, king);
    }
    public static Move queenPromotion(Field from, Field to, Piece piece) {
        return new Move(PROMOTION, from, to, QUEEN, piece);
    }
    public static Move knightPromotion(Field from, Field to, Piece piece) {
        return new Move(PROMOTION, from, to, KNIGHT, piece);
    }
    public static Move rookPromotion(Field from, Field to, Piece piece) {
        return new Move(PROMOTION, from, to, ROOK, piece);
    }
    public static Move bishopPromotion(Field from, Field to, Piece piece) {
        return new Move(PROMOTION, from, to, BISHOP, piece);
    }

    private final MoveType type;
    private final Field from;
    private final Field to;
    private final PieceKind promoted;
    private final Piece captured;

    @Override
    public String toString() {
        return format("%s to %s", from, to);
    }
}
