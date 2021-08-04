package com.calculusmaster.pokecord.game.pokemon;

import java.util.Arrays;
import java.util.List;

public enum PokemonSorterFlag
{
    NAME("--name"),
    NICKNAME("--nickname", "--nick"),
    MOVE("--move"),
    LEARNED_MOVE("--learnedmove", "--lmove"),
    AVAILABLE_MOVE("--availablemove", "--amove"),
    LEVEL("--level", "--lvl"),
    DYNAMAX_LEVEL("--dynamaxlevel", "--dlevel", "--dlvl"),
    IV("--iv"),
    EV("--ev"),
    TM("--tm"),
    TR("--tr"),
    TEAM("--team"),
    FAVORITES("--favorites", "--fav"),
    TYPE("--type"),
    MAIN_TYPE("--maintype"),
    SIDE_TYPE("--sidetype"),
    GENDER("--gender"),
    EGG_GROUP("--egggroup", "--egg"),
    SHINY("--shiny"),
    HPIV("--healthiv", "--hpiv"),
    ATKIV("--attackiv", "--atkiv"),
    DEFIV("--defenseiv", "--defiv"),
    SPATKIV("--specialattackiv", "--spatkiv"),
    SPDEFIV("--specialdefenseiv", "--spdefiv"),
    SPDIV("--speediv", "--spdiv"),
    HPEV("--healthev", "--hpev"),
    ATKEV("--attackev", "--atkev"),
    DEFEV("--defenseev", "--defev"),
    SPATKEV("--specialattackev", "--spatkev"),
    SPDEFEV("--specialdefenseev", "--spdefev"),
    SPDEV("--speedev", "--spdev"),
    LEGENDARY("--legendary", "--leg"),
    MYTHICAL("--mythical", "--myth"),
    ULTRA_BEAST("--ub", "--ultrabeast", "--ultra", "--beast"),
    MEGA("--mega"),
    PRIMAL("--primal"),
    MEGA_OR_PRIMAL("--mega|primal", "--primal|mega");

    public List<String> flags;
    PokemonSorterFlag(String... flags)
    {
        this.flags = Arrays.asList(flags);
    }
}
