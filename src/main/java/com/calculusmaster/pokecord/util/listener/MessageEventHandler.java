package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonEgg;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;
import java.util.SplittableRandom;
import java.util.function.Consumer;

public class MessageEventHandler
{
    private SplittableRandom random;
    private MessageReceivedEvent event;
    private PlayerDataQuery data;
    private boolean canSendMessage;

    public MessageEventHandler()
    {
        this.random = new SplittableRandom();
    }

    public void updateEvent(MessageReceivedEvent event)
    {
        //Reassign the SplittableRandom object every once in a while
        if(this.random.nextInt(5) < 1) this.random = new SplittableRandom(System.currentTimeMillis());

        this.event = event;
        if(PlayerDataQuery.isRegistered(event.getAuthor().getId())) this.data = PlayerDataQuery.ofNonNull(event.getAuthor().getId());
        this.canSendMessage = new ServerDataQuery(event.getGuild().getId()).isAbleToSendMessages(event.getChannel().getId());
    }

    public void activateEvent(MessageEvent event)
    {
        if(this.canEventActivate(event)) ThreadPoolHandler.LISTENER_EVENT.execute(() -> event.activator.accept(this));
    }

    private boolean canEventActivate(MessageEvent event)
    {
        return this.random.nextInt(event.total) < event.chance;
    }

    private void send(String content)
    {
        if(this.canSendMessage) this.event.getChannel().sendMessage(this.data.getMention() + "\n" + content).queue();
        else this.data.directMessage(content);
    }

    //Event Activate Methods
    private void activateRedeemEvent()
    {
        this.data.changeRedeems(1);

        this.data.getStatistics().incr(PlayerStatistic.NATURAL_REDEEMS_EARNED);

        this.send("You earned a Redeem!");
    }

    private void activateExperienceEvent()
    {
        Pokemon p = this.data.getSelectedPokemon();

        if(p.getLevel() >= 100) return;

        int initL = p.getLevel();

        int experience;
        int required = GrowthRate.getRequiredExp(p.getData().growthRate, p.getLevel());
        final SplittableRandom r = new SplittableRandom();

        //Messages: 0.1% - 10% of the required exp
        if(p.getLevel() < 25)
        {
            double fraction = r.nextInt(1, 100) / 1000D;
            experience = (int)(fraction * required);
        }
        //Messages: 10% to 150% of 5% of the required exp
        else if(p.getLevel() < 40)
        {
            int base = required / 100 * 5;
            experience = r.nextInt((int)(base * 0.1), (int)(base * 1.5));
        }
        //Messages: 10% to 150% of 20% of the required exp from 5 levels below
        else if(p.getLevel() < 80)
        {
            required = GrowthRate.getRequiredExp(p.getData().growthRate, p.getLevel() - 5);
            int base = (int)(required * 0.2);
            experience = r.nextInt((int)(base * 0.1), (int)(base * 1.5));
        }
        else experience = new SplittableRandom().nextInt(1000, 5000);

        if(experience == 0) experience = 50;

        //Augment Modifiers
        if(p.hasAugment(PokemonAugment.XP_BOOST_I)) experience *= 1.025;
        else if(p.hasAugment(PokemonAugment.XP_BOOST_II)) experience *= 1.05;
        else if(p.hasAugment(PokemonAugment.XP_BOOST_III)) experience *= 1.1;
        else if(p.hasAugment(PokemonAugment.XP_BOOST_IV)) experience *= 1.175;
        else if(p.hasAugment(PokemonAugment.XP_BOOST_V)) experience *= 1.275;

        p.addExp(experience);

        this.data.updateBountyProgression(ObjectiveType.EARN_XP_POKEMON, experience);

        if(p.getLevel() != initL)
        {
            this.data.updateBountyProgression(ObjectiveType.LEVEL_POKEMON, p.getLevel() - initL);

            this.send("Your " + p.getName() + " is now Level " + p.getLevel() + "!");
        }

        p.updateExperience();
    }

    private void activateEggExperienceEvent()
    {
        if(!this.data.hasActiveEgg()) return;

        int experience = new Random().nextInt(250) + 50;

        PokemonEgg egg = this.data.getActiveEgg();

        egg.addExp(experience);

        if(egg.canHatch())
        {
            Pokemon p = egg.hatch();

            p.upload();
            this.data.addPokemon(p.getUUID());
            this.data.removeActiveEgg();
            this.data.removeEgg(egg.getEggID());

            this.data.getStatistics().incr(PlayerStatistic.EGGS_HATCHED);

            Achievements.grant(this.data.getID(), Achievements.HATCHED_FIRST_EGG, event);
            if(p.getTotalIVRounded() >= 60) Achievements.grant(this.data.getID(), Achievements.HATCHED_FIRST_DECENT_IV, event);
            if(p.getTotalIVRounded() >= 70) Achievements.grant(this.data.getID(), Achievements.HATCHED_FIRST_GREAT_IV, event);
            if(p.getTotalIVRounded() >= 80) Achievements.grant(this.data.getID(), Achievements.HATCHED_FIRST_EXCELLENT_IV, event);
            if(p.getTotalIVRounded() >= 90) Achievements.grant(this.data.getID(), Achievements.HATCHED_FIRST_NEARLY_PERFECT_IV, event);

            this.send("Your Egg hatched into a new " + p.getName() + "!");
        }
    }

    public enum MessageEvent
    {
        REDEEM(1, 5000, MessageEventHandler::activateRedeemEvent),
        EXPERIENCE(1, 10, MessageEventHandler::activateExperienceEvent),
        EGG_EXPERIENCE(1, 10, MessageEventHandler::activateEggExperienceEvent);

        final int chance;
        final int total;
        final Consumer<MessageEventHandler> activator;
        MessageEvent(int chance, int total, Consumer<MessageEventHandler> activator) { this.chance = chance; this.total = total; this.activator = activator;}
    }
}
