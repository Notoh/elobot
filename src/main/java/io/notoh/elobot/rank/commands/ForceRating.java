package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class ForceRating extends Command  {

    private Database database;

    public ForceRating(Database database) {
        super("forcerating");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        boolean hasPerms = false;
        for(Role role : msg.getGuild().getMemberById(msg.getAuthor().getId()).getRoles()) {
            if(role.getId().equals(Util.MOD_ROLE)) {
                hasPerms = true;
            }
        }
        if(!hasPerms) {
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

        playerWrapper.forceRating(rating);
        database.updateRating(playerWrapper);
        msg.getChannel().sendMessage("Player " + name + "'s rating forced to " + rating + ".").queue();
    }
}
