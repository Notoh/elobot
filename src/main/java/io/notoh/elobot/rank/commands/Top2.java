package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.Player;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;


public class Top2 extends Command {
    private Database database;

    public Top2(Database database) {
        super("top2");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        boolean hasPerms = false;
        for(Role role : msg.getGuild().getMemberById(msg.getAuthor().getId()).getRoles()) {
            if(role.getId().equals(Util.ORG_ROLE)) {
                hasPerms = true;
            }
        }
        if(!hasPerms) {
            msg.getChannel().sendMessage("No permission!").queue();
            return;
        }
        String[] args = getArguments(msg);
        if(args.length < 10) {
            msg.getChannel().sendMessage("Usage: -top2 <ten players>").queue();
            return;
        }
        int bestRating = Integer.MIN_VALUE;
        Player best = null;
        int secondBest = Integer.MIN_VALUE;
        Player second = null;
        for(String name : args) {
            Player player = database.getPlayers().get(name);
            int rating = player.getRating();
            if(rating > bestRating) {
                second = best;
                secondBest = bestRating;
                bestRating = rating;
                best = player;
            } else if(rating > secondBest) {
                secondBest = rating;
                second = player;
            }
        }
        if(best == null || second == null) {
            msg.getChannel().sendMessage("An error occurred.").queue();
            return;
        }
        msg.getChannel().sendMessage("Top 2 are " + best.getName() + " and " + second.getName() + ".").queue();
    }
}
