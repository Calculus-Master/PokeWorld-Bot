package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.moves.Move;

public enum TR
{
    TR00("Swords Dance");

    private String move;

    TR(String move)
    {
        this.move = move;
    }

    public String getMoveName()
    {
        return this.move;
    }

    public Move asMove()
    {
        return Move.isMove(this.move) ? Move.asMove(this.move) : MoveList.Tackle;
    }

    public static TR getTR(int number)
    {
        for(TR t : values()) if(t.toString().contains(number + "")) return t;
        return null;
    }
}
