package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.behavior.Behavior;

public class WoodParticle extends Particle {

    public WoodParticle(Grid grid, int index) {
        super(Colors.WOOD_COLOR, Colors.varyColor(Colors.WOOD_COLOR),
                false, new Behavior[]{},
                grid, index);
    }
}
