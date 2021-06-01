package com.calculusmaster.pokecord.game.duel.elements;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;

import java.util.List;
import java.util.Random;

public class WildPokemon extends Player
{
    public WildPokemon(int level)
    {
        this(Global.POKEMON.get(new Random().nextInt(Global.POKEMON.size())), level);
    }

    public WildPokemon(String specific, int level)
    {
        super();

        Pokemon p = Pokemon.create(specific);
        p.setLevel(level);
        p.setHealth(p.getStat(Stat.HP));
        for(int i = 0; i < 4; i++)
        {
            p.learnMove(p.getAvailableMoves().get(new Random().nextInt(p.getAvailableMoves().size())), i + 1);
        }

        if(new Random().nextInt(100) < 1) p.setLearnedMoves("Eternabeam-Prismatic Laser-Soul Stealing 7 Star Strike-Close Combat");

        this.team = List.of(p);
        this.active = this.team.get(0);
        this.move = null;

        this.data = new PlayerDataQuery("BOT")
        {
            @Override
            public String getUsername()
            {
                return "The Wild " + p.getName();
            }
        };
    }
}
