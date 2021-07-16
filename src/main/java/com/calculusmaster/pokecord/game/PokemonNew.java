package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.enums.elements.Nature;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.PokemonData;
import com.calculusmaster.pokecord.util.custom.ExtendedHashMap;
import com.calculusmaster.pokecord.util.custom.StatIntMap;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PokemonNew
{
    private PokemonData data;
    private Document specific;

    private String UUID;

    private Optional<String> name = Optional.empty();
    private Optional<String> nickname = Optional.empty();
    private Optional<Boolean> shiny = Optional.empty();
    private Optional<List<Type>> type = Optional.empty();
    private Optional<Integer> level = Optional.empty();
    private Optional<Integer> exp = Optional.empty();
    private Optional<Nature> nature = Optional.empty();
    private Optional<Map<Stat, Integer>> ivs = Optional.empty();
    private Optional<Map<Stat, Integer>> evs = Optional.empty();
    private Optional<Map<Stat, Integer>> multipliers = Optional.empty();

    private Optional<Double> boost = Optional.empty();

    public static PokemonNew create(String name)
    {
        PokemonNew p = new PokemonNew();
        p.setUUID();
        p.setData(name);

        return p;
    }

    public static PokemonNew build(String UUID)
    {
        PokemonNew p = new PokemonNew();
        p.setUUID(UUID);
        p.setSpecificDocument();
        p.setData(p.getName());

        p.specific = Mongo.PokemonData.find(Filters.eq("UUID", UUID)).first();
        p.data = DataHelper.pokeData(p.specific.getString("name"));

        return p;
    }

    //Database Upload/Delete
    public static void upload(PokemonNew p)
    {
        Document data = new Document()
                .append("UUID", p.getUUID())
                .append("name", p.getName())
                .append("nickname", p.getNickname());

        Mongo.PokemonData.insertOne(data);
    }

    public static void delete(PokemonNew p)
    {
        Mongo.PokemonData.deleteOne(p.query());
    }

    //Backend Database Updates
    public void update()
    {
        this.setSpecificDocument();
    }

    public void update(Bson update)
    {
        Mongo.PokemonData.updateOne(this.query(), update);
    }

    //Specific Database Updates
    public void updateName()
    {
        this.update(Updates.set("name", this.getName()));
    }

    public void updateNickname()
    {
        this.update(Updates.set("nickname", this.getNickname()));
    }

    public void updateExperience()
    {
        this.update(Updates.set("level", this.getLevel()));
        this.update(Updates.set("exp", this.getExp()));
    }

    //Stats
    public int getStat(Stat s)
    {
        if(s.equals(Stat.HP))
        {
            //HP = Level + 10 + [((2 * Base + IV + EV / 4) * Level) / 100]
            int level = this.getLevel();
            double base = this.getBaseStat(Stat.HP);
            int IV = this.getIVs().get(Stat.HP);
            int EV = this.getEVs().get(Stat.HP);
            double maxHP = level + 10 + ((level * (2 * base + IV + EV / 4.0)) / 100);

            double dynamaxBoost = 1.0; //this.isDynamaxed ? 1.0 + (this.getDynamaxLevel() * 0.05 + 0.5) : 1.0;
            return (int)(maxHP * dynamaxBoost);
        }
        else
        {
            //Stat = Nature * [5 + ((2 * Base + IV + EV / 4) * Level) / 100]
            double nature = this.getNature().getMap().get(s);
            double base = this.getBaseStat(s);
            int IV = this.getIVs().get(s);
            int EV = this.getEVs().get(s);
            int level = this.getLevel();
            double stat = nature * (5 + ((level * (2 * base + IV + EV / 4.0)) / 100));
            return (int)(stat * this.calculateMultiplier(s) * this.boost.orElse(1.0));
        }
    }

    public int getBaseStat(Stat s)
    {
        return this.data.baseStats.get(s);
    }

    public int getTotalStat()
    {
        return Arrays.stream(Stat.values()).mapToInt(this::getStat).sum();
    }

    public double calculateMultiplier(Stat s)
    {
        return this.getStageChange().get(s) == 0 ? 1.0 : (double)(this.getStageChange().get(s) < 0 ? 2 : 2 + Math.abs(this.getStageChange().get(s))) / (this.getStageChange().get(s) < 0 ? 2 + Math.abs(this.getStageChange().get(s)) : 2);
    }

    public Map<Stat, Integer> getStageChange()
    {
        return this.multipliers.orElse(new StatIntMap());
    }

    //EVs
    public Map<Stat, Integer> getEVs()
    {
        return this.evs.orElse(StatIntMap.from(this.specific.getString("evs")));
    }

    public void addEVs(Stat s, int amount)
    {
        int ev = this.getEVs().get(s) + amount;

        if(this.getTotalEVs() <= 510 && ev <= 252)
        {
            this.evs = StatIntMap.from(ExtendedHashMap.copy(this.getEVs()).insert(s, ev)).optional();
        }
    }

    public StatIntMap yield()
    {
        return StatIntMap.from(this.data.yield);
    }

    public void gainEVs(PokemonNew defeated)
    {
        for(Stat s : Stat.values()) this.addEVs(s, defeated.yield().get(s));
    }

    public int getTotalEVs()
    {
        return this.getEVs().values().stream().mapToInt(ev -> ev).sum();
    }

    //IVs
    public Map<Stat, Integer> getIVs()
    {
        return this.ivs.orElse(StatIntMap.from(this.specific.getString("ivs")));
    }

    public void createIVs()
    {
        Map<Stat, Integer> ivs = new StatIntMap();
        for(Stat s : Stat.values()) ivs.put(s, new Random().nextInt(31) + 1);
        this.ivs = Optional.of(ivs);
    }

    public void createIVs(int min)
    {
        do { this.createIVs(); }
        while(this.getTotalIV() < min);
    }

    public double getTotalIV()
    {
        return this.getIVs().values().stream().mapToDouble(iv -> iv / 31D).sum() * 100D / 6D;
    }

    public String getTotalIVString()
    {
        return String.format("%.2f", this.getTotalIV());
    }

    //Nature
    public Nature getNature()
    {
        return this.nature.orElse(Nature.cast(this.specific.getString("nature")));
    }

    public void setNature(Nature nature)
    {
        this.nature = Optional.of(nature);
    }

    //EXP
    public int getExp()
    {
        return this.exp.orElse(this.specific.getInteger("exp"));
    }

    public void setExp(int exp)
    {
        this.exp = Optional.of(exp);
    }

    public void addExp(int amount)
    {
        if(this.getLevel() > 100) return;

        this.setExp(this.getExp() + amount);

        int required = GrowthRate.getRequiredExp(this.data.growthRate, this.getLevel());

        while(this.getExp() >= required && this.getLevel() <= 100)
        {
            this.setLevel(this.getLevel() + 1);
            this.setExp(this.getExp() - required);
            required = GrowthRate.getRequiredExp(this.data.growthRate, this.getLevel());
        }
    }

    //Level
    public int getLevel()
    {
        return this.level.orElse(this.specific.getInteger("level"));
    }

    public void setLevel(int level)
    {
        this.level = Optional.of(level);
    }

    //Type
    public List<Type> getType()
    {
        return this.type.orElse(this.data.types);
    }

    public void setType(int index, Type t)
    {
        List<Type> types = new ArrayList<>(List.copyOf(this.getType()));
        types.set(index, t);

        this.type = Optional.of(types);
    }

    public boolean isType(Type type)
    {
        return this.getType().stream().anyMatch(t -> t.equals(type));
    }

    //Shiny
    public boolean isShiny()
    {
        return this.shiny.orElse(this.specific.getBoolean("shiny"));
    }

    public void setShiny(boolean shiny)
    {
        this.shiny = Optional.of(shiny);
    }

    //Name and Nickname
    public String getName()
    {
        return this.name.orElse(this.data.name);
    }

    public void setName(String name)
    {
        this.name = Optional.of(name);
    }

    public String getNickname()
    {
        return this.nickname.orElse(this.specific.getString("nickname"));
    }

    public void setNickname(String nickname)
    {
        this.nickname = Optional.of(nickname);
    }

    public boolean hasNickname()
    {
        return !this.getNickname().equals("");
    }

    public String getDisplayName()
    {
        return this.hasNickname() ? "\"" + this.getNickname() + "\"" : this.getName();
    }

    //UUID
    private void setUUID(String UUID)
    {
        this.UUID = UUID;
    }

    private void setUUID()
    {
        StringBuilder uuid = new StringBuilder();
        for(int i = 0; i < 6; i++) uuid.append(IDHelper.alphanumeric(4)).append("-");
        this.setUUID(uuid.substring(0, uuid.toString().length() - 1));
    }

    public String getUUID()
    {
        return this.UUID;
    }

    public Bson query()
    {
        return Filters.eq("UUID", this.UUID);
    }

    //Data and PokemonData DB Document
    private void setData(String name)
    {
        this.data = DataHelper.pokeData(name).copy();
    }

    private void setSpecificDocument()
    {
        this.specific = Mongo.PokemonData.find(this.query()).first();
    }
}
