package org.bojarski.chess.board.map;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.bojarski.chess.board.map.Field.field;
import static org.bojarski.chess.board.map.Move.*;

// TODO: implement en-passant
public class Pawn extends Piece {
    private final boolean initial;

    Pawn(Side side, Field position, boolean initial) {
        super(PieceKind.PAWN, side, position);
        this.initial = initial;
    }

    @Override
    public List<Move> moves(Board board) {
        final List<Move> moves = new ArrayList<>();

        final var singleAdvance = field(position.getFile(), position.getRank() + side.rankAdvanceDirection())
                .filter(field -> board.piece(field).isEmpty());
        singleAdvance
                .map(field -> enablesPromotion(field) ? List.of(queenPromotion(position, field, null), knightPromotion(position, field, null), rookPromotion(position, field, null), bishopPromotion(position, field, null)) : prepareMove(field))
                .orElse(List.of())
                .forEach(moves::add);

        if (initial && singleAdvance.isPresent()) {
            field(position.getFile(), position.getRank() + 2 * side.rankAdvanceDirection())
                    .filter(field -> board.piece(field).isEmpty())
                    .map(field -> enablesPromotion(field) ? List.of(queenPromotion(position, field, null), knightPromotion(position, field, null), rookPromotion(position, field, null), bishopPromotion(position, field, null)) : prepareMove(field))
                    .orElse(List.of())
                    .forEach(moves::add);
        }

        field(position.getFile() - 1, position.getRank() + side.rankAdvanceDirection())
                .filter(field -> board.piece(field).filter(this::isEnemy).isPresent())
                .map(field -> enablesPromotion(field) ? List.of(queenPromotion(position, field, board.piece(field).get()), knightPromotion(position, field, board.piece(field).get()), rookPromotion(position, field, board.piece(field).get()), bishopPromotion(position, field, board.piece(field).get())) : List.of(capture(position, field, board.piece(field).get())))
                .orElse(List.of())
                .forEach(moves::add);

        field(position.getFile() + 1, position.getRank() + side.rankAdvanceDirection())
                .filter(field -> board.piece(field).filter(this::isEnemy).isPresent())
                .map(field -> enablesPromotion(field) ? List.of(queenPromotion(position, field, board.piece(field).get()), knightPromotion(position, field, board.piece(field).get()), rookPromotion(position, field, board.piece(field).get()), bishopPromotion(position, field, board.piece(field).get())) : List.of(capture(position, field, board.piece(field).get())))
                .orElse(List.of())
                .forEach(moves::add);

        return unmodifiableList(moves);
    }

    private List<Move> prepareMove(Field field) {
        return List.of(move(position, field));
    }

    private boolean enablesPromotion(Field field) {
        return Side.BLACK == side && field.getRank() == 0 || Side.WHITE == side && field.getRank() == 7;
    }

    @Override
    public Piece perform(Move move, Board board) {
        final var destination = move.to();
        return enablesPromotion(destination) ? move.promoted().of(side, destination) : new Pawn(side, destination, false);
    }
}
