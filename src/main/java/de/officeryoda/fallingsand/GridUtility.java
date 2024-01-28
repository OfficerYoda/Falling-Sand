package de.officeryoda.fallingsand;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.IntSummaryStatistics;

public class GridUtility {

    public static @NotNull Rectangle calculateBoundingRect(@org.jetbrains.annotations.NotNull Collection<Integer> modifiedIndices, int width) {
        // Calculate bounding box in a single pass for x and y coordinates
        IntSummaryStatistics statsX = modifiedIndices.stream().mapToInt(i -> i % width).summaryStatistics();
        IntSummaryStatistics statsY = modifiedIndices.stream().mapToInt(i -> i / width).summaryStatistics();

        return boundingRectFromStats(statsX, statsY);
    }

    public static @NotNull Rectangle calculateBoundingRect(int @NotNull [] modifiedIndices, int width) {
        // Calculate bounding box in a single pass for x and y coordinates
        IntSummaryStatistics statsX = Arrays.stream(modifiedIndices).map(i -> i % width).summaryStatistics();
        IntSummaryStatistics statsY = Arrays.stream(modifiedIndices).map(i -> i / width).summaryStatistics();

        return boundingRectFromStats(statsX, statsY);
    }

    public static @NotNull Rectangle calculateBoundingRect(Integer[] modifiedIndices, int width) {
        // Calculate bounding box in a single pass for x and y coordinates
        IntSummaryStatistics statsX = Arrays.stream(modifiedIndices).mapToInt(i -> i % width).summaryStatistics();
        IntSummaryStatistics statsY = Arrays.stream(modifiedIndices).mapToInt(i -> i / width).summaryStatistics();

        return boundingRectFromStats(statsX, statsY);
    }

    @Contract("_, _ -> new")
    public static @NotNull Rectangle calculateBoundingRect(@NotNull Rectangle rect1, @NotNull Rectangle rect2) {
        // Calculate the coordinates of the top-left corner
        int x = Math.min(rect1.x, rect2.x);
        int y = Math.min(rect1.y, rect2.y);

        // Calculate the dimensions of the bounding rectangle
        int width = Math.max(rect1.x + rect1.width, rect2.x + rect2.width) - x;
        int height = Math.max(rect1.y + rect1.height, rect2.y + rect2.height) - y;

        // Create and return the bounding rectangle
        return new Rectangle(x, y, width, height);
    }

    @NotNull
    private static Rectangle boundingRectFromStats(IntSummaryStatistics statsX, IntSummaryStatistics statsY) {
        int minX = statsX.getMin();
        int maxX = statsX.getMax();
        int minY = statsY.getMin();
        int maxY = statsY.getMax();

        // Create and return the bounding box
        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
}
