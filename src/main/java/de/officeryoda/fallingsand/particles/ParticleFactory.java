package de.officeryoda.fallingsand.particles;

import de.officeryoda.fallingsand.grid.Grid;

import java.util.ArrayList;
import java.util.List;

public class ParticleFactory {

    private static final List<Class<? extends Particle>> particleMap = new ArrayList<>();
    private static int selectedParticle = 1; // default: Sand

    static {
        particleMap.add(EmptyParticle.class);
        particleMap.add(SandParticle.class);
        particleMap.add(WoodParticle.class);
        particleMap.add(FireParticle.class);
    }

    public static Particle createParticle(Grid grid, int index) {
        Class<? extends Particle> particleClass = particleMap.get(selectedParticle);
        if (particleClass != null) {
            try {
                return particleClass.getDeclaredConstructor(Grid.class, int.class).newInstance(grid, index);
            } catch (Exception e) {
                throw new ClassCastException("Class doesn't have required constructor (Grid, int)\n" + e);
            }
        }
        throw new IllegalArgumentException("Invalid particle ID: " + selectedParticle);
    }

    public static Particle createDummyParticle() {
        return createParticle(null, -1);
    }

    public static void nextParticle() {
        selectedParticle++;
        selectedParticle %= particleMap.size();
    }
}