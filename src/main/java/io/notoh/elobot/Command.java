package io.notoh.elobot;


import net.dv8tion.jda.api.entities.Message;

public abstract class Command {

    public String command;

    public Command(String command) {
        this.command = command;
    }

    public abstract void run(Message msg);

    public String[] getArguments(Message msg) {
        int cut = msg.getContentRaw().indexOf(' ') + 1;
        if (cut == 0) {
            return new String[]{};
        }
        return msg.getContentRaw().substring(cut).split("\\s+");
    }

}
