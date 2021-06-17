package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Weather;

import java.util.ArrayList;
import java.util.Comparator;
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

    public MoveEffectBuilder addStatusEffect(StatusCondition status, int percent, boolean userChange)
    {
        this.moveEffects.add(new StatusConditionEffect(status, percent, userChange));
        return this;
    }

    public MoveEffectBuilder addStatusEffect(StatusCondition status, int percent)
    {
        return this.addStatusEffect(status, percent, false);
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

    public MoveEffectBuilder addRecoilEffect(double fraction)
    {
        this.moveEffects.add(new RecoilEffect(fraction));
        return this;
    }

    public MoveEffectBuilder addFractionHealEffect(double fraction)
    {
        this.moveEffects.add(new FractionHealEffect(fraction));
        return this;
    }

    public MoveEffectBuilder addDamageHealEffect(double fraction)
    {
        this.moveEffects.add(new DamageHealEffect(fraction));
        return this;
    }

    public MoveEffectBuilder addCritDamageEffect(int crit)
    {
        this.moveEffects.add(new IncreasedCritDamageEffect(crit));
        return this;
    }

    //A lot of moves default to increased crit chance of 1/8 = 3/24
    public MoveEffectBuilder addCritDamageEffect()
    {
        return this.addCritDamageEffect(3);
    }

    public MoveEffectBuilder addWeatherEffect(Weather weather)
    {
        this.moveEffects.add(new WeatherEffect(weather));
        return this;
    }

    public MoveEffectBuilder addTerrainEffect(DuelHelper.Terrain terrain)
    {
        this.moveEffects.add(new TerrainEffect(terrain));
        return this;
    }

    public MoveEffectBuilder addRoomEffect(DuelHelper.Room room)
    {
        this.moveEffects.add(new RoomEffect(room));
        return this;
    }

    public String execute()
    {
        //Initialization
        for(MoveEffect e : this.moveEffects) e.init(this.user, this.opponent, this.duel, this.move);

        //Execution
        StringBuilder results = new StringBuilder();

        this.moveEffects.sort(Comparator.comparingInt(MoveEffect::getPriority));

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
