package com.calculusmaster.pokecord.game.duel;

import com.calculusmaster.pokecord.game.Move;
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

        public EntryHazardHandler entryHazards;

        public boolean flyUsed;
        public boolean bounceUsed;
        public boolean digUsed;
        public boolean diveUsed;

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

            this.entryHazards = new EntryHazardHandler();

            this.flyUsed = false;
            this.bounceUsed = false;
            this.digUsed = false;
            this.diveUsed = false;

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
        SWAP;
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

        Move fallback = new Move("Tackle"); fallback.setPower(80);
        if(z == null) return fallback;

        Move ZMove = new Move("", baseMove.getType(), null, 0);

        switch(z)
        {
            //Types
            case BUGINIUM_Z -> {

                ZMove = new Move("Savage Spin Out", Type.BUG, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Fell Stinger", "Fury Cutter", "Infestation", "Struggle Bug", "Twineedle" -> ZMove.setPower(100);
                    case "Bug Bite", "Silver Wind", "Steamroller" -> ZMove.setPower(120);
                    case "Pin Missile", "Signal Beam", "U Turn" -> ZMove.setPower(140);
                    case "Leech Life", "Lunge", "X Scissor" -> ZMove.setPower(160);
                    case "Attack Order", "Bug Buzz", "First Impression", "Pollen Puff" -> ZMove.setPower(175);
                    case "Megahorn" -> ZMove.setPower(190);
                    default -> ZMove = fallback;
                }
            }
            case DARKINIUM_Z -> {

                ZMove = new Move("Black Hole Eclipse", Type.DARK, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Beat Up", "Fling", "Payback", "Pursuit", "Snarl" -> ZMove.setPower(100);
                    case "Assurance", "Bite", "Brutal Swing", "Feint Attack", "Knock Off", "Thief" -> ZMove.setPower(120);
                    case "Night Slash", "Sucker Punch" -> ZMove.setPower(140);
                    case "Crunch", "Dark Pulse", "Darkest Lariat", "Night Daze", "Power Trip", "Punishment", "Throat Chop" -> ZMove.setPower(160);
                    case "Foul Play" -> ZMove.setPower(175);
                    case "Hyperspace Fury" -> ZMove.setPower(190);
                    default -> ZMove = fallback;
                }
            }
            case DRAGONIUM_Z -> {

                ZMove = new Move("Devastating Drake", Type.DRAGON, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Dragon Rage", "Dual Chop", "Twister" -> ZMove.setPower(100);
                    case "Dragon Breath", "Dragon Tail" -> ZMove.setPower(120);
                    case "Core Enforcer" -> ZMove.setPower(140);
                    case "Dragon Claw", "Dragon Pulse" -> ZMove.setPower(160);
                    case "Dragon Hammer" -> ZMove.setPower(175);
                    case "Dragon Rush", "Spacial Rend" -> ZMove.setPower(180);
                    case "Clanging Scales" -> ZMove.setPower(185);
                    case "Outrage" -> ZMove.setPower(190);
                    case "Draco Meteor" -> ZMove.setPower(195);
                    case "Roar Of Time", "Dragon Energy" -> ZMove.setPower(200);
                    case "Eternabeam" -> ZMove.setPower(210);
                    default -> ZMove = fallback;
                }
            }
            case ELECTRIUM_Z -> {

                ZMove = new Move("Gigavolt Havoc", Type.ELECTRIC, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Charge Beam", "Electroweb", "Nuzzle", "Thunder Shock" -> ZMove.setPower(100);
                    case "Parabolic Charge", "Shock Wave", "Spark", "Thunder Fang" -> ZMove.setPower(120);
                    case "Thunder Punch", "Volt Switch" -> ZMove.setPower(140);
                    case "Discharge", "Electro Ball", "Zing Zap", "Thunder Cage" -> ZMove.setPower(160);
                    case "Thunderbolt", "Wild Charge" -> ZMove.setPower(175);
                    case "Fusion Bolt", "Plasma Fists" -> ZMove.setPower(180);
                    case "Thunder" -> ZMove.setPower(185);
                    case "Volt Tackle", "Zap Cannon" -> ZMove.setPower(190);
                    case "Bolt Strike" -> ZMove.setPower(195);
                    default -> ZMove = fallback;
                }
            }
            case FAIRIUM_Z -> {

                ZMove = new Move("Twinkle Tackle", Type.FAIRY, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Disarming Voice", "Draining Kiss", "Fairy Wind", "Nature's Madness" -> ZMove.setPower(100);
                    case "Dazzling Gleam" -> ZMove.setPower(160);
                    case "Moonblast", "Play Rough" -> ZMove.setPower(175);
                    case "Fleur Cannon" -> ZMove.setPower(195);
                    case "Light Of Ruin" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }
            }
            case FIGHTINIUM_Z -> {

                ZMove = new Move("All Out Pummeling", Type.FIGHTING, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Arm Thrust", "Counter", "Double Kick", "Karate Chop", "Mach Punch", "Power Up Punch", "Rock Smash", "Seismic Toss", "Vacuum Wave" -> ZMove.setPower(100);
                    case "Circle Throw", "Force Palm", "Low Sweep", "Revenge", "Rolling Kick", "Storm Throw", "Triple Kick" -> ZMove.setPower(120);
                    case "Brick Break", "Drain Punch", "Vital Throw", "Wake Up Slap" -> ZMove.setPower(140);
                    case "Aura Sphere", "Low Kick", "Reversal", "Secret Sword", "Sky Uppercut", "Submission" -> ZMove.setPower(160);
                    case "Flying Press" -> ZMove.setPower(170);
                    case "Sacred Sword", "Thunderous Kick" -> ZMove.setPower(175);
                    case "Cross Chop", "Dynamic Punch", "Final Gambit", "Hammer Arm", "Jump Kick" -> ZMove.setPower(180);
                    case "Close Combat", "Focus Blast", "Superpower" -> ZMove.setPower(190);
                    case "High Jump Kick" -> ZMove.setPower(195);
                    case "Focus Punch" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }
            }
            case FIRIUM_Z -> {

                ZMove = new Move("Inferno Overdrive", Type.FIRE, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Ember", "Fire Spin", "Flame Charge" -> ZMove.setPower(100);
                    case "Fire Fang", "Flame Wheel", "Incinerate" -> ZMove.setPower(120);
                    case "Fire Punch", "Flame Burst", "Mystical Fire" -> ZMove.setPower(140);
                    case "Blaze Kick", "Fiery Dance", "Fire Lash", "Fire Pledge", "Heat Crash", "Lava Plume", "Weather Ball" -> ZMove.setPower(160);
                    case "Flamethrower", "Heat Wave" -> ZMove.setPower(175);
                    case "Fusion Flare", "Inferno", "Magma Storm", "Sacred Fire", "Searing Shot" -> ZMove.setPower(180);
                    case "Fire Blast" -> ZMove.setPower(185);
                    case "Flare Blitz" -> ZMove.setPower(190);
                    case "Blue Flare", "Burn Up", "Overheat" -> ZMove.setPower(195);
                    case "Blast Burn", "Eruption", "Mind Blown", "Shell Trap" -> ZMove.setPower(200);
                    case "V Create" -> ZMove.setPower(220);
                    default -> ZMove = fallback;
                }
            }
            case FLYINIUM_Z -> {

                ZMove = new Move("Supersonic Skystrike", Type.FLYING, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Acrobatics", "Gust", "Peck" -> ZMove.setPower(100);
                    case "Aerial Ace", "Air Cutter", "Chatter", "Pluck", "Sky Drop", "Wing Attack" -> ZMove.setPower(120);
                    case "Air Slash" -> ZMove.setPower(140);
                    case "Bounce", "Drill Peck" -> ZMove.setPower(160);
                    case "Fly" -> ZMove.setPower(175);
                    case "Aeroblast", "Beak Blast" -> ZMove.setPower(180);
                    case "Hurricane" -> ZMove.setPower(185);
                    case "Brave Bird", "Dragon Ascent" -> ZMove.setPower(190);
                    case "Sky Attack" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }
            }
            case GHOSTIUM_Z -> {

                ZMove = new Move("Never Ending Nightmare", Type.GHOST, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Astonish", "Lick", "Night Shade", "Shadow Sneak" -> ZMove.setPower(100);
                    case "Omnious Wind", "Shadow Punch" -> ZMove.setPower(120);
                    case "Shadow Claw" -> ZMove.setPower(140);
                    case "Hex", "Shadow Ball", "Shadow Bone", "Spirit Shackle" -> ZMove.setPower(160);
                    case "Phantom Force", "Spectral Thief" -> ZMove.setPower(175);
                    case "Moongeist Beam" -> ZMove.setPower(180);
                    case "Shadow Force" -> ZMove.setPower(190);
                    default -> ZMove = fallback;
                }
            }
            case GRASSIUM_Z -> {

                ZMove = new Move("Bloom Doom", Type.GRASS, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Absorb", "Leafage", "Razor Leaf", "Vine Whip" -> ZMove.setPower(100);
                    case "Leaf Tornado", "Magical Leaf", "Mega Drain", "Needle Arm" -> ZMove.setPower(120);
                    case "Bullet Seed", "Giga Drain", "Horn Leech", "Trop Kick" -> ZMove.setPower(140);
                    case "Grass Knot", "Grass Pledge", "Seed Bomb" -> ZMove.setPower(160);
                    case "Energy Ball", "Leaf Blade", "Petal Blizzard" -> ZMove.setPower(175);
                    case "Petal Dance", "Power Whip", "Seed Flare", "Solar Beam", "Solar Blade", "Wood Hammer" -> ZMove.setPower(190);
                    case "Leaf Storm" -> ZMove.setPower(195);
                    case "Frenzy Plant" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }
            }
            case GROUNDIUM_Z -> {

                ZMove = new Move("Tectonic Rage", Type.GROUND, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Bonemerang", "Mud Shot", "Mud Slap", "Sand Tomb" -> ZMove.setPower(100);
                    case "Bone Club", "Bulldoze", "Mud Bomb" -> ZMove.setPower(120);
                    case "Bone Rush", "Magnitude", "Stomping Tantrum" -> ZMove.setPower(140);
                    case "Dig", "Drill Run" -> ZMove.setPower(160);
                    case "Earth Power", "High Horsepower", "Thousand Waves" -> ZMove.setPower(175);
                    case "Earthquake", "Fissure", "Thousand Arrows" -> ZMove.setPower(180);
                    case "Land's Wrath" -> ZMove.setPower(185);
                    case "Precipice Blades" -> ZMove.setPower(190);
                    default -> ZMove = fallback;
                }

            }
            case ICIUM_Z -> {

                ZMove = new Move("Subzero Slammer", Type.ICE, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Ice Ball", "Ice Shard", "Icy Wind", "Powder Snow" -> ZMove.setPower(100);
                    case "Aurora Beam", "Avalanche", "Frost Breath", "Glaciate", "Ice Fang" -> ZMove.setPower(120);
                    case "Freeze Dry", "Ice Punch", "Icicle Spear" -> ZMove.setPower(140);
                    case "Icicle Crash", "Weather Ball" -> ZMove.setPower(160);
                    case "Ice Beam" -> ZMove.setPower(175);
                    case "Ice Hammer", "Sheer Cold" -> ZMove.setPower(180);
                    case "Blizzard" -> ZMove.setPower(185);
                    case "Freeze Shock", "Ice Burn" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            case NORMALIUM_Z -> {

                ZMove = new Move("Breakneck Blitz", Type.NORMAL, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Barrage", "Bide", "Bind", "Comet Punch", "Constrict", "Cut", "Double Slap", "Echoed Voice", "Fake Out", "False Swipe", "Feint", "Fury Attack", "Fury Swipes", "Hold Back", "Pay Day", "Pound", "Present", "Quick Attack", "Rage", "Rapid Spin", "Scratch", "Snore", "Sonic Boom", "Spike Cannon", "Spit Up", "Super Fang", "Tackle", "Vise Grip", "Wrap" -> ZMove.setPower(100);
                    case "Covet", "Hidden Power", "Horn Attack", "Round", "Stomp", "Swift" -> ZMove.setPower(120);
                    case "Chip Away", "Crush Claw", "Dizzy Punch", "Double Hit", "Facade", "Headbutt", "Relic Song", "Retaliate", "Secret Power", "Slash", "Smelling Salts", "Tail Slap" -> ZMove.setPower(140);
                    case "Body Slam", "Endeavor", "Extreme Speed", "Flail", "Frustration", "Hyper Fang", "Mega Punch", "Natural Gift", "Razor Wind", "Return", "Slam", "Strength", "Tri Attack", "Weather Ball" -> ZMove.setPower(160);
                    case "Hyper Voice", "Revelation Dance", "Rock Climb", "Take Down", "Uproar" -> ZMove.setPower(175);
                    case "Egg Bomb", "Guillotine", "Horn Drill", "Judgement" -> ZMove.setPower(180);
                    case "Multi Attack" -> ZMove.setPower(185);
                    case "Crush Grip", "Double Edge", "Head Charge", "Mega Kick", "Techno Blast", "Thrash", "Wring Out" -> ZMove.setPower(190);
                    case "Skull Bash" -> ZMove.setPower(195);
                    case "Boomburst", "Explosion", "Giga Impact", "Hyper Beam", "Last Resort", "Self Destruct" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            case POISONIUM_Z -> {

                ZMove = new Move("Acid Downpour", Type.POISON, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Acid", "Acid Spray", "Clear Smog", "Poison Fang", "Poison Sting", "Poison Tail", "Smog" -> ZMove.setPower(100);
                    case "Sludge", "Venoshock" -> ZMove.setPower(120);
                    case "Cross Poison" -> ZMove.setPower(140);
                    case "Poison Jab" -> ZMove.setPower(160);
                    case "Sludge Bomb", "Sludge Wave" -> ZMove.setPower(175);
                    case "Belch", "Gunk Shot" -> ZMove.setPower(190);
                    default -> ZMove = fallback;
                }

            }
            case PSYCHIUM_Z -> {

                ZMove = new Move("Shattered Psyche", Type.PSYCHIC, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Confusion", "Mirror Coat", "Psywave" -> ZMove.setPower(100);
                    case "Heart Stamp", "Psybeam" -> ZMove.setPower(120);
                    case "Luster Purge", "Mist Ball", "Psycho Cut" -> ZMove.setPower(140);
                    case "Extrasensory", "Hyperspace Hole", "Psychic Fangs", "Psyshock", "Stored Power", "Zen Headbutt" -> ZMove.setPower(160);
                    case "Psychic", "Freezing Glare" -> ZMove.setPower(175);
                    case "Dream Eater", "Photon Geyser", "Psystrike" -> ZMove.setPower(180);
                    case "Future Sight", "Synchronoise" -> ZMove.setPower(190);
                    case "Prismatic Laser", "Psycho Boost" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            case ROCKIUM_Z -> {

                ZMove = new Move("Continental Crush", Type.ROCK, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Accelerock", "Rock Throw", "Rollout", "Smack Down" -> ZMove.setPower(100);
                    case "Ancient Power", "Rock Tomb" -> ZMove.setPower(120);
                    case "Rock Blast", "Rock Slide" -> ZMove.setPower(140);
                    case "Power Gem", "Weather Ball" -> ZMove.setPower(160);
                    case "Diamond Storm", "Stone Edge" -> ZMove.setPower(180);
                    case "Head Smash", "Rock Wrecker" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            case STEELIUM_Z -> {

                ZMove = new Move("Corkscrew Crash", Type.STEEL, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Bullet Punch", "Metal Burst", "Metal Claw" -> ZMove.setPower(100);
                    case "Magnet Bomb", "Mirror Shot" -> ZMove.setPower(120);
                    case "Smart Strike", "Steel Wing" -> ZMove.setPower(140);
                    case "Anchor Shot", "Flash Cannon", "Gyro Ball", "Heavy Slam", "Iron Head" -> ZMove.setPower(160);
                    case "Meteor Mash" -> ZMove.setPower(175);
                    case "Gear Grind", "Iron Tail", "Sunsteel Strike", "Behemoth Bash", "Behemoth Blade" -> ZMove.setPower(180);
                    case "Doom Desire" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
            case WATERIUM_Z -> {

                ZMove = new Move("Hydro Vortex", Type.WATER, baseMove.getCategory(), 0);

                switch (baseMove.getName())
                {
                    case "Aqua Jet", "Bubble", "Clamp", "Water Gun", "Water Shuriken", "Whirlpool" -> ZMove.setPower(100);
                    case "Brine", "Bubble Beam", "Octazooka", "Water Pulse" -> ZMove.setPower(120);
                    case "Razor Shell" -> ZMove.setPower(140);
                    case "Dive", "Liquidation", "Scald", "Water Pledge", "Waterfall", "Weather Ball" -> ZMove.setPower(160);
                    case "Aqua Tail", "Muddy Water", "Sparkling Aria", "Surf" -> ZMove.setPower(175);
                    case "Crabhammer" -> ZMove.setPower(180);
                    case "Hydro Pump", "Origin Pulse", "Steam Eruption" -> ZMove.setPower(185);
                    case "Hydro Cannon", "Water Spout" -> ZMove.setPower(200);
                    default -> ZMove = fallback;
                }

            }
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
            case ARCEIUM_Z -> ZMove = new Move("Decree Of Arceus", Type.NORMAL, Category.PHYSICAL, 260);
            case RAYQUAZIUM_Z -> ZMove = new Move("Draconic Ozone Ascent", Type.DRAGON, Category.PHYSICAL, 200);
            case ZYGARDIUM_Z -> ZMove = switch (baseMove.getName()) {
                case "Lands Wrath" -> new Move("Tectonic Z Wrath", Type.GROUND, Category.PHYSICAL, 180);
                case "Core Enforcer" -> new Move("Titanic Z Enforcer", Type.DRAGON, Category.SPECIAL, 195);
                case "Thousand Arrows" -> new Move("Million Arrow Barrage", Type.GROUND, Category.PHYSICAL, 190);
                case "Thousand Waves" -> new Move("Million Wave Tsunami", Type.GROUND, Category.PHYSICAL, 190);
                default -> new Move("Tackle");
            };
        }

        return ZMove;
    }
}
