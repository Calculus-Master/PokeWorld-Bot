package com.calculusmaster.pokecord.game.duel.component;

import com.calculusmaster.pokecord.game.enums.elements.Terrain;

public class TerrainHandler
{
    private Terrain terrain;
    private int turns;

    public TerrainHandler()
    {
        this.terrain = Terrain.NORMAL_TERRAIN;
        this.turns = -1;
    }

    public Terrain get()
    {
        return this.terrain;
    }

    public void setTerrain(Terrain terrain)
    {
        this.terrain = terrain;
        this.turns = 5;
    }

    public void removeTerrain()
    {
        this.terrain = Terrain.NORMAL_TERRAIN;
        this.turns = -1;
    }

    public void updateTurns()
    {
        if(this.turns > 0)
        {
            this.turns--;

            if(this.turns <= 0) this.removeTerrain();
        }
    }

    public boolean is(Terrain terrain)
    {
        return this.terrain == terrain;
    }
}
