package com.calculusmaster.pokecord.game.enums.elements;

import com.calculusmaster.pokecord.util.Global;

import java.util.HashMap;
import java.util.Map;

import static com.calculusmaster.pokecord.game.enums.elements.Stat.*;

public enum Nature
{
    HARDY(ATK, ATK),
    LONELY(ATK, DEF),
    BRAVE(ATK, SPD),
    ADAMANT(ATK, SPATK),
    NAUGHTY(ATK, SPDEF),
    BOLD(DEF, ATK),
    DOCILE(DEF, DEF),
    RELAXED(DEF, SPD),
    IMPISH(DEF, SPATK),
    LAX(DEF, SPDEF),
    TIMID(SPD, ATK),
    HASTY(SPD, DEF),
    SERIOUS(SPD, SPD),
    JOLLY(SPD, SPATK),
    NAIVE(SPD, SPDEF),
    MODEST(SPATK, ATK),
    MILD(SPATK, SPDEF),
    QUIET(SPATK, SPD),
    BASHFUL(SPATK, SPATK),
    RASH(SPATK, SPDEF),
    CALM(SPDEF, ATK),
    GENTLE(SPDEF, DEF),
    SASSY(SPDEF, SPD),
    CAREFUL(SPDEF, SPATK),
    QUIRKY(SPDEF, SPDEF);

    private final Map<Stat, Double> multipliers = new HashMap<>();
    private final Stat increase;
    private final Stat decrease;

    Nature(Stat increase, Stat decrease)
    {
        this.increase = increase;
        this.decrease = decrease;

        for(Stat s : Stat.values()) this.multipliers.put(s, 1.0);

        if(!increase.equals(decrease))
        {
            this.multipliers.put(increase, 1.1);
            this.multipliers.put(decrease, 0.9);
        }
    }

    public Map<Stat, Double> getMap()
    {
        return this.multipliers;
    }

    public static Nature cast(String nature)
    {
        return (Nature) Global.getEnumFromString(values(), nature);
    }

    public String getShopEntry()
    {
        return (this.increase.equals(this.decrease) ? "~~" : "") + "+10% **" + this.increase + "**\n-10% **" + this.decrease + "**" + (this.increase.equals(this.decrease) ? "~~" : "");
    }
}
