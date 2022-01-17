package com.calculusmaster.pokecord.game.enums.items;

import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.util.Global;

import java.util.Arrays;

import static com.calculusmaster.pokecord.game.enums.items.ItemType.*;

public enum Item
{
    NONE(0, MISC),
    //Functional Items (p!activate)
    EV_REALLOCATOR(0, FUNCTIONAL),
    IV_REROLLER(0, FUNCTIONAL),
    EV_CLEARER(0, FUNCTIONAL),
    //Pokemon Evolution Items
    FRIENDSHIP_BAND(500, EVOLUTION),
    THUNDER_STONE(250, EVOLUTION),
    ICE_STONE(250, EVOLUTION),
    MOON_STONE(250, EVOLUTION),
    FIRE_STONE(250, EVOLUTION),
    LEAF_STONE(250, EVOLUTION),
    SUN_STONE(250, EVOLUTION),
    WATER_STONE(250, EVOLUTION),
    DUSK_STONE(250, EVOLUTION),
    DAWN_STONE(250, EVOLUTION),
    SHINY_STONE(250, EVOLUTION),
    OVAL_STONE(250, EVOLUTION),
    TRADE_EVOLVER(1000, EVOLUTION),
    METAL_COAT(1500, EVOLUTION),
    KINGS_ROCK(250, EVOLUTION),
    GALARICA_CUFF(250, EVOLUTION),
    GALARICA_WREATH(250, EVOLUTION),
    RAZOR_FANG(250, EVOLUTION),
    RAZOR_CLAW(250, EVOLUTION),
    DRAGON_SCALE(250, EVOLUTION),
    UPGRADE(250, EVOLUTION),
    DUBIOUS_DISC(250, EVOLUTION),
    PRISM_SCALE(250, EVOLUTION),
    REAPER_CLOTH(250, EVOLUTION),
    DEEP_SEA_TOOTH(250, EVOLUTION),
    DEEP_SEA_SCALE(250, EVOLUTION),
    PROTECTOR(250, EVOLUTION),
    ELECTIRIZER(250, EVOLUTION),
    MAGMARIZER(250, EVOLUTION),
    SACHET(250, EVOLUTION),
    WHIPPED_DREAM(250, EVOLUTION),
    CRACKED_POT(250, EVOLUTION),
    TART_APPLE(250, EVOLUTION),
    SWEET_APPLE(250, EVOLUTION),
    SWEET(250, EVOLUTION),
    //Misc
    DESTINY_KNOT(1000, MISC),
    ZYGARDE_CUBE(6000, MISC),
    POWER_HERB(4000, MISC),
    LIGHT_CLAY(5000, MISC),
    //Arceus Plates
    DRACO_PLATE(1000, PLATE),
    DREAD_PLATE(1000, PLATE),
    EARTH_PLATE(1000, PLATE),
    FIST_PLATE(1000, PLATE),
    FLAME_PLATE(1000, PLATE),
    ICICLE_PLATE(1000, PLATE),
    INSECT_PLATE(1000, PLATE),
    IRON_PLATE(1000, PLATE),
    MEADOW_PLATE(1000, PLATE),
    MIND_PLATE(1000, PLATE),
    PIXIE_PLATE(1000, PLATE),
    SKY_PLATE(1000, PLATE),
    SPLASH_PLATE(1000, PLATE),
    SPOOKY_PLATE(1000, PLATE),
    STONE_PLATE(1000, PLATE),
    TOXIC_PLATE(1000, PLATE),
    ZAP_PLATE(1000, PLATE);

    public int cost;
    public ItemType type;

    Item(int cost, ItemType type)
    {
        this.cost = cost;
        this.type = type;
    }

    public String getName()
    {
        return this.toString();
    }

    public String getStyledName()
    {
        return switch(this) {
            case EV_REALLOCATOR -> "EV Reallocator";
            case IV_REROLLER -> "IV Reroller";
            case EV_CLEARER -> "EV Clearer";
            default -> Global.normalize(this.getName().replaceAll("_", " "));
        };
    }

    public static Item cast(String input)
    {
        return Arrays.stream(values()).filter(i -> i.toString().equalsIgnoreCase(input)).findFirst().orElse(null);
    }

    public Type getArceusPlateType()
    {
        return switch(this) {
            case DRACO_PLATE -> Type.DRAGON;
            case DREAD_PLATE -> Type.DARK;
            case EARTH_PLATE -> Type.GROUND;
            case FIST_PLATE -> Type.FIGHTING;
            case FLAME_PLATE -> Type.FIRE;
            case ICICLE_PLATE -> Type.ICE;
            case INSECT_PLATE -> Type.BUG;
            case IRON_PLATE -> Type.STEEL;
            case MEADOW_PLATE -> Type.GRASS;
            case MIND_PLATE -> Type.PSYCHIC;
            case PIXIE_PLATE -> Type.FAIRY;
            case SKY_PLATE -> Type.FLYING;
            case SPLASH_PLATE -> Type.WATER;
            case SPOOKY_PLATE -> Type.GHOST;
            case STONE_PLATE -> Type.ROCK;
            case TOXIC_PLATE -> Type.POISON;
            case ZAP_PLATE -> Type.ELECTRIC;
            default -> null;
        };
    }

    public boolean isPlateItem()
    {
        return this.type.equals(PLATE);
    }

    public boolean isFunctionalItem()
    {
        return this.type.equals(FUNCTIONAL);
    }
}
