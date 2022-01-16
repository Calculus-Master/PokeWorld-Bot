package com.calculusmaster.pokecord.game.moves.builder;

import java.util.function.Supplier;

public class CustomEffect extends MoveEffect
{
    private Supplier<String> effect;

    public CustomEffect(Supplier<String> effect)
    {
        this.effect = effect;
    }

    @Override
    public String get()
    {
        return this.effect.get();
    }
}
