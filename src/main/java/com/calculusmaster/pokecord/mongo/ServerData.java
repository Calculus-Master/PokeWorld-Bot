package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.mongo.cache.CacheHandler;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import net.dv8tion.jda.api.entities.Guild;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerData extends MongoQuery
{
    private static final ExecutorService UPDATER = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("ServerData Updater-", 0).factory());

    private ServerData(Document data)
    {
        super("serverID", data.getString("serverID"), Mongo.ServerData);
    }

    public static ServerData build(String serverID)
    {
        return CacheHandler.SERVER_DATA.get(serverID, id ->
        {
            LoggerHelper.info(ServerData.class, "Loading new PlayerData into Cache for ID: " + id + ".");
            Document data = Mongo.ServerData.find(Filters.eq("serverID", serverID)).first();

            return new ServerData(Objects.requireNonNull(data, "Null ServerData for ID: " + serverID));
        });
    }

    //Core

    public static void register(Guild server)
    {
        Document serverData = new Document()
                .append("serverID", server.getId())
                .append("settings", new Document());

        LoggerHelper.logDatabaseInsert(ServerData.class, serverData);

        Mongo.ServerData.insertOne(serverData);

        SpawnEventHelper.start(server);
    }

    public String getID()
    {
        return this.document.getString("serverID");
    }

    private synchronized void updateDocument(Document document)
    {
        this.document = document;
    }

    @Override
    protected void update(Bson update)
    {
        UPDATER.submit(() -> {
            Document d = this.database.findOneAndUpdate(this.query, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

            this.updateDocument(d);

            LoggerHelper.logDatabaseUpdate(this.getClass(), this.query, update);
        });
    }

    //Settings (key: "settings")
    public Document getSettingsData()
    {
        return this.document.get("settings", Document.class);
    }
}