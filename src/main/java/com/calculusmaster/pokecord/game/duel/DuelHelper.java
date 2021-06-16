package com.calculusmaster.pokecord.game.duel;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.elements.Player;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;

import java.util.*;
import java.util.stream.Collectors;

public class DuelHelper
{
    public static final List<Duel> DUELS = new ArrayList<>();
    public static final String BACKGROUND = "https://cutewallpaper.org/21/pokemon-battle-background/Battle-backgrounds-for-Pokemon-Showdown-Smogon-Forums.jpg";

    public static boolean isInDuel(String id)
    {
        return DUELS.stream().anyMatch(d -> d.hasPlayer(id));
    }

    public static Duel instance(String id)
    {
        if(id.chars().allMatch(Character::isDigit)) return DUELS.stream().filter(d -> d.hasPlayer(id)).collect(Collectors.toList()).get(0);
        else return DUELS.stream().filter(d -> d.getPlayers()[0].active.getUUID().equals(id) || d.getPlayers()[1].active.getUUID().equals(id)).collect(Collectors.toList()).get(0);
    }

    public static void delete(String id)
    {
        int index = -1;
        for(Duel d : DUELS) if(d.hasPlayer(id)) index = DUELS.indexOf(d);
        DUELS.remove(index);
    }

    public static class DuelPokemon
    {
        public String UUID;
        public boolean canUseMove;
        public int lastDamageTaken;
        public int statImmuneTurns;
        public boolean recharge;
        public boolean isRaised;

        public int asleepTurns;
        public int boundTurns;
        public int badlyPoisonedTurns;

        public boolean flyUsed;
        public boolean bounceUsed;
        public boolean digUsed;
        public boolean diveUsed;
        public boolean phantomForceUsed;
        public boolean shadowForceUsed;

        public boolean defenseCurlUsed;
        public int rolloutTurns;
        public int iceballTurns;
        public boolean rageUsed;
        public int magnetRiseTurns;
        public int tauntTurns;
        public boolean detectUsed;
        public boolean protectUsed;
        public boolean chargeUsed;
        public boolean futureSightUsed;
        public int futureSightTurns;
        public boolean lockOnUsed;
        public boolean thousandWavesUsed;
        public boolean imprisonUsed;
        public boolean destinyBondUsed;
        public int perishSongTurns;
        public boolean kingsShieldUsed;
        public int bideTurns;
        public int bideDamage;

        public boolean disguiseActivated;

        public DuelPokemon(String UUID)
        {
            this.UUID = UUID;
            this.setDefaults();
        }

        public void setDefaults()
        {
            this.canUseMove = true;
            this.lastDamageTaken = 0;
            this.statImmuneTurns = 0;
            this.recharge = false;
            this.isRaised = false;

            this.asleepTurns = 0;
            this.boundTurns = 0;
            this.badlyPoisonedTurns = 0;

            this.flyUsed = false;
            this.bounceUsed = false;
            this.digUsed = false;
            this.diveUsed = false;
            this.phantomForceUsed = false;
            this.shadowForceUsed = false;

            this.defenseCurlUsed = false;
            this.rolloutTurns = 0;
            this.iceballTurns = 0;
            this.rageUsed = false;
            this.magnetRiseTurns = 0;
            this.tauntTurns = 0;
            this.detectUsed = false;
            this.protectUsed = false;
            this.chargeUsed = false;
            this.futureSightUsed = false;
            this.futureSightTurns = 0;
            this.lockOnUsed = false;
            this.thousandWavesUsed = false;
            this.imprisonUsed = false;
            this.destinyBondUsed = false;
            this.perishSongTurns = 0;
            this.kingsShieldUsed = false;
            this.bideTurns = 0;
            this.bideDamage = 0;

            this.disguiseActivated = false;
        }
    }

    public record TurnAction(ActionType action, int moveInd, int swapInd) {}

    public static class EntryHazardHandler
    {
        private Map<EntryHazard, Integer> entryHazards;

        public EntryHazardHandler()
        {
            this.entryHazards = new HashMap<>();

            this.clearHazards();
        }

        public void addHazard(EntryHazard hazard)
        {
            int current = this.entryHazards.get(hazard);
            int hazardLimit = hazard.equals(EntryHazard.SPIKES) ? 3 : (hazard.equals(EntryHazard.TOXIC_SPIKES) ? 2 : 1);

            this.entryHazards.put(hazard, Math.min(hazardLimit, current + 1));
        }

        public boolean hasHazard(EntryHazard hazard)
        {
            return this.entryHazards.get(hazard) > 0;
        }

        public int getHazard(EntryHazard hazard)
        {
            return this.entryHazards.get(hazard);
        }

        public void removeHazard(EntryHazard hazard)
        {
            this.entryHazards.put(hazard, 0);
        }

        public void clearHazards()
        {
            this.removeHazard(EntryHazard.SPIKES);
            this.removeHazard(EntryHazard.STEALTH_ROCK);
            this.removeHazard(EntryHazard.STICKY_WEB);
            this.removeHazard(EntryHazard.TOXIC_SPIKES);
        }
    }

    public enum EntryHazard
    {
        SPIKES,
        STEALTH_ROCK,
        STICKY_WEB,
        TOXIC_SPIKES;
    }

    public enum Room
    {
        NORMAL_ROOM,
        TRICK_ROOM,
        WONDER_ROOM,
        MAGIC_ROOM;
    }

    public enum Terrain
    {
        NORMAL_TERRAIN,
        ELECRIC_TERRAIN,
        GRASSY_TERRAIN,
        MISTY_TERRAIN,
        PSYCHIC_TERRAIN;
    }

    public enum ActionType
    {
        MOVE,
        ZMOVE,
        SWAP,
        DYNAMAX;
    }

    public enum DuelStatus
    {
        WAITING,
        DUELING,
        COMPLETE;
    }

    public static Move getZMove(Player p, Move baseMove)
    {
        ZCrystal z = ZCrystal.cast(p.data.getEquippedZCrystal());
        Move fallback = new Move("Tackle");

        if(z == null) return fallback;

        Move ZMove = fallback;

        int ZPower;
        if(baseMove.getPower() <= 55) ZPower = 100;
        else if(baseMove.getPower() <= 65) ZPower = 120;
        else if(baseMove.getPower() <= 75) ZPower = 140;
        else if(baseMove.getPower() <= 85) ZPower = 160;
        else if(baseMove.getPower() <= 95) ZPower = 175;
        else if(baseMove.getPower() <= 100) ZPower = 180;
        else if(baseMove.getPower() <= 110) ZPower = 185;
        else if(baseMove.getPower() <= 125) ZPower = 190;
        else if(baseMove.getPower() <= 130) ZPower = 195;
        else ZPower = 200;

        ZPower = switch(baseMove.getName()) {
            case "Mega Drain" -> 120;
            case "Core Enforcer" -> 140;
            case "Weather Ball", "Hex" -> 160;
            case "Flying Press" -> 170;
            case "Gear Grind", "Fissure", "Guillotine", "Horn Drill", "Sheer Cold" -> 180;
            case "V Create" -> 220;
            default -> ZPower;
        };

        switch(z)
        {
            //Types
            case BUGINIUM_Z -> ZMove = new Move("Savage Spin Out", Type.BUG, baseMove.getCategory(), 0);
            case DARKINIUM_Z -> ZMove = new Move("Black Hole Eclipse", Type.DARK, baseMove.getCategory(), 0);
            case DRAGONIUM_Z -> ZMove = new Move("Devastating Drake", Type.DRAGON, baseMove.getCategory(), 0);
            case ELECTRIUM_Z -> ZMove = new Move("Gigavolt Havoc", Type.ELECTRIC, baseMove.getCategory(), 0);
            case FAIRIUM_Z -> ZMove = new Move("Twinkle Tackle", Type.FAIRY, baseMove.getCategory(), 0);
            case FIGHTINIUM_Z -> ZMove = new Move("All Out Pummeling", Type.FIGHTING, baseMove.getCategory(), 0);
            case FIRIUM_Z -> ZMove = new Move("Inferno Overdrive", Type.FIRE, baseMove.getCategory(), 0);
            case FLYINIUM_Z -> ZMove = new Move("Supersonic Skystrike", Type.FLYING, baseMove.getCategory(), 0);
            case GHOSTIUM_Z -> ZMove = new Move("Never Ending Nightmare", Type.GHOST, baseMove.getCategory(), 0);
            case GRASSIUM_Z -> ZMove = new Move("Bloom Doom", Type.GRASS, baseMove.getCategory(), 0);
            case GROUNDIUM_Z -> ZMove = new Move("Tectonic Rage", Type.GROUND, baseMove.getCategory(), 0);
            case ICIUM_Z -> ZMove = new Move("Subzero Slammer", Type.ICE, baseMove.getCategory(), 0);
            case NORMALIUM_Z -> ZMove = new Move("Breakneck Blitz", Type.NORMAL, baseMove.getCategory(), 0);
            case POISONIUM_Z -> ZMove = new Move("Acid Downpour", Type.POISON, baseMove.getCategory(), 0);
            case PSYCHIUM_Z -> ZMove = new Move("Shattered Psyche", Type.PSYCHIC, baseMove.getCategory(), 0);
            case ROCKIUM_Z -> ZMove = new Move("Continental Crush", Type.ROCK, baseMove.getCategory(), 0);
            case STEELIUM_Z -> ZMove = new Move("Corkscrew Crash", Type.STEEL, baseMove.getCategory(), 0);
            case WATERIUM_Z -> ZMove = new Move("Hydro Vortex", Type.WATER, baseMove.getCategory(), 0);
            //Uniques
            case ALORAICHIUM_Z -> ZMove = new Move("Stoked Sparksurfer", Type.ELECTRIC, Category.SPECIAL, 175);
            case DECIDIUM_Z -> ZMove = new Move("Sinister Arrow Raid", Type.GHOST, Category.PHYSICAL, 180);
            case EEVIUM_Z -> ZMove = new Move("Extreme Evoboost", Type.NORMAL, Category.STATUS, 0);
            case INCINIUM_Z -> ZMove = new Move("Malicious Moonsault", Type.DARK, Category.PHYSICAL, 180);
            case KOMMOIUM_Z -> ZMove = new Move("Clangorous Soulblaze", Type.DRAGON, Category.SPECIAL, 185);
            case LUNALIUM_Z -> ZMove = new Move("Menacing Moonraze Maelstrom", Type.GHOST, Category.SPECIAL, 200);
            case LYCANIUM_Z -> ZMove = new Move("Splintered Stormshards", Type.ROCK, Category.PHYSICAL, 190);
            case MARSHADIUM_Z -> ZMove = new Move("Soul Stealing 7 Star Strike", Type.GHOST, Category.PHYSICAL, 195);
            case MEWNIUM_Z -> ZMove = new Move("Genesis Supernova", Type.PSYCHIC, Category.SPECIAL, 185);
            case MIMIKIUM_Z -> ZMove = new Move("Let's Snuggle Forever", Type.FAIRY, Category.PHYSICAL, 190);
            case PIKANIUM_Z -> ZMove = new Move("Catastropika", Type.ELECTRIC, Category.PHYSICAL, 210);
            case PIKASHUNIUM_Z -> ZMove = new Move("10,000,000 Volt Thunderbolt", Type.ELECTRIC, Category.SPECIAL, 195);
            case PRIMARIUM_Z -> ZMove = new Move("Oceanic Operetta", Type.WATER, Category.SPECIAL, 195);
            case SNORLIUM_Z -> ZMove = new Move("Pulverizing Pancake", Type.NORMAL, Category.PHYSICAL, 210);
            case SOLGANIUM_Z -> ZMove = new Move("Searing Sunraze Smash", Type.STEEL, Category.PHYSICAL, 200);
            case TAPUNIUM_Z -> ZMove = new Move("Guardian of Alola", Type.FAIRY, Category.SPECIAL, 0);
            case ULTRANECROZIUM_Z -> {
                if(baseMove.getName().equals("Photon Geyser")) ZMove = new Move("Light That Burns The Sky", Type.PSYCHIC, Category.SPECIAL, 200);
                else if(baseMove.getName().equals("Prismatic Laser")) ZMove = new Move("Prismatic Light Beam", Type.PSYCHIC, Category.SPECIAL, 220);
            }
            //Custom Uniques
            case RESHIRIUM_Z -> ZMove = new Move("White Hot Inferno", Type.FIRE, Category.SPECIAL, 200);
            case ZEKRIUM_Z -> ZMove = new Move("Supercharged Storm Surge", Type.ELECTRIC, Category.PHYSICAL, 200);
            case KYURIUM_Z -> ZMove = new Move("Eternal Winter", Type.ICE, Category.SPECIAL, 180);
            case XERNIUM_Z -> ZMove = new Move("Tree Of Life", Type.FAIRY, Category.STATUS, 0);
            case YVELTIUM_Z -> ZMove = new Move("Cocoon Of Destruction", Type.DARK, Category.SPECIAL, 185);
            case DIANCIUM_Z -> ZMove = new Move("Dazzling Diamond Barrage", Type.ROCK, Category.PHYSICAL, 180);
            case ARCEIUM_Z -> ZMove = new Move("Decree Of Arceus", Type.NORMAL, Category.PHYSICAL, 150);
            case RAYQUAZIUM_Z -> ZMove = new Move("Draconic Ozone Ascent", Type.DRAGON, Category.PHYSICAL, 200);
            case ZYGARDIUM_Z -> ZMove = switch (baseMove.getName()) {
                case "Lands Wrath" -> new Move("Tectonic Z Wrath", Type.GROUND, Category.PHYSICAL, 180);
                case "Core Enforcer" -> new Move("Titanic Z Enforcer", Type.DRAGON, Category.SPECIAL, 195);
                case "Thousand Arrows" -> new Move("Million Arrow Barrage", Type.GROUND, Category.PHYSICAL, 190);
                case "Thousand Waves" -> new Move("Million Wave Tsunami", Type.GROUND, Category.PHYSICAL, 190);
                default -> new Move("Tackle");
            };
            case VOLCANIUM_Z -> ZMove = new Move("Volcanic Steam Geyser", Type.WATER, Category.SPECIAL, 195);
            case KYOGRIUM_Z -> ZMove = new Move("Primordial Tsunami", Type.WATER, Category.SPECIAL, 195);
            case GROUDONIUM_Z -> ZMove = new Move("Primordial Landslide", Type.GROUND, Category.PHYSICAL, 195);
            case GENESECTIUM_Z -> ZMove = new Move("Elemental Techno Overdrive", Type.NORMAL, Category.SPECIAL, 195);
            case MELMETALIUM_Z -> ZMove = new Move("Quadruple Steel Smash", Type.STEEL, Category.PHYSICAL, 75);
            case DIALGIUM_Z -> ZMove = new Move("Timeline Shatter", Type.DRAGON, Category.SPECIAL, 200);
            case PALKIUM_Z -> ZMove = new Move("Ultra Space Hypernova", Type.DRAGON, Category.SPECIAL, 200);
            case GIRATINIUM_Z -> ZMove = new Move("Dark Matter Explosion", Type.DRAGON, Category.SPECIAL, 270);
        }

        if(z.ordinal() <= 17) ZMove.setPower(ZPower);

        return ZMove;
    }

    public static Move getMaxMove(Pokemon p, Move baseMove)
    {
        //Fallback
        Move maxMove;

        String maxName;
        Type maxType = baseMove.getType();
        Category maxCategory = baseMove.getCategory();
        int maxPower = baseMove.getPower();

        if(baseMove.getCategory().equals(Category.STATUS))
        {
            maxName = "Max Guard";
            maxType = Type.NORMAL;
        }
        else if(p.canGigantamax() && Pokemon.GIGANTAMAX_DATA.get(p.getName()).moveType().equals(baseMove.getType()))
        {
            Pokemon.GigantamaxData data = Pokemon.GIGANTAMAX_DATA.get(p.getName());
            maxName = data.move();
            maxType = data.moveType();
        }
        else maxName = "Max " + switch(baseMove.getType()) {
            case BUG -> "Flutterby";
            case DARK -> "Darkness";
            case DRAGON -> "Wyrmwind";
            case ELECTRIC -> "Lightning";
            case FAIRY -> "Starfall";
            case FIGHTING -> "Knuckle";
            case FIRE -> "Flare";
            case FLYING -> "Airstream";
            case GHOST -> "Phantasm";
            case GRASS -> "Overgrowth";
            case GROUND -> "Quake";
            case ICE -> "Hailstorm";
            case NORMAL -> "Strike";
            case POISON -> "Ooze";
            case PSYCHIC -> "Mindstorm";
            case ROCK -> "Rockfall";
            case STEEL -> "Steelspike";
            case WATER -> "Geyser";
        };

        boolean isDecreased = Arrays.asList("Max Knuckle", "Max Ooze").contains(maxName);
        if(baseMove.getPower() <= 40) maxPower = isDecreased ? 70 : 90;
        else if(baseMove.getPower() <= 50) maxPower = isDecreased ? 75 : 100;
        else if(baseMove.getPower() <= 60) maxPower = isDecreased ? 80 : 110;
        else if(baseMove.getPower() <= 70) maxPower = isDecreased ? 85 : 120;
        else if(baseMove.getPower() <= 100) maxPower = isDecreased ? 90 : 130;
        else if(baseMove.getPower() <= 140) maxPower = isDecreased ? 95 : 140;
        else maxPower = isDecreased ? 100 : 150;

        maxMove = new Move(maxName, maxType, maxCategory, maxPower);
        maxMove.isZMove = false;
        maxMove.isMaxMove = true;

        return maxMove;
    }
}
