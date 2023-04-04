package com.calculusmaster.pokecord.game.pokemon.data.api;

import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.util.Global;
import kotlin.Pair;
import org.bson.Document;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;

import static com.calculusmaster.pokecord.game.pokemon.data.api.PokeAPIHelper.GET;

public class PokeAPIQuery
{
    private final PokemonEntity entity;
    private final String apiID;
    private final JSONObject pokemonData;
    private final JSONObject speciesData;

    public PokeAPIQuery(PokemonEntity entity)
    {
        this.entity = entity;
        this.apiID = entity.getAPIID();
        this.pokemonData = GET("pokemon/" + this.apiID.toLowerCase() + "/");

        String fullSpeciesEndpoint = this.pokemonData.getJSONObject("species").getString("url");
        this.speciesData = GET(fullSpeciesEndpoint.substring(fullSpeciesEndpoint.lastIndexOf("v2/") + 3));
    }

    public Document createDataJSON()
    {
        Document data = new Document(new LinkedHashMap<>());

        data.append("entity", this.entity.toString());
        data.append("name", this.entity.getName());

        //Background Info
        Document backgroundInformation = new Document(new LinkedHashMap<>());

            backgroundInformation.append("dex", this.entity.getDex());
            backgroundInformation.append("genus", this.getGenus());
            backgroundInformation.append("height", this.getHeight());
            backgroundInformation.append("weight", this.getWeight());
            backgroundInformation.append("types", this.getTypes());
            backgroundInformation.append("flavor", this.getFlavorText());
            backgroundInformation.append("species", this.getSpecies());
            backgroundInformation.append("default", this.getIsDefault());

        data.append("background", backgroundInformation);

        //Experience and Stats
        Document stats = new Document(new LinkedHashMap<>());

            stats.append("baseStats", this.getBaseStats());
            stats.append("yield", this.getEVYield());
            stats.append("exp", this.getBaseExperience());
            stats.append("growthRate", this.getGrowthRate());
            stats.append("captureRate", this.getCaptureRate());

        data.append("stats", stats);

        //Breeding
        Document breeding = new Document(new LinkedHashMap<>());

            breeding.append("genderRate", this.getGenderRate());
            breeding.append("eggGroups", this.getEggGroups());
            breeding.append("happiness", this.getBaseHappiness());
            breeding.append("hatchTarget", this.getHatchTarget());
            breeding.append("genderDifferences", this.hasGenderDifferences());

        data.append("breeding", breeding);

        //Abilities
        Document abilities = new Document(new LinkedHashMap<>());

            abilities.append("main", this.getAbilities());
            abilities.append("hidden", this.getHiddenAbilities());

        data.append("abilities", abilities);

        //Moves
        Document moves = new Document(new LinkedHashMap<>());

            moves.append("level", this.getLevelUpMoves());
            moves.append("egg", this.getEggMoves());
            moves.append("tm", this.getTMMoves());
            moves.append("tutor", this.getTutorMoves());

        data.append("moves", moves);

        return data;
    }

    private boolean hasGenderDifferences()
    {
        return this.speciesData.getBoolean("has_gender_differences");
    }

    private boolean getIsDefault()
    {
        return this.pokemonData.getBoolean("is_default");
    }

    private String getSpecies()
    {
        return this.pokemonData.getJSONObject("species").getString("name");
    }

    private Document getTutorMoves()
    {
        Map<Integer, List<String>> movesByVersion = new HashMap<>();
        for(Object rawMoveData : this.pokemonData.getJSONArray("moves"))
        {
            JSONObject moveData = (JSONObject) rawMoveData;

            String moveName = moveData.getJSONObject("move").getString("name").replaceAll("-", "_").toUpperCase();

            for(Object rawVersionData : moveData.getJSONArray("version_group_details"))
            {
                JSONObject versionData = (JSONObject) rawVersionData;

                if(versionData.getJSONObject("move_learn_method").getString("name").equalsIgnoreCase("tutor"))
                {
                    String versionURL = versionData.getJSONObject("version_group").getString("url");
                    int ver = Integer.parseInt(versionURL.split("/")[6].trim());

                    if(!movesByVersion.containsKey(ver)) movesByVersion.put(ver, new ArrayList<>());

                    movesByVersion.get(ver).add(moveName);
                }
            }
        }

        movesByVersion.remove(19); //Let's Go versions

        Document moves = new Document(new LinkedHashMap<>());

        //No Tutor Moves
        if(movesByVersion.isEmpty()) return moves.append("moves", new ArrayList<>()).append("version", "");

        List<Integer> versionsSorted = movesByVersion.keySet().stream().sorted().toList();
        int latestVersion = versionsSorted.get(versionsSorted.size() - 1);

        //Latest Version - can switch to do all versions if needed
        List<String> list = movesByVersion.get(latestVersion);
        moves.append("moves", list);

        String targetVersionName = Global.normalize(GET("version-group/" + latestVersion).getString("name").replaceAll("-", " "));
        if(targetVersionName.split("\\s+").length % 2 == 0)
        {
            String[] v = targetVersionName.split("\\s+");
            int middle = v.length / 2;

            String targetVersionNameRebuilt = "";
            for(int i = 0; i < middle; i++) targetVersionNameRebuilt += v[i] + " ";
            targetVersionNameRebuilt += "| ";
            for(int i = middle; i < v.length; i++) targetVersionNameRebuilt += v[i] + " ";
            targetVersionName = targetVersionNameRebuilt.trim();
        }
        else targetVersionName = targetVersionName.replaceAll("and", "|");

        moves.append("version", targetVersionName);

        return moves;
    }

    private Document getTMMoves()
    {
        Map<Integer, List<String>> movesByVersion = new HashMap<>();
        for(Object rawMoveData : this.pokemonData.getJSONArray("moves"))
        {
            JSONObject moveData = (JSONObject) rawMoveData;

            String moveName = moveData.getJSONObject("move").getString("name").replaceAll("-", "_").toUpperCase();

            for(Object rawVersionData : moveData.getJSONArray("version_group_details"))
            {
                JSONObject versionData = (JSONObject) rawVersionData;

                if(versionData.getJSONObject("move_learn_method").getString("name").equalsIgnoreCase("machine"))
                {
                    String versionURL = versionData.getJSONObject("version_group").getString("url");
                    int ver = Integer.parseInt(versionURL.split("/")[6].trim());

                    if(!movesByVersion.containsKey(ver)) movesByVersion.put(ver, new ArrayList<>());

                    movesByVersion.get(ver).add(moveName);
                }
            }
        }

        movesByVersion.remove(19); //Let's Go versions

        Document moves = new Document(new LinkedHashMap<>());

        //No TM Moves
        if(movesByVersion.isEmpty()) return moves.append("moves", new ArrayList<>()).append("version", "");

        List<Integer> versionsSorted = movesByVersion.keySet().stream().sorted().toList();
        int latestVersion = versionsSorted.get(versionsSorted.size() - 1);

        //Latest Version - can switch to do all versions if needed
        List<String> list = movesByVersion.get(latestVersion);
        moves.append("moves", list);

        String targetVersionName = Global.normalize(GET("version-group/" + latestVersion).getString("name").replaceAll("-", " "));
        if(targetVersionName.split("\\s+").length % 2 == 0)
        {
            String[] v = targetVersionName.split("\\s+");
            int middle = v.length / 2;

            String targetVersionNameRebuilt = "";
            for(int i = 0; i < middle; i++) targetVersionNameRebuilt += v[i] + " ";
            targetVersionNameRebuilt += "| ";
            for(int i = middle; i < v.length; i++) targetVersionNameRebuilt += v[i] + " ";
            targetVersionName = targetVersionNameRebuilt.trim();
        }
        else targetVersionName = targetVersionName.replaceAll("and", "|");

        moves.append("version", targetVersionName);

        return moves;
    }

    private Document getEggMoves()
    {
        Map<Integer, List<String>> movesByVersion = new HashMap<>();
        for(Object rawMoveData : this.pokemonData.getJSONArray("moves"))
        {
            JSONObject moveData = (JSONObject) rawMoveData;

            String moveName = moveData.getJSONObject("move").getString("name").replaceAll("-", "_").toUpperCase();

            for(Object rawVersionData : moveData.getJSONArray("version_group_details"))
            {
                JSONObject versionData = (JSONObject) rawVersionData;

                if(versionData.getJSONObject("move_learn_method").getString("name").equalsIgnoreCase("egg"))
                {
                    String versionURL = versionData.getJSONObject("version_group").getString("url");
                    int ver = Integer.parseInt(versionURL.split("/")[6].trim());

                    if(!movesByVersion.containsKey(ver)) movesByVersion.put(ver, new ArrayList<>());

                    movesByVersion.get(ver).add(moveName);
                }
            }
        }

        movesByVersion.remove(19); //Let's Go versions

        Document moves = new Document(new LinkedHashMap<>());

        //No Egg Moves
        if(movesByVersion.isEmpty()) return moves.append("moves", new ArrayList<>()).append("version", "");

        List<Integer> versionsSorted = movesByVersion.keySet().stream().sorted().toList();
        int latestVersion = versionsSorted.get(versionsSorted.size() - 1);

        //Latest Version - can switch to do all versions if needed
        List<String> list = movesByVersion.get(latestVersion);
        moves.append("moves", list);

        String targetVersionName = Global.normalize(GET("version-group/" + latestVersion).getString("name").replaceAll("-", " "));
        if(targetVersionName.split("\\s+").length % 2 == 0)
        {
            String[] v = targetVersionName.split("\\s+");
            int middle = v.length / 2;

            String targetVersionNameRebuilt = "";
            for(int i = 0; i < middle; i++) targetVersionNameRebuilt += v[i] + " ";
            targetVersionNameRebuilt += "| ";
            for(int i = middle; i < v.length; i++) targetVersionNameRebuilt += v[i] + " ";
            targetVersionName = targetVersionNameRebuilt.trim();
        }
        else targetVersionName = targetVersionName.replaceAll("and", "|");

        moves.append("version", targetVersionName);

        return moves;
    }

    private Document getLevelUpMoves()
    {
        Map<Integer, List<Pair<String, Integer>>> moveLevelsByVersion = new HashMap<>();
        for(Object rawMoveData : this.pokemonData.getJSONArray("moves"))
        {
            JSONObject moveData = (JSONObject) rawMoveData;

            String moveName = moveData.getJSONObject("move").getString("name").replaceAll("-", "_").toUpperCase();

            for(Object rawVersionData : moveData.getJSONArray("version_group_details"))
            {
                JSONObject versionData = (JSONObject) rawVersionData;

                if(versionData.getJSONObject("move_learn_method").getString("name").equalsIgnoreCase("level-up"))
                {
                    String versionURL = versionData.getJSONObject("version_group").getString("url");
                    int ver = Integer.parseInt(versionURL.split("/")[6].trim());

                    if(!moveLevelsByVersion.containsKey(ver)) moveLevelsByVersion.put(ver, new ArrayList<>());

                    moveLevelsByVersion.get(ver).add(new Pair<>(moveName, versionData.getInt("level_learned_at")));
                }
            }
        }

        moveLevelsByVersion.remove(19); //Let's Go versions

        Document moves = new Document(new LinkedHashMap<>());
        List<Integer> versionsSorted = moveLevelsByVersion.keySet().stream().sorted().toList();
        int latestVersion = versionsSorted.get(versionsSorted.size() - 1);

        //Latest Version - can switch to do all versions if needed
        Document moveLevels = new Document(new LinkedHashMap<>());
        List<Pair<String, Integer>> list = moveLevelsByVersion.get(latestVersion);
        list.sort(Comparator.comparingInt(Pair::getSecond));
        for(Pair<String, Integer> p : list) moveLevels.append(p.getFirst(), p.getSecond());

        moves.append("moves", moveLevels);

        String targetVersionName = Global.normalize(GET("version-group/" + latestVersion).getString("name").replaceAll("-", " "));
        if(targetVersionName.split("\\s+").length % 2 == 0)
        {
            String[] v = targetVersionName.split("\\s+");
            int middle = v.length / 2;

            String targetVersionNameRebuilt = "";
            for(int i = 0; i < middle; i++) targetVersionNameRebuilt += v[i] + " ";
            targetVersionNameRebuilt += "| ";
            for(int i = middle; i < v.length; i++) targetVersionNameRebuilt += v[i] + " ";
            targetVersionName = targetVersionNameRebuilt.trim();
        }
        else targetVersionName = targetVersionName.replaceAll("and", "|");

        moves.append("version", targetVersionName);

        return moves;
    }

    private List<String> getFlavorText()
    {
        List<String> flavorText = new ArrayList<>();

        for(Object o : this.speciesData.getJSONArray("flavor_text_entries"))
        {
            JSONObject j = (JSONObject)o;

            if(j.getJSONObject("language").getString("name").equalsIgnoreCase("en"))
            {
                String flavor = j.getString("flavor_text")
                        .replaceAll("\n", " ")
                        .replaceAll("\f", " ")
                        .replaceAll("\u00ad ", "")
                        .replaceAll(this.entity.getName().toUpperCase(), this.entity.getName())
                        .replaceAll("POKéMON", "Pokémon")
                        .replaceAll("\\s+", " ")
                        .trim();

                String[] flavorSplit = flavor.split(" ");
                for(int i = 0; i < flavorSplit.length; i++)
                    if(flavorSplit[i].length() > 4
                            && flavorSplit[i].replaceAll("’s", "").toUpperCase().equals(flavorSplit[i].replaceAll("’s", ""))) flavorSplit[i] = Global.normalize(flavorSplit[i]);
                flavor = String.join(" ", flavorSplit);

                flavorText.add(flavor);
            }
        }

        return flavorText.stream().distinct().toList();
    }

    private List<String> getHiddenAbilities()
    {
        List<Ability> abilities = new ArrayList<>();

        for(Object o : this.pokemonData.getJSONArray("abilities"))
        {
            JSONObject j = (JSONObject)o;
            if(j.getBoolean("is_hidden"))
                abilities.add(Ability.cast(j.getJSONObject("ability").getString("name").replaceAll("-", "_")));
        }

        return abilities.stream().map(Ability::toString).toList();
    }

    private List<String> getAbilities()
    {
        List<Ability> abilities = new ArrayList<>();

        for(Object o : this.pokemonData.getJSONArray("abilities"))
        {
            JSONObject j = (JSONObject)o;
            if(!j.getBoolean("is_hidden"))
                abilities.add(Ability.cast(j.getJSONObject("ability").getString("name").replaceAll("-", "_")));
        }

        return abilities.stream().map(Ability::toString).toList();
    }

    private int getHatchTarget()
    {
        try { return this.speciesData.getInt("hatch_counter"); }
        catch(Exception e) { return 0; }
    }

    private int getBaseHappiness()
    {
        try { return this.speciesData.getInt("base_happiness"); }
        catch(Exception e) { return 0; }
    }

    private List<String> getEggGroups()
    {
        List<EggGroup> eggGroups = new ArrayList<>();

        for(Object o : this.speciesData.getJSONArray("egg_groups"))
        {
            String eggGroupName = ((JSONObject)o).getString("name");

            EggGroup egg = EggGroup.cast(eggGroupName);

            if(egg == null) egg = switch(eggGroupName) {
                case "water1" -> EggGroup.WATER_1;
                case "water2" -> EggGroup.WATER_2;
                case "water3" -> EggGroup.WATER_3;
                case "humanshape" -> EggGroup.HUMAN_LIKE;
                case "no-eggs" -> EggGroup.NO_EGGS;
                case "indeterminate" -> EggGroup.AMORPHOUS;
                case "ground" -> EggGroup.FIELD;
                case "plant" -> EggGroup.GRASS;
                default -> throw new IllegalStateException("Unexpected Egg Group: " + eggGroupName);
            };

            eggGroups.add(egg);
        }

        return eggGroups.stream().map(EggGroup::toString).toList();
    }

    private int getCaptureRate()
    {
        return this.speciesData.getInt("capture_rate");
    }

    private int getGenderRate()
    {
        return this.speciesData.getInt("gender_rate");
    }

    private int getBaseExperience()
    {
        try { return this.pokemonData.getInt("base_experience"); }
        catch(Exception e) { return 0; }
    }

    private List<Integer> getEVYield()
    {
        List<Integer> yield = new ArrayList<>();
        List<String> indices = List.of("hp", "attack", "defense", "special-attack", "special-defense", "speed");

        for(Object o : this.pokemonData.getJSONArray("stats"))
        {
            JSONObject statData = (JSONObject)o;
            String statName = statData.getJSONObject("stat").getString("name");

            yield.add(indices.indexOf(statName), statData.getInt("effort"));
        }

        return yield;
    }

    private List<Integer> getBaseStats()
    {
        List<Integer> stats = new ArrayList<>();
        List<String> indices = List.of("hp", "attack", "defense", "special-attack", "special-defense", "speed");

        for(Object o : this.pokemonData.getJSONArray("stats"))
        {
            JSONObject statData = (JSONObject)o;
            String statName = statData.getJSONObject("stat").getString("name");

            stats.add(indices.indexOf(statName), statData.getInt("base_stat"));
        }

        return stats;
    }

    private String getGrowthRate()
    {
        String r = this.speciesData.getJSONObject("growth_rate").getString("name");

        GrowthRate g = GrowthRate.cast(r);
        if(g == null)
        {
            g = switch(r) {
                case "medium" -> GrowthRate.MEDIUM_FAST;
                case "medium-slow" -> GrowthRate.MEDIUM_SLOW;
                case "slow-then-very-fast" -> GrowthRate.ERRATIC;
                case "fast-then-very-slow" -> GrowthRate.FLUCTUATING;
                default -> throw new IllegalStateException("Unexpected Growth Rate Value: " + r);
            };
        }

        return g.toString();
    }

    private List<String> getTypes()
    {
        List<Type> t = new ArrayList<>();
        for(Object o : this.pokemonData.getJSONArray("types"))
            t.add(Type.cast(((JSONObject)o).getJSONObject("type").getString("name")));
        return t.stream().map(Type::toString).toList();
    }

    private double getWeight()
    {
        //Weight given in Hectograms
        return BigDecimal
                .valueOf(this.pokemonData.getDouble("weight"))
                .multiply((BigDecimal.valueOf(0.1D)))
                .doubleValue();
    }

    private double getHeight()
    {
        //Height given in Decimeters
        return BigDecimal
                .valueOf(this.pokemonData.getDouble("height"))
                .multiply((BigDecimal.valueOf(0.1D)))
                .doubleValue();
    }

    //"x Pokemon" information
    private String getGenus()
    {
        String genus = null;

        for(Object o : this.speciesData.getJSONArray("genera"))
        {
            JSONObject j = (JSONObject)o;
            if(j.getJSONObject("language").getString("name").equals("en")) genus = j.getString("genus");
        }

        return genus == null ? "" : genus;
    }
}
