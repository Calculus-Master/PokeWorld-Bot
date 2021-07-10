package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.SpawnEventHelper;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;
import org.json.JSONArray;

import java.util.List;
import java.util.stream.Collectors;

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
                .append("spawnchannel", new JSONArray());

        Mongo.ServerData.insertOne(serverData);

        SpawnEventHelper.start(server);
    }

    private void update()
    {
        this.document = Mongo.ServerData.find(this.query).first();
    }

    //key: "prefix"

    public String getPrefix()
    {
        return this.json().getString("prefix");
    }

    public void setPrefix(String prefix)
    {
        Mongo.ServerData.updateOne(this.query, Updates.set("prefix", prefix));

        this.update();
    }

    //key: "spawnchannel"

    public List<String> getSpawnChannels()
    {
        return this.json().getJSONArray("spawnchannel").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public void addSpawnChannel(String channelID)
    {
        Mongo.ServerData.updateOne(this.query, Updates.push("spawnchannel", channelID));

        this.update();
    }

    public void removeSpawnChannel(String channelID)
    {
        Mongo.ServerData.updateOne(this.query, Updates.pull("spawnchannel", channelID));

        this.update();
    }

    //key: "equipzcrystal_duel"

    public boolean canEquipZCrystalDuel()
    {
        return this.json().getBoolean("equipzcrystal_duel");
    }

    public void setEquipZCrystalDuel(boolean val)
    {
        Mongo.ServerData.updateOne(this.query, Updates.set("equipzcrystal_duel", val));

        this.update();
    }
}