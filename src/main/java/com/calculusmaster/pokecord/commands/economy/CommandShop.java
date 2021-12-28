package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Nature;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.enums.Prices;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandShop extends Command
{
    public static final List<Item> ITEM_ENTRIES = new ArrayList<>();
    public static final List<Integer> ITEM_PRICES = new ArrayList<>();
    public static int ITEM_COUNT_MIN;
    public static int ITEM_COUNT_MAX;

    public static final List<TM> TM_ENTRIES = new ArrayList<>();
    public static int TM_COUNT;
    public static int TM_PRICE = 0;

    public static final List<TR> TR_ENTRIES = new ArrayList<>();
    public static int TR_COUNT;
    public static int TR_PRICE = 0;

    public static final List<ZCrystal> ZCRYSTAL_ENTRIES = new ArrayList<>();
    public static int ZCRYSTAL_COUNT_MIN;
    public static int ZCRYSTAL_COUNT_MAX;

    public CommandShop(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.ACCESS_BUY_SHOP)) return this.invalidMasteryLevel(Feature.ACCESS_BUY_SHOP);

        if(this.msg.length == 1)
        {
            this.embed.setTitle("Pokecord2 Shop");
            for(Page p : Page.values()) this.embed.addField(p.title, "`" + this.serverData.getPrefix() + "shop " + p.commands.get(0) + "`\n" + p.desc, false);
            this.embed.setDescription("Here is a list of all the shop pages.");
        }
        else
        {
            if(Page.isInvalid(this.msg[1])) this.response = "Invalid page! Use `p!shop` to see the possible shop pages.";
            else
            {
                Page p = Page.cast(this.msg[1]);

                Pokemon selected = this.playerData.getSelectedPokemon();

                if(Page.MEGA.matches(this.msg[1]))
                {
                    if(this.insufficientMasteryLevel(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS)) return this.invalidMasteryLevel(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS);

                    this.embed
                            .addField("Price", "All Mega Evolutions cost `" + Prices.SHOP_MEGA.get() + "` credits!", false)
                            .addField("Single Mega Evolution", this.getCommandFormatted("buy mega") + " – Buy the Mega Evolution of a Pokemon that does not have an X or Y Mega Evolution.", false)
                            .addField("Mega X Evolution", this.getCommandFormatted("buy mega x") + " - Buy the X Mega Evolution of a Pokemon that has an X or Y Mega Evolution", false)
                            .addField("Mega Y Evolution", this.getCommandFormatted("buy mega y") + " - Buy the Y Mega Evolution of a Pokemon that has an X or Y Mega Evolution", false);

                    this.embed.setFooter("Your Selected Pokemon (" + selected.getName() + ") " + switch(selected.getMegaList().size()) {
                        case 1 -> "has one Mega Evolution!";
                        case 2 -> "has an X and Y Mega Evolution!";
                        default -> "cannot Mega Evolve!";
                    });
                }
                else if(Page.FORMS.matches(this.msg[1]))
                {
                    if(this.insufficientMasteryLevel(Feature.ACQUIRE_POKEMON_FORMS)) return this.invalidMasteryLevel(Feature.ACQUIRE_POKEMON_FORMS);

                    this.embed
                            .addField("Price", "All Forms cost `" + Prices.SHOP_FORM.get() + "` credits!", false)
                            .addField("Purchase", "To buy a form, type `p!buy form <name>`, where <name> is the name of the form.", false)
                            .addField("Selected Pokemon", selected.getName(), false);

                    StringBuilder availableForms = new StringBuilder();

                    if(selected.getFormsList().isEmpty()) availableForms.append("None");
                    else if(selected.getName().contains("Aegislash")) availableForms.append("Aegislash Forms are not purchasable! Aegislash will automatically switch forms in duels – Shield Form when using Kings Shield (and subsequent status moves), and Blade Form when using any damaging move.");
                    else for(String s : selected.getFormsList()) availableForms.append(s).append("\n");

                    this.embed.addField("Available Forms", availableForms.toString(), false);
                }
                else if(Page.NATURE.matches(this.msg[1]))
                {
                    this.embed
                            .addField("Price", "All Natures cost `" + Prices.SHOP_NATURE.get() + "` credits!", false)
                            .addField("Selected Pokemon", "Nature: `" + selected.getNature().toString() + "`", false);

                    for(Nature n : Nature.values()) this.embed.addField(n.toString() + (n.hasNoEffect() ? "*" : ""), n.getShopEntry(), true);
                    for(int i = 0; i < Nature.values().length % 3; i++) this.embed.addBlankField(true);

                    this.embed.setFooter("Natures with an asterisk (*) have no effect on a Pokemon's Stats.");
                }
                else if(Page.CANDY.matches(this.msg[1]))
                {
                    this.embed.addField("Price", "Rare Candies cost `" + Prices.SHOP_CANDY.get() + "` each.", false);
                    this.embed.addField("Selected Pokemon", "Level `" + selected.getLevel() + "`\nYou can buy a maximum of `" + (100 - selected.getLevel()) + "` Rare Candies!", false);
                }
                else if(Page.ITEMS.matches(this.msg[1]))
                {
                    if(this.insufficientMasteryLevel(Feature.GIVE_POKEMON_ITEMS)) return this.invalidMasteryLevel(Feature.GIVE_POKEMON_ITEMS);

                    for(int i = 0; i < ITEM_ENTRIES.size(); i++) this.embed.addField(ITEM_ENTRIES.get(i).getStyledName(), "Number: `" + (i + 1) + "`\nPrice: " + ITEM_PRICES.get(i) + "c", true);
                    for(int i = 0; i < ITEM_ENTRIES.size() % 3; i++) this.embed.addBlankField(true);
                }
                else if(Page.TM.matches(this.msg[1]))
                {
                    if(this.insufficientMasteryLevel(Feature.ACCESS_TMS)) return this.invalidMasteryLevel(Feature.ACCESS_TMS);

                    this.embed.addField("Price", "Each TM costs `" + CommandShop.TM_PRICE + "` credits!", false);

                    StringBuilder s = new StringBuilder();
                    for(TM tm : TM_ENTRIES) s.append(tm.getShopEntry()).append("\n");
                    this.embed.addField("Available TMs", s.toString(), false);
                }
                else if(Page.TR.matches(this.msg[1]))
                {
                    if(this.insufficientMasteryLevel(Feature.ACCESS_TRS)) return this.invalidMasteryLevel(Feature.ACCESS_TRS);

                    this.embed.addField("Price", "Each TR costs `" + CommandShop.TR_PRICE + "` credits!", false);

                    StringBuilder s = new StringBuilder();
                    for(TR tr : TR_ENTRIES) s.append(tr.getShopEntry()).append("\n");
                    this.embed.addField("Available TRs", s.toString(), false);
                }
                else if(Page.MOVETUTOR.matches(this.msg[1]))
                {
                    if(this.insufficientMasteryLevel(Feature.PURCHASE_MOVE_TUTOR_MOVES)) return this.invalidMasteryLevel(Feature.PURCHASE_MOVE_TUTOR_MOVES);

                    this.embed
                            .addField("Price", "All Move Tutor Moves cost " + Prices.SHOP_MOVETUTOR.get() + " credits!", false)
                            .addField("Info", "Buying a Move Tutor move will automatically add it to the first slot of your Selected Pokemon. If you accidentally replace it, there is no way of retrieving that move without buying it again, so be careful!", false);

                    StringBuilder s = new StringBuilder();
                    for(String move : MoveTutorRegistry.MOVE_TUTOR_MOVES) s.append("`").append(move).append("`\n");

                    this.embed.addField("Moves", s.toString(), false);
                }
                else if(Page.ZCRYSTAL.matches(this.msg[1]))
                {
                    if(this.insufficientMasteryLevel(Feature.PURCHASE_Z_CRYSTALS)) return this.invalidMasteryLevel(Feature.PURCHASE_Z_CRYSTALS);

                    this.embed.addField("Price", "All Z Crystals cost " + Prices.SHOP_ZCRYSTAL.get() + " credits!", false);

                    StringBuilder s = new StringBuilder();
                    for(ZCrystal z : ZCRYSTAL_ENTRIES) s.append("`").append(z.getStyledName()).append("`\n");

                    this.embed.addField("Z Crystals", s.toString(), false);
                }

                this.embed.setTitle("Pokecord2 Shop - " + p.title);
                this.embed.setDescription(p.desc);
            }
        }

        return this;
    }

    public static void updateShops()
    {
        LoggerHelper.info(CommandShop.class, "Updating Shop!");

        ITEM_ENTRIES.clear();
        TM_ENTRIES.clear();
        TR_ENTRIES.clear();
        ZCRYSTAL_ENTRIES.clear();

        int count;

        Random r = new Random();

        //Items
        count = r.nextInt(ITEM_COUNT_MAX - ITEM_COUNT_MIN + 1) + ITEM_COUNT_MIN;

        for(int i = 0; i < count; i++)
        {
            Item item = Item.values()[r.nextInt(Item.values().length)];

            if(!item.equals(Item.NONE) && !ITEM_ENTRIES.contains(item) && !item.isFunctionalItem())
            {
                ITEM_ENTRIES.add(item);
                ITEM_PRICES.add(item.cost + (r.nextInt(item.cost / 2) * (r.nextInt(10) < 5 ? 1 : -1)));
            }
            else i--;
        }

        //TMs
        for(int i = 0; i < TM_COUNT; i++)
        {
            TM tm = TM.values()[r.nextInt(TM.values().length)];

            if(TM_ENTRIES.contains(tm)) i--;
            else TM_ENTRIES.add(tm);
        }

        TM_PRICE = Prices.SHOP_BASE_TM.get() + r.nextInt(Prices.SHOP_RANDOM_TM.get() + 1);

        //TRs
        for(int i = 0; i < TR_COUNT; i++)
        {
            TR tr = TR.values()[r.nextInt(TR.values().length)];

            if(TR_ENTRIES.contains(tr)) i--;
            else TR_ENTRIES.add(tr);
        }

        TR_PRICE = Prices.SHOP_BASE_TR.get() + r.nextInt(Prices.SHOP_RANDOM_TR.get() + 1);

        //Z Crystals
        count = new Random().nextInt(ZCRYSTAL_COUNT_MAX - ZCRYSTAL_COUNT_MIN + 1) + ZCRYSTAL_COUNT_MIN;

        for(int i = 0; i < count; i++)
        {
            ZCrystal z = ZCrystal.getRandomUniqueZCrystal();

            if(ZCRYSTAL_ENTRIES.contains(z)) i--;
            else ZCRYSTAL_ENTRIES.add(z);
        }
    }

    private enum Page
    {
        MEGA("Mega Evolutions", "Buy Mega Evolutions!", "mega"),
        FORMS("Forms", "Buy Forms!", "forms"),
        NATURE("Natures", "Buy Natures!", "nature"),
        CANDY("Candies", "Buy Rare Candies to level up Pokemon quickly!", "candy"),
        ITEMS("Items", "Buy Pokemon-Related Items!", "items"),
        TM("TMs", "Buy Technical Machines (TMs) to teach your Pokemon new moves!", "tm"),
        TR("TRs", "Buy Technical Records (TRs) to teach your Pokemon new moves!", "tr"),
        MOVETUTOR("Move Tutor", "Buy Move Tutor moves to teach your Pokemon!", "movetutor", "mt"),
        ZCRYSTAL("Z Crystals", "Buy Z Crystals to unlock the power of Z-Moves in duels!", "zcrystal", "z");

        private List<String> commands;
        public String title, desc;

        Page(String title, String desc, String... commands)
        {
            this.title = title;
            this.desc = desc;
            this.commands = Arrays.asList(commands);
        }

        public boolean matches(String s)
        {
            return this.commands.contains(s);
        }

        public static boolean isInvalid(String s)
        {
            return Arrays.stream(values()).noneMatch(p -> p.matches(s));
        }

        public static Page cast(String s)
        {
            for(Page p : values()) if(p.matches(s)) return p;
            return null;
        }
    }
}
