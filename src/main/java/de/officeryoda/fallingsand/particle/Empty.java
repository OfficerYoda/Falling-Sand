package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.behavior.Behavior;

public class Empty extends Particle {
    public Empty(Grid grid, int index) {
        super(Colors.BACKGROUND_COLOR, Colors.BACKGROUND_COLOR, true, new Behavior[0], grid, index);
    }
}
