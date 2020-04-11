package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
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
        ds.addPlayer(name);
        msg.getChannel().sendMessage("Player " + name + " added. You start with a rating of 1500 and " +
                "performance deviation of " +
                "350.0.").queue();

    }
}
