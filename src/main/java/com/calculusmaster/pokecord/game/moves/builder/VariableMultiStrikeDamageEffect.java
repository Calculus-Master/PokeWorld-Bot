package com.calculusmaster.pokecord.game.moves.builder;

import java.util.Random;

public class VariableMultiStrikeDamageEffect extends FixedMultiStrikeDamageEffect
{
    public VariableMultiStrikeDamageEffect()
    {
        super(switch(new Random().nextInt(8)) {
            case 0, 1, 2 -> 2;
            case 3, 4, 5 -> 3;
            case 6 -> 4;
            case 7 -> 5;
            default -> 2;
        });
    }
}
