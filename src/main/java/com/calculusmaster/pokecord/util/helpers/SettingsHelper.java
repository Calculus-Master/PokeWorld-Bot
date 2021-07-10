package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.Arrays;

public class SettingsHelper
{
    private String playerID;
    private Bson query;
    private JSONObject settingsJSON;

    public SettingsHelper(String playerID)
    {
        this.playerID = playerID;
        this.query = Filters.eq("playerID", playerID);
        this.update();
    }

    private void update()
    {
        this.settingsJSON = new JSONObject(Mongo.SettingsData.find(this.query).first().toJson());
    }

    public static void register(String playerID)
    {
        Document settingsData = new Document()
                .append("playerID", playerID)
                .append("detailed", false);

        Mongo.SettingsData.insertOne(settingsData);
    }

    //Core
    public void updateSettingBoolean(Setting s, boolean value)
    {
        switch(s) {
            case CLIENT_DETAILED -> Mongo.SettingsData.updateOne(this.query, Updates.set("detailed", value));
        }

        this.update();
    }

    public boolean getSettingBoolean(Setting s)
    {
        return switch(s) {
            case CLIENT_DETAILED -> this.settingsJSON.getBoolean("detailed");
            default -> false;
        };
    }

    public enum Setting
    {
        //Client
        CLIENT_DETAILED("detailed", "Toggles the display of IVs and EVs throughout the bot"),
        //Server
        SERVER_PREFIX("prefix", "Changes the bot prefix (default `p!`)"),
        SERVER_SPAWNCHANNEL("spawnchannel", "Toggles if spawns are enabled in a specific channel."),
        SERVER_ZCRYSTAL_DUEL_EQUIP("equipzcrystal_duel", "Toggles if players can equip Z Crystals while in a duel."),
        SERVER_DYNAMAX("dynamax", "Toggles if players can dynamax in duels."),
        SERVER_ZMOVE("zmoves", "Toggles if players can use Z-Moves in duels."),
        SERVER_DUELCHANNEL("duelchannel", "Restricts dueling to certain channels."),
        SERVER_BOTCHANNEL("botchannel", "Restricts all bot commands to certain channels.");

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
