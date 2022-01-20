package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.game.player.Settings;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PlayerSettingsQuery
{
    private Document document;
    private final Bson query;

    public PlayerSettingsQuery(String playerID)
    {
        this.query = Filters.eq("playerID", playerID);
        this.update();
    }

    private void update()
    {
        this.document = Mongo.SettingsData.find(this.query).first();
    }

    public static void register(String playerID)
    {
        Document settingsData = new Document()
                .append("playerID", playerID)
                .append("detailed", false)
                .append("autoinfo", false)
                .append("default_order", "NUMBER")
                .append("listfields", false);

        LoggerHelper.logDatabaseInsert(PlayerSettingsQuery.class, settingsData);

        Mongo.SettingsData.insertOne(settingsData);
    }

    //Core
    public <T> void update(Settings s, T value)
    {
        Mongo.SettingsData.updateOne(this.query, Updates.set(s.getCommand(), value));

        this.update();
    }

    public <T> T get(Settings s, Class<T> clazz)
    {
        return clazz.cast(this.document.get(s.getCommand()));
    }
}
