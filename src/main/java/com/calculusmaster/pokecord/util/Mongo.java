package com.calculusmaster.pokecord.util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Mongo
{
    //Connection Strings & Clients
    private static final ConnectionString CONNECT_MAIN = new ConnectionString(PrivateInfo.MAIN_USER);
    private static final MongoClient CLIENT_MAIN = MongoClients.create(MongoClientSettings.builder().applyConnectionString(CONNECT_MAIN).retryReads(true).retryWrites(true).build());

    //Databases
    private static final MongoDatabase ImmutableDB = CLIENT_MAIN.getDatabase("Immutable");
    private static final MongoDatabase MutableDB = CLIENT_MAIN.getDatabase("Mutable");

    //Database Collections
    public static final MongoCollection<Document> PokemonInfo = ImmutableDB.getCollection("PokemonInfo");
    public static final MongoCollection<Document> MoveInfo = ImmutableDB.getCollection("MoveInfo");

    public static final MongoCollection<Document> PlayerData = MutableDB.getCollection("PlayerData");
    public static final MongoCollection<Document> ServerData = MutableDB.getCollection("ServerData");
    public static final MongoCollection<Document> PokemonData = MutableDB.getCollection("PokemonData");
    public static final MongoCollection<Document> ReportData = MutableDB.getCollection("ReportData");
    public static final MongoCollection<Document> PerformanceData = MutableDB.getCollection("PerformanceData");
    public static final MongoCollection<Document> MarketData = MutableDB.getCollection("MarketData");
    public static final MongoCollection<Document> GymData = MutableDB.getCollection("GymData");
    public static final MongoCollection<Document> DexData = MutableDB.getCollection("DexData");
    public static final MongoCollection<Document> SettingsData = MutableDB.getCollection("SettingsData");
    public static final MongoCollection<Document> PlayerStatisticsData = MutableDB.getCollection("PlayerStatisticsData");
    public static final MongoCollection<Document> BountyData = MutableDB.getCollection("BountyData");

    public static void main(String[] args)
    {
        addPokemonJSONFilesToCollection();
    }

    public static void addPokemonJSONFilesToCollection()
    {
        String[] fileNames = new File(PrivateInfo.JSON_SRC_PATH).list();

        File f;
        Document d;

        List<Document> toInsert = new ArrayList<>();
        try
        {
            for (String name : fileNames)
            {
                System.out.println("Creating Document for " + name);
                f = new File(PrivateInfo.JSON_SRC_PATH + name);
                d = getDocumentFromJSON(new JSONObject(new JSONTokener(new FileInputStream(f))));
                toInsert.add(d);
            }
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }

        toInsert.sort(Comparator.comparingInt(d2 -> d2.getInteger("dex")));
        for(Document doc : toInsert) Mongo.PokemonInfo.insertOne(doc);
    }

    public static Document getDocumentFromJSON(JSONObject j)
    {
        Document data = new Document();
        final String[] keys = {"name", "fillerinfo", "dex", "type", "evolutions", "evolutionsLVL", "forms", "mega", "stats", "ev", "moves", "movesLVL", "movesTM", "movesTR", "abilities", "growthrate", "exp", "normalURL", "shinyURL"};

        data.append(keys[0], j.getString(keys[0]))
                .append(keys[1], j.getString(keys[1]))
                .append(keys[2], j.getInt(keys[2]))
                .append(keys[3], j.getJSONArray(keys[3]))
                .append(keys[4], j.getJSONArray(keys[4]))
                .append(keys[5], j.getJSONArray(keys[5]))
                .append(keys[6], j.getJSONArray(keys[6]))
                .append(keys[7], j.getJSONArray(keys[7]))
                .append(keys[8], j.getJSONArray(keys[8]))
                .append(keys[9], j.getJSONArray(keys[9]))
                .append(keys[10], j.getJSONArray(keys[10]))
                .append(keys[11], j.getJSONArray(keys[11]))
                .append(keys[12], j.getJSONArray(keys[12]))
                .append(keys[13], j.getJSONArray(keys[13]))
                .append(keys[14], j.getJSONArray(keys[14]))
                .append(keys[15], j.getString(keys[15]))
                .append(keys[16], j.getInt(keys[16]))
                .append(keys[17], j.getString(keys[17]))
                .append(keys[18], j.getString(keys[18]));

        return data;
    }
}
