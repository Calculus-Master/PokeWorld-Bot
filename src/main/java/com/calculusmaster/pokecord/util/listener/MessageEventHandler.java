package com.calculusmaster.pokecord.util.listener;

import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.evolution.EvolutionData;
import com.calculusmaster.pokecord.game.pokemon.evolution.EvolutionRegistry;
import com.calculusmaster.pokecord.game.pokemon.evolution.PokemonEgg;
import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.mongo.ServerDataQuery;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;
import java.util.function.Consumer;

public class MessageEventHandler
{
    private Random random;
    private MessageReceivedEvent event;
    private PlayerData data;
    private boolean canSendMessage;

    public MessageEventHandler()
    {
        this.random = new Random();
    }

    public void updateEvent(MessageReceivedEvent event)
    {
        //Reassign the SplittableRandom object every once in a while
        if(this.random.nextInt(5) < 1) this.random = new Random(System.currentTimeMillis());

        this.event = event;
        if(PlayerData.isRegistered(event.getAuthor().getId())) this.data = PlayerData.build(event.getAuthor().getId());
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

        this.send("You earned a Redeem!");
    }

    private void activateExperienceEvent()
    {
        Pokemon p = this.data.getSelectedPokemon();

        if(p.getLevel() >= 100) return;

        int initL = p.getLevel();

        int experience;
        int required = GrowthRate.getRequiredExp(p.getData().getGrowthRate(), p.getLevel());

        //Messages: 0.1% - 10% of the required exp
        if(p.getLevel() < 25)
        {
            double fraction = this.random.nextInt(1, 100) / 1000D;
            experience = (int)(fraction * required);
        }
        //Messages: 10% to 150% of 5% of the required exp
        else if(p.getLevel() < 40)
        {
            int base = required / 100 * 5;
            experience = this.random.nextInt((int)(base * 0.1), (int)(base * 1.5));
        }
        //Messages: 10% to 150% of 20% of the required exp from 5 levels below
        else if(p.getLevel() < 80)
        {
            required = GrowthRate.getRequiredExp(p.getData().getGrowthRate(), p.getLevel() - 5);
            int base = (int)(required * 0.2);
            experience = this.random.nextInt((int)(base * 0.1), (int)(base * 1.5));
        }
        else experience = this.random.nextInt(1000, 5000);

        if(experience == 0) experience = 50;

        p.addExp(experience);

        this.data.updateObjective(ObjectiveType.EARN_XP_POKEMON, experience);

        //Level Up Occurred
        if(p.getLevel() != initL)
        {
            this.data.updateObjective(ObjectiveType.LEVEL_POKEMON, p.getLevel() - initL);
            this.data.addExp(PMLExperience.LEVEL_POKEMON, 100);

            this.send("Your " + p.getDisplayName() + " is now **Level " + p.getLevel() + "**!");

            if(EvolutionRegistry.hasEvolutionData(p.getEntity()) && EvolutionRegistry.getEvolutionData(p.getEntity()).stream().anyMatch(EvolutionData::hasLevelTrigger))
                EvolutionRegistry.checkAutomaticEvolution(p, this.data, this.event.getGuild().getId());
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

            this.data.getStatistics().increase(StatisticType.EGGS_HATCHED);

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
