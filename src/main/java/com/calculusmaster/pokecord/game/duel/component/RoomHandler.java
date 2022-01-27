package com.calculusmaster.pokecord.game.duel.component;

import com.calculusmaster.pokecord.game.enums.elements.Room;

import java.util.*;

public class RoomHandler
{
    private EnumSet<Room> rooms;
    private Map<Room, Integer> turns;

    public RoomHandler()
    {
        this.rooms = EnumSet.noneOf(Room.class);
        this.turns = new HashMap<>();
    }

    public boolean isActive(Room room)
    {
        return this.rooms.contains(room);
    }

    public void addRoom(Room room)
    {
        this.rooms.add(room);
        this.turns.put(room, 5);
    }

    public void removeRoom(Room room)
    {
        this.rooms.remove(room);
        this.turns.remove(room);
    }

    public void updateTurns()
    {
        this.turns.replaceAll((r, i) -> i - 1);

        List<Runnable> queue = new ArrayList<>();
        for(Room r : this.rooms) if(this.turns.get(r) <= 0) queue.add(() -> this.removeRoom(r));

        queue.forEach(Runnable::run);
    }
}
