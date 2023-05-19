package com.calculusmaster.pokecord.game.pokemon.data.api;

import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class PokeAPIHelper
{
    private static final LinkedHashMap<String, JSONObject> CACHE = new LinkedHashMap<>();
    public static final EnumSet<Ability> errors = EnumSet.noneOf(Ability.class);

    public static void main(String[] args) throws IOException
    {
//        try (ExecutorService pool = Executors.newFixedThreadPool(10)) {
//        Arrays.stream(Ability.values()).forEach(a -> pool.submit(() -> {
//            try {
//                createAbilityJSON(a);
//            } catch (IOException e) {
//                errors.add(a);
//            }
//        })); }
//
//        System.out.println("Errors: " + errors.stream().map(Ability::toString).collect(Collectors.joining("\n")));
//
//        List<String> list = new ArrayList<>();
//
//        list.add("dex,name,data");
//        Arrays.stream(PokemonEntity.values()).forEach(e -> list.add("%s,%s,%s".formatted(e.getDex(), e.getJSONFileName().substring(e.getJSONFileName().indexOf("_") + 1), e.getImageName())));
//
//        try(FileWriter w = new FileWriter("pokemon.csv"))
//        {
//            w.write(String.join("\n", list));
//        }

        MoveEntity.init();

        System.out.println("-----------------Moves-----------------");
        Arrays.stream(MoveEntity.values()).filter(e -> !e.isZMove() && !e.isMaxMove() && !Move.WIP_MOVES.contains(e) && !Move.isImplemented(e)).forEach(System.out::println);

        System.out.println("-----------------Abilities-----------------");
        Arrays.stream(Ability.values()).filter(e -> !Ability.IMPLEMENTED.contains(e)).forEach(System.out::println);
    }

    private static void createAbilityJSON(Ability a) throws IOException
    {
        String key = a.toString().toLowerCase().replaceAll("_", "-");
        JSONObject sourceData = GET("ability/" + key);

        if(sourceData == null)
        {
            errors.add(a);
            return;
        }

        Document abilityData = new Document(new LinkedHashMap<>());

        for(Object o : sourceData.getJSONArray("names"))
        {
            JSONObject d = (JSONObject)o;
            if(d.getJSONObject("language").getString("name").equals("en")) {abilityData.append("name", d.getString("name")); break;}
        }

        List<String> effects = new ArrayList<>();
        for(Object o : sourceData.getJSONArray("effect_entries"))
        {
            JSONObject d = (JSONObject)o;
            if(d.getJSONObject("language").getString("name").equals("en")) effects.add(d.getString("short_effect"));
        }
        abilityData.append("effectEntries", effects);

        List<String> flavorTexts = new ArrayList<>();
        for(Object o : sourceData.getJSONArray("flavor_text_entries"))
        {
            JSONObject d = (JSONObject)o;
            if(d.getJSONObject("language").getString("name").equals("en")) flavorTexts.add(d.getString("flavor_text"));
        }
        abilityData.append("flavorTextEntries", flavorTexts.stream().map(s -> s.replaceAll("POKéMON", "Pokémon").replaceAll("\n", " ")).distinct().toList());

        String fileName = Arrays.stream(a.toString().split("_")).map(Global::normalize).collect(Collectors.joining("_"));
        try(FileWriter w = new FileWriter("/Users/saptarshimallick/Downloads/abilities/" + fileName + ".json"))
        {
            w.write(abilityData.toJson(JsonWriterSettings.builder().indent(true).indentCharacters("    ").build()));
        }
    }

    private static void timed(Runnable r)
    {
        long i = System.nanoTime();
        r.run();
        System.out.println((System.nanoTime() - i) / 1E9);
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
            return null;
        }

        JSONObject json = new JSONObject(response.body());
        CACHE.put(endpoint, json);
        LoggerHelper.time(PokeAPIHelper.class, "PokeAPI Request", timeI, System.currentTimeMillis());
        return json;
    }
}
