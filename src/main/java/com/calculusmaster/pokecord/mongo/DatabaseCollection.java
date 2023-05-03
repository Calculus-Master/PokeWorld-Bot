package com.calculusmaster.pokecord.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public enum DatabaseCollection
{
    CONFIG      ("ConfigData"),

    PLAYER      ("PlayerData"),
    SERVER      ("ServerData"),

    POKEMON     ("PokemonData"),
    MARKET      ("MarketData"),
    DEX         ("DexData"),

    BOUNTY      ("BountyData"),
    EGG         ("EggData"),
    TRAINER     ("TrainerData"),
    MEGA_CHARGE ("MegaChargeData"),

    TIME        ("TimeData"),
    REPORT      ("ReportData"),
    CRASH       ("CrashData"),


    ;

    private final String collectionName;

    DatabaseCollection(String collectionName)
    {
        this.collectionName = collectionName;
    }

    public MongoCollection<Document> getCollection(MongoDatabase database)
    {
        return database.getCollection(this.collectionName);
    }
}
