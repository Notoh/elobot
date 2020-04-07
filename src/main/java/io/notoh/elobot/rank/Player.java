package io.notoh.elobot.rank;

import org.jetbrains.annotations.NotNull;

public class Player implements Comparable<Player> {
    private double deviation;
    private int rating;
    private String name;

    public Player(String name, int rating, double deviation) {
        this.name = name;
        this.rating = rating;
        this.deviation = deviation;
    }

    public String getName() {
        return name;
    }

    public void playGame(int opposedRating, double outcome,
                         double performance) {
        double[] update = Calculator.newRating(rating,deviation,opposedRating, outcome,performance);
        rating = (int) update[0];
        deviation = update[1];
    }

    public int getRating() {
        return  rating;
    }

    public double getDeviation() {
        return deviation;
    }

    public void updatePeriodDeviation() {
        deviation = Calculator.periodDeviation(deviation);
    }

    @Override
    public int compareTo(@NotNull Player o) {
        return Integer.compare(o.rating, rating);
    }
}
