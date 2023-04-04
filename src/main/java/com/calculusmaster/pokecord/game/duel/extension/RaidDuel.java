package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.EntryHazardHandler;
import com.calculusmaster.pokecord.game.duel.component.FieldBarrierHandler;
import com.calculusmaster.pokecord.game.duel.component.FieldEffectsHandler;
import com.calculusmaster.pokecord.game.duel.component.FieldGMaxDoTHandler;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.duel.players.WildPlayer;
import com.calculusmaster.pokecord.game.enums.elements.Room;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.event.RaidEventHelper;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.*;

public class RaidDuel extends WildDuel
{
    private String duelID;
    private List<String> waiting;

    public static RaidDuel create()
    {
        RaidDuel duel = new RaidDuel();

        duel.setStatus(DuelStatus.WAITING);
        duel.setID();
        duel.setDefaults();

        DUELS.add(duel);
        return duel;
    }

    public static RaidDuel instance(String duelID)
    {
        return DUELS.stream().filter(d -> d instanceof RaidDuel).map(d -> (RaidDuel)d).filter(raid -> raid.getDuelID().equals(duelID)).collect(Collectors.toList()).get(0);
    }

    public static void delete(String duelID)
    {
        RaidDuel instance = instance(duelID);
        DUELS.remove(instance);
    }

    @Override
    public void turnHandler()
    {
        this.turnSetup();

        for(Player p : this.players) if (p.active.isFainted()) this.queuedMoves.put(p.ID, new TurnAction(ActionType.IDLE, -1, -1));

        if(!this.isComplete())
        {
            for(int i = 0; i < this.getUserPlayers().length; i++) if(!this.getAction(i).equals(ActionType.IDLE)) this.moveAction(i);

            List<MoveEntity> movePool = this.getRaidBoss().active.getLevelUpMoves().stream().filter(Move::isImplemented).collect(Collectors.collectingAndThen(Collectors.toList(), list -> { Collections.shuffle(list); return list; }));
            this.getRaidBoss().move = new Move(movePool.isEmpty() ? MoveEntity.TACKLE : movePool.get(new Random().nextInt(movePool.size())));

            if(this.getRaidBoss().move.is(MoveEntity.EXPLOSION, MoveEntity.SELF_DESTRUCT, MoveEntity.MEMENTO))
            {
                this.getRaidBoss().move = new Move(MoveEntity.TACKLE);
                this.getRaidBoss().move.setPower(350);
            }

            List<Integer> pool = new ArrayList<>();
            for(int i = 0; i < this.getUserPlayers().length; i++) pool.add(i);

            pool.stream().filter(this::isUsingMove).collect(Collectors.toList()).sort(Comparator.comparingInt(i -> this.players[(int)i].move.getPriority()).thenComparingInt(i -> this.players[(int)i].active.getStat(Stat.SPD)).reversed());

            if(this.room.isActive(Room.TRICK_ROOM)) Collections.reverse(pool);

            for(int i : pool)
            {
                this.other = this.players.length - 1;
                this.moveLogic(i);
                this.results.add("\n");
            }

            this.current = this.other;
            do this.other = new Random().nextInt(this.getUserPlayers().length);
            while(this.players[this.other].active.isFainted());

            this.results.add("\n");
            this.moveLogic(this.current);
        }

        this.onTurnEnd();
    }

    @Override
    protected void onTurnEnd()
    {
        this.updateWeatherTerrainRoom();

        this.weatherEffects();

        this.barriers[0].updateTurns();
        this.barriers[1].updateTurns();

        for(int i = 0; i < this.players.length; i++) this.checkDynamax(i);

        if(this.isComplete())
        {
            super.onWin();

            this.sendTurnEmbed();
            this.sendWinEmbed();
            this.setStatus(DuelStatus.COMPLETE);
        }
        else this.sendTurnEmbed();

        this.queuedMoves.clear();
    }

    @Override
    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        boolean playersWon = this.getRaidBoss().active.isFainted();

        if(playersWon)
        {
            int credits = new Random().nextInt(2000) + 1000;
            int pokeXP = new Random().nextInt(100) + 500;

            Map<String, String> pokemonToPlayer = new HashMap<>();
            for(Player p : this.players) pokemonToPlayer.put(p.active.getUUID(), p.ID);

            List<Map.Entry<String, Integer>> sorted = this.damageDealt.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
            Collections.reverse(sorted);

            String highestDamage = this.getUserPlayers().length == 1 ? "" : pokemonToPlayer.get(sorted.get(0).getKey());

            StringBuilder winnings = new StringBuilder();
            String extraWinnings = "";

            for(Player p : this.getUserPlayers())
            {
                UserPlayer userPlayer = (UserPlayer)p;

                double multiplier = highestDamage.equals(p.ID) ? 1.3 : (p.active.isFainted() ? 0.3 : 1.0);

                userPlayer.data.changeCredits((int)(credits * multiplier));
                userPlayer.active.addExp((int)(pokeXP * multiplier), userPlayer.data, this.channels.get(0).getGuild().getId());

                if(highestDamage.equals(p.ID)) userPlayer.data.addExp(PMLExperience.DUEL_RAID_MVP, 100);
                else if(!p.active.isFainted()) userPlayer.data.addExp(PMLExperience.DUEL_RAID_PARTICIPANT, 75);

                userPlayer.data.getStatistics().incr(PlayerStatistic.RAIDS_WON);
                userPlayer.data.updateBountyProgression(ObjectiveType.WIN_RAID_DUEL);
                Achievements.grant(p.ID, Achievements.WON_FIRST_RAID, null);

                winnings.append(userPlayer.data.getUsername()).append(" - `").append((int)(credits * multiplier)).append("c`\n");

                if(highestDamage.equals(p.ID) && !p.active.isFainted() && new Random().nextInt(100) < 50)
                {
                    Pokemon reward = Pokemon.create(this.getRaidBoss().active.getEntity());
                    reward.setIVs(50);
                    reward.setLevel(60);
                    Arrays.stream(Stat.values()).forEach(s -> reward.setEV(s, 20));
                    reward.setDynamaxLevel(2);
                    reward.setNickname("Raid " + reward.getName());

                    reward.upload();
                    userPlayer.data.addPokemon(reward.getUUID());

                    userPlayer.data.getStatistics().incr(PlayerStatistic.RAIDS_WON_MVP);
                    Achievements.grant(p.ID, Achievements.WON_FIRST_RAID_HIGHEST_DAMAGE, null);

                    extraWinnings = "\n\n**" + p.getName() + " caught the Raid Pokemon!**";
                }
            }

            winnings.deleteCharAt(winnings.length() - 1);

            embed.setDescription("The Raid Pokemon is defeated!\n\n**Rewards:**\n" + winnings + extraWinnings);
        }
        else embed.setDescription("Raid Pokemon could not be defeated! No rewards earned!");

        for(Player p : this.getUserPlayers())
        {
            ((UserPlayer)p).data.getStatistics().incr(PlayerStatistic.RAIDS_COMPLETED);
            ((UserPlayer)p).data.updateBountyProgression(ObjectiveType.PARTICIPATE_RAID);
            Achievements.grant(p.ID, Achievements.COMPLETED_FIRST_RAID, null);
        }

        this.sendEmbed(embed.build());

        DuelHelper.delete(this.players[0].ID);
        RaidEventHelper.removeServer(this.channels.get(0).getGuild().getId());
    }

    private static void testImageGeneration() throws IOException
    {
        CSVHelper.init();
        PokemonEntity.init();
        MoveEntity.init();
        PokemonRarity.init();

        //Background is 800 x 480 -> 400 x 240
        int y = 50;
        int spacing = 25;
        int backgroundW = 800;
        int backgroundH = 480;
        int hint = BufferedImage.TYPE_INT_ARGB;

        int basePlayerSize = 100;
        int bossSize = 150;

        Pokemon[] players = new Pokemon[new SplittableRandom().nextInt(5, 9) + 1];
        for(int i = 0; i < players.length - 1; i++) players[i] = Pokemon.create(PokemonRarity.getSpawn());
        Pokemon boss = Pokemon.create(PokemonRarity.getLegendarySpawn());
        players[players.length - 1] = boss;

        for(Pokemon p : players) p.setHealth(p.getStat(Stat.HP));

        players[new SplittableRandom().nextInt(players.length - 1)].enterDynamax();

        Image background = ImageIO.read(new URL(BACKGROUND)).getScaledInstance(backgroundW, backgroundH, hint);

        BufferedImage combined = new BufferedImage(background.getWidth(null), background.getHeight(null), hint);
        combined.getGraphics().drawImage(background, 0, 0, null);

        List<Image> playerImages = new ArrayList<>();

        for(int i = 0; i < players.length - 1; i++)
        {
            if(!players[i].isFainted())
            {
                int size = players[i].isDynamaxed() ? (int)(basePlayerSize * 1.25) : basePlayerSize;

                String image = Pokemon.getImage(players[i].getEntity(), players[i].isShiny(), players[i], null);
                URL resource = Pokecord.class.getResource(image);

                System.out.println("Player " + i + ": Reading URL – Name: " + players[i].getName() + ", URL: " + image);

                if(resource != null)
                {
                    Image p = ImageIO.read(resource).getScaledInstance(size, size, hint);
                    playerImages.add(p);
                }
                else LoggerHelper.warn(Duel.class, "Pokemon Image not found. Entity: " + players[i].getEntity().toString() + ", Image File: " + image);
            }
        }

        System.out.println(playerImages.size());

        new RaidDuel().drawPlayerImages(combined, playerImages, backgroundW, backgroundH);

        if(!players[players.length - 1].isFainted())
        {
            int size = players[players.length - 1].isDynamaxed() ? (int)(bossSize * 1.25) : bossSize;

            String image = Pokemon.getImage(players[players.length - 1].getEntity(), players[players.length - 1].isShiny(), players[players.length - 1], null);
            URL resource = Pokecord.class.getResource(image);

            System.out.println("Raid Boss: Reading URL – Name: " + players[players.length - 1].getName() + ", URL: " + image);

            if(resource != null)
            {
                Image p = ImageIO.read(resource).getScaledInstance(size, size, hint);
                combined.getGraphics().drawImage(p, (backgroundW - spacing) - size, y, null);
            }
            else LoggerHelper.warn(Duel.class, "Pokemon Image not found. Entity: " + players[players.length - 1].getEntity().toString() + ", Image File: " + image);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(backgroundH * backgroundW * 4);
        ImageIO.write(combined, "png", out);

        byte[] bytes = out.toByteArray(); //This is the slow line

        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream("output.png")))
        {
            outputStream.write(bytes);
        }
    }

    public static void main(String[] args) throws IOException
    {
        testImageGeneration();
        //TODO: Fix Dynamax Pokemon not appearing correctly sized
    }

    @Override
    public InputStream getImage() throws Exception
    {
        //Background is 800 x 480 -> 400 x 240
        int y = 50;
        int spacing = 25;
        int backgroundW = 400;
        int backgroundH = 240;
        int hint = BufferedImage.TYPE_INT_ARGB;

        int basePlayerSize = 40;
        int bossSize = 150;

        Image background = ImageIO.read(new URL(BACKGROUND)).getScaledInstance(backgroundW, backgroundH, hint);

        BufferedImage combined = new BufferedImage(background.getWidth(null), background.getHeight(null), hint);
        combined.getGraphics().drawImage(background, 0, 0, null);

        List<Image> playerImages = new ArrayList<>();

        for(int i = 0; i < this.players.length - 1; i++)
        {
            if(!this.players[i].active.isFainted())
            {
                int size = this.players[i].active.isDynamaxed() ? (int)(basePlayerSize * 1.25) : basePlayerSize;

                String image = Pokemon.getImage(this.players[i].active.getEntity(), this.players[i].active.isShiny(), this.players[i].active, this.players[i].move.getEntity());
                URL resource = Pokecord.class.getResource(image);

                if(resource != null)
                {
                    Image p = ImageIO.read(resource).getScaledInstance(size, size, hint);
                    playerImages.add(p);
                }
                else LoggerHelper.warn(Duel.class, "Pokemon Image not found. Entity: " + this.players[i].active.getEntity().toString() + ", Image File: " + image);
            }
        }

        this.drawPlayerImages(combined, playerImages, backgroundW, backgroundH);

        if(!this.players[this.players.length - 1].active.isFainted())
        {
            int size = this.players[this.players.length - 1].active.isDynamaxed() ? (int)(bossSize * 1.25) : bossSize;

            String image = Pokemon.getImage(this.players[this.players.length - 1].active.getEntity(), this.players[this.players.length - 1].active.isShiny(), this.players[this.players.length - 1].active, this.players[this.players.length - 1].move.getEntity());
            URL resource = Pokecord.class.getResource(image);

            if(resource != null)
            {
                Image p = ImageIO.read(resource).getScaledInstance(size, size, hint);
                combined.getGraphics().drawImage(p, (backgroundW - spacing) - size, y, null);
            }
            else LoggerHelper.warn(Duel.class, "Pokemon Image not found. Entity: " + this.players[this.players.length - 1].active.getEntity().toString() + ", Image File: " + image);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(backgroundH * backgroundW * 4);
        ImageIO.write(combined, "png", out);

        byte[] bytes = out.toByteArray(); //This is the slow line

        return new ByteArrayInputStream(bytes);
    }

    private void drawPlayerImages(BufferedImage combined, List<Image> players, int backW, int backH)
    {
        int columnSize = 4;
        List<List<Image>> imageColumns = new ArrayList<>(); //Columns of 4 images
        for(int i = 0; i < players.size(); i += columnSize) imageColumns.add(players.subList(i, Math.min(i + columnSize, players.size())));

        int spacing = 5;
        int size = 40; //Minimum size, spacing should take care of dynamaxed pokemon
        int y = 20;
        int x = 25;

        for(List<Image> column : imageColumns)
        {
            for (Image image : column)
            {
                combined.getGraphics().drawImage(image, x, y, null);

                y += size + spacing;
            }

            y = 20;
            x += size + spacing;
        }
    }

    @Override
    public boolean isComplete()
    {
        return this.players[this.players.length - 1].active.isFainted() || Arrays.stream(this.getUserPlayers()).allMatch(p -> p.active.isFainted());
    }

    public void start()
    {
        this.players = new Player[this.waiting.size() + 1];

        for(int i = 0; i < this.waiting.size(); i++)
        {
            PlayerDataQuery p = PlayerDataQuery.ofNonNull(this.waiting.get(i));
            this.players[i] = new UserPlayer(p, p.getSelectedPokemon());
        }

        this.setWildPokemon(null);

        double baseMultiplierHP = switch(this.getRaidBoss().active.getRarity()) {
            case COPPER -> 1.5;
            case SILVER -> 1.6;
            case GOLD -> 1.7;
            case DIAMOND -> 1.8;
            case PLATINUM -> 1.85;
            case MYTHICAL -> 1.95;
            case ULTRA_BEAST -> 1.97;
            case LEGENDARY -> 2.0;
        };

        double baseMultiplierStat = switch(this.getRaidBoss().active.getRarity()) {
            case COPPER -> 1.8;
            case SILVER -> 1.9;
            case GOLD -> 2.0;
            case DIAMOND -> 2.1;
            case PLATINUM -> 2.2;
            case MYTHICAL -> 2.5;
            case ULTRA_BEAST -> 2.6;
            case LEGENDARY -> 2.7;
        };

        this.players[this.players.length - 1].active.getBoosts().setHealthBoost(baseMultiplierHP + (this.waiting.size() - 1));
        this.players[this.players.length - 1].active.getBoosts().setStatBoost(baseMultiplierStat + (0.3 * (this.waiting.size() - 1)));

        for(Player p : this.players) p.active.setHealth(p.active.getStat(Stat.HP));

        for(int i = 0; i < this.players.length; i++) this.setDuelPokemonObjects(i);

        this.queuedMoves.clear();

        this.sendTurnEmbed();
    }

    @Override
    protected String getHealthBars()
    {
        if(this.isComplete()) return "";
        else
        {
            StringBuilder hb = new StringBuilder();
            for(int i = 0; i < this.players.length - 1; i++) hb.append(this.getHB(i)).append("\n");
            hb.append("\n**Raid Leader ").append(this.getHB(this.players.length - 1)).append("**");
            return hb.toString();
        }
    }

    @Override
    public void checkReady()
    {
        if(Arrays.stream(this.getUserPlayers()).allMatch(p -> p.active.isFainted() || this.queuedMoves.containsKey(p.ID)))
        {
            this.turnHandler();
        }
    }

    @Override
    public void setWildPokemon(PokemonEntity entity)
    {
        if(new Random().nextInt(100) < 5) entity = PokemonEntity.ETERNATUS_ETERNAMAX;

        this.players[this.players.length - 1] = entity == null ? new WildPlayer(100) : new WildPlayer(entity, 100);

        Player raidBoss = this.players[this.players.length - 1];

        raidBoss.active.setIVs(80);
        Arrays.stream(Stat.values()).forEach(s -> raidBoss.active.setEV(s, 50));
    }

    @Override
    public void setDefaults()
    {
        this.waiting = new ArrayList<>();
        this.players = new Player[0];

        super.setDefaults();

        this.entryHazards = new EntryHazardHandler[this.players.length];
        this.barriers = new FieldBarrierHandler[this.players.length];
        this.gmaxDoT = new FieldGMaxDoTHandler[this.players.length];
        this.fieldEffects = new FieldEffectsHandler[this.players.length];

        for(int i = 0; i < this.players.length; i++)
        {
            this.entryHazards[i] = new EntryHazardHandler();
            this.barriers[i] = new FieldBarrierHandler();
            this.gmaxDoT[i] = new FieldGMaxDoTHandler();
            this.fieldEffects[i] = new FieldEffectsHandler();
        }
    }

    private Player[] getUserPlayers()
    {
        Player[] players = new Player[this.players.length - 1];
        System.arraycopy(this.players, 0, players, 0, players.length);
        return players;
    }

    public Player getRaidBoss()
    {
        return this.players[this.players.length - 1];
    }

    @Override
    public Player getOpponent(String ID)
    {
        return this.getRaidBoss();
    }

    public void addPlayer(String ID)
    {
        if(!this.waiting.contains(ID)) this.waiting.add(ID);
    }

    public void removePlayer(String ID)
    {
        this.waiting.remove(ID);
    }

    public boolean isPlayerWaiting(String ID)
    {
        return this.waiting.stream().anyMatch(s -> s.equals(ID));
    }

    public List<String> getWaitingPlayers()
    {
        return this.waiting;
    }

    private void setID()
    {
        this.duelID = IDHelper.numeric(10);
    }

    public String getDuelID()
    {
        return this.duelID;
    }
}
