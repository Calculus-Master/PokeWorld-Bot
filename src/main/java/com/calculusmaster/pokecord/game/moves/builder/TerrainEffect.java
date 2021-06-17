package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.util.Global;

public class TerrainEffect extends MoveEffect
{
    private DuelHelper.Terrain terrain;

    public TerrainEffect(DuelHelper.Terrain terrain)
    {
        this.terrain = terrain;
    }

    @Override
    public String get()
    {
        this.duel.terrain = this.terrain;
        this.duel.terrainTurns = 5;

        return this.user.getName() + " created " + Global.normalCase(this.terrain.toString().replaceAll("_", " "));
    }
}
