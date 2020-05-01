package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class Punish extends Command {

    private Database database;

    public Punish(Database database) {
        super("punish");
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
        if(args.length != 1) {
            msg.getChannel().sendMessage("Usage: -punish <player>").queue();
            return;
        }
        String name = args[0];
        PlayerWrapper playerWrapper = database.getPlayers().get(name);
        if(playerWrapper == null) {
            msg.getChannel().sendMessage("Player " + name + " does not exist!").queue();
            return;
        }
        playerWrapper.punish();
        database.updateRating(playerWrapper);
        msg.getChannel().sendMessage("Player " + name + " punished 20 rating.").queue();
    }
}
