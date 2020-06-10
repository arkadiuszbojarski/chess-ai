package org.bojarski.negamax;

import lombok.Builder;
import lombok.NonNull;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Domain<S, A> {
    static <S, A> DomainAdapter.DomainAdapterBuilder<S, A> builder() {
        return DomainAdapter.builder();
    }

    Collection<A> actions(S state);
    S perform(S state, A action);

    @Builder
    class DomainAdapter<S, A> implements Domain<S, A> {
        @NonNull
        private final Function<S, Collection<A>> actionsProducer;
        @NonNull private final BiFunction<S, A, S> actionPerformer;

        @Override
        public Collection<A> actions(S state) {
            return actionsProducer.apply(state);
        }

        @Override
        public S perform(S state, A action) {
            return actionPerformer.apply(state, action);
        }
    }
}
