package com.calculusmaster.pokecord.util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Mongo
{
    //Connection Strings & Clients
    private static final ConnectionString CONNECT_MAIN = new ConnectionString(PrivateInfo.MAIN_USER);
    private static final MongoClient CLIENT_MAIN = MongoClients.create(MongoClientSettings.builder().applyConnectionString(CONNECT_MAIN).retryReads(true).retryWrites(true).build());

    //Databases
    private static final MongoDatabase ImmutableDB = CLIENT_MAIN.getDatabase("Immutable");
    private static final MongoDatabase MutableDB = CLIENT_MAIN.getDatabase("Mutable");

    //Database Collections
    public static final MongoCollection<Document> PlayerData = MutableDB.getCollection("PlayerData");
    public static final MongoCollection<Document> ServerData = MutableDB.getCollection("ServerData");
    public static final MongoCollection<Document> PokemonData = MutableDB.getCollection("PokemonData");
    public static final MongoCollection<Document> ReportData = MutableDB.getCollection("ReportData");
    public static final MongoCollection<Document> MarketData = MutableDB.getCollection("MarketData");
    public static final MongoCollection<Document> GymData = MutableDB.getCollection("GymData");
    public static final MongoCollection<Document> DexData = MutableDB.getCollection("DexData");
    public static final MongoCollection<Document> SettingsData = MutableDB.getCollection("SettingsData");
    public static final MongoCollection<Document> PlayerStatisticsData = MutableDB.getCollection("PlayerStatisticsData");
    public static final MongoCollection<Document> BountyData = MutableDB.getCollection("BountyData");
    public static final MongoCollection<Document> ConfigData = MutableDB.getCollection("ConfigData");
    public static final MongoCollection<Document> EggData = MutableDB.getCollection("EggData");
    public static final MongoCollection<Document> TrainerData = MutableDB.getCollection("TrainerData");
    public static final MongoCollection<Document> CrashData = MutableDB.getCollection("CrashData");
    public static final MongoCollection<Document> TimeData = MutableDB.getCollection("TimeData");
}
