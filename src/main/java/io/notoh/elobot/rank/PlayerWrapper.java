package io.notoh.elobot.rank;

import org.jetbrains.annotations.NotNull;

public class PlayerWrapper implements Comparable<PlayerWrapper> {

    private int kills;
    private int deaths;
    private int wins;
    private int losses;
    private int rating;
    private String name;

    public PlayerWrapper(String name, int kills, int deaths, int wins, int losses, int rating) {
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.losses = losses;
        this.rating = rating;
    }

    public void addKills(int kills) {
        this.kills += kills;
    }

    public void addDeaths(int deaths) {
        this.deaths += deaths;
    }

    public void addWin() {
        wins++;
    }

    public void addLoss() {
        losses++;
    }

    public double getKDA() {
        return (double) kills / (double) deaths;
    }

    public double getWinPct() {
        return (double) wins / (double) (wins+losses);
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getRating() {
        return rating;
    }

    public void playGame(int kills, int deaths, double outcome) {
        int out = outcome > 0.5 ? 12 : -12;

        rating += (0.8*kills - deaths) + out;
    }

    @Override
    public int compareTo(@NotNull PlayerWrapper o) {
        return (o.rating - rating);
    }

    public String getName() {
        return name;
    }
}
