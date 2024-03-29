package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Terrain;
import com.calculusmaster.pokecord.util.Global;

public class TerrainEffect extends MoveEffect
{
    private Terrain terrain;

    public TerrainEffect(Terrain terrain)
    {
        this.terrain = terrain;
    }

    @Override
    public String get()
    {
        this.duel.terrain.setTerrain(this.terrain);

        return this.user.getName() + " created a " + Global.normalize(this.terrain.toString().replaceAll("_", " ") + "!");
    }
}
