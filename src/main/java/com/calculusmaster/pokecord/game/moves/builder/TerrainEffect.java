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
        this.duel.terrain = this.terrain;
        this.duel.terrainTurns = 5;

        return this.user.getName() + " created " + Global.normalize(this.terrain.toString().replaceAll("_", " "));
    }
}
