package de.officeryoda.fallingsand.particles.behavior;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.Particle;
import de.officeryoda.fallingsand.particles.behavior.Behavior;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MoveBehavior extends Behavior {

    protected final double maxSpeed;
    protected final double acceleration;
    protected double velocity;

    public MoveBehavior(double acceleration, double maxSpeed) {
        this.maxSpeed = maxSpeed;
        this.velocity = 0;
        this.acceleration = acceleration;
    }

    @Override
    public void update(Particle particle, Grid grid, int direction) {
        if(!shouldUpdate(direction)) return;

        int index = particle.getIndex();

        updateVelocity();
        int updateCount = getUpdateCount();

        // Update the number of times the particle instructs us to
        for(int v = 0; v < updateCount; v++) {
            step(particle, grid); // could change the particle index
            int newIndex = particle.getIndex();

            // If we swapped the particle to a new location,
            // we need to update our index to be that new one.
            // As we are repeatedly updating the same this.
            if(newIndex != index) {
                // We can add the same index multiple times; it's a set.
                grid.onModified(index);
                grid.onModified(newIndex);
            } else {
                resetVelocity();
                break;
            }

            index = particle.getIndex();
        }

        if(updateCount == 0) {
            grid.onModified(index);
        }
    }

    protected void step(@NotNull Particle particle, @NotNull Grid grid) {
        int i = particle.getIndex();
        if(grid.isEmpty(i)) { // OPT could be replaced with 'particle.isEmpty()' but leaving it in for now
            resetVelocity();
            return;
        }

        MovesResult movesResult = possibleMoves(grid, i);
        List<Integer> moves = movesResult.moves();
        List<Integer> weights = movesResult.weights();

        if(moves.isEmpty()) {
            resetVelocity();
            return;
        }

        int choice = choose(moves, weights);
        grid.swap(i, choice);
    }

    protected int choose(@NotNull List<Integer> moves, @NotNull List<Integer> weights) {
        if(moves.size() != weights.size()) {
            throw new IllegalArgumentException("Array and weights must be the same length");
        }

        int sum = weights.stream().mapToInt(Integer::intValue).sum();
        double r = Math.random() * sum;
        double threshold = 0;

        for(int i = 0; i < moves.size(); i++) {
            threshold += weights.get(i);
            if(threshold >= r) {
                return moves.get(i);
            }
        }

        throw new IllegalStateException("Shouldn't get here.");
    }

    protected int getUpdateCount() {
        double abs = Math.abs(velocity);
        int floored = (int) abs;
        double remainder = abs - floored;

        // Treat a remainder (e.g., 0.5) as a random chance to update
        return floored + (Math.random() < remainder ? 1 : 0);
    }

    protected boolean shouldUpdate(int direction) {
        return direction == Math.signum(nextVelocity());
    }

    protected boolean canPassThrough(@NotNull Particle particle) {
        return particle.isEmpty() || particle.isAiry() || particle.isFluid();
    }

    protected MovesResult possibleMoves(@NotNull Grid grid, int i) {
        int gridWidth = grid.getWidth();

        double nextDelta = Math.signum(velocity) * gridWidth;
        int nextVertical = i + (int) nextDelta;
        int nextVerticalLeft = nextVertical - 1;
        int nextVerticalRight = nextVertical + 1;
        int column = nextVertical % gridWidth;

        List<Integer> moves = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();

        if(canPassThrough(grid.get(nextVertical))) {
            moves.add(nextVertical);
            weights.add(2);
        } else {
            // Check to make sure belowLeft didn't wrap to the next line
            if(canPassThrough(grid.get(nextVerticalLeft)) && nextVerticalLeft % gridWidth < column) {
                moves.add(nextVerticalLeft);
                weights.add(1);
            }

            // Check to make sure belowRight didn't wrap to the next line
            if(canPassThrough(grid.get(nextVerticalRight)) && nextVerticalRight % gridWidth > column) {
                moves.add(nextVerticalRight);
                weights.add(1);
            }
        }

        return new MovesResult(moves, weights);
    }

    protected void resetVelocity() {
        velocity = 0;
    }

    protected void updateVelocity() {
        velocity = nextVelocity();
    }

    protected double nextVelocity() {
        if(velocity >= maxSpeed) {
            return maxSpeed;
        }
        double newVelocity = velocity + acceleration;

        if(Math.abs(newVelocity) > maxSpeed) {
            newVelocity = Math.signum(newVelocity) * maxSpeed;
        }
        return newVelocity;
    }

    protected record MovesResult(List<Integer> moves, List<Integer> weights) {
    }
}
