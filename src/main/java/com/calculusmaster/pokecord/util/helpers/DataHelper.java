package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.game.pokemon.PokemonData;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.custom.ExtendedHashMap;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.Range;

import java.util.*;
import java.util.stream.Collectors;

public class DataHelper
{
    public static final Map<String, PokemonData> POKEMON_DATA = new HashMap<>();
    public static final List<String> POKEMON = new ArrayList<>();

    public static final Map<String, MoveData> MOVE_DATA = new HashMap<>();
    public static final List<String> MOVES = new ArrayList<>();

    public static final Map<String, List<String>> SERVER_PLAYERS = new HashMap<>();
    public static final List<List<String>> EV_LISTS = new ArrayList<>();
    public static final Map<Type, List<String>> TYPE_LISTS = new HashMap<>();
    public static final Map<String, GigantamaxData> GIGANTAMAX_DATA = new HashMap<>();
    public static final Map<Integer, List<String>> POKEMON_SPECIES_DESC = new HashMap<>();
    public static final Map<Integer, List<EggGroup>> POKEMON_EGG_GROUPS = new HashMap<>();
    public static final Map<Integer, Integer> POKEMON_BASE_HATCH_TARGETS = new HashMap<>();
    public static final Map<Integer, Integer> POKEMON_GENDER_RATES = new HashMap<>();

    //Pokemon Data
    public static void createPokemonData()
    {
        Mongo.PokemonInfo.find().forEach(d -> POKEMON_DATA.put(d.getString("name"), new PokemonData(d)));
    }

    public static void createPokemonList()
    {
        POKEMON.addAll(POKEMON_DATA.keySet());
    }

    public static PokemonData pokeData(String name)
    {
        return POKEMON_DATA.get(name).copy();
    }

    public static int dex(String name)
    {
        return pokeData(name).dex;
    }

    //Moves
    public static void createMoveData()
    {
        //id,identifier,generation_id,damage_class_id
        final Map<String, String> typeID = new HashMap<>();
        CSVHelper.readCSV("types").forEach(s -> typeID.put(s[0], Global.normalCase(s[1])));

        //Category (Damage Class)
        final Map<String, String> categoryID = new ExtendedHashMap<String, String>().insert("1", "STATUS").insert("2", "PHYSICAL").insert("3", "SPECIAL");

        //move_id,version_group_id,language_id,flavor_text
        final List<String[]> movesFlavorCSV = CSVHelper.readCSV("move_flavor_text").stream().filter(l -> l[2].equals("9")).collect(Collectors.toList());

        //id,identifier,generation_id,type_id,power,pp,accuracy,priority,target_id,damage_class_id,effect_id,effect_chance,contest_type_id,contest_effect_id,super_contest_effect_id
        final List<Range<Integer>> skipIDs = Arrays.asList(Range.between(622, 658), Range.between(662, 664), Range.between(695, 703), Range.between(719, 719), Range.between(723, 741), Range.between(757, 774), Range.between(10001, 10018));
        List<String[]> movesCSV = CSVHelper.readCSV("moves").stream()
                .filter(line -> skipIDs.stream().noneMatch(r -> r.contains(Integer.parseInt(line[0]))))
                .collect(Collectors.toList());

        for(String[] moveLine : movesCSV)
        {
            String name = Global.normalCase(moveLine[1].replaceAll("-", " "));
            String type = typeID.get(moveLine[3]);
            String category = categoryID.get(moveLine[9]);
            int power = moveLine[4].equals("") ? 0 : Integer.parseInt(moveLine[4]);
            int accuracy = moveLine[6].equals("") ? 100 : Integer.parseInt(moveLine[6]);
            List<String> flavor = movesFlavorCSV.stream().filter(line -> line[0].equals(moveLine[0])).map(s -> s[3]).distinct().collect(Collectors.toList());

            MOVE_DATA.put(name, new MoveData(name, Type.cast(type), Category.cast(category), power, accuracy, flavor));
        }
    }

    public static void createMoveList()
    {
        MOVES.addAll(MOVE_DATA.keySet());
    }

    public static MoveData moveData(String name)
    {
        return MOVE_DATA.get(name).copy();
    }

    //Server Players
    public static void updateServerPlayers(Guild g)
    {
        SERVER_PLAYERS.put(g.getId(), new ArrayList<>());

        g.loadMembers();

        for(Member m : g.getMembers()) if(PlayerDataQuery.isRegistered(m.getId())) SERVER_PLAYERS.get(g.getId()).add(m.getId());
    }

    public static void removeServer(String ID)
    {
        SERVER_PLAYERS.remove(ID);
    }

    //EV Lists
    public static void createEVLists()
    {
        for(int i = 0; i < 6; i++) EV_LISTS.add(new ArrayList<>());

        Mongo.PokemonInfo.find(Filters.exists("ev")).forEach(d -> {
            List<Integer> j = d.getList("ev", Integer.class);
            for(int i = 0; i < 6; i++) if(j.get(i) > 0) EV_LISTS.get(i).add(d.getString("name"));
        });
    }

    //Type Lists
    public static void createTypeLists()
    {
        for(Type t : Type.values()) TYPE_LISTS.put(t, new ArrayList<>());

        Mongo.PokemonInfo.find().forEach(d -> {
            List<Type> types = d.getList("type", String.class).stream().distinct().map(Type::cast).collect(Collectors.toList());
            for(Type t : types) TYPE_LISTS.get(t).add(d.getString("name"));
        });
    }

    //Gigantamax
    public record GigantamaxData(String pokemon, String move, Type moveType, String normalImage, String shinyImage) {}

    public static void createGigantamaxDataMap()
    {
        registerGigantamax("Charizard", "Wildfire", Type.FIRE, "https://archives.bulbagarden.net/media/upload/thumb/8/88/006Charizard-Gigantamax.png/600px-006Charizard-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8g5-b2a95aec-480d-4ec9-8ed7-0f7b465bbe04.png");
        registerGigantamax("Butterfree", "Befuddle", Type.BUG, "https://archives.bulbagarden.net/media/upload/thumb/f/fd/012Butterfree-Gigantamax.png/600px-012Butterfree-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8la-da8208d5-c16d-4574-a6d1-7021b5d8e4b7.png");
        registerGigantamax("Pikachu", "Volt Crash", Type.ELECTRIC, "https://archives.bulbagarden.net/media/upload/thumb/6/6b/025Pikachu-Gigantamax.png/600px-025Pikachu-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8nd-f539417b-3e5e-4372-8940-001642e83a29.png");
        registerGigantamax("Meowth", "Gold Rush", Type.NORMAL, "https://archives.bulbagarden.net/media/upload/thumb/9/9f/052Meowth-Gigantamax.png/600px-052Meowth-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8p6-8e182c3c-f06b-40b1-acc1-e6fb1e78ecf2.png");
        registerGigantamax("Machamp", "Chi Strike", Type.FIGHTING, "https://archives.bulbagarden.net/media/upload/c/c4/068Machamp-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8r6-345e59b2-5d11-434f-835e-f409ffe1645b.png");
        registerGigantamax("Gengar", "Terror", Type.GHOST, "https://archives.bulbagarden.net/media/upload/3/31/094Gengar-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8tf-072f9eb4-202b-4762-968a-aba5dc736fdc.png");
        registerGigantamax("Kingler", "Foam Burst", Type.WATER, "https://archives.bulbagarden.net/media/upload/d/d3/099Kingler-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8v7-479dc844-e073-42ed-8e75-ebcec0e1ddc2.png");
        registerGigantamax("Lapras", "Resonance", Type.ICE, "https://archives.bulbagarden.net/media/upload/1/15/131Lapras-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8xe-0ff37d4c-2d62-4e23-9636-a26e56e641f9.png");
        registerGigantamax("Eevee", "Cuddle", Type.NORMAL, "https://archives.bulbagarden.net/media/upload/thumb/2/2e/133Eevee-Gigantamax.png/600px-133Eevee-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8yy-eca81996-8027-4e92-adea-d34789cf89c5.png");
        registerGigantamax("Snorlax", "Replenish", Type.NORMAL, "https://archives.bulbagarden.net/media/upload/thumb/3/38/143Snorlax-Gigantamax.png/600px-143Snorlax-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h9j8-9f20c69d-fc0b-4404-b23f-c1ce8f13fef9.png");
        registerGigantamax("Garbodor", "Malodor", Type.POISON, "https://archives.bulbagarden.net/media/upload/c/c9/569Garbodor-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h9m3-45758612-321e-47d3-b7de-a948bf1655a4.png");
        registerGigantamax("Melmetal", "Meltdown", Type.STEEL, "https://archives.bulbagarden.net/media/upload/thumb/5/5e/809Melmetal-Gigantamax.png/600px-809Melmetal-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de609ih-d7f1603a-92d1-45fb-9382-466ebd59e1ac.png");
        registerGigantamax("Corviknight", "Wind Rage", Type.FLYING, "https://archives.bulbagarden.net/media/upload/thumb/2/2e/823Corviknight-Gigantamax.png/600px-823Corviknight-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5m4-af08351e-42a4-432c-8f8d-ee33e9a27608.png");
        registerGigantamax("Orbeetle", "Gravitas", Type.PSYCHIC, "https://archives.bulbagarden.net/media/upload/2/24/826Orbeetle-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5pg-fb09e50b-14d8-4501-b84d-b828200604e2.png");
        registerGigantamax("Drednaw", "Stonesurge", Type.WATER, "https://archives.bulbagarden.net/media/upload/thumb/b/b4/834Drednaw-Gigantamax.png/600px-834Drednaw-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5t5-a5195291-9518-40a2-9846-f6c3f78f4355.png");
        registerGigantamax("Coalossal", "Volcalith", Type.ROCK, "https://archives.bulbagarden.net/media/upload/b/b0/839Coalossal-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5wa-b42cff90-3a0b-4902-ad86-575e5534b104.png");
        registerGigantamax("Flapple", "Tartness", Type.GRASS, "https://archives.bulbagarden.net/media/upload/a/a2/841Flapple-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5y8-9349734d-323c-463a-9dc6-8853a2ed527d.png");
        registerGigantamax("Appletun", "Sweetness", Type.GRASS, "https://archives.bulbagarden.net/media/upload/a/a2/841Flapple-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c5y8-9349734d-323c-463a-9dc6-8853a2ed527d.png");
        registerGigantamax("Sandaconda", "Sandblast", Type.GROUND, "https://archives.bulbagarden.net/media/upload/thumb/1/19/844Sandaconda-Gigantamax.png/600px-844Sandaconda-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c63w-15849167-115c-4126-ac8f-9147dc2e80ad.png");
        registerGigantamax("Toxtricity Low Key", "Stunshock", Type.ELECTRIC, "https://archives.bulbagarden.net/media/upload/thumb/7/73/849Toxtricity-Gigantamax.png/600px-849Toxtricity-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c66g-4ae1f5ae-e105-49db-a5f0-f1c1bc29029e.png");
        registerGigantamax("Toxtricity Amped", "Stunshock", Type.ELECTRIC, "https://archives.bulbagarden.net/media/upload/thumb/7/73/849Toxtricity-Gigantamax.png/600px-849Toxtricity-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c66g-4ae1f5ae-e105-49db-a5f0-f1c1bc29029e.png");
        registerGigantamax("Centiskorch", "Centiferno", Type.FIRE, "https://archives.bulbagarden.net/media/upload/thumb/b/b4/851Centiskorch-Gigantamax.png/600px-851Centiskorch-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c68f-e67e2d21-921f-4402-9c51-c566a8b21158.png");
        registerGigantamax("Hatterene", "Smite", Type.FAIRY, "https://archives.bulbagarden.net/media/upload/6/6a/858Hatterene-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6a1-c9664b1f-7032-4a22-970a-ed004a02ef97.png");
        registerGigantamax("Grimmsnarl", "Snooze", Type.DARK, "https://archives.bulbagarden.net/media/upload/4/45/861Grimmsnarl-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6by-d97b1fa5-b7ee-4763-9f7a-e058dbe15822.png");
        registerGigantamax("Alcremie", "Finale", Type.FAIRY, "https://archives.bulbagarden.net/media/upload/thumb/4/40/869Alcremie-Gigantamax.png/600px-869Alcremie-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6fz-26076c68-27ea-4bf3-870d-6f7b5d4fe841.png");
        registerGigantamax("Copperajah", "Steelsurge", Type.STEEL, "https://archives.bulbagarden.net/media/upload/1/16/879Copperajah-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6hr-b15b2a1b-c278-43bd-95fc-4c85f9f2c91c.png");
        registerGigantamax("Duraludon", "Depletion", Type.DRAGON, "https://archives.bulbagarden.net/media/upload/1/1b/884Duraludon-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de1c6jb-dec68b9e-738c-448c-82c0-c616b150f441.png");
        registerGigantamax("Venusaur", "Vine Lash", Type.GRASS, "https://archives.bulbagarden.net/media/upload/thumb/8/8a/003Venusaur-Gigantamax.png/600px-003Venusaur-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8a2-5c5b3deb-fbc3-468a-a2d4-d430b18d6d6e.png");
        registerGigantamax("Blastoise", "Cannonade", Type.WATER, "https://archives.bulbagarden.net/media/upload/thumb/d/dc/009Blastoise-Gigantamax.png/600px-009Blastoise-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h8ig-946b4fb0-5730-434e-b7fc-6d62e21543d9.png");
        registerGigantamax("Rillaboom", "Drum Solo", Type.GRASS, "https://archives.bulbagarden.net/media/upload/thumb/0/0b/812Rillaboom-Gigantamax.png/600px-812Rillaboom-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h9pw-0e6e9f81-e373-4e50-8f77-2b9ff451da93.png");
        registerGigantamax("Cinderace", "Fireball", Type.FIRE, "https://archives.bulbagarden.net/media/upload/thumb/1/1e/815Cinderace-Gigantamax.png/600px-815Cinderace-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h9sn-c4565af1-9718-46d6-9187-557362c96071.png");
        registerGigantamax("Inteleon", "Hydrosnipe", Type.WATER, "https://archives.bulbagarden.net/media/upload/thumb/b/bf/818Inteleon-Gigantamax.png/600px-818Inteleon-Gigantamax.png", "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/i/e48d6b9d-3b1d-46a0-a254-3a448ec3a8a5/de0h9uz-7dbcfcfc-ee7d-4503-8d0a-2f3c273792bb.png");
        registerGigantamax("Urshifu", "One Blow", Type.DARK, "https://archives.bulbagarden.net/media/upload/thumb/f/fd/892Urshifu-Gigantamax_Single_Strike.png/600px-892Urshifu-Gigantamax_Single_Strike.png");
        registerGigantamax("Urshifu Rapid Strike", "Rapid Flow", Type.WATER, "https://archives.bulbagarden.net/media/upload/thumb/1/1d/892Urshifu-Gigantamax_Rapid_Strike.png/600px-892Urshifu-Gigantamax_Rapid_Strike.png");

        //Custom
        //Rayquaza, Groudon and Kyogre images were made by User "KingofAnime-KoA" on DeviantArt
        registerGigantamax("Rayquaza", "Stratoblast", Type.FLYING, "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/8ba904d2-8453-4d48-aa9f-1c01fb79c3af/de45v3l-207fe813-2cae-4df9-9990-fef5d4500738.png/v1/fill/w_1280,h_1467,strp/gigantamax_rayquaza__the_gmax_lord_of_the_skies_by_kingofanime_koa_de45v3l-fullview.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTQ2NyIsInBhdGgiOiJcL2ZcLzhiYTkwNGQyLTg0NTMtNGQ0OC1hYTlmLTFjMDFmYjc5YzNhZlwvZGU0NXYzbC0yMDdmZTgxMy0yY2FlLTRkZjktOTk5MC1mZWY1ZDQ1MDA3MzgucG5nIiwid2lkdGgiOiI8PTEyODAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.1ZIszvLOVzHyEzH7NcOo-y0LmeX7UTwDnsv6ziuZNdg");
        registerGigantamax("Kyogre", "Oceanize", Type.WATER, "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/8ba904d2-8453-4d48-aa9f-1c01fb79c3af/de4o4sh-d052123f-4620-41d2-8188-fc193ae3962c.png/v1/fill/w_1280,h_1377,strp/gigantimax_kyogre__the_colossal_lord_of_the_oceans_by_kingofanime_koa_de4o4sh-fullview.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTM3NyIsInBhdGgiOiJcL2ZcLzhiYTkwNGQyLTg0NTMtNGQ0OC1hYTlmLTFjMDFmYjc5YzNhZlwvZGU0bzRzaC1kMDUyMTIzZi00NjIwLTQxZDItODE4OC1mYzE5M2FlMzk2MmMucG5nIiwid2lkdGgiOiI8PTEyODAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.x7DsTDYYQLuzVIV5xlzSQcnRASW5oCcb3wLaZtGWIUY");
        registerGigantamax("Groudon", "Evaporation", Type.GROUND, "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/8ba904d2-8453-4d48-aa9f-1c01fb79c3af/de4x2c3-4b0bcdd8-5f75-47ef-b952-31875146991a.png/v1/fill/w_1280,h_1370,strp/gigantamax_groudon__the_gmax_molten_kaiju__by_kingofanime_koa_de4x2c3-fullview.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTM3MCIsInBhdGgiOiJcL2ZcLzhiYTkwNGQyLTg0NTMtNGQ0OC1hYTlmLTFjMDFmYjc5YzNhZlwvZGU0eDJjMy00YjBiY2RkOC01Zjc1LTQ3ZWYtYjk1Mi0zMTg3NTE0Njk5MWEucG5nIiwid2lkdGgiOiI8PTEyODAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.3TMzdvI8QLmyfo1wkh6ZScaUVzD-eRsgbwG54hn70dE");
    }

    private static void registerGigantamax(String name, String move, Type type, String normal, String shiny)
    {
        GIGANTAMAX_DATA.put(name, new GigantamaxData(name, "G Max " + move, type, normal, shiny));
    }

    private static void registerGigantamax(String name, String move, Type type, String normal)
    {
        registerGigantamax(name, move, type, normal, normal);
    }

    //Species Description Lines (from CSV)
    public static void createSpeciesDescLists()
    {
        List<String[]> speciesCSV = CSVHelper.readCSV("pokemon_species_flavor_text").stream().filter(l -> l[2].equals("9")).collect(Collectors.toList());

        //species_id,version_id,language_id,flavor_text
        //language_id 9 is English
        for(int i = 1; i <= 898; i++)
        {
            final int dex = i;
            List<String> lines = speciesCSV.stream()
                    .filter(l -> l[0].equals(dex + "")) //Lines with dex number
                    .map(l -> l[3]) //Map to the Flavor Text
                    .distinct()//Remove duplicates
                    .map(s -> s.replaceAll("\n", " ")) //Replace new line characters with spaces
                    .map(s -> s.replaceAll("POKéMON", "Pokemon")) //Fix the weirdly formatted Pokemon word
                    .collect(Collectors.toList());
            POKEMON_SPECIES_DESC.put(dex, lines);
        }
    }

    //Egg Groups (from CSV)
    public static void createEggGroupLists()
    {
        List<String[]> eggCSV = CSVHelper.readCSV("pokemon_egg_groups");

        //species_id,egg_group_id
        for(int i = 1; i <= 898; i++)
        {
            final int dex = i;
            List<EggGroup> group = eggCSV.stream()
                    .filter(l -> l[0].equals(dex + "")) //Find specific dex number
                    .map(l -> Integer.parseInt(l[1])) //Map to the Egg Group ID
                    .map(id -> EggGroup.values()[id - 1]) //Transform Egg Group ID to EggGroup Enum Object
                    .collect(Collectors.toList());
            POKEMON_EGG_GROUPS.put(dex, group);
        }
    }

    //Egg Hatch Targets (from CSV)
    public static void createBaseEggHatchTargetsMap()
    {
        List<String[]> hatchCSV = CSVHelper.readCSV("pokemon_species");

        //id,identifier,generation_id,evolves_from_species_id,evolution_chain_id,color_id,shape_id,habitat_id,gender_rate,capture_rate,base_happiness,is_baby,hatch_counter,has_gender_differences,growth_rate_id,forms_switchable,is_legendary,is_mythical,order,conquest_order
        for(int i = 1; i <= 898; i++)
        {
            final int dex = i;
            int target = hatchCSV.stream()
                    .filter(l -> l[0].equals(dex + "")) //Find specific dex number
                    .map(l -> Integer.parseInt(l[12])) //Map to the Hatch Counter
                    .map(count -> count * 257) //Convert Egg Cycles to Steps
                    .collect(Collectors.toList())
                    .get(0);
            POKEMON_BASE_HATCH_TARGETS.put(dex, target);
        }
    }

    //Gender Rates (from CSV)
    public static void createGenderRateMap()
    {
        List<String[]> genderCSV = CSVHelper.readCSV("pokemon_species");

        //id,identifier,generation_id,evolves_from_species_id,evolution_chain_id,color_id,shape_id,habitat_id,gender_rate,capture_rate,base_happiness,is_baby,hatch_counter,has_gender_differences,growth_rate_id,forms_switchable,is_legendary,is_mythical,order,conquest_order
        for(int i = 1; i <= 898; i++)
        {
            final int dex = i;
            int rate = genderCSV.stream()
                    .filter(l -> l[0].equals(dex + "")) //Find specific dex number
                    .map(l -> l[8]) //Map to Gender Rate
                    .map(Integer::parseInt) //Map to Int
                    .collect(Collectors.toList())
                    .get(0);
            POKEMON_GENDER_RATES.put(dex, rate);
        }
    }
}
