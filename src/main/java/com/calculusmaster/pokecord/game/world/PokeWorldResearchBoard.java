package com.calculusmaster.pokecord.game.world;

import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.objectives.ResearchTask;
import com.calculusmaster.pokecord.game.objectives.types.AbstractObjective;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.IntStream;

public class PokeWorldResearchBoard
{
    public static final int TASK_COUNT = 6;
    public static final int FEATURED_POKEMON_COUNT = 3;

    private static PokeWorldResearchBoard CURRENT;

    //TODO: Research Board TODOs
    // Add missions
    // Add more mechanics for featured Pokemon
    // Some way to remove bounties from player inventories (+ some associated cost or setback)
    // Add better generation for tasks so duplicates are less likely

    public static void init()
    {
        Document data = Mongo.MiscData.find(Filters.eq("type", "research_board")).first();

        if(data == null)
        {
            Mongo.MiscData.insertOne(new Document("type", "research_board"));

            CURRENT = new PokeWorldResearchBoard();

            PokeWorldResearchBoard.refresh();
        }
        else CURRENT = new PokeWorldResearchBoard(data);
    }

    public static PokeWorldResearchBoard getCurrentResearchBoard()
    {
        return CURRENT;
    }

    public static void refresh()
    {
        CURRENT.generate();
        CURRENT.update();
    }

    //Class

    private final List<List<ObjectiveType>> taskHeaders;
    private final EnumSet<PokemonEntity> featuredPokemon;

    public PokeWorldResearchBoard()
    {
        this.taskHeaders = new ArrayList<>();
        this.featuredPokemon = EnumSet.noneOf(PokemonEntity.class);
    }

    public PokeWorldResearchBoard(Document data)
    {
        this();

        Document tasks = data.get("tasks", Document.class);
        tasks.keySet().forEach(s -> this.taskHeaders.add(tasks.getList(s, String.class).stream().map(ObjectiveType::valueOf).toList()));

        this.featuredPokemon.addAll(data.getList("featured_pokemon", String.class).stream().map(PokemonEntity::valueOf).toList());
    }

    private void update()
    {
        Document data = new Document("type", "research_board");

        Document tasks = new Document();
        IntStream.range(0, this.taskHeaders.size())
                .forEach(i -> tasks.append(
                        String.valueOf(i),
                        this.taskHeaders.get(i).stream().map(Enum::toString).toList()
                ));
        data.append("tasks", tasks);

        data.append("featured_pokemon", this.featuredPokemon.stream().toList());

        Mongo.MiscData.replaceOne(Filters.eq("type", "research_board"), data);
    }

    public void generate()
    {
        this.taskHeaders.clear();
        IntStream.range(0, TASK_COUNT).forEach(i -> this.taskHeaders.add(ResearchTask.create().getObjectives().stream().map(AbstractObjective::getObjectiveType).toList()));

        this.featuredPokemon.clear();
        IntStream.range(0, FEATURED_POKEMON_COUNT).forEach(i -> this.featuredPokemon.add(PokemonEntity.getRandom()));

        LoggerHelper.info(PokeWorldResearchBoard.class, "Generated new Research Board!");
    }

    public boolean isFeatured(PokemonEntity p)
    {
        return this.featuredPokemon.contains(p);
    }

    public List<List<ObjectiveType>> getTaskHeaders()
    {
        return this.taskHeaders;
    }

    public EnumSet<PokemonEntity> getFeaturedPokemon()
    {
        return this.featuredPokemon;
    }
}
