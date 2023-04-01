package com.calculusmaster.pokecord.game.moves.registry;

import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.util.Global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class MoveTutorRegistry
{
    public static final List<MoveEntity> MOVE_TUTOR_MOVES = new ArrayList<>();
    public static final Map<MoveEntity, Predicate<Pokemon>> VALIDATORS = new HashMap<>();

    public static void init()
    {
        //Fire-Type Starters
        MoveTutorRegistry.register(MoveEntity.BLAST_BURN, PokemonEntity.CHARIZARD, PokemonEntity.TYPHLOSION, PokemonEntity.BLAZIKEN, PokemonEntity.INFERNAPE, PokemonEntity.EMBOAR, PokemonEntity.DELPHOX, PokemonEntity.INCINEROAR, PokemonEntity.CINDERACE, PokemonEntity.SKELEDIRGE);
        MoveTutorRegistry.register(MoveEntity.FIRE_PLEDGE, p -> Global.isStarter(p.getEntity()) && p.isType(Type.FIRE));
        //Water-Type Starters
        MoveTutorRegistry.register(MoveEntity.HYDRO_CANNON, PokemonEntity.BLASTOISE, PokemonEntity.FERALIGATR, PokemonEntity.SWAMPERT, PokemonEntity.EMPOLEON, PokemonEntity.SAMUROTT, PokemonEntity.GRENINJA, PokemonEntity.PRIMARINA, PokemonEntity.INTELEON, PokemonEntity.QUAQUAVAL);
        MoveTutorRegistry.register(MoveEntity.WATER_PLEDGE, p -> Global.isStarter(p.getEntity()) && p.isType(Type.WATER));
        //Grass-Type Starters
        MoveTutorRegistry.register(MoveEntity.FRENZY_PLANT, PokemonEntity.VENUSAUR, PokemonEntity.MEGANIUM, PokemonEntity.SCEPTILE, PokemonEntity.TORTERRA, PokemonEntity.SERPERIOR, PokemonEntity.CHESNAUGHT, PokemonEntity.DECIDUEYE, PokemonEntity.RILLABOOM, PokemonEntity.MEOWSCARADA);
        MoveTutorRegistry.register(MoveEntity.GRASS_PLEDGE, p -> Global.isStarter(p.getEntity()) && p.isType(Type.GRASS));

        //Type Specific Moves
        MoveTutorRegistry.register(MoveEntity.DRACO_METEOR, p -> p.getType().get(0).equals(Type.DRAGON));
        MoveTutorRegistry.register(MoveEntity.STEEL_BEAM, p -> p.getType().get(0).equals(Type.STEEL) && !p.is(PokemonEntity.NECROZMA_DUSK_MANE));

        //S&S Type Moves
        MoveTutorRegistry.register(MoveEntity.TERRAIN_PULSE);
        MoveTutorRegistry.register(MoveEntity.BURNING_JEALOUSY);
        MoveTutorRegistry.register(MoveEntity.FLIP_TURN);
        MoveTutorRegistry.register(MoveEntity.GRASSY_GLIDE);
        MoveTutorRegistry.register(MoveEntity.RISING_VOLTAGE);
        MoveTutorRegistry.register(MoveEntity.COACHING);
        MoveTutorRegistry.register(MoveEntity.SCORCHING_SANDS);
        MoveTutorRegistry.register(MoveEntity.DUAL_WINGBEAT);
        MoveTutorRegistry.register(MoveEntity.METEOR_BEAM);
        MoveTutorRegistry.register(MoveEntity.SKITTER_SMACK);
        MoveTutorRegistry.register(MoveEntity.TRIPLE_AXEL);
        MoveTutorRegistry.register(MoveEntity.CORROSIVE_GAS);
        MoveTutorRegistry.register(MoveEntity.EXPANDING_FORCE);
        MoveTutorRegistry.register(MoveEntity.POLTERGEIST);
        MoveTutorRegistry.register(MoveEntity.SCALE_SHOT);
        MoveTutorRegistry.register(MoveEntity.LASH_OUT);
        MoveTutorRegistry.register(MoveEntity.STEEL_ROLLER);
        MoveTutorRegistry.register(MoveEntity.MISTY_EXPLOSION);

        //Signature Moves (for Specific Pokemon)
        MoveTutorRegistry.register(MoveEntity.VOLT_TACKLE, p -> p.is(PokemonEntity.PIKACHU));
        MoveTutorRegistry.register(MoveEntity.DRAGON_ASCENT, p -> p.is(PokemonEntity.RAYQUAZA, PokemonEntity.RAYQUAZA_MEGA));
        MoveTutorRegistry.register(MoveEntity.SECRET_SWORD, p -> p.is(PokemonEntity.KELDEO, PokemonEntity.KELDEO_RESOLUTE));
        MoveTutorRegistry.register(MoveEntity.RELIC_SONG, p -> p.is(PokemonEntity.MELOETTA_ARIA, PokemonEntity.MELOETTA_PIROUETTE));
    }

    private static void register(MoveEntity name)
    {
        register(name, p -> p.getData().getTutorMoves().contains(name));
    }

    private static void register(MoveEntity name, PokemonEntity... pokemon)
    {
        register(name, p -> List.of(pokemon).contains(p.getEntity()));
    }

    private static void register(MoveEntity name, Predicate<Pokemon> rule)
    {
        MOVE_TUTOR_MOVES.add(name);
        VALIDATORS.put(name, rule);
    }
}
