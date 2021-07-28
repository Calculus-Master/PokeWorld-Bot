package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.duel.players.WildPokemon;
import com.calculusmaster.pokecord.game.enums.elements.Room;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.calculusmaster.pokecord.util.helpers.event.RaidEventHelper;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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

        //Status Conditions
        for(int i = 0; i < this.players.length; i++) if(!this.players[i].active.isFainted()) this.data(i).canUseMove = this.statusConditionEffects(i);

        if(!this.isComplete())
        {
            for(int i = 0; i < this.getNonBotPlayers().length; i++) if(!this.getAction(i).equals(ActionType.IDLE)) this.moveAction(i);

            this.getRaidBoss().move = new Move(this.getRaidBoss().active.getAllMoves().get(new Random().nextInt(this.getRaidBoss().active.getAllMoves().size())));

            List<Integer> pool = new ArrayList<>();
            for(int i = 0; i < this.getNonBotPlayers().length; i++) pool.add(i);

            pool.stream().filter(this::isUsingMove).collect(Collectors.toList()).sort(Comparator.comparingInt(i -> this.players[(int)i].move.getPriority()).thenComparingInt(i -> this.players[(int)i].active.getStat(Stat.SPD)).reversed());

            if(this.room.equals(Room.TRICK_ROOM)) Collections.reverse(pool);

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

        for(int i = 0; i < this.players.length; i++) this.checkDynamax(i);

        if(this.isComplete())
        {
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
            for(Player p : this.getNonBotPlayers()) p.data.updateBountyProgression(ObjectiveType.WIN_RAID_DUEL);

            int credits = new Random().nextInt(2000) + 1000;
            int ppXP = new Random().nextInt(1000) + 500;
            int pokeXP = new Random().nextInt(100) + 500;

            Map<String, String> pokemonToPlayer = new HashMap<>();
            for(Player p : this.players) pokemonToPlayer.put(p.active.getUUID(), p.ID);

            List<Map.Entry<String, Integer>> sorted = this.damageDealt.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
            Collections.reverse(sorted);

            String highestDamage = this.getNonBotPlayers().length == 1 ? "" : pokemonToPlayer.get(sorted.get(0).getKey());

            for(Player p : this.getNonBotPlayers()) Achievements.grant(p.ID, Achievements.WON_FIRST_RAID, this.event);
            for(Player p : this.getNonBotPlayers()) if(p.ID.equals(highestDamage)) Achievements.grant(p.ID, Achievements.WON_FIRST_RAID_HIGHEST_DAMAGE, this.event);

            StringBuilder winnings = new StringBuilder();

            for(Player p : this.getNonBotPlayers())
            {
                double multiplier = highestDamage.equals(p.ID) ? 1.2 : (p.active.isFainted() ? 0.3 : 1.0);

                p.data.changeCredits((int)(credits * multiplier));
                p.data.addPokePassExp((int)(ppXP * multiplier), this.event);
                p.active.addExp((int)(pokeXP * multiplier));

                winnings.append(p.data.getUsername()).append(" - `").append((int)(credits * multiplier)).append("c`\n");
            }

            winnings.deleteCharAt(winnings.length() - 1);

            embed.setDescription("The Raid Pokemon is defeated!\n\n**Rewards:**\n" + winnings);
        }
        else
        {
            embed.setDescription("Raid Pokemon could not be defeated! No rewards earned!");
        }

        for(Player p : this.getNonBotPlayers()) p.data.updateBountyProgression(ObjectiveType.PARTICIPATE_RAID);
        for(Player p : this.getNonBotPlayers()) Achievements.grant(p.ID, Achievements.COMPLETED_FIRST_RAID, this.event);

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();

        DuelHelper.delete(this.players[0].ID);
        RaidEventHelper.removeServer(this.event.getGuild().getId());
    }

    @Override
    public InputStream getImage() throws Exception
    {
        //Background is 800 x 480 -> 400 x 240
        int baseSize = 150;
        int y = 50;
        int spacing = 25;
        int backgroundW = 400;
        int backgroundH = 240;
        int hint = BufferedImage.TYPE_INT_ARGB;

        Image background = ImageIO.read(new URL(BACKGROUND)).getScaledInstance(backgroundW, backgroundH, hint);
        BufferedImage combined = new BufferedImage(background.getWidth(null), background.getHeight(null), hint);

        combined.getGraphics().drawImage(background, 0, 0, null);

        if(!this.players[0].active.isFainted())
        {
            int size = this.players[0].active.isDynamaxed() ? (int)(baseSize * 1.25) : baseSize;

            Image p1 = ImageIO.read(new URL(this.getPokemonURL(0))).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p1, spacing, y, null);
        }

        if(!this.players[this.players.length - 1].active.isFainted())
        {
            int size = this.players[this.players.length - 1].active.isDynamaxed() ? (int)(baseSize * 1.25) : baseSize;

            Image p2 = ImageIO.read(new URL(this.getPokemonURL(this.players.length - 1))).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p2, (backgroundW - spacing) - size, y, null);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(combined, "png", out);

        byte[] bytes = out.toByteArray(); //This is the slow line

        return new ByteArrayInputStream(bytes);
    }

    @Override
    public boolean isComplete()
    {
        return this.players[this.players.length - 1].active.isFainted() || Arrays.stream(this.getNonBotPlayers()).allMatch(p -> p.active.isFainted());
    }

    @Override
    protected void turnSetup()
    {
        super.turnSetup();

        for(int i = 0; i < this.players.length; i++) this.players[i].move = null;
    }

    public void start()
    {
        this.players = new Player[this.waiting.size() + 1];

        for(int i = 0; i < this.waiting.size(); i++) this.players[i] = new Player(this.waiting.get(i), 1);

        this.setWildPokemon("");
        this.players[this.players.length - 1].active.hpBuff = 1.5 + this.waiting.size();
        this.players[this.players.length - 1].active.statBuff = 1.75 + (0.2 * this.waiting.size());

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
        this.players[this.players.length - 1] = pokemon.equals("") ? new WildPokemon(100) : new WildPokemon(pokemon, 100);

        Player raidBoss = this.players[this.players.length - 1];

        raidBoss.active.setIVs(80);
        raidBoss.active.setEVs("50-50-50-50-50-50");
    }

    @Override
    public void setDefaults()
    {
        this.waiting = new ArrayList<>();
        this.players = new Player[0];
        super.setDefaults();
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
        //TODO: Set the message received event
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
