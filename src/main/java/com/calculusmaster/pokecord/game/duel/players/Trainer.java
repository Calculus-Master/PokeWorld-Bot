package com.calculusmaster.pokecord.game.duel.players;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.*;

public class Trainer extends Player
{
    //Daily Trainers - randomized trainers that players can challenge for rewards
    public static int BASE_COUNT;
    public static int COUNT_DEVIATION;

    public static final List<TrainerInfo> DAILY_TRAINERS = new ArrayList<>();

    public static void init()
    {
        DAILY_TRAINERS.clear();

        Mongo.TrainerData.find().forEach(d -> DAILY_TRAINERS.add(TrainerInfo.fromDB(d.getString("trainerID"))));

        if(DAILY_TRAINERS.isEmpty())
        {
            createDailyTrainers();
            init();
        }
    }

    public static void createDailyTrainers()
    {
        List<String> trainerNames = new ArrayList<>(Arrays.asList("Team Rocket Grunt", "Team Aqua Grunt", "Team Magma Grunt", "Team Galactic Grunt", "Team Plasma Grunt", "Team Flare Grunt", "Team Skull Grunt", "Team Yell Grunt"));

        DAILY_TRAINERS.clear();

        Mongo.TrainerData.deleteMany(Filters.exists("trainerID"));

        Random r = new Random();
        String name;
        ZCrystal z;
        String[] team;
        int level;
        int size;

        final int max = BASE_COUNT + COUNT_DEVIATION;
        final int min = BASE_COUNT - COUNT_DEVIATION;
        final int count = new Random().nextInt(max - min + 1) + min;

        for(int i = 0; i < count; i++)
        {
            name = trainerNames.get(r.nextInt(trainerNames.size()));
            trainerNames.remove(name);

            if(r.nextInt(10) < 1) z = ZCrystal.values()[r.nextInt(18)]; //Only type Z Crystals
            else z = null;

            if(i == 0) size = 2;
            else if(i == count - 1) size = 6;
            else size = 4;

            level = 20 + ((100 - 20) * (i + 1) / count);

            team = new String[size];
            for(int j = 0; j < team.length; j++) team[j] = PokemonData.POKEMON.get(r.nextInt(PokemonData.POKEMON.size()));

            DAILY_TRAINERS.add(new TrainerInfo(name, level, z, 1.2, team));
        }

        for(TrainerInfo t : DAILY_TRAINERS) TrainerInfo.toDB(t);
    }

    public TrainerInfo info;

    public static Trainer create(TrainerInfo info)
    {
        Trainer t = new Trainer();
        t.ID = info.trainerID;
        t.setTeam(info.pokemonLevel, info.pokemon, info.buff);
        t.move = null;
        t.usedZMove = false;
        t.info = info;

        t.data = new PlayerDataQuery(t.ID)
        {
            @Override
            public String getUsername()
            {
                return info.name;
            }

            @Override
            public boolean hasZCrystal(String z)
            {
                return info.zcrystal != null && z.equals(this.getEquippedZCrystal());
            }

            @Override
            public String getEquippedZCrystal()
            {
                return info.zcrystal.getStyledName();
            }
        };

        return t;
    }

    private void setTeam(int level, List<String> pokemon, double statBuff)
    {
        List<Pokemon> teamBuilder = new ArrayList<>();

        for(String s : pokemon)
        {
            Pokemon p = Pokemon.create(s);
            p.setLevel(level);
            p.statBuff = statBuff;
            p.setHealth(p.getStat(Stat.HP));

            for(int i = 0; i < 4; i++)
            {
                p.learnMove(p.getAvailableMoves().get(new Random().nextInt(p.getAvailableMoves().size())), i + 1);
            }

            teamBuilder.add(p);
        }

        this.team = Collections.unmodifiableList(teamBuilder);
        this.active = this.team.get(0);
    }

    public static boolean hasPlayerDefeated(String trainerID, String playerID)
    {
        return DAILY_TRAINERS.stream().filter(ti -> ti.trainerID.equals(trainerID)).toList().get(0).playersDefeated.contains(playerID);
    }

    public static void addPlayerDefeated(String trainerID, String playerID)
    {
        Mongo.TrainerData.updateOne(Filters.eq("trainerID", trainerID), Updates.push("players_defeated", playerID));

        Trainer.init();
    }

    public static class TrainerInfo
    {
        public String trainerID;
        public String name;
        public List<String> pokemon;
        public ZCrystal zcrystal;
        public int pokemonLevel;
        public double buff;
        public List<String> playersDefeated;

        public TrainerInfo(String name, int level, ZCrystal z, double buff, String... pokemon)
        {
            this.trainerID = IDHelper.alphanumeric(12);
            this.name = name;
            this.pokemonLevel = level;
            this.zcrystal = z;
            this.buff = buff;
            this.pokemon = Arrays.asList(pokemon);
            this.playersDefeated = new ArrayList<>();
        }

        //Purely for Database Serialization
        private TrainerInfo(String trainerID)
        {
            this.trainerID = trainerID;
        }

        static void toDB(TrainerInfo trainer)
        {
            Document data = new Document()
                    .append("trainerID", trainer.trainerID)
                    .append("name", trainer.name)
                    .append("pokemon", trainer.pokemon)
                    .append("zcrystal", trainer.zcrystal == null ? "NONE" : trainer.zcrystal.getStyledName())
                    .append("pokemon_level", trainer.pokemonLevel)
                    .append("buff", trainer.buff)
                    .append("players_defeated", trainer.playersDefeated);

            Mongo.TrainerData.insertOne(data);
        }

        static TrainerInfo fromDB(String trainerID)
        {
            Document data = Mongo.TrainerData.find(Filters.eq("trainerID", trainerID)).first();

            TrainerInfo trainer = new TrainerInfo(trainerID);

            trainer.name = data.getString("name");
            trainer.pokemon = data.getList("pokemon", String.class);
            trainer.zcrystal = ZCrystal.cast(data.getString("zcrystal"));
            trainer.pokemonLevel = data.getInteger("pokemon_level");
            trainer.buff = data.getDouble("buff");
            trainer.playersDefeated = data.getList("players_defeated", String.class);

            return trainer;
        }
    }
}
