package de.officeryoda.fallingsand.particle.behavior;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.Particle;

import java.util.ArrayList;
import java.util.List;

public class FallBehavior extends Behavior {

    private final double maxSpeed;
    private final double acceleration;
    private double velocity;

    // Constructor for initializing maxSpeed, acceleration, and velocity
    public FallBehavior(double acceleration, double maxSpeed) {
        this.maxSpeed = maxSpeed;
        this.velocity = 0;
        this.acceleration = acceleration;
    }

    // Update the particle's position in the grid based on behavior
    public void update(Particle particle, Grid grid) {
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

    // Perform a step of the behavior, updating particle position in the grid
    private void step(Particle particle, Grid grid) {
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

    // Choose an element from an array based on weights
    private int choose(List<Integer> moves, List<Integer> weights) {
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

    // Reset velocity to 0
    private void resetVelocity() {
        this.velocity = 0;
    }

    // Update velocity based on acceleration and maxSpeed constraints
    private void updateVelocity() {
        this.velocity = nextVelocity();
    }

    // Calculate the next velocity considering acceleration and maxSpeed constraints
    private double nextVelocity() {
        if(this.velocity >= maxSpeed) {
            return maxSpeed;
        }
        double newVelocity = this.velocity + this.acceleration;

        if(Math.abs(newVelocity) > this.maxSpeed) {
            newVelocity = Math.signum(newVelocity) * this.maxSpeed;
        }
        return newVelocity;
    }

    // Calculate the number of updates based on the current velocity
    private int getUpdateCount() {
        double abs = Math.abs(this.velocity);
        int floored = (int) abs;
        double remainder = abs - floored;

        // Treat a remainder (e.g., 0.5) as a random chance to update
        return floored + (Math.random() < remainder ? 1 : 0);
    }

    // Check if a particle can pass through based on its properties
    private boolean canPassThrough(Particle particle) {
        return particle.isEmpty()/* || particle.isAiry()*/; // TO-DO uncomment the commented code when implemented
    }

    // Calculate possible moves and their weights based on grid and current position
    private MovesResult possibleMoves(Grid grid, int i) {
        int gridWidth = grid.getWidth();

        double nextDelta = Math.signum(this.velocity) * gridWidth;
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

    // record representing the result of possible moves and their weights
    private record MovesResult(List<Integer> moves, List<Integer> weights) {
    }
}
