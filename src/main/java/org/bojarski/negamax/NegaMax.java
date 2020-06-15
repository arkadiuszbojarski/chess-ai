package org.bojarski.negamax;

import lombok.NonNull;
import lombok.Value;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

@Value(staticConstructor = "of")
public class NegaMax<S, A> {
    @NonNull private final ToDoubleFunction<S> heuristic;
    @NonNull private final Domain<S, A> domain;

    public Search<S, A> search(final S start, final Predicate<S> predicate) {
        return new NegaMaxSearch(start, predicate, 2);
    }

    public Search<S, A> search(final S start, final Predicate<S> predicate, int depth) {
        return new NegaMaxSearch(start, predicate, depth);
    }

    private class NegaMaxSearch implements Search<S, A> {
        private final Predicate<S> predicate;

        private boolean completed;
        private int depth;
        private A optimal;
        private S start;

        public NegaMaxSearch(S start, Predicate<S> predicate, int depth) {
            this.completed = predicate.test(start);
            this.predicate = predicate;
            this.depth = depth;
            this.start = start;
        }

        @Override
        public Iterator<A> iterator() {
            return new PrincipalVariationIterator();
        }

        private double negamax(S state, int depth, double alpha, double beta) {
            final var goal = goal(state);
            if (goal) completed = true;
            if (depth == 0 || goal) {
                return heuristic.applyAsDouble(state);
            }

            for (A action : actions(state)) {
                final var child = domain.perform(state, action);
                final var score = -negamax(child, depth - 1, -beta, -alpha);
                if (score >= beta) return beta;
                if (score > alpha) alpha = score;
            }

            return alpha;
        }

        private Collection<A> actions(S state) {
            return domain.actions(state);
        }

        private boolean goal(S state) {
            return predicate.test(state);
        }

        private class PrincipalVariationIterator implements Iterator<A> {

            private PrincipalVariationIterator() {
                if (depth == 0 || goal(start)) completed = true;
            }

            @Override
            public boolean hasNext() {
                return !completed;
            }

            @Override
            public A next() {
                if (!hasNext()) throw new NoSuchElementException();

                var alpha = Double.NEGATIVE_INFINITY;
                var beta = Double.POSITIVE_INFINITY;

                for (A action : actions(start)) {
                    final var child = domain.perform(start, action);
                    final var score = -negamax(child, depth - 1, -beta, -alpha);
                    if (score >= beta) {
                        break;
                    }
                    if (score > alpha) {
                        optimal = action;
                        alpha = score;
                    }
                }

                return optimal;
            }
        }
    }
}
