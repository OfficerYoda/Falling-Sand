package de.officeryoda.fallingsand.particles.behavior;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.interfaces.LimitedLifeExecutor;
import de.officeryoda.fallingsand.particles.Particle;
import de.officeryoda.fallingsand.particles.behavior.executors.FlammableExecutor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FlammableBehavior extends LimitedLifeBehavior {

    private final int fuel = (int) (10 + 100 * (Math.random()));
    private final double ignitionChance;
    private double chancesToIgnite;
    @Getter
    private boolean burning;
    @Getter
    @Setter
    private boolean extinguished;

    public FlammableBehavior(int lifetime, boolean burning, double ignitionChance, LimitedLifeExecutor lifeExecutor) {
        super(lifetime, lifeExecutor);
        this.ignitionChance = ignitionChance;
        this.chancesToIgnite = 0;
        this.burning = burning;
    }

    public FlammableBehavior(int lifetime, boolean burning, double ignitionChance) {
        this(lifetime, burning, ignitionChance, new FlammableExecutor());
    }

    @Override
    public void update(Particle particle, Grid grid, int direction) {
        if(extinguished && burning) {
            burning = false;
        }

        if(chancesToIgnite > 0 && !burning) {
            // Check if we caught on fire
            double chanceToCatch = chancesToIgnite * ignitionChance;
            if(Math.random() < chanceToCatch) {
                burning = true;
                extinguished = false;
            }
            chancesToIgnite = 0;
        }

        // If we're burning, update our remaining
        // life and try to spread more fire.
        if(burning) {
            super.update(particle, grid, direction);
        }

        processNeighbours(particle, grid);
    }

    private void processNeighbours(Particle particle, Grid grid) {
        List<Integer> candidates = getNeighbours(particle, grid);
        candidates.forEach((index) -> {
            Particle p = grid.get(index);
            FlammableBehavior flammable = p.getBehavior(FlammableBehavior.class);
            if(flammable != null) {
                if(extinguished) {
                    if(flammable.isBurning() && Math.random() < 0.015) // 1.5% chance to extinguish neighbouring burning particles
                        flammable.extinguish(grid.get(index));
                } else if(burning) {
                        flammable.chancesToIgnite += 0.5 + Math.random() * 0.5;
                }
            }
        });
    }

    private @NotNull List<Integer> getNeighbours(@NotNull Particle particle, @NotNull Grid grid) {
        int index = particle.getIndex();
        int gridWidth = grid.getWidth();
        int gridSize = grid.getGridSize();
        int column = index % gridWidth;

        List<Integer> candidates = new ArrayList<>();

        // Each of the 8 directions
        for(int dx = -1; dx <= 1; dx++) {
            for(int dy = -1; dy <= 1; dy++) {
                int di = index + dx + dy * gridWidth;
                int x = di % gridWidth;
                // Make sure it's in our grid
                boolean inBounds = di >= 0 && di < gridSize;
                // Make sure we didn't wrap to the next or previous row
                boolean noWrap = Math.abs(x - column) <= 1;
                if(inBounds && noWrap) {
                    candidates.add(di);
                }
            }
        }

        return candidates;
    }

    public void extinguish(Particle particle) {
        if(extinguished) return;
        extinguished = true;
        particle.setColor(Colors.varyColor(particle.getBaseColor()));
    }
}


