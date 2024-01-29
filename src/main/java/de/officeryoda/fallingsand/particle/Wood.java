package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.Colors;
import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.behavior.Behavior;

public class Wood extends Particle {

    public Wood(Grid grid, int index) {
        super(Colors.WOOD_COLOR, Colors.varyColor(Colors.WOOD_COLOR), false, new Behavior[0], grid, index);
    }
}
