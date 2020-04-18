package io.notoh.elobot;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.notoh.elobot.rank.Player;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

import java.sql.*;
import java.util.*;

public final class Database {

    private Connection conn;
    Map<String, Message> messageCache = new HashMap<>();
    private Map<String, Player> players = new HashMap<>();
    private List<Player> sortedPlayers = new ArrayList<>();
    private Map<String, Player> valPlayers = new HashMap<>();
    private List<Player> valSortedPlayers = new ArrayList<>();

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
                double deviation = Double.parseDouble(ranks.getString(3));
                Player player = new Player(name, rating, deviation);
                players.put(name, player);
                sortedPlayers.add(player);
            }

            rankData.close();

            rankData = conn.createStatement();
            ranks = rankData.executeQuery("SELECT * FROM valratings");
            while(ranks.next()) {
                String name = ranks.getString(1);
                int rating = Integer.parseInt(ranks.getString(2));
                double deviation = Double.parseDouble(ranks.getString(3));
                Player player = new Player(name, rating, deviation);
                valPlayers.put(name, player);
                valSortedPlayers.add(player);
            }
            rankData.close();

            (new Timer()).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        for(String name : players.keySet()) {
                            Player player = players.get(name);
                            PreparedStatement stmt =
                                    conn.prepareStatement("UPDATE ratings SET rating = ?, deviation = ? WHERE handle = ?");
                            stmt.setString(1, String.valueOf(player.getRating()));
                            stmt.setString(2, Util.DECIMAL_FORMAT.format(player.getDeviation()));
                            stmt.setString(3, name);
                            stmt.execute();
                            stmt.close();
                        }
                        for(String name : valPlayers.keySet()) {
                            Player player = valPlayers.get(name);
                            PreparedStatement stmt = conn.prepareStatement("UPDATE valratings SET rating = ?, deviation = ? WHERE handle = ?");
                            stmt.setString(1, String.valueOf(player.getRating()));
                            stmt.setString(2, Util.DECIMAL_FORMAT.format(player.getDeviation()));
                            stmt.setString(3, name);
                            stmt.execute();
                            stmt.close();

                        }
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                }
            }, 1800000, 1800000); //30 min
            (new Timer()).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        for (String name : players.keySet()) {
                            Player player = players.get(name);
                            player.updatePeriodDeviation();
                            PreparedStatement stmt =
                                    conn.prepareStatement("UPDATE ratings SET deviation = ? WHERE handle = ?");
                            stmt.setString(1, Util.DECIMAL_FORMAT.format(player.getDeviation()));
                            stmt.setString(2, name);
                            stmt.execute();
                            stmt.close();
                        }
                        for (String name : players.keySet()) {
                            Player player = valPlayers.get(name);
                            player.updatePeriodDeviation();
                            PreparedStatement stmt =
                                    conn.prepareStatement("UPDATE valratings SET deviation = ? WHERE handle = ?");
                            stmt.setString(1, Util.DECIMAL_FORMAT.format(player.getDeviation()));
                            stmt.setString(2, name);
                            stmt.execute();
                            stmt.close();
                        }
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                }
            }, 172800000, 172800000); //2 days
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
            Player player = new Player(name, 1500, 350.0);
            players.put(name, player);
            sortedPlayers.add(player);
            PreparedStatement stmt =
                    conn.prepareStatement("INSERT INTO ratings VALUES (?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, String.valueOf(player.getRating()));
            stmt.setString(3, Util.DECIMAL_FORMAT.format(player.getDeviation()));
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

    public void updateRating(String name, String rating, String deviation) {
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE ratings SET rating = ?, deviation = ? WHERE handle = ?");
            stmt.setString(1, rating);
            stmt.setString(2, deviation);
            stmt.setString(3, name);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addValPlayer(String name) {
        try {
            Player player = new Player(name, 1500, 350.0);
            valPlayers.put(name, player);
            valSortedPlayers.add(player);
            PreparedStatement stmt =
                    conn.prepareStatement("INSERT INTO valratings VALUES (?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, String.valueOf(player.getRating()));
            stmt.setString(3, Util.DECIMAL_FORMAT.format(player.getDeviation()));
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteValPlayer(String name) {
        try {
            valSortedPlayers.remove(players.get(name));
            valPlayers.remove(name);
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM valratings WHERE handle = ?");
            stmt.setString(1, name);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateValRating(String name, String rating, String deviation) {
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE valratings SET rating = ?, deviation = ? WHERE handle = ?");
            stmt.setString(1, rating);
            stmt.setString(2, deviation);
            stmt.setString(3, name);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeName(String old, String newName) {
        try {
            Player player = players.get(old);
            players.remove(old);
            sortedPlayers.remove(player);
            Player newPlayer = new Player(newName, player.getRating(), player.getDeviation());
            players.put(newName, newPlayer);
            sortedPlayers.add(newPlayer);
            PreparedStatement stmt = conn.prepareStatement("UPDATE ratings SET handle = ? WHERE handle = ?");
            stmt.setString(1, newName);
            stmt.setString(2, old);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeValName(String old, String newName) {
        try {
            Player player = valPlayers.get(old);
            valPlayers.remove(old);
            valSortedPlayers.remove(player);
            Player newPlayer = new Player(newName, player.getRating(), player.getDeviation());
            valPlayers.put(newName, newPlayer);
            valSortedPlayers.add(newPlayer);
            PreparedStatement stmt = conn.prepareStatement("UPDATE valratings SET handle = ? WHERE handle = ?");
            stmt.setString(1, newName);
            stmt.setString(2, old);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Player> getSortedPlayers() {
        Collections.sort(sortedPlayers);
        return sortedPlayers;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public Map<String, Player> getValPlayers() {
        return valPlayers;
    }

    public List<Player> getValSortedPlayers() {
        Collections.sort(valSortedPlayers);
        return valSortedPlayers;
    }
}
