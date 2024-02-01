package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.behavior.Behavior;
import de.officeryoda.fallingsand.particles.behavior.MovesBehavior;

public class SandParticle extends Particle {

    public SandParticle(Grid grid, int index) {
        super(Colors.SAND_COLOR, Colors.varyColor(Colors.SAND_COLOR),
                new Behavior[]{new MovesBehavior(0.4, 16)},
                grid, index, 0.5);
    }
}
