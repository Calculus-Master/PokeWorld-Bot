package com.calculusmaster.pokecord.game.pokemon;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.moves.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialEvolutionRegistry
{
    public static final List<Evolution> EVOLUTIONS = new ArrayList<>();

    //TODO: Wurmple -> Cascoon/Silcoon
    //TODO: Burmy -> Mothim (Male) or Wormadam (Male) at Level 20
    //TODO: Karrablast -> Escaliver & Shelmet -> Accelgor w/Trade
    //TODO: G. Farfetchd -> Sirfetchd (3 critical hits in a battle)

    //TODO: Forms: Castform, Zacian/Zamazenta, Wormadam, Rotom, Basculin, Darmanitan Zen (Galarian Darmanitan is added), Meowstic, Pumpkaboo, Gourgeist, Oricorio

    public static void init()
    {
        register("Pikachu", "Raichu", PokeItem.THUNDER_STONE)
                .another("Charjabug", "Vikavolt")
                .another("Eelektrik", "Eelektross");

        register("Alolan Sandshrew", "Alolan Sandslash", PokeItem.ICE_STONE)
                .another("Alolan Vulpix", "Alolan Ninetales")
                .another("Galarian Darumaka", "Galarian Darmanitan");

        register("Nidorina", "Nidoqueen", PokeItem.MOON_STONE)
                .another("Nidorino", "Nidoking")
                .another("Clefairy", "Clefable")
                .another("Jigglypuff", "Wigglytuff")
                .another("Skitty", "Delcatty")
                .another("Munna", "Musharna");

        register("Vulpix", "Ninetales", PokeItem.FIRE_STONE)
                .another("Growlithe", "Arcanine")
                .another("Pansear", "Simisear");

        register("Gloom", "Vileplume", PokeItem.LEAF_STONE)
                .another("Bellossom", PokeItem.SUN_STONE);

        register("Weepinbell", "Victreebel", PokeItem.LEAF_STONE)
                .another("Exeggcute", "Exeggutor")
                .another("Nuzleaf", "Shiftry")
                .another("Pansage", "Simisage");

        register("Shellder", "Cloyster", PokeItem.WATER_STONE)
                .another("Staryu", "Starmine")
                .another("Lombre", "Ludicolo")
                .another("Panpour", "Simipour");

        register("Eevee", "Vaporeon", PokeItem.WATER_STONE)
                .another("Flareon", PokeItem.FIRE_STONE)
                .another("Jolteon", PokeItem.THUNDER_STONE)
                .another("Sylveon", p -> p.getLearnedMoves().stream().map(Move::new).anyMatch(m -> m.getType().equals(Type.FAIRY)));

        register("Onix", "Steelix", PokeItem.METAL_COAT)
                .another("Scyther", "Scizor");

        register("Haunter", "Gengar", PokeItem.TRADE_EVOLVER)
                .another("Poliwhirl", "Politoed")
                .another("Kadabra", "Alakazam")
                .another("Machoke", "Machamp")
                .another("Graveler", "Golem")
                .another("Alolan Graveler", "Alolan Golem")
                .another("Boldore", "Gigalith")
                .another("Gurdurr", "Conkeldurr")
                .another("Phantump", "Trevenant")
                .another("Pumpkaboo", "Gourgeist");

        register("Poipole", "Naganadel", "Dragon Pulse");

        register("Poliwhirl", "Poliwrath", PokeItem.WATER_STONE)
                .another("Politoed", PokeItem.KINGS_ROCK);

        register("Aipom", "Ambipom", "Double Hit");

        register("Sunkern", "Sunflora", PokeItem.LEAF_STONE)
                .another("Cottonee", "Whimsicott")
                .another("Petilil", "Lilligant")
                .another("Helioptile", "Heliolisk");

        register("Yanma", "Yanmega", "Ancient Power")
                .another("Piloswine", "Mamoswine")
                .another("Tangela", "Tangrowth");

        register("Murkrow", "Honchkrow", PokeItem.DUSK_STONE)
                .another("Misdreavus", "Mismagius")
                .another("Lampent", "Chandelure")
                .another("Doublade", "Aegislash");

        register("Slowpoke", "Slowking", PokeItem.KINGS_ROCK);

        register("Galarian Slowpoke", "Galarian Slowbro", PokeItem.GALARICA_CUFF)
                .another("Galarian Slowking", PokeItem.GALARICA_WREATH);

        register("Gligar", "Gliscor", PokeItem.RAZOR_FANG);

        register("Sneasel", "Weavile", PokeItem.RAZOR_CLAW);

        register("Seadra", "Kingdra", PokeItem.DRAGON_SCALE);

        register("Porygon", "Porygon2", PokeItem.UPGRADE);

        register("Porygon", "Porygonz", PokeItem.DUBIOUS_DISC);

        register("Tyrogue", "Hitmonlee", p -> p.getStat(Stat.ATK) > p.getStat(Stat.DEF))
                .another("Hitmonchan", p -> p.getStat(Stat.ATK) < p.getStat(Stat.DEF))
                .another("Hitmontop", p -> p.getStat(Stat.ATK) == p.getStat(Stat.DEF));

        register("Kirlia", "Gallade", PokeItem.DAWN_STONE)
                .another("Snorunt", "Froslass");

        register("Roselia", "Roserade", PokeItem.SHINY_STONE)
                .another("Togetic", "Togekiss")
                .another("Minccino", "Cinccino")
                .another("Floette", "Florges");

        register("Feebas", "Milotic", PokeItem.PRISM_SCALE);

        register("Dusclops", "Dusknoir", PokeItem.REAPER_CLOTH);

        register("Clamperl", "Huntail", PokeItem.DEEP_SEA_TOOTH)
                .another("Gorebyss", PokeItem.DEEP_SEA_SCALE);

        register("Bonsly", "Sudowoodo", "Mimic")
                .another("Mime Jr", "Mr Mime");

        register("Happiny", "Chansey", PokeItem.OVAL_STONE);

        register("Lickitung", "Lickilicky", "Rollout");

        register("Rhydon", "Rhyperior", PokeItem.PROTECTOR);

        register("Electabuzz", "Electivire", PokeItem.ELECTIRIZER);

        register("Magmar", "Magmortar", PokeItem.MAGMARIZER);

        register("Spritzee", "Aromatisse", PokeItem.SACHET);

        register("Swirlix", "Slurpuff", PokeItem.WHIPPED_DREAM);

        register("Steenee", "Tsareena", "Stomp");

        register("Alolan Meowth", "Alolan Persian", PokeItem.FRIENDSHIP_BAND)
                .another("Chansey", "Blissey")
                .another("Golbat", "Crobat")
                .another("Pichu", "Pikachu")
                .another("Cleffa", "Clefairy")
                .another("Igglybuff", "Jigglypuff")
                .another("Togepi", "Togetic")
                .another("Azurill", "Marill")
                .another("Budew", "Roselia")
                .another("Chingling", "Chimecho")
                .another("Buneary", "Lopunny")
                .another("Munchlax", "Snorlax")
                .another("Riolu", "Lucario")
                .another("Woobat", "Swoobat")
                .another("Swadloon", "Leavanny")
                .another("Type Null", "Silvally")
                .another("Snom", "Frosmoth");

        register("Clobbopus", "Grapploct", "Taunt");

        register("Toxel", "Toxtricity Amped", p -> p.getNature().isAmped())
                .another("Toxtricity Low Key", p -> !p.getNature().isAmped());

        register("Sinistea", "Polteageist", PokeItem.CRACKED_POT);

        register("Applin", "Flapple", PokeItem.TART_APPLE)
                .another("Appletun", PokeItem.SWEET_APPLE);

        register("Milcery", "Alcremie", PokeItem.SWEET);
    }

    public static String getTarget(Pokemon p)
    {
        List<Evolution> possible = EVOLUTIONS.stream().filter(e -> e.source.equals(p.getName())).collect(Collectors.toList());

        for(Evolution e : possible) if(e.validator.canEvolve(p)) return e.target;
        return "";
    }

    public static boolean hasSpecialEvolution(String pokemon)
    {
        return EVOLUTIONS.stream().anyMatch(e -> e.source.equals(pokemon));
    }

    public static boolean canEvolve(Pokemon p)
    {
        return EVOLUTIONS.stream().filter(e -> e.source.equals(p.getName())).anyMatch(e -> e.validator.canEvolve(p));
    }

    private static Evolution register(String source, String target, String move)
    {
        return register(source, target, p -> p.getLearnedMoves().contains(move));
    }

    private static Evolution register(String source, String target, PokeItem item)
    {
        EvolutionValidator normal = p -> p.hasItem() && PokeItem.asItem(p.getItem()).equals(item);
        EvolutionValidator friendship = p -> p.hasItem() && PokeItem.asItem(p.getItem()).equals(item) && hasFriendship(p);
        return register(source, target, item.equals(PokeItem.FRIENDSHIP_BAND) ? friendship : normal);
    }

    public static boolean hasFriendship(Pokemon p)
    {
        return p.getLevel() >= 50 && p.getEVTotal() >= 20;
    }

    private static Evolution register(String source, String target, EvolutionValidator validator)
    {
        Evolution ev = new Evolution(source, target, validator);

        EVOLUTIONS.add(ev);

        return ev;
    }

    private static class Evolution
    {
        private String source;
        private String target;
        private EvolutionValidator validator;

        Evolution(String source, String target, EvolutionValidator validator)
        {
            this.source = source;
            this.target = target;
            this.validator = validator;
        }

        Evolution another(String source, String target)
        {
            register(source, target, this.validator);
            return this;
        }

        Evolution another(String target, PokeItem item)
        {
            register(this.source, target, item);
            return this;
        }

        Evolution another(String target, EvolutionValidator validator)
        {
            register(this.source, target, validator);
            return this;
        }
    }

    private interface EvolutionValidator
    {
        boolean canEvolve(Pokemon p);
    }
}