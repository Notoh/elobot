package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class DeletePlayer extends Command {

    private Database database;

    public DeletePlayer(Database database) {
        super("deleteplayer");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        boolean hasPerms = false;
        for(Role role : msg.getGuild().getMemberById(msg.getAuthor().getId()).getRoles()) {
            if(role.getId().equals(Util.UPDATE_ROLE)) {
                hasPerms = true;
            }
        }
        if(!hasPerms) {
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
