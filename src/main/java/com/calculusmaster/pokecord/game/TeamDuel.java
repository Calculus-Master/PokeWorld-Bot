package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.XPBooster;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;

import static com.calculusmaster.pokecord.game.DuelHelper.*;

/**Temporary, will replace Duel if complete
 * @see com.calculusmaster.pokecord.commands.duel.CommandTeamDuel
 */
public class TeamDuel
{
    private DuelStatus status;
    private MessageReceivedEvent event;
    private int size;
    private Player[] players;
    private Map<String, TurnAction> queuedMoves = new HashMap<>();

    private int turn;
    private int current;
    private int other;

    private List<String> results;

    public static TeamDuel create(String player1ID, String player2ID, int size, MessageReceivedEvent event)
    {
        TeamDuel duel = new TeamDuel();

        duel.setStatus(DuelStatus.WAITING);
        duel.setEvent(event);
        duel.setPlayers(player1ID, player2ID, size);

        DUELS.add(duel);
        return duel;
    }

    //Main Duel Logic
    public void turnHandler()
    {
        this.setStatus(DuelStatus.DUELING);

        this.results = new ArrayList<>();

        this.players[0].move = null;
        this.players[1].move = null;

        //Both players are using a move
        if(!this.queuedMoves.get(this.players[0].ID).action().equals(ActionType.SWAP) && !this.queuedMoves.get(this.players[1].ID).action().equals(ActionType.SWAP))
        {
            //Base Move
            this.players[0].move = new Move(this.players[0].active.getLearnedMoves().get(this.queuedMoves.get(this.players[0].ID).moveInd() - 1));
            this.players[1].move = new Move(this.players[0].active.getLearnedMoves().get(this.queuedMoves.get(this.players[0].ID).moveInd() - 1));

            //Check if Z-Move
            if(this.getAction(0).equals(ActionType.ZMOVE)) this.players[0].move = DuelHelper.getZMove(this.players[0], this.players[0].move);
            if(this.getAction(1).equals(ActionType.ZMOVE)) this.players[1].move = DuelHelper.getZMove(this.players[1], this.players[1].move);

            //Set who attacks first
            int speed1 = this.players[0].active.getStat(Stat.SPD);
            int speed2 = this.players[1].active.getStat(Stat.SPD);

            if(this.players[0].move.getPriority() == this.players[1].move.getPriority()) this.current = speed1 == speed2 ? (new Random().nextInt(100) < 50 ? 0 : 1) : (speed1 > speed2 ? 0 : 1);
            else this.current = this.players[0].move.getPriority() > this.players[1].move.getPriority() ? 0 : 1;

            this.other = this.current == 0 ? 1 : 0;

            //Do moves
            results.add(this.turn(this.players[this.current].move));

            if(!this.players[this.other].active.isFainted())
            {
                this.current = this.current == 0 ? 1 : 0;
                this.other = this.current == 0 ? 1 : 0;

                results.add("\n" + this.turn(this.players[this.current].move));
            }
            else results.add("\n" + this.players[this.other].active.getName() + " fainted!");
        }
        //Either player wants to swap out a pokemon
        else
        {
            boolean player1Swap = this.getAction(0).equals(ActionType.SWAP);

            if(player1Swap) //Player 1 wants to swap
            {
                this.current = 1;
                this.other = 0;

                Move move = new Move(this.players[1].active.getLearnedMoves().get(this.queuedMoves.get(this.players[1].ID).moveInd() - 1));
                if(this.getAction(1).equals(ActionType.ZMOVE)) move = DuelHelper.getZMove(this.players[1], move);

                int ind = this.queuedMoves.get(this.players[0].ID).swapInd() - 1;
                this.players[0].swap(ind);

                results.add(this.players[0].data.getUsername() + " brought in " + this.players[0].active.getName() + "!\n");
                results.add(this.turn(move));
            }
            else //Player 2 wants to swap
            {
                this.current = 0;
                this.other = 1;

                Move move = new Move(this.players[0].active.getLearnedMoves().get(this.queuedMoves.get(this.players[0].ID).moveInd() - 1));
                if(this.getAction(0).equals(ActionType.ZMOVE)) move = DuelHelper.getZMove(this.players[0], move);

                int ind = this.queuedMoves.get(this.players[1].ID).swapInd() - 1;
                this.players[1].swap(ind);

                results.add(this.players[1].data.getUsername() + " brought in " + this.players[1].active.getName() + "!\n");
                results.add(this.turn(move));
            }
        }

        if(this.isComplete())
        {
            this.sendTurnEmbed();
            this.sendWinEmbed();
            this.setStatus(DuelStatus.COMPLETE);
        }
        else
        {
            try
            {
                this.sendTurnEmbed();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        this.queuedMoves.clear();
    }

    //Always use this.current!
    public String turn(Move move)
    {
        this.players[this.other].active.damage(300);
        return this.players[this.current].active.getName() + " used " + move.getName() + "!";
    }

    //Response Embeds

    public void sendTurnEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        StringBuilder s = new StringBuilder();

        s.append(this.getHealthBars()).append("\n\n");

        if(this.results != null) for(String str : this.results) s.append(str).append(" ");

        embed.setDescription(s.toString().trim());

        embed.setImage("attachment://duel.png");

        try
        {
            this.event.getChannel().sendFile(this.getImage(), "duel.png").embed(embed.build()).queue();
        }
        catch (Exception e)
        {
            this.event.getChannel().sendMessage(embed.build()).queue();
            e.printStackTrace();
        }
    }

    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        int c = this.giveWinCredits();
        int exp = this.giveWinExp();
        embed.setDescription(this.getWinner().data.getUsername() + " has won!\nThey earned " + c + " credits and no exp (WIP)!");

        this.event.getChannel().sendMessage(embed.build()).queue();

        DuelHelper.delete(this.players[0].ID);
    }

    private int giveWinExp()
    {
        double booster = this.getWinner().data.hasXPBooster() ? XPBooster.getInstance(this.getWinner().data.getXPBoosterLength()).boost : 1.0;

        //int winExp = (int)(booster * win.getDuelExp(this.playerPokemon[winner == 0 ? 1 : 0]));
        //win.addExp(this.winExp);
        //win.gainEVs(this.playerPokemon[winner == 0 ? 1 : 0]);

        //Pokemon.updateExperience(win);
        //Pokemon.updateEVs(win);
        return 0;
    }

    private int giveWinCredits()
    {
        int winCredits = new Random().nextInt(501) + 500;
        this.getWinner().data.changeCredits(winCredits);
        return winCredits;
    }

    //Useful Getters/Setters
    public void submitMove(String id, int moveIndex, boolean z)
    {
        this.queuedMoves.put(id, new TurnAction(z ? ActionType.ZMOVE : ActionType.MOVE, moveIndex, -1));
    }

    public void submitMove(String id, int swapIndex)
    {
        if(this.players[this.indexOf(id)].team.get(swapIndex - 1).isFainted())
        {
            this.event.getChannel().sendMessage("That pokemon is fainted!").queue();
            return;
        }

        this.queuedMoves.put(id, new TurnAction(ActionType.SWAP, -1, swapIndex));
    }

    public void checkReady()
    {
        if(this.queuedMoves.containsKey(this.players[0].ID) && this.queuedMoves.containsKey(this.players[1].ID))
        {
            turnHandler();
        }
    }

    public boolean hasPlayerSubmittedMove(String id)
    {
        return this.queuedMoves.containsKey(id);
    }

    public boolean isComplete()
    {
        return this.players[0].lost() || this.players[1].lost();
    }

    public ActionType getAction(int player)
    {
        return this.queuedMoves.get(this.players[player].ID).action();
    }

    public Player getWinner()
    {
        return this.players[0].lost() ? this.players[1] : this.players[0];
    }

    public InputStream getImage() throws Exception
    {
        //Background is 800 x 480 -> 400 x 240

        int size = 125;
        int hint = BufferedImage.TYPE_INT_ARGB;

        Image background = ImageIO.read(new URL(BACKGROUND)).getScaledInstance(400, 240, hint);
        BufferedImage combined = new BufferedImage(background.getWidth(null), background.getHeight(null), hint);

        combined.getGraphics().drawImage(background, 0, 0, null);

        if(!this.players[0].active.isFainted())
        {
            Image p1 = ImageIO.read(this.players[0].active.getURL()).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p1, 50, 70, null);
        }

        if(!this.players[1].active.isFainted())
        {
            Image p2 = ImageIO.read(this.players[1].active.getURL()).getScaledInstance(size, size, hint);
            combined.getGraphics().drawImage(p2, 350 - size, 70, null);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(combined, "png", out);

        byte[] bytes = out.toByteArray(); //This is the slow line

        return new ByteArrayInputStream(bytes);
    }

    private String getHealthBars()
    {
        String healthBarP1 = this.getHB(0);
        String healthBarP2 = this.getHB(1);

        return this.isComplete() ? "" : healthBarP1 + "\n" + healthBarP2;
    }

    private String getHB(int p)
    {
        StringBuilder sb = new StringBuilder().append(this.players[p].data.getUsername()).append("'s ").append(this.players[p].active.getName()).append(": ");

        if(this.players[p].active.isFainted()) sb.append("FAINTED");
        else sb.append(this.players[p].active.getHealth()).append(" / ").append(this.players[p].active.getStat(Stat.HP)).append(" HP ").append(this.players[p].active.getActiveStatusConditions());

        return sb.toString();
    }

    //Core Getters and Setters

    public void setStatus(DuelStatus status)
    {
        this.status = status;
    }

    public DuelStatus getStatus()
    {
        return this.status;
    }

    private void setEvent(MessageReceivedEvent event)
    {
        this.event = event;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getSize()
    {
        return this.size;
    }

    public void setPlayers(String player1ID, String player2ID, int size)
    {
        this.players = new Player[]{new Player(player1ID, size), new Player(player2ID, size)};
    }

    public Player[] getPlayers()
    {
        return this.players;
    }

    public int indexOf(String id)
    {
        return this.players[0].ID.equals(id) ? 0 : 1;
    }

    public boolean hasPlayer(String id)
    {
        return this.players[0].ID.equals(id) || this.players[1].ID.equals(id);
    }
}
