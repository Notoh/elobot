package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.Objects;

public class Carry extends Command {

    private Database database;

    public Carry(Database database) {
        super("carry");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        boolean hasPerms = false;
        for(Role role : Objects.requireNonNull(msg.getGuild().getMemberById(msg.getAuthor().getId())).getRoles()) {
            if(role.getId().equals(Util.UPDATE_ROLE)) {
                hasPerms = true;
            }
        }
        if(!hasPerms) {
            msg.getChannel().sendMessage("No permission!").queue();
            return;
        }

        String[] args = getArguments(msg);
        if(args.length != 1) {
            msg.getChannel().sendMessage("Usage: -carry <player>").queue();
            return;
        }
        String name = args[0].toLowerCase();
        PlayerWrapper playerWrapper = database.getPlayers().get(name);
        if(playerWrapper == null) {
            msg.getChannel().sendMessage("Player " + name + " does not exist!").queue();
            return;
        }
        playerWrapper.carry();
        database.updateRating(playerWrapper);
        msg.getChannel().sendMessage("Player " + name + " added 5 rating.").queue();
    }
}