package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.game.player.Settings;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

public class PlayerSettingsQuery
{
    private String playerID;
    private Bson query;
    private JSONObject settingsJSON;

    public PlayerSettingsQuery(String playerID)
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
                .append("detailed", false)
                .append("autoinfo", false)
                .append("default_order", "NUMBER");

        Mongo.SettingsData.insertOne(settingsData);
    }

    //Core
    public void updateSettingBoolean(Settings s, boolean value)
    {
        switch(s) {
            case CLIENT_DETAILED -> Mongo.SettingsData.updateOne(this.query, Updates.set("detailed", value));
            case CLIENT_CATCH_AUTO_INFO -> Mongo.SettingsData.updateOne(this.query, Updates.set("autoinfo", value));
            case CLIENT_POKEMON_LIST_FIELDS -> Mongo.SettingsData.updateOne(this.query, Updates.set("listfields", value));
        }

        this.update();
    }

    public void updateSettingString(Settings s, String value)
    {
        switch(s) {
            case CLIENT_DEFAULT_ORDER -> Mongo.SettingsData.updateOne(this.query, Updates.set("default_order", value));
        }
    }

    public boolean getSettingBoolean(Settings s)
    {
        return switch(s) {
            case CLIENT_DETAILED -> this.settingsJSON.getBoolean("detailed");
            case CLIENT_CATCH_AUTO_INFO -> this.settingsJSON.getBoolean("autoinfo");
            case CLIENT_POKEMON_LIST_FIELDS -> this.settingsJSON.getBoolean("listfields");
            default -> false;
        };
    }

    public String getSettingString(Settings s)
    {
        return switch(s) {
            case CLIENT_DEFAULT_ORDER -> this.settingsJSON.getString("default_order");
            default -> "";
        };
    }

}
