package io.notoh.elobot;


import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public abstract class Command {

    public String[] command;

    public Command(String... names) {
        this.command = names;
    }

    public abstract void run(Message msg);

    public String[] getArguments(Message msg) {
        int cut = msg.getContentRaw().indexOf(' ') + 1;
        if (cut == 0) {
            return new String[]{};
        }
        return msg.getContentRaw().substring(cut).split("\\s+");
    }

    public boolean checkPermission(Member member) {
        if(member == null) {
            return false;
        }
        for(Role role : member.getRoles()) {
            if(role.getId().equals(Util.MOD_ROLE)) {
                return true;
            }
        }
        return false;
    }

}
