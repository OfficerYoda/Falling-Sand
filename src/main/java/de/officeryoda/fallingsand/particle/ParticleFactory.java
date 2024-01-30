package de.officeryoda.fallingsand.particle;

import de.officeryoda.fallingsand.grid.Grid;

import java.util.HashMap;
import java.util.Map;

public class ParticleFactory {

    private static final Map<Integer, Class<? extends Particle>> particleMap = new HashMap<>();
    private static int selectedParticle = 1; // default: Sand

    static {
        particleMap.put(-1, OutOfBoundsParticle.class);
        particleMap.put(0, EmptyParticle.class);
        particleMap.put(1, SandParticle.class);
        particleMap.put(2, WoodParticle.class);
        particleMap.put(3, SmokeParticle.class);
    }

    public static Particle createParticle(Grid grid, int index) {
        Class<? extends Particle> particleClass = particleMap.get(selectedParticle);
        if (particleClass != null) {
            try {
                return particleClass.getDeclaredConstructor(Grid.class, int.class).newInstance(grid, index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("Invalid particle ID: " + selectedParticle);
    }

    public static Particle createDummyParticle() {
        return createParticle(null, -1);
    }

    public static void nextParticle() {
        selectedParticle++;
        selectedParticle %= particleMap.keySet().size();
    }
}