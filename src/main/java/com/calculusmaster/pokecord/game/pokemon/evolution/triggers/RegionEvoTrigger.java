package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.enums.elements.Region;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.world.RegionManager;
import com.calculusmaster.pokecord.util.Global;

public class RegionEvoTrigger implements EvolutionTrigger
{
    private final Region region;
    private final boolean invert;

    public RegionEvoTrigger(Region region, boolean invert)
    {
        this.region = region;
        this.invert = invert;
    }

    @Override
    public boolean canEvolve(Pokemon p, String serverID)
    {
        return !serverID.isEmpty() && (this.invert != RegionManager.getCurrentRegion().equals(this.region));
    }

    @Override
    public String getDescription()
    {
        return (this.invert ? "Not in " : "In ") + Global.normalize(this.region.toString());
    }
}
