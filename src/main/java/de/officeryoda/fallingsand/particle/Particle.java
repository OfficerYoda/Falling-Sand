package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.Colors;

import java.awt.*;

public abstract class Particle {

    protected Color color;
    protected boolean isEmpty;
    protected double velocity;
    protected double maxVelocity;
    protected double acceleration;

    protected Particle(Color color, boolean isEmpty, double maxVelocity, double acceleration) {
        this.color = Colors.varyColor(color);
        this.isEmpty = isEmpty;
        this.velocity = 0;
        this.maxVelocity = maxVelocity;
        this.acceleration = acceleration;
    }

    public void updateVelocity() {
        velocity += acceleration;

        velocity = Math.clamp(velocity, -maxVelocity, maxVelocity);
    }

    public void resetVelocity() {
        this.velocity = 0;
    }

    public int getUpdateCount() {
        double abs = Math.abs(this.velocity);
        double floored = Math.floor(abs);
        double mod = abs - floored;
        // Treat a remainder (e.g. 0.5) as a random chance to update
        return (int) (floored + (Math.random() < mod ? 1 : 0));
    }

    public Color getColor() {
        return this.color;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }
}