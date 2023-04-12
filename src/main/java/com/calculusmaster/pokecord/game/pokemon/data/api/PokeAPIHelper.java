package com.calculusmaster.pokecord.game.pokemon.data.api;

import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.evolution.FormRegistry;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class PokeAPIHelper
{
    private static final LinkedHashMap<String, JSONObject> CACHE = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException
    {
        int count = 1281; //Max: 1281
        //PokeAPIHelper.createPokemonEntityEnum(count);

        //Formatted JSON printing
//        JSONObject o = GET("pokemon/miraidon");
//        System.out.println(o.toString(4));


//        checkPokemonWithMissingImageFiles(true);
//        checkPokemonWithMissingImageFiles(false);
//        checkUnusedImageFiles(false);
//
//        if(true) return;
//        PokemonEntity e = PokemonEntity.values()[new Random().nextInt(PokemonEntity.values().length)];
//        System.out.println("Test: " + e.getAPIID());
//        createPokemonDataJSON(e);
//        //System.out.println(q.createDataJSON().toJson(JsonWriterSettings.builder().indent(true).indentCharacters("    ").build()));
//        System.out.println(e.getName());
//        System.out.println(q.getMoves().toJson(JsonWriterSettings.builder().indent(true).indentCharacters("    ").build()));
        //Remaining Abilities
//        List<String> missing = new ArrayList<>();
//        GET("ability?limit=400").getJSONArray("results").forEach(o -> {
//            String rawName = ((JSONObject)o).getString("name");
//
//            if(!GET("ability/" + rawName).getBoolean("is_main_series")) return;
//
//            String name = rawName.replaceAll("-", "_").toUpperCase();
//            if(Ability.cast(name) == null) missing.add(name);
//        });
//
//        missing.stream().map(s -> s + ",").forEach(System.out::println);

        //createMoveEntityEnumAndJSONs(1, 2); //902 is the max for now
        //checkMissingMoveDataJSONFiles();
        //MoveEntity.init();
        //System.out.println(MoveEntity.values()[new Random().nextInt(MoveEntity.values().length)].data());

        ////createPokemonDataJSONs();
        //List<PokemonEntity> missingHappiness = List.of(PokemonEntity.WYRDEER, PokemonEntity.KLEAVOR, PokemonEntity.URSALUNA, PokemonEntity.BASCULEGION_MALE, PokemonEntity.BASCULEGION_FEMALE, PokemonEntity.SNEASLER, PokemonEntity.OVERQWIL, PokemonEntity.ENAMORUS_THERIAN, PokemonEntity.ENAMORUS);
        //missingHappiness.forEach(e -> {createPokemonDataJSON(e);});

        //createPokemonDataJSON(PokemonEntity.CRYOGONAL);

//        PokemonEntity.init();
//        System.out.println(PokemonEntity.values()[new Random().nextInt(PokemonEntity.values().length)].data());


        //Mongo.PlayerData.findOneAndUpdate(Filters.eq("playerID", "309135641453527040"), , new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER))

//        long i = System.currentTimeMillis();
//        Mongo.PlayerData.find(Filters.eq("playerID", "309135641453527040")).projection(Projections.fields(Projections.include("team"))).first();
//        System.out.println((System.currentTimeMillis() - i) / 1000.);

//        FormRegistry.init();
//        Arrays.stream(PokemonEntity.values()).filter(FormRegistry::hasFormData).forEach(e -> {
//            FormRegistry.FormData d = FormRegistry.getFormData(e);
//            if(e.isNotSpawnable() && d.isSwitchable()) System.out.println("Checking " + e.getName() + " – " + d.getDefaultForm().getName() + " - Others: " + d.getForms().stream().map(PokemonEntity::getName).collect(Collectors.joining(", ")) + ", Switchable: " + d.isSwitchable());
//        });
//        MoveEntity.init();
//        EvolutionRegistry.init();
//        Arrays.stream(PokemonEntity.values()).filter(EvolutionRegistry::hasEvolutionData).forEach(e -> {
//            List<EvolutionData> d = EvolutionRegistry.getEvolutionData(e);
//            if(d.size() > 1 && d.stream().anyMatch(ed -> ed.getTriggers().stream().anyMatch(tr -> tr instanceof TradeEvoTrigger || tr instanceof TradeWithEvoTrigger))) System.out.println(e.getName() + " - " + d.stream().map(data -> data.getTarget().getName() + ": " + data.getTriggers().stream().map(EvolutionTrigger::getDescription).collect(Collectors.joining(", "))).collect(Collectors.joining(" -----|----- ")));
//        });

//        Mongo.PlayerData.find(Filters.eq("playerID", "309135641453527040")).first();
//
//        timed(() -> {
//            System.out.println("Old");
//            List<String> uuids = Mongo.PlayerData.find(Filters.eq("playerID", "309135641453527040")).first().getList("pokemon", String.class);
//            uuids.forEach(s -> Mongo.PokemonData.find(Filters.eq("UUID", "1v788ickww9iyrxxgdzss7k2")).first());
//        });
//
//        timed(() -> {
//            System.out.println("New");
//            List<String> uuids = Mongo.PlayerData.find(Filters.eq("playerID", "309135641453527040")).projection(Projections.fields(Projections.include("pokemon"))).first().getList("pokemon", String.class);
//            uuids.forEach(s -> Mongo.PokemonData.find(Filters.eq("UUID", "1v788ickww9iyrxxgdzss7k2")).projection(Projections.include("ivs", "evs")).first());
//        });

//        LocalDateTime time = Global.timeNow().plusHours(12);
//        int targetHour = 19;
//
//        //Remove minutes, seconds, nanos
//        time = time.minusSeconds(time.getSecond()).minusMinutes(time.getMinute()).minusNanos(time.getNano());
//
//        if(time.getHour() > targetHour) time = time.plusHours(24 - time.getHour());
//        time = time.plusHours(targetHour);
//        System.out.println(time);
//
//        long epoch = time.toEpochSecond(ZoneOffset.of("-7"));
//        System.out.println(LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.of("-7")));
//
//        System.out.println(LocalDateTime.ofEpochSecond(1680832800, 0, ZoneOffset.of("-7")));

        FormRegistry.init();

        Arrays.stream(PokemonEntity.values()).filter(FormRegistry::hasFormData).filter(PokemonEntity::isNotSpawnable).forEach(System.out::println);
    }

    private static void timed(Runnable r)
    {
        long i = System.currentTimeMillis();
        r.run();
        System.out.println((System.currentTimeMillis() - i) / 1000.);
    }

    private static void checkMissingAndUnusedImageFiles()
    {
        String normalFolder = "/Users/saptarshimallick/Desktop/Pokemon Images/Pokemon Normal/";
        String shinyFolder = "/Users/saptarshimallick/Desktop/Pokemon Images/Pokemon Shiny/";

        List<String> allNormalFiles = new ArrayList<>(List.of(new File(normalFolder).list()));
        List<String> allShinyFiles = new ArrayList<>(List.of(new File(shinyFolder).list()));

        allNormalFiles.remove(".DS_Store");
        allShinyFiles.remove(".DS_Store");

        for(PokemonEntity entity : PokemonEntity.values())
        {
            String normal = Pokemon.getImage(entity, false, null, null).substring("/data/images/normal".length());
            String shiny = Pokemon.getImage(entity, true, null, null).substring("/data/images/shiny".length());

            //Check if the files are found
            File normalFile = new File(normalFolder + normal);
            File shinyFile = new File(shinyFolder + shiny);

            boolean normalFileFound = normalFile.exists();
            boolean shinyFileFound = shinyFile.exists();

            if(!normalFileFound) System.out.println(entity + " ----- Normal Image Missing ----- " + normal);
            else allNormalFiles.remove(normal);
            if(!shinyFileFound) System.out.println(entity + " ----- Shiny Image Missing ----- " + shiny);
            else allShinyFiles.remove(shiny);

            if(!normalFileFound || !shinyFileFound) System.out.println();
        }

        System.out.println("---------- Unused Normal Files ----------\n" + String.join("\n", allNormalFiles));
        System.out.println();
        System.out.println("---------- Unused Shiny Files ----------\n" + String.join("\n", allShinyFiles));
    }

    public static void createPokemonDataJSONs()
    {
        List<String> errors = new ArrayList<>();
        for(PokemonEntity entity : PokemonEntity.values())
        {
            try { PokeAPIHelper.createPokemonDataJSON(entity); }
            catch(Exception e) { errors.add(entity.getAPIID() + "(" + entity + ")" + " –––– Error creating JSON: " + e.getMessage()); }
        }

        System.out.println("---------------------------------\n\n\n\n\n");
        System.out.println("Errors: " + errors.size());
        errors.forEach(System.out::println);
    }

    private static void createPokemonDataJSON(PokemonEntity entity) throws IOException
    {
        PokeAPIQuery q = new PokeAPIQuery(entity);

        Document j = q.createDataJSON();

        String dir = "/Users/saptarshimallick/Desktop/pokemonJSONs/";
        String fName = entity.getJSONFileName() + ".json";
        File f = new File(dir + fName);

        FileWriter w = new FileWriter(f);
        w.write(j.toJson(JsonWriterSettings.builder().indent(true).indentCharacters(" ".repeat(4)).build()));
        w.close();
    }

    private static void checkMissingMoveDataJSONFiles()
    {
        for(MoveEntity moveEntity : MoveEntity.values())
        {
            if(moveEntity.isMaxMove() || moveEntity.isZMove()) continue;
            File f = new File("/Users/saptarshimallick/Desktop/moveJSONs/" + moveEntity.getJSONFileName() + ".json");
            if(!f.exists()) System.out.println("Missing: " + moveEntity.getJSONFileName() + " for " + moveEntity);
        }
    }

    private static void checkUnusedImageFiles(boolean shiny)
    {
        String homeDirectory = "/Users/saptarshimallick/Desktop/Pokemon Images/";

        String dir = homeDirectory + "/Pokemon " + (shiny ? "Shiny" : "Normal") + "/";

        Set<String> usedFiles = new HashSet<>();
        Arrays.stream(PokemonEntity.values()).forEach(entity -> {
            String fName = entity.getImageName(shiny);
            File normalFile = new File(dir + fName + ".png");
            if(normalFile.exists()) usedFiles.add(normalFile.getName());
        });

        Arrays.stream(new File(dir).list()).forEach(f -> {
            if(usedFiles.stream().noneMatch(s -> s.equalsIgnoreCase(f))) System.out.println("File Not Used: " + f);
        });
    }

    private static void checkPokemonWithMissingImageFiles(boolean shiny)
    {
        String homeDirectory = "/Users/saptarshimallick/Desktop/Pokemon Images/";
        Arrays.stream(PokemonEntity.values()).forEach(entity -> {
            File normalFile = new File(homeDirectory + "/Pokemon " + (shiny ? "Shiny" : "Normal") + "/" + entity.getImageName(false) + ".png");
            if(!normalFile.exists()) LoggerHelper.error(PokeAPIHelper.class, (shiny ? "Shiny" : "Normal") + " Image NOT FOUND. For: " + entity.getName() + " – '" + entity.getImageName(false) + "'.");
        });
    }

    private static void createMoveDataJSON(JSONObject moveData) throws IOException
    {
        Document output = new Document(new LinkedHashMap<>());

        //Name
        String name = "";
        for(Object o : moveData.getJSONArray("names"))
        {
            JSONObject langData = (JSONObject)o;
            if(langData.getJSONObject("language").getString("name").equals("en")) name = langData.getString("name");
        }

        if(name.isEmpty()) throw new IllegalStateException("Move (" + moveData.getString("name") + ")" + " has no English name.");
        output.append("name", name);

        //Order (ID)
        output.append("order", moveData.getInt("id"));

        //Type
        output.append("type", moveData.getJSONObject("type").getString("name").toUpperCase());

        //Category
        output.append("category", moveData.getJSONObject("damage_class").getString("name").toUpperCase());

        //Priority
        output.append("priority", moveData.getInt("priority"));

        //Power
        boolean nullPower = moveData.get("power") == null || moveData.get("power").toString().equals("null");
        output.append("power", nullPower ? 0 : moveData.getInt("power"));

        //Accuracy
        boolean nullAccuracy = moveData.get("accuracy") == null || moveData.get("accuracy").toString().equals("null");
        output.append("accuracy", nullAccuracy ? 100 : moveData.getInt("accuracy"));

        //PP
        boolean nullPP = moveData.get("pp") == null || moveData.get("pp").toString().equals("null");
        output.append("pp", nullPP ? 0 : moveData.getInt("pp"));

        //Null Attributes
        output.append("nullAttributes", new Document(new LinkedHashMap<>())
                .append("powerExists", !nullPower)
                .append("accuracyExists", !nullAccuracy)
                .append("ppExists", !nullPP));

        //Meta - Start
        Document meta = new Document(new LinkedHashMap<>());
        JSONObject metaJSON = moveData.get("meta") == null || !moveData.has("meta") || moveData.get("meta").toString().equals("null") ? null : moveData.getJSONObject("meta");

        //Target (not in meta object)
        meta.append("target", moveData.getJSONObject("target").getString("name").replaceAll("-", "_").toUpperCase());

        //Ailment
        meta.append("ailment", metaJSON == null ? "" : metaJSON.getJSONObject("ailment").getString("name").replaceAll("-", "_").toUpperCase());

        //Ailment Chance
        meta.append("ailmentChance", metaJSON == null ? 0 : metaJSON.getInt("ailment_chance"));

        //Crit Rate
        meta.append("critRate", metaJSON == null ? 0 : metaJSON.getInt("crit_rate"));

        //Drain
        meta.append("drain", metaJSON == null ? 0 : metaJSON.getInt("drain"));

        //Flinch Chance
        meta.append("flinchChance", metaJSON == null ? 0 : metaJSON.getInt("flinch_chance"));

        //Healing
        meta.append("healing", metaJSON == null ? 0 : metaJSON.getInt("healing"));

        //Max Hits
        try { meta.append("maxHits", metaJSON == null ? 0 : metaJSON.getInt("max_hits")); }
        catch(Exception e) { meta.append("maxHits", 0); }

        //Max Turns
        try { meta.append("maxTurns", metaJSON == null ? 0 : metaJSON.getInt("max_turns")); }
        catch(Exception e) { meta.append("maxTurns", 0); }

        //Min Hits
        try { meta.append("minHits", metaJSON == null ? 0 : metaJSON.getInt("min_hits")); }
        catch(Exception e) { meta.append("minHits", 0); }

        //Min Turns
        try { meta.append("minTurns", metaJSON == null ? 0 : metaJSON.getInt("min_turns")); }
        catch(Exception e) { meta.append("minTurns", 0); }

        //Stat Chance
        meta.append("statChance", metaJSON == null ? 0 : metaJSON.getInt("stat_chance"));

        //Stat Changes (not in meta object)
        List<Document> statChanges = new ArrayList<>();
        for(Object o : moveData.getJSONArray("stat_changes"))
        {
            JSONObject statChangeJSON = (JSONObject)o;
            Document statChangeData = new Document(new LinkedHashMap<>());

            String statName = switch(statChangeJSON.getJSONObject("stat").getString("name"))
                    {
                        case "hp" -> "HP";
                        case "attack" -> "ATK";
                        case "defense" -> "DEF";
                        case "special-attack" -> "SPATK";
                        case "special-defense" -> "SPDEF";
                        case "speed" -> "SPD";
                        case "accuracy" -> "ACCURACY";
                        case "evasion" -> "EVASION";
                        case "crit-chance" -> "CRIT_CHANCE";
                        default -> throw new IllegalStateException("Invalid stat name: " + statChangeJSON.getJSONObject("stat").getString("name"));
                    };

            statChangeData.append("stat", statName);
            statChangeData.append("change", statChangeJSON.getInt("change"));

            statChanges.add(statChangeData);
        }
        meta.append("statChanges", statChanges);

        //Effect Chance
        meta.append("effectChance", metaJSON == null || !metaJSON.has("effect_chance") || metaJSON.get("effect_chance") == null ? 0 : metaJSON.getInt("effect_chance"));

        output.append("meta", meta);
        //Meta - End

        //Effect Entries
        List<String> effectEntries = new ArrayList<>();
        for(Object o : moveData.getJSONArray("effect_entries"))
        {
            JSONObject effectJSON = (JSONObject)o;
            if(effectJSON.getJSONObject("language").getString("name").equals("en"))
                effectEntries.add(effectJSON.getString("effect")
                        .replaceAll("\\n+", " ")
                        .replaceAll("\u00ad ", "")
                        .replaceAll("\\s+", " ")
                );
        }

        output.append("effectEntries", effectEntries);

        //Flavor Text Entries
        List<String> flavorTextEntries = new ArrayList<>();
        for(Object o : moveData.getJSONArray("flavor_text_entries"))
        {
            JSONObject flavorTextJSON = (JSONObject)o;
            if(flavorTextJSON.getJSONObject("language").getString("name").equals("en"))
                flavorTextEntries.add(flavorTextJSON.getString("flavor_text")
                        .replaceAll("\\n+", " ")
                        .replaceAll("\u00ad ", "")
                        .replaceAll("\\s+", " ")
                        .trim()
                );
        }
        flavorTextEntries = flavorTextEntries.stream().distinct().toList();

        output.append("flavorTextEntries", flavorTextEntries);

        //Write JSON

        String dir = "/Users/saptarshimallick/Desktop/moveJSONs/";
        String fName = Global.normalize(moveData.getString("name").replaceAll("-", " ")).replaceAll(" ", "_") + ".json";
        FileWriter writer = new FileWriter(dir + fName);
        writer.write(output.toJson(JsonWriterSettings.builder().indent(true).indentCharacters(" ".repeat(4)).build()));
        writer.close();
    }

    public static void createMoveEntityEnum(int start, int end) throws IOException
    {
        List<String> moveEnumNames = new ArrayList<>();
        for(int i = start; i <= end; i++)
        {
            JSONObject moveData = GET("move/" + i);

            //Name & ID for sorting
            String enumName = moveData.getString("name").replaceAll("-", "_").toUpperCase();
            moveEnumNames.add("%s\t(%s, %s),".formatted(enumName, moveData.getString("name"), moveData.getInt("id")));

            //Create JSON
            try { PokeAPIHelper.createMoveDataJSON(moveData); }
            catch(Exception e)
            {
                System.out.println("Error creating JSON for move " + moveData.getInt("id") + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        FileWriter w = new FileWriter("moves.txt");
        w.write(String.join("\n", moveEnumNames));
        w.close();

        //Temporary, fixing the quotes
        Scanner sc = new Scanner(new File("moves.txt"));

        List<String> readLines = new ArrayList<>();
        while(sc.hasNextLine())
        {
            String s = sc.nextLine().trim();

            int i1 = s.indexOf("("); //First index of (
            int i2 = s.indexOf(","); //First index of ,

            String s2 = s.substring(0, i1 + 1) + "\"" + s.substring(i1 + 1, i2) + "\"" + s.substring(i2);

            readLines.add(s2);
        }

        FileWriter w2 = new FileWriter("moves.txt");
        w2.write(String.join("\n", readLines));
        w2.close();
    }

    public static void createMoveEntityJSONs(int start, int end)
    {
        List<Integer> ignore = new ArrayList<>();
        Arrays.stream(MoveEntity.values()).filter(me -> me.isZMove() || me.isMaxMove()).forEach(me -> ignore.add(me.data().getOrder()));

        List<String> errors = new ArrayList<>();
        for(int i = start; i <= end; i++)
        {
            if(ignore.contains(i)) continue;

            JSONObject moveData = GET("move/" + i);
            try { PokeAPIHelper.createMoveDataJSON(moveData); }
            catch(Exception e) { errors.add(moveData.getInt("id") + " –––– Error creating JSON: " + e.getMessage()); }
        }

        System.out.println("---------------------------------\n\n\n\n\n");
        System.out.println("Errors: " + errors.size());
        errors.forEach(System.out::println);
    }

    public static void createPokemonEntityEnum(int count) throws IOException
    {
        JSONObject mainListJSON = GET("pokemon?offset=0&limit=" + count);
        List<String> pokemon = new ArrayList<>();

        for(Object res : mainListJSON.getJSONArray("results"))
        {
            JSONObject result = (JSONObject)res;
            pokemon.add(result.getString("name"));
        }

        Map<String, Integer> orders = new HashMap<>();
        Map<String, JSONObject> data = new HashMap<>();

        pokemon.forEach(name -> {
            JSONObject pokemonData = GET("pokemon/" + name);
            orders.put(name, pokemonData.getInt("order"));
            data.put(name, pokemonData);
        });

        pokemon.sort(Comparator.comparingInt(orders::get));

        CSVHelper.init();
        PokemonRarity.init();
        JSONArray nationalDex = GET("pokedex/national").getJSONArray("pokemon_entries");
        List<String> lines = new ArrayList<>();

        pokemon.forEach(name -> {
            JSONObject d = data.get(name);

            String enumName = name.replaceAll("-", "_").toUpperCase();
            String pokeName = Global.normalize(d.getString("name").replaceAll("-", " "));

            String species = d.getJSONObject("species").getString("name");
            int dex = -1;
            for(Object o : nationalDex)
            {
                JSONObject dexEntry = (JSONObject)o;
                if(dexEntry.getJSONObject("pokemon_species").getString("name").equals(species))
                {
                    dex = dexEntry.getInt("entry_number");
                    break;
                }
            }

            PokemonRarity.Rarity rarity = null;

            lines.add("%s\t(\"%s\", \"%s\", %s, %s),".formatted(enumName, name, pokeName, dex == -1 ? "INV" : dex, rarity == null ? "NULL" : rarity.toString()));
        });

        FileWriter w = new FileWriter("pokemon.txt");
        w.write(String.join("\n", lines));
        w.close();

        //temporary, fixing the order
        Scanner sc = new Scanner(new File("pokemon.txt"));

        List<String> readLines = new ArrayList<>();
        while(sc.hasNextLine()) readLines.add(sc.nextLine().trim());

        readLines.sort((l1, l2) -> {
            int i1 = Integer.parseInt(l1.split(",")[2].trim());
            int i2 = Integer.parseInt(l2.split(",")[2].trim());
            return i1 - i2;
        });

        FileWriter w2 = new FileWriter("pokemon.txt");
        w2.write(String.join("\n", readLines));
        w2.close();
    }

    public static JSONObject GET(String endpoint)
    {
        if(CACHE.containsKey(endpoint)) return CACHE.get(endpoint);

        String URL = "https://pokeapi.co/api/v2/" + endpoint;

        long timeI = System.currentTimeMillis();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).build();

        HttpResponse<String> response;
        try { response = client.send(request, HttpResponse.BodyHandlers.ofString()); }
        catch(IOException | InterruptedException e)
        {
            LoggerHelper.error(PokeAPIHelper.class, "PokeAPI GET Request failed. URL: " + URL);
            e.printStackTrace();
            return new JSONObject();
        }

        JSONObject json = new JSONObject(response.body());
        CACHE.put(endpoint, json);
        LoggerHelper.time(PokeAPIHelper.class, "PokeAPI Request", timeI, System.currentTimeMillis());
        return json;
    }
}
