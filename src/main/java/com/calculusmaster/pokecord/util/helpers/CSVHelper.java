package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.Pokecord;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CSVHelper
{
    public static List<String[]> CSV_POKEMON_DATA_STANDARD;
    public static List<String[]> CSV_POKEMON_DATA_IMAGES;
    public static List<String[]> CSV_POKEMON_DATA_STATS;
    public static List<String[]> CSV_POKEMON_DATA_EFFORT_VALUES;
    public static List<String[]> CSV_POKEMON_DATA_FORMS_MEGAS;
    public static List<String[]> CSV_POKEMON_DATA_BREEDING;
    public static List<String[]> CSV_POKEMON_DATA_EVOLUTIONS;
    public static List<String[]> CSV_POKEMON_DATA_MOVES;
    public static List<String[]> CSV_POKEMON_DATA_DESCRIPTIONS;
    public static List<String[]> CSV_POKEMON_DATA_RARITIES;

    public static List<String[]> CSV_MOVE_DATA;

    public static void init()
    {
        CSV_POKEMON_DATA_STANDARD = CSVHelper.readDataCSV("standard");
        CSV_POKEMON_DATA_IMAGES = CSVHelper.readDataCSV("images");
        CSV_POKEMON_DATA_STATS = CSVHelper.readDataCSV("stats");
        CSV_POKEMON_DATA_EFFORT_VALUES = CSVHelper.readDataCSV("effort_values");
        CSV_POKEMON_DATA_FORMS_MEGAS = CSVHelper.readDataCSV("forms_megas");
        CSV_POKEMON_DATA_BREEDING = CSVHelper.readDataCSV("breeding");
        CSV_POKEMON_DATA_EVOLUTIONS = CSVHelper.readDataCSV("evolutions");
        CSV_POKEMON_DATA_MOVES = CSVHelper.readDataCSV("moves");
        CSV_POKEMON_DATA_DESCRIPTIONS = CSVHelper.readDataCSV("descriptions");
        CSV_POKEMON_DATA_RARITIES = CSVHelper.readDataCSV("rarities");

        CSV_MOVE_DATA = CSVHelper.readCSV("/data_csv/move_data");
    }

    private static List<String[]> readDataCSV(String fileName)
    {
        return CSVHelper.readCSV("/data_csv/pokemon_data_" + fileName);
    }

    public static List<String[]> readPokemonCSV(String fileName)
    {
        return CSVHelper.readCSV("/csv/" + fileName);
    }

    private static List<String[]> readCSV(String path)
    {
        path = path + ".csv";

        try
        {
            InputStream fileStream = Objects.requireNonNull(Pokecord.class.getResourceAsStream(path));
            CSVReader reader = new CSVReader(new InputStreamReader(fileStream));

            List<String[]> lines = reader.readAll();
            lines.remove(0);

            return lines;
        }
        catch (IOException | CsvException e)
        {
            e.printStackTrace();

            LoggerHelper.error(CSVHelper.class, "Failed to read CSV file \"" + path + "\"");

            return new ArrayList<>();
        }
    }
}
