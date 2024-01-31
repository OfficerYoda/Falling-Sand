package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.particles.behavior.Behavior;

import java.awt.*;

public class OutOfBoundsParticle extends Particle{

    public OutOfBoundsParticle() {
        super(Color.pink, Color.pink,
                new Behavior[]{},
                null, -1);
    }
}
