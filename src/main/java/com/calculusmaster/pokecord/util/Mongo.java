package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Mongo
{
    //Connection Strings & Clients
    private static final ConnectionString CONNECT_MAIN = new ConnectionString(PrivateInfo.MAIN_USER);
    private static final MongoClient CLIENT_MAIN = MongoClients.create(MongoClientSettings.builder().applyConnectionString(CONNECT_MAIN).retryReads(true).retryWrites(true).build());

    private static final MongoDatabase PokecordDB = CLIENT_MAIN.getDatabase("Pokecord2");

    public static final MongoCollection<Document> ConfigData = PokecordDB.getCollection("ConfigData");

    public static final MongoCollection<Document> PlayerData = PokecordDB.getCollection("PlayerData");
    public static final MongoCollection<Document> ServerData = PokecordDB.getCollection("ServerData");
    public static final MongoCollection<Document> SettingsData = PokecordDB.getCollection("SettingsData");
    public static final MongoCollection<Document> StatisticsData = PokecordDB.getCollection("StatisticsData");

    public static final MongoCollection<Document> PokemonData = PokecordDB.getCollection("PokemonData");
    public static final MongoCollection<Document> MarketData = PokecordDB.getCollection("MarketData");
    public static final MongoCollection<Document> DexData = PokecordDB.getCollection("DexData");

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
        List<Document> list = new ArrayList<>();
        Mongo.PokemonData.find().forEach(list::add);

        list.forEach(d -> {
            int megaCharges = PokemonRarity.MEGA.stream().anyMatch(s -> s.contains(d.getString("name"))) ? 5
                    : (PokemonRarity.MEGA_LEGENDARY.stream().anyMatch(s -> s.contains(d.getString("name"))) ? 3 : 0);

            Mongo.PokemonData.updateOne(Filters.eq("UUID", d.getString("UUID")), Updates.set("megacharges", megaCharges));
        });
    }

    //Database Collections - Legacy

    @Deprecated
    private static final MongoDatabase MutableDB = CLIENT_MAIN.getDatabase("Mutable");

    public static final MongoCollection<Document> LegacyPlayerData = MutableDB.getCollection("PlayerData");
    public static final MongoCollection<Document> LegacyServerData = MutableDB.getCollection("ServerData");
    public static final MongoCollection<Document> LegacyPokemonData = MutableDB.getCollection("PokemonData");
    public static final MongoCollection<Document> LegacyReportData = MutableDB.getCollection("ReportData");
    public static final MongoCollection<Document> LegacyMarketData = MutableDB.getCollection("MarketData");
    public static final MongoCollection<Document> LegacyDexData = MutableDB.getCollection("DexData");
    public static final MongoCollection<Document> LegacySettingsData = MutableDB.getCollection("SettingsData");
    public static final MongoCollection<Document> LegacyPlayerStatisticsData = MutableDB.getCollection("PlayerStatisticsData");
    public static final MongoCollection<Document> LegacyBountyData = MutableDB.getCollection("BountyData");
    public static final MongoCollection<Document> LegacyConfigData = MutableDB.getCollection("ConfigData");
    public static final MongoCollection<Document> LegacyEggData = MutableDB.getCollection("EggData");
    public static final MongoCollection<Document> LegacyTrainerData = MutableDB.getCollection("TrainerData");
    public static final MongoCollection<Document> LegacyCrashData = MutableDB.getCollection("CrashData");
    public static final MongoCollection<Document> LegacyTimeData = MutableDB.getCollection("TimeData");
}
