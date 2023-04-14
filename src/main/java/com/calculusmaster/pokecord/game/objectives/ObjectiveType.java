package com.calculusmaster.pokecord.game.objectives;

import com.calculusmaster.pokecord.game.objectives.types.*;
import org.bson.Document;

import java.util.Random;
import java.util.function.Supplier;

public enum ObjectiveType
{
    CATCH_POKEMON(GenericObjective::new, "Catch Pokemon.", 30, 100),
    CATCH_POKEMON_TYPE(TypeObjective::new, "Catch Pokemon of a specific type.", 20, 40),
    CATCH_POKEMON_NAME(PokemonSpecificObjective::new, "Catch a specific Pokemon.", 1, 10),
    CATCH_POKEMON_POOL(PokemonListObjective::new, "Catch Pokemon from the given list.", 1, 15),

    COMPLETE_TRADE(GenericObjective::new, "Complete trades.", 1, 4),
    COMPLETE_PVP_DUEL(GenericObjective::new, "Complete player versus player duels.", 1, 10),
    COMPLETE_WILD_DUEL(GenericObjective::new, "Complete wild Pokemon duels.", 10, 80),
    COMPLETE_TRAINER_DUEL(GenericObjective::new, "Complete trainer duels.", 2, 15),

    EARN_XP_POKEMON(GenericObjective::new, "Earn Pokemon XP.", 2000, 16000),
    LEVEL_POKEMON(GenericObjective::new, "Level Pokemon.", 1, 20),
    EVOLVE_POKEMON(GenericObjective::new, "Evolve Pokemon.", 1, 5),
    BREED_POKEMON(GenericObjective::new, "Breed Pokemon.", 1, 10),
    SWAP_POKEMON(GenericObjective::new, "Swap out Pokemon in duels.", 5, 30),

    DEFEAT_POKEMON(GenericObjective::new, "Defeat Pokemon.", 5, 50),
    DEFEAT_POKEMON_TYPE(TypeObjective::new, "Defeat Pokemon of a specific type.", 5, 25),
    DEFEAT_POKEMON_POOL(PokemonListObjective::new, "Defeat Pokemon from the given list.", 5, 15),
    DEFEAT_LEGENDARY(GenericObjective::new, "Defeat Legendary Pokemon.", 1, 3),

    DAMAGE_POKEMON(GenericObjective::new, "Deal damage to opposing Pokemon.", 100, 3000),
    DAMAGE_POKEMON_TYPE(TypeObjective::new, "Deal damage to opposing Pokemon of a specific type.", 100, 1500),

    USE_MOVES(GenericObjective::new, "Use moves.", 20, 50),
    USE_MOVES_CATEGORY(CategoryObjective::new, "Use moves of a specific category.", 10, 30),
    USE_MOVES_TYPE(TypeObjective::new, "Use moves of a specific type.", 10, 30),
    USE_MOVES_NAME(MoveSpecificObjective::new, "Use a specific move.", 2, 6),
    USE_MOVES_POOL(MoveListObjective::new, "Use moves from the given list.", 2, 10),
    USE_MOVES_POWER_LESS(PowerObjective::new, "Use moves with power less than the given power.", 5, 20),
    USE_MOVES_POWER_GREATER(PowerObjective::new, "Use moves with power greater than the given power.", 5, 20),
    USE_MOVES_PRIORITY_HIGH(GenericObjective::new, "Use moves with high priority.", 1, 5),
    USE_MOVES_PRIORITY_LOW(GenericObjective::new, "Use moves with low priority.", 1, 5),
    USE_ZMOVE(GenericObjective::new, "Use Z-Moves.", 2, 9),
    USE_ZMOVE_TYPE(TypeObjective::new, "Use Z-Moves of a specific type.", 2, 10),
    USE_MAX_MOVE(GenericObjective::new, "Use Max Moves.", 10, 30),
    USE_MAX_MOVE_TYPE(TypeObjective::new, "Use Max Moves of a specific type.", 10, 20),

    ;

    public final Supplier<? extends AbstractObjective> constructor;
    private final String description;
    private final int min;
    private final int max;

    ObjectiveType(Supplier<? extends AbstractObjective> constructor, String description, int min, int max)
    {
        this.constructor = constructor;
        this.description = description;
        this.min = min;
        this.max = max;
    }

    public AbstractObjective build(Document data)
    {
        AbstractObjective objective = this.constructor.get();
        objective.read(data);
        return objective;
    }

    public AbstractObjective create()
    {
        AbstractObjective objective = this.constructor.get();
        objective.setObjectiveType(this);
        objective.setTarget(this.getRandomTarget());
        objective.generate();
        return objective;
    }

    public int getRandomTarget()
    {
        return new Random().nextInt(this.max - this.min + 1) + this.min;
    }

    public String getDescription()
    {
        return this.description;
    }
}
