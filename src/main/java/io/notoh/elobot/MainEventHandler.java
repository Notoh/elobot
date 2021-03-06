package io.notoh.elobot;


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class MainEventHandler extends ListenerAdapter {

    private final Map<List<String>, Command> commands = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        if(msg.getAuthor().isBot()) {
            return;
        }

        if(!msg.getContentRaw().startsWith(Util.PREFIX)) {
            return;
        }
        int index = msg.getContentRaw().indexOf(' ');
        String commandName = msg.getContentRaw().substring(Util.PREFIX.length(), index < 0 ?
                msg.getContentRaw().length() : index).toLowerCase();

        for(Map.Entry<List<String>, Command> entry : commands.entrySet()) {
            if(entry.getKey().contains(commandName)) {
                entry.getValue().run(msg);
                return;
            }
        }
    }

    public void addCommand(Command cmd) {
        commands.put(Arrays.asList(cmd.command), cmd);
    }



}
