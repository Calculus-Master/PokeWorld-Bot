package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.bounties.Bounty;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandBounties extends Command
{
    public CommandBounties(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        this.checkBountyCount();
        List<Bounty> bounties = this.playerData.getBounties();

        boolean collect = this.msg.length >= 3 && this.msg[1].equals("collect") && this.isNumeric(2) && this.getInt(2) > 0 && this.getInt(2) <= bounties.size();
        boolean reroll = this.msg.length == 3 && this.msg[1].equals("reroll") && this.isNumeric(2) && this.getInt(2) > 0 && this.getInt(2) <= bounties.size();
        boolean acquire = this.msg.length >= 2 && Arrays.asList("acquire", "get", "new").contains(this.msg[1]);

        if(collect)
        {
            Bounty b = bounties.get(this.getInt(2) - 1);

            if(!b.getObjective().isComplete())
            {
                this.sendMsg("You have not finished this Bounty's Objective yet!");
            }
            else
            {
                this.playerData.changeCredits(b.getReward());
                this.playerData.removeBounty(b.getBountyID());

                Bounty.delete(b.getBountyID());

                this.playerData.addPokePassExp(Bounty.POKEPASS_EXP_YIELD, this.event);
                this.playerData.getStats().incr(PlayerStatistic.BOUNTIES_COMPLETED);
                Achievements.grant(this.player.getId(), Achievements.COMPLETED_FIRST_BOUNTY, this.event);

                if(new Random().nextInt(50) < 10 && this.playerData.getSelectedPokemon().getLevel() != 100) this.playerData.getSelectedPokemon().addExp(500);

                this.sendMsg("You earned " + b.getReward() + " credits and some PokePass XP for completing this bounty!");
            }
        }
        else if(reroll)
        {
            Bounty b = bounties.get(this.getInt(2) - 1);
            int newReward = b.getReward() / 2;

            Bounty rerolledBounty = Bounty.create().setReward(newReward);

            this.playerData.removeBounty(b.getBountyID());
            Bounty.delete(b.getBountyID());

            this.playerData.addBounty(rerolledBounty.getBountyID());
            Bounty.toDB(rerolledBounty);

            this.sendMsg("Rerolled Bounty with lower rewards!");
        }
        else if(acquire)
        {
            if(bounties.size() == Bounty.MAX_BOUNTIES_HELD)
            {
                this.sendMsg("You already have the maximum amount of Bounties! Either complete them, or use `p!bounties reroll <num>` to reroll the objective!");
                return this;
            }

            int requested = this.msg.length == 3 && this.isNumeric(2) ? this.getInt(2) : 1;
            requested = Math.min(requested, Bounty.MAX_BOUNTIES_HELD - bounties.size());

            for(int i = 0; i < requested; i++)
            {
                Bounty b = Bounty.create();

                Bounty.toDB(b);
                this.playerData.addBounty(b.getBountyID());
            }

            this.sendMsg("You acquired " + requested + " new " + (requested > 1 ? "bounties!" : "bounty!"));
        }
        else
        {
            this.embed = new EmbedBuilder();

            for(int i = 0; i < bounties.size(); i++)
            {
                this.embed.addField("Bounty " + (i + 1), bounties.get(i).getOverview(), true);
            }

            this.embed.setTitle(this.player.getName() + "'s Bounties");
            this.embed.setDescription("Bounties are short tasks with small rewards!");
        }
        return this;
    }

    private void checkBountyCount()
    {
        int count = this.playerData.getBountyIDs().size();

        if(count == 0)
        {
            int added = Bounty.MAX_BOUNTIES_HELD;

            for(int i = 0; i < added; i++)
            {
                Bounty b = Bounty.create();

                Bounty.toDB(b);
                this.playerData.addBounty(b.getBountyID());
            }

            this.sendMsg("You acquired " + added + " new bounties!");
        }
    }
}
