package com.calculusmaster.pokecord.game.duel.elements;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Global;

import java.util.*;

public class Trainer extends Player
{
    //Daily Trainers - randomized trainers that players can challenge for rewards
    public static final List<TrainerInfo> DAILY_TRAINERS = new ArrayList<>();
    //Gym Leaders - (mostly) fixed trainers that offer PvE progression
    public static final List<List<TrainerInfo>> GYM_LEADERS = new ArrayList<>();

    public static void setDailyTrainers()
    {
        List<String> trainerNames = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"));
        List<Integer> levels = Arrays.asList(25, 50, 75, 100);
        List<Integer> sizes = Arrays.asList(2, 4, 4, 6);
        DAILY_TRAINERS.clear();

        Random r = new Random();
        String name;
        ZCrystal z;
        String[] team;
        for(int i = 0; i < levels.size(); i++)
        {
            name = trainerNames.get(r.nextInt(trainerNames.size()));
            trainerNames.remove(name);

            if(r.nextInt(10) < 1) z = ZCrystal.values()[r.nextInt(18)]; //Only type Z Crystals
            else z = null;

            team = new String[sizes.get(i)];
            for(int j = 0; j < team.length; j++) team[j] = Global.POKEMON.get(r.nextInt(Global.POKEMON.size()));

            DAILY_TRAINERS.add(new TrainerInfo(name, levels.get(i), z, team));
        }
    }

    //TODO: Make sure Trainer battles work first then work on these
    private static void createGymLeaders()
    {

    }

    public TrainerInfo info;

    public static Trainer create(TrainerInfo info)
    {
        Trainer t = new Trainer();
        t.setID(info.name);
        t.setTeam(info.pokemonLevel, info.pokemon);
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
                return info.zcrystal != null;
            }
        };

        return t;
    }

    private void setTeam(int level, List<String> pokemon)
    {
        List<Pokemon> teamBuilder = new ArrayList<>();

        for(String s : pokemon)
        {
            Pokemon p = Pokemon.create(s);
            p.setLevel(level);
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

    private void setID(String name)
    {
        StringBuilder ID = new StringBuilder().append("TRAINER-").append(name).append("-");
        for(int i = 0; i < 6; i++) ID.append("abcdefghijklmnopqrstuvwxyz".charAt(new Random().nextInt(26)));

        this.ID = ID.toString();
    }

    public static class TrainerInfo
    {
        public String name;
        public List<String> pokemon;
        public ZCrystal zcrystal;
        public int pokemonLevel;

        public TrainerInfo(String name, int level, ZCrystal z, String... pokemon)
        {
            this.name = name;
            this.pokemonLevel = level;
            this.zcrystal = z;
            this.pokemon = Arrays.asList(pokemon);
        }
    }
}
