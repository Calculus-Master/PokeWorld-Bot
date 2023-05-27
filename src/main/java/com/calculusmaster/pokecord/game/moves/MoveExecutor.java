package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class MoveExecutor
{
    //TODO: Transfer moves to new system
    private final Pokemon user, opponent;
    private final Duel duel;
    private final Move move;

    private final MoveEffectBuilder effects;

    public MoveExecutor(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        this.user = user;
        this.opponent = opponent;
        this.duel = duel;
        this.move = move;

        this.effects = MoveEffectBuilder.make(this.user, this.opponent, this.duel, this.move);
    }

    //Helper
    private String defaultDamage()
    {
        return MoveEffectBuilder.defaultDamage(this.user, this.opponent, this.duel, this.move);
    }

    //Z-Moves
    public String SavageSpinOut()
    {
        return this.defaultDamage();
    }

    public String BlackHoleEclipse()
    {
        return this.defaultDamage();
    }

    public String DevastatingDrake()
    {
        return this.defaultDamage();
    }

    public String GigavoltHavoc()
    {
        return this.defaultDamage();
    }

    public String TwinkleTackle()
    {
        return this.defaultDamage();
    }

    public String AllOutPummeling()
    {
        return this.defaultDamage();
    }

    public String InfernoOverdrive()
    {
        return this.defaultDamage();
    }

    public String SupersonicSkystrike()
    {
        return this.defaultDamage();
    }

    public String NeverEndingNightmare()
    {
        return this.defaultDamage();
    }

    public String BloomDoom()
    {
        return this.defaultDamage();
    }

    public String TectonicRage()
    {
        return this.defaultDamage();
    }

    public String SubzeroSlammer()
    {
        return this.defaultDamage();
    }

    public String BreakneckBlitz()
    {
        return this.defaultDamage();
    }

    public String AcidDownpour()
    {
        return this.defaultDamage();
    }

    public String ShatteredPsyche()
    {
        return this.defaultDamage();
    }

    public String ContinentalCrush()
    {
        return this.defaultDamage();
    }

    public String CorkscrewCrash()
    {
        return this.defaultDamage();
    }

    public String HydroVortex()
    {
        return this.defaultDamage();
    }



    //Max Moves

    //Standard Moves
}
