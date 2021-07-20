package com.calculusmaster.pokecord.game.duel;

import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.elements.Player;
import com.calculusmaster.pokecord.game.duel.elements.Trainer;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.calculusmaster.pokecord.game.duel.DuelHelper.DUELS;
import static com.calculusmaster.pokecord.game.duel.DuelHelper.DuelStatus;

public class TrainerDuel extends Duel
{
    public static Duel create(String playerID, MessageReceivedEvent event, Trainer.TrainerInfo trainer)
    {
        TrainerDuel duel = new TrainerDuel();

        duel.setStatus(DuelStatus.WAITING);
        duel.setEvent(event);
        duel.setPlayers(playerID, trainer.name, trainer.pokemon.size());
        duel.setTrainer(trainer);
        duel.limitPlayerPokemon(trainer.pokemonLevel);
        duel.setDefaults();
        duel.setDuelPokemonObjects(0);
        duel.setDuelPokemonObjects(1);

        DUELS.add(duel);
        return duel;
    }

    @Override
    public void sendWinEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        boolean elite = ((Trainer)this.players[1]).info.elite;

        //Player won
        if(this.getWinner().ID.equals(this.players[0].ID))
        {
            //TODO: Trainer Duel per battle credits? (Bonus for defeating all is already a thing)
            //int c = this.giveWinCredits();
            int c = 0;

            this.uploadEVs(0);
            this.uploadExperience();

            String bot = ((Trainer)this.players[1]).info.name;

            embed.setDescription("You defeated " + bot + "!");

            Achievements.grant(this.players[0].ID, Achievements.WON_FIRST_TRAINER_DUEL, this.event);

            this.players[0].data.addPokePassExp(500, this.event);

            //Elite Trainer
            if(elite)
            {
                int credits = new Random().nextInt(500) + 500;
                this.players[0].data.changeCredits(credits);

                Achievements.grant(this.players[0].ID, Achievements.DEFEATED_FIRST_ELITE_TRAINER, this.event);
                this.players[0].data.getStats().incr(PlayerStatistic.ELITE_TRAINER_DUELS_WON);
                this.players[0].data.updateBountyProgression(b -> {
                    if(b.getType().equals(ObjectiveType.WIN_ELITE_DUEL) || b.getType().equals(ObjectiveType.COMPLETE_ELITE_DUEL)) b.update();
                });

                this.event.getChannel().sendMessage(this.players[0].data.getMention() + ": You defeated the Elite Trainer and earned " + credits + " credits!").queue();
            }
            //Regular Daily Trainer
            else
            {
                List<String> playersDefeatedBot = new ArrayList<>(Trainer.PLAYER_TRAINERS_DEFEATED.get(bot));
                if(!playersDefeatedBot.contains(this.players[0].ID))
                {
                    playersDefeatedBot.add(this.players[0].ID);
                    Trainer.PLAYER_TRAINERS_DEFEATED.put(bot, playersDefeatedBot);
                    this.players[0].data.getStats().incr(PlayerStatistic.TRAINER_DUELS_WON);

                    boolean dailyComplete = true;
                    for(Trainer.TrainerInfo ti : Trainer.DAILY_TRAINERS) if(!Trainer.PLAYER_TRAINERS_DEFEATED.get(ti.name).contains(this.players[0].ID)) dailyComplete = false;

                    if(dailyComplete)
                    {
                        //TODO: Win Credits
                        int winCredits = 1;
                        this.players[0].data.changeCredits(winCredits);
                        //TODO: Trainer Duels need to be challenging to players of all levels
                        this.event.getChannel().sendMessage(this.players[0].data.getMention() + ": You defeated all of today's trainers! You earned a bonus " + winCredits + " credits! DAILY BONUS IS CURRENTLY DISABLED!").queue();

                        Achievements.grant(this.players[0].ID, Achievements.DEFEATED_DAILY_TRAINERS, this.event);
                    }
                }
            }
        }
        //Player lost
        else
        {
            this.uploadEVs(0);

            if(elite) this.players[0].data.updateBountyProgression(ObjectiveType.COMPLETE_ELITE_DUEL);
            embed.setDescription("You were defeated by " + ((Trainer)this.players[1]).info.name + "!");
        }

        this.event.getChannel().sendMessageEmbeds(embed.build()).queue();
        DuelHelper.delete(this.players[0].ID);
    }

    @Override
    public void sendTurnEmbed()
    {
        super.sendTurnEmbed();

        if(!this.isComplete() && this.players[1].active.isFainted())
        {
            this.submitMove(this.players[1].ID, 1, 'm');
            this.checkReady();
        }
    }

    @Override
    public void checkReady()
    {
        if(this.queuedMoves.containsKey(this.players[0].ID))
        {
            turnHandler();
        }
    }

    @Override
    protected void turnSetup()
    {
        super.turnSetup();

        int index;
        char type;

        if(this.players[0].active.isFainted() && !this.players[1].active.isFainted())
        {
            index = -1;
            type = 'i';
        }
        //If active is fainted, AI needs to swap
        else if(this.players[1].active.isFainted())
        {
            int ind = -1;
            for(int i = 0; i < this.players[1].team.size(); i++)
            {
                if(!this.players[1].team.get(i).isFainted())
                {
                    ind = i + 1;
                    i = this.players[1].team.size();
                }
            }

            index = ind;
            type = 's';
        }
        //Z-Move
        else if(!this.players[1].usedZMove)
        {
            int ind = new Random().nextInt(4);
            Move move = new Move(this.players[1].active.getLearnedMoves().get(ind));

            index = ind + 1;

            if(this.players[1].data.hasZCrystal(ZCrystal.getCrystalOfType(move.getType()).getStyledName())) type = 'z';
            else type = 'm';

        }
        //Dynamax
        else if(!this.players[1].usedDynamax && new Random().nextInt(100) < 33)
        {
            index = new Random().nextInt(4) + 1;
            type = 'd';
        }
        //Normal Move
        else
        {
            index = new Random().nextInt(4) + 1;
            type = 'm';
        }

        this.submitMove(this.players[1].ID, index, type);
    }

    protected void limitPlayerPokemon(int level)
    {
        for(int i = 0; i < this.players[0].team.size(); i++)
        {
            if(this.players[0].team.get(i).getLevel() >= level + 5)
            {
                this.players[0].team.get(i).setLevel(level + 5);
                this.players[0].team.get(i).setHealth(this.players[0].team.get(0).getStat(Stat.HP));
            }
        }

        this.players[0].active = this.players[0].team.get(0);
    }

    @Override
    public void setPlayers(String player1ID, String player2ID, int size)
    {
        this.players = new Player[]{new Player(player1ID, size), null};
        this.size = size;
    }

    private void setTrainer(Trainer.TrainerInfo info)
    {
        this.players[1] = Trainer.create(info);
        Random r = new Random();

        if(info.elite)
        {
            StringBuilder ivs = new StringBuilder();
            for(int i = 0; i < 6; i++) ivs.append(r.nextInt(12) + 20).append("-");
            ivs.deleteCharAt(ivs.length() - 1);

            StringBuilder evs = new StringBuilder();
            for(int i = 0; i < 6; i++) evs.append(r.nextInt(203) + 50).append("-");
            evs.deleteCharAt(ivs.length() - 1);

            for(int i = 0; i < this.players[1].team.size(); i++)
            {
                this.players[1].team.get(i).setIVs(ivs.toString());
                this.players[1].team.get(i).setEVs(evs.toString());
                this.players[1].team.get(i).setHealth(this.players[1].team.get(i).getStat(Stat.HP));
                this.players[1].team.get(i).setShiny(r.nextInt(100) < 33);
            }

            this.players[1].active = this.players[1].team.get(0);

            return;
        }

        int highest = this.players[0].team.get(0).getEVTotal();
        String condensed = this.players[0].team.get(0).getVCondensed(this.players[0].team.get(0).getEVs());
        //Copy EVs
        for(int i = 0; i < this.players[0].team.size(); i++)
        {
            if(this.players[1].team.get(i).getEVTotal() > highest)
            {
                highest = this.players[0].team.get(i).getEVTotal();
                condensed = this.players[0].team.get(i).getVCondensed(this.players[0].team.get(i).getEVs());
            }
        }

        for(int i = 0; i < this.players[1].team.size(); i++)
        {
            this.players[1].team.get(i).setEVs(condensed);
            this.players[1].team.get(i).setHealth(this.players[1].team.get(i).getStat(Stat.HP));
        }

        this.players[1].active = this.players[1].team.get(0);
    }
}
