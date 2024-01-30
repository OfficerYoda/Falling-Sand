package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.behavior.Behavior;
import de.officeryoda.fallingsand.particle.behavior.GasMovesBehavior;

public class SmokeParticle extends Particle {

    public SmokeParticle(Grid grid, int index) {
        super(Colors.SMOKE_COLOR, Colors.varyColor(Colors.SMOKE_COLOR, -0.05, 0.05, -0.05),
                new Behavior[]{new GasMovesBehavior(-0.05, 0.25)},
                grid, index,
                false, true);
    }
}
