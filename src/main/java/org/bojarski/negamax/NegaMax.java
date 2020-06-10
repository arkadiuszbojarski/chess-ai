package org.bojarski.negamax;

import lombok.NonNull;
import lombok.Value;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.util.Comparator.comparing;

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
        private final Map<PrincipalVariation<S, A>, Double> scores = new HashMap<>();
        private final Predicate<S> predicate;

        private boolean completed;
        private int depth;
        private S start;

        public NegaMaxSearch(S start, Predicate<S> predicate, int depth) {
            this.completed = predicate.test(start);
            this.predicate = predicate;
            this.depth = depth;
            this.start = start;
        }

        @Override
        public Iterator<PrincipalVariation<S, A>> iterator() {
            return new PrincipalVariationIterator();
        }

        private double negamax(S state, int depth, double alpha, double beta, PrincipalVariation<S, A> source) {
            final var goal = goal(state);
            if (goal) completed = true;
            if (depth == 0 || goal) {
                return heuristic.applyAsDouble(state);
            }

            for (A action : actions(state)) {
                final var child = domain.perform(state, action);
                final var link = source.link(action);
                final var score = -negamax(child, depth - 1, -beta, -alpha, link);
                link.score(score);
                if (score >= beta) return beta;
                if (score > alpha) {
                    alpha = score;
                    scores.put(link, score);
                }
            }

            return alpha;
        }

        private Collection<A> actions(S state) {
            return domain.actions(state);
        }

        private boolean goal(S state) {
            return predicate.test(state);
        }

        private class PrincipalVariationIterator implements Iterator<PrincipalVariation<S, A>> {

            @Override
            public boolean hasNext() {
                return !completed;
            }

            @Override
            public PrincipalVariation<S, A> next() {
                if (!hasNext()) throw new NoSuchElementException();
                negamax(start, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, PrincipalVariation.head());
                depth += 2;

                final var maximization = scores.entrySet().stream()
                        .filter(entry -> entry.getKey().maximizing())
                        .max(Map.Entry.comparingByValue());

                final var minimization = scores.entrySet().stream()
                        .filter(entry -> !entry.getKey().maximizing())
                        .max(Map.Entry.comparingByValue());

                final var optimal = Stream
                        .of(maximization, minimization)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .max(comparing(entry -> abs(entry.getValue())))
                        .map(Map.Entry::getKey)
                        .orElse(null);

                return optimal;
            }
        }
    }
}
