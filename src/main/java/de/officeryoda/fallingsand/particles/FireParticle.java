package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.behavior.Behavior;
import de.officeryoda.fallingsand.particles.behavior.FlammableBehavior;

public class FireParticle extends Particle {

    protected FireParticle(Grid grid, int index) {
        super(Colors.FIRE_COLOR, Colors.varyColor(Colors.FIRE_COLOR),
                new Behavior[]{new FlammableBehavior((int) (75 + 50 * Math.random()))},
                grid, index,
                false,true);
    }
}
