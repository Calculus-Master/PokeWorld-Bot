package com.calculusmaster.pokecord.game.pokemon.evolution;

import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonStats;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.cache.CacheHandler;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.*;

import static com.calculusmaster.pokecord.game.enums.elements.EggGroup.DITTO;
import static com.calculusmaster.pokecord.game.enums.elements.Gender.FEMALE;
import static com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity.*;

public class PokemonEgg
{
    public static int MAX_EGGS;

    private final Random r = new Random();
    private String eggID;
    private PokemonEntity target;
    private int exp;
    private int max;
    private PokemonStats ivs;
    private EnumSet<MoveEntity> eggMoves;
    private Ability ability;

    public static PokemonEgg create(Pokemon parent1, Pokemon parent2)
    {
        //Assumes breeding conditions have been satisfied

        PokemonEgg egg = new PokemonEgg();
        PokemonEntity target;

        if(parent1.getEggGroups().contains(DITTO) && parent2.getEggGroups().contains(DITTO))
        {
            Pokemon notDitto = parent1.getEggGroups().contains(DITTO) ? parent2 : parent1;

            if(notDitto.is(MANAPHY) || notDitto.is(PHIONE)) target = PHIONE;
            else target = notDitto.getEntity();
        }
        else
        {
            PokemonEntity current = parent1.getGender().equals(FEMALE) ? parent1.getEntity() : parent2.getEntity();

            PokemonEntity prev = EvolutionRegistry.getPreviousEvolution(current);
            while(prev != null)
            {
                current = prev;
                prev = EvolutionRegistry.getPreviousEvolution(current);
            }

            target = current;
        }

        //Nidoran | Illumise/Volbeat
        if(target.equals(NIDORAN_F) || target.equals(NIDORAN_M)) target = egg.r.nextBoolean() ? NIDORAN_F : NIDORAN_M;
        if(target.equals(ILLUMISE) || target.equals(VOLBEAT)) target = egg.r.nextBoolean() ? ILLUMISE : VOLBEAT;

        egg.setEggID();
        egg.setTarget(target);
        egg.setExp(0);
        egg.setMaxExp((int)(255 * (target.data().getRawHatchTarget() + 1) * (Math.random() + 1)));
        egg.setIVs(parent1, parent2);
        egg.setEggMoves(parent1, parent2);
        egg.setAbility(parent1, parent2);

        return egg;
    }

    public static PokemonEgg build(Document data)
    {
        PokemonEgg egg = new PokemonEgg();

        egg.setEggID(data.getString("eggID"));
        egg.setTarget(PokemonEntity.cast(data.getString("target")));
        egg.setExp(data.getInteger("exp"));
        egg.setMaxExp(data.getInteger("max"));
        egg.setIVs(data.get("ivs", Document.class));
        egg.setEggMoves(data.getList("moves", String.class));
        egg.setAbility(data.getString("ability"));

        return egg;
    }

    public static PokemonEgg build(String eggID)
    {
        Document cache = CacheHandler.EGG_DATA.get(eggID, id ->
        {
            LoggerHelper.info(PokemonEgg.class, "Loading new EggData into Cache for ID: " + id + ".");

            return Mongo.EggData.find(Filters.eq("eggID", id)).first();
        });

        if(cache == null)
        {
            LoggerHelper.error(PokemonEgg.class, "Null Egg Data for EggID: " + eggID + ".");
            return null;
        }
        else return PokemonEgg.build(cache);
    }

    public void upload()
    {
        Document eggData = new Document()
                .append("eggID", this.getEggID())
                .append("target", this.getTarget().toString())
                .append("exp", this.getExp())
                .append("max", this.getMaxExp())
                .append("ivs", this.getIVs().serialized())
                .append("moves", this.getEggMoves().stream().map(MoveEntity::toString).toList())
                .append("ability", this.ability == null ? "" : this.ability.toString());

        LoggerHelper.logDatabaseInsert(PokemonEgg.class, eggData);

        Mongo.EggData.insertOne(eggData);
    }

    public Pokemon hatch()
    {
        Pokemon hatched = Pokemon.create(this.getTarget());

        //IVs
        PokemonStats ivs = new PokemonStats();
        for(Stat s : Stat.values())
            ivs.set(s, this.ivs.get(s) == -1 ? hatched.getIVs().get(s) : this.ivs.get(s));
        hatched.setIVs(ivs.get());

        //Egg Moves
        hatched.setAvailableEggMoves(this.eggMoves);

        //Ability
        if(this.ability != null) hatched.setAbility(this.ability);

        Mongo.EggData.deleteOne(Filters.eq("eggID", this.getEggID()));
        return hatched;
    }

    public boolean canHatch()
    {
        return this.exp >= this.max;
    }

    public String getOverview()
    {
        return "ID: " + this.eggID + "\nEXP: `" + this.exp + " / " + this.max + "` XP";
    }

    public void setAbility(Pokemon parent1, Pokemon parent2)
    {
        Pokemon female = parent1.getEggGroups().contains(DITTO) ? parent2 : parent2.getEggGroups().contains(DITTO) ? parent1 : parent1.getGender().equals(FEMALE) ? parent1 : parent2;

        if(this.r.nextFloat() < 0.6F) this.ability = female.getAbility();
        else this.ability = null;
    }

    public void setAbility(String ability)
    {
        this.ability = ability.isEmpty() ? null : Ability.valueOf(ability);
    }

    public Ability getAbility()
    {
        return this.ability;
    }

    public EnumSet<MoveEntity> getEggMoves()
    {
        return this.eggMoves;
    }

    public void setEggMoves(Pokemon parent1, Pokemon parent2)
    {
        List<MoveEntity> pool = new ArrayList<>();
        pool.addAll(parent1.getData().getEggMoves());
        pool.addAll(parent2.getData().getEggMoves());

        float baseChance = 0.5F;
        if(parent1.hasPrestiged()) baseChance += 0.025F * parent1.getPrestigeLevel();
        if(parent2.hasPrestiged()) baseChance += 0.025F * parent2.getPrestigeLevel();
        final float chance = baseChance;

        this.eggMoves = EnumSet.noneOf(MoveEntity.class);
        pool.stream().filter(e -> this.r.nextFloat() < chance).forEach(this.eggMoves::add);
    }

    public void setEggMoves(List<String> moves)
    {
        this.eggMoves = moves.isEmpty() ? EnumSet.noneOf(MoveEntity.class) : EnumSet.copyOf(moves.stream().map(MoveEntity::valueOf).toList());
    }

    public PokemonStats getIVs()
    {
        return this.ivs;
    }

    public void setIVs(Pokemon parent1, Pokemon parent2)
    {
        PokemonStats ivs = new PokemonStats();

        int number = parent1.getItem().equals(Item.DESTINY_KNOT) || parent2.getItem().equals(Item.DESTINY_KNOT) ? 5 : 3;

        List<Stat> stats = new ArrayList<>(Arrays.asList(Stat.values()));
        Collections.shuffle(stats);
        stats.subList(0, 6 - number).clear();

        for(Stat s : Stat.values())
            ivs.set(s, stats.contains(s) ? (this.r.nextBoolean() ? parent1 : parent2).getIVs().get(s) : -1);

        this.ivs = ivs;
    }

    public void setIVs(Document ivs)
    {
        this.ivs = new PokemonStats(ivs);
    }

    public void addExp(int amount)
    {
        if(this.exp >= this.max) return;

        this.exp = Global.clamp(this.exp + amount, 0, this.max);

        Mongo.EggData.updateOne(Filters.eq("eggID", this.getEggID()), Updates.set("exp", this.exp));
    }

    public void setExp(int exp)
    {
        this.exp = exp;
    }

    public int getExp()
    {
        return this.exp;
    }

    public void setMaxExp(int max)
    {
        this.max = max;
    }

    public int getMaxExp()
    {
        return this.max;
    }

    public void setTarget(PokemonEntity target)
    {
        this.target = target;
    }

    public PokemonEntity getTarget()
    {
        return this.target;
    }

    public void setEggID(String eggID)
    {
        this.eggID = eggID;
    }

    public void setEggID()
    {
        this.setEggID(IDHelper.numeric(6));
    }

    public String getEggID()
    {
        return this.eggID;
    }
}
