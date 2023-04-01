package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class ItemEvoTrigger implements EvolutionTrigger
{
    protected final Item item;

    public ItemEvoTrigger(Item item)
    {
        this.item = item;
    }

    @Override
    public boolean canEvolve(Pokemon p, String serverID)
    {
        return p.hasItem() && p.getItem().equals(this.item);
    }

    @Override
    public String getDescription()
    {
        return "Holding a " + this.item.getStyledName();
    }
}
