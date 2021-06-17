package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.util.Global;

public class RoomEffect extends MoveEffect
{
    private DuelHelper.Room room;

    public RoomEffect(DuelHelper.Room room)
    {
        this.room = room;
    }

    @Override
    public String get()
    {
        this.duel.room = this.room;
        this.duel.roomTurns = 5;

        return this.user.getName() + " created a weird area! (" + Global.normalCase(this.room.toString().replaceAll("_", " ")) + ")!";
    }
}
