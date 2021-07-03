package io.notoh.elobot;

import com.mysql.cj.jdbc.MysqlDataSource;
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
            }
        } catch(SQLException e) {
            e.printStackTrace();
            error(e);
        }

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            System.out.println("Database keep alive.");
            try(Statement rankxd = connection.createStatement()) {
                rankxd.executeQuery("SELECT * FROM ratings");
                bot.getTextChannelById(Util.CHANNEL_ID).sendMessage("Keep Alive success!").queue();
            } catch (SQLException e) {
                e.printStackTrace();
                error(e);
            }
        }, 1, 1, TimeUnit.DAYS);

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
        try(PreparedStatement stmt = connection.prepareStatement("INSERT INTO ratings VALUES (?,?,?,?,?,?)")) {
            PlayerWrapper player = new PlayerWrapper(name, 0,0,0,0, 1500);
            players.put(name, player);
            sortedPlayers.add(player);
            stmt.setString(1, name);
            stmt.setString(2, String.valueOf(player.getRating()));
            stmt.setString(3, String.valueOf(player.getKills()));
            stmt.setString(4, String.valueOf(player.getDeaths()));
            stmt.setString(5, String.valueOf(player.getWins()));
            stmt.setString(6, String.valueOf(player.getLosses()));
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
                " ?, kills = ?, deaths = ?, wins = ?, losses = ? WHERE handle = ?")) {
            stmt.setString(1, String.valueOf(playerWrapper.getRating()));
            stmt.setString(2, String.valueOf(playerWrapper.getKills()));
            stmt.setString(3, String.valueOf(playerWrapper.getDeaths()));
            stmt.setString(4, String.valueOf(playerWrapper.getWins()));
            stmt.setString(5, String.valueOf(playerWrapper.getLosses()));
            stmt.setString(6, playerWrapper.getName());
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
            PlayerWrapper newPlayer = new PlayerWrapper(newName, player.getKills(), player.getDeaths(), player.getWins(), player.getLosses(), player.getRating());
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

    @SuppressWarnings("ConstantConditions")
    private void error(Exception e) {
        bot.getUserById(129712117837332481L).openPrivateChannel().queue((channel) -> channel.sendMessage(e.getMessage()).queue());
    }

    public List<PlayerWrapper> getSortedPlayers() {
        Collections.sort(sortedPlayers);
        return sortedPlayers;
    }

    public Map<String, PlayerWrapper> getPlayers() {
        return players;
    }
}
