package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.behavior.Behavior;

import java.awt.*;

public abstract class Particle {

    protected Color baseColor;
    protected Color color;
    protected boolean isEmpty;
    protected Behavior[] behaviors;
    protected Grid grid;
    protected int index;

    protected Particle(Color baseColor, Color color, boolean isEmpty, Behavior[] behaviors, Grid grid, int index) {
        this.baseColor = baseColor;
        this.color = color;
        this.isEmpty = isEmpty;
        this.behaviors = behaviors;
        this.grid = grid;
        this.index = index;
    }

    public void update() {
        for(Behavior behavior : behaviors) {
            behavior.update(this, grid);
        }
    }

    public Color getColor() {
        return this.color;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public Color getBaseColor() {
        return this.baseColor;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}