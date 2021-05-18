package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.game.Move;

public enum TR
{
    TR00("Swords Dance"),
    TR01("Body Slam"),
    TR02("Flamethrower"),
    TR03("Hydro Pump"),
    TR04("Surf"),
    TR05("Ice Beam"),
    TR06("Blizzard"),
    TR07("Low Kick"),
    TR08("Thunderbolt"),
    TR09("Thunder"),
    TR10("Earthquake"),
    TR11("Psychic"),
    TR12("Agility"),
    TR13("Focus Energy"),
    TR14("Metronome"),
    TR15("Fire Blast"),
    TR16("Waterfall"),
    TR17("Amnesia"),
    TR18("Leech Life"),
    TR19("Tri Attack"),
    TR20("Substitute"),
    TR21("Reversal"),
    TR22("Sludge Bomb"),
    TR23("Spikes"),
    TR24("Outrage"),
    TR25("Psyshock"),
    TR26("Endure"),
    TR27("Sleep Talk"),
    TR28("Megahorn"),
    TR29("Baton Pass"),
    TR30("Encore"),
    TR31("Iron Tail"),
    TR32("Crunch"),
    TR33("Shadow Ball"),
    TR34("Future Sight"),
    TR35("Uproar"),
    TR36("Heat Wave"),
    TR37("Taunt"),
    TR38("Trick"),
    TR39("Superpower"),
    TR40("Skill Swap"),
    TR41("Blaze Kick"),
    TR42("Hyper Voice"),
    TR43("Overheat"),
    TR44("Cosmic Power"),
    TR45("Muddy Water"),
    TR46("Iron Defense"),
    TR47("Dragon Claw"),
    TR48("Bulk Up"),
    TR49("Calm Mind"),
    TR50("Leaf Blade"),
    TR51("Dragon Dance"),
    TR52("Gyro Ball"),
    TR53("Close Combat"),
    TR54("Toxic Spikes"),
    TR55("Flare Blitz"),
    TR56("Aura Sphere"),
    TR57("Poison Jab"),
    TR58("Dark Pulse"),
    TR59("Seed Bomb"),
    TR60("X-Scissor"),
    TR61("Bug Buzz"),
    TR62("Dragon Pulse"),
    TR63("Power Gem"),
    TR64("Focus Blast"),
    TR65("Energy Ball"),
    TR66("Brave Bird"),
    TR67("Earth Power"),
    TR68("Nasty Plot"),
    TR69("Zen Headbutt"),
    TR70("Flash Cannon"),
    TR71("Leaf Storm"),
    TR72("Power Whip"),
    TR73("Gunk Shot"),
    TR74("Iron Head"),
    TR75("Stone Edge"),
    TR76("Stealth Rock"),
    TR77("Grass Knot"),
    TR78("Sludge Wave"),
    TR79("Heavy Slam"),
    TR80("Electro Ball"),
    TR81("Foul Play"),
    TR82("Stored Power"),
    TR83("Ally Switch"),
    TR84("Scald"),
    TR85("Work Up"),
    TR86("Wild Charge"),
    TR87("Drill Run"),
    TR88("Heat Crash"),
    TR89("Hurricane"),
    TR90("Play Rough"),
    TR91("Venom Drench"),
    TR92("Dazzling Gleam"),
    TR93("Darkest Lariat"),
    TR94("High Horsepower"),
    TR95("Throat Chop"),
    TR96("Pollen Puff"),
    TR97("Psychic Fangs"),
    TR98("Liquidation"),
    TR99("Body Press");

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
        return Move.isMove(this.move) ? new Move(this.move) : new Move("Tackle");
    }

    public Move.MoveData getMoveData()
    {
        return Move.isMove(this.move) ? Move.MOVES.get(this.move) : Move.MOVES.get("Tackle");
    }

    public static TR get(int number)
    {
        return values()[number];
    }
}
