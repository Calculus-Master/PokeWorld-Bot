package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;

import java.util.List;

public class ServerDataQuery extends MongoQuery
{
    public ServerDataQuery(String serverID)
    {
        super("serverID", serverID, Mongo.ServerData);
    }

    //Core

    public static boolean isRegistered(Guild server)
    {
        return !new ServerDataQuery(server.getId()).isNull();
    }

    public static void register(Guild server)
    {
        Document serverData = new Document()
                .append("serverID", server.getId())
                .append("prefix", "p!")
                .append("spawnchannel", new JSONArray())
                .append("equipzcrystal_duel", true)
                .append("dynamax", true)
                .append("zmoves", true)
                .append("duelchannel", new JSONArray())
                .append("botchannel", new JSONArray());

        LoggerHelper.logDatabaseInsert(ServerDataQuery.class, serverData);

        Mongo.ServerData.insertOne(serverData);

        SpawnEventHelper.start(server);
    }

    @Override
    protected void update(Bson update)
    {
        Mongo.ServerData.updateOne(this.query, update);

        this.document = Mongo.ServerData.find(this.query).first();
    }

    //key: "prefix"

    public String getPrefix()
    {
        return this.document.getString("prefix");
    }

    public void setPrefix(String prefix)
    {
        this.update(Updates.set("prefix", prefix));
    }

    //key: "spawnchannel"

    public List<String> getSpawnChannels()
    {
        return this.document.getList("spawnchannel", String.class);
    }

    public void addSpawnChannel(String channelID)
    {
        this.update(Updates.push("spawnchannel", channelID));
    }

    public void removeSpawnChannel(String channelID)
    {
        this.update(Updates.pull("spawnchannel", channelID));
    }

    //key: "equipzcrystal_duel"

    public boolean canEquipZCrystalDuel()
    {
        return this.document.getBoolean("equipzcrystal_duel");
    }

    public void setEquipZCrystalDuel(boolean val)
    {
        this.update(Updates.set("equipzcrystal_duel", val));
    }

    //key: "dynamax"

    public boolean isDynamaxEnabled()
    {
        return this.document.getBoolean("dynamax");
    }

    public void setDynamaxEnabled(boolean val)
    {
        this.update(Updates.set("dynamax", val));
    }

    //key: "zmoves"

    public boolean areZMovesEnabled()
    {
        return this.document.getBoolean("zmoves");
    }

    public void setZMovesEnabled(boolean val)
    {
        this.update(Updates.set("zmoves", val));
    }

    //key: "duelchannel"

    public List<String> getDuelChannels()
    {
        return this.document.getList("duelchannel", String.class);
    }

    public void addDuelChannel(String channelID)
    {
        this.update(Updates.push("duelchannel", channelID));
    }

    public void removeDuelChannel(String channelID)
    {
        this.update(Updates.pull("duelchannel", channelID));
    }

    public void clearDuelChannels()
    {
        this.update(Updates.set("duelchannel", new JSONArray()));
    }

    //key: "botchannel"

    public List<String> getBotChannels()
    {
        return this.document.getList("botchannel", String.class);
    }

    public void addBotChannel(String channelID)
    {
        this.update(Updates.push("botchannel", channelID));
    }

    public void removeBotChannel(String channelID)
    {
        this.update(Updates.pull("botchannel", channelID));
    }

    public void clearBotChannels()
    {
        this.update(Updates.set("botchannel", new JSONArray()));
    }
}