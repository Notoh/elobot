package io.notoh.elobot.rank.commands.veto;

import io.notoh.elobot.Command;
import io.notoh.elobot.Database;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class NotBanned extends Command {

    public NotBanned(Database database) {
        super("notbanned");
    }

    @Override
    public void run(Message msg) {
        MessageBuilder builder = new MessageBuilder();
        builder.append("```\n");
        for(String string : InitiateBan.maps.keySet()) {
            if(!InitiateBan.maps.get(string)) {
                builder.append(string).append("\n");
            }
        }
        builder.append("```");
        msg.getChannel().sendMessage(builder.build()).queue();
    }
}
