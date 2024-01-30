package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.behavior.Behavior;

import java.awt.*;

public class OutOfBoundsParticle extends Particle{

    public OutOfBoundsParticle() {
        super(Color.pink, Color.pink, false, new Behavior[]{}, null, -1);
    }
}
