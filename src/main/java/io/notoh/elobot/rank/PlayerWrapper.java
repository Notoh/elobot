package io.notoh.elobot.rank;

public class PlayerWrapper implements Comparable<PlayerWrapper> {

    private int kills;
    private int deaths;
    private int wins;
    private int losses;
    private final String name;
    private int rating;
    private int rawRating;

    public PlayerWrapper(String name, int kills, int deaths, int wins, int losses, int rating, int rawRating) {
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.losses = losses;
        this.rating = rating;
        this.rawRating = rawRating;
    }

    public void carry() {
        rating += 5;
        rawRating += 5;
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
        if(wins + losses <= 100) {
            this.rawRating = rating;
        }
    }

    public void punish() {
        rating -= 20;
        rawRating -= 20;
    }

    public void pardon() {
        rating += 20;
        rawRating += 20;
    }

    public void playGame(boolean won, int kills, int deaths) {
        if(won) {
            wins++;
        } else {
            losses++;
        }

        this.kills += kills;
        this.deaths += deaths;

        int cozyElo = (kills-deaths) + (won ? 12 : -12);

        if(wins + losses <= 100) {
            rating += cozyElo;
            rawRating += cozyElo;
            return;
        }

        this.rating = 1500 + (rawRating + cozyElo) * (100 / wins + losses);

        rawRating += cozyElo;
    }

    public void invertGame(boolean won, int kills, int deaths) {
        if(won) {
            wins--;
        } else {
            losses--;
        }
        int cozyElo = (kills-deaths) + (won ? 12 : -12);
        rating -= cozyElo;
        rawRating -= cozyElo;
    }
    @Override
    public int compareTo(PlayerWrapper o) {
        return o.rating - rating;
    }

    public String getName() {
        return name;
    }

    public int getRawRating() {
        return rawRating;
    }
}
