package de.officeryoda.fallingsand;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        SwingUtilities.invokeLater(() -> new GridDrawer());
    }
}