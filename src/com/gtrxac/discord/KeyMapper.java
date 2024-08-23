package com.gtrxac.discord;

import javax.microedition.lcdui.*;

public class KeyMapper extends Canvas implements CommandListener, Strings {
    private State s;
    private int fontHeight;
    private int curHotkey;

    private Displayable lastScreen;

    private Command cancelCommand;
    private Command skipCommand;

    private String[] hotkeyStrings;

    private int[] newHotkeys;

    KeyMapper(State s) {
        this.s = s;
        setCommandListener(this);
        
        fontHeight = s.messageFont.getHeight();
        newHotkeys = new int[5];
        lastScreen = s.disp.getCurrent();
        
        cancelCommand = Locale.createCommand(CANCEL, Command.BACK, 0);
        skipCommand = Locale.createCommand(SKIP, Command.ITEM, 1);
        addCommand(cancelCommand);
        addCommand(skipCommand);

        hotkeyStrings = new String[5];
        for (int i = 0; i < 5; i++) {
            hotkeyStrings[i] = Locale.get(HOTKEY_SEND + i);
        }
    }
    
    protected void paint(Graphics g) {
        // BlackBerry fix
        g.setClip(0, 0, getWidth(), getHeight());

        // Clear screen
        g.setColor(ChannelView.backgroundColors[s.theme]);
        g.fillRect(0, 0, getWidth(), getHeight());

        String prompt = Locale.get(KEY_MAPPER_PROMPT) + "\n" + hotkeyStrings[curHotkey];
        String[] promptLines = Util.wordWrap(prompt, getWidth(), s.messageFont);
        
        g.setFont(s.messageFont);
        g.setColor(ChannelView.messageColors[s.theme]);

        int y = (getHeight() - fontHeight*promptLines.length)/2;
        
        for (int i = 0; i < promptLines.length; i++) {
            g.drawString(promptLines[i], getWidth()/2, y, Graphics.TOP | Graphics.HCENTER);
            y += fontHeight;
        }
    }
    
    private void mapKey(int keycode) {
        // Map this key to the new action and go to next action
        newHotkeys[curHotkey] = keycode;
        curHotkey++;

        // If all actions have been mapped, save and close
        if (curHotkey == 5) {
	        s.sendHotkey = newHotkeys[0];
	        s.replyHotkey = newHotkeys[1];
	        s.copyHotkey = newHotkeys[2];
	        s.refreshHotkey = newHotkeys[3];
	        s.backHotkey = newHotkeys[4];
            LoginSettings.save(s);
            s.disp.setCurrent(lastScreen);
        } else {
            repaint();
        }
    }
    
    protected void keyPressed(int keycode) {
        // Check if key already mapped to another action
        for (int i = 0; i < curHotkey; i++) {
            if (newHotkeys[i] == keycode) {
                s.error(Locale.get(KEY_MAPPER_DUPLICATE) + hotkeyStrings[i]);
                return;
            }
        }
        mapKey(keycode);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c == skipCommand) {
            mapKey(0);
        }
        else if (c == cancelCommand) {
            s.disp.setCurrent(lastScreen);
        }
    }
}