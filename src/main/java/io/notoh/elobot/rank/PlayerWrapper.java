package io.notoh.elobot.rank;

public class PlayerWrapper implements Comparable<PlayerWrapper> {

    private int kills;
    private int deaths;
    private int wins;
    private int losses;
    private final String name;
    private int rating;

    public PlayerWrapper(String name, int kills, int deaths, int wins, int losses, int rating) {
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.losses = losses;
        this.rating = rating;
    }

    public void carry() {
        rating += 5;
    }

    public void addKills(int kills) {
        this.kills += kills;
    }

    public void addDeaths(int deaths) {
        this.deaths += deaths;
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

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void punish() {
        rating -= 20;
    }

    public void pardon() {
        rating += 20;
    }

    public void playGame(boolean won, int kills, int deaths) {
        if(won) {
            wins++;
        } else {
            losses++;
        }
        int cozyElo = (kills-deaths) + (won ? 12 : -12);

        if(wins + losses < 100) {
            rating += cozyElo;
            return;
        }

        rating += ((int) Math.round((rating - 1500 + cozyElo)*(double)(100/wins+losses)));
    }

    public void invertGame(boolean won, int kills, int deaths) {
        int cozyElo = (kills-deaths) + (won ? 12 : -12);

        if(wins + losses < 100) {
            rating -= cozyElo;
        }

        rating -= ((int) Math.round((rating - 1500 + cozyElo)*(double)(100/wins+losses)));

        if(won) {
            wins--;
        } else {
            losses--;
        }
    }

    @Override
    public int compareTo(PlayerWrapper o) {
        return o.rating - rating;
    }

    public String getName() {
        return name;
    }
}
