package io.notoh.elobot;

import com.zaxxer.hikari.HikariDataSource;
import io.notoh.elobot.rank.Glicko2;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.api.JDA;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Database {

    private Connection conn;
    private final Map<String, PlayerWrapper> players;
    private final List<PlayerWrapper> sortedPlayers;

    @SuppressWarnings("ConstantConditions")
    public Database(JDA bot) {
        players = new ConcurrentHashMap<>();
        sortedPlayers = Collections.synchronizedList(new ArrayList<>());
        try {
            System.out.println("Connecting to DB");
            final HikariDataSource ds = new HikariDataSource();
            ds.setUsername(Util.DB_USER);
            ds.setPassword(Util.DB_PASS);
            ds.setJdbcUrl(Util.DB_URL);
            ds.addDataSourceProperty("autoReconnect", "true");
            ds.addDataSourceProperty("maxReconnects", "10");
            ds.addDataSourceProperty("createDatabaseIfNotExist", "true");
            ds.addDataSourceProperty("useUnicode", "true");
            ds.addDataSourceProperty("characterEncoding", "UTF-8");
            System.out.println("Credentials set");

            System.out.println("Getting connection");
            conn = ds.getConnection();

            System.out.println("Connected to DB");

            Statement rankData = conn.createStatement();
            ResultSet ranks = rankData.executeQuery("SELECT * FROM ratings");

            while(ranks.next()) {
                String name = ranks.getString(1);
                double rating = Double.parseDouble(ranks.getString(2));
                int kills = Integer.parseInt(ranks.getString(3));
                int deaths = Integer.parseInt(ranks.getString(4));
                int wins = Integer.parseInt(ranks.getString(5));
                int losses = Integer.parseInt(ranks.getString(6));
                double rd = Double.parseDouble(ranks.getString(7));
                double volatility = Double.parseDouble(ranks.getString(8));
                PlayerWrapper player = new PlayerWrapper(name, kills, deaths, wins, losses, rating, rd, volatility);
                players.put(name, player);
                sortedPlayers.add(player);
            }

            rankData.close();
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> sortedPlayers.forEach(PlayerWrapper::idleDeviation), 7, 7, TimeUnit.DAYS);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            System.out.println("Database keep alive.");
            try {
                Statement rankxd = conn.createStatement();
                rankxd.executeQuery("SELECT * FROM ratings");
                rankxd.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
                bot.getTextChannelById(Util.CHANNEL_ID).sendMessage("<@129712117837332481> sql exception debug flag " +
                        "raised").queue();
            }
            bot.getTextChannelById(Util.CHANNEL_ID).sendMessage("Keep Alive success!").queue();
        }, 1, 1, TimeUnit.DAYS);

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
            PlayerWrapper player = new PlayerWrapper(name, 0,0,0,0, Glicko2.newPlayerRating, Glicko2.newPlayerDeviation, Glicko2.newPlayerVolatility);
            players.put(name, player);
            sortedPlayers.add(player);
            PreparedStatement stmt =
                    conn.prepareStatement("INSERT INTO ratings VALUES (?,?,?,?,?,?,?,?)");
            stmt.setString(1, name);
            stmt.setString(2, String.valueOf(player.getRating()));
            stmt.setString(3, String.valueOf(player.getKills()));
            stmt.setString(4, String.valueOf(player.getDeaths()));
            stmt.setString(5, String.valueOf(player.getWins()));
            stmt.setString(6, String.valueOf(player.getLosses()));
            stmt.setString(7, String.valueOf(player.getDeviation()));
            stmt.setString(8, String.valueOf(player.getVolatility()));
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
            PreparedStatement stmt = conn.prepareStatement("UPDATE ratings SET rating " + "=" +
                    " ?, kills = ?, deaths = ?, wins = ?, losses = ?, rd = ?, volatility = ?" +
                    " WHERE handle = ?");
            stmt.setString(1, String.valueOf(playerWrapper.getRating()));
            stmt.setString(2, String.valueOf(playerWrapper.getKills()));
            stmt.setString(3, String.valueOf(playerWrapper.getDeaths()));
            stmt.setString(4, String.valueOf(playerWrapper.getWins()));
            stmt.setString(5, String.valueOf(playerWrapper.getLosses()));
            stmt.setString(6, playerWrapper.getName());
            stmt.setString(7, String.valueOf(playerWrapper.getDeviation()));
            stmt.setString(8, String.valueOf(playerWrapper.getVolatility()));
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
            PlayerWrapper newPlayer = new PlayerWrapper(newName, player.getKills(), player.getDeaths(), player.getWins(), player.getLosses(), player.getRating(), player.getDeviation(), player.getVolatility());
            players.put(newName, newPlayer);
            sortedPlayers.add(newPlayer);
            PreparedStatement stmt = conn.prepareStatement("UPDATE ratings SET handle = ? WHERE handle = ?");
            stmt.setString(1, newName);
            stmt.setString(2, old);
            stmt.execute();
            stmt.close();
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
