package com.calculusmaster.pokecord.game.pokemon.data;

import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.moves.data.MoveData;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonStats;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PokemonData
{
    private final PokemonEntity entity; public PokemonEntity getEntity() { return this.entity; }
    private final String name; public String getName() { return this.name; }

    //Background
    private final int dex; public int getDex() { return this.dex; }
    private final String genus; public String getGenus() { return this.genus; }
    private final double height; public double getHeight() { return this.height; }
    private final double weight; public double getWeight() { return this.weight; }
    private final List<Type> types; public List<Type> getTypes() { return this.types; }
    private final List<String> flavorText; public List<String> getFlavorText() { return this.flavorText; }

    //Stats
    private final PokemonStats baseStats; public PokemonStats getBaseStats() { return this.baseStats; }
    private final PokemonStats yield; public PokemonStats getEVYield() { return this.yield; }
    private final int baseExperience; public int getBaseExperience() { return this.baseExperience; }
    private final GrowthRate growthRate; public GrowthRate getGrowthRate() { return this.growthRate; }
    private final int captureRate; public int getCaptureRate() { return this.captureRate; }

    //Breeding
    private final int genderRate; public int getGenderRate() { return this.genderRate; }
    private final EnumSet<EggGroup> eggGroups; public EnumSet<EggGroup> getEggGroups() { return this.eggGroups; }
    private final int baseHappiness; public int getBaseHappiness() { return this.baseHappiness; }
    private final int rawHatchTarget; public int getRawHatchTarget() { return this.rawHatchTarget; }

    //Abilities
    private final EnumSet<Ability> mainAbilities; public EnumSet<Ability> getMainAbilities() { return this.mainAbilities; }
    private final EnumSet<Ability> hiddenAbilities; public EnumSet<Ability> getHiddenAbilities() { return this.hiddenAbilities; }

    //Moves
    private final Map<MoveEntity, Integer> levelUpMoves; public Map<MoveEntity, Integer> getLevelUpMoves() { return this.levelUpMoves; }
    private final String levelUpMovesVersion; public String getLevelUpMovesVersion() { return this.levelUpMovesVersion; }

    private final EnumSet<MoveEntity> eggMoves; public EnumSet<MoveEntity> getEggMoves() { return this.eggMoves; }
    private final String eggMovesVersion; public String getEggMovesVersion() { return this.eggMovesVersion; }

    private final EnumSet<MoveEntity> tms; public EnumSet<MoveEntity> getTMs() { return this.tms; }
    private final String tmsVersion; public String getTMsVersion() { return this.tmsVersion; }

    private final EnumSet<MoveEntity> tutorMoves; public EnumSet<MoveEntity> getTutorMoves() { return this.tutorMoves; }
    private final String tutorMovesVersion; public String getTutorMovesVersion() { return this.tutorMovesVersion; }

    public PokemonData(PokemonEntity entity)
    {
        this.entity = entity;
        this.name = entity.getName();
        this.dex = entity.getDex();

        JSONObject json = this.readDataJSON();

        JSONObject background = json.getJSONObject("background");
        this.genus = background.getString("genus");
        this.height = background.getDouble("height");
        this.weight = background.getDouble("weight");
        this.types = background.getJSONArray("types").toList().stream().map(o -> Type.valueOf(o.toString())).toList();
        this.flavorText = background.getJSONArray("flavor").toList().stream().map(Object::toString).toList();

        JSONObject stats = json.getJSONObject("stats");
        this.baseStats = new PokemonStats(); for(Stat s : Stat.values()) this.baseStats.set(s, stats.getJSONArray("baseStats").getInt(s.ordinal()));
        this.yield = new PokemonStats(); for(Stat s : Stat.values()) this.yield.set(s, stats.getJSONArray("yield").getInt(s.ordinal()));
        this.baseExperience = stats.getInt("exp");
        this.growthRate = GrowthRate.valueOf(stats.getString("growthRate"));
        this.captureRate = stats.getInt("captureRate");

        JSONObject breeding = json.getJSONObject("breeding");
        this.genderRate = breeding.getInt("genderRate");
        this.eggGroups = EnumSet.noneOf(EggGroup.class); for(String s : breeding.getJSONArray("eggGroups").toList().stream().map(Object::toString).toList()) this.eggGroups.add(EggGroup.valueOf(s));
        this.baseHappiness = breeding.getInt("happiness");
        this.rawHatchTarget = breeding.getInt("hatchTarget");

        JSONObject abilities = json.getJSONObject("abilities");
        this.mainAbilities = EnumSet.noneOf(Ability.class); for(String s : abilities.getJSONArray("main").toList().stream().map(Object::toString).toList()) this.mainAbilities.add(Ability.valueOf(s));
        this.hiddenAbilities = EnumSet.noneOf(Ability.class); for(String s : abilities.getJSONArray("hidden").toList().stream().map(Object::toString).toList()) this.hiddenAbilities.add(Ability.valueOf(s));

        JSONObject moves = json.getJSONObject("moves");

        JSONObject levelUpMoves = moves.getJSONObject("level");
        this.levelUpMoves = new LinkedHashMap<>(); for(String s : levelUpMoves.getJSONObject("moves").keySet()) this.levelUpMoves.put(MoveEntity.valueOf(s), levelUpMoves.getJSONObject("moves").getInt(s));
        this.levelUpMovesVersion = levelUpMoves.getString("version");

        JSONObject eggMoves = moves.getJSONObject("egg");
        this.eggMoves = EnumSet.noneOf(MoveEntity.class);
        for(Object o : eggMoves.getJSONArray("moves")) this.eggMoves.add(MoveEntity.valueOf((String)o));
        this.eggMovesVersion = eggMoves.getString("version");

        JSONObject tms = moves.getJSONObject("tm");
        this.tms = EnumSet.noneOf(MoveEntity.class);
        for(Object o : tms.getJSONArray("moves")) this.tms.add(MoveEntity.valueOf((String)o));
        this.tms.removeIf(e -> !TM.isMoveTM(e));
        this.tmsVersion = tms.getString("version");

        JSONObject tutorMoves = moves.getJSONObject("tutor");
        this.tutorMoves = EnumSet.noneOf(MoveEntity.class);
        for(Object o : tutorMoves.getJSONArray("moves")) this.tutorMoves.add(MoveEntity.valueOf((String)o));
        this.tutorMovesVersion = tutorMoves.getString("version");
    }

    private JSONObject readDataJSON()
    {
        try
        {
            URL path = this.getClass().getResource("/data/pokemon/" + this.entity.getJSONFileName() + ".json");
            File f = new File(path.toURI());
            return new JSONObject(new JSONTokener(new FileInputStream(f)));
        }
        catch(URISyntaxException | NullPointerException | IOException e)
        {
            LoggerHelper.error(MoveData.class, "Unable to find PokemonData JSON. PokemonEntity: %s, JSON Name: %s".formatted(this.entity.toString(), this.entity.getJSONFileName() + ".json"));
            e.printStackTrace();
            return new JSONObject();
        }
    }

    @Override
    public String toString() {
        return "PokemonDataNew{" +
                "entity=" + this.entity +
                ", name='" + this.name + '\'' +
                ", dex=" + this.dex +
                ", genus='" + this.genus + '\'' +
                ", height=" + this.height +
                ", weight=" + this.weight +
                ", types=" + this.types +
                ", flavorText=" + this.flavorText +
                ", baseStats=" + this.baseStats +
                ", yield=" + this.yield +
                ", baseExperience=" + this.baseExperience +
                ", growthRate=" + this.growthRate +
                ", captureRate=" + this.captureRate +
                ", genderRate=" + this.genderRate +
                ", eggGroups=" + this.eggGroups +
                ", baseHappiness=" + this.baseHappiness +
                ", rawHatchTarget=" + this.rawHatchTarget +
                ", mainAbilities=" + this.mainAbilities +
                ", hiddenAbilities=" + this.hiddenAbilities +
                ", levelUpMoves=" + this.levelUpMoves +
                ", levelUpMovesVersion='" + this.levelUpMovesVersion + '\'' +
                ", eggMoves=" + this.eggMoves +
                ", eggMovesVersion='" + this.eggMovesVersion + '\'' +
                ", tms=" + this.tms +
                ", tmsVersion='" + this.tmsVersion + '\'' +
                ", tutorMoves=" + this.tutorMoves +
                ", tutorMovesVersion='" + this.tutorMovesVersion + '\'' +
                '}';
    }
}
