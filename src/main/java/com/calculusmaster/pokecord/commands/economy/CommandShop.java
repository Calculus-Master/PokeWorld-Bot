package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Nature;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaEvolutionRegistry;
import com.calculusmaster.pokecord.game.world.PokeWorldShop;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.Prices;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandShop extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("shop")
                .withConstructor(CommandShop::new)
                .withFeature(Feature.ACCESS_BUY_SHOP)
                .withCommand(Commands
                        .slash("shop", "Access the %s Shop and buy items, TMs, Megas, Natures, Rare Candies, and more!".formatted(Pokecord.NAME))
                        .addSubcommands(
                                new SubcommandData("candy", "View information about rare candies."),
                                new SubcommandData("nature", "View information about Pokemon natures."),
                                new SubcommandData("item", "View the shop's current item offerings and prices."),
                                new SubcommandData("mega", "View information about acquiring your active Pokemon's Mega-Evolutions."),
                                new SubcommandData("tm", "View the shop's current TM offerings and price."),
                                new SubcommandData("move-tutor", "View Move Tutor move offerings and price."),
                                new SubcommandData("z-crystal", "View the shop's current unique Z-Crystal offerings and price.")
                        )
                        .addSubcommandGroups(
                                new SubcommandGroupData("buy", "Purchase things from the shop!")
                                        .addSubcommands(
                                                new SubcommandData("candy", "Buy rare candies to instantly level your Pokemon.")
                                                        .addOption(OptionType.INTEGER, "amount", "The amount of rare candies to buy.", false),
                                                new SubcommandData("nature", "Buy a nature to instantly change your active Pokemon's nature.")
                                                        .addOption(OptionType.STRING, "nature", "The nature to buy.", true, true),
                                                new SubcommandData("item", "Buy an item from the shop.")
                                                        .addOption(OptionType.STRING, "item", "The item to buy.", true, true),
                                                new SubcommandData("mega", "Buy a Mega-Evolution for your active Pokemon.")
                                                        .addOption(OptionType.STRING, "mega", "Specify buying an X or Y Mega-Evolution.", false, true),
                                                new SubcommandData("tm", "Buy a TM from the shop.")
                                                        .addOption(OptionType.STRING, "tm", "The TM to buy.", true, true),
                                                new SubcommandData("move-tutor", "Buy a Move Tutor move from the shop.")
                                                        .addOption(OptionType.STRING, "move-tutor", "The Move Tutor move to buy.", true, true)
                                                        .addOption(OptionType.INTEGER, "move-slot", "The move slot of your active Pokemon to teach the move to.", true),
                                                new SubcommandData("z-crystal", "Buy a unique Z-Crystal from the shop.")
                                                        .addOption(OptionType.STRING, "z-crystal", "The Z-Crystal to buy.", true, true)
                                        )
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());
        PokeWorldShop shop = PokeWorldShop.getCurrentShop();

        //Shop View Commands
        if(event.getSubcommandGroup() == null)
        {
            if(subcommand.equals("candy"))
            {
                Pokemon p = this.playerData.getSelectedPokemon();

                this.embed
                        .setTitle(Pokecord.NAME + " Shop: Rare Candies")
                        .setDescription("""
                                Rare Candies are special items that instantly level your Pokemon by 1.
                                You can buy rare candies from the shop using `/shop buy candy`.
                                
                                Cost: **%sc**
                                %s's Current Level: **%s**
                                """.formatted(Prices.SHOP_CANDY.get(), p.getName(), p.getLevel()));
            }
            else if(subcommand.equals("nature"))
            {
                Pokemon p = this.playerData.getSelectedPokemon();

                this.embed
                        .setTitle(Pokecord.NAME + " Shop: Natures")
                        .setDescription("""
                        Natures provide a boost to one stat at the cost of another.
                        You can buy a nature from the shop to instantly change your active Pokemon's nature, using `/shop buy nature`.
                        
                        Cost: **%sc**
                        %s's Nature: **%s**
                        """.formatted(Prices.SHOP_NATURE.get(), p.getName(), Global.normalize(p.getNature().toString())))
                        .setFooter("*Some natures will boost and lower the same stat, in which case the stat will be unaffected.");

                for(Nature n : Nature.values()) this.embed.addField(Global.normalize(n.toString()) + (n.hasNoEffect() ? "*" : ""), n.getShopEntry(), true);
                for(int i = 0; i < Nature.values().length % 3; i++) this.embed.addBlankField(true);
            }
            else if(subcommand.equals("item"))
            {
                if(this.isInvalidMasteryLevel(Feature.GIVE_POKEMON_ITEMS)) return this.respondInvalidMasteryLevel(Feature.GIVE_POKEMON_ITEMS);

                List<String> itemNames = new ArrayList<>(), itemPrices = new ArrayList<>(), itemTypes = new ArrayList<>();
                for(Item i : shop.getItems())
                {
                    itemNames.add(i.getStyledName());
                    itemPrices.add("**" + shop.getItemPrice(i) + "c**");
                    itemTypes.add("*" + Global.normalize(i.getType().toString()) + "*");
                }

                this.embed
                        .setTitle(Pokecord.NAME + " Shop: Items")
                        .setDescription("""
                                Items can be held by Pokemon and have various uses.
                                Some items can be used in battle, some assist in evolution, and much more.
                                To buy an item, use `/shop buy item`.
                                """)
                        .addField("Items", String.join("\n", itemNames), true)
                        .addField("Cost", String.join("\n", itemPrices), true)
                        .addField("Type", String.join("\n", itemTypes), true);
            }
            else if(subcommand.equals("mega"))
            {
                if(this.isInvalidMasteryLevel(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS)) return this.respondInvalidMasteryLevel(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS);

                Pokemon p = this.playerData.getSelectedPokemon();
                String contents = p.getName() + " does not Mega-Evolve.";
                if(MegaEvolutionRegistry.isMega(p.getEntity())) contents = p.getName() + " is already a Mega-Evolved Pokemon.";
                else if(MegaEvolutionRegistry.hasMegaData(p.getEntity()))
                {
                    MegaEvolutionRegistry.MegaEvolutionData megaData = MegaEvolutionRegistry.getData(p.getEntity());

                    if(megaData.isSingle()) contents = p.getName() + " Mega-Evolves into " + megaData.getMega().getName();
                    else contents = p.getName() + " has two Mega-Evolutions: " + megaData.getMegaX().getName() + " and " + megaData.getMegaY().getName();
                }

                this.embed.setTitle(Pokecord.NAME + " Shop: Mega Evolutions")
                        .setDescription("""
                                Mega-Evolutions are powerful transformations available to certain Pokemon that greatly increase their stats and introduce new abilities.
                                Some Pokemon have two possible Mega-Evolutions – an X or Y Mega-Evolution.
                                
                                To buy a Mega-Evolution, use `/shop buy mega`, and specify the X or Y evolution if the Pokemon has both forms.
                                
                                Cost: **%sc**
                                **%s**
                                """.formatted(Prices.SHOP_MEGA.get(), contents));
            }
            else if(subcommand.equals("tm"))
            {
                if(this.isInvalidMasteryLevel(Feature.ACCESS_TMS)) return this.respondInvalidMasteryLevel(Feature.ACCESS_TMS);

                List<String> tmNames = new ArrayList<>(), tmMoves = new ArrayList<>();
                for(TM tm : shop.getTMs())
                {
                    tmNames.add(tm.toString());
                    tmMoves.add("*" + tm.getMove().getName() + "*");
                }

                this.embed
                        .setTitle(Pokecord.NAME + " Shop – Technical Machines (TMs)")
                        .setDescription("""
                                Technical Machines (TMs) are items that let you teach Pokemon moves they could not normally learn by leveling up.
                                You can view the PokeDex entry for a Pokemon to see what TMs they can learn.
                                To buy a TM, use `/shop buy tm`.
                                
                                *Note: The TM Cost changes regularly when the shop does!*
                                Cost: **%sc**
                                """.formatted(shop.getTMPrice()))
                        .addField("TMs", String.join("\n", tmNames), true)
                        .addField("Move", String.join("\n", tmMoves), true)
                        .addBlankField(true);
            }
            else if(subcommand.equals("move-tutor"))
            {
                if(this.isInvalidMasteryLevel(Feature.PURCHASE_MOVE_TUTOR_MOVES)) return this.respondInvalidMasteryLevel(Feature.PURCHASE_MOVE_TUTOR_MOVES);

                Pokemon p = this.playerData.getSelectedPokemon();

                List<String> available = new ArrayList<>();
                for(MoveEntity move : MoveTutorRegistry.MOVE_TUTOR_MOVES)
                    if(MoveTutorRegistry.VALIDATORS.get(move).test(p)) available.add(move.getName());

                this.embed
                        .setTitle(Pokecord.NAME + " Shop – Move Tutors")
                        .setDescription("""
                                Move Tutor moves are special moves that they could not normally learn by leveling up.
                                To buy a Move Tutor to teach your Pokemon a particular move, use `/shop buy move-tutor`.
                                You will also need to specify which move slot to teach the move to, so check your moves with `/moves view` before buying a Move Tutor move!
                                
                                Cost: **%sc**
                                """.formatted(Prices.SHOP_MOVETUTOR.get()))
                        .addField("Available Move Tutors", available.isEmpty() ? p.getName() + " cannot learn any moves from Move Tutors." : String.join("\n", available), false);
            }
            else if(subcommand.equals("z-crystal"))
            {
                if(this.isInvalidMasteryLevel(Feature.PURCHASE_Z_CRYSTALS)) return this.respondInvalidMasteryLevel(Feature.PURCHASE_Z_CRYSTALS);

                List<String> zCrystalNames = new ArrayList<>();
                for(ZCrystal zc : shop.getZCrystals())
                {
                    boolean owned = this.playerData.getInventory().hasZCrystal(zc);
                    zCrystalNames.add((owned ? "~~" : "") + zc.getStyledName() + (owned ? "~~" : ""));
                }

                this.embed.setTitle(Pokecord.NAME + " Shop: Z-Crystals")
                        .setDescription("""
                                Z-Crystals are items that when equipped, let you unleash powerful Z-Moves in Duels.
                                The shop sells "Unique" Z-Crystals – these are crystals dedicated to a particular Pokemon species, and permit the use of a special Z-Move.
                                To buy a Z-Crystal, use `/shop buy z-crystal`.
                                
                                *Note: The Z-Crystal Cost changes regularly when the shop does!*
                                Cost: **%sc**
                                """.formatted(shop.getZCrystalPrice()))
                        .addField("Z-Crystals", String.join("\n", zCrystalNames), true)
                        .addBlankField(true)
                        .addBlankField(true)
                        .setFooter("Crossed-out Z-Crystals are ones that you already own. Use /inventory to see them!");
            }
        }
        //Shop Buy Commands
        else if(event.getSubcommandGroup().equals("buy"))
        {
            if(subcommand.equals("candy"))
            {
                OptionMapping amountOption = event.getOption("amount");
                int amount = amountOption == null ? 1 : amountOption.getAsInt();

                Pokemon p = this.playerData.getSelectedPokemon();
                amount = Math.min(amount, 100 - p.getLevel());

                if(p.getLevel() == 100) return this.error("Your Pokemon is already max level! You cannot buy Rare Candies for it.");
                else if(amount <= 0) return this.error("Amount must be greater than 0.");

                int cost = amount * Prices.SHOP_CANDY.get();

                if(this.playerData.getCredits() < cost) return this.error("You do not have enough credits to purchase " + amount + " Rare Candies (Total Cost: **" + cost +  "c**).");

                this.playerData.changeCredits(-cost);
                p.setLevel(p.getLevel() + amount);
                p.updateExperience();

                this.response = "Successfully purchased **" + amount + "** Rare Candies for **" + cost + "c**! Your " + p.getName() + " is now **Level " + p.getLevel() + "**.";
            }
            else if(subcommand.equals("nature"))
            {
                OptionMapping natureOption = Objects.requireNonNull(event.getOption("nature"));
                String natureInput = natureOption.getAsString();

                Nature nature = Nature.cast(natureInput);
                Pokemon p = this.playerData.getSelectedPokemon();

                if(nature == null) return this.error("\"" + natureInput + "\" is not a valid nature!");
                else if(p.getNature().equals(nature)) return this.error("Your " + p.getName() + " already has a **" + Global.normalize(nature.toString()) + "** nature!");
                else if(this.playerData.getCredits() < Prices.SHOP_NATURE.get()) return this.error("You do not have enough credits to purchase a nature (Cost: **" + Prices.SHOP_NATURE.get() + "c**).");

                this.playerData.changeCredits(-Prices.SHOP_NATURE.get());
                p.setNature(nature);
                p.updateNature();

                this.response = "Your " + p.getName() + " now has a **" + Global.normalize(nature.toString()) + "** nature! (Cost: **" + Prices.SHOP_NATURE.get() + "c**).";
            }
            else if(subcommand.equals("item"))
            {
                if(this.isInvalidMasteryLevel(Feature.GIVE_POKEMON_ITEMS)) return this.respondInvalidMasteryLevel(Feature.GIVE_POKEMON_ITEMS);

                OptionMapping itemOption = Objects.requireNonNull(event.getOption("item"));
                String itemInput = itemOption.getAsString();

                Item item = Item.cast(itemInput);
                if(item == null) return this.error("\"" + itemInput + "\" is not a valid item!");
                else if(!shop.getItems().contains(item)) return this.error("This item is not currently for sale in the shop! Use `/shop items` to see what's for sale.");
                else if(this.playerData.getCredits() < shop.getItemPrice(item)) return this.error("You do not have enough credits to purchase this item (Cost: **" + shop.getItemPrice(item) + "c**).");

                this.playerData.changeCredits(-shop.getItemPrice(item));
                this.playerData.getInventory().addItem(item);
                this.playerData.updateInventory();

                this.response = "Successfully purchased a **" + item.getStyledName() + "** for **" + shop.getItemPrice(item) + "c**!";
            }
            else if(subcommand.equals("mega"))
            {
                if(this.isInvalidMasteryLevel(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS)) return this.respondInvalidMasteryLevel(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS);

                OptionMapping megaOption = event.getOption("mega");

                Pokemon p = this.playerData.getSelectedPokemon();

                if(MegaEvolutionRegistry.isMega(p.getEntity())) return this.error(p.getName() + " is already a Mega-Evolved Pokemon!");
                else if(!MegaEvolutionRegistry.hasMegaData(p.getEntity())) return this.error(p.getName() + " cannot Mega-Evolve.");

                MegaEvolutionRegistry.MegaEvolutionData megaData = MegaEvolutionRegistry.getData(p.getEntity());
                String megaInput = megaOption == null ? "" : megaOption.getAsString().toUpperCase();

                if(megaData.isXY() && (megaOption == null || (!megaInput.equals("X") && !megaInput.equals("Y"))))
                    return this.error("Invalid input. You must specify either 'X' or 'Y'.");

                PokemonEntity targetMega = megaData.isSingle() ? megaData.getMega() : (megaInput.equals("X") ? megaData.getMegaX() : megaData.getMegaY());

                if(this.playerData.getOwnedMegas().contains(targetMega)) return this.error("You already own the " + megaInput + " Mega-Evolution for " + p.getName() + ". Use `/mega` to Mega-Evolve it!");
                else if(this.playerData.getCredits() < Prices.SHOP_MEGA.get()) return this.error("You do not have enough credits to purchase a Mega-Evolution (Cost: **" + Prices.SHOP_MEGA.get() + "c**).");

                this.playerData.changeCredits(-Prices.SHOP_MEGA.get());
                this.playerData.addOwnedMegas(megaData.getMega());

                this.response = "Successfully purchased the " + (megaInput.isEmpty() ? "" : "**" + megaInput + "**") + "Mega-Evolution for " + p.getName() + " for " + "**" + Prices.SHOP_MEGA.get() + "c**! Use `/mega` to Mega-Evolve it!";
            }
            else if(subcommand.equals("tm"))
            {
                if(this.isInvalidMasteryLevel(Feature.ACCESS_TMS)) return this.respondInvalidMasteryLevel(Feature.ACCESS_TMS);

                OptionMapping tmOption = Objects.requireNonNull(event.getOption("tm"));
                String tmInput = tmOption.getAsString();

                TM tm = TM.cast(tmInput);

                if(tm == null) return this.error("\"" + tmInput + "\" is not a valid TM name!");
                else if(!shop.getTMs().contains(tm)) return this.error("This TM is not currently for sale in the shop! Use `/shop tms` to see what's for sale.");
                else if(this.playerData.getCredits() < shop.getTMPrice()) return this.error("You do not have enough credits to purchase this TM (Cost: **" + shop.getTMPrice() + "c**).");

                this.playerData.changeCredits(-shop.getTMPrice());
                this.playerData.getInventory().addTM(tm);
                this.playerData.updateInventory();

                this.response = "Successfully purchased **" + tm + "** for **" + shop.getTMPrice() + "c**!";
            }
            else if(subcommand.equals("move-tutor"))
            {
                OptionMapping moveTutorOption = Objects.requireNonNull(event.getOption("move-tutor"));
                OptionMapping moveSlotOption = Objects.requireNonNull(event.getOption("move-slot"));

                String moveTutorInput = moveTutorOption.getAsString();
                int moveSlotInput = moveSlotOption.getAsInt();

                Pokemon p = this.playerData.getSelectedPokemon();
                MoveEntity moveTutor = MoveEntity.cast(moveTutorInput);
                if(moveTutor == null) return this.error("\"" + moveTutorInput + "\" is not a valid move name!");
                else if(!MoveTutorRegistry.MOVE_TUTOR_MOVES.contains(moveTutor)) return this.error("This move is not available through a Move Tutor. Use `/shop move-tutors` to see what's available for your active Pokemon.");
                else if(!MoveTutorRegistry.VALIDATORS.get(moveTutor).test(p)) return this.error(p.getName() + " cannot learn " + moveTutor.getName() + " from a Move Tutor.");
                else if(this.playerData.getCredits() < Prices.SHOP_MOVETUTOR.get()) return this.error("You do not have enough credits to purchase this Move Tutor (Cost: **" + Prices.SHOP_MOVETUTOR.get() + "c**).");
                else if(moveSlotInput < 1 || moveSlotInput > 4) return this.error("Invalid move slot. You must specify a number between 1 and 4.");

                this.playerData.changeCredits(-Prices.SHOP_MOVETUTOR.get());
                p.learnMove(moveTutor, moveSlotInput - 1);
                p.updateMoves();

                this.response = "Successfully taught " + p.getName() + " the move **" + moveTutor.getName() + "** from a Move Tutor, for **" + Prices.SHOP_MOVETUTOR.get() + "c**!";
            }
            else if(subcommand.equals("z-crystal"))
            {
                if(this.isInvalidMasteryLevel(Feature.PURCHASE_Z_CRYSTALS)) return this.respondInvalidMasteryLevel(Feature.PURCHASE_Z_CRYSTALS);

                OptionMapping zCrystalOption = Objects.requireNonNull(event.getOption("z-crystal"));
                String zCrystalInput = zCrystalOption.getAsString();

                ZCrystal zCrystal = ZCrystal.cast(zCrystalInput);

                if(zCrystal == null) return this.error("\"" + zCrystalInput + "\" is not a valid Z-Crystal name!");
                else if(!shop.getZCrystals().contains(zCrystal)) return this.error("This Z-Crystal is not currently for sale in the shop! Use `/shop z-crystal` to see what's for sale.");
                else if(this.playerData.getInventory().getZCrystals().contains(zCrystal)) return this.error("You already own this Z-Crystal!");
                else if(this.playerData.getCredits() < shop.getZCrystalPrice()) return this.error("You do not have enough credits to purchase this Z-Crystal (Cost: **" + shop.getZCrystalPrice() + "c**).");

                this.playerData.changeCredits(-shop.getZCrystalPrice());
                this.playerData.getInventory().addZCrystal(zCrystal);
                this.playerData.updateInventory();

                this.response = "Successfully purchased **" + zCrystal.getStyledName() + "** for **" + shop.getZCrystalPrice() + "c**!";
            }
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        String optionName = event.getFocusedOption().getName();
        String currentInput = event.getFocusedOption().getValue();

        PokeWorldShop shop = PokeWorldShop.getCurrentShop();

        switch (optionName)
        {
            case "nature" -> event.replyChoiceStrings(this.getAutocompleteOptions(currentInput, Arrays.stream(Nature.values()).map(n -> Global.normalize(n.toString())).toList())).queue();
            case "move-tutor" -> event.replyChoiceStrings(this.getAutocompleteOptions(currentInput, MoveTutorRegistry.MOVE_TUTOR_MOVES.stream().map(MoveEntity::getName).toList())).queue();
            case "item" -> event.replyChoiceStrings(this.getAutocompleteOptions(currentInput, shop.getItems().stream().map(Item::getStyledName).toList())).queue();
            case "mega" -> event.replyChoiceStrings("X", "Y").queue();
            case "tm" -> event.replyChoiceStrings(this.getAutocompleteOptions(currentInput, shop.getTMs().stream().map(TM::toString).toList())).queue();
            case "z-crystal" -> event.replyChoiceStrings(this.getAutocompleteOptions(currentInput, shop.getZCrystals().stream().map(ZCrystal::getStyledName).toList())).queue();
        }

        return true;
    }
}
