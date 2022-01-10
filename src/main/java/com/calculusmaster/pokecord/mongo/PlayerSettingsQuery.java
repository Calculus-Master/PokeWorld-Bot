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
    public <T> void updateSetting(Settings s, T value)
    {
        Mongo.SettingsData.updateOne(this.query, Updates.set(s.getCommand(), value));

        this.update();
    }

    public <T> T getSetting(Settings s, Class<T> clazz)
    {
        return clazz.cast(this.settingsJSON.get(s.getCommand()));
    }
}
