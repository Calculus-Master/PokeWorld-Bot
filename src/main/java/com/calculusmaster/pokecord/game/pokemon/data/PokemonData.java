package com.calculusmaster.pokecord.game.pokemon.data;

import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonStats;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public final class PokemonData
{
    public static final List<String> POKEMON = new ArrayList<>();
    private static final LinkedHashMap<String, PokemonData> POKEMON_DATA = new LinkedHashMap<>();

    public static void init()
    {
        CSVHelper.CSV_POKEMON_DATA_STANDARD
                .stream()
                .map(line -> line[0])
                .forEachOrdered(name -> {
                    POKEMON.add(name);
                    POKEMON_DATA.put(name, new PokemonData(name));
                });
    }

    public static PokemonData get(String name)
    {
        return POKEMON_DATA.get(name);
    }

    //Fields (Categorized by CSV Data Source)

    //Standard CSV
    public final String name;
    public final int dex;
    public final String species;
    public final double height;
    public final double weight;
    public final List<Type> types;
    public final GrowthRate growthRate;
    public final int baseEXP;

    //Images CSV
    public final String normalURL;
    public final String shinyURL;

    //Stats CSV
    public final PokemonStats baseStats;

    //EV CSV
    public final PokemonStats yield;

    //Forms & Megas CSV
    public final List<String> forms;
    public final List<String> megas;

    //Breeding CSV
    public int genderRate;
    public List<EggGroup> eggGroups;
    public int hatchTarget;

    //Evolutions CSV
    public final LinkedHashMap<String, Integer> evolutions;

    //Moves CSV
    public final List<Ability> abilities;
    public final LinkedHashMap<String, Integer> moves;
    public final EnumSet<TM> validTMs;
    public final EnumSet<TR> validTRs;

    //Descriptions CSV
    public final List<String> descriptions;

    private PokemonData(String name)
    {
        this.name = name;

        //Standard: {"name", "dex", "species", "height", "weight", "type", "growth", "yield"}
        String[] standard = this.readCSVLine(CSVHelper.CSV_POKEMON_DATA_STANDARD);

        this.dex = Integer.parseInt(standard[1]);
        this.species = standard[2];
        this.height = Double.parseDouble(standard[3]);
        this.weight = this.name.equals("Eternamax Eternatus") ? Double.MAX_VALUE : Double.parseDouble(standard[4]);
        this.types = Stream.of(Type.cast(standard[5].split("-")[0]), Type.cast(standard[5].split("-")[1])).distinct().toList();
        this.growthRate = GrowthRate.cast(standard[6]);
        this.baseEXP = Integer.parseInt(standard[7]);

        //Images: {"name", "normal", "shiny"}
        String[] images = this.readCSVLine(CSVHelper.CSV_POKEMON_DATA_IMAGES);

        this.normalURL = images[1];
        this.shinyURL = images[2];

        //Stats: {"name", "hp", "atk", "def", "spatk", "spdef", "spd"}
        String[] stats = this.readCSVLine(CSVHelper.CSV_POKEMON_DATA_STATS);

        String[] statValues = Arrays.copyOfRange(stats, 1, stats.length);
        this.baseStats = new PokemonStats(Integer.parseInt(statValues[0]), Integer.parseInt(statValues[1]), Integer.parseInt(statValues[2]), Integer.parseInt(statValues[3]), Integer.parseInt(statValues[4]), Integer.parseInt(statValues[5]));

        //EVs: {"name", "hp", "atk", "def", "spatk", "spdef", "spd"}
        String[] evs = this.readCSVLine(CSVHelper.CSV_POKEMON_DATA_EFFORT_VALUES);

        String[] evValues = Arrays.copyOfRange(evs, 1, evs.length);
        this.yield = new PokemonStats(Integer.parseInt(evValues[0]), Integer.parseInt(evValues[1]), Integer.parseInt(evValues[2]), Integer.parseInt(evValues[3]), Integer.parseInt(evValues[4]), Integer.parseInt(evValues[5]));

        //Forms/Megas: {"name", "forms", "megas"}
        String[] formMega = this.readCSVLine(CSVHelper.CSV_POKEMON_DATA_FORMS_MEGAS);

        this.forms = List.of(formMega[1].split("-"));
        this.megas = List.of(formMega[2].split("-"));

        //Breeding: {"name", "gender_rate", "egg_groups", "hatch_target"}
        String[] breeding = this.readCSVLine(CSVHelper.CSV_POKEMON_DATA_BREEDING);

        this.genderRate = Integer.parseInt(breeding[1]);
        this.eggGroups = Stream.of(breeding[2].split("-")).map(EggGroup::cast).toList();
        this.hatchTarget = Integer.parseInt(breeding[3]);

        //Evolutions: {"name", "evolutions", "levels"}
        String[] evolutions = this.readCSVLine(CSVHelper.CSV_POKEMON_DATA_EVOLUTIONS);

        this.evolutions = new LinkedHashMap<>();
        if(!evolutions[1].isEmpty()) for(int i = 0; i < evolutions[1].split("-").length; i++) this.evolutions.put(evolutions[1].split("-")[i], Integer.parseInt(evolutions[2].split("-")[i]));

        //Moves: {"name", "abilities", "moves", "levels", "tms", "trs"}
        String[] moves = this.readCSVLine(CSVHelper.CSV_POKEMON_DATA_MOVES);

        this.abilities = Stream.of(moves[1].split("-")).map(Ability::cast).toList();
        this.moves = new LinkedHashMap<>();
        for(int i = 0; i < moves[2].split("-").length; i++) this.moves.put(moves[2].split("-")[i], Integer.parseInt(moves[3].split("-")[i]));
        this.validTMs = moves[4].isEmpty() ? EnumSet.noneOf(TM.class) : EnumSet.copyOf(Stream.of(moves[4].split("-")).map(Integer::parseInt).map(TM::get).toList());
        this.validTRs = moves[5].isEmpty() ? EnumSet.noneOf(TR.class) : EnumSet.copyOf(Stream.of(moves[5].split("-")).map(Integer::parseInt).map(TR::get).toList());

        //Descriptions: {"name", "descriptions"}
        String[] descriptions = this.readCSVLine(CSVHelper.CSV_POKEMON_DATA_DESCRIPTIONS);

        if(descriptions == null) this.descriptions = List.of("No Pokemon description available.");
        else this.descriptions = List.of(descriptions[1].split("\\|")).stream()
                .distinct() //Remove duplicates
                .map(s -> s.replaceAll("\n", " ")) //Replace new line characters with spaces
                .map(s -> s.replaceAll("POKéMON", "Pokemon")) //Fix the weirdly formatted Pokemon word
                .toList();
    }

    private String[] readCSVLine(List<String[]> full)
    {
        return full.stream().filter(line -> line[0].equals(this.name)).findFirst().orElse(null);
    }

    //Legacy Code (Converting the Pokemon Info Database to CSV Files)
//
//    private static final List<Document> PokemonInfoDatabase = new ArrayList<>();
//    static { Mongo.PokemonInfo.find().forEach(PokemonInfoDatabase::add); }

    public static void main(String[] args) throws IOException
    {
//        writeCSVFile("pokemon_data_standard.csv", new String[]{"name", "dex", "species", "height", "weight", "type", "growth", "yield"},
//                (d, s, o) -> {
//                    String[] out = new String[s];
//                    out[0] = d.getString("name");
//                    out[1] = String.valueOf(d.getInteger("dex"));
//
//                    String[] filler = d.getString("fillerinfo").split("-");
//                    out[2] = filler[0];
//                    out[3] = filler[1];
//                    out[4] = filler[2];
//
//                    out[5] = d.getList("type", String.class).get(0) + "-" + d.getList("type", String.class).get(1);
//                    out[6] = d.getString("growthrate");
//                    out[7] = String.valueOf(d.getInteger("exp"));
//
//                    o.add(out);
//        });
//
//        writeCSVFile("pokemon_data_images.csv", new String[]{"name", "normal", "shiny"},
//                (d, s, o) -> {
//                    String[] out = new String[s];
//
//                    out[0] = d.getString("name");
//                    out[1] = d.getString("normalURL");
//                    out[2] = d.getString("shinyURL");
//
//                    o.add(out);
//        });
//
//        writeCSVFile("pokemon_data_stats.csv", new String[]{"name", "hp", "atk", "def", "spatk", "spdef", "spd"},
//                (d, s, o) -> {
//                    String[] out = new String[s];
//
//                    out[0] = d.getString("name");
//                    for(int i = 1; i < out.length; i++) out[i] = String.valueOf(d.getList("stats", Integer.class).get(i - 1));
//
//                    o.add(out);
//        });
//
//        writeCSVFile("pokemon_data_effort_values.csv", new String[]{"name", "hp", "atk", "def", "spatk", "spdef", "spd"},
//                (d, s, o) -> {
//                    String[] out = new String[s];
//
//                    out[0] = d.getString("name");
//                    for(int i = 1; i < out.length; i++) out[i] = String.valueOf(d.getList("ev", Integer.class).get(i - 1));
//
//                    o.add(out);
//        });
//
//        writeCSVFile("pokemon_data_forms_megas.csv", new String[]{"name", "forms", "megas"},
//                (d, s, o) -> {
//                    String[] out = new String[s];
//
//                    out[0] = d.getString("name");
//                    out[1] = String.join("-", d.getList("forms", String.class));
//                    out[2] = String.join("-", d.getList("mega", String.class));
//
//                    o.add(out);
//        });
//
//        DataHelper.createGenderRateMap();
//        DataHelper.createEggGroupLists();
//        DataHelper.createBaseEggHatchTargetsMap();
//
//        writeCSVFile("pokemon_data_breeding.csv", new String[]{"name", "gender_rate", "egg_groups", "hatch_target"},
//                (d, s, o) -> {
//                    String[] out = new String[s];
//
//                    out[0] = d.getString("name");
//                    out[1] = DataHelper.POKEMON_GENDER_RATES.get(d.getInteger("dex")).toString();
//                    out[2] = DataHelper.POKEMON_EGG_GROUPS.get(d.getInteger("dex")).stream().map(EggGroup::toString).collect(Collectors.joining("-"));
//                    out[3] = DataHelper.POKEMON_BASE_HATCH_TARGETS.get(d.getInteger("dex")).toString();
//
//                    o.add(out);
//        });
//
//        writeCSVFile("pokemon_data_evolutions.csv", new String[]{"name", "evolutions", "levels"},
//                (d, s, o) -> {
//                    String[] out = new String[s];
//
//                    out[0] = d.getString("name");
//                    out[1] = String.join("-", d.getList("evolutions", String.class));
//                    out[2] = String.join("-", d.getList("evolutionsLVL", Integer.class).stream().map(String::valueOf).toList());
//
//                    o.add(out);
//        });
//
//        writeCSVFile("pokemon_data_moves.csv", new String[]{"name", "abilities", "moves", "levels", "tms", "trs"},
//                (d, s, o) -> {
//                    String[] out = new String[s];
//
//                    out[0] = d.getString("name");
//                    out[1] = String.join("-", d.getList("abilities", String.class));
//                    out[2] = String.join("-", d.getList("moves", String.class));
//                    out[3] = String.join("-", d.getList("movesLVL", Integer.class).stream().map(String::valueOf).toList());
//                    out[4] = String.join("-", d.getList("movesTM", Integer.class).stream().map(String::valueOf).toList());
//                    out[5] = String.join("-", d.getList("movesTR", Integer.class).stream().map(String::valueOf).toList());
//
//                    o.add(out);
//        });

//        DataHelper.createSpeciesDescLists();
//
//        writeCSVFile("pokemon_data_descriptions.csv", new String[]{"name", "descriptions"},
//                (d, s, o) -> {
//                    String[] out = new String[s];
//
//                    out[0] = d.getString("name");
//                    out[1] = String.join("|", DataHelper.POKEMON_SPECIES_DESC.get(d.getInteger("dex")));
//
//                    o.add(out);
//        });
    }

//    private interface DocumentParser { void parse(Document d, int arraySize, List<String[]> outputList); }
//
//    private static void writeCSVFile(String fileName, String[] header, DocumentParser parser) throws IOException
//    {
//        CSVWriter writer = Objects.requireNonNull(PokemonData.createWriter(fileName));
//        writer.writeNext(header);
//
//        List<String[]> data = new ArrayList<>();
//        PokemonInfoDatabase.forEach(d -> parser.parse(d, header.length, data));
//
//        writer.writeAll(data);
//        writer.close();
//    }
//
//    private static CSVWriter createWriter(String fileName) throws IOException
//    {
//        return new CSVWriter(new FileWriter(fileName), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
//    }
}
