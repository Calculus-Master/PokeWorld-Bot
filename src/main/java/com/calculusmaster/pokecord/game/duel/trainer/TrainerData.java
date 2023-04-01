package com.calculusmaster.pokecord.game.duel.trainer;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestrictionRegistry;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class TrainerData
{
    private String trainerID;
    private final String name;
    private final int trainerClass;
    private final List<PokemonEntity> team;
    private final ZCrystal zcrystal;
    private final int averagePokemonLevel;
    private final float multiplier;
    private List<TeamRestriction> restrictions;

    //For TrainerData Creation
    public TrainerData(String name, int trainerClass, List<PokemonEntity> team, ZCrystal zcrystal, int level, float multiplier)
    {
        this.trainerID = IDHelper.alphanumeric(12);
        this.name = name;
        this.trainerClass = trainerClass;
        this.team = team;
        this.zcrystal = zcrystal;
        this.averagePokemonLevel = level;
        this.multiplier = multiplier;
        this.restrictions = new ArrayList<>();
    }

    public Document serialize()
    {
        return new Document()
                .append("trainerID", this.trainerID)
                .append("name", this.name)
                .append("trainerClass", this.trainerClass)
                .append("team", this.team.stream().map(PokemonEntity::toString).toList())
                .append("zcrystal", this.zcrystal == null ? "" : this.zcrystal.toString())
                .append("averagePokemonLevel", this.averagePokemonLevel)
                .append("multiplier", this.multiplier)
                .append("restrictions", this.restrictions.stream().map(TeamRestriction::getRestrictionID).toList());
    }

    public TrainerData(Document data)
    {
        this(
                data.getString("name"),
                data.getInteger("trainerClass"),
                data.getList("team", String.class).stream().map(PokemonEntity::cast).toList(),
                ZCrystal.cast(data.getString("zcrystal")),
                data.getInteger("averagePokemonLevel"),
                data.getDouble("multiplier").floatValue()
        );

        this.trainerID = data.getString("trainerID");
        this.restrictions = data.getList("restrictions", String.class).stream().map(TeamRestrictionRegistry::getRestrictionByID).toList();
    }

    public void addRestriction(TeamRestriction restriction)
    {
        this.restrictions.add(restriction);
    }

    public String getTrainerID()
    {
        return this.trainerID;
    }

    public String getName()
    {
        return this.name;
    }

    public int getTrainerClass()
    {
        return this.trainerClass;
    }

    public List<PokemonEntity> getTeam()
    {
        return this.team;
    }

    public ZCrystal getZCrystal()
    {
        return this.zcrystal;
    }

    public int getAveragePokemonLevel()
    {
        return this.averagePokemonLevel;
    }

    public float getMultiplier()
    {
        return this.multiplier;
    }

    public List<TeamRestriction> getRestrictions()
    {
        return this.restrictions;
    }
}
