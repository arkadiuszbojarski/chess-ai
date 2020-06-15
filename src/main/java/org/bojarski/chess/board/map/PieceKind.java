package org.bojarski.chess.board.map;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Optional;

import static java.util.Arrays.stream;

@Getter
@Accessors(fluent = true)
public enum PieceKind {
    PAWN("P") {
        @Override
        public Piece of(Side side, Field position) {
            return new Pawn(side, position, true);
        }
    },
    ROOK("R") {
        @Override
        public Piece of(Side side, Field position) {
            return new Rook(side, position);
        }
    },
    KNIGHT("H") {
        @Override
        public Piece of(Side side, Field position) {
            return new Knight(side, position);
        }
    },
    BISHOP("B") {
        @Override
        public Piece of(Side side, Field position) {
            return new Bishop(side, position);
        }
    },
    QUEEN("Q") {
        @Override
        public Piece of(Side side, Field position) {
            return new Queen(side, position);
        }
    },
    KING("K") {
        @Override
        public Piece of(Side side, Field position) {
            return new King(side, position);
        }
    };

    private String code;

    PieceKind(String code) {
        this.code = code;
    }

    public abstract Piece of(Side side, Field position);

    public static Optional<PieceKind> byCode(final String code) {
        return stream(PieceKind.values())
                .filter(kind -> kind.code.equals(code.toUpperCase()))
                .findAny();
    }
}
