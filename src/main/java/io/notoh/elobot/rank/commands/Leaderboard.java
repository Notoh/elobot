package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class Leaderboard extends Command {

    private final Database database;

    public Leaderboard(Database ds) {
        super("leaderboard", "lb");
        this.database = ds;
    }

    @Override
    public void run(Message msg) {
        int page;
        String[] args = getArguments(msg);
        if(args.length == 0) {
            page = 1;
        } else {
            page = Integer.parseInt(args[0]);
        }
        if(page < 1) {
            msg.getChannel().sendMessage("Invalid page!").queue();
            return;
        }

        List<PlayerWrapper> players = database.getSortedPlayers();
        if(players.size() < 1 + ((page-1) * 40)) { //page 2 needs at least 41 players
            msg.getChannel().sendMessage("Too large a page!").queue();
            return;
        }
        MessageBuilder builder = new MessageBuilder(); //avoids 2000 char limit


        builder.append("```\n");
        for(int i = ((page-1) * 40); i < 40 + ((page-1) * 40); i++) {
            if(i > players.size() - 1) {
                break;
            }
            PlayerWrapper player = players.get(i);
            builder.append(players.indexOf(player) + 1).append(" - ").append(player.getName()).append(" - ").append(Util.DECIMAL_FORMAT.format(player.getRating())).append("\n");
        }
        builder.append("```");
        msg.getChannel().sendMessage(builder.build()).queue();
    }


}
