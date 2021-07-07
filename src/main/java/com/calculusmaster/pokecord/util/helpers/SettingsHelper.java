package com.calculusmaster.pokecord.util.helpers;

import java.util.Arrays;

public class SettingsHelper
{
    public enum Setting
    {
        //Client
        //Server
        SERVER_PREFIX("prefix", "Changes the bot prefix (default `p!`)"),
        SERVER_SPAWNCHANNEL("spawnchannel", "Toggles if spawns are enabled in a specific channel.");

        private String command;
        private String desc;

        Setting(String command, String desc)
        {
            this.command = command;
            this.desc = desc;
        }

        public static boolean isValid(String command)
        {
            return Arrays.stream(values()).anyMatch(s -> s.getCommand().equals(command));
        }

        public boolean matches(String input)
        {
            return this.command.equals(input);
        }

        public boolean isClient()
        {
            return this.toString().contains("CLIENT");
        }

        public boolean isServer()
        {
            return this.toString().contains("SERVER");
        }

        public String getCommand()
        {
            return this.command;
        }

        public String getDesc()
        {
            return this.desc;
        }
    }
}
