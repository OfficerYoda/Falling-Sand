package de.officeryoda.fallingsand;

import de.officeryoda.fallingsand.grid.Grid;
import de.officeryoda.fallingsand.grid.GridDrawer;

public class Main {
    public static void main(String[] args) {
        System.out.println("Falling Sand!");

        Grid grid = new Grid(1440, 720 - GridDrawer.TITLE_BAR_HEIGHT, 2);
    }
}