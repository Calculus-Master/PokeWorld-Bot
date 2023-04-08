package com.calculusmaster.pokecord.game.world;

import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.ItemType;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.util.enums.Prices;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PokeWorldShop
{
    private static PokeWorldShop CURRENT;

    //Bounds for Shop Generation
    public static int[] ITEM_COUNTS;
    public static int[] TM_COUNTS;
    public static int[] Z_CRYSTAL_COUNTS;

    public static void init()
    {
        Document data = Mongo.MiscData.find(Filters.eq("type", "shop")).first();

        if(data == null)
        {
            Mongo.MiscData.insertOne(new Document("type", "shop"));

            CURRENT = new PokeWorldShop();

            CURRENT.generate();
            CURRENT.update();
        }
        else CURRENT = new PokeWorldShop(data);
    }

    public static PokeWorldShop getCurrentShop()
    {
        return CURRENT;
    }

    public static void refresh()
    {
        CURRENT.generate();
        CURRENT.update();
    }

    //Class

    private List<Item> items;

    private List<TM> tms;
    private int tmPrice;

    private List<ZCrystal> zCrystals;
    private int zCrystalPrice;

    public PokeWorldShop()
    {
        this.items = new ArrayList<>();
        this.tms = new ArrayList<>();
        this.tmPrice = -1;
        this.zCrystals = new ArrayList<>();
        this.zCrystalPrice = -1;
    }

    public PokeWorldShop(Document data)
    {
        this();

        this.items = data.getList("items", String.class).stream().map(Item::valueOf).toList();

        this.tms = data.get("tms", Document.class).getList("contents", String.class).stream().map(TM::valueOf).toList();
        this.tmPrice = data.get("tms", Document.class).getInteger("price");

        this.zCrystals = data.get("z_crystals", Document.class).getList("contents", String.class).stream().map(ZCrystal::valueOf).toList();
        this.zCrystalPrice = data.get("z_crystals", Document.class).getInteger("price");
    }

    private void update()
    {
        Document data = new Document("type", "shop")
                .append("items", this.items.stream().map(Item::toString).toList())
                .append("tms", new Document("contents", this.tms.stream().map(TM::toString).toList())
                        .append("price", this.tmPrice))
                .append("z_crystals", new Document("contents", this.zCrystals.stream().map(ZCrystal::toString).toList())
                        .append("price", this.zCrystalPrice));

        Mongo.MiscData.replaceOne(Filters.eq("type", "shop"), data);
    }

    private void generate()
    {
        Random r = new Random();

        //Items
        this.items.clear(); //TODO: Better Item Generation
        int countItem = ITEM_COUNTS[0] + r.nextInt(ITEM_COUNTS[1] - ITEM_COUNTS[0] + 1);
        List<Item> poolOtherItems = new ArrayList<>(List.copyOf(Item.getAllNot(ItemType.DRIVE, ItemType.MEMORY, ItemType.PLATE)));
        List<Item> poolTypeItems = new ArrayList<>(List.copyOf(Item.getAll(ItemType.DRIVE, ItemType.MEMORY, ItemType.PLATE)));

        if(r.nextBoolean() || r.nextBoolean())
        {
            poolOtherItems.remove(Item.EVERSTONE);
            this.items.add(Item.EVERSTONE);
        }

        IntStream.range(0, 3).forEach(i -> this.items.add(poolTypeItems.remove(r.nextInt(poolTypeItems.size()))));
        IntStream.range(0, countItem - this.items.size()).forEach(i -> this.items.add(poolOtherItems.remove(r.nextInt(poolOtherItems.size()))));

        //TMs
        this.tms.clear();
        int countTM = TM_COUNTS[0] + r.nextInt(TM_COUNTS[1] - TM_COUNTS[0] + 1);
        List<Integer> poolTM = IntStream.range(0, TM.values().length).boxed().collect(Collectors.toList());
        IntStream.range(0, countTM).forEach(i -> this.tms.add(TM.values()[poolTM.remove(r.nextInt(poolTM.size()))]));

        this.tmPrice = Prices.SHOP_BASE_TM.get() + r.nextInt(Prices.SHOP_RANDOM_TM.get());
        this.tmPrice = this.tmPrice - this.tmPrice % 10;

        //Z-Crystals
        this.zCrystals.clear();
        int countZCrystal = Z_CRYSTAL_COUNTS[0] + r.nextInt(Z_CRYSTAL_COUNTS[1] - Z_CRYSTAL_COUNTS[0] + 1);
        List<Integer> poolZCrystal = IntStream.range(ZCrystal.WATERIUM_Z.ordinal() + 1, ZCrystal.values().length).boxed().collect(Collectors.toList());
        IntStream.range(0, countZCrystal).forEach(i -> this.zCrystals.add(ZCrystal.values()[poolZCrystal.remove(r.nextInt(poolZCrystal.size()))]));

        this.zCrystalPrice = Prices.SHOP_ZCRYSTAL.get();

        LoggerHelper.info(PokeWorldShop.class, "New PokeWorldShop generated! Shop: " + this);
    }

    @Override
    public String toString()
    {
        return "PokeWorldShop{" +
                "items=[" + this.items.stream().map(Enum::toString).collect(Collectors.joining(",")) +
                "], tms=[" + this.tms.stream().map(Enum::toString).collect(Collectors.joining(",")) +
                "], tmPrice=" + this.tmPrice +
                ", zCrystals=[" + this.zCrystals.stream().map(Enum::toString).collect(Collectors.joining(",")) +
                "], zCrystalPrice=" + this.zCrystalPrice +
                '}';
    }

    public List<Item> getItems()
    {
        return this.items;
    }

    public int getItemPrice(Item item)
    {
        return item.getCost(); //TODO: This method exists just in case we want to add shop price modifiers or something
    }

    public List<TM> getTMs()
    {
        return this.tms;
    }

    public int getTMPrice()
    {
        return this.tmPrice;
    }

    public List<ZCrystal> getZCrystals()
    {
        return this.zCrystals;
    }

    public int getZCrystalPrice()
    {
        return this.zCrystalPrice;
    }
}
