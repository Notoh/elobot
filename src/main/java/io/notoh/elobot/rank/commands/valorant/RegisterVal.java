package io.notoh.elobot.rank.commands.valorant;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import net.dv8tion.jda.core.entities.Message;


public class RegisterVal extends Command {

    public Database ds;

    public RegisterVal(Database ds) {
        super("registerval");
        this.ds = ds;
    }


    @Override
    public void run(Message msg) {
        String[] args = getArguments(msg);
        if(args.length == 0) {
            msg.getChannel().sendMessage("Correct usage: -registerval <name>").queue();
            return;
        }
        String name = args[0];
        ds.addValPlayer(name);
        msg.getChannel().sendMessage("Player " + name + " added. You start with a rating of 1500 and " +
                "performance deviation of " +
                "350.0.").queue();

    }
}
