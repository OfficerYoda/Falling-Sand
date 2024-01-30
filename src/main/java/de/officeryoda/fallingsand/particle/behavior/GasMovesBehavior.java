package de.officeryoda.fallingsand.particle.behavior;

import de.officeryoda.fallingsand.particle.Particle;

public class GasMovesBehavior extends MovesBehavior {

    public GasMovesBehavior(double acceleration, double maxSpeed) {
        super(acceleration, maxSpeed);
    }

    @Override
    protected boolean canPassThrough(Particle particle) {
        return particle.isEmpty();
    }
}
