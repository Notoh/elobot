package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.core.entities.Message;


public class Register extends Command {

    public Database ds;

    public Register(Database ds) {
        super("register");
        this.ds = ds;
    }


    @Override
    public void run(Message msg) {
        String[] args = getArguments(msg);
        if(args.length == 0) {
            msg.getChannel().sendMessage("Correct usage: -register <name>").queue();
            return;
        }
        String name = args[0].toLowerCase();
        PlayerWrapper player = ds.getPlayers().get(name);
        if(player != null) {
            msg.getChannel().sendMessage("Player " + name + " already exists!").queue();
            return;
        }
        ds.addPlayer(name);
        msg.getChannel().sendMessage("Player " + name + " added. You start with a rating of 0, standard mean of 25 " +
                "and a " +
                "standard " +
                "deviation of 8.3333").queue();

    }
}
