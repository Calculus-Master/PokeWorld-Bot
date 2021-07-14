package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.PokemonRarity;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class Pokemon
{
    private JSONObject genericJSON;
    protected JSONObject specificJSON;

    private String UUID;
    private int num;
    private boolean shiny;
    private Nature nature;
    private int level;
    private int exp;
    private Map<Stat, Integer> IV = new TreeMap<>();
    private Map<Stat, Integer> EV = new TreeMap<>();
    private List<String> learnedMoves = new ArrayList<>();
    private int heldTM;
    private int heldTR;
    private String heldItem;
    private int dynamaxLevel;
    private String nickname;

    private int health;
    private Type[] type;
    private Map<StatusCondition, Boolean> status;
    private Map<Stat, Integer> statMultiplier = new TreeMap<>();
    public double statBuff;
    private boolean isDynamaxed;

    //Init Global List
    public static void init()
    {
        Mongo.PokemonInfo.find(Filters.exists("name")).forEach(d -> Global.POKEMON.add(d.getString("name")));
    }

    //Constructors
    public static Pokemon build(String UUID)
    {
        Pokemon p = new Pokemon();
        p.setUUID(UUID);
        p.setNumber(-1);

        p.linkSpecificJSON(UUID);
        p.linkGenericJSON(p.getSpecificJSON().getString("name"));

        JSONObject specific = p.getSpecificJSON();

        p.setLevel(specific.getInt("level"));
        p.setExp(specific.getInt("exp"));
        p.setShiny(specific.getBoolean("shiny"));
        p.setIVs(specific.getString("ivs"));
        p.setEVs(specific.getString("evs"));
        p.setNature(specific.getString("nature"));
        p.setLearnedMoves(specific.getString("moves"));
        p.setTM(specific.getInt("tm"));
        p.setTR(specific.getInt("tr"));
        p.setItem(specific.has("item") ? specific.getString("item") : PokeItem.NONE.getName());
        p.setDynamaxLevel(specific.getInt("dynamax_level"));
        p.setNickname(specific.getString("nickname"));

        p.setHealth(p.getStat(Stat.HP));
        p.setType();
        p.setStatusConditions();
        p.setDefaultStatMultipliers();
        p.statBuff = 1.0;
        p.setDynamax(false);

        LoggerHelper.info(Pokemon.class, "Pokemon Built: UUID (" + UUID + "), NAME (" + p.getName() + ")");
        return p;
    }

    public static Pokemon create(String name)
    {
        Pokemon p = new Pokemon();
        p.setUUID();
        p.setNumber(-1);

        p.linkGenericJSON(Global.normalCase(name));

        p.setLevel(1);
        p.setExp(0);
        p.setShiny(new Random().nextInt(4096) == 1);
        p.setIVs();
        p.setEVs();
        p.setNature();
        p.setLearnedMoves("Tackle-Tackle-Tackle-Tackle");
        p.setTM(-1);
        p.setTR(-1);
        p.setItem(PokeItem.NONE);
        p.setDynamaxLevel(0);
        p.setNickname("");

        p.setHealth(p.getStat(Stat.HP));
        p.setType();
        p.setStatusConditions();
        p.setDefaultStatMultipliers();
        p.statBuff = 1.0;
        p.setDynamax(false);

        LoggerHelper.info(Pokemon.class, "Pokemon Created: UUID (" + p.getUUID() + "), NAME (" + name + ")");
        return p;
    }

    //Builder for minimal access
    //WILL CRASH IF ANY OTHER GETTERS ARE USED
    public static Pokemon buildCore(String UUID, int num)
    {
        Document d = Mongo.PokemonData.find(Filters.eq("UUID", UUID)).first();
        if(d == null) return null;

        Pokemon p = new Pokemon()
        {
            @Override
            public String getUUID() {
                return UUID;
            }

            @Override
            public int getLevel() {
                return d.getInteger("level");
            }

            @Override
            public String getName() {
                return d.getString("name");
            }

            @Override
            public boolean isShiny() {
                return d.getBoolean("shiny");
            }

            @Override
            public String getNickname() {
                return d.getString("nickname");
            }

            @Override
            public void linkSpecificJSON(String UUID) {
                this.specificJSON = new JSONObject(d.toJson());
            }
        };

        p.linkGenericJSON(p.getName());
        p.linkSpecificJSON(UUID);

        p.setNumber(num + 1);

        p.setIVs(d.getString("ivs"));
        p.setType();
        p.setItem(d.getString("item"));

        return p;
    }

    public int getNumber()
    {
        return this.num;
    }

    public void setNumber(int n)
    {
        this.num = n;
    }

    //JSONs
    public void linkGenericJSON(String name)
    {
        this.genericJSON = Pokemon.genericJSON(name);
    }

    public void linkSpecificJSON(String UUID)
    {
        this.specificJSON = Pokemon.specificJSON(UUID);
    }

    public static JSONObject genericJSON(String name)
    {
        return new JSONObject(Mongo.PokemonInfo.find(Filters.eq("name", Global.normalCase(name))).first().toJson());
    }

    public static JSONObject specificJSON(String UUID)
    {
        return new JSONObject(Mongo.PokemonData.find(Filters.eq("UUID", UUID)).first().toJson());
    }

    public static void uploadPokemon(Pokemon p)
    {
        Pokemon.deletePokemon(p);

        Document pokeData = new Document()
                .append("UUID", p.getUUID())
                .append("name", p.getName())
                .append("shiny", p.isShiny())
                .append("level", p.getLevel())
                .append("exp", p.getExp())
                .append("nature", p.getNature().toString())
                .append("ivs", p.getVCondensed(p.getIVs()))
                .append("evs", p.getVCondensed(p.getEVs()))
                .append("moves", p.getMovesCondensed())
                .append("tm", p.getTM())
                .append("tr", p.getTR())
                .append("item", p.getItem())
                .append("dynamax_level", p.getDynamaxLevel())
                .append("nickname", p.getNickname());

        Mongo.PokemonData.insertOne(pokeData);

        LoggerHelper.info(Pokemon.class, "Pokemon Uploaded: UUID (" + p.getUUID() + "), NAME (" + p.getName() + ")");
    }

    private static void update(Pokemon p, Bson... update)
    {
        for(Bson u : update) Mongo.PokemonData.updateOne(p.getQuery(), u);

        CacheHelper.updatePokemon(p.getUUID());
    }

    public static void updateExperience(Pokemon p)
    {
        update(p, Updates.set("level", p.getLevel()), Updates.set("exp", p.getExp()));
    }

    public static void updateMoves(Pokemon p)
    {
        update(p, Updates.set("moves", p.getMovesCondensed()));
    }

    public static void updateEVs(Pokemon p)
    {
        update(p, Updates.set("evs", p.getVCondensed(p.getEVs())));
    }

    public static void updateName(Pokemon p, String evolved)
    {
        update(p, Updates.set("name", Global.normalCase(evolved)));
    }

    public static void updateNickname(Pokemon p)
    {
        update(p, Updates.set("nickname", Global.normalCase(p.getNickname())));
    }

    public static void updateTMTR(Pokemon p)
    {
        update(p, Updates.set("tm", p.getTM()), Updates.set("tr", p.getTR()));
    }

    public static void updateItem(Pokemon p)
    {
        update(p, Updates.set("item", p.getItem()));
    }

    public static void updateDynamaxLevel(Pokemon p)
    {
        update(p, Updates.set("dynamax_level", p.getDynamaxLevel()));
    }

    public static void deletePokemon(Pokemon p)
    {
        Mongo.PokemonData.deleteOne(p.getQuery());
    }

    public JSONObject getGenericJSON()
    {
        return this.genericJSON;
    }

    public JSONObject getSpecificJSON()
    {
        return this.specificJSON;
    }

    public int getValue()
    {
        System.out.println(this.getName());
        PokemonRarity.Rarity rarity = PokemonRarity.POKEMON_RARITIES.get(this.getName());
        if(rarity == null) rarity = PokemonRarity.Rarity.values()[new Random().nextInt(PokemonRarity.Rarity.values().length)];

        int basePrice = switch(rarity) {
                    case COPPER -> 500;
                    case SILVER -> 1000;
                    case GOLD -> 2000;
                    case DIAMOND -> 5000;
                    case PLATINUM -> 10000;
                    case MYTHICAL -> 15000;
                    case LEGENDARY -> 20000;
                    case EXTREME -> 50000;
                };

        int numZero = 0;
        int numMax = 0;
        int numSoftMax = 0;

        for(Stat s : this.getIVs().keySet())
        {
            switch (this.getIVs().get(s))
            {
                case 0 -> numZero++;
                case 31 -> {
                    numMax++;
                    numSoftMax++;
                }
                case 30, 29, 28 -> numSoftMax++;
            }
        }

        switch (numMax)
        {
            case 6 -> basePrice *= 15;
            case 5 -> basePrice *= 10;
            case 4 -> basePrice *= 7;
            case 3 -> basePrice *= 5;
            case 2, 1 -> basePrice += (basePrice / 2);
        }

        switch(numSoftMax)
        {
            case 6 -> basePrice += 7000;
            case 5 -> basePrice += 5000;
        }

        switch(numZero)
        {
            case 6 -> basePrice *= 15;
            case 5 -> basePrice *= 10;
            case 4, 3 -> basePrice *= 5;
        }

        for(Stat s : this.getEVs().keySet()) basePrice += this.getEVs().get(s) * 100;

        if(this.getName().contains("Mega")) basePrice += 1500;

        return basePrice;
    }

    //Status Conditions

    public void addStatusCondition(StatusCondition s)
    {
        if((this.isType(Type.ELECTRIC) && s.equals(StatusCondition.PARALYZED)) ||
                (this.isType(Type.ICE) && s.equals(StatusCondition.FROZEN)) ||
                (this.isType(Type.FIRE) && s.equals(StatusCondition.BURNED)) ||
                ((this.isType(Type.POISON) || this.isType(Type.STEEL)) && s.equals(StatusCondition.POISONED))) return;
        else this.status.put(s, true);
    }

    public void removeStatusCondition(StatusCondition s)
    {
        this.status.put(s, false);
    }

    public void clearStatusConditions()
    {
        this.status.replaceAll((s, v) -> false);
    }

    public boolean hasStatusCondition(StatusCondition s)
    {
        return this.status.get(s);
    }

    public boolean hasAnyStatusCondition()
    {
        return Arrays.stream(StatusCondition.values()).noneMatch(this::hasStatusCondition);
    }

    public Map<StatusCondition, Boolean> getStatusConditionMap()
    {
        return this.status;
    }

    public void setStatusConditions()
    {
        this.status = new HashMap<>();

        for(StatusCondition sc : StatusCondition.values()) this.status.put(sc, false);
    }

    public String getActiveStatusConditions()
    {
        List<String> active = new ArrayList<>();
        for(StatusCondition s : this.status.keySet()) if(this.status.get(s)) active.add(s.getAbbrev());

        if(active.isEmpty()) return "";

        StringBuilder s = new StringBuilder().append("(");
        for(String str : active) s.append(str).append(", ");
        s.deleteCharAt(s.length() - 1).deleteCharAt(s.length() - 1).append(")");

        return s.toString();
    }

    //Health
    public void setHealth(int num)
    {
        this.health = num;
    }

    public void damage(int amount)
    {
        this.health -= amount;

        DuelHelper.instance(this.getUUID()).addDamage(amount, this.getUUID());
    }

    public void heal(int amount)
    {
        this.health = Math.min(this.getStat(Stat.HP), this.health + amount);
    }

    public int getHealth()
    {
        return this.health;
    }

    public boolean isFainted()
    {
        return this.health <= 0;
    }

    //Moves
    public List<String> getAvailableMoves()
    {
        List<String> movesList = new ArrayList<>();
        JSONArray m = this.genericJSON.getJSONArray("moves");
        JSONArray mL = this.genericJSON.getJSONArray("movesLVL");

        for(int i = 0; i < m.length(); i++) if(mL.getInt(i) <= this.getLevel()) movesList.add(m.getString(i));

        if(this.hasTM()) movesList.add(Global.normalCase(TM.get(this.heldTM).getMoveName()));
        if(this.hasTR()) movesList.add(Global.normalCase(TR.get(this.heldTR).getMoveName()));

        if(this.getName().contains("Zygarde") && this.hasItem() && PokeItem.asItem(this.getItem()).equals(PokeItem.ZYGARDE_CUBE))
        {
            Collections.addAll(movesList, "Core Enforcer", "Dragon Dance", "Extreme Speed", "Thousand Arrows", "Thousand Waves");
        }

        return movesList;
    }

    public List<String> getAllMoves()
    {
        JSONArray moves = this.genericJSON.getJSONArray("moves");
        List<String> movesList = new ArrayList<>();
        for(int i = 0; i < moves.length(); i++) movesList.add(moves.getString(i));

        if(this.getName().contains("Zygarde") && this.hasItem() && PokeItem.asItem(this.getItem()).equals(PokeItem.ZYGARDE_CUBE))
        {
            Collections.addAll(movesList, "Core Enforcer", "Dragon Dance", "Extreme Speed", "Thousand Arrows", "Thousand Waves");
        }

        return movesList;
    }

    public String getMovesCondensed()
    {
        StringBuilder s = new StringBuilder();
        this.learnedMoves.forEach(m -> s.append(m).append("-"));
        return s.deleteCharAt(s.length() - 1).toString();
    }

    public void setLearnedMoves(String moves)
    {
        for(String s : moves.split("-")) this.learnedMoves.add(Global.normalCase(s));
    }

    public List<String> getLearnedMoves()
    {
        return this.learnedMoves;
    }

    public void learnMove(String move, int replace)
    {
        if(this.getAvailableMoves().contains(move) || Move.MOVE_TUTOR_MOVES.containsKey(move)) this.learnedMoves.set(replace - 1, move);
    }

    public boolean hasTM()
    {
        return this.heldTM != -1;
    }

    public boolean hasTR()
    {
        return this.heldTR != -1;
    }

    public void setTM(int TM)
    {
        this.heldTM = TM;
    }

    public void setTR(int TR)
    {
        this.heldTR = TR;
    }

    public int getTM()
    {
        return this.heldTM;
    }

    public int getTR()
    {
        return this.heldTR;
    }

    public List<Integer> getAllValidTMs()
    {
        return this.genericJSON.getJSONArray("movesTM").toList().stream().map(i -> (int)i).collect(Collectors.toList());
    }

    public List<Integer> getAllValidTRs()
    {
        return this.genericJSON.getJSONArray("movesTR").toList().stream().map(i -> (int)i).collect(Collectors.toList());
    }

    public boolean canLearnTM(int tm)
    {
        return this.getAllValidTMs().stream().anyMatch(i -> tm == i);
    }

    public boolean canLearnTR(int tr)
    {
        return this.getAllValidTRs().stream().anyMatch(i -> tr == i);
    }

    //Items
    public void setItem(String s)
    {
        this.heldItem = s;
    }

    public void setItem(PokeItem item)
    {
        this.heldItem = item.getName();
    }

    public void removeItem()
    {
        this.heldItem = PokeItem.NONE.getName();
    }

    public String getItem()
    {
        return this.heldItem;
    }

    public boolean hasItem()
    {
        return !this.getItem().equals(PokeItem.NONE.getName());
    }

    //UUID
    private void setUUID()
    {
        StringBuilder uuid = new StringBuilder();
        for(int i = 0; i < 6; i++)
        {
            uuid.append(IDHelper.alphanumeric(4)).append("-");
        }
        this.setUUID(uuid.substring(0, uuid.toString().length() - 1));
    }

    public void setUUID(String UUID)
    {
        this.UUID = UUID;
    }

    public String getUUID()
    {
        return this.UUID;
    }

    public Bson getQuery()
    {
        return Filters.eq("UUID", this.getUUID());
    }

    //Shiny
    public void setShiny(boolean isShiny)
    {
        this.shiny = isShiny;
    }

    public boolean isShiny()
    {
        return this.shiny;
    }

    //Image
    public String getImage()
    {
        if(this.getName().equals("Deerling")) return Global.getDeerlingImage(this.isShiny());
        else if(this.getName().equals("Sawsbuck")) return Global.getSawsbuckImage(this.isShiny());

        //TODO: Basic Dynamax Image
        if(this.isDynamaxed && this.canGigantamax()) return this.isShiny() ? DataHelper.GIGANTAMAX_DATA.get(this.getName()).shinyImage() : DataHelper.GIGANTAMAX_DATA.get(this.getName()).normalImage();

        String image = this.genericJSON.getString(this.isShiny() ? "shinyURL" : "normalURL");
        return image.equals("") ? Pokemon.getWIPImage() : image;
    }

    public static String getWIPImage()
    {
        return "http://clipart-library.com/img/1657818.png";
    }

    //Levels and Experience
    public void setLevel(int level)
    {
        this.level = Math.min(level, 100);
    }

    public int getLevel()
    {
        return this.level;
    }

    public void setExp(int exp)
    {
        this.exp = exp;
    }

    public int getExp()
    {
        return this.exp;
    }

    public void addExp(int exp)
    {
        this.exp += exp;
        int required = GrowthRate.getRequiredExp(this.genericJSON.getString("growthrate"), this.level);

        while(this.exp >= required && this.level < 100)
        {
            this.level++;
            this.exp -= required;
            required = GrowthRate.getRequiredExp(this.genericJSON.getString("growthrate"), this.level);
        }
    }

    public int getDuelExp(Pokemon opponent)
    {
        int a = 1;
        int b = this.genericJSON.getInt("exp");
        int e = 1;
        int L = opponent.getLevel();
        int Lp = this.getLevel();
        int p = 1;
        int s = 1;
        int t = 1;

        return (int)(t * e * p * (1 + (a * b * L / (5.0 * s)) * (Math.pow(2 * L + 10, 2.5) / Math.pow(L + Lp + 10, 2.5))));
    }

    //Evolution
    public boolean canEvolve()
    {
        if(specialCanEvolve()) return true;
        else
        return this.genericJSON.getJSONArray("evolutionsLVL").length() != 0 && this.genericJSON.getJSONArray("evolutionsLVL").getInt(0) <= this.getLevel();
    }

    public boolean specialCanEvolve()
    {
        PokeItem item = PokeItem.asItem(this.getItem());
        //TODO: Friendship evolutions: Alolan Meowth -> Persian, Chansey -> Blissey, Golbat -> Crobat, Pichu -> Pikachu, Cleffa -> Clefairy, Igglybuff -> Jigglypuff, Togepi -> Togetic, Azurill -> Marill, Budew -> Roselia, Chingling -> Chimecho, Buneary -> Lopunny, Munchlax -> Snorlax, Riolu -> Lucario, Woobat -> Swoobat, Swadloon -> Leavanny
        //TODO: Friendship evolutions pt2: Type Null -> Silvally, Snom -> Frosmoth
        //TODO: Trade evolutions: Poliwhirl -> Politoed, Kadabra -> Alakazam, Machoke -> Machamp, Graveler -> Golem, Alolan Graveler -> Alolan Golem, Haunter -> Gengar, Boldore -> Gigalith, Gurdurr -> Conkeldurr, Phantump -> Trevenant, Pumpkaboo -> Gourgeist
        //TODO: Regional evolutions: Exeggcute -> Alolan Exeggutor w/Leaf Stone in Alola, Koffing -> Galarian Weezing in Galar
        //TODO: Other evolutions: Cubone -> Alolan Marowak in Alola Nighttime, Mantyke -> Mantine (w/Remoraid in party)
        //TODO: Eeveelutions: Espeon (High Friendship, Daytime), Umbreon (High Friendship, Nighttime), Leafeon (Mossy Rock), Glaceon (Icy Rock), Sylveon (Affection/Fairy Move)
        //TODO: Cosmoem -> Solgaleo and Cosmoem -> Lunala
        //TODO: Wurmple -> Cascoon/Silcoon
        //TODO: Magnetic Field evolutions: Magneton -> Magnezone, Nosepass -> Probopass
        //TODO: Burmy -> Mothim (Male) or Wormadam (Male) at Level 20
        //TODO: Karrablast -> Escaliver & Shelmet -> Accelgor w/Trade
        //TODO: Crabrawler -> Crabominable at Mount Lanakila
        //TODO: Galarian Yamask -> Runerigus (near Dusty Bowl)
        //TODO: Applin -> Flapple (Tart Apple), Applin -> Appletun (Sweet Apple)
        //TODO: Toxel -> Toxtricity (Amped Nature and Low Key Nature)
        //TODO: Clobbopus -> Grapploct (after Taunt learned)
        //TODO: Sinistea -> Polteageist (Cracked Pot)
        //TODO: G. Farfetchd -> Sirfetchd (3 critical hits in a battle)
        //TODO: Milcery -> Alcremie (Sweet)
        //TODO: Kubfu -> Urshifu (Tower of Darkness) and Kubfu -> Urshifu Rapid Strike (Tower of Water)

        //TODO: Forms: Castform, Zacian/Zamazenta, Wormadam, Rotom, Basculin, Darmanitan Zen (Galarian Darmanitan is added), Meowstic, Pumpkaboo, Gourgeist, Oricorio

        //Trade evolutions are converted into item based ones

        return switch (this.getName()) {
            case "Pikachu", "Charjabug", "Eelektrik" -> item.equals(PokeItem.THUNDER_STONE);
            case "Alolan Sandshrew", "Alolan Vulpix", "Galarian Darumaka" -> item.equals(PokeItem.ICE_STONE);
            case "Nidorina", "Nidorino", "Clefairy", "Jigglypuff", "Skitty", "Munna" -> item.equals(PokeItem.MOON_STONE);
            case "Vulpix", "Growlithe", "Pansear" -> item.equals(PokeItem.FIRE_STONE);
            case "Gloom" -> item.equals(PokeItem.LEAF_STONE) || item.equals(PokeItem.SUN_STONE);
            case "Weepinbell", "Exeggcute", "Nuzleaf", "Pansage" -> item.equals(PokeItem.LEAF_STONE);
            case "Shellder", "Staryu", "Lombre", "Panpour" -> item.equals(PokeItem.WATER_STONE);
            case "Eevee" -> item.equals(PokeItem.WATER_STONE) || item.equals(PokeItem.FIRE_STONE) || item.equals(PokeItem.THUNDER_STONE);
            case "Onix", "Scyther" -> item.equals(PokeItem.METAL_COAT);
            case "Haunter" -> item.equals(PokeItem.TRADE_EVOLVER);
            case "Poipole" -> this.getLearnedMoves().contains("Dragon Pulse");
            case "Poliwhirl" -> item.equals(PokeItem.WATER_STONE) || item.equals(PokeItem.KINGS_ROCK);
            case "Aipom" -> this.getLearnedMoves().contains("Double Hit");
            case "Sunkern", "Cottonee", "Petilil", "Helioptile" -> item.equals(PokeItem.SUN_STONE);
            case "Yanma", "Piloswine", "Tangela" -> this.getLearnedMoves().contains("Ancient Power");
            case "Murkrow", "Misdreavus", "Lampent", "Doublade" -> item.equals(PokeItem.DUSK_STONE);
            case "Slowpoke" -> item.equals(PokeItem.KINGS_ROCK);
            case "Galarian Slowpoke" -> item.equals(PokeItem.GALARICA_CUFF) || item.equals(PokeItem.GALARICA_WREATH);
            case "Gligar" -> item.equals(PokeItem.RAZOR_FANG);
            case "Sneasel" -> item.equals(PokeItem.RAZOR_CLAW);
            case "Seadra" -> item.equals(PokeItem.DRAGON_SCALE);
            case "Porygon" -> item.equals(PokeItem.UPGRADE);
            case "Porygon2" -> item.equals(PokeItem.DUBIOUS_DISC);
            case "Tyrogue" -> true;
            case "Kirlia", "Glalie" -> item.equals(PokeItem.DAWN_STONE);
            case "Roselia", "Togetic", "Minccino", "Floette" -> item.equals(PokeItem.SHINY_STONE);
            case "Feebas" -> item.equals(PokeItem.PRISM_SCALE);
            case "Dusclops" -> item.equals(PokeItem.REAPER_CLOTH);
            case "Clamperl" -> item.equals(PokeItem.DEEP_SEA_TOOTH) || item.equals(PokeItem.DEEP_SEA_SCALE);
            case "Bonsly", "Mime Jr" -> this.getLearnedMoves().contains("Mimic");
            case "Happiny" -> item.equals(PokeItem.OVAL_STONE);
            case "Lickitung" -> this.getLearnedMoves().contains("Rollout");
            case "Rhydon" -> item.equals(PokeItem.PROTECTOR);
            case "Electabuzz" -> item.equals(PokeItem.ELECTIRIZER);
            case "Magmar" -> item.equals(PokeItem.MAGMARIZER);
            case "Spritzee" -> item.equals(PokeItem.SACHET);
            case "Swirlix" -> item.equals(PokeItem.WHIPPED_DREAM);
            case "Steenee" -> this.getLearnedMoves().contains("Stomp");
            default -> false;
        };
    }

    public void evolve()
    {
        boolean isNormal = !this.specialCanEvolve();
        String newEvolution = "";

        PokeItem item = PokeItem.asItem(this.getItem());

        if(isNormal) newEvolution = this.genericJSON.getJSONArray("evolutions").getString(0);
        else newEvolution = switch(this.getName())
            {
                case "Pikachu" -> "Raichu";
                case "Alolan Sandshrew" -> "Alolan Sandslash";
                case "Nidorina" -> "Nidoqueen";
                case "Nidorino" -> "Nidoking";
                case "Clefairy" -> "Clefable";
                case "Vulpix" -> "Ninetales";
                case "Alolan Vulpix" -> "Alolan Ninetales";
                case "Jigglypuff" -> "Wigglytuff";
                case "Gloom" -> item.equals(PokeItem.LEAF_STONE) ? "Vileplume" : "Bellossom";
                case "Growlithe" -> "Arcanine";
                case "Weepinbell" -> "Victreebel";
                case "Shellder" -> "Cloyster";
                case "Exeggcute" -> "Exeggutor";
                case "Staryu" -> "Starmie";
                case "Eevee" -> item.equals(PokeItem.WATER_STONE) ? "Vaporeon" : (item.equals(PokeItem.THUNDER_STONE) ? "Jolteon" : "Flareon");
                case "Onix" -> "Steelix";
                case "Haunter" -> "Gengar";
                case "Charjabug" -> "Vikavolt";
                case "Poipole" -> "Naganadel";
                case "Poliwhirl" -> item.equals(PokeItem.WATER_STONE) ? "Poliwrath" : "Politoed";
                case "Aipom" -> "Ambipom";
                case "Sunkern" -> "Sunflora";
                case "Yanma" -> "Yanmega";
                case "Murkrow" -> "Honchkrow";
                case "Slowpoke" -> "Slowking";
                case "Galarian Slowpoke" -> item.equals(PokeItem.GALARICA_CUFF) ? "Galarian Slowbro" : "Galarian Slowking";
                case "Misdreavus" -> "Mismagius";
                case "Gligar" -> "Gliscor";
                case "Scyther" -> "Scizor";
                case "Sneasel" -> "Weavile";
                case "Piloswine" -> "Mamoswine";
                case "Seadra" -> "Kingdra";
                case "Porygon" -> "Porygon2";
                case "Porygon2" -> "PorygonZ";
                case "Tyrogue" -> this.getStat(Stat.ATK) == this.getStat(Stat.DEF) ? "Hitmontop" : (this.getStat(Stat.ATK) > this.getStat(Stat.DEF) ? "Hitmonlee" : "Hitmonchan");
                case "Lombre" -> "Ludicolo";
                case "Nuzleaf" -> "Shiftry";
                case "Kirlia" -> "Gallade";
                case "Skitty" -> "Delcatty";
                case "Roselia" -> "Roserade";
                case "Feebas" -> "Milotic";
                case "Dusclops" -> "Dusknoir";
                case "Snorunt" -> "Froslass";
                case "Clamperl" -> item.equals(PokeItem.DEEP_SEA_TOOTH) ? "Huntail" : "Gorebyss";
                case "Bonsly" -> "Sudowoodo";
                case "Mime Jr" -> "Mr Mime";
                case "Happiny" -> "Chansey";
                case "Lickitung" -> "Lickilicky";
                case "Rhydon" -> "Rhyperior";
                case "Tangela" -> "Tangrowth";
                case "Electabuzz" -> "Electivire";
                case "Magmar" -> "Magmortar";
                case "Togetic" -> "Togekiss";
                case "Pansage" -> "Simisage";
                case "Pansear" -> "Simisear";
                case "Panpour" -> "Simipour";
                case "Munna" -> "Musharna";
                case "Cottonee" -> "Whimsicott";
                case "Petilil" -> "Lilligant";
                case "Minccino" -> "Cinccino";
                case "Eelektrik" -> "Eelektross";
                case "Lampent" -> "Chandelure";
                case "Floette" -> "Florges";
                case "Doublade" -> "Aegislash";
                case "Spritzee" -> "Aromatisse";
                case "Swirlix" -> "Slurpuff";
                case "Helioptil" -> "Heliolisk";
                case "Steenee" -> "Tsareena";
                case "Galarian Darumaka" -> "Galarian Darmanitan";
                default -> "";
            };

        if(!Global.POKEMON.contains(newEvolution))
        {
            System.out.println(newEvolution + " isn't implemented!");
            return;
        }

        if(newEvolution.isEmpty()) throw new IllegalStateException("Evolution failed - " + this.getName());

        this.linkGenericJSON(Global.normalCase(newEvolution));
        Pokemon.updateName(this, newEvolution);
    }

    //Megas and Forms
    public boolean hasMega()
    {
        return this.genericJSON.getJSONArray("mega").length() > 0;
    }

    public boolean hasForms()
    {
        return this.genericJSON.getJSONArray("forms").length() > 0;
    }

    public List<String> getMegaList()
    {
        return this.genericJSON.getJSONArray("mega").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public List<String> getFormsList()
    {
        return this.genericJSON.getJSONArray("forms").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public void changeForm(String form)
    {
        form = Global.normalCase(form);
        this.linkGenericJSON(form);
        Pokemon.updateName(this, form);
    }

    //Dynamax and Gigantamax
    public boolean isDynamaxed()
    {
        return this.isDynamaxed;
    }

    public void setDynamax(boolean dynamaxed)
    {
        this.isDynamaxed = dynamaxed;
    }

    public int getDynamaxLevel()
    {
        return this.dynamaxLevel;
    }

    public void increaseDynamaxLevel()
    {
        if(this.dynamaxLevel <= 9)
        {
            this.dynamaxLevel++;
            Pokemon.updateDynamaxLevel(this);
        }
    }

    public void setDynamaxLevel(int level)
    {
        this.dynamaxLevel = level;
    }

    public void enterDynamax()
    {
        double healthRatio = (double)this.getHealth() / this.getStat(Stat.HP);

        this.setDynamax(true);

        this.setHealth((int)(healthRatio * this.getStat(Stat.HP)));
    }

    public void exitDynamax()
    {
        double healthRatio = (double)this.getHealth() / this.getStat(Stat.HP);

        this.setDynamax(false);

        this.setHealth((int)(healthRatio * this.getStat(Stat.HP)));
    }

    public boolean canGigantamax()
    {
        return Pokemon.existsGigantamax(this.getName());
    }

    public static boolean existsGigantamax(String p)
    {
        return DataHelper.GIGANTAMAX_DATA.containsKey(p);
    }

    public static DataHelper.GigantamaxData getGigantamaxData(String p)
    {
        return DataHelper.GIGANTAMAX_DATA.get(p);
    }

    //IVs and EVs
    public Map<Stat, Integer> getIVs()
    {
        return this.IV;
    }

    public Map<Stat, Integer> getEVs()
    {
        return this.EV;
    }

    public void addEV(Stat s, int amount)
    {
        int newEV = this.EV.get(s) + amount;
        if(this.getEVTotal() <= 510 && newEV <= 252) this.EV.put(s, this.EV.get(s) + amount);
    }

    public Map<Stat, Integer> getEVYield()
    {
        JSONArray yield = this.genericJSON.getJSONArray("ev");
        Map<Stat, Integer> EVYield = new HashMap<>();

        for(int i = 0; i < yield.length(); i++) if(yield.getInt(i) != 0) EVYield.put(Stat.values()[i], yield.getInt(i));
        return EVYield;
    }

    public void gainEVs(Pokemon defeated)
    {
        for(Stat s : defeated.getEVYield().keySet()) this.addEV(s, defeated.getEVYield().get(s));
    }

    public int getEVTotal()
    {
        return this.EV.keySet().stream().mapToInt(stat -> this.EV.get(stat)).sum();
    }

    public String getVCondensed(Map<Stat, Integer> v)
    {
        StringBuilder cond = new StringBuilder();
        for(int i = 0; i < Stat.values().length; i++) cond.append(v.get(Stat.values()[i])).append("-");
        return cond.substring(0, cond.length() - 1).trim();
    }

    public void setIVs(String cond)
    {
        for(int i = 0; i < 6; i++) this.IV.put(Stat.values()[i], Integer.parseInt(cond.split("-")[i]));
    }

    public void setEVs(String cond)
    {
        for(int i = 0; i < 6; i++) this.EV.put(Stat.values()[i], Integer.parseInt(cond.split("-")[i]));
    }

    public void setIVs()
    {
        for(int i = 0; i < 6; i++) this.IV.put(Stat.values()[i], new Random().nextInt(32));
    }

    public void setIVs(int min)
    {
        do { this.setIVs(); }
        while(this.getTotalIVRounded() < min);
    }

    public void setEVs()
    {
        for(int i = 0; i < 6; i++) this.EV.put(Stat.values()[i], 0);
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

    //Nature
    public void setNature(String nature)
    {
        this.nature = Nature.cast(nature);
    }

    public void setNature()
    {
        this.setNature(Nature.values()[new Random().nextInt(Nature.values().length)].toString());
    }

    public Nature getNature()
    {
        return this.nature;
    }

    //Stat
    public int getStat(Stat s)
    {
        if(s.equals(Stat.HP))
        {
            //HP = Level + 10 + [((2 * Base + IV + EV / 4) * Level) / 100]
            double base = this.getBaseStat(Stat.HP);
            int IV = this.IV.get(Stat.HP);
            int EV = this.EV.get(Stat.HP);
            double maxHP = this.level + 10 + ((this.level * (2 * base + IV + EV / 4.0)) / 100);

            double dynamaxBoost = this.isDynamaxed ? 1.0 + (this.getDynamaxLevel() * 0.05 + 0.5) : 1.0;
            return (int)(maxHP * dynamaxBoost);
        }
        else
        {
            //Stat = Nature * [5 + ((2 * Base + IV + EV / 4) * Level) / 100]
            double nature = this.nature.getMap().get(s);
            double base = this.getBaseStat(s);
            int IV = this.IV.get(s);
            int EV = this.EV.get(s);
            double stat = nature * (5 + ((this.level * (2 * base + IV + EV / 4.0)) / 100));
            return (int)(stat * this.getStatMultiplier(s) * statBuff);
        }
    }

    public int getTotalStat()
    {
        return Arrays.stream(Stat.values()).mapToInt(this::getStat).sum();
    }

    public int getBaseStat(Stat s)
    {
        return this.genericJSON.getJSONArray("stats").getInt(s.ordinal());
    }

    public void setDefaultStatMultipliers()
    {
        for(int i = 0; i < Stat.values().length; i++) this.statMultiplier.put(Stat.values()[i], 0);
    }

    public void changeStatMultiplier(Stat s, int stageChange)
    {
        this.statMultiplier.put(s, (this.statMultiplier.get(s) == -6 && stageChange < 0) || (this.statMultiplier.get(s) == 6 && stageChange > 0) ? this.statMultiplier.get(s) : this.statMultiplier.get(s) + stageChange);
    }

    public double getStatMultiplier(Stat s)
    {
        return this.statMultiplier.get(s) == 0 ? 1.0 : (double)(this.statMultiplier.get(s) < 0 ? 2 : 2 + Math.abs(this.statMultiplier.get(s))) / (this.statMultiplier.get(s) < 0 ? 2 + Math.abs(this.statMultiplier.get(s)) : 2);
    }

    public int getStageChange(Stat s)
    {
        return this.statMultiplier.get(s);
    }

    public List<String> getAbilities()
    {
        return this.genericJSON.getJSONArray("abilities").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    //Simple Methods
    public String getName()
    {
        return this.genericJSON.getString("name");
    }

    public String getNickname()
    {
        return this.nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public boolean hasNickname()
    {
        return !this.getNickname().equals("");
    }

    //Decides between nickname or real name, if the nickname exists or not
    public String getDisplayName()
    {
        return this.hasNickname() ? "\"" + this.getNickname() + "\"" : this.getName();
    }

    public Type[] getType()
    {
        return this.type;
    }

    public void setType(Type t, int ind)
    {
        this.type[ind] = t;
    }

    public void setType()
    {
        this.type = new Type[]{Type.cast(this.genericJSON.getJSONArray("type").getString(0)), Type.cast(this.genericJSON.getJSONArray("type").getString(1))};
    }

    public boolean isType(Type t)
    {
        return this.getType()[0].equals(t) || this.getType()[1].equals(t);
    }

    public double getWeight()
    {
        return Double.parseDouble(this.genericJSON.getString("fillerinfo").split("-")[2]);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return UUID.equals(pokemon.UUID);
    }
}
