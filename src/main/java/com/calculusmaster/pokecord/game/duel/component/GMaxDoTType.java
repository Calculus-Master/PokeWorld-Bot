package com.calculusmaster.pokecord.game.duel.component;

import com.calculusmaster.pokecord.game.enums.elements.Type;

public enum GMaxDoTType
{
    NONE(null),
    WILDFIRE(Type.FIRE),
    VINE_LASH(Type.GRASS),
    CANNONADE(Type.WATER),
    VOLCALITH(Type.ROCK);

    public Type immuneType;

    GMaxDoTType(Type immuneType)
    {
        this.immuneType = immuneType;
    }
}
