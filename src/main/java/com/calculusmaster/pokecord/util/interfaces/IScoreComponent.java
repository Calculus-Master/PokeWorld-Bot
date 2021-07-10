package com.calculusmaster.pokecord.util.interfaces;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public interface IScoreComponent
{
    int get(PlayerDataQuery p);
}
