package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.component.EntryHazardHandler;
import com.calculusmaster.pokecord.game.duel.component.FieldBarrierHandler;
import com.calculusmaster.pokecord.game.duel.component.FieldEffectsHandler;
import com.calculusmaster.pokecord.game.duel.component.FieldGMaxDoTHandler;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.duel.players.WildPokemon;
import com.calculusmaster.pokecord.game.enums.elements.Room;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
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
            for(int i = 0; i < this.getNonBotPlayers().length; i++) if(!this.getAction(i).equals(ActionType.IDLE)) this.moveAction(i);

            List<String> movePool = this.getRaidBoss().active.allMoves().stream().filter(Move::isImplemented).collect(Collectors.collectingAndThen(Collectors.toList(), list -> { Collections.shuffle(list); return list; }));
            this.getRaidBoss().move = new Move(movePool.isEmpty() ? "Tackle" : movePool.get(new Random().nextInt(movePool.size())));

            if(Arrays.asList("Explosion", "Self Destruct", "Memento").contains(this.getRaidBoss().move.getName()))
            {
                this.getRaidBoss().move = new Move("Tackle");
                this.getRaidBoss().move.setPower(350);
            }

            List<Integer> pool = new ArrayList<>();
            for(int i = 0; i < this.getNonBotPlayers().length; i++) pool.add(i);

            pool.stream().filter(this::isUsingMove).collect(Collectors.toList()).sort(Comparator.comparingInt(i -> this.players[(int)i].move.getPriority()).thenComparingInt(i -> this.players[(int)i].active.getStat(Stat.SPD)).reversed());

            if(this.room.isActive(Room.TRICK_ROOM)) Collections.reverse(pool);

            for(int i : pool)
            {
                this.other = this.players.length - 1;
                this.moveLogic(i);
                this.results.add("\n");
            }

            this.current = this.other;
            do this.other = new Random().nextInt(this.getNonBotPlayers().length);
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

            String highestDamage = this.getNonBotPlayers().length == 1 ? "" : pokemonToPlayer.get(sorted.get(0).getKey());

            StringBuilder winnings = new StringBuilder();
            String extraWinnings = "";

            for(Player p : this.getNonBotPlayers())
            {
                double multiplier = highestDamage.equals(p.ID) ? 1.3 : (p.active.isFainted() ? 0.3 : 1.0);

                p.data.changeCredits((int)(credits * multiplier));
                p.data.addExp((int)(40 * multiplier));
                p.active.addExp((int)(pokeXP * multiplier));

                p.data.getStatistics().incr(PlayerStatistic.RAIDS_WON);
                p.data.updateBountyProgression(ObjectiveType.WIN_RAID_DUEL);
                Achievements.grant(p.ID, Achievements.WON_FIRST_RAID, this.event);

                winnings.append(p.data.getUsername()).append(" - `").append((int)(credits * multiplier)).append("c`\n");

                if(highestDamage.equals(p.ID) && !p.active.isFainted() && new Random().nextInt(100) < 50)
                {
                    Pokemon reward = Pokemon.create(this.getRaidBoss().active.getName());
                    reward.setIVs(50);
                    reward.setLevel(60);
                    Arrays.stream(Stat.values()).forEach(s -> reward.setEV(s, 20));
                    reward.setDynamaxLevel(2);
                    reward.setNickname("Raid " + reward.getName());

                    reward.upload();
                    p.data.addPokemon(reward.getUUID());

                    p.data.getStatistics().incr(PlayerStatistic.RAIDS_WON_MVP);
                    Achievements.grant(p.ID, Achievements.WON_FIRST_RAID_HIGHEST_DAMAGE, this.event);

                    extraWinnings = "\n\n**" + p.data.getUsername() + " caught the Raid Pokemon!**";
                }
            }

            winnings.deleteCharAt(winnings.length() - 1);

            embed.setDescription("The Raid Pokemon is defeated!\n\n**Rewards:**\n" + winnings + extraWinnings);
        }
        else embed.setDescription("Raid Pokemon could not be defeated! No rewards earned!");

        for(Player p : this.getNonBotPlayers())
        {
            p.data.getStatistics().incr(PlayerStatistic.RAIDS_COMPLETED);
            p.data.updateBountyProgression(ObjectiveType.PARTICIPATE_RAID);
            Achievements.grant(p.ID, Achievements.COMPLETED_FIRST_RAID, this.event);
        }

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();

        DuelHelper.delete(this.players[0].ID);
        RaidEventHelper.removeServer(this.event.getGuild().getId());
    }

    private static void testImageGeneration() throws IOException
    {
        CSVHelper.init();
        PokemonData.init();
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
                System.out.println("Reading URL – Name: " + players[i].getName() + ", URL: " + players[i].getImage());
                Image image = ImageIO.read(new URL(players[i].getImage())).getScaledInstance(size, size, hint);

                playerImages.add(image);
            }
        }

        System.out.println(playerImages.size());

        new RaidDuel().drawPlayerImages(combined, playerImages, backgroundW, backgroundH);

        if(!players[players.length - 1].isFainted())
        {
            int size = players[players.length - 1].isDynamaxed() ? (int)(bossSize * 1.25) : bossSize;

            Image p2 = ImageIO.read(new URL(players[players.length - 1].getImage())).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p2, (backgroundW - spacing) - size, y, null);
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
                Image image = ImageIO.read(new URL(this.getPokemonURL(i))).getScaledInstance(size, size, hint);

                playerImages.add(image);
            }
        }

        this.drawPlayerImages(combined, playerImages, backgroundW, backgroundH);

        if(!this.players[this.players.length - 1].active.isFainted())
        {
            int size = this.players[this.players.length - 1].active.isDynamaxed() ? (int)(bossSize * 1.25) : bossSize;

            Image p2 = ImageIO.read(new URL(this.getPokemonURL(this.players.length - 1))).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p2, (backgroundW - spacing) - size, y, null);
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
        return this.players[this.players.length - 1].active.isFainted() || Arrays.stream(this.getNonBotPlayers()).allMatch(p -> p.active.isFainted());
    }

    public void start()
    {
        this.players = new Player[this.waiting.size() + 1];

        for(int i = 0; i < this.waiting.size(); i++) this.players[i] = new Player(this.waiting.get(i), 1);

        this.setWildPokemon("");

        double baseMultiplierHP = switch(PokemonRarity.POKEMON_RARITIES.getOrDefault(this.getRaidBoss().active.getName(), PokemonRarity.Rarity.EXTREME)) {
            case COPPER -> 1.5;
            case SILVER -> 1.6;
            case GOLD -> 1.7;
            case DIAMOND -> 1.8;
            case PLATINUM -> 1.85;
            case MYTHICAL -> 1.95;
            case LEGENDARY -> 2.0;
            case EXTREME -> 2.3;
        };

        double baseMultiplierStat = switch(PokemonRarity.POKEMON_RARITIES.getOrDefault(this.getRaidBoss().active.getName(), PokemonRarity.Rarity.EXTREME)) {
            case COPPER -> 1.8;
            case SILVER -> 1.9;
            case GOLD -> 2.0;
            case DIAMOND -> 2.1;
            case PLATINUM -> 2.2;
            case MYTHICAL -> 2.5;
            case LEGENDARY -> 2.6;
            case EXTREME -> 3.0;
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
            hb.append("\n**Raid Boss ").append(this.getHB(this.players.length - 1)).append("**");
            return hb.toString();
        }
    }

    @Override
    protected String getHB(int p)
    {
        return super.getHB(p).replaceAll("The Wild", "");
    }

    @Override
    public void checkReady()
    {
        if(Arrays.stream(this.getNonBotPlayers()).allMatch(p -> p.active.isFainted() || this.queuedMoves.containsKey(p.ID)))
        {
            this.turnHandler();
        }
    }

    @Override
    public void setWildPokemon(String pokemon)
    {
        if(new Random().nextInt(100) < 5) pokemon = "Eternamax Eternatus";

        this.players[this.players.length - 1] = pokemon.equals("") ? new WildPokemon(100) : new WildPokemon(pokemon, 100);

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

    private Player[] getNonBotPlayers()
    {
        Player[] players = new Player[this.players.length - 1];
        System.arraycopy(this.players, 0, players, 0, players.length);
        return players;
    }

    public Player getRaidBoss()
    {
        return this.players[this.players.length - 1];
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
