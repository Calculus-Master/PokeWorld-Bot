package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.util.PrivateInfo;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Mongo
{
    //Connection Strings & Clients
    private static final ConnectionString CONNECT_MAIN = new ConnectionString(PrivateInfo.MAIN_USER);
    private static final MongoClient CLIENT_MAIN = MongoClients.create(MongoClientSettings.builder().applyConnectionString(CONNECT_MAIN).retryReads(true).retryWrites(true).build());

    //Database Entry
    private static final MongoDatabase PokecordDB = CLIENT_MAIN.getDatabase("Pokecord2");

    //Updater Thread Pool
    public static final ExecutorService UPDATER = Executors.newCachedThreadPool();

    //Updates
    public static void updateOne(String reason, DatabaseCollection collection, Bson query, Bson update)
    {
        long timeI = System.currentTimeMillis();
        UPDATER.submit(() -> {
            LoggerHelper.info(Mongo.class, "Database Update (One): \"%s\" (%s) – Query: %s, Update: %s".formatted(reason, collection.toString(), query.toString(), update.toString()));
            collection.getCollection(PokecordDB).updateOne(query, update);
            Mongo.logTime(timeI);
        });
    }

    public static void updateMany(String reason, DatabaseCollection collection, Bson query, Bson update)
    {
        long timeI = System.currentTimeMillis();
        UPDATER.submit(() -> {
            LoggerHelper.info(Mongo.class, "Database Update (Many): \"%s\" (%s) – Query: %s, Update: %s".formatted(reason, collection.toString(), query.toString(), update.toString()));
            collection.getCollection(PokecordDB).updateMany(query, update);
            Mongo.logTime(timeI);
        });
    }

    //Finds
    public static Document findOne(String reason, DatabaseCollection collection, Bson query)
    {
        long timeI = System.currentTimeMillis();
        LoggerHelper.info(Mongo.class, "Database Query (One): \"%s\" (%s) – Query: %s".formatted(reason, collection.toString(), query.toString()));
        Document d = collection.getCollection(PokecordDB).find(query).first();
        Mongo.logTime(timeI);
        return d;
    }

    public static List<Document> findMany(String reason, DatabaseCollection collection, Bson query)
    {
        long timeI = System.currentTimeMillis();
        LoggerHelper.info(Mongo.class, "Database Query (Many): \"%s\" (%s) – Query: %s".formatted(reason, collection.toString(), query.toString()));
        List<Document> d = collection.getCollection(PokecordDB).find(query).into(new ArrayList<>());
        Mongo.logTime(timeI);
        return d;
    }

    public static List<Document> findAll(String reason, DatabaseCollection collection)
    {
        long timeI = System.currentTimeMillis();
        LoggerHelper.info(Mongo.class, "Database Query (All): \"%s\" (%s)".formatted(reason, collection.toString()));
        List<Document> d = collection.getCollection(PokecordDB).find().into(new ArrayList<>());
        Mongo.logTime(timeI);
        return d;
    }

    //Inserts
    public static void insertOne(String reason, DatabaseCollection collection, Document doc)
    {
        long timeI = System.currentTimeMillis();
        UPDATER.submit(() -> {
            LoggerHelper.info(Mongo.class, "Database Insert (One): \"%s\" (%s) – Document: %s".formatted(reason, collection.toString(), doc.toString()));
            collection.getCollection(PokecordDB).insertOne(doc);
            Mongo.logTime(timeI);
        });
    }

    //Deletes
    public static void deleteOne(String reason, DatabaseCollection collection, Bson query)
    {
        long timeI = System.currentTimeMillis();
        UPDATER.submit(() -> {
            LoggerHelper.info(Mongo.class, "Database Delete (One): \"%s\" (%s) – Query: %s".formatted(reason, collection.toString(), query.toString()));
            collection.getCollection(PokecordDB).deleteOne(query);
            Mongo.logTime(timeI);
        });
    }

    private static void logTime(long timeI)
    {
        LoggerHelper.info(Mongo.class, "Database Update took " + ((System.currentTimeMillis() - timeI) / 1000.) + "ms.");
    }
    
    //Old accessors for databases

    public static final MongoCollection<Document> ConfigData = PokecordDB.getCollection("ConfigData");
    public static final MongoCollection<Document> MiscData = PokecordDB.getCollection("MiscData");

    public static final MongoCollection<Document> PlayerData = PokecordDB.getCollection("PlayerData");
    public static final MongoCollection<Document> ServerData = PokecordDB.getCollection("ServerData");
    public static final MongoCollection<Document> SettingsData = PokecordDB.getCollection("SettingsData");
    public static final MongoCollection<Document> StatisticsData = PokecordDB.getCollection("StatisticsData");

    public static final MongoCollection<Document> PokemonData = PokecordDB.getCollection("PokemonData");
    public static final MongoCollection<Document> MarketData = PokecordDB.getCollection("MarketData");

    public static final MongoCollection<Document> BountyData = PokecordDB.getCollection("BountyData");
    public static final MongoCollection<Document> EggData = PokecordDB.getCollection("EggData");
    public static final MongoCollection<Document> TrainerData = PokecordDB.getCollection("TrainerData");
    public static final MongoCollection<Document> MegaChargeData = PokecordDB.getCollection("MegaChargeData");

    public static final MongoCollection<Document> TimeData = PokecordDB.getCollection("TimeData");

    public static final MongoCollection<Document> ReportData = PokecordDB.getCollection("ReportData");
    public static final MongoCollection<Document> CrashData = PokecordDB.getCollection("CrashData");

    //For random database queries
    public static void main(String[] args)
    {
        Mongo.BountyData.deleteMany(Filters.exists("bountyID"));
        Mongo.CrashData.deleteMany(Filters.exists("error"));
        Mongo.EggData.deleteMany(Filters.exists("eggID"));
        Mongo.PlayerData.deleteMany(Filters.exists("playerID"));
        Mongo.PokemonData.deleteMany(Filters.exists("UUID"));
        Mongo.ServerData.deleteMany(Filters.exists("serverID"));
        Mongo.SettingsData.deleteMany(Filters.exists("playerID"));
        Mongo.StatisticsData.deleteMany(Filters.exists("playerID"));
        Mongo.TrainerData.deleteMany(Filters.exists("trainerID"));
    }
}
