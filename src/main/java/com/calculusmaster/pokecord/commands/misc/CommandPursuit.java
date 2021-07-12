package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.bounties.components.Bounty;
import com.calculusmaster.pokecord.game.bounties.components.PursuitBuilder;
import com.calculusmaster.pokecord.game.bounties.enums.PursuitSize;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class CommandPursuit extends Command
{
    private static final String NO_PURSUIT_MESSAGE = "You do not have an active Pursuit! Use either `p!pursuit info` to see more information, or `p!pursuit start` to start a Pursuit!";

    public CommandPursuit(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        boolean start = this.msg.length >= 2 && this.msg[1].equals("start");
        boolean info = this.msg.length == 2 && this.msg[1].equals("info");
        boolean advance = this.msg.length == 2 && Arrays.asList("advance", "continue", "next").contains(this.msg[1]);

        if(start)
        {
            if(this.playerData.hasPursuit()) this.sendMsg("You already have an active Pursuit!");
            else
            {
                PursuitSize size = this.msg.length == 3 && PursuitSize.cast(this.msg[2]) != null ? PursuitSize.cast(this.msg[2]) : PursuitSize.AVERAGE;

                this.sendMsg("Generating your Pursuit...");
                PursuitBuilder pursuit = PursuitBuilder.create(size);
                this.playerData.setPursuit(pursuit.getIDs());
                this.playerData.increasePursuitLevel();
                pursuit.build();
                this.sendMsg("Your Pursuit was created!");
            }
        }
        else if(info)
        {
            this.embed.setDescription("Pursuits are large amounts of bounties that grant extra rewards per bounty as well as a large reward after completing one. Pursuits come in multiple sizes, as shown below.");
            this.embed.setTitle("Pursuit Info");

            for(PursuitSize s : PursuitSize.values())
            {
                this.embed.addField(s.toString(), s.getOverview(), true);
            }

            if(this.playerData.hasPursuit()) this.embed.setFooter("You have an active Pursuit! Type `p!pursuit` to see your progress.");
            else this.embed.setFooter("You do not have an active Pursuit! To start one, type `p!pursuit start <size>`. If you leave out the <size> argument, the Pursuit will default to a size of AVERAGE");
        }
        else if(advance)
        {
            if(!this.playerData.hasPursuit()) this.sendMsg(NO_PURSUIT_MESSAGE);
            else
            {
                Bounty b = this.playerData.getCurrentPursuitBounty();

                if(!b.getObjective().isComplete()) this.sendMsg("You have not completed this bounty's objective yet!");
                else
                {
                    int count = this.playerData.getPursuitIDs().size();
                    PursuitSize pursuitSize = PursuitSize.get(count);

                    //Bounty Rewards
                    this.playerData.changeCredits((int)(b.getReward() * pursuitSize.multiplier));

                    this.playerData.getStats().incr(PlayerStatistic.BOUNTIES_COMPLETED);
                    Achievements.grant(this.player.getId(), Achievements.COMPLETED_FIRST_BOUNTY, this.event);

                    //Delete the Bounty from DB
                    Bounty.delete(b.getBountyID());

                    this.playerData.increasePursuitLevel();

                    if(this.playerData.getPursuitLevel() > this.playerData.getPursuitIDs().size())
                    {
                        int creditReward = pursuitSize.finalRewardCredits;

                        //Pursuit Final Rewards
                        this.playerData.addPokePassExp(pursuitSize.getPokePassXPReward(count), this.event);
                        this.playerData.changeCredits(creditReward);

                        this.playerData.removePursuit();

                        this.sendMsg("You have conquered this Pursuit! You earned " + creditReward + " credits and a large amount of PokePass XP!");
                    }
                    else this.sendMsg("You are now at Pursuit Level " + this.playerData.getPursuitLevel() + " / " + this.playerData.getPursuitIDs().size() + "!");
                }
            }
        }
        else
        {
            if(!this.playerData.hasPursuit())
            {
                this.sendMsg(NO_PURSUIT_MESSAGE);
            }
            else
            {
                int level = this.playerData.getPursuitLevel();
                Bounty current = this.playerData.getCurrentPursuitBounty();

                this.embed.addField("Pursuit Bounty", current.getOverview(), true);
                this.embed.setTitle(this.player.getName() + "'s Pursuit");
                this.embed.setDescription("Pursuit Progress: " + level + " / " + this.playerData.getPursuitIDs().size());
            }
        }

        return this;
    }
}
