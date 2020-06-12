package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

public class Rank extends Command {

    private Database database;

    public Rank(Database ds) {
        super("rank");
        this.database = ds;
    }

    @Override
    public void run(Message msg) {
        String[] args = getArguments(msg);

        if(args.length == 0) {
            msg.getChannel().sendMessage("Correct usage: -rank <name>").queue();
            return;
        }

        String name = args[0].toLowerCase();
        PlayerWrapper player = database.getPlayers().get(name);
        if(player == null) {
            msg.getChannel().sendMessage("Player " + name + " does not exist!").queue();
            return;
        }
        List<PlayerWrapper> players = database.getSortedPlayers();
        int rank = players.indexOf(player) + 1;
        int rating = player.getRating();

        msg.getChannel().sendMessage(name + " has rank " + rank + " with a rating of " + rating + " with a" +
                " KDA of " + Util.DECIMAL_FORMAT.format(player.getKDA()) + " and a Win% of " + Util.DECIMAL_FORMAT.format(player.getWinPct()*100) + "%.").queue();
    }

}
