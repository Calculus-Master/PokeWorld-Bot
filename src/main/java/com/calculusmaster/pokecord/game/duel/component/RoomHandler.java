package com.calculusmaster.pokecord.game.duel.component;

import com.calculusmaster.pokecord.game.enums.elements.Room;

import java.util.*;

public class RoomHandler
{
    private EnumSet<Room> rooms;
    private Map<Room, Integer> roomTurns;

    public RoomHandler()
    {
        this.rooms = EnumSet.noneOf(Room.class);
        this.roomTurns = new HashMap<>();
    }

    public boolean isActive(Room room)
    {
        return this.rooms.contains(room);
    }

    public void addRoom(Room room)
    {
        this.rooms.add(room);
        this.roomTurns.put(room, 5);
    }

    public void removeRoom(Room room)
    {
        this.rooms.remove(room);
        this.roomTurns.remove(room);
    }

    public void updateTurns()
    {
        this.roomTurns.replaceAll((r, i) -> i - 1);

        List<Runnable> queue = new ArrayList<>();
        for(Room r : this.rooms) if(this.roomTurns.get(r) <= 0) queue.add(() -> this.removeRoom(r));

        queue.forEach(Runnable::run);
    }
}
