package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.behavior.Behavior;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public abstract class Particle {

    @Getter
    protected Color baseColor;
    @Setter
    @Getter
    protected Color color;
    protected Behavior[] behaviors;
    protected Grid grid;
    @Setter
    @Getter
    protected int index;

    @Getter
    protected boolean empty;
    @Getter
    protected boolean airy;

    protected Particle(Color baseColor, Color color, Behavior[] behaviors, Grid grid, int index, boolean empty, boolean airy) {
        this.baseColor = baseColor;
        this.color = color;
        this.behaviors = behaviors;
        this.grid = grid;
        this.index = index;
        this.empty = empty;
        this.airy = airy;
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
}