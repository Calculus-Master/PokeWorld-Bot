package com.calculusmaster.pokecord.util.helpers;

import ch.qos.logback.core.util.FileUtil;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.PrivateInfo;
import com.mongodb.client.model.Filters;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bson.Document;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CSVHelper
{
    private static List<String[]> pokemonInfo = new ArrayList<>();
    private static List<String[]> pokemonSpeciesNames = new ArrayList<>();
    private static List<String[]> pokemonSpeciesInfo = new ArrayList<>();
    private static List<String[]> pokemonTypeInfo = new ArrayList<>();
    private static List<String[]> pokemonTypeNames = new ArrayList<>();
    private static List<String[]> pokemonStatInfo = new ArrayList<>();
    private static List<String[]> pokemonMoveInfo = new ArrayList<>();
    private static List<String[]> pokemonMoveNames = new ArrayList<>();
    private static List<String[]> pokemonAbilityInfo = new ArrayList<>();
    private static List<String[]> pokemonAbilityNames = new ArrayList<>();

    private static final String CSV_PATH = PrivateInfo.CSV_PATH;
    private static final String WRITE_PATH = PrivateInfo.OUTPUT_PATH;

    private static Map<Integer, String> NORMAL_URL = new HashMap<>();
    private static Map<Integer, String> SHINY_URL = new HashMap<>();

    private static final String[] keys = {"name", "fillerinfo", "dex", "type", "evolutions", "evolutionsLVL", "forms", "mega", "stats", "ev", "moves", "movesLVL", "movesTM", "movesTR", "abilities", "growthrate", "exp", "normalURL", "shinyURL"};

    public static void main(String[] args) throws IOException, CsvException
    {
        //Read data from CSVs into Objects
        createCSVLists();

        String[] fileNames = new File(PrivateInfo.JSON_SRC_PATH).list();

        File f;
        Document d;
        JSONObject j;

        List<Integer> skip = Arrays.asList(888, 889, 890, 894, 895);

        //Write files
        for(int i = 810; i <= 898; i++)
        {
            if(!skip.contains(i))
            {
                try
                {
                    for (String name : fileNames)
                    {
                        if(name.contains(i + "") && name.charAt(name.indexOf(i + "") + 1) != '.')
                        {
                            System.out.println("Name: " + name);
                            f = new File(PrivateInfo.JSON_SRC_PATH + name);
                            j = new JSONObject(new JSONTokener(new FileInputStream(f)));
                            NORMAL_URL.put(i, j.getString("normalURL"));
                            SHINY_URL.put(i, j.getString("shinyURL"));
                        }
                    }
                }
                catch (FileNotFoundException e) { e.printStackTrace(); }

                writePokemonJSONFile(i);
            }
        }

//        Mongo.PerformanceData.deleteMany(Filters.exists("name"));
//
//        List<String> moves = new ArrayList<>(Arrays.asList("Healing Wish", "Misty Terrain", "Teleport", "Double Team", "Wish", "Magical Leaf", "Draining Kiss", "Fury Cutter", "Retaliate", "Clanging Scales", "Bide", "Belly Drum", "Sky Uppercut", "Dragon Tail", "Accelerock", "Sand Attack", "Quick Attack", "Howl", "Odor Sleuth", "Stealth Rock", "Rock Climb"));
//
//        Move.init();
//
//        for(int i = 0; i < moves.size(); i++)
//        {
//            if(Move.isMove(moves.get(i)))
//            {
//                System.out.println("Removed " + moves.get(i));
//                moves.remove(i);
//                i--;
//            }
//        }
//
//        for(String s : moves) Mongo.PerformanceData.insertOne(getMoveData(s));
    }

    private static void createCSVLists() throws IOException, CsvException
    {
        pokemonInfo = new CSVReader(new FileReader(CSV_PATH + "pokemon.csv")).readAll();
        pokemonInfo.remove(0);

        pokemonSpeciesNames = new CSVReader(new FileReader(CSV_PATH + "pokemon_species_names.csv")).readAll();
        pokemonSpeciesNames.remove(0);

        pokemonSpeciesInfo = new CSVReader(new FileReader(CSV_PATH + "pokemon_species.csv")).readAll();
        pokemonSpeciesInfo.remove(0);

        pokemonTypeInfo = new CSVReader(new FileReader(CSV_PATH + "pokemon_types.csv")).readAll();
        pokemonTypeInfo.remove(0);

        pokemonTypeNames = new CSVReader(new FileReader(CSV_PATH + "type_names.csv")).readAll();
        pokemonTypeNames.remove(0);

        pokemonStatInfo = new CSVReader(new FileReader(CSV_PATH + "pokemon_stats.csv")).readAll();
        pokemonStatInfo.remove(0);

        pokemonMoveInfo = new CSVReader(new FileReader(CSV_PATH + "pokemon_moves.csv")).readAll();
        pokemonMoveInfo.remove(0);

        pokemonMoveNames = new CSVReader(new FileReader(CSV_PATH + "moves.csv")).readAll();
        pokemonMoveNames.remove(0);

        pokemonAbilityInfo = new CSVReader(new FileReader(CSV_PATH + "pokemon_abilities.csv")).readAll();
        pokemonAbilityInfo.remove(0);

        pokemonAbilityNames = new CSVReader(new FileReader(CSV_PATH + "abilities.csv")).readAll();
        pokemonAbilityNames.remove(0);
    }

    private static Document getMoveData(String name)
    {
        String[] line = pokemonMoveNames.stream().filter(l -> Global.normalCase(l[1].replaceAll("-", " ")).equals(name)).collect(Collectors.toList()).get(0);

        String type = typeFromID(line[3]);
        String category = switch(Integer.parseInt(line[9])) {
            case 1 -> "Status";
            case 2 -> "Physical";
            case 3 -> "Special";
            default -> null;
        };

        Document move = new Document()
                .append("name", name)
                .append("type", Global.normalCase(type))
                .append("category", category)
                .append("power", line[4].equals("") ? 0 : Integer.parseInt(line[4]))
                .append("accuracy", line[6].equals("") ? 100 : Integer.parseInt(line[6]))
                .append("info", "");

        return move;
    }

    private static Document getPokemonData(int dex)
    {
        Document data = new Document();

        if(pokemonInfo.stream().filter(l -> l[0].equals("" + dex)).count() == 0)
        {
            System.out.println("This dex number failed: " + dex);
            return null;
        }

        //id,identifier,species_id,height,weight,base_experience,order,is_default
        String[] pokemonCSVLine = pokemonInfo.stream().filter(l -> l[0].equals(dex + "")).collect(Collectors.toList()).get(0);

        //pokemon_species_id,local_language_id,name,genus
        //local_language_id = 9 for English (index 8 since there are 9 entries per dex number)
        String[] pokemonSpeciesNameCSVLine = pokemonSpeciesNames.stream().filter(l -> l[0].equals(dex + "")).filter(l -> l[1].equals("9")).collect(Collectors.toList()).get(0);

        //id,identifier,generation_id,evolves_from_species_id,evolution_chain_id,color_id,shape_id,habitat_id,gender_rate,capture_rate,base_happiness,is_baby,hatch_counter,has_gender_differences,growth_rate_id,forms_switchable,is_legendary,is_mythical,order,conquest_order
        String[] pokemonSpeciesCSVLine = pokemonSpeciesInfo.stream().filter(l -> l[0].equals("" + dex)).collect(Collectors.toList()).get(0);

        //pokemon_id,type_id,slot
        List<String[]> pokemonTypeCSVLine = pokemonTypeInfo.stream().filter(l -> l[0].equals("" + dex)).collect(Collectors.toList());

        //pokemon_id,stat_id,base_stat,effort
        List<String[]> pokemonStatLines = pokemonStatInfo.stream().filter(l -> l[0].equals("" + dex)).collect(Collectors.toList());

        //pokemon_id,version_group_id,move_id,pokemon_move_method_id,level,order
        //Version group 18 is Ultra Sun, 20 is Sword
        List<String[]> pokemonTMMoveLines = pokemonMoveInfo.stream().filter(l -> l[0].equals("" + dex)).filter(l -> l[3].equals("4")).filter(l -> l[1].equals("18")).collect(Collectors.toList());
        List<String[]> pokemonTRMoveLines = pokemonMoveInfo.stream().filter(l -> l[0].equals("" + dex)).filter(l -> l[3].equals("4")).filter(l -> l[1].equals("20")).collect(Collectors.toList());
        //USUM: List<String[]> pokemonLVLMoveLines = pokemonMoveInfo.stream().filter(l -> l[0].equals("" + dex)).filter(l -> l[3].equals("1")).filter(l -> l[1].equals("18")).collect(Collectors.toList());
        //SS:
        List<String[]> pokemonLVLMoveLines = pokemonMoveInfo.stream().filter(l -> l[0].equals("" + dex)).filter(l -> l[3].equals("1")).filter(l -> l[1].equals("20")).collect(Collectors.toList());

        //pokemon_id,ability_id,is_hidden,slot
        List<String[]> pokemonAbilityLines = pokemonAbilityInfo.stream().filter(l -> l[0].equals("" + dex)).collect(Collectors.toList());

        String[] movesLVL = movesLVL(pokemonLVLMoveLines);

        data.append(keys[0], Global.normalCase(pokemonCSVLine[1]))
                .append(keys[1], pokemonSpeciesNameCSVLine[3].replaceAll("-", " ").replaceAll("é", "e") + "-" + htwt(pokemonCSVLine[3]) + "-" + htwt(pokemonCSVLine[4]))
                .append(keys[2], dex)
                .append(keys[3], type(pokemonTypeCSVLine))
                .append(keys[4], "[]")
                .append(keys[5], "[]")
                .append(keys[6], "[]")
                .append(keys[7], "[]")
                .append(keys[8], stats(pokemonStatLines))
                .append(keys[9], ev(pokemonStatLines))
                .append(keys[10], movesLVL[0])
                .append(keys[11], movesLVL[1])
                .append(keys[12], movesTM(pokemonTMMoveLines))
                .append(keys[13], movesTR(pokemonTRMoveLines))
                .append(keys[14], abilities(pokemonAbilityLines))
                .append(keys[15], growthrate(Integer.parseInt(pokemonSpeciesCSVLine[14])))
                .append(keys[16], pokemonCSVLine[5])
                .append(keys[17], "")
                .append(keys[18], "");

        return data;
    }

    private static String htwt(String htwt)
    {
        return "" + Double.parseDouble(htwt) / 10.;
    }

    private static String growthrate(int id)
    {
        return switch(id) {
            case 1 -> "Slow";
            case 2 -> "Medium_Fast";
            case 3 -> "Fast";
            case 4 -> "Medium_Slow";
            case 5 -> "Erratic";
            case 6 -> "Fluctuating";
            default -> "ERROR";
        };
    }

    private static String type(List<String[]> typeLines)
    {
        StringBuilder sb = new StringBuilder().append("[");

        if(typeLines.size() == 2) sb.append("\"" + typeFromID(typeLines.get(0)[1]) + "\", \"" + typeFromID(typeLines.get(1)[1]) + "\"");
        else if(typeLines.size() == 1) for(int i = 0; i < 2; i++) sb.append("\"" + typeFromID(typeLines.get(0)[1]) + "\"" + (i == 0 ? ", " : ""));

        return sb.append("]").toString();
    }

    private static String typeFromID(String idStr)
    {
        String[] line = pokemonTypeNames.stream().filter(l -> l[0].equals(idStr)).filter(l -> l[1].equals("9")).collect(Collectors.toList()).get(0);
        return line[2];
    }

    private static String stats(List<String[]> statLines)
    {
        //1: HP, 2: ATK, 3: DEF, 4: SPATK, 5: SPDEF, 6: SPD
        Map<Stat, Integer> stats = new HashMap<>();

        for(String[] s : statLines) stats.put(Stat.values()[Integer.parseInt(s[1]) - 1], Integer.parseInt(s[2]));
        return "[" + stats.get(Stat.HP) + ", " + stats.get(Stat.ATK) + ", " + stats.get(Stat.DEF) + ", " + stats.get(Stat.SPATK) + ", " + stats.get(Stat.SPDEF) + ", " + stats.get(Stat.SPD) + "]";
    }

    private static String ev(List<String[]> statLines)
    {
        //1: HP, 2: ATK, 3: DEF, 4: SPATK, 5: SPDEF, 6: SPD
        Map<Stat, Integer> evYield = new HashMap<>();

        for(String[] s : statLines) evYield.put(Stat.values()[Integer.parseInt(s[1]) - 1], Integer.parseInt(s[3]));
        return "[" + evYield.get(Stat.HP) + ", " + evYield.get(Stat.ATK) + ", " + evYield.get(Stat.DEF) + ", " + evYield.get(Stat.SPATK) + ", " + evYield.get(Stat.SPDEF) + ", " + evYield.get(Stat.SPD) + "]";
    }

    private static String movesTM(List<String[]> tmLines)
    {
        if(tmLines.isEmpty()) return "[]";

        List<TM> enumTMs = Arrays.asList(TM.values());
        List<Integer> outputList = new ArrayList<>();
        List<String> tmNames = new ArrayList<>();
        List<String> tmNamesFinal = new ArrayList<>();

        for(String[] tmLine : tmLines)
        {
            //tmLine[2] is the move ID
            tmNames.add(pokemonMoveNames.stream().filter(l -> l[0].equals(tmLine[2])).collect(Collectors.toList()).get(0)[1]);
        }

        for(String move : tmNames)
        {
            tmNamesFinal.add(Global.normalCase(move.replaceAll("-", " ")));
        }

        for(String s : tmNamesFinal)
        {
            for(TM t : enumTMs)
            {
                if(t.getMoveName().toLowerCase().equals(s.toLowerCase())) outputList.add(t.ordinal() + 1);
            }
        }

        Set<Integer> finalTMList = new HashSet<>(outputList);
        outputList = new ArrayList<>(finalTMList);
        outputList.sort(Comparator.comparingInt(i -> i));

        StringBuilder sb = new StringBuilder();

        for(int i : outputList) sb.append(i).append(", ");

        return "[" + sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).toString().trim() + "]";
    }

    private static String movesTR(List<String[]> trLines)
    {
        if(trLines.isEmpty()) return "[]";

        List<TR> enumTRs = Arrays.asList(TR.values());
        List<Integer> outputList = new ArrayList<>();
        List<String> trNames = new ArrayList<>();
        List<String> trNamesFinal = new ArrayList<>();

        for(String[] trLine : trLines)
        {
            //tmLine[2] is the move ID
            trNames.add(pokemonMoveNames.stream().filter(l -> l[0].equals(trLine[2])).collect(Collectors.toList()).get(0)[1]);
        }

        for(String move : trNames)
        {
            trNamesFinal.add(Global.normalCase(move.replaceAll("-", " ")));
        }

        for(String s : trNamesFinal)
        {
            for(TR t : enumTRs)
            {
                if(t.getMoveName().toLowerCase().equals(s.toLowerCase())) outputList.add(t.ordinal());
            }
        }

        Set<Integer> finalTMList = new HashSet<>(outputList);
        outputList = new ArrayList<>(finalTMList);
        outputList.sort(Comparator.comparingInt(i -> i));

        StringBuilder sb = new StringBuilder();

        for(int i : outputList) sb.append(i).append(", ");

        if(outputList.isEmpty()) return "[]";
        return "[" + sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).toString().trim() + "]";

    }

    //pokemon_id,version_group_id,move_id,pokemon_move_method_id,level,order
    //For input moveLines List ^
    private static String[] movesLVL(List<String[]> moveLines)
    {
        //List<String> names = new ArrayList<>();
        //List<Integer> levels = new ArrayList<>();
        Map<String, Integer> nameLevelMap = new HashMap<>();

        String name;
        for(String[] line : moveLines)
        {
            name = pokemonMoveNames.stream().filter(l -> l[0].equals(line[2])).collect(Collectors.toList()).get(0)[1];
            name = Global.normalCase(name.replaceAll("-", " "));
            name = "\"" + name + "\"";
            if(!nameLevelMap.containsKey(name)) nameLevelMap.put(name, Integer.parseInt(line[4]));
            //names.add(pokemonMoveNames.stream().filter(l -> l[0].equals(line[2])).collect(Collectors.toList()).get(0)[1]);
            //levels.add(Integer.parseInt(line[4]));
        }

        List<Map.Entry<String, Integer>> sortedMoves = nameLevelMap.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());

        //List<Map.Entry<String, Integer>> sortableMoves = new ArrayList<>(nameLevelMap.entrySet());
        //sortableMoves.sort(Comparator.comparingInt(Map.Entry::getValue));

        StringBuilder moveNames = new StringBuilder();
        StringBuilder moveLevels = new StringBuilder();

        for(Map.Entry<String, Integer> e : sortedMoves)
        {
            moveNames.append(e.getKey()).append(", ");
            moveLevels.append(e.getValue()).append(", ");
        }

        String moves = moveNames.deleteCharAt(moveNames.length() - 1).deleteCharAt(moveNames.length() - 1).toString().trim();
        String levels = moveLevels.deleteCharAt(moveLevels.length() - 1).deleteCharAt(moveLevels.length() - 1).toString().trim();

        return new String[]{"[" + moves + "]", "[" + levels + "]"};
    }

    private static String abilities(List<String[]> abilityLines)
    {
        List<String> ids = new ArrayList<>();
        List<String> abilities = new ArrayList<>();

        for(String[] ab : abilityLines) ids.add(ab[1]);

        for(String s : ids) abilities.add(pokemonAbilityNames.stream().filter(l -> l[0].equals(s)).collect(Collectors.toList()).get(0)[1]);

        return abilities.stream().map(a -> "\"" + Global.normalCase(a.replaceAll("-", " ")) + "\"").collect(Collectors.toList()).toString();
    }

    private static void writePokemonJSONFile(int dex) throws IOException
    {
        Document data = getPokemonData(dex);
        data.put("normalURL", NORMAL_URL.get(dex));
        data.put("shinyURL", SHINY_URL.get(dex));

        String fileName = dex + " " + data.getString("name") + ".json";

        Files.write(Paths.get(WRITE_PATH + fileName), getJSONString(data).getBytes());
        System.out.println("Wrote " + fileName);
    }

    private static String getJSONString(Document d)
    {
        if(d == null) return "";
        StringBuilder sb = new StringBuilder().append("{\n");
        String indent = "    ";

        for(int i = 0; i < keys.length; i++)
        {
            sb.append(indent + "\"" + keys[i] + "\": ");

            if(d.get(keys[i]).toString().chars().allMatch(Character::isDigit) && !d.get(keys[i]).toString().equals("")) sb.append(Integer.parseInt(d.get(keys[i]).toString()));
            else if(d.get(keys[i]).toString().contains("[")) sb.append(d.get(keys[i]).toString());
            else sb.append("\"" + d.get(keys[i]) + "\"");

            sb.append(i == keys.length - 1 ? "\n" : ",\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
