package com.calculusmaster.pokecord.game.pokemon;

import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonBoosts;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonDuelStatChanges;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonDuelStatOverrides;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonStats;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.cache.PlayerDataCache;
import com.calculusmaster.pokecord.util.cache.PokemonDataCache;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Pokemon
{
    //Fields
    private PokemonData data;

    private String UUID;
    private String name;
    private String nickname;
    private int number;
    private boolean shiny;
    private Gender gender;
    private Nature nature;
    private int level;
    private int exp;
    private int dynamaxLevel;
    private int prestigeLevel;
    private PokemonStats ivs;
    private PokemonStats evs;
    private List<String> moves;
    private Item item;
    private TM tm;
    private TR tr;

    //Duel Fields
    private List<Type> type;
    private List<String> abilities;
    private PokemonDuelStatChanges statChanges;
    private PokemonBoosts boosts;
    private int health;
    private EnumSet<StatusCondition> statusConditions;
    private boolean isDynamaxed;
    private PokemonDuelStatOverrides statOverrides;
    private Item consumedItem;

    //Misc
    private PokemonRarity.Rarity rarity;

    //Private Constructor
    private Pokemon() {}

    //Factory Creator and Builder
    public static Pokemon create(String name)
    {
        Pokemon p = new Pokemon();

        p.setData(name);
        p.setUUID();
        p.setName(name);
        p.setNickname("");
        p.setNumber(-1);
        p.setShiny();
        p.setGender();
        p.setNature();
        p.setLevel(1);
        p.setDynamaxLevel(0);
        p.setPrestigeLevel(0);
        p.setExp(0);
        p.setIVs();
        p.setEVs();
        p.setMoves();
        p.setItem(Item.NONE);
        p.setTM();
        p.setTR();

        p.setDuelDefaults();
        p.setupMisc();

        return p;
    }

    private static Pokemon build(Document data, int number)
    {
        Pokemon p = new Pokemon();

        p.setData(data.getString("name"));
        p.setUUID(data.getString("UUID"));
        p.setName(data.getString("name"));
        p.setNickname(data.getString("nickname"));
        p.setNumber(number);
        p.setShiny(data.getBoolean("shiny"));
        p.setGender(Gender.cast(data.getString("gender")));
        p.setNature(Nature.cast(data.getString("nature")));
        p.setLevel(data.getInteger("level"));
        p.setDynamaxLevel(data.getInteger("dynamaxLevel"));
        p.setPrestigeLevel(data.getInteger("prestigeLevel"));
        p.setExp(data.getInteger("exp"));
        p.setIVs(data.get("ivs", Document.class));
        p.setEVs(data.get("evs", Document.class));
        p.setMoves(data.getList("moves", String.class));
        p.setItem(Item.cast(data.getString("item")));
        p.setTM(data.getInteger("tm") == -1 ? null : TM.get(data.getInteger("tm")));
        p.setTR(data.getInteger("tr") == -1 ? null : TR.get(data.getInteger("tr")));

        p.setDuelDefaults();
        p.setupMisc();

        return p;
    }

    private void setDuelDefaults()
    {
        this.type = new ArrayList<>(List.copyOf(this.data.types));
        this.abilities = new ArrayList<>(List.copyOf(this.data.abilities));
        this.statChanges = new PokemonDuelStatChanges();
        this.boosts = new PokemonBoosts();
        this.health = this.getMaxHealth();
        this.statusConditions = EnumSet.noneOf(StatusCondition.class);
        this.isDynamaxed = false;
        this.statOverrides = new PokemonDuelStatOverrides();
        this.consumedItem = Item.NONE;
    }

    private void setupMisc()
    {
        this.rarity = PokemonRarity.POKEMON_RARITIES.get(this.name);
    }

    public static Pokemon build(String UUID)
    {
        return Pokemon.build(UUID, -1);
    }

    public static Pokemon build(String UUID, int number)
    {
        Document cache = PokemonDataCache.getCache(UUID);

        //This usually means that the data wasn't cached on bot initialization
        if(cache == null)
        {
            cache = Mongo.PokemonData.find(Filters.eq("UUID", UUID)).first();
            PokemonDataCache.addCacheData(UUID, cache);

            //This most likely means the UUID isn't in the database
            if(cache == null) LoggerHelper.error(com.calculusmaster.pokecord.game.pokemon.Pokemon.class, "UUID {%s} not found in Database!".formatted(UUID));
        }

        return cache == null ? null : Pokemon.build(PokemonDataCache.getCache(UUID), number);
    }

    //Querying for the Player Owner of this Pokemon; if no owner (AI Pokemon), returns null
    public PlayerDataQuery getOwner()
    {
        String poke = "{Name: %s, UUID: %s}".formatted(this.name, this.UUID);
        LoggerHelper.info(Pokemon.class, "Searching for Player Owner of " + poke);

        PlayerDataQuery out =  PlayerDataCache.CACHE.values().stream().map(PlayerDataCache::data).filter(data -> data.getPokemonList().contains(this.getUUID())).findFirst().orElse(null);

        if(out == null) LoggerHelper.warn(Pokemon.class, "Unable to find Player Owner of " + poke);

        return out;
    }

    //Database
    private Document buildDatabaseDocument()
    {
        return new Document()
                .append("UUID", this.UUID)
                .append("name", this.name)
                .append("nickname", this.nickname)
                .append("shiny", this.shiny)
                .append("gender", this.gender.toString())
                .append("nature", this.nature.toString())
                .append("level", this.level)
                .append("exp", this.exp)
                .append("dynamaxLevel", this.dynamaxLevel)
                .append("prestigeLevel", this.prestigeLevel)
                .append("ivs", this.ivs.serialized())
                .append("evs", this.evs.serialized())
                .append("moves", this.moves)
                .append("item", this.item.toString())
                .append("tm", this.tm == null ? -1 : this.tm.getNumber())
                .append("tr", this.tr == null ? -1 : this.tr.getNumber());
    }

    public void upload()
    {
        Document data = this.buildDatabaseDocument();

        LoggerHelper.logDatabaseInsert(Pokemon.class, data);

        Mongo.PokemonData.insertOne(data);

        PokemonDataCache.addCacheData(this.getUUID(), data);
    }

    public void delete()
    {
        Mongo.PokemonData.deleteOne(this.query());
    }

    public void completeUpdate()
    {
        Mongo.PokemonData.replaceOne(this.query(), this.buildDatabaseDocument());

        PokemonDataCache.updateCache(this.getUUID());
    }

    private void update(Bson... updates)
    {
        LoggerHelper.logDatabaseUpdate(Pokemon.class, updates);

        Mongo.PokemonData.updateOne(this.query(), List.of(updates));

        PokemonDataCache.updateCache(this.getUUID());
    }

    public void updateName()
    {
        this.update(Updates.set("name", this.name));
    }

    public void updateNickname()
    {
        this.update(Updates.set("nickname", this.nickname));
    }

    public void updateNature()
    {
        this.update(Updates.set("nature", this.nature));
    }

    public void updateExperience()
    {
        this.update(Updates.set("level", this.level), Updates.set("exp", this.exp));
    }

    public void updateDynamaxLevel()
    {
        this.update(Updates.set("dynamaxLevel", this.dynamaxLevel));
    }

    public void updatePrestigeLevel()
    {
        this.update(Updates.set("prestigeLevel", this.prestigeLevel));
    }

    public void updateEVs()
    {
        this.update(Updates.set("evs", this.evs.serialized()));
    }

    public void updateIVs()
    {
        this.update(Updates.set("ivs", this.ivs.serialized()));
    }

    public void updateMoves()
    {
        this.update(Updates.set("moves", this.moves));
    }

    public void updateItem()
    {
        this.update(Updates.set("item", this.item.toString()));
    }

    public void updateTMTR()
    {
        this.update(Updates.set("tm", this.tm == null ? -1 : this.tm.getNumber()), Updates.set("tr", this.tr == null ? -1 : this.tr.getNumber()));
    }

    //Mastery
    public boolean isMastered()
    {
        //Level must be 100
        if(this.getLevel() < 100) return false;

        //EVs must be maxed out
        else if(this.getEVTotal() < 510) return false;

        //Must have 4 moves that aren't Tackle
        else if(this.moves.stream().anyMatch(s -> s.equals("Tackle"))) return false;

        //Dynamax Level must be 10
        else if(this.getDynamaxLevel() < 10) return false;

        //Must have an Item
        else if(this.getItem().equals(Item.NONE)) return false;

        //If checks have been passed, the Pokemon has been Mastered
        else return true;
    }

    //Other

    public void addStatusCondition(StatusCondition s)
    {
        List<Predicate<Pokemon>> immunities = List.of(
                p -> p.isType(Type.ELECTRIC) && s.equals(StatusCondition.PARALYZED),
                p -> p.isType(Type.ICE) && s.equals(StatusCondition.FROZEN),
                p -> p.isType(Type.FIRE) && s.equals(StatusCondition.BURNED),
                p -> (p.isType(Type.POISON) || p.isType(Type.STEEL)) && s.equals(StatusCondition.POISONED)
        );

        if(immunities.stream().noneMatch(p -> p.test(this))) this.statusConditions.add(s);
    }

    public void removeStatusCondition(StatusCondition s)
    {
        this.statusConditions.remove(s);
    }

    public boolean hasStatusCondition(StatusCondition s)
    {
        return this.statusConditions.contains(s);
    }

    public boolean hasAnyStatusCondition()
    {
        return !this.statusConditions.isEmpty();
    }

    public void clearStatusConditions()
    {
        this.statusConditions.clear();
    }

    public void clearStatusConditions(StatusCondition... conditions)
    {
        Arrays.stream(conditions).forEach(this::removeStatusCondition);
    }

    public EnumSet<StatusCondition> getStatusConditions()
    {
        return this.statusConditions;
    }

    public String getActiveStatusConditions()
    {
        List<String> active = new ArrayList<>();
        for(StatusCondition s : this.statusConditions) active.add(s.getAbbrev());

        if(active.isEmpty()) return "";

        StringBuilder s = new StringBuilder().append("(");
        for (String str : active) s.append(str).append(", ");
        s.deleteCharAt(s.length() - 1).deleteCharAt(s.length() - 1).append(")");

        return s.toString();
    }

    public PokemonDuelStatOverrides overrides()
    {
        return this.statOverrides;
    }

    public PokemonDuelStatChanges changes()
    {
        return this.statChanges;
    }

    public PokemonBoosts getBoosts()
    {
        return this.boosts;
    }

    public int getBaseStat(Stat s)
    {
        return this.data.baseStats.get(s);
    }

    public int getStat(Stat s)
    {
        if(this.statOverrides != null && this.statOverrides.has(s)) return this.statOverrides.get(s);

        double masteredBoost = this.isMastered() ? 1.05 : 1.0;
        double prestigeBoost = this.getPrestigeBonus(s);

        double commonBoosts = masteredBoost * prestigeBoost;

        if(s.equals(Stat.HP))
        {
            //HP = Level + 10 + [((2 * Base + IV + EV / 4) * Level) / 100]
            double base = this.getBaseStat(Stat.HP);
            int IV = this.ivs.get(Stat.HP);
            int EV = this.evs.get(Stat.HP);
            double maxHP = this.level + 10 + ((this.level * (2 * base + IV + EV / 4.0)) / 100);

            double dynamaxBoost = this.isDynamaxed ? 1.0 + (this.getDynamaxLevel() * 0.05 + 0.5) : 1.0;
            double rawBoost = this.boosts.getHealthBoost();

            return (int)(maxHP * dynamaxBoost * commonBoosts * rawBoost);
        }
        else
        {
            //Stat = Nature * [5 + ((2 * Base + IV + EV / 4) * Level) / 100]
            double nature = this.nature.getMap().get(s);
            double base = this.getBaseStat(s);
            int IV = this.ivs.get(s);
            int EV = this.evs.get(s);
            double stat = nature * (5 + ((this.level * (2 * base + IV + EV / 4.0)) / 100));

            double modifier = this.statChanges.getModifier(s);
            double rawBoost = this.boosts.getStatBoost();

            return (int)(stat * modifier * commonBoosts * rawBoost);
        }
    }

    public int getMaxHealth()
    {
        return this.getStat(Stat.HP);
    }

    public int getMaxHealth(double fraction)
    {
        return (int)(this.getMaxHealth() * fraction);
    }

    public int getTotalStat()
    {
        return Arrays.stream(Stat.values()).mapToInt(this::getStat).sum();
    }

    public void heal(int amount)
    {
        if(!this.isFainted()) this.setHealth(Math.min(this.health + amount, this.getMaxHealth()));
    }

    public void damage(int amount)
    {
        this.setHealth(Math.max(0, this.health - amount));
    }

    public boolean isFainted()
    {
        return this.health <= 0;
    }

    public void setHealth(int health)
    {
        this.health = health;
    }

    public int getHealth()
    {
        return this.health;
    }

    public boolean isDynamaxed()
    {
        return this.isDynamaxed;
    }

    public void enterDynamax()
    {
        double healthRatio = (double)this.getHealth() / this.getStat(Stat.HP);

        this.isDynamaxed = true;

        this.setHealth((int)(healthRatio * this.getStat(Stat.HP)));
    }

    public void exitDynamax()
    {
        double healthRatio = (double)this.getHealth() / this.getStat(Stat.HP);

        this.isDynamaxed = false;

        this.setHealth((int)(healthRatio * this.getStat(Stat.HP)));
    }

    public double getWeight()
    {
        return this.data.weight;
    }

    public boolean hasMega()
    {
        return !this.data.megas.isEmpty();
    }

    public boolean hasForms()
    {
        return !this.data.forms.isEmpty();
    }

    public List<String> getMegaList()
    {
        return this.data.megas;
    }

    public List<String> getFormsList()
    {
        return this.data.forms;
    }

    public void changeForm(String form)
    {
        form = Global.normalize(form);
        this.setData(form);
        this.name = form;
    }

    public void evolve(String evolution)
    {
        this.changeForm(evolution);
    }

    public String getImage()
    {
        if (this.getName().equals("Deerling")) return Global.getDeerlingImage(this.isShiny());
        else if (this.getName().equals("Sawsbuck")) return Global.getSawsbuckImage(this.isShiny());

        //TODO: Basic Dynamax Image
        if (this.isDynamaxed && this.canGigantamax())
            return this.isShiny() ? DataHelper.getGigantamaxData(this.getName()).shinyImage() : DataHelper.getGigantamaxData(this.getName()).normalImage();

        String image = this.isShiny() ? this.data.shinyURL : this.data.normalURL;
        return image.equals("") ? com.calculusmaster.pokecord.game.pokemon.Pokemon.getWIPImage() : image;
    }

    public static String getWIPImage()
    {
        return "http://clipart-library.com/img/1657818.png";
    }

    public boolean canGigantamax()
    {
        return DataHelper.hasGigantamax(this.getName());
    }

    public DataHelper.GigantamaxData getGigantamaxData()
    {
        return DataHelper.GIGANTAMAX_DATA.get(this.getName());
    }

    public List<String> getAbilities()
    {
        return this.abilities;
    }

    public void addAbility(String ability)
    {
        this.abilities.add(ability);
    }

    public void clearAbilities()
    {
        this.abilities.clear();
    }

    public void setAbilities(List<String> abilities)
    {
        this.abilities = new ArrayList<>(List.copyOf(abilities));
    }

    //Type

    public List<Type> getType()
    {
        return this.type;
    }

    //Replaces given type with newType
    public void replaceType(Type current, Type replacement)
    {
        this.type.set(this.type.indexOf(current), replacement);
    }

    //Remove Types
    public void removeType(Type remove)
    {
        this.replaceType(remove, Type.NORMAL);
    }

    //Replaces all types with the parameter
    public void setType(Type t)
    {
        this.setType(List.of(t));
    }

    //Replaces all types with the parameter types
    public void setType(List<Type> t)
    {
        this.type = new ArrayList<>(t);
    }

    //Adds a third typing to the Pokemon (Forest's Curse and Trick-or-Treat)
    public void addType(Type t)
    {
       if(this.type.size() == 3) this.type.set(2, t);
       else this.type.add(t);
    }

    public boolean isType(Type t)
    {
        return this.type.contains(t);
    }

    //TM & TR

    public boolean canLearnTM(TM tm)
    {
        return this.data.validTMs.contains(tm);
    }

    public boolean canLearnTR(TR tr)
    {
        return this.data.validTRs.contains(tr);
    }

    public boolean hasTM()
    {
        return this.tm != null;
    }

    public boolean hasTR()
    {
        return this.tr != null;
    }

    public void setTM()
    {
        this.tm = null;
    }

    public void setTR()
    {
        this.tr = null;
    }

    public void setTM(TM tm)
    {
        this.tm = tm;
    }

    public void setTR(TR tr)
    {
        this.tr = tr;
    }

    public TM getTM()
    {
        return this.tm;
    }

    public TR getTR()
    {
        return this.tr;
    }

    //Item
    public boolean hasItem()
    {
        return !this.item.equals(Item.NONE);
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public void removeItem()
    {
        if(this.item.isConsumable()) this.consumedItem = this.item;
        this.item = Item.NONE;
    }

    public boolean hasConsumedItem()
    {
        return !this.consumedItem.equals(Item.NONE);
    }

    public Item getConsumedItem()
    {
        return this.consumedItem;
    }

    public void restoreItem()
    {
        if(this.hasConsumedItem())
        {
            this.item = this.consumedItem;
            this.consumedItem = Item.NONE;
        }
    }

    public boolean hasItem(Item item)
    {
        return this.item.equals(item);
    }

    public Item getItem()
    {
        return this.item;
    }

    //Moves

    public List<String> allMoves()
    {
        List<String> all = new ArrayList<>(List.copyOf(this.data.moves.keySet()));
        all.sort(Comparator.comparingInt(s -> this.data.moves.get(s)));

        return this.withMovesOverride(all);
    }

    public List<String> availableMoves()
    {
        List<String> available = new ArrayList<>(this.allMoves().stream().filter(s -> this.data.moves.get(s) <= this.getLevel()).sorted(Comparator.comparingInt(s -> this.data.moves.get(s))).toList());

        if(this.hasTM()) available.add(this.tm.getMoveName());
        if(this.hasTR()) available.add(this.tr.getMoveName());

        return this.withMovesOverride(available);
    }

    private List<String> withMovesOverride(List<String> input)
    {
        if(this.getName().contains("Zygarde") && this.item.equals(Item.ZYGARDE_CUBE))
        {
            Collections.addAll(input, "Core Enforcer", "Dragon Dance", "Extreme Speed", "Thousand Arrows", "Thousand Waves");
        }

        return input;
    }

    public void learnMove(String move, int index)
    {
        this.moves.set(index, move);
    }

    public void setMoves()
    {
        this.setMoves(List.of("Tackle", "Tackle", "Tackle", "Tackle"));
    }

    public void setMoves(List<String> moves)
    {
        this.moves = new ArrayList<>(moves);
    }

    public List<String> getMoves()
    {
        return this.moves;
    }

    public Move getMove(int index)
    {
        return new Move(this.moves.get(index));
    }

    //IVs & EVs

    public void setIVs(Document data)
    {
        this.ivs = new PokemonStats(data);
    }

    public void setIVs(int min)
    {
        do this.setIVs();
        while(this.getTotalIVRounded() < min);
    }

    public void setEVs(Document data)
    {
        this.evs = new PokemonStats(data);
    }

    public void setEVs(Map<Stat, Integer> evs)
    {
        evs.forEach((key, value) -> this.evs.set(key, value));
    }

    public void setIVs()
    {
        this.ivs = new PokemonStats();
        Arrays.stream(Stat.values()).forEach(s -> this.ivs.set(s, new SplittableRandom().nextInt(31) + 1));
    }

    public void setIVs(Map<Stat, Integer> ivs)
    {
        ivs.forEach((key, value) -> this.ivs.set(key, value));
    }

    public void setEVs()
    {
        this.evs = new PokemonStats();
    }

    public void setIV(Stat s, int IV)
    {
        this.ivs.set(s, IV);
    }

    public void setEV(Stat s, int EV)
    {
        this.evs.set(s, EV);
    }

    public void addEVs(Stat stat, int amount)
    {
        if(this.getEVTotal() < 510 && this.getEVs().get(stat) + amount <= 252) this.evs.increase(stat, amount);
    }

    public void gainEVs(Pokemon other)
    {
        for(Stat s : other.getEVYield().keySet()) this.addEVs(s, other.getEVYield().get(s));
    }

    public int getEVTotal()
    {
        return this.getEVs().values().stream().mapToInt(i -> i).sum();
    }

    public String getTotalIV()
    {
        return String.format("%.2f", this.getIVs().values().stream().mapToDouble(iv -> iv / 31D).sum() * 100 / 6D) + "%";
    }

    public double getTotalIVRounded()
    {
        String iv = this.getTotalIV();
        return Double.parseDouble(iv.substring(0, iv.indexOf("%")));
    }

    public LinkedHashMap<Stat, Integer> getEVYield()
    {
        return this.data.yield.get();
    }

    public LinkedHashMap<Stat, Integer> getIVs()
    {
        return this.ivs.get();
    }

    public LinkedHashMap<Stat, Integer> getEVs()
    {
        return this.evs.get();
    }

    //Prestige Level
    public double getPrestigeBonus(Stat s)
    {
        if(!this.hasPrestiged()) return 1.0;
        else
        {
            double ratio = (double)this.getPrestigeLevel() / (double)this.getMaxPrestigeLevel();

            boolean hp = s.equals(Stat.HP);
            double maxBonus = switch(this.rarity) {
                case COPPER, SILVER, GOLD -> hp ? 0.75 : 0.50;
                case DIAMOND, PLATINUM -> hp ? 0.50 : 0.35;
                case MYTHICAL, LEGENDARY, EXTREME -> hp ? 0.30 : 0.25;
            };

            return 1.0 + (ratio * maxBonus);
        }
    }

    public int getPrestigeLevel()
    {
        return this.prestigeLevel;
    }

    public boolean hasPrestiged()
    {
        return this.prestigeLevel != 0;
    }

    public void setPrestigeLevel(int prestigeLevel)
    {
        this.prestigeLevel = prestigeLevel;
    }

    public int getMaxPrestigeLevel()
    {
        return switch(this.rarity) {
            case COPPER -> 7;
            case SILVER -> 6;
            case GOLD -> 5;
            case DIAMOND -> 4;
            case PLATINUM -> 3;
            case MYTHICAL -> 2;
            case LEGENDARY, EXTREME -> 1;
        };
    }

    public void increasePrestigeLevel()
    {
        if(this.prestigeLevel < this.getMaxPrestigeLevel()) this.prestigeLevel++;
    }

    //Dynamax Level

    public void increaseDynamaxLevel()
    {
        if(this.dynamaxLevel < 10) this.dynamaxLevel++;
    }

    public void setDynamaxLevel(int dynamaxLevel)
    {
        this.dynamaxLevel = dynamaxLevel;
    }

    public int getDynamaxLevel()
    {
        return this.dynamaxLevel;
    }

    //Level & Experience

    public void addExp(int exp)
    {
        this.exp += exp;

        int req = GrowthRate.getRequiredExp(this.data.growthRate, this.level);

        while(this.exp >= req && this.level < 100)
        {
            this.level++;
            this.exp -= req;

            req = GrowthRate.getRequiredExp(this.data.growthRate, this.level);
        }
    }

    public int getDefeatExp(Pokemon other)
    {
        int a = 1;
        int b = this.data.baseEXP;
        int e = 1;
        int L = other.getLevel();
        int Lp = this.getLevel();
        int p = 1;
        int s = 1;
        int t = 1;

        return (int) (t * e * p * (1 + (a * b * L / (5.0 * s)) * (Math.pow(2 * L + 10, 2.5) / Math.pow(L + Lp + 10, 2.5))));
    }

    public void setExp(int exp)
    {
        this.exp = exp;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getExp()
    {
        return this.exp;
    }

    public int getLevel()
    {
        return this.level;
    }

    //Nature
    public void setNature()
    {
        this.setNature(Nature.values()[new SplittableRandom().nextInt(Nature.values().length)]);
    }

    public void setNature(Nature nature)
    {
        this.nature = nature;
    }

    public Nature getNature()
    {
        return this.nature;
    }

    //Gender
    public void setGender()
    {
        this.setGender(this.data.genderRate == -1 ? Gender.UNKNOWN : (new SplittableRandom().nextInt(8) < this.data.genderRate ? Gender.FEMALE : Gender.MALE));
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
    }

    public Gender getGender()
    {
        return this.gender;
    }

    public EnumSet<EggGroup> getEggGroups()
    {
        return EnumSet.copyOf(this.data.eggGroups);
    }

    //Shiny
    public void setShiny()
    {
        this.setShiny(new SplittableRandom().nextInt(4096) < 1);
    }

    public void setShiny(boolean shiny)
    {
        this.shiny = shiny;
    }

    public boolean isShiny()
    {
        return this.shiny;
    }

    //Number
    public void setNumber(int number)
    {
        this.number = number;
    }

    public int getNumber()
    {
        return this.number;
    }

    //Nickname
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getNickname()
    {
        return this.nickname;
    }

    public boolean hasNickname()
    {
        return !this.nickname.equals("");
    }

    public String getDisplayName()
    {
        return this.hasNickname() ? "\"" + this.getNickname() + "\"" : this.getNameWithOverride();
    }

    //Name

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public String getNameWithOverride()
    {
        return switch(this.getName()) {
            case "Ho Oh" -> "Ho-oh";
            case "Porygon Z" -> "Porygon-Z";
            case "Jangmo O" -> "Jangmo-o";
            case "Hakamo O" -> "Hakamo-o";
            case "Kommo O" -> "Kommo-o";
            case "Mr Mime" -> "Mr. Mime";
            case "Mime Jr" -> "Mime Jr.";
            case "Galarian Mr Mime" -> "Galarian Mr. Mime";
            case "Mr Rime" -> "Mr. Rime";
            default -> this.getName();
        };
    }

    //UUID

    public void setUUID()
    {
        this.setUUID(IntStream.rangeClosed(1, 6).mapToObj(i -> IDHelper.alphanumeric(4)).collect(Collectors.joining("-")));
    }

    public void setUUID(String UUID)
    {
        this.UUID = UUID;
    }

    public String getUUID()
    {
        return this.UUID;
    }

    //Data

    public void setData(String name)
    {
        this.data = PokemonData.get(name);
    }

    public PokemonData getData()
    {
        return this.data;
    }

    //Core

    private Bson query()
    {
        return Filters.eq("UUID", this.UUID);
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        else if(o == null || this.getClass() != o.getClass()) return false;
        else return this.UUID.equals(((Pokemon)o).UUID);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.UUID);
    }
}
