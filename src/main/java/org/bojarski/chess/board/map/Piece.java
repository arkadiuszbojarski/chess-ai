package org.bojarski.chess.board.map;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.bojarski.chess.board.map.Field.field;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Piece {
    @NonNull protected final PieceKind type;
    @NonNull protected final Side side;
    @NonNull protected final Field position;

    protected boolean isEnemy(Piece piece) {
        return piece.side() != this.side;
    }

    public Piece perform(Move move, Board board) {
        return type.of(side, move.to());
    }

    public abstract List<Move> moves(Board board);

    protected boolean generateMove(List<Move> moves, int file, int rank, Board board) {
        final var destination = field(position.getFile() + file, position.getRank() + rank).orElse(null);

        if (destination != null) {
            final var target = board.piece(destination);

            if (target.isEmpty()) {
                moves.add(Move.move(position, destination));
            }

            target.filter(this::isEnemy)
                    .map(enemy -> enemy.type() == PieceKind.KING ? Move.check(position, destination, enemy) : Move.capture(position, destination, enemy))
                    .ifPresent(moves::add);

            return target.isEmpty();
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", side, this.getClass().getSimpleName(), position);
    }

}
