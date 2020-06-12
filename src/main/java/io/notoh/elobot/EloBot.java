package io.notoh.elobot;


import io.notoh.elobot.rank.commands.*;
import io.notoh.elobot.rank.commands.veto.Ban;
import io.notoh.elobot.rank.commands.veto.InitiateBan;
import io.notoh.elobot.rank.commands.veto.NotBanned;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class EloBot {

    public void start() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("./credentials.txt"));
            Util.TOKEN = br.readLine();
            Util.DB_USER = br.readLine();
            Util.DB_PASS = br.readLine();
            Util.DB_URL = br.readLine();
            br.close();
            System.out.println("Read credentials successfully.");

            JDA bot = new JDABuilder(Util.TOKEN).build().awaitReady();
            Database database = new Database(bot);
            MainEventHandler handler = new MainEventHandler(database);
            bot.addEventListener(handler);

            handler.addCommand(new Rank(database));
            handler.addCommand(new Register(database));
            handler.addCommand(new AddGameExport(database));
            handler.addCommand(new DeletePlayer(database));
            handler.addCommand(new Leaderboard(database));
            handler.addCommand(new Help(database));
            handler.addCommand(new Captains(database));
            handler.addCommand(new ChangeName(database));
            handler.addCommand(new Punish(database));
            handler.addCommand(new Pardon(database));
            handler.addCommand(new Carry(database));
            handler.addCommand(new ForceRating(database));
            handler.addCommand(new RemoveGame(database));

            handler.addCommand(new Lb(database));
            handler.addCommand(new R(database));

            handler.addCommand(new Ban(database));
            handler.addCommand(new InitiateBan(database));
            handler.addCommand(new NotBanned(database));

         } catch (LoginException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

}
