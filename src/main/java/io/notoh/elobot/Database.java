package io.notoh.elobot;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Database {

    private Connection connection;
    private final Map<String, PlayerWrapper> players;
    private final List<PlayerWrapper> sortedPlayers;
    private final JDA bot;

    @SuppressWarnings("ConstantConditions")
    public Database(JDA bot) {
        this.bot = bot;
        players = new ConcurrentHashMap<>();
        sortedPlayers = Collections.synchronizedList(new ArrayList<>());
        try {
            System.out.println("Connecting to DB");
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(Util.DB_USER);
            dataSource.setPassword(Util.DB_PASS);
            dataSource.setURL(Util.DB_URL);

            System.out.println("Credentials set");

            dataSource.setCreateDatabaseIfNotExist(true);
            dataSource.setAutoReconnect(true);
            System.out.println("Getting connection");
            connection = dataSource.getConnection();

            System.out.println("Connected to DB");

            synchronized (this) {
                Statement rankData = connection.createStatement();
                ResultSet ranks = rankData.executeQuery("SELECT * FROM ratings");

                while(ranks.next()) {
                    String name = ranks.getString(1);
                    int rating = ranks.getInt(2);
                    int kills = ranks.getInt(3);
                    int deaths = ranks.getInt(4);
                    int wins = ranks.getInt(5);
                    int losses = ranks.getInt(6);
                    int placementRating = ranks.getInt(7);
                    PlayerWrapper player = new PlayerWrapper(name, kills, deaths, wins, losses, rating, placementRating);
                    players.put(name, player);
                    sortedPlayers.add(player);
                }

                rankData.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
            error(e);
        }

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try(Statement rankxd = connection.createStatement()) {
                rankxd.executeQuery("SELECT * FROM ratings");
                bot.getTextChannelById(Util.CHANNEL_ID).sendMessage("Keep Alive success!").queue();
            } catch (SQLException e) {
                e.printStackTrace();
                error(e);
            }
        }, 1, 1, TimeUnit.HOURS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                error(e);
            }
        }));
    }

    //TODO make these more consistent in their function i.e. only handle sql calls and the commands handle everything
    // else as opposed to this weird mixed shit

    public synchronized void addPlayer(String name) {
        try(PreparedStatement stmt = connection.prepareStatement("INSERT INTO ratings VALUES (?,?,?,?,?,?,?)")) {
            PlayerWrapper player = new PlayerWrapper(name, 0,0,0,0, 1500, 1500);
            players.put(name, player);
            sortedPlayers.add(player);
            stmt.setString(1, name);
            stmt.setInt(2, player.getRating());
            stmt.setInt(3, player.getKills());
            stmt.setInt(4, player.getDeaths());
            stmt.setInt(5, player.getWins());
            stmt.setInt(6, player.getLosses());
            stmt.setInt(7, player.getRawRating());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            error(e);
        }
    }

    public synchronized void deletePlayer(String name) {
        try(PreparedStatement stmt = connection.prepareStatement("DELETE FROM ratings WHERE handle = ?")) {
            sortedPlayers.remove(players.get(name));
            players.remove(name);
            stmt.setString(1, name);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            error(e);
        }
    }

    public synchronized void updateRating(PlayerWrapper playerWrapper) {
        try(PreparedStatement stmt = connection.prepareStatement("UPDATE ratings SET rating =" +
                " ?, kills = ?, deaths = ?, wins = ?, losses = ?, placementRating = ? WHERE handle = ?")) {
            stmt.setInt(1, playerWrapper.getRating());
            stmt.setInt(2, playerWrapper.getKills());
            stmt.setInt(3, playerWrapper.getDeaths());
            stmt.setInt(4, playerWrapper.getWins());
            stmt.setInt(5, playerWrapper.getLosses());
            stmt.setInt(6, playerWrapper.getRawRating());
            stmt.setString(7, playerWrapper.getName());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            error(e);
        }
    }

    public synchronized void changeName(String old, String newName) {
        try(PreparedStatement stmt = connection.prepareStatement("UPDATE ratings SET handle = ? WHERE handle = ?")) {
            PlayerWrapper player = players.get(old);
            players.remove(old);
            sortedPlayers.remove(player);
            PlayerWrapper newPlayer = new PlayerWrapper(newName, player.getKills(), player.getDeaths(),
                    player.getWins(), player.getLosses(), player.getRating(), player.getRawRating());
            players.put(newName, newPlayer);
            sortedPlayers.add(newPlayer);
            stmt.setString(1, newName);
            stmt.setString(2, old);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            error(e);
        }
    }

    private void error(Exception e) {
        e.printStackTrace();
        User user = bot.getUserByTag("Notoh#9288");
        if(user != null) {
            user.openPrivateChannel().flatMap((channel) -> channel.sendMessage(e.getMessage())).queue();
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
