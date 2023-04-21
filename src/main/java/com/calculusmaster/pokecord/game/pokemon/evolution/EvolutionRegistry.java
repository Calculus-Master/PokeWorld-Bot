package com.calculusmaster.pokecord.game.pokemon.evolution;

import com.calculusmaster.pokecord.game.enums.elements.Gender;
import com.calculusmaster.pokecord.game.enums.elements.Region;
import com.calculusmaster.pokecord.game.enums.elements.Time;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.triggers.*;
import com.calculusmaster.pokecord.mongo.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity.*;

public class EvolutionRegistry
{
    private static final Map<PokemonEntity, List<EvolutionData>> EVOLUTION_DATA = new HashMap<>();

    public static void checkAutomaticEvolution(Pokemon p, PlayerData playerData, String serverID)
    {
        if(playerData != null & !serverID.isEmpty() && !p.hasItem(Item.EVERSTONE) && EvolutionRegistry.hasEvolutionData(p.getEntity()))
        {
            List<EvolutionData> dataList = EvolutionRegistry.getEvolutionData(p.getEntity());
            for(EvolutionData data : dataList)
                if(data.validate(p, serverID))
                {
                    String original = p.hasNickname() ? p.getDisplayName() + " (" + p.getName() + ")" : p.getName();

                    p.evolve(data, playerData);

                    playerData.directMessage("Your " + original + " evolved into a **" + p.getName() + "**!");
                    break;
                }
        }
    }

    //TODO: Go through each Evolution trigger and assign checks in the respective parts of the bot
    //like level up trigger is checked during level up, etc
    public static void init()
    {
        EvolutionRegistry.register(BULBASAUR, IVYSAUR, VENUSAUR, 16, 32);
        EvolutionRegistry.register(CHARMANDER, CHARMELEON, CHARIZARD, 16, 32);
        EvolutionRegistry.register(SQUIRTLE, WARTORTLE, BLASTOISE, 16, 32);
        EvolutionRegistry.register(CATERPIE, METAPOD, BUTTERFREE, 7, 10);
        EvolutionRegistry.register(WEEDLE, KAKUNA, BEEDRILL, 7, 10);
        EvolutionRegistry.register(PIDGEY, PIDGEOTTO, PIDGEOT, 18, 36);

        EvolutionRegistry.register(RATTATA, RATICATE, 20);
        EvolutionRegistry.register(RATTATA_ALOLA, RATICATE_ALOLA, new LevelEvoTrigger(20), new TimeEvoTrigger(Time.NIGHT));

        EvolutionRegistry.register(SPEAROW, FEAROW, 20);
        EvolutionRegistry.register(EKANS, ARBOK, 22);

        EvolutionRegistry.register(PICHU, PIKACHU, new HighFriendshipEvoTrigger());
        EvolutionRegistry.register(PIKACHU, RAICHU, new ItemEvoTrigger(Item.THUNDER_STONE), new RegionEvoTrigger(Region.ALOLA, true));
        EvolutionRegistry.register(PIKACHU, RAICHU_ALOLA, new ItemEvoTrigger(Item.THUNDER_STONE), new RegionEvoTrigger(Region.ALOLA, false));

        EvolutionRegistry.register(SANDSHREW, SANDSLASH, 22);
        EvolutionRegistry.register(SANDSHREW_ALOLA, SANDSLASH_ALOLA, Item.ICE_STONE);

        EvolutionRegistry.register(NIDORAN_F, NIDORINA, 16);
        EvolutionRegistry.register(NIDORINA, NIDOQUEEN, Item.MOON_STONE);
        EvolutionRegistry.register(NIDORAN_M, NIDORINO, 16);
        EvolutionRegistry.register(NIDORINO, NIDOKING, Item.MOON_STONE);

        EvolutionRegistry.register(CLEFFA, CLEFAIRY, new HighFriendshipEvoTrigger());
        EvolutionRegistry.register(CLEFAIRY, CLEFABLE, 16);

        EvolutionRegistry.register(VULPIX, NINETALES, Item.FIRE_STONE);
        EvolutionRegistry.register(VULPIX_ALOLA, NINETALES_ALOLA, Item.ICE_STONE);

        EvolutionRegistry.register(IGGLYBUFF, JIGGLYPUFF, new HighFriendshipEvoTrigger());
        EvolutionRegistry.register(JIGGLYPUFF, WIGGLYTUFF, Item.MOON_STONE);

        EvolutionRegistry.register(ZUBAT, GOLBAT, 22);
        EvolutionRegistry.register(GOLBAT, CROBAT, new HighFriendshipEvoTrigger());

        EvolutionRegistry.register(ODDISH, GLOOM, 21);
        EvolutionRegistry.register(GLOOM, VILEPLUME, Item.LEAF_STONE);
        EvolutionRegistry.register(GLOOM, BELLOSSOM, Item.SUN_STONE);

        EvolutionRegistry.register(PARAS, PARASECT, 24);
        EvolutionRegistry.register(VENONAT, VENOMOTH, 31);

        EvolutionRegistry.register(DIGLETT, DUGTRIO, 26);
        EvolutionRegistry.register(DIGLETT_ALOLA, DUGTRIO_ALOLA, 26);

        EvolutionRegistry.register(MEOWTH, PERSIAN, 28);
        EvolutionRegistry.register(MEOWTH_ALOLA, PERSIAN_ALOLA, new HighFriendshipEvoTrigger());
        EvolutionRegistry.register(MEOWTH_GALAR, PERRSERKER, 28);

        EvolutionRegistry.register(PSYDUCK, GOLDUCK, 33);

        EvolutionRegistry.register(MANKEY, PRIMEAPE, 28);
        EvolutionRegistry.register(PRIMEAPE, ANNIHILAPE, new WIPEvoTrigger()); //TODO: Primeape -> Annhilape (Use Rage Fist 20 Times)

        EvolutionRegistry.register(GROWLITHE, ARCANINE, Item.FIRE_STONE);
        EvolutionRegistry.register(GROWLITHE_HISUI, ARCANINE_HISUI, Item.FIRE_STONE);

        EvolutionRegistry.register(POLIWAG, POLIWHIRL, 25);
        EvolutionRegistry.register(POLIWHIRL, POLIWRATH, Item.WATER_STONE);
        EvolutionRegistry.register(POLIWHIRL, POLITOED, new TradeEvoTrigger(), new ItemEvoTrigger(Item.KINGS_ROCK));

        EvolutionRegistry.register(ABRA, KADABRA, 16);
        EvolutionRegistry.register(KADABRA, ALAKAZAM, new TradeEvoTrigger());

        EvolutionRegistry.register(MACHOP, MACHOKE, 28);
        EvolutionRegistry.register(MACHOKE, MACHAMP, new TradeEvoTrigger());

        EvolutionRegistry.register(BELLSPROUT, WEEPINBELL, 21);
        EvolutionRegistry.register(WEEPINBELL, VICTREEBEL, Item.LEAF_STONE);

        EvolutionRegistry.register(TENTACOOL, TENTACRUEL, 30);

        EvolutionRegistry.register(GEODUDE, GRAVELER, 25);
        EvolutionRegistry.register(GRAVELER, GOLEM, new TradeEvoTrigger());
        EvolutionRegistry.register(GEODUDE_ALOLA, GRAVELER_ALOLA, 25);
        EvolutionRegistry.register(GRAVELER_ALOLA, GOLEM_ALOLA, new TradeEvoTrigger());

        EvolutionRegistry.register(PONYTA, RAPIDASH, 40);
        EvolutionRegistry.register(PONYTA_GALAR, RAPIDASH_GALAR, 40);

        EvolutionRegistry.register(SLOWPOKE, SLOWBRO, 37);
        EvolutionRegistry.register(SLOWPOKE, SLOWKING, new TradeEvoTrigger(), new ItemEvoTrigger(Item.KINGS_ROCK));
        EvolutionRegistry.register(SLOWPOKE_GALAR, SLOWBRO_GALAR, Item.GALARICA_CUFF);
        EvolutionRegistry.register(SLOWPOKE_GALAR, SLOWKING_GALAR, Item.GALARICA_WREATH);

        EvolutionRegistry.register(MAGNEMITE, MAGNETON, 30);
        EvolutionRegistry.register(MAGNETON, MAGNEZONE, Item.THUNDER_STONE);

        EvolutionRegistry.register(DODUO, DODRIO, 31);
        EvolutionRegistry.register(SEEL, DEWGONG, 34);

        EvolutionRegistry.register(GRIMER, MUK, 38);
        EvolutionRegistry.register(GRIMER_ALOLA, MUK_ALOLA, 38);

        EvolutionRegistry.register(SHELLDER, CLOYSTER, Item.WATER_STONE);

        EvolutionRegistry.register(GASTLY, HAUNTER, 25);
        EvolutionRegistry.register(HAUNTER, GENGAR, new TradeEvoTrigger());

        EvolutionRegistry.register(ONIX, STEELIX, new TradeEvoTrigger(), new ItemEvoTrigger(Item.METAL_COAT));
        EvolutionRegistry.register(DROWZEE, HYPNO, 26);
        EvolutionRegistry.register(KRABBY, KINGLER, 28);

        EvolutionRegistry.register(VOLTORB, ELECTRODE, 30);
        EvolutionRegistry.register(VOLTORB_HISUI, ELECTRODE_HISUI, Item.LEAF_STONE);

        EvolutionRegistry.register(EXEGGCUTE, EXEGGUTOR, new ItemEvoTrigger(Item.LEAF_STONE), new RegionEvoTrigger(Region.ALOLA, true));
        EvolutionRegistry.register(EXEGGCUTE, EXEGGUTOR_ALOLA, new ItemEvoTrigger(Item.LEAF_STONE), new RegionEvoTrigger(Region.ALOLA, false));

        EvolutionRegistry.register(CUBONE, MAROWAK, new LevelEvoTrigger(28), new RegionEvoTrigger(Region.ALOLA, true));
        EvolutionRegistry.register(CUBONE, MAROWAK_ALOLA, new LevelEvoTrigger(28), new TimeEvoTrigger(Time.NIGHT), new RegionEvoTrigger(Region.ALOLA, false));

        EvolutionRegistry.register(TYROGUE, HITMONLEE, new WIPEvoTrigger());
        EvolutionRegistry.register(TYROGUE, HITMONCHAN, new WIPEvoTrigger());
        EvolutionRegistry.register(TYROGUE, HITMONTOP, new WIPEvoTrigger());

        EvolutionRegistry.register(LICKITUNG, LICKILICKY, new MoveLearnedEvoTrigger(MoveEntity.ROLLOUT));

        EvolutionRegistry.register(KOFFING, WEEZING, new LevelEvoTrigger(35), new RegionEvoTrigger(Region.GALAR, true));
        EvolutionRegistry.register(KOFFING, WEEZING_GALAR, new LevelEvoTrigger(35), new RegionEvoTrigger(Region.GALAR, false));

        EvolutionRegistry.register(RHYHORN, RHYDON, 42);
        EvolutionRegistry.register(RHYDON, RHYPERIOR, new TradeEvoTrigger(), new ItemEvoTrigger(Item.PROTECTOR));

        EvolutionRegistry.register(HAPPINY, CHANSEY, new ItemEvoTrigger(Item.OVAL_STONE), new TimeEvoTrigger(Time.DAY));
        EvolutionRegistry.register(CHANSEY, BLISSEY, new HighFriendshipEvoTrigger());

        EvolutionRegistry.register(TANGELA, TANGROWTH, new MoveLearnedEvoTrigger(MoveEntity.ANCIENT_POWER));

        EvolutionRegistry.register(HORSEA, SEADRA, 32);
        EvolutionRegistry.register(SEADRA, KINGDRA, new TradeEvoTrigger(), new ItemEvoTrigger(Item.DRAGON_SCALE));

        EvolutionRegistry.register(GOLDEEN, SEAKING, 33);
        EvolutionRegistry.register(STARYU, STARMIE, Item.WATER_STONE);

        EvolutionRegistry.register(MIME_JR, MR_MIME, new MoveLearnedEvoTrigger(MoveEntity.MIMIC), new RegionEvoTrigger(Region.GALAR, true));
        EvolutionRegistry.register(MIME_JR, MR_MIME_GALAR, new MoveLearnedEvoTrigger(MoveEntity.MIMIC), new RegionEvoTrigger(Region.GALAR, false));
        EvolutionRegistry.register(MR_MIME_GALAR, MR_RIME, 42);

        EvolutionRegistry.register(SCYTHER, SCIZOR, new TradeEvoTrigger(), new ItemEvoTrigger(Item.METAL_COAT));
        EvolutionRegistry.register(SCYTHER, KLEAVOR, Item.BLACK_AUGURITE);

        EvolutionRegistry.register(SMOOCHUM, JYNX, 30);

        EvolutionRegistry.register(ELEKID, ELECTABUZZ, 30);
        EvolutionRegistry.register(ELECTABUZZ, ELECTIVIRE, new TradeEvoTrigger(), new ItemEvoTrigger(Item.ELECTIRIZER));

        EvolutionRegistry.register(MAGBY, MAGMAR, 30);
        EvolutionRegistry.register(MAGMAR, MAGMORTAR, new TradeEvoTrigger(), new ItemEvoTrigger(Item.MAGMARIZER));

        EvolutionRegistry.register(MAGIKARP, GYARADOS, 20);

        EvolutionRegistry.register(EEVEE, VAPOREON, Item.WATER_STONE);
        EvolutionRegistry.register(EEVEE, JOLTEON, Item.THUNDER_STONE);
        EvolutionRegistry.register(EEVEE, FLAREON, Item.FIRE_STONE);
        EvolutionRegistry.register(EEVEE, ESPEON, new HighFriendshipEvoTrigger(), new TimeEvoTrigger(Time.DAY));
        EvolutionRegistry.register(EEVEE, UMBREON, new HighFriendshipEvoTrigger(), new TimeEvoTrigger(Time.NIGHT));
        EvolutionRegistry.register(EEVEE, LEAFEON, new ItemEvoTrigger(Item.LEAF_STONE));
        EvolutionRegistry.register(EEVEE, GLACEON, new ItemEvoTrigger(Item.ICE_STONE));
        EvolutionRegistry.register(EEVEE, SYLVEON, new MoveTypeLearnedEvoTrigger(Type.FAIRY), new HighFriendshipEvoTrigger());

        EvolutionRegistry.register(PORYGON, PORYGON2, new TradeEvoTrigger(), new ItemEvoTrigger(Item.UPGRADE));
        EvolutionRegistry.register(PORYGON2, PORYGON_Z, new TradeEvoTrigger(), new ItemEvoTrigger(Item.DUBIOUS_DISC));

        EvolutionRegistry.register(OMANYTE, OMASTAR, 40);
        EvolutionRegistry.register(KABUTO, KABUTOPS, 40);
        EvolutionRegistry.register(MUNCHLAX, SNORLAX, new HighFriendshipEvoTrigger());
        EvolutionRegistry.register(DRATINI, DRAGONAIR, DRAGONITE, 30, 55);
        EvolutionRegistry.register(CHIKORITA, BAYLEEF, MEGANIUM, 16, 32);

        EvolutionRegistry.register(CYNDAQUIL, QUILAVA, 14);
        EvolutionRegistry.register(QUILAVA, TYPHLOSION, new LevelEvoTrigger(36), new RegionEvoTrigger(Region.HISUI, true));
        EvolutionRegistry.register(QUILAVA, TYPHLOSION_HISUI, new LevelEvoTrigger(36), new RegionEvoTrigger(Region.HISUI, false));

        EvolutionRegistry.register(TOTODILE, CROCONAW, FERALIGATR, 18, 30);
        EvolutionRegistry.register(SENTRET, FURRET, 15);
        EvolutionRegistry.register(HOOTHOOT, NOCTOWL, 20);
        EvolutionRegistry.register(LEDYBA, LEDIAN, 18);
        EvolutionRegistry.register(SPINARAK, ARIADOS, 22);
        EvolutionRegistry.register(CHINCHOU, LANTURN, 27);

        EvolutionRegistry.register(TOGEPI, TOGETIC, new HighFriendshipEvoTrigger());
        EvolutionRegistry.register(TOGETIC, TOGEKISS, Item.SHINY_STONE);

        EvolutionRegistry.register(NATU, XATU, 25);
        EvolutionRegistry.register(MAREEP, FLAAFFY, AMPHAROS, 15, 30);

        EvolutionRegistry.register(AZURILL, MARILL, new HighFriendshipEvoTrigger());
        EvolutionRegistry.register(MARILL, AZUMARILL, 18);

        EvolutionRegistry.register(BONSLY, SUDOWOODO, new MoveLearnedEvoTrigger(MoveEntity.MIMIC));
        EvolutionRegistry.register(HOPPIP, SKIPLOOM, JUMPLUFF, 18, 27);
        EvolutionRegistry.register(AIPOM, AMBIPOM, new MoveLearnedEvoTrigger(MoveEntity.DOUBLE_HIT));
        EvolutionRegistry.register(SUNKERN, SUNFLORA, Item.SUN_STONE);
        EvolutionRegistry.register(YANMA, YANMEGA, new MoveLearnedEvoTrigger(MoveEntity.ANCIENT_POWER));

        EvolutionRegistry.register(WOOPER, QUAGSIRE, 20);
        EvolutionRegistry.register(WOOPER_PALDEA, CLODSIRE, 20);

        EvolutionRegistry.register(MURKROW, HONCHKROW, Item.DUSK_STONE);
        EvolutionRegistry.register(MISDREAVUS, MISMAGIUS, Item.DUSK_STONE);
        EvolutionRegistry.register(WYNAUT, WOBBUFFET, 15);
        EvolutionRegistry.register(PINECO, FORRETRESS, 31);
        EvolutionRegistry.register(GLIGAR, GLISCOR, new ItemEvoTrigger(Item.RAZOR_FANG), new TimeEvoTrigger(Time.NIGHT));
        EvolutionRegistry.register(SNUBBULL, GRANBULL, 23);

        EvolutionRegistry.register(SNEASEL, WEAVILE, new ItemEvoTrigger(Item.RAZOR_CLAW), new TimeEvoTrigger(Time.NIGHT));
        EvolutionRegistry.register(SNEASEL_HISUI, SNEASLER, new ItemEvoTrigger(Item.RAZOR_CLAW), new TimeEvoTrigger(Time.DAY));

        EvolutionRegistry.register(TEDDIURSA, URSARING, 30);
        EvolutionRegistry.register(URSARING, URSALUNA, new WIPEvoTrigger()); //TODO: Ursaring -> Ursaluna (Peat Block, Under Full Moon)

        EvolutionRegistry.register(SLUGMA, MAGCARGO, 38);

        EvolutionRegistry.register(SWINUB, PILOSWINE, 33);
        EvolutionRegistry.register(PILOSWINE, MAMOSWINE, new MoveLearnedEvoTrigger(MoveEntity.ANCIENT_POWER));

        EvolutionRegistry.register(REMORAID, OCTILLERY, 25);
        EvolutionRegistry.register(MANTYKE, MANTINE, new WIPEvoTrigger()); //TODO: Mantyke -> Mantine (Remoraid in party)
        EvolutionRegistry.register(HOUNDOUR, HOUNDOOM, 24);
        EvolutionRegistry.register(PHANPY, DONPHAN, 25);
        EvolutionRegistry.register(LARVITAR, PUPITAR, TYRANITAR, 30, 55);
        EvolutionRegistry.register(STANTLER, WYRDEER, new WIPEvoTrigger()); //TODO: Stantler -> Wyrdeer (Use Psyshield Bash 20 times Agile Style)
        EvolutionRegistry.register(GIRAFARIG, FARIGIRAF, new MoveLearnedEvoTrigger(MoveEntity.TWIN_BEAM));
        EvolutionRegistry.register(DUNSPARCE, DUDUNSPARCE, new MoveLearnedEvoTrigger(MoveEntity.HYPER_DRILL));
        EvolutionRegistry.register(TREECKO, GROVYLE, SCEPTILE, 16, 36);
        EvolutionRegistry.register(TORCHIC, COMBUSKEN, BLAZIKEN, 16, 36);
        EvolutionRegistry.register(MUDKIP, MARSHTOMP, SWAMPERT, 16, 36);
        EvolutionRegistry.register(POOCHYENA, MIGHTYENA, 18);

        EvolutionRegistry.register(ZIGZAGOON, LINOONE, 20);
        EvolutionRegistry.register(ZIGZAGOON_GALAR, LINOONE_GALAR, 20);
        EvolutionRegistry.register(LINOONE_GALAR, OBSTAGOON, new LevelEvoTrigger(35), new TimeEvoTrigger(Time.NIGHT));

        EvolutionRegistry.register(WURMPLE, SILCOON, 7); //TODO: Wurmple -> Silcoon or Cascoon (Random based on personality)
        EvolutionRegistry.register(SILCOON, BEAUTIFLY, 10);
        EvolutionRegistry.register(WURMPLE, CASCOON, 7);
        EvolutionRegistry.register(CASCOON, DUSTOX, 10);

        EvolutionRegistry.register(LOTAD, LOMBRE, 14);
        EvolutionRegistry.register(LOMBRE, LUDICOLO, Item.WATER_STONE);

        EvolutionRegistry.register(SEEDOT, NUZLEAF, 14);
        EvolutionRegistry.register(NUZLEAF, SHIFTRY, Item.LEAF_STONE);

        EvolutionRegistry.register(TAILLOW, SWELLOW, 22);
        EvolutionRegistry.register(WINGULL, PELIPPER, 25);

        EvolutionRegistry.register(RALTS, KIRLIA, 20);
        EvolutionRegistry.register(KIRLIA, GARDEVOIR, 30);
        EvolutionRegistry.register(KIRLIA, GALLADE, new ItemEvoTrigger(Item.DAWN_STONE), new GenderEvoTrigger(Gender.MALE));

        EvolutionRegistry.register(SURSKIT, MASQUERAIN, 22);
        EvolutionRegistry.register(SHROOMISH, BRELOOM, 23);
        EvolutionRegistry.register(SLAKOTH, VIGOROTH, SLAKING, 18, 36);
        EvolutionRegistry.register(NINCADA, NINJASK, 20); //TODO: Nincada -> Ninjask produces an extra Shedinja
        EvolutionRegistry.register(WHISMUR, LOUDRED, EXPLOUD, 20, 40);
        EvolutionRegistry.register(MAKUHITA, HARIYAMA, 24);
        EvolutionRegistry.register(NOSEPASS, PROBOPASS, Item.THUNDER_STONE);
        EvolutionRegistry.register(SKITTY, DELCATTY, Item.MOON_STONE);
        EvolutionRegistry.register(ARON, LAIRON, AGGRON, 32, 42);
        EvolutionRegistry.register(MEDITITE, MEDICHAM, 37);
        EvolutionRegistry.register(ELECTRIKE, MANECTRIC, 26);

        EvolutionRegistry.register(BUDEW, ROSELIA, new HighFriendshipEvoTrigger(), new TimeEvoTrigger(Time.DAY));
        EvolutionRegistry.register(ROSELIA, ROSERADE, Item.SHINY_STONE);

        EvolutionRegistry.register(GULPIN, SWALOT, 26);
        EvolutionRegistry.register(CARVANHA, SHARPEDO, 30);
        EvolutionRegistry.register(WAILMER, WAILORD, 40);
        EvolutionRegistry.register(NUMEL, CAMERUPT, 33);
        EvolutionRegistry.register(SPOINK, GRUMPIG, 32);
        EvolutionRegistry.register(TRAPINCH, VIBRAVA, FLYGON, 35, 45);
        EvolutionRegistry.register(CACNEA, CACTURNE, 32);
        EvolutionRegistry.register(SWABLU, ALTARIA, 35);
        EvolutionRegistry.register(BARBOACH, WHISCASH, 30);
        EvolutionRegistry.register(CORPHISH, CRAWDAUNT, 30);
        EvolutionRegistry.register(BALTOY, CLAYDOL, 36);
        EvolutionRegistry.register(LILEEP, CRADILY, 40);
        EvolutionRegistry.register(ANORITH, ARMALDO, 40);
        EvolutionRegistry.register(FEEBAS, MILOTIC, new TradeEvoTrigger(), new ItemEvoTrigger(Item.PRISM_SCALE));
        EvolutionRegistry.register(SHUPPET, BANETTE, 37);

        EvolutionRegistry.register(DUSKULL, DUSCLOPS, 37);
        EvolutionRegistry.register(DUSCLOPS, DUSKNOIR, new TradeEvoTrigger(), new ItemEvoTrigger(Item.REAPER_CLOTH));

        EvolutionRegistry.register(CHINGLING, CHIMECHO, new HighFriendshipEvoTrigger(), new TimeEvoTrigger(Time.NIGHT));

        EvolutionRegistry.register(SNORUNT, GLALIE, 42);
        EvolutionRegistry.register(SNORUNT, FROSLASS, new ItemEvoTrigger(Item.DAWN_STONE), new GenderEvoTrigger(Gender.FEMALE));

        EvolutionRegistry.register(SPHEAL, SEALEO, WALREIN, 32, 44);

        EvolutionRegistry.register(CLAMPERL, HUNTAIL, new TradeEvoTrigger(), new ItemEvoTrigger(Item.DEEP_SEA_TOOTH));
        EvolutionRegistry.register(CLAMPERL, GOREBYSS, new TradeEvoTrigger(), new ItemEvoTrigger(Item.DEEP_SEA_SCALE));

        EvolutionRegistry.register(BAGON, SHELGON, SALAMENCE, 30, 50);
        EvolutionRegistry.register(BELDUM, METANG, METAGROSS, 20, 45);
        EvolutionRegistry.register(TURTWIG, GROTLE, TORTERRA, 18, 32);
        EvolutionRegistry.register(CHIMCHAR, MONFERNO, INFERNAPE, 14, 36);
        EvolutionRegistry.register(PIPLUP, PRINPLUP, EMPOLEON, 16, 36);
        EvolutionRegistry.register(STARLY, STARAVIA, STARAPTOR, 14, 34);
        EvolutionRegistry.register(BIDOOF, BIBAREL, 15);
        EvolutionRegistry.register(KRICKETOT, KRICKETUNE, 10);
        EvolutionRegistry.register(SHINX, LUXIO, LUXRAY, 15, 30);
        EvolutionRegistry.register(CRANIDOS, RAMPARDOS, 30);
        EvolutionRegistry.register(SHIELDON, BASTIODON, 30);

        EvolutionRegistry.register(BURMY_PLANT, MOTHIM, new LevelEvoTrigger(20), new GenderEvoTrigger(Gender.MALE));
        EvolutionRegistry.register(BURMY_SANDY, MOTHIM, new LevelEvoTrigger(20), new GenderEvoTrigger(Gender.MALE));
        EvolutionRegistry.register(BURMY_TRASH, MOTHIM, new LevelEvoTrigger(20), new GenderEvoTrigger(Gender.MALE));
        EvolutionRegistry.register(BURMY_PLANT, WORMADAM_PLANT, new LevelEvoTrigger(20), new GenderEvoTrigger(Gender.FEMALE));
        EvolutionRegistry.register(BURMY_SANDY, WORMADAM_SANDY, new LevelEvoTrigger(20), new GenderEvoTrigger(Gender.FEMALE));
        EvolutionRegistry.register(BURMY_TRASH, WORMADAM_TRASH, new LevelEvoTrigger(20), new GenderEvoTrigger(Gender.FEMALE));

        EvolutionRegistry.register(COMBEE, VESPIQUEN, new LevelEvoTrigger(21), new GenderEvoTrigger(Gender.FEMALE));
        EvolutionRegistry.register(BUIZEL, FLOATZEL, 26);
        EvolutionRegistry.register(CHERUBI, CHERRIM, 25);
        EvolutionRegistry.register(SHELLOS, GASTRODON, 30);
        EvolutionRegistry.register(DRIFLOON, DRIFBLIM, 28);
        EvolutionRegistry.register(BUNEARY, LOPUNNY, new HighFriendshipEvoTrigger());
        EvolutionRegistry.register(GLAMEOW, PURUGLY, 38);
        EvolutionRegistry.register(STUNKY, SKUNTANK, 34);
        EvolutionRegistry.register(BRONZOR, BRONZONG, 33);
        EvolutionRegistry.register(GIBLE, GABITE, GARCHOMP, 24, 48);
        EvolutionRegistry.register(RIOLU, LUCARIO, new HighFriendshipEvoTrigger(), new TimeEvoTrigger(Time.DAY));
        EvolutionRegistry.register(HIPPOPOTAS, HIPPOWDON, 34);
        EvolutionRegistry.register(SKORUPI, DRAPION, 40);
        EvolutionRegistry.register(CROAGUNK, TOXICROAK, 37);
        EvolutionRegistry.register(FINNEON, LUMINEON, 31);
        EvolutionRegistry.register(SNOVER, ABOMASNOW, 40);
        EvolutionRegistry.register(SNIVY, SERVINE, SERPERIOR, 17, 36);
        EvolutionRegistry.register(TEPIG, PIGNITE, EMBOAR, 17, 36);

        EvolutionRegistry.register(OSHAWOTT, DEWOTT, 17);
        EvolutionRegistry.register(DEWOTT, SAMUROTT, new LevelEvoTrigger(36), new RegionEvoTrigger(Region.HISUI, true));
        EvolutionRegistry.register(DEWOTT, SAMUROTT_HISUI, new LevelEvoTrigger(36), new RegionEvoTrigger(Region.HISUI, false));

        EvolutionRegistry.register(PATRAT, WATCHOG, 20);
        EvolutionRegistry.register(LILLIPUP, HERDIER, STOUTLAND, 16, 32);
        EvolutionRegistry.register(PURRLOIN, LIEPARD, 20);
        EvolutionRegistry.register(PANSAGE, SIMISAGE, Item.LEAF_STONE);
        EvolutionRegistry.register(PANSEAR, SIMISEAR, Item.FIRE_STONE);
        EvolutionRegistry.register(PANPOUR, SIMIPOUR, Item.WATER_STONE);
        EvolutionRegistry.register(MUNNA, MUSHARNA, Item.MOON_STONE);
        EvolutionRegistry.register(PIDOVE, TRANQUILL, UNFEZANT, 21, 32);
        EvolutionRegistry.register(BLITZLE, ZEBSTRIKA, 27);

        EvolutionRegistry.register(ROGGENROLA, BOLDORE, 25);
        EvolutionRegistry.register(BOLDORE, GIGALITH, new TradeEvoTrigger());

        EvolutionRegistry.register(WOOBAT, SWOOBAT, new HighFriendshipEvoTrigger());
        EvolutionRegistry.register(DRILBUR, EXCADRILL, 31);

        EvolutionRegistry.register(TIMBURR, GURDURR, 25);
        EvolutionRegistry.register(GURDURR, CONKELDURR, new TradeEvoTrigger());

        EvolutionRegistry.register(TYMPOLE, PALPITOAD, SEISMITOAD, 25, 36);

        EvolutionRegistry.register(SEWADDLE, SWADLOON, 20);
        EvolutionRegistry.register(SWADLOON, LEAVANNY, new HighFriendshipEvoTrigger());

        EvolutionRegistry.register(VENIPEDE, WHIRLIPEDE, SCOLIPEDE, 22, 30);
        EvolutionRegistry.register(COTTONEE, WHIMSICOTT, Item.SUN_STONE);

        EvolutionRegistry.register(PETILIL, LILLIGANT, new ItemEvoTrigger(Item.SUN_STONE), new RegionEvoTrigger(Region.HISUI, true));
        EvolutionRegistry.register(PETILIL, LILLIGANT_HISUI, new ItemEvoTrigger(Item.SUN_STONE), new RegionEvoTrigger(Region.HISUI, false));

        EvolutionRegistry.register(SANDILE, KROKOROK, KROOKODILE, 29, 40);

        EvolutionRegistry.register(DARUMAKA, DARMANITAN, 35);
        EvolutionRegistry.register(DARUMAKA_GALAR, DARMANITAN_GALAR, Item.ICE_STONE);

        EvolutionRegistry.register(DWEBBLE, CRUSTLE, 34);
        EvolutionRegistry.register(SCRAGGY, SCRAFTY, 39);

        EvolutionRegistry.register(YAMASK, COFAGRIGUS, 34);
        EvolutionRegistry.register(YAMASK_GALAR, RUNERIGUS, new WIPEvoTrigger()); //TODO: Galarian Yamask -> Runerigus (Near Dusty Bowl)

        EvolutionRegistry.register(TIRTOUGA, CARRACOSTA, 37);
        EvolutionRegistry.register(ARCHEN, ARCHEOPS, 37);
        EvolutionRegistry.register(TRUBBISH, GARBODOR, 36);

        EvolutionRegistry.register(ZORUA, ZOROARK, 30);
        EvolutionRegistry.register(ZORUA_HISUI, ZOROARK_HISUI, 30);

        EvolutionRegistry.register(MINCCINO, CINCCINO, Item.SHINY_STONE);
        EvolutionRegistry.register(GOTHITA, GOTHORITA, GOTHITELLE, 32, 41);
        EvolutionRegistry.register(SOLOSIS, DUOSION, REUNICLUS, 32, 41);
        EvolutionRegistry.register(DUCKLETT, SWANNA, 35);
        EvolutionRegistry.register(VANILLITE, VANILLISH, VANILLUXE, 35, 47);
        EvolutionRegistry.register(DEERLING, SAWSBUCK, 34);

        EvolutionRegistry.register(KARRABLAST, ESCAVALIER, new TradeWithEvoTrigger(SHELMET));
        EvolutionRegistry.register(FOONGUS, AMOONGUSS, 39);
        EvolutionRegistry.register(FRILLISH, JELLICENT, 40);
        EvolutionRegistry.register(JOLTIK, GALVANTULA, 36);
        EvolutionRegistry.register(FERROSEED, FERROTHORN, 40);
        EvolutionRegistry.register(KLINK, KLANG, KLINKLANG, 38, 49);

        EvolutionRegistry.register(TYNAMO, EELEKTRIK, 39);
        EvolutionRegistry.register(EELEKTRIK, EELEKTROSS, Item.THUNDER_STONE);

        EvolutionRegistry.register(ELGYEM, BEHEEYEM, 42);

        EvolutionRegistry.register(LITWICK, LAMPENT, 41);
        EvolutionRegistry.register(LAMPENT, CHANDELURE, Item.DUSK_STONE);

        EvolutionRegistry.register(AXEW, FRAXURE, HAXORUS, 38, 48);
        EvolutionRegistry.register(CUBCHOO, BEARTIC, 37);
        EvolutionRegistry.register(SHELMET, ACCELGOR, new TradeWithEvoTrigger(KARRABLAST));
        EvolutionRegistry.register(MIENFOO, MIENSHAO, 50);
        EvolutionRegistry.register(GOLETT, GOLURK, 43);

        EvolutionRegistry.register(PAWNIARD, BISHARP, 52);
        EvolutionRegistry.register(BISHARP, KINGAMBIT, new WIPEvoTrigger()); //TODO: Bisharp -> Kingambit (Defeat 3 Bisharp holding Leader's Crest)

        EvolutionRegistry.register(RUFFLET, BRAVIARY, new LevelEvoTrigger(54), new RegionEvoTrigger(Region.HISUI, true));
        EvolutionRegistry.register(RUFFLET, BRAVIARY_HISUI, new LevelEvoTrigger(54), new RegionEvoTrigger(Region.HISUI, false));

        EvolutionRegistry.register(VULLABY, MANDIBUZZ, 54);
        EvolutionRegistry.register(DEINO, ZWEILOUS, HYDREIGON, 50, 64);
        EvolutionRegistry.register(LARVESTA, VOLCARONA, 59);
        EvolutionRegistry.register(CHESPIN, QUILLADIN, CHESNAUGHT, 16, 36);
        EvolutionRegistry.register(FENNEKIN, BRAIXEN, DELPHOX, 16, 36);
        EvolutionRegistry.register(FROAKIE, FROGADIER, GRENINJA, 16, 36);
        EvolutionRegistry.register(BUNNELBY, DIGGERSBY, 20);
        EvolutionRegistry.register(FLETCHLING, FLETCHINDER, TALONFLAME, 17, 35);
        EvolutionRegistry.register(SCATTERBUG, SPEWPA, VIVILLON, 9, 12);
        EvolutionRegistry.register(LITLEO, PYROAR, 35);

        EvolutionRegistry.register(FLABEBE, FLOETTE, 19);
        EvolutionRegistry.register(FLOETTE, FLORGES, Item.SHINY_STONE);

        EvolutionRegistry.register(SKIDDO, GOGOAT, 32);
        EvolutionRegistry.register(PANCHAM, PANGORO, new LevelEvoTrigger(32), new MoveTypeLearnedEvoTrigger(Type.DARK));

        EvolutionRegistry.register(ESPURR, MEOWSTIC_MALE, new LevelEvoTrigger(25), new GenderEvoTrigger(Gender.MALE));
        EvolutionRegistry.register(ESPURR, MEOWSTIC_FEMALE, new LevelEvoTrigger(25), new GenderEvoTrigger(Gender.FEMALE));

        EvolutionRegistry.register(HONEDGE, DOUBLADE, 35);
        EvolutionRegistry.register(DOUBLADE, AEGISLASH_BLADE, Item.DUSK_STONE);

        EvolutionRegistry.register(SPRITZEE, AROMATISSE, Item.SACHET);
        EvolutionRegistry.register(SWIRLIX, SLURPUFF, new TradeEvoTrigger(), new ItemEvoTrigger(Item.WHIPPED_DREAM));
        EvolutionRegistry.register(INKAY, MALAMAR, 30);
        EvolutionRegistry.register(BINACLE, BARBARACLE, 39);
        EvolutionRegistry.register(SKRELP, DRAGALGE, 48);
        EvolutionRegistry.register(CLAUNCHER, CLAWITZER, 37);
        EvolutionRegistry.register(HELIOPTILE, HELIOLISK, Item.SUN_STONE);
        EvolutionRegistry.register(TYRUNT, TYRANTRUM, new LevelEvoTrigger(39), new TimeEvoTrigger(Time.DAY));
        EvolutionRegistry.register(AMAURA, AURORUS, new LevelEvoTrigger(39), new TimeEvoTrigger(Time.NIGHT));

        EvolutionRegistry.register(GOOMY, SLIGGOO, new LevelEvoTrigger(40), new RegionEvoTrigger(Region.HISUI, true));
        EvolutionRegistry.register(SLIGGOO, GOODRA, new LevelEvoTrigger(50), new RegionEvoTrigger(Region.HISUI, true), new TimeEvoTrigger(Time.NIGHT));
        EvolutionRegistry.register(GOOMY, SLIGGOO_HISUI, new LevelEvoTrigger(40), new RegionEvoTrigger(Region.HISUI, false));
        EvolutionRegistry.register(SLIGGOO_HISUI, GOODRA_HISUI, new LevelEvoTrigger(50), new RegionEvoTrigger(Region.HISUI, false), new TimeEvoTrigger(Time.NIGHT));

        EvolutionRegistry.register(PHANTUMP, TREVENANT, new TradeEvoTrigger());

        EvolutionRegistry.register(PUMPKABOO_AVERAGE, GOURGEIST_AVERAGE, new TradeEvoTrigger());
        EvolutionRegistry.register(PUMPKABOO_SMALL, GOURGEIST_SMALL, new TradeEvoTrigger());
        EvolutionRegistry.register(PUMPKABOO_LARGE, GOURGEIST_LARGE, new TradeEvoTrigger());
        EvolutionRegistry.register(PUMPKABOO_SUPER, GOURGEIST_SUPER, new TradeEvoTrigger());

        EvolutionRegistry.register(BERGMITE, AVALUGG, new LevelEvoTrigger(37), new RegionEvoTrigger(Region.HISUI, true));
        EvolutionRegistry.register(BERGMITE, AVALUGG_HISUI, new LevelEvoTrigger(37), new RegionEvoTrigger(Region.HISUI, false));

        EvolutionRegistry.register(NOIBAT, NOIVERN, 48);

        EvolutionRegistry.register(ROWLET, DARTRIX, 34);
        EvolutionRegistry.register(DARTRIX, DECIDUEYE, new LevelEvoTrigger(34), new RegionEvoTrigger(Region.HISUI, true));
        EvolutionRegistry.register(DARTRIX, DECIDUEYE_HISUI, new LevelEvoTrigger(36), new RegionEvoTrigger(Region.HISUI, false));

        EvolutionRegistry.register(LITTEN, TORRACAT, INCINEROAR, 17, 34);
        EvolutionRegistry.register(POPPLIO, BRIONNE, PRIMARINA, 17, 34);
        EvolutionRegistry.register(PIKIPEK, TRUMBEAK, TOUCANNON, 14, 28);
        EvolutionRegistry.register(YUNGOOS, GUMSHOOS, new LevelEvoTrigger(20), new TimeEvoTrigger(Time.DAY));

        EvolutionRegistry.register(GRUBBIN, CHARJABUG, 20);
        EvolutionRegistry.register(CHARJABUG, VIKAVOLT, Item.THUNDER_STONE);

        EvolutionRegistry.register(CRABRAWLER, CRABOMINABLE, Item.ICE_STONE);
        EvolutionRegistry.register(CUTIEFLY, RIBOMBEE, 25);

        EvolutionRegistry.register(ROCKRUFF, LYCANROC_MIDDAY, new LevelEvoTrigger(25), new TimeEvoTrigger(Time.DAY));
        EvolutionRegistry.register(ROCKRUFF, LYCANROC_MIDNIGHT, new LevelEvoTrigger(25), new TimeEvoTrigger(Time.NIGHT));
        EvolutionRegistry.register(ROCKRUFF, LYCANROC_DUSK, new LevelEvoTrigger(25), new TimeEvoTrigger(Time.DUSK));

        EvolutionRegistry.register(MAREANIE, TOXAPEX, 38);
        EvolutionRegistry.register(MUDBRAY, MUDSDALE, 30);
        EvolutionRegistry.register(DEWPIDER, ARAQUANID, 22);
        EvolutionRegistry.register(FOMANTIS, LURANTIS, new LevelEvoTrigger(34), new TimeEvoTrigger(Time.DAY));
        EvolutionRegistry.register(MORELULL, SHIINOTIC, 24);
        EvolutionRegistry.register(SALANDIT, SALAZZLE, new LevelEvoTrigger(33), new GenderEvoTrigger(Gender.FEMALE));
        EvolutionRegistry.register(STUFFUL, BEWEAR, 27);

        EvolutionRegistry.register(BOUNSWEET, STEENEE, 18);
        EvolutionRegistry.register(STEENEE, TSAREENA, new MoveLearnedEvoTrigger(MoveEntity.STOMP));

        EvolutionRegistry.register(WIMPOD, GOLISOPOD, 30);
        EvolutionRegistry.register(SANDYGAST, PALOSSAND, 42);
        EvolutionRegistry.register(TYPE_NULL, SILVALLY, new HighFriendshipEvoTrigger());
        EvolutionRegistry.register(JANGMO_O, HAKAMO_O, KOMMO_O, 35, 45);

        EvolutionRegistry.register(COSMOG, COSMOEM, 43);
        EvolutionRegistry.register(COSMOEM, SOLGALEO, new LevelEvoTrigger(53), new TimeEvoTrigger(Time.DAY));
        EvolutionRegistry.register(COSMOEM, LUNALA, new LevelEvoTrigger(53), new TimeEvoTrigger(Time.NIGHT));

        EvolutionRegistry.register(POIPOLE, NAGANADEL, new MoveLearnedEvoTrigger(MoveEntity.DRAGON_PULSE));
        EvolutionRegistry.register(MELTAN, MELMETAL, new WIPEvoTrigger()); //TODO: Meltan -> Melmetal (400 Meltan Candies)
        EvolutionRegistry.register(GROOKEY, THWACKEY, RILLABOOM, 16, 35);
        EvolutionRegistry.register(SCORBUNNY, RABOOT, CINDERACE, 16, 35);
        EvolutionRegistry.register(SOBBLE, DRIZZILE, INTELEON, 16, 35);
        EvolutionRegistry.register(BLIPBUG, DOTTLER, ORBEETLE, 10, 30);
        EvolutionRegistry.register(ROOKIDEE, CORVISQUIRE, CORVIKNIGHT, 18, 38);
        EvolutionRegistry.register(SKWOVET, GREEDENT, 24);
        EvolutionRegistry.register(NICKIT, THIEVUL, 18);
        EvolutionRegistry.register(WOOLOO, DUBWOOL, 24);
        EvolutionRegistry.register(CHEWTLE, DREDNAW, 22);
        EvolutionRegistry.register(YAMPER, BOLTUND, 25);
        EvolutionRegistry.register(GOSSIFLEUR, ELDEGOSS, 20);
        EvolutionRegistry.register(SIZZLIPEDE, CENTISKORCH, 28);
        EvolutionRegistry.register(ROLYCOLY, CARKOL, COALOSSAL, 18, 34);
        EvolutionRegistry.register(ARROKUDA, BARRASKEWDA, 26);
        EvolutionRegistry.register(MILCERY, ALCREMIE, new ItemEvoTrigger(Item.SWEET));

        EvolutionRegistry.register(APPLIN, FLAPPLE, Item.TART_APPLE);
        EvolutionRegistry.register(APPLIN, APPLETUN, Item.SWEET_APPLE);

        EvolutionRegistry.register(FARFETCHD_GALAR, SIRFETCHD, new WIPEvoTrigger()); //TODO: Galarian Farfetch'd -> Sirfetch'd (3 Crits in a Battle)
        EvolutionRegistry.register(CORSOLA_GALAR, CURSOLA, 38);
        EvolutionRegistry.register(IMPIDIMP, MORGREM, GRIMMSNARL, 32, 42);
        EvolutionRegistry.register(HATENNA, HATTREM, HATTERENE, 32, 42);
        EvolutionRegistry.register(CUFANT, COPPERAJAH, 34);

        EvolutionRegistry.register(TOXEL, TOXTRICITY_AMPED, new LevelEvoTrigger(30), new WIPEvoTrigger()); //TODO: Toxel -> Toxtricity (Level 30 + Amped vs Low Key Nature)
        EvolutionRegistry.register(TOXEL, TOXTRICITY_LOW_KEY, new LevelEvoTrigger(30), new WIPEvoTrigger());

        EvolutionRegistry.register(SILICOBRA, SANDACONDA, 36);
        EvolutionRegistry.register(SINISTEA, POLTEAGEIST, Item.CRACKED_POT);
        EvolutionRegistry.register(SNOM, FROSMOTH, new HighFriendshipEvoTrigger(), new TimeEvoTrigger(Time.NIGHT));
        EvolutionRegistry.register(CLOBBOPUS, GRAPPLOCT, new MoveLearnedEvoTrigger(MoveEntity.TAUNT));
        EvolutionRegistry.register(DREEPY, DRAKLOAK, DRAGAPULT, 50, 60);

        EvolutionRegistry.register(KUBFU, URSHIFU_SINGLE_STRIKE, Item.SCROLL_OF_DARKNESS);
        EvolutionRegistry.register(KUBFU, URSHIFU_RAPID_STRIKE, Item.SCROLL_OF_WATERS);

        EvolutionRegistry.register(BASCULIN_WHITE, BASCULEGION_MALE, new GenderEvoTrigger(Gender.MALE), new WIPEvoTrigger()); //TODO: White Basculin -> Basculegion (receive 294 recoil damage in battle)
        EvolutionRegistry.register(BASCULIN_WHITE, BASCULEGION_FEMALE, new GenderEvoTrigger(Gender.FEMALE), new WIPEvoTrigger());

        EvolutionRegistry.register(QWILFISH_HISUI, OVERQWIL, new WIPEvoTrigger()); //TODO: Qwilfish Hisui -> Overqwil (Use Barb Barrage 20 times Strong Style)

        EvolutionRegistry.register(SPRIGATITO, FLORAGATO, MEOWSCARADA, 16, 36);
        EvolutionRegistry.register(FUECOCO, CROCALOR, SKELEDIRGE, 16, 36);
        EvolutionRegistry.register(QUAXLY, QUAXWELL, QUAQUAVAL, 16, 36);

        EvolutionRegistry.register(LECHONK, OINKOLOGNE_MALE, new LevelEvoTrigger(18), new GenderEvoTrigger(Gender.MALE));
        EvolutionRegistry.register(LECHONK, OINKOLOGNE_FEMALE, new LevelEvoTrigger(18), new GenderEvoTrigger(Gender.FEMALE));

        EvolutionRegistry.register(TAROUNTULA, SPIDOPS, 15);
        EvolutionRegistry.register(NYMBLE, LOKIX, 24);
        EvolutionRegistry.register(RELLOR, RABSCA, new WIPEvoTrigger()); //TODO: Rellor -> Rabsca (Walk 1k steps in Let's Go Mode)
        EvolutionRegistry.register(GREAVARD, HOUNDSTONE, new LevelEvoTrigger(30), new TimeEvoTrigger(Time.NIGHT));
        EvolutionRegistry.register(FLITTLE, ESPATHRA, 35);
        EvolutionRegistry.register(WIGLETT, WUGTRIO, 26);
        EvolutionRegistry.register(FINIZEN, PALAFIN_ZERO, 38);
        EvolutionRegistry.register(SMOLIV, DOLLIV, ARBOLIVA, 25, 35);
        EvolutionRegistry.register(CAPSAKID, SCOVILLAIN, Item.FIRE_STONE);
        EvolutionRegistry.register(TADBULB, BELLIBOLT, Item.THUNDER_STONE);
        EvolutionRegistry.register(VAROOM, REVAVROOM, 40);
        EvolutionRegistry.register(TANDEMAUS, MAUSHOLD, 25);
        EvolutionRegistry.register(CETODDLE, CETITAN, Item.ICE_STONE);
        EvolutionRegistry.register(FRIGIBAX, ARCTIBAX, BAXCALIBUR, 35, 54);

        EvolutionRegistry.register(PAWMI, PAWMO, 18);
        EvolutionRegistry.register(PAWMO, PAWMOT, new WIPEvoTrigger()); //TODO: Pawmo -> Pawmot (Walk 1k steps in Let's Go Mode)

        EvolutionRegistry.register(WATTREL, KILOWATTREL, 25);
        EvolutionRegistry.register(NACLI, NACLSTACK, GARGANACL, 24, 38);
        EvolutionRegistry.register(GLIMMET, GLIMMORA, 35);
        EvolutionRegistry.register(SHROODLE, GRAFAIAI, 28);
        EvolutionRegistry.register(FIDOUGH, DACHSBUN, 26);

        EvolutionRegistry.register(CHARCADET, ARMAROUGE, Item.AUSPICIOUS_ARMOR);
        EvolutionRegistry.register(CHARCADET, CERULEDGE, Item.MALICIOUS_ARMOR);

        EvolutionRegistry.register(MASCHIFF, MABOSSTIFF, 30);
        EvolutionRegistry.register(BRAMBLIN, BRAMBLEGHAST, new WIPEvoTrigger()); //TODO: Bramblin -> Brambleghast (Walk 1k steps in Let's Go Mode)
        EvolutionRegistry.register(TOEDSCOOL, TOEDSCRUEL, 30);
        EvolutionRegistry.register(TINKATINK, TINKATUFF, TINKATON, 24, 38);

        EvolutionRegistry.register(GIMMIGHOUL, GHOLDENGO, new WIPEvoTrigger()); //TODO: Gimmighoul (Chest) -> Gholdengo (Collect 999 coins from Roaming Form)
    }

    //Access
    public static boolean hasEvolutionData(PokemonEntity entity)
    {
        return EVOLUTION_DATA.containsKey(entity) && EvolutionRegistry.getEvolutionData(entity).size() > 0;
    }

    public static List<EvolutionData> getEvolutionData(PokemonEntity entity)
    {
        return EVOLUTION_DATA.get(entity);
    }

    //Registering
    private static void register(PokemonEntity source, PokemonEntity target, EvolutionTrigger trigger1, EvolutionTrigger... triggers)
    {
        if(!EVOLUTION_DATA.containsKey(source)) EVOLUTION_DATA.put(source, new ArrayList<>());

        EVOLUTION_DATA.get(source).add(new EvolutionData(source, target, trigger1, triggers));
    }

    private static void register(PokemonEntity source, PokemonEntity target, int level)
    {
        EvolutionRegistry.register(source, target, new LevelEvoTrigger(level));
    }

    private static void register(PokemonEntity base, PokemonEntity stage1, PokemonEntity stage2, int level1, int level2)
    {
        EvolutionRegistry.register(base, stage1, level1);
        EvolutionRegistry.register(stage1, stage2, level2);
    }

    private static void register(PokemonEntity source, PokemonEntity target, Item item)
    {
        EvolutionRegistry.register(source, target, new ItemEvoTrigger(item));
    }
}
