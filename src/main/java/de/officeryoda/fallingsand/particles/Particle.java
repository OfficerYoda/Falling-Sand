package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.particles.behavior.Behavior;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Particle {

    @Getter
    protected final double brushSpawnChance;
    private final Map<Class<? extends Behavior>, Behavior> behaviorLookup = new HashMap<>();
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
    @Getter
    protected boolean fluid;

    protected Particle(Color baseColor, Color color, Behavior[] behaviors, Grid grid, int index, double brushSpawnChance, boolean empty, boolean airy, boolean fluid) {
        this.baseColor = baseColor;
        this.color = color;
        this.behaviors = behaviors;
        Arrays.stream(behaviors).forEach(behavior -> behaviorLookup.put(behavior.getClass(), behavior));
        this.grid = grid;
        this.index = index;
        this.brushSpawnChance = brushSpawnChance;
        this.empty = empty;
        this.airy = airy;
        this.fluid = fluid;
    }

    protected Particle(Color baseColor, Color color, Behavior[] behaviors, Grid grid, int index, double brushSpawnChance, boolean empty, boolean airy) {
        this(baseColor, color, behaviors, grid, index, brushSpawnChance, empty, airy, false);
    }

    protected Particle(Color baseColor, Color color, Behavior[] behaviors, Grid grid, int index, double brushSpawnChance, boolean empty) {
        this(baseColor, color, behaviors, grid, index, brushSpawnChance, empty, false);
    }

    protected Particle(Color baseColor, Color color, Behavior[] behaviors, Grid grid, int index,double brushSpawnChance) {
        this(baseColor, color, behaviors, grid, index, brushSpawnChance, false);
    }

    public void update(int direction) {
        for(Behavior behavior : behaviors) {
            behavior.update(this, grid, direction);
        }
    }

    public <T extends Behavior> T getBehavior(Class<T> behaviorClass) {
        return behaviorClass.cast(behaviorLookup.get(behaviorClass));
    }
}