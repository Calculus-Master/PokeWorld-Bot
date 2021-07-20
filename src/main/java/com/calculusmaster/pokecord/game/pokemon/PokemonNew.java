package com.calculusmaster.pokecord.game.pokemon;

import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.custom.ExtendedHashMap;
import com.calculusmaster.pokecord.util.custom.StatIntMap;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.stream.Collectors;

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
    private Optional<Integer> dynamaxLevel = Optional.empty();
    private Optional<PokeItem> item = Optional.empty();
    private Optional<TM> tm = Optional.empty();
    private Optional<TR> tr = Optional.empty();
    private Optional<List<String>> learnedMoves = Optional.empty();

    private Optional<Integer> health = Optional.empty();
    private Optional<Double> boost = Optional.empty();
    private Optional<Boolean> isDynamaxed = Optional.empty();
    private Optional<Map<StatusCondition, Boolean>> status = Optional.empty();

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
                .append("shiny", p.isShiny())
                .append("level", p.getLevel())
                .append("exp", p.getExp())
                .append("nature", p.getNature().toString())
                .append("ivs", StatIntMap.to(p.getIVs()))
                .append("evs", StatIntMap.to(p.getEVs()))
                .append("moves", p.condense(p.getLearnedMoves()))
                .append("tm", p.getTM())
                .append("tr", p.getTR())
                .append("item", p.getItem())
                .append("dynamax_level", p.getDynamaxLevel())
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

    //Status Conditions
    public Map<StatusCondition, Boolean> getStatusConditions()
    {
        return this.status.orElse(ExtendedHashMap.createStatusConditionMap());
    }

    public void addStatusCondition(StatusCondition s)
    {
        if((this.isType(Type.ELECTRIC) && s.equals(StatusCondition.PARALYZED)) ||
                (this.isType(Type.ICE) && s.equals(StatusCondition.FROZEN)) ||
                (this.isType(Type.FIRE) && s.equals(StatusCondition.BURNED)) ||
                ((this.isType(Type.POISON) || this.isType(Type.STEEL)) && s.equals(StatusCondition.POISONED))) return;
        else this.status = Optional.of(ExtendedHashMap.copy(this.getStatusConditions()).insert(s, true));
    }

    public void removeStatusCondition(StatusCondition s)
    {
        this.status = Optional.of(ExtendedHashMap.copy(this.getStatusConditions()).insert(s, false));
    }

    public void clearStatusConditions()
    {
        this.status = Optional.of(ExtendedHashMap.copy(this.getStatusConditions()).editEach((s, v) -> false));
    }

    public boolean hasStatusCondition(StatusCondition s)
    {
        return this.getStatusConditions().get(s);
    }

    public boolean hasAnyStatusConditions()
    {
        return this.getStatusConditions().values().stream().noneMatch(v -> v);
    }

    public String getActiveStatusConditions()
    {
        List<String> active = this.getStatusConditions().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).map(StatusCondition::getAbbrev).collect(Collectors.toList());

        if(active.isEmpty()) return "";

        StringBuilder s = new StringBuilder().append("(");
        for(String str : active) s.append(str).append(", ");
        s.deleteCharAt(s.length() - 1).deleteCharAt(s.length() - 1).append(")");

        return s.toString();
    }

    //Moves
    public List<String> getAllMoves()
    {
        List<String> moves = new ArrayList<>();
        moves.addAll(this.data.moves.keySet());
        moves.sort(Comparator.comparingInt(s -> this.data.moves.get(s)));

        if(this.getName().contains("Zygarde") && this.hasItem() && this.getItem().equals(PokeItem.ZYGARDE_CUBE))
        {
            moves.addAll(Arrays.asList("Core Enforcer", "Dragon Dance", "Extreme Speed", "Thousand Arrows", "Thousand Waves"));
        }

        return moves;
    }

    public List<String> getAvailableMoves()
    {
        List<String> moves = new ArrayList<>(List.copyOf(this.getAllMoves())).stream().filter(s -> this.data.moves.get(s) <= this.getLevel()).sorted(Comparator.comparingInt(s -> this.data.moves.get(s))).collect(Collectors.toList());;

        if(this.hasTM()) moves.add(this.getTM().getMoveName());
        if(this.hasTR()) moves.add(this.getTR().getMoveName());

        if(this.getName().contains("Zygarde") && this.hasItem() && this.getItem().equals(PokeItem.ZYGARDE_CUBE))
        {
            Collections.addAll(moves, "Core Enforcer", "Dragon Dance", "Extreme Speed", "Thousand Arrows", "Thousand Waves");
        }

        return moves;
    }

    public List<String> getLearnedMoves()
    {
        return this.learnedMoves.orElse(this.expand(this.specific.getString("moves")));
    }

    public void learnMove(String move, int index)
    {
        List<String> moves = new ArrayList<>(List.copyOf(this.getLearnedMoves()));
        moves.set(index - 1, move);
        this.learnedMoves = Optional.of(moves);
    }

    private List<String> expand(String condensed)
    {
        return new ArrayList<>(Arrays.asList(condensed.split("-")));
    }

    private String condense(List<String> list)
    {
        StringBuilder condensed = new StringBuilder();
        for(String s : list) condensed.append(Global.normalCase(s));
        return condensed.toString();
    }

    //TM & TR
    public TM getTM()
    {
        return this.tm.orElse(!this.hasTM() ? null : TM.get(this.getTMNumber()));
    }

    public int getTMNumber()
    {
        return this.specific.getInteger("tm");
    }

    public TR getTR()
    {
        return this.tr.orElse(!this.hasTR() ? null : TR.get(this.getTRNumber()));
    }

    public int getTRNumber()
    {
        return this.specific.getInteger("tr");
    }

    public void setTM(TM tm)
    {
        this.tm = Optional.of(tm);
    }

    public void setTR(TR tr)
    {
        this.tr = Optional.of(tr);
    }

    public boolean hasTM()
    {
        return this.getTMNumber() != -1;
    }

    public boolean hasTR()
    {
        return this.getTRNumber() != -1;
    }

    public boolean canLearn(TM tm)
    {
        return this.getValidTMs().contains(tm);
    }

    public boolean canLearn(TR tr)
    {
        return this.getValidTRs().contains(tr);
    }

    public List<TM> getValidTMs()
    {
        return this.data.validTMs;
    }

    public List<TR> getValidTRs()
    {
        return this.data.validTRs;
    }

    //Items
    public PokeItem getItem()
    {
        return this.item.orElse(PokeItem.asItem(this.specific.getString("item")));
    }

    public void setItem(PokeItem item)
    {
        this.item = Optional.of(item);
    }

    public boolean hasItem()
    {
        return this.item.isEmpty() || !this.getItem().equals(PokeItem.NONE);
    }

    public void removeItem()
    {
        this.setItem(PokeItem.NONE);
    }

    //Mega & Forms
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

    public void transform(String form)
    {
        this.setData(Global.normalCase(form));
        //TODO: This used to update database: Pokemon.updateName(this, form);
    }

    //Dynamax and Gigantamax
    public boolean isDynamaxed()
    {
        return this.isDynamaxed.orElse(false);
    }

    public void setDynamax(boolean value)
    {
        this.isDynamaxed = Optional.of(value);
    }

    public int getDynamaxLevel()
    {
        return this.dynamaxLevel.orElse(this.specific.getInteger("dynamax_level"));
    }

    public void increaseDynamaxLevel()
    {
        if(this.getDynamaxLevel() <= 9) this.setDynamaxLevel(this.getDynamaxLevel() + 1);
        //TODO: This used to update database: Pokemon.updateDynamaxLevel(this);
    }

    public void setDynamaxLevel(int level)
    {
        this.dynamaxLevel = Optional.of(level);
    }

    public void enterDynamax()
    {
        double ratio = (double)this.getHealth() / (double)this.getMaxHealth();

        this.setDynamax(true);

        this.setHealth((int)(ratio * this.getMaxHealth()));
    }

    public void exitDynamax()
    {
        double ratio = (double)this.getHealth() / (double)this.getMaxHealth();

        this.setDynamax(true);

        this.setHealth((int)(ratio * this.getMaxHealth()));
    }

    public boolean canGigantamax()
    {
        return existsGigantamax(this.getName());
    }

    public static boolean existsGigantamax(String name)
    {
        return DataHelper.GIGANTAMAX_DATA.containsKey(name);
    }

    public static DataHelper.GigantamaxData getGigantamaxData(String name)
    {
        return DataHelper.GIGANTAMAX_DATA.get(name);
    }

    //Health
    public int getHealth()
    {
        return this.health.orElse(this.getMaxHealth());
    }

    public int getMaxHealth()
    {
        return this.getStat(Stat.HP);
    }

    public boolean isFainted()
    {
        return this.getHealth() <= 0;
    }

    public void setHealth(int HP)
    {
        this.health = Optional.of(Global.clamp(HP, 0, this.getMaxHealth()));
    }

    public void damage(int amount)
    {
        this.setHealth(this.getHealth() - amount);
        DuelHelper.instance(this.getUUID()).addDamage(amount, this.getUUID());
    }

    public void heal(int amount)
    {
        this.setHealth(this.getHealth() + amount);
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

            double dynamaxBoost = this.isDynamaxed() ? 1.0 + (this.getDynamaxLevel() * 0.05 + 0.5) : 1.0;
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

    private double calculateMultiplier(Stat s)
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

    public int getDuelExp(PokemonNew opponent)
    {
        int a = 1;
        int b = this.data.baseEXP;
        int e = 1;
        int L = opponent.getLevel();
        int Lp = this.getLevel();
        int p = 1;
        int s = 1;
        int t = 1;

        return (int)(t * e * p * (1 + (a * b * L / (5.0 * s)) * (Math.pow(2 * L + 10, 2.5) / Math.pow(L + Lp + 10, 2.5))));
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
