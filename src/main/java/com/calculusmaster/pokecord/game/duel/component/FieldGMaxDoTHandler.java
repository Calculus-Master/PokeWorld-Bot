package com.calculusmaster.pokecord.game.duel.component;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class FieldGMaxDoTHandler
{
    private GMaxDoTType type;
    private int turns;

    public FieldGMaxDoTHandler()
    {
        this.type = GMaxDoTType.NONE;
        this.turns = 0;
    }

    public void updateTurns()
    {
        if(this.turns > 0) this.turns--;

        if(this.turns == 0) this.remove();
    }

    public void addDoT(GMaxDoTType type)
    {
        this.type = type;
        this.turns = 4;
    }

    public void remove()
    {
        this.type = GMaxDoTType.NONE;
        this.turns = 0;
    }

    public boolean applies(Pokemon p)
    {
        if(!this.type.equals(GMaxDoTType.NONE)) return !p.isType(this.type.immuneType);
        else return false;
    }

    public boolean exists()
    {
        return !this.type.equals(GMaxDoTType.NONE) && this.turns == 0;
    }

    public String getEffectName()
    {
        return switch(this.type) {
            case WILDFIRE -> "Wildfire";
            case VINE_LASH -> "Vine Lash";
            case CANNONADE -> "Cannonade";
            case VOLCALITH -> "Volcalith";
            case NONE -> "NONE";
        };
    }
}
