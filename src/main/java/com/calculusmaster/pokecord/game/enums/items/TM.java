package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.util.helpers.DataHelper;

public enum TM
{
    TM01("Work Up"),
    TM02("Dragon Claw"),
    TM03("Psyshock"),
    TM04("Calm Mind"),
    TM05("Roar"),
    TM06("Toxic"),
    TM07("Hail"),
    TM08("Bulk Up"),
    TM09("Venoshock"),
    TM10("Hidden Power"),
    TM11("Sunny Day"),
    TM12("Taunt"),
    TM13("Ice Beam"),
    TM14("Blizzard"),
    TM15("Hyper Beam"),
    TM16("Light Screen"),
    TM17("Protect"),
    TM18("Rain Dance"),
    TM19("Roost"),
    TM20("Safeguard"),
    TM21("Frustration"),
    TM22("Solar Beam"),
    TM23("Smack Down"),
    TM24("Thunderbolt"),
    TM25("Thunder"),
    TM26("Earthquake"),
    TM27("Return"),
    TM28("Leech Life"),
    TM29("Psychic"),
    TM30("Shadow Ball"),
    TM31("Brick Break"),
    TM32("Double Team"),
    TM33("Reflect"),
    TM34("Sludge Wave"),
    TM35("Flamethrower"),
    TM36("Sludge Bomb"),
    TM37("Sandstorm"),
    TM38("Fire Blast"),
    TM39("Rock Tomb"),
    TM40("Aerial Ace"),
    TM41("Torment"),
    TM42("Facade"),
    TM43("Flame Charge"),
    TM44("Rest"),
    TM45("Attract"),
    TM46("Thief"),
    TM47("Low Sweep"),
    TM48("Round"),
    TM49("Echoed Voice"),
    TM50("Overheat"),
    TM51("Steel Wing"),
    TM52("Focus Blast"),
    TM53("Energy Ball"),
    TM54("False Swipe"),
    TM55("Scald"),
    TM56("Fling"),
    TM57("Charge Beam"),
    TM58("Sky Drop"),
    TM59("Brutal Swing"),
    TM60("Quash"),
    TM61("Will O Wisp"),
    TM62("Acrobatics"),
    TM63("Embargo"),
    TM64("Explosion"),
    TM65("Shadow Claw"),
    TM66("Payback"),
    TM67("Smart Strike"),
    TM68("Giga Impact"),
    TM69("Rock Polish"),
    TM70("Aurora Veil"),
    TM71("Stone Edge"),
    TM72("Volt Switch"),
    TM73("Thunder Wave"),
    TM74("Gyro Ball"),
    TM75("Swords Dance"),
    TM76("Fly"),
    TM77("Psych Up"),
    TM78("Bulldoze"),
    TM79("Frost Breath"),
    TM80("Rock Slide"),
    TM81("X Scissor"),
    TM82("Dragon Tail"),
    TM83("Infestation"),
    TM84("Poison Jab"),
    TM85("Dream Eater"),
    TM86("Grass Knot"),
    TM87("Swagger"),
    TM88("Sleep Talk"),
    TM89("U Turn"),
    TM90("Substitute"),
    TM91("Flash Cannon"),
    TM92("Trick Room"),
    TM93("Wild Charge"),
    TM94("Surf"),
    TM95("Snarl"),
    TM96("Nature Power"),
    TM97("Dark Pulse"),
    TM98("Waterfall"),
    TM99("Dazzling Gleam"),
    TM100("Confide");

    private String move;
    TM(String move)
    {
        this.move = move;
    }

    public String getMoveName()
    {
        return this.move;
    }

    public MoveData getMoveData()
    {
        return DataHelper.moveData(this.move);
    }

    public int getNumber()
    {
        return Integer.parseInt(this.toString().replaceAll("TM", ""));
    }

    public String getShopEntry()
    {
        return "`" + this + "` - " + this.getMoveName();
    }

    public static boolean isOutOfBounds(int num)
    {
        return num < 1 || num > 100;
    }

    public static TM get(int number)
    {
        return values()[number - 1];
    }

    public static TM get(String TM)
    {
        return get(Integer.parseInt(TM.toLowerCase().replaceAll("tm", "")));
    }
}
