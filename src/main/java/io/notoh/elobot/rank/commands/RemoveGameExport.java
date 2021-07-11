package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import net.dv8tion.jda.api.entities.Message;

public class RemoveGameExport extends Command {

    private final AddGameExport addGameExport;

    public RemoveGameExport(AddGameExport addGameExport) {
        super("removegameexport");
        this.addGameExport = addGameExport;
    }

    @Override
    public void run(Message msg) {
        if(!checkPermission(msg.getMember())) {
            msg.getChannel().sendMessage("No permission!").queue();
        }
        msg.getChannel().sendMessage("WARNING: THIS COMMAND DOES NOT WORK PROPERLY ON PLAYERS WITH GAMES >100").queue();
        addGameExport.execute(getArguments(msg), msg.getChannel(), true);
    }
}
