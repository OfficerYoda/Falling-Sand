package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particle.behavior.Behavior;

import java.awt.*;

public abstract class Particle {

    protected Color baseColor;
    protected Color color;
    protected Behavior[] behaviors;
    protected Grid grid;
    protected int index;

    protected boolean isEmpty;
    protected boolean isAiry;

    protected Particle(Color baseColor, Color color, Behavior[] behaviors, Grid grid, int index, boolean isEmpty, boolean isAiry) {
        this.baseColor = baseColor;
        this.color = color;
        this.behaviors = behaviors;
        this.grid = grid;
        this.index = index;
        this.isEmpty = isEmpty;
        this.isAiry = isAiry;
    }

    protected Particle(Color baseColor, Color color, Behavior[] behaviors, Grid grid, int index, boolean isEmpty) {
        this(baseColor, color, behaviors, grid, index, isEmpty, false);
    }

    protected Particle(Color baseColor, Color color, Behavior[] behaviors, Grid grid, int index) {
        this(baseColor, color, behaviors, grid, index, false, false);
    }

    public void update(int direction) {
        for(Behavior behavior : behaviors) {
            behavior.update(this, grid, direction);
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
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

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public boolean isAiry() {
        return this.isAiry;
    }
}