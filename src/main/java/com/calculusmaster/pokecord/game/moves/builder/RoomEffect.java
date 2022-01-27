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
        String stylizedName = Global.normalize(this.room.toString().replaceAll("_", " "));

        if(this.duel.room.isActive(this.room))
        {
            this.duel.room.removeRoom(this.room);
            return this.user.getName() + " removed the " + stylizedName + "!";
        }
        else
        {
            this.duel.room.addRoom(this.room);
            return this.user.getName() + " created a strange area! A " + stylizedName + " was set up!";
        }
    }
}
