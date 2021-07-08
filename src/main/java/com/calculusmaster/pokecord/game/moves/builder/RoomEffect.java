package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Room;
import com.calculusmaster.pokecord.util.Global;

public class RoomEffect extends MoveEffect
{
    private Room room;

    public RoomEffect(Room room)
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
