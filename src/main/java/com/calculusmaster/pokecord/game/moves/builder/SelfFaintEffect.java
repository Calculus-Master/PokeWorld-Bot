package com.calculusmaster.pokecord.game.moves.builder;

public class SelfFaintEffect extends MoveEffect
{
    @Override
    public String get()
    {
        this.user.damage(this.user.getHealth());

        return this.user.getName() + " fainted!";
    }
}
