package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.behavior.Behavior;
import de.officeryoda.fallingsand.particle.behavior.FallBehavior;

public class SmokeParticle extends Particle {

    public SmokeParticle(Grid grid, int index) {
        super(Colors.SMOKE_COLOR, Colors.varyColor(Colors.SMOKE_COLOR, -0.05, 0.05, -0.05), false, new Behavior[]{new FallBehavior(-0.4, 8)}, grid, index);
    }
}
