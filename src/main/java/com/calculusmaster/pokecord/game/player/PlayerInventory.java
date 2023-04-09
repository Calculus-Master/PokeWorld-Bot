package com.calculusmaster.pokecord.game.player;

import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import org.bson.Document;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerInventory
{
    private final Map<Item, Integer> items;
    private final EnumSet<ZCrystal> zcrystals;
    private final Map<TM, Integer> tms;

    public PlayerInventory()
    {
        this.items = new LinkedHashMap<>();
        this.zcrystals = EnumSet.noneOf(ZCrystal.class);
        this.tms = new LinkedHashMap<>();
    }

    public PlayerInventory(Document data)
    {
        this();

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
    }

    public void removeItem(Item item)
    {
        int count = this.items.getOrDefault(item, 0);

        if(count - 1 == 0) this.items.remove(item);
        else if(count - 1 > 0) this.items.put(item, count - 1);
    }

    public void addZCrystal(ZCrystal crystal)
    {
        this.zcrystals.add(crystal);
    }

    public void addTM(TM tm)
    {
        this.tms.put(tm, this.tms.getOrDefault(tm, 0) + 1);
    }

    public void removeTM(TM tm)
    {
        int count = this.tms.getOrDefault(tm, 0);

        if(count - 1 == 0) this.tms.remove(tm);
        else if(count - 1 > 0) this.tms.put(tm, count - 1);
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
