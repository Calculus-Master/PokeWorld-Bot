package com.calculusmaster.pokecord.game.duel.core;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.registry.MaxMoveRegistry;
import com.calculusmaster.pokecord.game.moves.registry.ZMoveRegistry;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.helpers.DataHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if(id.chars().allMatch(Character::isDigit)) return DUELS.stream().filter(d -> d.hasPlayer(id)).toList().get(0);
        else return DUELS.stream().filter(d -> Arrays.stream(d.getPlayers()).anyMatch(p -> p.active.getUUID().equals(id))).toList().get(0);
    }

    public static void delete(String id)
    {
        DUELS.removeIf(d -> Arrays.stream(d.getPlayers()).anyMatch(p -> p.ID.equals(id)));
    }

    //Core Components

    public record TurnAction(ActionType action, int moveInd, int swapInd) {}

    public enum ActionType
    {
        MOVE,
        ZMOVE,
        SWAP,
        DYNAMAX,
        IDLE
    }

    public enum DuelStatus
    {
        WAITING,
        DUELING,
        COMPLETE
    }

    //Z-Moves and Max Moves

    public static Move getZMove(Player p, Move baseMove)
    {
        ZCrystal z = ZCrystal.cast(p.data.getEquippedZCrystal());
        Move fallback = new Move("Tackle");

        if(z == null) return fallback;

        Move ZMove = switch(z)
        {
            //Types
            case BUGINIUM_Z -> new Move("Savage Spin Out");
            case DARKINIUM_Z -> new Move("Black Hole Eclipse");
            case DRAGONIUM_Z -> new Move("Devastating Drake");
            case ELECTRIUM_Z -> new Move("Gigavolt Havoc");
            case FAIRIUM_Z -> new Move("Twinkle Tackle");
            case FIGHTINIUM_Z -> new Move("All Out Pummeling");
            case FIRIUM_Z -> new Move("Inferno Overdrive");
            case FLYINIUM_Z -> new Move("Supersonic Skystrike");
            case GHOSTIUM_Z -> new Move("Never Ending Nightmare");
            case GRASSIUM_Z -> new Move("Bloom Doom");
            case GROUNDIUM_Z -> new Move("Tectonic Rage");
            case ICIUM_Z -> new Move("Subzero Slammer");
            case NORMALIUM_Z -> new Move("Breakneck Blitz");
            case POISONIUM_Z -> new Move("Acid Downpour");
            case PSYCHIUM_Z -> new Move("Shattered Psyche");
            case ROCKIUM_Z -> new Move("Continental Crush");
            case STEELIUM_Z -> new Move("Corkscrew Crash");
            case WATERIUM_Z -> new Move("Hydro Vortex");
            //Uniques
            case ALORAICHIUM_Z -> new Move("Stoked Sparksurfer");
            case DECIDIUM_Z -> new Move("Sinister Arrow Raid");
            case EEVIUM_Z -> new Move("Extreme Evoboost");
            case INCINIUM_Z -> new Move("Malicious Moonsault");
            case KOMMOIUM_Z -> new Move("Clangorous Soulblaze");
            case LUNALIUM_Z -> new Move("Menacing Moonraze Maelstrom");
            case LYCANIUM_Z -> new Move("Splintered Stormshards");
            case MARSHADIUM_Z -> new Move("Soul Stealing 7 Star Strike");
            case MEWNIUM_Z -> new Move("Genesis Supernova");
            case MIMIKIUM_Z -> new Move("Let's Snuggle Forever");
            case PIKANIUM_Z -> new Move("Catastropika");
            case PIKASHUNIUM_Z -> new Move("10,000,000 Volt Thunderbolt");
            case PRIMARIUM_Z -> new Move("Oceanic Operetta");
            case SNORLIUM_Z -> new Move("Pulverizing Pancake");
            case SOLGANIUM_Z -> new Move("Searing Sunraze Smash");
            case TAPUNIUM_Z -> new Move("Guardian of Alola");
            case ULTRANECROZIUM_Z -> switch(baseMove.getName()) {
                case "Photon Geyser" -> new Move("Light That Burns The Sky");
                case "Prismatic Laser" -> new Move("The Blinding One");
                default -> fallback;
            };
            //Custom Uniques
            case RESHIRIUM_Z -> new Move("White Hot Inferno");
            case ZEKRIUM_Z -> new Move("Supercharged Storm Surge");
            case KYURIUM_Z -> switch (baseMove.getName()) {
                case "Glaciate" -> new Move("Eternal Winter");
                case "Freeze Shock" -> new Move("Freezing Storm Surge");
                case "Ice Burn" -> new Move("Blazing Iceferno");
                default -> fallback;
            };
            case XERNIUM_Z -> new Move("Tree Of Life");
            case YVELTIUM_Z -> new Move("Cocoon Of Destruction");
            case DIANCIUM_Z -> new Move("Dazzling Diamond Barrage");
            case ARCEIUM_Z -> new Move("Decree Of Arceus");
            case RAYQUAZIUM_Z -> new Move("Draconic Ozone Ascent");
            case ZYGARDIUM_Z -> switch (baseMove.getName()) {
                case "Lands Wrath" -> new Move("Tectonic Z Wrath");
                case "Core Enforcer" -> new Move("Titanic Z Enforcer");
                case "Thousand Arrows" -> new Move("Million Arrow Barrage");
                case "Thousand Waves" -> new Move("Million Wave Tsunami");
                default -> fallback;
            };
            case VOLCANIUM_Z -> new Move("Volcanic Steam Geyser");
            case KYOGRIUM_Z -> new Move("Primordial Tsunami");
            case GROUDONIUM_Z -> new Move("Primordial Landslide");
            case GENESECTIUM_Z -> new Move("Elemental Techno Overdrive");
            case MELMETALIUM_Z -> switch (baseMove.getName()) {
                case "Double Iron Bash" -> new Move("Quadruple Steel Smash");
                case "Acid Armor" -> new Move("Metal Liquidation");
                default -> fallback;
            };
            case DIALGIUM_Z -> new Move("Timeline Shatter");
            case PALKIUM_Z -> new Move("Ultra Space Hypernova");
            case GIRATINIUM_Z -> new Move("Dark Matter Explosion");
            case ETERNIUM_Z -> switch (baseMove.getName()) {
                case "Eternabeam" -> new Move("The Darkest Day");
                case "Dynamax Cannon" -> new Move("Max Particle Beam");
                default -> fallback;
            };
            case DARKRAIUM_Z -> new Move("Nightmare Void");
        };

        if(ZMoveRegistry.TYPED_ZMOVES.contains(ZMove.getName()))
        {
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

            ZMove.setPower(ZPower);
            ZMove.setCategory(baseMove.getCategory());
        }

        return ZMove;
    }

    public static Move getMaxMove(Pokemon p, Move baseMove)
    {
        Move maxMove;

        if(baseMove.getCategory().equals(Category.STATUS)) maxMove = new Move("Max Guard");
        else if(p.canGigantamax() && DataHelper.getGigantamaxData(p.getName()).moveType().equals(baseMove.getType())) maxMove = new Move(DataHelper.getGigantamaxData(p.getName()).move(), DataHelper.getGigantamaxData(p.getName()).moveType(), null, 0);
        else maxMove = new Move(MaxMoveRegistry.get(baseMove.getType()).name);

        int maxPower;

        boolean isDecreased = Arrays.asList("Max Knuckle", "Max Ooze").contains(maxMove.getName());

        if(baseMove.getCategory().equals(Category.STATUS)) maxPower = 0;
        else if(baseMove.getPower() <= 40) maxPower = isDecreased ? 70 : 90;
        else if(baseMove.getPower() <= 50) maxPower = isDecreased ? 75 : 100;
        else if(baseMove.getPower() <= 60) maxPower = isDecreased ? 80 : 110;
        else if(baseMove.getPower() <= 70) maxPower = isDecreased ? 85 : 120;
        else if(baseMove.getPower() <= 100) maxPower = isDecreased ? 90 : 130;
        else if(baseMove.getPower() <= 140) maxPower = isDecreased ? 95 : 140;
        else maxPower = isDecreased ? 100 : 150;

        maxMove.setPower(maxPower);
        maxMove.setCategory(baseMove.getCategory());

        return maxMove;
    }
}
