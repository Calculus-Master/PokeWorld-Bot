package com.calculusmaster.pokecord.game.duel.players;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.helpers.DataHelper;

import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;

public class WildPokemon extends Player
{
    public WildPokemon(int level)
    {
        this(PokemonData.POKEMON.get(new Random().nextInt(PokemonData.POKEMON.size())), level);
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

    public WildPokemon(Type type, int level)
    {
        List<String> pool = DataHelper.TYPE_LISTS.get(type);
        Pokemon p = Pokemon.create(pool.get(new SplittableRandom().nextInt(pool.size())));

        p.setLevel(level);
        p.statBuff = 1.5;
        p.setEVs("50-50-50-50-50-50");
        p.setHealth(p.getStat(Stat.HP));

        for(int i = 0; i < 4; i++) p.learnMove(p.getAvailableMoves().get(new Random().nextInt(p.getAvailableMoves().size())), i + 1);

        this.team = List.of(p);
        this.active = this.team.get(0);
        this.move = null;

        this.data = new PlayerDataQuery("BOT")
        {
            @Override
            public String getUsername()
            {
                return "Z Trial Leader " + p.getName();
            }
        };
    }
}
