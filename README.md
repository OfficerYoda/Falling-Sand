# Falling Sand Game

This project is a Java-based implementation of the Falling Sand game.

## Description

The Falling Sand game is a particle simulation game where different particles interact with each other in interesting ways. This project includes various particle behaviors and interactions, such as flammability and movement variation.

## Implementation

The game is implemented in Java using the Swing library for the graphical user interface. The game consists of a grid of cells, where each cell represents a particle. The particles interact with each other based on their behavior and properties.

### Controls

- Left-click: Place particles.
- Right-click: Clear all particles.
- Middle-click: Change the particle type.
- Mouse wheel: Change the particle spawn radius.
- P: Pause the simulation.

### Particles and Behaviors

- Sand: Falls down and piles up on the ground.
- Wood: Burns when in contact with fire.
- Fire: Burns wood and spreads out; can spawn smoke particles.
- Smoke: Rises up and spreads out; can set other particles on fire.
- Water: Flows down and spreads out on the ground and extinguishes burning particles.

### Some pictures
![Particle-Sand.png](src%2Fmain%2Fresources%2FParticle-Sand.png)
![Particle-Fire.png](src%2Fmain%2Fresources%2FParticle-Fire.png)
![Particle-Water.png](src%2Fmain%2Fresources%2FParticle-Water.png)