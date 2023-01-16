package com.calculusmaster.pokecord.game.duel.players;

import com.calculusmaster.pokecord.game.duel.trainer.TrainerData;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.Random;
import java.util.stream.IntStream;

public class TrainerPlayer extends AIPlayer
{
    private final TrainerData trainerData;

    public TrainerPlayer(TrainerData data)
    {
        this.ID = data.getTrainerID();
        this.trainerData = data;

        Random r = new Random();
        this.setTeam(data.getTeam().stream().map(s -> {
            Pokemon p = Pokemon.create(s);
            p.setLevel(Math.min(100, r.nextInt(data.getAveragePokemonLevel() - 5, data.getAveragePokemonLevel() + 6)));
            p.getBoosts().setStatBoost(this.trainerData.getMultiplier());
            IntStream.range(0, 4).forEach(i -> p.learnMove(p.availableMoves().get(r.nextInt(p.availableMoves().size())), i));
            return p;
        }).toList());
    }

    public boolean hasZCrystal(ZCrystal zCrystal)
    {
        return this.trainerData.getZCrystal() != null && zCrystal.equals(this.trainerData.getZCrystal());
    }

    @Override
    public String getName()
    {
        return this.trainerData.getName();
    }

    public TrainerData getData()
    {
        return this.trainerData;
    }
}
