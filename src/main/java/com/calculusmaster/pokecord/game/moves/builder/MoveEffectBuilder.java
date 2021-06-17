package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

import java.util.ArrayList;
import java.util.List;

public class MoveEffectBuilder
{
    private Pokemon user, opponent;
    private Duel duel;
    private Move move;

    private List<MoveEffect> moveEffects;

    private MoveEffectBuilder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        this.user = user;
        this.opponent = opponent;
        this.duel = duel;
        this.move = move;

        this.moveEffects = new ArrayList<>();
    }

    public static MoveEffectBuilder make(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return new MoveEffectBuilder(user, opponent, duel, move);
    }

    public MoveEffectBuilder addDamageEffect()
    {
        this.moveEffects.add(new DamageEffect());
        return this;
    }

    public MoveEffectBuilder addFixedDamageEffect(int damage)
    {
        this.moveEffects.add(new FixedDamageEffect(damage));
        return this;
    }

    public MoveEffectBuilder addStatusEffect(StatusCondition status, int percent)
    {
        this.moveEffects.add(new StatusConditionEffect(status, percent));
        return this;
    }

    //For single stat changed
    public MoveEffectBuilder addStatChangeEffect(Stat s, int stage, int percent, boolean userChange)
    {
        this.moveEffects.add(new StatChangeEffect(s, stage, percent, userChange));
        return this;
    }

    //For multiple stats changed
    public MoveEffectBuilder addStatChangeEffect(StatChangeEffect e)
    {
        this.moveEffects.add(e);
        return this;
    }

    public MoveEffectBuilder addVariableMultiStrikeEffect()
    {
        this.moveEffects.add(new VariableMultiStrikeDamageEffect());
        return this;
    }

    public MoveEffectBuilder addFixedMultiStrikeEffect(int times)
    {
        this.moveEffects.add(new FixedMultiStrikeDamageEffect(times));
        return this;
    }

    public String execute()
    {
        //Initialization
        for(MoveEffect e : this.moveEffects) e.init(this.user, this.opponent, this.duel, this.move);

        //Execution
        StringBuilder results = new StringBuilder();

        //TODO: Sort MoveEffects in order starting from Damage to StatChanges

        int damageDealt = 0;
        for(MoveEffect e : this.moveEffects)
        {
            if(!(e instanceof RecoilEffect) && !(e instanceof DamageHealEffect)) results.append(e.get());

            if(e instanceof FixedDamageEffect)
            {
                damageDealt = ((FixedDamageEffect)e).getDamage();
            }

            if(e instanceof RecoilEffect)
            {
                ((RecoilEffect)e).set(damageDealt);
                results.append(e.get());
            }

            if(e instanceof DamageHealEffect)
            {
                ((DamageHealEffect)e).set(damageDealt);
                results.append(e.get());
            }

            results.append(" ");
        }

        return results.toString().trim();
    }
}
