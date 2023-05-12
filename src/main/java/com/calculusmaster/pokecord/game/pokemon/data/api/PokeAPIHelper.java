package com.calculusmaster.pokecord.game.pokemon.data.api;

import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;

public class PokeAPIHelper
{
    private static final LinkedHashMap<String, JSONObject> CACHE = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException
    {

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
            return new JSONObject();
        }

        JSONObject json = new JSONObject(response.body());
        CACHE.put(endpoint, json);
        LoggerHelper.time(PokeAPIHelper.class, "PokeAPI Request", timeI, System.currentTimeMillis());
        return json;
    }
}
