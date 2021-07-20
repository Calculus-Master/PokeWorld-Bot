package com.calculusmaster.pokecord.game.pokepass;

import com.calculusmaster.pokecord.game.pokemon.PokemonSkin;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class SkinReward extends TierReward
{
    private PokemonSkin skin;

    public SkinReward(PokemonSkin skin)
    {
        this.skin = skin;
    }

    @Override
    public String grantReward(PlayerDataQuery player)
    {
        player.addSkin(this.skin);

        return "You earned a new skin for " + this.skin.pokemon + " - \"" + this.skin.skinName + "\"";
    }

    @Override
    public String getTierDescription()
    {
        return "Skin Reward: **" + this.skin.skinName + "**";
    }
}
