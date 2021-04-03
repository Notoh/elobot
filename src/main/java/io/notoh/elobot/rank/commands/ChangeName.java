package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.rank.PlayerWrapper;
import net.dv8tion.jda.api.entities.Message;

public class ChangeName extends Command {

    private final Database database;

    public ChangeName(Database database) {
        super("changename");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        if(!checkPermission(msg.getMember())) {
            msg.getChannel().sendMessage("No permission!").queue();
            return;
        }
        String[] args = getArguments(msg);
        if(args.length < 2) {
            msg.getChannel().sendMessage("Correct usage: -changename <old> <new>").queue();
            return;
        }

        String oldName = args[0].toLowerCase();
        String newName = args[1].toLowerCase();
        PlayerWrapper player = database.getPlayers().get(newName);
        if(player != null) {
            msg.getChannel().sendMessage("Player " + newName + " already exists!").queue();
            return;
        }

        if(database.getPlayers().get(oldName) == null) {
            msg.getChannel().sendMessage("Player " + oldName + " does not exist!").queue();
            return;
        }

        database.changeName(oldName, newName);
        msg.getChannel().sendMessage("Name of " + oldName + " changed to " + newName + ".").queue();
    }
}
