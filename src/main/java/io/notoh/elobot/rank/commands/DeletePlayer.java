package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import net.dv8tion.jda.api.entities.Message;

public class DeletePlayer extends Command {

    private final Database database;

    public DeletePlayer(Database database) {
        super("deleteplayer");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        if(!checkPermission(msg.getMember())) {
            msg.getChannel().sendMessage("No permission!").queue();
            return;
        }

        String[] args = getArguments(msg);
        if(args.length == 0) {
            msg.getChannel().sendMessage("Correct usage: -deleteplayer <player>").queue();
            return;
        }

        String name = args[0].toLowerCase();
        if(database.getPlayers().get(name) == null) {
            msg.getChannel().sendMessage("Player does not exist!").queue();
            return;
        }
        database.deletePlayer(name);
        msg.getChannel().sendMessage("Deleted player " + name + ".").queue();


    }
}
