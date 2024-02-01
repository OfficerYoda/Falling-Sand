package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.behavior.Behavior;
import de.officeryoda.fallingsand.particles.behavior.FlammableBehavior;

public class FireParticle extends Particle {

    protected FireParticle(Grid grid, int index) {
        super(Colors.FIRE_COLOR, Colors.varyColor(Colors.FIRE_COLOR),
                new Behavior[]{new FlammableBehavior((int) (10 + 100 * Math.random()), true, 1.0)},
                grid, index, 0.5);
    }
}
