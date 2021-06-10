package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.commands.economy.CommandShop;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.util.CacheHelper;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.PokemonRarity;
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
    private JSONObject specificJSON;

    private String UUID;
    private int num;
    private boolean shiny;
    private Nature nature;
    private Map<Stat, Double> natureMap;

    private int level;
    private int exp;

    private Map<Stat, Integer> IV = new TreeMap<>();
    private Map<Stat, Integer> EV = new TreeMap<>();

    private List<String> learnedMoves = new ArrayList<>();
    private int heldTM;
    private int heldTR;

    private String heldItem;

    private int health;
    private Type[] type;
    private Map<StatusCondition, Boolean> status;
    private int crit;
    private Map<Stat, Integer> statMultiplier = new TreeMap<>();
    private boolean statImmune;
    private boolean endure;
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

        p.setHealth(p.getStat(Stat.HP));
        p.setType();
        p.setStatusConditions();
        p.setCrit(1);
        p.setDefaultStatMultipliers();
        p.setStatImmune(false);
        p.setEndure(false);
        p.statBuff = 1.0;
        p.setDynamax(false);

        Global.logInfo(Pokemon.class, "build", "Pokemon Built (UUID: " + UUID + ", Name: " + p.getName() + ")!");
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

        p.setHealth(p.getStat(Stat.HP));
        p.setType();
        p.setStatusConditions();
        p.setCrit(1);
        p.setDefaultStatMultipliers();
        p.setStatImmune(false);
        p.setEndure(false);
        p.statBuff = 1.0;
        p.setDynamax(false);

        Global.logInfo(Pokemon.class, "create", "New Pokemon (" + name + ") Created!");
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
        };

        p.linkGenericJSON(p.getName());
        p.linkSpecificJSON(UUID);

        p.setNumber(num + 1);

        p.setIVs(d.getString("ivs"));
        p.setType();

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
                .append("item", p.getItem());

        Mongo.PokemonData.insertOne(pokeData);
    }

    public static void updateExperience(Pokemon p)
    {
        Mongo.PokemonData.updateOne(p.getQuery(), Updates.set("level", p.getLevel()));
        Mongo.PokemonData.updateOne(p.getQuery(), Updates.set("exp", p.getExp()));
        CacheHelper.updatePokemon(p.getUUID());
    }

    public static void updateExpLevel(Pokemon p)
    {
        Mongo.PokemonData.updateOne(p.getQuery(), Updates.set("exp", p.getExp()));
        CacheHelper.updatePokemon(p.getUUID());
    }

    public static void updateMoves(Pokemon p)
    {
        Mongo.PokemonData.updateOne(p.getQuery(), Updates.set("moves", p.getMovesCondensed()));
    }

    public static void updateEVs(Pokemon p)
    {
        Mongo.PokemonData.updateOne(p.getQuery(), Updates.set("evs", p.getVCondensed(p.getEVs())));
    }

    public static void updateName(Pokemon p, String evolved)
    {
        Mongo.PokemonData.updateOne(p.getQuery(), Updates.set("name", Global.normalCase(evolved)));
        CacheHelper.updatePokemon(p.getUUID());
    }

    public static void updateTMTR(Pokemon p)
    {
        Mongo.PokemonData.updateOne(p.getQuery(), Updates.set("tm", p.getTM()));
        Mongo.PokemonData.updateOne(p.getQuery(), Updates.set("tr", p.getTR()));
    }

    public static void updateItem(Pokemon p)
    {
        Mongo.PokemonData.updateOne(p.getQuery(), Updates.set("item", p.getItem()));
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

        int basePrice = switch(rarity)
                {
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
        int numSoftZero = 0;
        int numMax = 0;
        int numSoftMax = 0;

        for(Stat s : this.getIVs().keySet())
        {
            switch (this.getIVs().get(s))
            {
                case 0 -> {
                    numZero++;
                    numSoftZero++;
                }
                case 1, 2 -> numSoftZero++;
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

        if(this.health <= 0 && this.endure)
        {
            this.health = 1;
            this.endure = false;
        }
        else if(this.endure) this.endure = false;

        DuelHelper.instance(this.getUUID()).addDamage(amount, this.getUUID());
        //if(!duel.getPokemon()[duel.turn].getUUID().equals(this.getUUID())) duel.lastDamage = amount;
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
        if(this.getAvailableMoves().contains(move) || CommandShop.MOVE_TUTOR_MOVES.contains(move)) this.learnedMoves.set(replace - 1, move);
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

    public boolean canLearnTM(int tm)
    {
        for(int i = 0; i < this.genericJSON.getJSONArray("movesTM").length(); i++) if(this.genericJSON.getJSONArray("movesTM").getInt(i) == tm) return true;
        return false;
    }

    public boolean canLearnTR(int tr)
    {
        for(int i = 0; i < this.genericJSON.getJSONArray("movesTR").length(); i++) if(this.genericJSON.getJSONArray("movesTR").getInt(i) == tr) return true;
        return false;
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

    @Deprecated
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
        String charList = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random r = new Random();
        StringBuilder uuid = new StringBuilder();
        for(int i = 0; i < 6; i++)
        {
            for(int a = 0; a < 4; a++) uuid.append(charList.charAt(r.nextInt(charList.length())));
            uuid.append("-");
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
        if(this.isDynamaxed && this.canGigantamax()) return this.isShiny() ? GIGANTAMAX_DATA.get(this.getName()).shinyImage() : GIGANTAMAX_DATA.get(this.getName()).normalImage();

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
                case "Gloom" -> PokeItem.asItem(this.getItem()).equals(PokeItem.LEAF_STONE) ? "Vileplume" : "Bellossom";
                case "Growlithe" -> "Arcanine";
                case "Weepinbell" -> "Victreebel";
                case "Shellder" -> "Cloyster";
                case "Exeggcute" -> "Exeggutor";
                case "Staryu" -> "Starmie";
                case "Eevee" -> PokeItem.asItem(this.getItem()).equals(PokeItem.WATER_STONE) ? "Vaporeon" : (PokeItem.asItem(this.getItem()).equals(PokeItem.THUNDER_STONE) ? "Jolteon" : "Flareon");
                case "Onix" -> "Steelix";
                case "Haunter" -> "Gengar";
                case "Charjabug" -> "Vikavolt";
                case "Poipole" -> "Naganadel";
                case "Poliwhirl" -> PokeItem.asItem(this.getItem()).equals(PokeItem.WATER_STONE) ? "Poliwrath" : "Politoed";
                case "Aipom" -> "Ambipom";
                case "Sunkern" -> "Sunflora";
                case "Yanma" -> "Yanmega";
                case "Murkrow" -> "Honchkrow";
                case "Slowpoke" -> "Slowking";
                case "Galarian Slowpoke" -> PokeItem.asItem(this.getItem()).equals(PokeItem.GALARICA_CUFF) ? "Galarian Slowbro" : "Galarian Slowking";
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
                case "Clamperl" -> PokeItem.asItem(this.getItem()).equals(PokeItem.DEEP_SEA_TOOTH) ? "Huntail" : "Gorebyss";
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

    public boolean canGigantamax()
    {
        return GIGANTAMAX_DATA.keySet().stream().anyMatch(s -> s.equals(this.getName()));
    }

    public static final Map<String, GigantamaxData> GIGANTAMAX_DATA = new HashMap<>();

    public record GigantamaxData(String pokemon, String move, Type moveType, String normalImage, String shinyImage) {};

    public static void gigantamaxInit()
    {
        Pokemon.addGigantamaxData("Charizard", "Wildfire", Type.FIRE, "https://archives.bulbagarden.net/media/upload/thumb/8/88/006Charizard-Gigantamax.png/600px-006Charizard-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8g5-b2a95aec-480d-4ec9-8ed7-0f7b465bbe04.png");
        Pokemon.addGigantamaxData("Butterfree", "Befuddle", Type.BUG, "https://archives.bulbagarden.net/media/upload/thumb/f/fd/012Butterfree-Gigantamax.png/600px-012Butterfree-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8la-da8208d5-c16d-4574-a6d1-7021b5d8e4b7.png");
        Pokemon.addGigantamaxData("Pikachu", "Volt Crash", Type.ELECTRIC, "https://archives.bulbagarden.net/media/upload/thumb/6/6b/025Pikachu-Gigantamax.png/600px-025Pikachu-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8nd-f539417b-3e5e-4372-8940-001642e83a29.png");
        Pokemon.addGigantamaxData("Meowth", "Gold Rush", Type.NORMAL, "https://archives.bulbagarden.net/media/upload/thumb/9/9f/052Meowth-Gigantamax.png/600px-052Meowth-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8p6-8e182c3c-f06b-40b1-acc1-e6fb1e78ecf2.png");
        Pokemon.addGigantamaxData("Machamp", "Chi Strike", Type.FIGHTING, "https://archives.bulbagarden.net/media/upload/c/c4/068Machamp-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8r6-345e59b2-5d11-434f-835e-f409ffe1645b.png");
        Pokemon.addGigantamaxData("Gengar", "Terror", Type.GHOST, "https://archives.bulbagarden.net/media/upload/3/31/094Gengar-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8tf-072f9eb4-202b-4762-968a-aba5dc736fdc.png");
        Pokemon.addGigantamaxData("Kingler", "Foam Burst", Type.WATER, "https://archives.bulbagarden.net/media/upload/d/d3/099Kingler-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8v7-479dc844-e073-42ed-8e75-ebcec0e1ddc2.png");
        Pokemon.addGigantamaxData("Lapras", "Resonance", Type.ICE, "https://archives.bulbagarden.net/media/upload/1/15/131Lapras-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8xe-0ff37d4c-2d62-4e23-9636-a26e56e641f9.png");
        Pokemon.addGigantamaxData("Eevee", "Cuddle", Type.NORMAL, "https://archives.bulbagarden.net/media/upload/thumb/2/2e/133Eevee-Gigantamax.png/600px-133Eevee-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8yy-eca81996-8027-4e92-adea-d34789cf89c5.png");
        Pokemon.addGigantamaxData("Snorlax", "Replenish", Type.NORMAL, "https://archives.bulbagarden.net/media/upload/thumb/3/38/143Snorlax-Gigantamax.png/600px-143Snorlax-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h9j8-9f20c69d-fc0b-4404-b23f-c1ce8f13fef9.png");
        Pokemon.addGigantamaxData("Garbodor", "Malodor", Type.POISON, "https://archives.bulbagarden.net/media/upload/c/c9/569Garbodor-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h9m3-45758612-321e-47d3-b7de-a948bf1655a4.png");
        Pokemon.addGigantamaxData("Melmetal", "Meltdown", Type.STEEL, "https://archives.bulbagarden.net/media/upload/thumb/5/5e/809Melmetal-Gigantamax.png/600px-809Melmetal-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de609ih-d7f1603a-92d1-45fb-9382-466ebd59e1ac.png");
        Pokemon.addGigantamaxData("Corviknight", "Wind Rage", Type.FLYING, "https://archives.bulbagarden.net/media/upload/thumb/2/2e/823Corviknight-Gigantamax.png/600px-823Corviknight-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5m4-af08351e-42a4-432c-8f8d-ee33e9a27608.png");
        Pokemon.addGigantamaxData("Orbeetle", "Gravitas", Type.PSYCHIC, "https://archives.bulbagarden.net/media/upload/2/24/826Orbeetle-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5pg-fb09e50b-14d8-4501-b84d-b828200604e2.png");
        Pokemon.addGigantamaxData("Drednaw", "Stonesurge", Type.WATER, "https://archives.bulbagarden.net/media/upload/thumb/b/b4/834Drednaw-Gigantamax.png/600px-834Drednaw-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5t5-a5195291-9518-40a2-9846-f6c3f78f4355.png");
        Pokemon.addGigantamaxData("Coalossal", "Volcalith", Type.ROCK, "https://archives.bulbagarden.net/media/upload/b/b0/839Coalossal-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5wa-b42cff90-3a0b-4902-ad86-575e5534b104.png");
        Pokemon.addGigantamaxData("Flapple", "Tartness", Type.GRASS, "https://archives.bulbagarden.net/media/upload/a/a2/841Flapple-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5y8-9349734d-323c-463a-9dc6-8853a2ed527d.png");
        Pokemon.addGigantamaxData("Appletun", "Sweetness", Type.GRASS, "https://archives.bulbagarden.net/media/upload/a/a2/841Flapple-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5y8-9349734d-323c-463a-9dc6-8853a2ed527d.png");
        Pokemon.addGigantamaxData("Sandaconda", "Sandblast", Type.GROUND, "https://archives.bulbagarden.net/media/upload/thumb/1/19/844Sandaconda-Gigantamax.png/600px-844Sandaconda-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c63w-15849167-115c-4126-ac8f-9147dc2e80ad.png");
        Pokemon.addGigantamaxData("Toxtricity Low Key", "Stunshock", Type.ELECTRIC, "https://archives.bulbagarden.net/media/upload/thumb/7/73/849Toxtricity-Gigantamax.png/600px-849Toxtricity-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c66g-4ae1f5ae-e105-49db-a5f0-f1c1bc29029e.png");
        Pokemon.addGigantamaxData("Toxtricity Amped", "Stunshock", Type.ELECTRIC, "https://archives.bulbagarden.net/media/upload/thumb/7/73/849Toxtricity-Gigantamax.png/600px-849Toxtricity-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c66g-4ae1f5ae-e105-49db-a5f0-f1c1bc29029e.png");
        Pokemon.addGigantamaxData("Centiskorch", "Centiferno", Type.FIRE, "https://archives.bulbagarden.net/media/upload/thumb/b/b4/851Centiskorch-Gigantamax.png/600px-851Centiskorch-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c68f-e67e2d21-921f-4402-9c51-c566a8b21158.png");
        Pokemon.addGigantamaxData("Hatterene", "Smite", Type.FAIRY, "https://archives.bulbagarden.net/media/upload/6/6a/858Hatterene-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6a1-c9664b1f-7032-4a22-970a-ed004a02ef97.png");
        Pokemon.addGigantamaxData("Grimmsnarl", "Snooze", Type.DARK, "https://archives.bulbagarden.net/media/upload/4/45/861Grimmsnarl-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6by-d97b1fa5-b7ee-4763-9f7a-e058dbe15822.png");
        Pokemon.addGigantamaxData("Alcremie", "Finale", Type.FAIRY, "https://archives.bulbagarden.net/media/upload/thumb/4/40/869Alcremie-Gigantamax.png/600px-869Alcremie-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6fz-26076c68-27ea-4bf3-870d-6f7b5d4fe841.png");
        Pokemon.addGigantamaxData("Copperajah", "Steelsurge", Type.STEEL, "https://archives.bulbagarden.net/media/upload/1/16/879Copperajah-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6hr-b15b2a1b-c278-43bd-95fc-4c85f9f2c91c.png");
        Pokemon.addGigantamaxData("Duraludon", "Depletion", Type.DRAGON, "https://archives.bulbagarden.net/media/upload/1/1b/884Duraludon-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6jb-dec68b9e-738c-448c-82c0-c616b150f441.png");
        Pokemon.addGigantamaxData("Venusaur", "Vine Lash", Type.GRASS, "https://archives.bulbagarden.net/media/upload/thumb/8/8a/003Venusaur-Gigantamax.png/600px-003Venusaur-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8a2-5c5b3deb-fbc3-468a-a2d4-d430b18d6d6e.png");
        Pokemon.addGigantamaxData("Blastoise", "Cannonade", Type.WATER, "https://archives.bulbagarden.net/media/upload/thumb/d/dc/009Blastoise-Gigantamax.png/600px-009Blastoise-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8ig-946b4fb0-5730-434e-b7fc-6d62e21543d9.png");
        Pokemon.addGigantamaxData("Rillaboom", "Drum Solo", Type.GRASS, "https://archives.bulbagarden.net/media/upload/thumb/0/0b/812Rillaboom-Gigantamax.png/600px-812Rillaboom-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h9pw-0e6e9f81-e373-4e50-8f77-2b9ff451da93.png");
        Pokemon.addGigantamaxData("Cinderace", "Fireball", Type.FIRE, "https://archives.bulbagarden.net/media/upload/thumb/1/1e/815Cinderace-Gigantamax.png/600px-815Cinderace-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h9sn-c4565af1-9718-46d6-9187-557362c96071.png");
        Pokemon.addGigantamaxData("Inteleon", "Hydrosnipe", Type.WATER, "https://archives.bulbagarden.net/media/upload/thumb/b/bf/818Inteleon-Gigantamax.png/600px-818Inteleon-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h9uz-7dbcfcfc-ee7d-4503-8d0a-2f3c273792bb.png");
        Pokemon.addGigantamaxData("Urshifu", "One Blow", Type.DARK, "https://archives.bulbagarden.net/media/upload/thumb/f/fd/892Urshifu-Gigantamax_Single_Strike.png/600px-892Urshifu-Gigantamax_Single_Strike.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6m6-b13fb190-33da-4d4c-ad72-4a7c80476c7b.png");
        Pokemon.addGigantamaxData("Urshifu Rapid Strike", "Rapid Flow", Type.WATER, "https://archives.bulbagarden.net/media/upload/thumb/1/1d/892Urshifu-Gigantamax_Rapid_Strike.png/600px-892Urshifu-Gigantamax_Rapid_Strike.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6nz-bce37412-64cf-4fe0-ba44-88bc4fb95eec.png");
    }

    private static void addGigantamaxData(String name, String move, Type type, String normal, String shiny)
    {
        GIGANTAMAX_DATA.put(name, new GigantamaxData(name, "G Max " + move, type, normal, shiny));
    }

    private static void addGigantamaxData(String name, String move, Type type, String normal)
    {
        addGigantamaxData(name, move, type, normal, normal);
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
        this.setNatureJSON();
    }

    public void setNature()
    {
        this.setNature(Nature.values()[new Random().nextInt(Nature.values().length)].toString());
    }

    public void setNatureJSON()
    {
        this.natureMap = new HashMap<>();
        JSONObject natureJSON = new JSONObject(Mongo.NatureInfo.find(Filters.eq("name", this.nature.toString().toUpperCase())).first().toJson());
        for(Stat s : new Stat[]{Stat.ATK, Stat.DEF, Stat.SPATK, Stat.SPDEF, Stat.SPD}) this.natureMap.put(s, natureJSON.getDouble(s.toString()));
    }

    public Nature getNature()
    {
        return this.nature;
    }

    //Stat
    public int getStat(Stat s)
    {
        if(s.equals(Stat.HP)) return this.getMaxHP();
        else
        {
            //Stat = Nature * [5 + ((2 * Base + IV + EV / 4) * Level) / 100]
            double nature = this.natureMap.get(s);
            double base = this.genericJSON.getJSONArray("stats").getInt(s.ordinal());
            int IV = this.IV.get(s);
            int EV = this.EV.get(s);
            double stat = nature * (5 + ((this.level * (2 * base + IV + EV / 4.0)) / 100));
            return (int)(stat * this.getStatMultiplier(s) * statBuff);
        }
    }

    public void setDefaultStatMultipliers()
    {
        for(int i = 0; i < Stat.values().length; i++) this.statMultiplier.put(Stat.values()[i], 0);
    }

    public void changeStatMultiplier(Stat s, int stageChange)
    {
        if(this.isStatImmune() && stageChange < 0) return;
        else this.statMultiplier.put(s, (this.statMultiplier.get(s) == -6 && stageChange < 0) || (this.statMultiplier.get(s) == 6 && stageChange > 0) ? this.statMultiplier.get(s) : this.statMultiplier.get(s) + stageChange);
    }

    public double getStatMultiplier(Stat s)
    {
        return this.statMultiplier.get(s) == 0 ? 1.0 : (double)(this.statMultiplier.get(s) < 0 ? 2 : 2 + Math.abs(this.statMultiplier.get(s))) / (this.statMultiplier.get(s) < 0 ? 2 + Math.abs(this.statMultiplier.get(s)) : 2);
    }

    public boolean isStatImmune()
    {
        return this.statImmune;
    }

    public void setStatImmune(boolean statImmune)
    {
        this.statImmune = statImmune;
    }

    public void setEndure(boolean endure)
    {
        this.endure = endure;
    }

    public boolean isCrit()
    {
        return (new Random().nextInt(24) + 1) <= this.crit;
    }

    public void setCrit(int crit)
    {
        this.crit = crit;
    }

    public int getCrit()
    {
        return this.crit;
    }

    private int getMaxHP()
    {
        //HP = Level + 10 + [((2 * Base + IV + EV / 4) * Level) / 100]
        double base = this.genericJSON.getJSONArray("stats").getInt(0);
        int IV = this.IV.get(Stat.HP);
        int EV = this.EV.get(Stat.HP);
        double maxHP = this.level + 10 + ((this.level * (2 * base + IV + EV / 4.0)) / 100);
        return (int)maxHP;
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
