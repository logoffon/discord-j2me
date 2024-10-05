package com.gtrxac.discord;

import javax.microedition.lcdui.*;

public class MainMenu extends ListScreen implements CommandListener, Strings {
    private State s;
    private Command quitCommand;
    private static boolean hasFavorites;

    private static MainMenu instance;

    public static MainMenu get(State s) {
        if (instance == null || s != null) {
            instance = new MainMenu(s);
            hasFavorites = false;
        }
        // Determine if favorites option should be shown:
        // Add if needed
        if (!FavoriteGuilds.empty() && !hasFavorites) {
            instance.insert(1, Locale.get(MAIN_MENU_FAVORITES), instance.s.ic.favorites);
            hasFavorites = true;
        }
        // Remove if needed
        else if (FavoriteGuilds.empty() && hasFavorites) {
            instance.delete(1);
            hasFavorites = false;
        }
        return instance;
    }

    private MainMenu(State s) {
        super("Discord", false);
        setCommandListener(this); 
        this.s = s;

        quitCommand = Locale.createCommand(QUIT, Command.EXIT, 0);
        addCommand(quitCommand);

        append(Locale.get(MAIN_MENU_GUILDS), s.ic.guilds);
        append(Locale.get(MAIN_MENU_DMS), s.ic.dms);
        append(Locale.get(MAIN_MENU_SETTINGS), s.ic.settings);
        append(Locale.get(MAIN_MENU_LOG_OUT), s.ic.logout);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == SELECT_COMMAND) {
            int index = getSelectedIndex();
            if (!hasFavorites && index >= 1) index++;

            switch (index) {
                case 0: {
                    s.openGuildSelector(true, false);
                    break;
                }
                case 1: {
                    FavoriteGuilds.openSelector(s, true);
                    break;
                }
                case 2: {
                    s.openDMSelector(true, false);
                    break;
                }
                case 3: {
                    s.disp.setCurrent(new SettingsForm(s));
                    break;
                }
                case 4: {
                    if (s.gateway != null) s.gateway.stop = true;
                    s.myUserId = null;
                    s.disp.setCurrent(new LoginForm(s));
                }
            }
        }
        else if (c == quitCommand) {
            s.midlet.notifyDestroyed();
        }
    }
}
