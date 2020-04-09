package io.notoh.elobot.rank.commands.valorant;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.rank.Player;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;
import java.util.Queue;

public class LeaderboardVal extends Command {

    private Database database;

    public LeaderboardVal(Database ds) {
        super("leaderboardval");
        this.database = ds;
    }

    @Override
    public void run(Message msg) {

        List<Player> players = database.getValSortedPlayers();
        MessageBuilder builder = new MessageBuilder(); //avoids 2000 char limit

        builder.append("```\n");
        for (Player player : players) {
            builder.append("Rank ").append(players.indexOf(player) + 1).append(": ").append(player.getName()).append(" with rating ").append(player.getRating()).append("\n");
        }
        builder.append("```");
        Queue<Message> msgs = builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE);
        for (Message message : msgs) {
            msg.getChannel().sendMessage(message).queue();
        }
    }


}