package com.calculusmaster.pokecord.game.player;

import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerInventory
{
    private static final ExecutorService UPDATER = Executors.newFixedThreadPool(5);

    private final PlayerDataQuery playerData;

    private final Map<Item, Integer> items;
    private final EnumSet<ZCrystal> zcrystals;
    private final Map<TM, Integer> tms;

    public PlayerInventory(PlayerDataQuery playerData)
    {
        this.playerData = playerData;

        this.items = new LinkedHashMap<>();
        this.zcrystals = EnumSet.noneOf(ZCrystal.class);
        this.tms = new LinkedHashMap<>();
    }

    public PlayerInventory(PlayerDataQuery playerData, Document data)
    {
        this(playerData);

        data.get("items", Document.class).forEach((i, amount) -> this.items.put(Item.cast(i), (int)amount));
        data.getList("zcrystals", String.class).forEach(z -> this.zcrystals.add(ZCrystal.cast(z)));
        data.get("tms", Document.class).forEach((tm, amount) -> this.tms.put(TM.cast(tm), (int)amount));
    }

    public Document serialize()
    {
        Document data = new Document(new LinkedHashMap<>());

        Document items = new Document(new LinkedHashMap<>());
        this.items.forEach((item, count) -> items.append(item.toString(), count));
        data.append("items", items);

        data.append("zcrystals", this.zcrystals.stream().map(Enum::toString).toList());

        Document tms = new Document(new LinkedHashMap<>());
        this.tms.forEach((tm, count) -> tms.append(tm.toString(), count));
        data.append("tms", tms);

        return data;
    }

    //Updaters
    public void addItem(Item item)
    {
        this.items.put(item, this.items.getOrDefault(item, 0) + 1);

        this.playerData.getStatistics().increase(StatisticType.ITEMS_ACQUIRED);
        UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.inc("inventory.items." + item.toString(), 1)));
    }

    public void removeItem(Item item)
    {
        int count = this.items.getOrDefault(item, 0);

        if(count - 1 == 0)
        {
            this.items.remove(item);

            UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.unset("inventory.items." + item.toString())));
        }
        else if(count - 1 > 0)
        {
            this.items.put(item, count - 1);

            UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.inc("inventory.items." + item.toString(), -1)));
        }
    }

    public void addZCrystal(ZCrystal crystal)
    {
        this.zcrystals.add(crystal);

        UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.push("inventory.zcrystals", crystal.toString())));
    }

    public void addTM(TM tm)
    {
        this.tms.put(tm, this.tms.getOrDefault(tm, 0) + 1);

        this.playerData.getStatistics().increase(StatisticType.TMS_ACQUIRED);
        UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.inc("inventory.tms." + tm.toString(), 1)));
    }

    public void removeTM(TM tm)
    {
        int count = this.tms.getOrDefault(tm, 0);

        if(count - 1 == 0)
        {
            this.tms.remove(tm);

            UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.unset("inventory.tms." + tm.toString())));
        }
        else if(count - 1 > 0)
        {
            this.tms.put(tm, count - 1);

            UPDATER.submit(() -> Mongo.PlayerData.updateOne(this.playerData.getQuery(), Updates.inc("inventory.tms." + tm.toString(), -1)));
        }
    }

    //Accessors
    public Map<Item, Integer> getItems()
    {
        return this.items;
    }

    public boolean hasItem(Item i)
    {
        return this.items.containsKey(i);
    }

    public int getItemCount()
    {
        return this.items.values().stream().mapToInt(i -> i).sum();
    }
    public boolean hasZCrystal(ZCrystal z)
    {
        return this.zcrystals.contains(z);
    }

    public EnumSet<ZCrystal> getZCrystals()
    {
        return this.zcrystals;
    }

    public boolean hasTM(TM tm)
    {
        return this.tms.containsKey(tm);
    }

    public Map<TM, Integer> getTMs()
    {
        return this.tms;
    }

    public int getTMCount()
    {
        return this.tms.values().stream().mapToInt(i -> i).sum();
    }
}
