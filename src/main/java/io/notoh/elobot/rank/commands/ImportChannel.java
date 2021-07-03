package io.notoh.elobot.rank.commands;

import io.notoh.elobot.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportChannel extends Command {

    private final AddGameExport addGameExport;

    public ImportChannel(AddGameExport addGameExport) {
        super("importchannel");
        this.addGameExport = addGameExport;
    }

    @Override
    public void run(Message msg) {
        if(!checkPermission(msg.getMember())) {
            msg.getChannel().sendMessage("No permission!").queue();
        }
        String[] args = getArguments(msg);
        if(args.length != 3) {
            msg.getChannel().sendMessage("Format: -importchannel <id> <beginning_msg_id> <exclude_reaction>").queue();
            return;
        }

        MessageChannel channel = msg.getJDA().getTextChannelById(args[0]);
        if(channel == null) {
            msg.getChannel().sendMessage("Cannot find channel").queue();
            return;
        }

        msg.getChannel().sendMessage("Requesting history").queue();
        MessageHistory history = channel.getHistoryAfter(args[1], 100).complete();
        msg.getChannel().sendMessage("History retrieved").queue();

        List<Message> messages = new ArrayList<>(history.getRetrievedHistory());
        Collections.reverse(messages);

        for(Message message : messages) {
            if(checkReactions(message, args[2])) {
                continue;
            }
            msg.getChannel().sendMessage("Message id loaded " + message.getId()).queue();
            if(!addGameExport.execute(message.getContentRaw().replace("`", "").split("\\s+"), msg.getChannel(), false)) {
                break;
            }
        }
    }

    private boolean checkReactions(Message message, String excludeReaction) {
         return message.getReactionById(excludeReaction) != null;
    }
}
