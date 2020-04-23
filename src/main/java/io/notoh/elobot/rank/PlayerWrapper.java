package io.notoh.elobot.rank;

import de.gesundkrank.jskills.Player;
import de.gesundkrank.jskills.Rating;
import org.jetbrains.annotations.NotNull;

public class PlayerWrapper implements Comparable<PlayerWrapper> {

    private final Player<String> player;
    private Rating rating;
    private int kills;
    private int deaths;
    private int wins;
    private int losses;

    public PlayerWrapper(String name, Rating rating, int kills, int deaths, int wins, int losses) {
        this.player = new Player<>(name);
        this.rating = rating;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.losses = losses;
    }

    public Player<String> getPlayer() {
        return player;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
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

    @Override
    public int compareTo(@NotNull PlayerWrapper o) {
        return (int) (1000*o.rating.getConservativeRating() - 1000*rating.getConservativeRating());
    }
}
