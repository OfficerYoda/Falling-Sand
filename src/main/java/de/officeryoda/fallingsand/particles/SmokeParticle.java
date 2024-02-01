package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.behavior.*;
import de.officeryoda.fallingsand.particles.behavior.executors.BurningSmokeLifeExecutor;
import de.officeryoda.fallingsand.particles.behavior.executors.SmokeLifeExecutor;

public class SmokeParticle extends Particle {

    public SmokeParticle(Grid grid, int index) {
        super(Colors.SMOKE_COLOR,
                Colors.varyColor(Colors.SMOKE_COLOR, -0.05, 0.05, -0.05),
                new Behavior[]{
                        new GasMovesBehavior(-0.05, 0.25),
                        Math.random() < 0.1 ? // 10% chance to start burning when spawned
                                new FlammableBehavior((int) (200 + 400 * (Math.random())), true, 1.0, new BurningSmokeLifeExecutor()) :
                                new LimitedLifeBehavior((int) (400 + 400 * (Math.random())), new SmokeLifeExecutor())
                },
                grid, index, 1.0,
                false,
                true);
    }
}

