package de.officeryoda.fallingsand.particle;

import java.util.HashMap;
import java.util.Map;

public class ParticleFactory {

    private static final Map<Integer, Class<? extends Particle>> particleMap = new HashMap<>();
    private static int selectedParticle = 1; // default: Sand

    static {
        particleMap.put(0, Empty.class);
        particleMap.put(1, Sand.class);
        particleMap.put(2, Wood.class);
    }

    public static Particle createParticle() {
        return createParticle(selectedParticle);
    }

    public static Particle createParticle(int id) {
        Class<? extends Particle> particleClass = particleMap.get(id);
        if (particleClass != null) {
            try {
                return particleClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("Invalid particle ID: " + id);
    }

    public static void nextParticle() {
        selectedParticle++;
        selectedParticle %= particleMap.keySet().size();
    }
}