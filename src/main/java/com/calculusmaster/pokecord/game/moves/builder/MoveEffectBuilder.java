package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.GMaxDoTType;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    //Move Defaults
    public static String defaultDamage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public static String statusDamage(Pokemon user, Pokemon opponent, Duel duel, Move move, StatusCondition status, int percent)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(status, percent)
                .execute();
    }

    public static String statChangeDamage(Pokemon user, Pokemon opponent, Duel duel, Move move, Stat s, int stage, int percent, boolean userChange)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(s, stage, percent, userChange)
                .execute();
    }

    public static String multiDamage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addVariableMultiStrikeEffect()
                .execute();
    }

    public static String multiDamage(Pokemon user, Pokemon opponent, Duel duel, Move move, int times)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedMultiStrikeEffect(times)
                .execute();
    }

    //Move Logic
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

    public MoveEffectBuilder addStatusEffect(StatusCondition status)
    {
        return this.addStatusEffect(status, 100, false);
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

    //Accuracy stage changed
    public MoveEffectBuilder addAccuracyChangeEffect(int stage, int percent, boolean userChange)
    {
        this.moveEffects.add(new AccuracyChangeEffect(stage, percent, userChange));
        return this;
    }

    //Evasion stage changed
    public MoveEffectBuilder addEvasionChangeEffect(int stage, int percent, boolean userChange)
    {
        this.moveEffects.add(new EvasionChangeEffect(stage, percent, userChange));
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

    public MoveEffectBuilder addFixedHealEffect(int HP)
    {
        this.moveEffects.add(new FixedHealEffect(HP));
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

    public MoveEffectBuilder addTerrainEffect(Terrain terrain)
    {
        this.moveEffects.add(new TerrainEffect(terrain));
        return this;
    }

    public MoveEffectBuilder addRoomEffect(Room room)
    {
        this.moveEffects.add(new RoomEffect(room));
        return this;
    }

    public MoveEffectBuilder addOHKOEffect()
    {
        this.moveEffects.add(new OHKODamageEffect());
        return this;
    }

    public MoveEffectBuilder addEntryHazardEffect(EntryHazard hazard)
    {
        this.moveEffects.add(new EntryHazardEffect(hazard));
        return this;
    }

    public MoveEffectBuilder addConsecutiveDamageEffect(int... powers)
    {
        this.moveEffects.add(new ConsecutiveDamageEffect(powers));
        return this;
    }

    public MoveEffectBuilder addCustomEffect(Supplier<String> effect)
    {
        this.moveEffects.add(new CustomEffect(effect));
        return this;
    }

    public MoveEffectBuilder addCustomRunnableEffect(Runnable effect)
    {
        return this.addCustomEffect(() -> {
            effect.run();
            return "";
        });
    }

    public MoveEffectBuilder addConditionalCustomEffect(boolean applyEffect, Supplier<String> ifPass, Supplier<String> ifFailed)
    {
        return applyEffect ? this.addCustomEffect(ifPass) : this.addCustomEffect(ifFailed);
    }

    public MoveEffectBuilder addConditionalEffect(boolean condition, Consumer<MoveEffectBuilder> ifTrue)
    {
        if(condition) ifTrue.accept(this);
        return this;
    }

    public MoveEffectBuilder addConditionalEffect(boolean condition, Consumer<MoveEffectBuilder> ifTrue, Consumer<MoveEffectBuilder> ifFalse)
    {
        if(condition) ifTrue.accept(this);
        else ifFalse.accept(this);
        return this;
    }

    public MoveEffectBuilder addSelfFaintEffect()
    {
        this.moveEffects.add(new SelfFaintEffect());
        return this;
    }

    public MoveEffectBuilder addFixedSelfDamageEffect(int damage)
    {
        this.moveEffects.add(new FixedSelfDamageEffect(damage));
        return this;
    }

    public MoveEffectBuilder addFractionSelfDamageEffect(double fraction)
    {
        this.moveEffects.add(new FractionSelfDamageEffect(fraction));
        return this;
    }

    public MoveEffectBuilder addGMaxDoTEffect(GMaxDoTType type)
    {
        this.moveEffects.add(new GMaxDoTEffect(type));
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
            if(!(e instanceof DamageDependentEffect)) results.append(e.get());

            if(e instanceof FixedDamageEffect fde)
            {
                damageDealt = fde.getDamage();
            }

            if(e instanceof DamageDependentEffect dde)
            {
                dde.setDamageDealt(damageDealt);
                results.append(e.get());
            }

            results.append(" ");
        }

        if(this.moveEffects.stream().noneMatch(me -> me instanceof FixedDamageEffect)) this.duel.data(this.opponent.getUUID()).lastDamageTaken = 0;

        return results.toString().trim();
    }
}
