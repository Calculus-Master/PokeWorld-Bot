package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StatChangeEffect extends MoveEffect
{
    private Map<Stat, Integer> statChanges;
    private int percent;
    private boolean userChange;

    public StatChangeEffect(Stat s, int stage, int percent, boolean userChange)
    {
        this.statChanges = new HashMap<>();
        this.statChanges.put(s, stage);

        this.percent = percent;
        this.userChange = userChange;
    }

    public StatChangeEffect(int stage, int percent, boolean userChange)
    {
        this(Stat.HP, stage, percent, userChange);
        Arrays.stream(Stat.values()).filter(s -> !s.equals(Stat.HP)).forEach(s -> this.add(s, stage));
    }

    public StatChangeEffect add(Stat s, int stage)
    {
        this.statChanges.put(s, stage);
        return this;
    }

    @Override
    public String get()
    {
        //TODO: Combine multiple stat changes with "and"
        StringBuilder result = new StringBuilder();

        Pokemon target = this.userChange ? this.user : this.opponent;

        if(new Random().nextInt(100) < this.percent)
        {
            for(Stat s : this.statChanges.keySet())
            {
                if(this.statChanges.containsKey(s))
                {
                    if(this.statChanges.get(s) < 0 && target.hasAbility(Ability.BIG_PECKS))
                    {
                        result.append(Ability.BIG_PECKS.formatActivation(target.getName(), target.getName() + "'s defense was not lowered!"));
                    }

                    if(this.statChanges.get(s) < 0 && this.duel.data(target.getUUID()).mistTurns > 0)
                    {
                        result.append(target.getName()).append(" is immune to the change in ").append(s.name);
                    }
                    else
                    {
                        target.changes().change(s, this.statChanges.get(s));
                        result.append(target.getName()).append("'s ").append(s.name).append(this.statChanges.get(s) > 0 ? " rose " : " was lowered ").append("by ").append(Math.abs(this.statChanges.get(s))).append(" stage").append(this.statChanges.get(s) > 1 ? "s" : "").append("! ");
                    }
                }
            }
        }

        return result.toString().trim();
    }
}
