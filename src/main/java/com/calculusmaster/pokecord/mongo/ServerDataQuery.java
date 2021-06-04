package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.SpawnEventHandler;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bson.Document;

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

    public static void register(Guild server, String channelID)
    {
        Document serverData = new Document()
                .append("serverID", server.getId())
                .append("prefix", "p!")
                .append("spawnchannel", channelID);

        Mongo.ServerData.insertOne(serverData);

        SpawnEventHandler.start(server);
    }

    //Gets

    public String getPrefix()
    {
        return this.json().getString("prefix");
    }

    public String getSpawnChannelID()
    {
        return this.json().getString("spawnchannel");
    }

    //Updates

    public void setPrefix(String prefix)
    {
        Mongo.ServerData.updateOne(this.query, Updates.set("prefix", prefix));
    }

    public void setSpawnChannel(MessageChannel channel)
    {
        Mongo.ServerData.updateOne(this.query, Updates.set("spawnchannel", channel.getId()));
    }
}