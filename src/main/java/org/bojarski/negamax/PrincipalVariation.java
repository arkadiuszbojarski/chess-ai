package org.bojarski.negamax;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PrincipalVariation<S, A> {
    public static <S, A> PrincipalVariation<S, A> head() {
        return new PrincipalVariation<>( null, false, true, 0, null);
    }

    private List<PrincipalVariation<S, A>> children = new ArrayList<>();
    private Double score;

    private final PrincipalVariation<S, A> parent;
    private final boolean maximizing;
    private final boolean head;
    private final int length;
    private final A action;

    public PrincipalVariation<S, A> link(A action) {
        final var variation = new PrincipalVariation( this, !maximizing, false, length + 1, action);
        this.children.add(variation);

        return variation;
    }

    public void score(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return head ? "()" : parent + " " + action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrincipalVariation<?, ?> that = (PrincipalVariation<?, ?>) o;
        return maximizing == that.maximizing &&
                head == that.head &&
                length == that.length &&
                Objects.equals(parent, that.parent) &&
                Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, maximizing, head, length, action);
    }
}
