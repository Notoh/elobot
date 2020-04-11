package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.Player;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

public class Rank extends Command {

    private Database database;

    public Rank(Database ds) {
        super("rankcs");
        this.database = ds;
    }

    @Override
    public void run(Message msg) {
        String[] args = getArguments(msg);

        if(args.length == 0) {
            msg.getChannel().sendMessage("Correct usage: -rankcs <name>").queue();
            return;
        }

        String name = args[0];
        Player player = database.getPlayers().get(name);
        if(player == null) {
            msg.getChannel().sendMessage("Player " + name + " does not exist!").queue();
            return;
        }
        List<Player> players = database.getSortedPlayers();
        int rank = players.indexOf(player) + 1;
        double deviation = player.getDeviation();
        int rating = player.getRating();

        msg.getChannel().sendMessage(name + " has rank " + rank + " with a rating of " + rating + " with a " +
                "performance deviation of " + Util.DECIMAL_FORMAT.format(deviation) + ".").queue();
    }

}
