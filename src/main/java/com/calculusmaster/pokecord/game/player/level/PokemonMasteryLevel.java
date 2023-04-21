package com.calculusmaster.pokecord.game.player.level;

import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.level.pmltasks.AbstractPMLTask;
import com.calculusmaster.pokecord.game.player.level.pmltasks.ExperiencePMLTask;
import com.calculusmaster.pokecord.mongo.PlayerData;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PokemonMasteryLevel
{
    private int level;
    private Supplier<EmbedBuilder> embed;
    private EnumSet<Feature> features;
    private List<AbstractPMLTask> requirements;

    private PokemonMasteryLevel(int level)
    {
        this.level = level;
        this.embed = EmbedBuilder::new;
        this.features = EnumSet.noneOf(Feature.class);
        this.requirements = new ArrayList<>();
    }

    public static PokemonMasteryLevel create(int level)
    {
        return new PokemonMasteryLevel(level);
    }

    public EmbedBuilder getEmbed()
    {
        return this.embed.get().setTimestamp(Instant.now());
    }

    public void register()
    {
        MasteryLevelManager.MASTERY_LEVELS.add(this);
    }

    public boolean canLevelUp(PlayerData p)
    {
        return this.requirements.stream().allMatch(task -> task.isCompleted(p));
    }

    public PokemonMasteryLevel withEmbed(Supplier<EmbedBuilder> embedSupplier)
    {
        this.embed = embedSupplier;
        return this;
    }

    public PokemonMasteryLevel withFeaturesUnlocked(Feature... features)
    {
        Collections.addAll(this.features, features);
        return this;
    }

    public PokemonMasteryLevel withExperienceRequirement(int pokemonLevels)
    {
        this.requirements.add(new ExperiencePMLTask(pokemonLevels * PMLExperience.LEVEL_POKEMON.experience));
        return this;
    }

    public PokemonMasteryLevel withTaskRequirement(AbstractPMLTask task)
    {
        this.requirements.add(task);
        return this;
    }

    public List<AbstractPMLTask> getTasks()
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
