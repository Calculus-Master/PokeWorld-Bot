package com.calculusmaster.pokecord.game.duel.elements;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.json.JSONArray;

import java.util.*;

public class GymLeader extends Player
{
    //Gym Leaders - (mostly) fixed trainers that offer PvE progression
    public static final List<List<LeaderInfo>> GYM_LEADERS = new ArrayList<>();

    public static void init()
    {
        //Level 1
        final List<LeaderInfo> LEVEL_1 = new ArrayList<>();

        LeaderInfo Cilan;
            LeaderTeamSlot cilan_1 = new LeaderTeamSlot(20)
                    .addPokemon("Pansage", "Fury Swipes", "Bite", "Vine Whip", "Lick");
            LeaderTeamSlot cilan_2 = new LeaderTeamSlot(20)
                    .addPokemon("Ivysaur", "Sleep Powder", "Vine Whip", "Razor Leaf", "Poison Powder")
                    .addPokemon("Ferrothorn", "Pin Missile", "Flash Cannon", "Power Whip");
        Cilan = new LeaderInfo("Cilan", 1, 20, ZCrystal.GRASSIUM_Z, cilan_1, cilan_2);
        LEVEL_1.add(Cilan);

        LeaderInfo Cress;
            LeaderTeamSlot cress_1 = new LeaderTeamSlot(20)
                    .addPokemon("Panpour", "Water Gun");
            LeaderTeamSlot cress_2 = new LeaderTeamSlot(20)
                    .addPokemon("Wartortle", "Bite", "Water Gun");
        Cress = new LeaderInfo("Cress", 1, 20, ZCrystal.WATERIUM_Z, cress_1, cress_2);
        LEVEL_1.add(Cress);

        LeaderInfo Chili;
            LeaderTeamSlot chili_1 = new LeaderTeamSlot(20)
                    .addPokemon("Pansear");
            LeaderTeamSlot chili_2 = new LeaderTeamSlot(20)
                    .addPokemon("Charmeleon");
        Chili = new LeaderInfo("Chili", 1, 20, ZCrystal.FIRIUM_Z, chili_1, chili_2);
        LEVEL_1.add(Chili);

        GYM_LEADERS.add(LEVEL_1);
    }

    public static List<String> getPlayersDefeated(String gymLeaderName)
    {
        return Mongo.GymData.find(Filters.eq("name", gymLeaderName)).first().getList("defeated", String.class);
    }

    public LeaderInfo info;

    public static GymLeader create(LeaderInfo info)
    {
        GymLeader g = new GymLeader();
        g.setID(info.name);

        g.team = Collections.unmodifiableList(g.createTeam(info));
        g.active = g.team.get(0);
        g.move = null;
        g.usedZMove = false;
        g.info = info;

        g.data = new PlayerDataQuery(g.ID)
        {
            @Override
            public String getUsername()
            {
                return info.name;
            }

            @Override
            public boolean hasZCrystal(String z)
            {
                return z.equals(this.getEquippedZCrystal());
            }

            @Override
            public String getEquippedZCrystal()
            {
                return info.zcrystal.getStyledName();
            }
        };

        return g;
    }

    private List<Pokemon> createTeam(LeaderInfo info)
    {
        List<Pokemon> team = new ArrayList<>();

        for(int i = 0; i < info.slots.size(); i++) team.add(info.slots.get(i).getSlotPokemon());

        return team;
    }

    private void setID(String name)
    {
        StringBuilder ID = new StringBuilder().append("LEADER-").append(name).append("-");
        for(int i = 0; i < 6; i++) ID.append("abcdefghijklmnopqrstuvwxyz".charAt(new Random().nextInt(26)));

        this.ID = ID.toString();
    }

    public static class LeaderInfo
    {
        public String name;
        public int gymLevel;
        public int pokemonLevel;
        public ZCrystal zcrystal;
        public List<LeaderTeamSlot> slots;

        public LeaderInfo(String name, int gymLevel, int pokemonLevel, ZCrystal zcrystal, LeaderTeamSlot... slots)
        {
            this.name = name;
            this.gymLevel = gymLevel;
            this.pokemonLevel = pokemonLevel;
            this.zcrystal = zcrystal;
            this.slots = new ArrayList<>(Arrays.asList(slots));

            if(Mongo.GymData.find(Filters.eq("name", name)).first() == null)
            {
                Mongo.GymData.insertOne(new Document("name", name).append("defeated", new JSONArray()));
            }
        }

    }

    private static class LeaderTeamSlot
    {
        public int level;
        public List<String> pokemon;
        public Map<String, List<String>> forcedMoves;

        LeaderTeamSlot(int level)
        {
            this.level = level;
            this.pokemon = new ArrayList<>();
            this.forcedMoves = new HashMap<>();
        }

        LeaderTeamSlot addPokemon(String pokemon, String... moveset)
        {
            this.pokemon.add(pokemon);
            this.forcedMoves.put(pokemon, new ArrayList<>(Arrays.asList(moveset)));

            return this;
        }

        Pokemon getSlotPokemon()
        {
            Pokemon p = Pokemon.create(this.pokemon.get(new Random().nextInt(this.pokemon.size())));
            p.setLevel(this.level);
            p.setHealth(p.getStat(Stat.HP));

            StringBuilder moves = new StringBuilder();
            String m;
            List<String> moveListCopy = new ArrayList<>(List.copyOf(this.forcedMoves.get(p.getName())));
            for(int i = 0; i < 4; i++)
            {
                if(moveListCopy.isEmpty())
                {
                    m = p.getAvailableMoves().get(new Random().nextInt(p.getAvailableMoves().size()));
                }
                else
                {
                    m = moveListCopy.get(new Random().nextInt(moveListCopy.size()));
                    moveListCopy.remove(m);
                }

                moves.append(m).append("-");
            }
            moves.deleteCharAt(moves.length() - 1);
            p.setLearnedMoves(moves.toString());

            Random r = new Random();
            StringBuilder ivs = new StringBuilder();
            for(int i = 0; i < 6; i++) ivs.append(r.nextInt(11) + 21).append("-");
            ivs.deleteCharAt(ivs.length() - 1);
            p.setIVs(ivs.toString());

            return p;
        }
    }
}
