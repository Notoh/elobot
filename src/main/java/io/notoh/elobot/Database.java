package io.notoh.elobot;

import com.mysql.cj.jdbc.MysqlDataSource;
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
                int rating = Integer.parseInt(ranks.getString(2));
                int kills = Integer.parseInt(ranks.getString(3));
                int deaths = Integer.parseInt(ranks.getString(4));
                int wins = Integer.parseInt(ranks.getString(5));
                int losses = Integer.parseInt(ranks.getString(6));
                PlayerWrapper player = new PlayerWrapper(name, kills, deaths, wins, losses, rating);
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
            PlayerWrapper player = new PlayerWrapper(name, 0,0,0,0,1500);
            players.put(name, player);
            sortedPlayers.add(player);
            PreparedStatement stmt =
                    conn.prepareStatement("INSERT INTO ratings VALUES (?,?,?,?,?,?)");
            stmt.setString(1, name);
            stmt.setString(2, String.valueOf(player.getRating()));
            stmt.setString(3, String.valueOf(player.getKills()));
            stmt.setString(4, String.valueOf(player.getDeaths()));
            stmt.setString(5, String.valueOf(player.getWins()));
            stmt.setString(6, String.valueOf(player.getLosses()));
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
            int rating = playerWrapper.getRating();
            int kills = playerWrapper.getKills();
            int deaths = playerWrapper.getDeaths();
            int wins = playerWrapper.getWins();
            int losses = playerWrapper.getLosses();
            PreparedStatement stmt = conn.prepareStatement("UPDATE ratings SET rating " + "=" +
                    " ?, kills = ?, deaths = ?, wins = ?, losses = ?" +
                    " WHERE handle = ?");
            stmt.setString(1, String.valueOf(rating));
            stmt.setString(2, String.valueOf(kills));
            stmt.setString(3, String.valueOf(deaths));
            stmt.setString(4, String.valueOf(wins));
            stmt.setString(5, String.valueOf(losses));
            stmt.setString(6, playerWrapper.getName());
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
            PlayerWrapper newPlayer = new PlayerWrapper(newName, player.getKills(), player.getDeaths(),
                    player.getWins(), player.getLosses(), player.getRating());
            players.put(newName, newPlayer);
            sortedPlayers.add(newPlayer);
            PreparedStatement stmt = conn.prepareStatement("UPDATE ratings SET handle = ? WHERE handle = ?");
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
