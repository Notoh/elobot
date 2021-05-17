package io.notoh.elobot.rank;

import static io.notoh.elobot.rank.Glicko2.*;

public class PlayerWrapper implements Comparable<PlayerWrapper> {

    private int kills;
    private int deaths;
    private int wins;
    private int losses;
    private final String name;
    private double g1Rating;
    private double g1Rd;
    private double volatility;
    private int tempKills;
    private int tempDeaths;

    public PlayerWrapper(String name, int kills, int deaths, int wins, int losses, double rating, double deviation, double volatility) {
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.losses = losses;
        this.g1Rating = rating;
        this.g1Rd = deviation;
        this.volatility = volatility;
    }

    public void carry() {
        g1Rating += 5;
    }

    public void addKillsAndSetTemp(int kills) {
        this.kills += kills;
        this.tempKills = kills;
    }

    public void addDeathsAndSetTemp(int deaths) {
        this.deaths = deaths;
        this.tempDeaths = deaths;
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

    public double getRating() {
        return g1Rating;
    }

    public void setRating(double rating) {
        this.g1Rating = rating;
    }

    public double getDeviation() {
        return g1Rd;
    }

    public void setDeviation(double deviation) {
        this.g1Rd = deviation;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public double getVolatility() {
        return volatility;
    }

    public void punish() {
        g1Rating -= 20;
    }

    public void pardon() {
        g1Rating += 20;
    }

    public boolean isProvisional() {
        return g1Rd > 100;
    }

    public void playGame(double outcome, double[] glicko1) {
        double perf = Math.min(15, ((0.8 * tempKills) - tempDeaths) * 0.5);
        double[] glicko1Result = glicko2ToGlicko1(calculateNewRating(glicko1ToGlicko2(glicko1[0], g1Rd, volatility),
                glicko1ToGlicko2(new double[]{glicko1[2], glicko1[3], Glicko2.newPlayerVolatility}), outcome));
        this.setRating(g1Rating + (glicko1Result[0] - glicko1[0]) + perf);
        this.setDeviation(glicko1Result[1]);
        this.setVolatility(glicko1Result[2]);
    }

    public double[] getGlicko() {
        return new double[]{g1Rating, g1Rd, volatility};
    }

    public void idleDeviation() {
        g1Rd = glicko2ToGlicko1(new double[]{0, updateDeviation(g1Rd, volatility), 0})[1];
    }

    @Override
    public int compareTo(PlayerWrapper o) {
        if(isProvisional() && o.isProvisional()) {
            return 0;
        } else if(isProvisional()) {
            return Integer.MAX_VALUE;
        } else if(o.isProvisional()) {
            return Integer.MIN_VALUE;
        }
        return (int) (o.g1Rating - g1Rating);
    }

    public String getName() {
        return name;
    }
}
