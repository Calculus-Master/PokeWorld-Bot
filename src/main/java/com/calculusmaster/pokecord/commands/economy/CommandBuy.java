package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Nature;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class CommandBuy extends Command
{
    //Prices
    public static final int COST_MEGA = 2000;
    public static final int COST_FORM = 1500;
    public static final int COST_NATURE = 200;
    public static final int COST_RARE_CANDY = 500;
    public static final int COST_MOVETUTOR = 10000;

    public CommandBuy(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        Pokemon selected = this.playerData.getSelectedPokemon();

        if(this.msg[1].equals("nature") && this.msg.length == 3)
        {
            if(this.playerData.getCredits() >= CommandBuy.COST_NATURE && Nature.cast(this.msg[2]) != null)
            {
                this.embed.setDescription(selected.getName() + "'s Nature was changed from " + Global.normalCase(selected.getNature().toString()) + " to " + Global.normalCase(this.msg[2]));
                selected.setNature(this.msg[2]);
                this.playerData.changeCredits(-1 * CommandBuy.COST_NATURE);
            }
            else this.embed.setDescription("You do not have enough money!");
        }
        else if(this.msg[1].equals("candy"))
        {
            int num = this.msg.length > 2 && isNumeric(2) && Integer.parseInt(this.msg[2]) > 0 ? Math.min(100, Integer.parseInt(this.msg[2])) : 1;
            int cost = num * COST_RARE_CANDY;

            if(this.playerData.getCredits() >= cost)
            {
                if(selected.getLevel() + num > 100)
                {
                    num = 100 - selected.getLevel();
                    cost = num * COST_RARE_CANDY;
                }

                this.embed.setDescription("Bought " + num + " rare candies for a total of " + cost + " credits! " + selected.getName() + " leveled up to Level " + (selected.getLevel() + num) + "!");
                this.playerData.changeCredits(-1 * cost);
                selected.setLevel(selected.getLevel() + num);
            }
            else this.embed.setDescription("You do not have enough money for " + num + " rare candies");
        }
        else if(this.msg[1].equals("item") && this.msg.length == 3)
        {
            int cost = isNumeric(2) ? CommandShop.itemPrices.get(Integer.parseInt(this.msg[2]) - 1) : -1;
            if(cost > 0 && this.playerData.getCredits() >= cost)
            {
                PokeItem item = CommandShop.entriesItem.get(Integer.parseInt(this.msg[2]) - 1);

                this.playerData.changeCredits(-1 * cost);
                this.playerData.addItem(item.getName());

                this.embed.setDescription("Bought `" + item.getStyledName() + "` for " + cost + "c!");
            }
        }
        else if(this.msg[1].equals("form") && this.msg.length >= 3)
        {
            StringBuilder formBuilder = new StringBuilder();
            for(int i = 2; i < this.msg.length; i++) formBuilder.append(this.msg[i]).append(" ");
            String form = Global.normalCase(formBuilder.toString().trim());

            if(!selected.getName().contains("Aegislash") && selected.hasForms() && this.playerData.getCredits() >= CommandBuy.COST_FORM && selected.getFormsList().contains(form))
            {
                this.embed.setDescription(selected.getName() + " transformed into " + form);
                selected.changeForm(form);
                this.playerData.changeCredits(-1 * CommandBuy.COST_FORM);
            }
            else if(this.playerData.getCredits() < CommandBuy.COST_FORM)
            {
                this.embed.setDescription("You do not have enough money! You need " + (CommandBuy.COST_FORM - this.playerData.getCredits()) + " more credits!");
            }
            else this.embed.setDescription(selected.getName() + " cannot transform into " + form);
        }
        else if(this.msg[1].equals("mega"))
        {
            boolean hasMoney = this.playerData.getCredits() >= CommandBuy.COST_MEGA;

            if(this.msg.length == 2 && selected.hasMega() && hasMoney)
            {
                if(selected.getMegaList().size() == 1)
                {
                    this.embed.setDescription(selected.getName() + " mega evolved into " + selected.getMegaList().get(0) + "!");
                    selected.changeForm(selected.getMegaList().get(0));
                    this.playerData.changeCredits(-1 * CommandBuy.COST_MEGA);
                }
                else this.embed.setDescription("Use p!buy mega x or p!buy mega y to buy the specific evolution!");
            }
            else if(this.msg.length == 3 && selected.hasMega() && hasMoney)
            {
                if(selected.getMegaList().size() == 2)
                {
                    String chosen = Global.normalCase(selected.getMegaList().get(this.msg[2].contains("x") ? 0 : 1));
                    this.embed.setDescription(selected.getName() + " mega evolved into " + chosen + "!");
                    selected.changeForm(selected.getMegaList().get(this.msg[2].contains("x") ? 0 : 1));
                    this.playerData.changeCredits(-1 * CommandBuy.COST_MEGA);
                }
                else this.embed.setDescription("Use p!buy mega to mega evolve!");
            }
        }
        else if(this.msg[1].equals("tm"))
        {
            boolean isInTMList = CommandShop.entriesTM.stream().anyMatch(tm -> tm.toUpperCase().contains(this.msg[2].toUpperCase()));
            System.out.println(CommandShop.entriesTM + ", checking for " + this.msg[2]);
            if(isInTMList && this.playerData.getCredits() >= CommandShop.currentTMPrice)
            {
                this.playerData.addTM(this.msg[2].toUpperCase());
                this.playerData.changeCredits(-1 * CommandShop.currentTMPrice);
                this.embed.setDescription("Successfully bought " + this.msg[2].toUpperCase());
            }
            else if(!isInTMList)
            {
                this.embed.setDescription("Invalid TM!");
            }
            else this.embed.setDescription("You don't have enough money!");
        }
        else if(this.msg[1].equals("tr"))
        {
            boolean isInTRList = CommandShop.entriesTR.stream().anyMatch(tr -> tr.toUpperCase().contains(this.msg[2].toUpperCase()));
            System.out.println(CommandShop.entriesTR + ", checking for " + this.msg[2]);
            if(isInTRList && this.playerData.getCredits() >= CommandShop.currentTRPrice)
            {
                this.playerData.addTR(this.msg[2].toUpperCase());
                this.playerData.changeCredits(-1 * CommandShop.currentTRPrice);
                this.embed.setDescription("Successfully bought " + this.msg[2].toUpperCase());
            }
            else if(!isInTRList)
            {
                this.embed.setDescription("Invalid TR!");
            }
            else this.embed.setDescription("You don't have enough money!");
        }
        else if(this.msg[1].equals("movetutor") && this.msg.length >= 3 && this.playerData.getCredits() >= CommandBuy.COST_MOVETUTOR)
        {
            String move = this.msg.length == 2 ? Global.normalCase(this.msg[1]) : Global.normalCase(this.msg[2] + " " + this.msg[3]);

            List<String> blastBurnPokemon = Arrays.asList("Charizard", "Mega Charizard X", "Mega Charizard Y", "Typhlosion", "Blaziken", "Mega Blaziken", "Infernape", "Emboar", "Delphox", "Incineroar");
            List<String> hydroCannonPokemon = Arrays.asList("Blastoise", "Mega Blastoise", "Feraligatr", "Swampert", "Mega Swampert", "Empoleon", "Samurott", "Greninja", "Primarina");
            List<String> frenzyPlantPokemon = Arrays.asList("Venusaur", "Mega Venusaur", "Meganium", "Sceptile", "Mega Sceptile", "Torterra", "Serperior", "Chesnaught", "Decidueye");

            if(CommandShop.MOVE_TUTOR_MOVES.contains(move))
            {
                boolean blastBurn = move.equals("Blast Burn") && blastBurnPokemon.contains(selected.getName());
                boolean hydroCannon = move.equals("Hydro Cannon") && hydroCannonPokemon.contains(selected.getName());
                boolean frenzyPlant = move.equals("Frenzy Plant") && frenzyPlantPokemon.contains(selected.getName());
                boolean dracoMeteor = move.equals("Draco Meteor") && selected.getType()[0].equals(Type.DRAGON);
                boolean voltTackle = move.equals("Volt Tackle") && selected.getName().equals("Pikachu");
                boolean dragonAscent = move.equals("Dragon Ascent") && (selected.getName().equals("Rayquaza") || selected.getName().equals("Mega Rayquaza"));
                boolean secretSword = move.equals("Secret Sword") && selected.getName().equals("Keldeo");
                boolean relicSong = move.equals("Relic Song") && selected.getName().contains("Meloetta");

                if(blastBurn || hydroCannon || frenzyPlant || dracoMeteor || voltTackle || dragonAscent || secretSword || relicSong)
                {
                    this.playerData.changeCredits(-1 * COST_MOVETUTOR);

                    selected.learnMove(move, 1);
                    Pokemon.updateMoves(selected);
                    System.out.println(selected.getLearnedMoves());

                    this.embed.setDescription("Bought " + move + " for " + selected.getName() + "!");
                }
            }
        }
        else if(this.msg[1].equals("zcrystal") && this.msg.length == 4)
        {
            ZCrystal z = ZCrystal.cast(Global.normalCase(this.msg[2] + " " + this.msg[3]));

            if(z == null || !CommandShop.entriesZCrystal.contains(z.getStyledName()))
            {
                this.embed.setDescription("Invalid Z Crystal!");
                return this;
            }
            else if(this.playerData.getCredits() < CommandShop.priceZCrystal)
            {
                this.embed.setDescription("You don't have enough credits!");
                return this;
            }
            else if(this.playerData.hasZCrystal(z.getStyledName()))
            {
                this.embed.setDescription("You already own this Z-Crystal!");
                return this;
            }

            this.playerData.addZCrystal(z.getStyledName());
            this.playerData.changeCredits(-1 * CommandShop.priceZCrystal);

            this.embed.setDescription("Successfully bought " + z.getStyledName() + "!");
        }
        else
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        Pokemon.uploadPokemon(selected);
        this.embed.setTitle(this.player.getName());
        this.color = selected.getType()[0].getColor();
        return this;
    }
}
