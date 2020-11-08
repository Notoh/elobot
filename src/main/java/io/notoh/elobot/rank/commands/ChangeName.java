package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import io.notoh.elobot.Util;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public class ChangeName extends Command {

    private Database database;

    public ChangeName(Database database) {
        super("changename");
        this.database = database;
    }

    @Override
    public void run(Message msg) {
        boolean hasPerms = false;
        Member member = msg.getMember();
        if(member == null) {
            return;
        }
        for(Role role : member.getRoles()) {
            if(role.getId().equals(Util.UPDATE_ROLE)) {
                hasPerms = true;
            }
        }

        if(!hasPerms) {
            msg.getChannel().sendMessage("No permission!").queue();
            return;
        }
        String[] args = getArguments(msg);
        if(args.length < 2) {
            msg.getChannel().sendMessage("Correct usage: -changename <old> <new>").queue();
            return;
        }
        if(database.getPlayers().get(args[0].toLowerCase()) == null) {
            msg.getChannel().sendMessage("Player " + args[0] + " does not exist!").queue();
            return;
        }

        database.changeName(args[0].toLowerCase(), args[1].toLowerCase());
        msg.getChannel().sendMessage("Name of " + args[0].toLowerCase() + " changed to " + args[1].toLowerCase() +
                ".").queue();
    }
}
