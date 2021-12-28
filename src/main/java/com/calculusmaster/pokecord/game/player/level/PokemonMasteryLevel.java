package com.calculusmaster.pokecord.game.player.level;

import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.level.leveltasks.AbstractLevelTask;
import com.calculusmaster.pokecord.game.player.level.leveltasks.ExperienceLevelTask;
import com.calculusmaster.pokecord.game.player.level.leveltasks.PokemonLevelTask;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class PokemonMasteryLevel
{
    private int level;
    private EnumSet<Feature> features;
    private List<AbstractLevelTask> requirements;

    private PokemonMasteryLevel(int level)
    {
        this.level = level;
        this.features = EnumSet.noneOf(Feature.class);
        this.requirements = new ArrayList<>();
    }

    public static PokemonMasteryLevel create(int level)
    {
        return new PokemonMasteryLevel(level);
    }

    public void register()
    {
        MasteryLevelManager.MASTERY_LEVELS.add(this);
    }

    public boolean canLevelUp(PlayerDataQuery p)
    {
        return this.requirements.stream().allMatch(task -> task.isCompleted(p));
    }

    public PokemonMasteryLevel withFeaturesUnlocked(Feature... features)
    {
        Collections.addAll(this.features, features);
        return this;
    }

    public PokemonMasteryLevel withExperienceRequirement(int exp)
    {
        this.requirements.add(new ExperienceLevelTask(exp));
        return this;
    }

    public PokemonMasteryLevel withPokemonRequirement(int amount)
    {
        this.requirements.add(new PokemonLevelTask(amount));
        return this;
    }

    public PokemonMasteryLevel withTaskRequirement(AbstractLevelTask task)
    {
        this.requirements.add(task);
        return this;
    }

    public List<AbstractLevelTask> getTasks()
    {
        return this.requirements;
    }

    public EnumSet<Feature> getFeatures()
    {
        return this.features;
    }

    public String getUnlockedFeaturesOverview()
    {
        return this.features.stream().map(Feature::getOverview).map(overview -> "- " + overview).collect(Collectors.joining("\n"));
    }

    public int getLevel()
    {
        return this.level;
    }
}
