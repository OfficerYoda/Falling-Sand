package de.officeryoda.fallingsand.particles.behavior;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.Particle;

public class ExtinguishBehavior extends Behavior{

    @Override
    public void update(Particle particle, Grid grid, int direction) {
        int index = particle.getIndex();
        int gridWidth = grid.getWidth();
        int gridSize = grid.getGridSize();
        int column = index % gridWidth;

        // check neighbours
        for(int dx = -1; dx <= 1; dx++) {
            for(int dy = -1; dy <= 1; dy++) {
                int di = index + dx + dy * gridWidth;
                if(di == index) continue; // self check

                int x = di % gridWidth;
                // Make sure it's in our grid
                boolean inBounds = di >= 0 && di < gridSize;
                // Make sure we didn't wrap to the next or previous row
                boolean noWrap = Math.abs(x - column) <= 1;
                if(inBounds && noWrap) {
                    FlammableBehavior flammable = grid.get(di).getBehavior(FlammableBehavior.class);
                    if(flammable == null) continue;
                    flammable.extinguish(grid.get(di));
                }
            }
        }
    }
}
