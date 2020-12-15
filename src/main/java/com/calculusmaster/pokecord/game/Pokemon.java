package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Pokemon
{
    private JSONObject genericJSON;
    private JSONObject specificJSON;

    private String UUID;
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
    private StatusCondition status;
    private int crit;

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
        p.setItem(specific.getString("item"));

        p.setHealth(p.getStat(Stat.HP));
        p.removeStatusConditions();
        p.setCrit(1);

        Global.logInfo(Pokemon.class, "build", "Pokemon Built (UUID: " + UUID + ", Name: " + p.getName() + ")!");
        return p;
    }

    public static Pokemon create(String name)
    {
        Pokemon p = new Pokemon();
        p.setUUID();

        p.linkGenericJSON(Global.normalCase(name));

        p.setLevel(1);
        p.setExp(0);
        p.setShiny(new Random().nextInt(1000000) == 1);
        p.setIVs();
        p.setEVs();
        p.setNature();
        p.setLearnedMoves("Tackle-Tackle-Tackle-Tackle");
        p.setTM(-1);
        p.setTR(-1);
        p.removeItem();

        p.setHealth(p.getStat(Stat.HP));
        p.removeStatusConditions();
        p.setCrit(1);

        Global.logInfo(Pokemon.class, "create", "New Pokemon (" + name + ") Created!");
        return p;
    }

    //Builder for minimal access
    //WILL CRASH IF ANY OTHER GETTERS ARE USED
    public static Pokemon buildCore(String UUID, int num)
    {
        JSONObject specific = Pokemon.specificJSON(UUID);

        Pokemon p = new Pokemon()
        {
            @Override
            public String getUUID() {
                return UUID;
            }

            @Override
            public int getLevel() {
                return specific.getInt("level");
            }

            @Override
            public String getName() {
                return specific.getString("name");
            }

            @Override
            public int getNumber()
            {
                return num + 1;
            }
        };

        p.setIVs(specific.getString("ivs"));

        return p;
    }

    public int getNumber()
    {
        return -1;
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
        return new JSONObject(Mongo.PokemonInfo.find(Filters.eq("name", name.substring(0, 1).toUpperCase() + name.substring(1))).first().toJson());
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

    //Status Conditions
    public void setStatusCondition(StatusCondition status)
    {
        this.status = status;
    }

    public void removeStatusConditions()
    {
        this.status = StatusCondition.NORMAL;
    }

    public StatusCondition getStatusCondition()
    {
        return this.status;
    }

    //Health
    public void setHealth(int num)
    {
        this.health = num;
    }

    public void changeHealth(int amount)
    {
        this.health += amount;
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

        if(this.hasTM()) movesList.add(Global.normalCase(TM.get(this.heldTM).toString()));
        if(this.hasTR()) movesList.add(Global.normalCase(TR.get(this.heldTR).toString()));

        return movesList;
    }

    public List<String> getAllMoves()
    {
        JSONArray moves = this.genericJSON.getJSONArray("moves");
        List<String> movesList = new ArrayList<>();
        for(int i = 0; i < moves.length(); i++) movesList.add(moves.getString(i));
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
        if(this.getAvailableMoves().contains(move)) this.learnedMoves.set(replace - 1, move);
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
    //TODO: Items
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
        String image = this.genericJSON.getString(this.isShiny() ? "shinyURL" : "normalURL");
        return image.equals("") ? Pokemon.getWIPImage() : image;
    }

    public URL getURL() throws MalformedURLException
    {
        return new URL(this.getImage());
    }

    public static String getWIPImage()
    {
        return "http://clipart-library.com/img/1657818.png";
    }

    //Levels and Experience
    public void setLevel(int level)
    {
        this.level = level;
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

        //System.out.println("Initial LVL: " + this.level + ", Initial EXP: " + this.exp);

        while(this.exp >= required && this.level < 100)
        {
            System.out.println("Needs: " + required + ", Has: " + this.exp);

            this.level++;
            this.exp -= required;
            required = GrowthRate.getRequiredExp(this.genericJSON.getString("growthrate"), this.level);
        }

        //System.out.println("Final LVL: " + this.level + ", Final EXP: " + this.exp);
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
        //TODO: Special evolutions, like through trade or items
        return this.genericJSON.getJSONArray("evolutionsLVL").length() != 0 && this.genericJSON.getJSONArray("evolutionsLVL").getInt(0) <= this.getLevel();
    }

    public void evolve()
    {
        if(!this.canEvolve()) return;

        String newEvolution = this.genericJSON.getJSONArray("evolutions").getString(0);

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

    private List<String> getListStringFromJSONArray(String key)
    {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < this.genericJSON.getJSONArray(key).length(); i++) list.add(this.genericJSON.getJSONArray("mega").getString(i));
        return list;
    }

    public List<String> getMegaList()
    {
        return this.getListStringFromJSONArray("mega");
    }

    public List<String> getFormsList()
    {
        return this.getListStringFromJSONArray("forms");
    }

    public void changeForm(String form)
    {
        form = Global.normalCase(form);
        System.out.println(this.getName() + " is being transformed into " + form);
        this.linkGenericJSON(form);
        Pokemon.updateName(this, form);
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

    public void changeIV(Stat s, int amount)
    {
        this.IV.put(s, this.IV.get(s) + amount);
    }

    public void addEV(Stat s, int amount)
    {
        this.EV.put(s, this.EV.get(s) + amount);
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
        return Double.parseDouble(this.getTotalIV().substring(0, 5));
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
            return (int)stat;
        }
    }

    public boolean isCrit()
    {
        return (new Random().nextInt(24) + 1) <= this.crit;
    }

    public void setCrit(int crit)
    {
        this.crit = crit;
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


    //Simple Methods
    public String getName()
    {
        return this.genericJSON.getString("name");
    }

    public Type[] getType()
    {
        return new Type[]{Type.cast(this.genericJSON.getJSONArray("type").getString(0)), Type.cast(this.genericJSON.getJSONArray("type").getString(1))};
    }
}
