package io.notoh.elobot;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.gesundkrank.jskills.Rating;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

import java.sql.*;
import java.util.*;

public final class Database {

    private Connection conn;
    Map<String, Message> messageCache = new HashMap<>();
    private Map<String, PlayerWrapper> players = new HashMap<>();
    private List<PlayerWrapper> sortedPlayers = new ArrayList<>();

    public Database(JDA bot) {
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(Util.DB_USER);
            dataSource.setPassword(Util.DB_PASS);
            dataSource.setURL(Util.DB_URL);

            dataSource.setCreateDatabaseIfNotExist(true);
            dataSource.setAutoReconnect(true);
            conn = dataSource.getConnection();

            System.out.println("Connected to DB.");

            Statement rankData = conn.createStatement();
            ResultSet ranks = rankData.executeQuery("SELECT * FROM ratings");

            while(ranks.next()) {

                String name = ranks.getString(1);
                double mean = Double.parseDouble(ranks.getString(2));
                double deviation = Double.parseDouble(ranks.getString(3));
                double conservative = Double.parseDouble(ranks.getString(4));
                int kills = Integer.parseInt(ranks.getString(5));
                int deaths = Integer.parseInt(ranks.getString(6));
                int wins = Integer.parseInt(ranks.getString(7));
                int losses = Integer.parseInt(ranks.getString(8));
                Rating rating = new Rating(mean, deviation);
                PlayerWrapper player = new PlayerWrapper(name, rating, kills,deaths,wins,losses);
                players.put(name, player);
                sortedPlayers.add(player);
            }

            rankData.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    //TODO make these more consistent in their function i.e. only handle sql calls and the commands handle everything
    // else as opposed to this weird mixed shit

    public void addPlayer(String name) {
        try {
            PlayerWrapper player = new PlayerWrapper(name, new Rating(25, 25.0/3.0),0,0,0,0);
            players.put(name, player);
            sortedPlayers.add(player);
            PreparedStatement stmt =
                    conn.prepareStatement("INSERT INTO ratings VALUES (?, ?, ?, ?,?,?,?,?)");
            stmt.setString(1, name);
            stmt.setString(2, Util.DECIMAL_FORMAT.format(player.getRating().getMean()));
            stmt.setString(3, Util.DECIMAL_FORMAT.format(player.getRating().getStandardDeviation()));
            stmt.setString(4, Util.DECIMAL_FORMAT.format(player.getRating().getConservativeRating()));
            stmt.setString(5, String.valueOf(player.getKills()));
            stmt.setString(6, String.valueOf(player.getDeaths()));
            stmt.setString(7, String.valueOf(player.getWins()));
            stmt.setString(8, String.valueOf(player.getLosses()));
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayer(String name) {
        try {
            sortedPlayers.remove(players.get(name));
            players.remove(name);
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM ratings WHERE handle = ?");
            stmt.setString(1, name);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRating(PlayerWrapper playerWrapper) {
        try {
            double mean = playerWrapper.getRating().getMean();
            double deviation = playerWrapper.getRating().getStandardDeviation();
            double conservative = playerWrapper.getRating().getConservativeRating();
            int kills = playerWrapper.getKills();
            int deaths = playerWrapper.getDeaths();
            int wins = playerWrapper.getWins();
            int losses = playerWrapper.getLosses();
            PreparedStatement stmt = conn.prepareStatement("UPDATE ratings SET mean = ?, deviation = ?, conservative " + "= ?, kills = ?, deaths = ?, wins = ?, losses = ?" +
                    " WHERE handle = ?");
            stmt.setString(1, Util.DECIMAL_FORMAT.format(mean));
            stmt.setString(2, Util.DECIMAL_FORMAT.format(deviation));
            stmt.setString(3, Util.DECIMAL_FORMAT.format(conservative));
            stmt.setString(4, String.valueOf(kills));
            stmt.setString(5, String.valueOf(deaths));
            stmt.setString(6, String.valueOf(wins));
            stmt.setString(7, String.valueOf(losses));
            stmt.setString(8, playerWrapper.getPlayer().getId());
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeName(String old, String newName) {
        try {
            PlayerWrapper player = players.get(old);
            players.remove(old);
            sortedPlayers.remove(player);
            PlayerWrapper newPlayer = new PlayerWrapper(newName, player.getRating(), player.getKills(), player.getDeaths(), player.getWins(), player.getLosses());
            players.put(newName, newPlayer);
            sortedPlayers.add(newPlayer);
            PreparedStatement stmt = conn.prepareStatement("UPDATE RATINGS SET handle = ? WHERE handle = ?");
            stmt.setString(1, newName);
            stmt.setString(2, old);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PlayerWrapper> getSortedPlayers() {
        Collections.sort(sortedPlayers);
        return sortedPlayers;
    }

    public Map<String, PlayerWrapper> getPlayers() {
        return players;
    }
}
