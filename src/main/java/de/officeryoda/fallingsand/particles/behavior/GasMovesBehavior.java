package de.officeryoda.fallingsand.particles.behavior;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.Particle;

import java.util.ArrayList;
import java.util.List;

public class GasMovesBehavior extends MovesBehavior {

    public GasMovesBehavior(double acceleration, double maxSpeed) {
        super(acceleration, maxSpeed);
    }

    @Override
    protected MovesResult possibleMoves(Grid grid, int i) {
        int gridWidth = grid.getWidth();

        double nextDelta = Math.signum(this.velocity) * gridWidth;
        int nextVertical = i + (int) nextDelta;
        int nextVerticalLeft = nextVertical - 1;
        int nextVerticalRight = nextVertical + 1;
        int column = nextVertical % gridWidth;

        List<Integer> moves = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();


        // check all possible ways to add horizontal movement
        if(canPassThrough(grid.get(nextVertical))) {
            moves.add(nextVertical);
            weights.add(2);
        }
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

        return new MovesResult(moves, weights);
    }

    @Override
    protected boolean canPassThrough(Particle particle) {
        return particle.isEmpty();
    }
}
