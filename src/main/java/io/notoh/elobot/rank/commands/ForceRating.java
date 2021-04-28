package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.api.entities.Message;

public class ForceRating extends Command  {

    private final Database database;

    public ForceRating(Database database) {
        super("forcerating");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        if(!checkPermission(msg.getMember())) {
            msg.getChannel().sendMessage("No permission!").queue();
            return;
        }

        String[] args = getArguments(msg);
        if(args.length != 2) {
            msg.getChannel().sendMessage("Usage: -forcerating <player> <rating>").queue();
            return;
        }
        String name = args[0].toLowerCase();
        PlayerWrapper playerWrapper = database.getPlayers().get(name);
        int rating;
        try {
            rating = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            msg.getChannel().sendMessage(args[1] + " is not a number!").queue();
            return;
        }

        playerWrapper.setRating(rating);
        database.updateRating(playerWrapper);
        msg.getChannel().sendMessage("Player " + name + "'s rating forced to " + rating + ".").queue();
    }
}
