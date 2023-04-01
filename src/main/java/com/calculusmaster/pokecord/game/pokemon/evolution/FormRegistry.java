package com.calculusmaster.pokecord.game.pokemon.evolution;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class FormRegistry
{
    private static final Map<PokemonEntity, FormData> FORM_DATA = new HashMap<>();

    //TODO: Check all non-switchable forms and implement their in game acquisition methods
    public static void init()
    {
        FormRegistry.register(true, PokemonEntity.RATTATA, PokemonEntity.RATTATA_ALOLA);
        FormRegistry.register(true, PokemonEntity.RATICATE, PokemonEntity.RATICATE_ALOLA);
        FormRegistry.register(true, PokemonEntity.RAICHU, PokemonEntity.RAICHU_ALOLA);
        FormRegistry.register(true, PokemonEntity.SANDSHREW, PokemonEntity.SANDSHREW_ALOLA);
        FormRegistry.register(true, PokemonEntity.SANDSLASH, PokemonEntity.SANDSLASH_ALOLA);
        FormRegistry.register(true, PokemonEntity.VULPIX, PokemonEntity.VULPIX_ALOLA);
        FormRegistry.register(true, PokemonEntity.NINETALES, PokemonEntity.NINETALES_ALOLA);
        FormRegistry.register(true, PokemonEntity.DIGLETT, PokemonEntity.DIGLETT_ALOLA);
        FormRegistry.register(true, PokemonEntity.DUGTRIO, PokemonEntity.DUGTRIO_ALOLA);
        FormRegistry.register(true, PokemonEntity.MEOWTH, PokemonEntity.MEOWTH_ALOLA, PokemonEntity.MEOWTH_GALAR);
        FormRegistry.register(true, PokemonEntity.PERSIAN, PokemonEntity.PERSIAN_ALOLA);
        FormRegistry.register(true, PokemonEntity.GROWLITHE, PokemonEntity.GROWLITHE_HISUI);
        FormRegistry.register(true, PokemonEntity.ARCANINE, PokemonEntity.ARCANINE_HISUI);
        FormRegistry.register(true, PokemonEntity.GEODUDE, PokemonEntity.GEODUDE_ALOLA);
        FormRegistry.register(true, PokemonEntity.GRAVELER, PokemonEntity.GRAVELER_ALOLA);
        FormRegistry.register(true, PokemonEntity.GOLEM, PokemonEntity.GOLEM_ALOLA);
        FormRegistry.register(true, PokemonEntity.PONYTA, PokemonEntity.PONYTA_GALAR);
        FormRegistry.register(true, PokemonEntity.RAPIDASH, PokemonEntity.RAPIDASH_GALAR);
        FormRegistry.register(true, PokemonEntity.SLOWPOKE, PokemonEntity.SLOWPOKE_GALAR);
        FormRegistry.register(true, PokemonEntity.SLOWBRO, PokemonEntity.SLOWBRO_GALAR);
        FormRegistry.register(true, PokemonEntity.FARFETCHD, PokemonEntity.FARFETCHD_GALAR);
        FormRegistry.register(true, PokemonEntity.GRIMER, PokemonEntity.GRIMER_ALOLA);
        FormRegistry.register(true, PokemonEntity.MUK, PokemonEntity.MUK_ALOLA);
        FormRegistry.register(true, PokemonEntity.VOLTORB, PokemonEntity.VOLTORB_HISUI);
        FormRegistry.register(true, PokemonEntity.ELECTRODE, PokemonEntity.ELECTRODE_HISUI);
        FormRegistry.register(true, PokemonEntity.EXEGGUTOR, PokemonEntity.EXEGGUTOR_ALOLA);
        FormRegistry.register(true, PokemonEntity.MAROWAK, PokemonEntity.MAROWAK_ALOLA);
        FormRegistry.register(true, PokemonEntity.WEEZING, PokemonEntity.WEEZING_GALAR);
        FormRegistry.register(true, PokemonEntity.MR_MIME, PokemonEntity.MR_MIME_GALAR);
        FormRegistry.register(true, PokemonEntity.TAUROS, PokemonEntity.TAUROS_PALDEA_COMBAT, PokemonEntity.TAUROS_PALDEA_AQUA, PokemonEntity.TAUROS_PALDEA_BLAZE);

        //TODO: Decide if the Birds get to have switchable forms
        FormRegistry.register(true, PokemonEntity.ARTICUNO, PokemonEntity.ARTICUNO_GALAR);
        FormRegistry.register(true, PokemonEntity.ZAPDOS, PokemonEntity.ZAPDOS_GALAR);
        FormRegistry.register(true, PokemonEntity.MOLTRES, PokemonEntity.MOLTRES_GALAR);

        FormRegistry.register(true, PokemonEntity.TYPHLOSION, PokemonEntity.TYPHLOSION_HISUI);
        FormRegistry.register(true, PokemonEntity.WOOPER, PokemonEntity.WOOPER_PALDEA);
        FormRegistry.register(true, PokemonEntity.SLOWKING, PokemonEntity.SLOWKING_GALAR);
        FormRegistry.register(true, PokemonEntity.QWILFISH, PokemonEntity.QWILFISH_HISUI);
        FormRegistry.register(true, PokemonEntity.SNEASEL, PokemonEntity.SNEASEL_HISUI);
        FormRegistry.register(true, PokemonEntity.CORSOLA, PokemonEntity.CORSOLA_GALAR);
        FormRegistry.register(true, PokemonEntity.ZIGZAGOON, PokemonEntity.ZIGZAGOON_GALAR);
        FormRegistry.register(true, PokemonEntity.LINOONE, PokemonEntity.LINOONE_GALAR);
        FormRegistry.register(false, PokemonEntity.CASTFORM, PokemonEntity.CASTFORM_RAINY, PokemonEntity.CASTFORM_SUNNY, PokemonEntity.CASTFORM_SNOWY);
        FormRegistry.register(true, PokemonEntity.DEOXYS_NORMAL, PokemonEntity.DEOXYS_DEFENSE, PokemonEntity.DEOXYS_ATTACK, PokemonEntity.DEOXYS_SPEED);
        FormRegistry.register(false, PokemonEntity.BURMY_PLANT, PokemonEntity.BURMY_SANDY, PokemonEntity.BURMY_TRASH);
        FormRegistry.register(false, PokemonEntity.WORMADAM_PLANT, PokemonEntity.WORMADAM_SANDY, PokemonEntity.WORMADAM_TRASH);
        FormRegistry.register(true, PokemonEntity.ROTOM, PokemonEntity.ROTOM_FAN, PokemonEntity.ROTOM_HEAT, PokemonEntity.ROTOM_MOW, PokemonEntity.ROTOM_FROST, PokemonEntity.ROTOM_WASH);

        //TODO: Decide if Origin Forme should be switchable
        FormRegistry.register(false, PokemonEntity.DIALGA, PokemonEntity.DIALGA_ORIGIN);
        FormRegistry.register(false, PokemonEntity.PALKIA, PokemonEntity.PALKIA_ORIGIN);
        FormRegistry.register(false, PokemonEntity.GIRATINA_ALTERED, PokemonEntity.GIRATINA_ORIGIN);

        FormRegistry.register(false, PokemonEntity.SHAYMIN_LAND, PokemonEntity.SHAYMIN_SKY);
        FormRegistry.register(true, PokemonEntity.SAMUROTT, PokemonEntity.SAMUROTT_HISUI);
        FormRegistry.register(true, PokemonEntity.LILLIGANT, PokemonEntity.LILLIGANT_HISUI);
        FormRegistry.register(true, PokemonEntity.BASCULIN_BLUE, PokemonEntity.BASCULIN_RED, PokemonEntity.BASCULIN_WHITE);
        FormRegistry.register(true, PokemonEntity.DARUMAKA, PokemonEntity.DARUMAKA_GALAR);

        //TODO: Darmanitan <-> Darmanitan Galar is valid, but Darmanitan Zen & Darmanitan Galar Zen are not
        FormRegistry.register(true, PokemonEntity.DARMANITAN, PokemonEntity.DARMANITAN_GALAR, PokemonEntity.DARMANITAN_ZEN, PokemonEntity.DARMANITAN_GALAR_ZEN);

        FormRegistry.register(true, PokemonEntity.YAMASK, PokemonEntity.YAMASK_GALAR);
        FormRegistry.register(true, PokemonEntity.ZORUA, PokemonEntity.ZORUA_HISUI);
        FormRegistry.register(true, PokemonEntity.ZOROARK, PokemonEntity.ZOROARK_HISUI);
        FormRegistry.register(true, PokemonEntity.STUNFISK, PokemonEntity.STUNFISK_GALAR);
        FormRegistry.register(true, PokemonEntity.BRAVIARY, PokemonEntity.BRAVIARY_HISUI);
        FormRegistry.register(false, PokemonEntity.TORNADUS, PokemonEntity.TORNADUS_THERIAN);
        FormRegistry.register(false, PokemonEntity.THUNDURUS, PokemonEntity.THUNDURUS_THERIAN);
        FormRegistry.register(false, PokemonEntity.LANDORUS, PokemonEntity.LANDORUS_THERIAN); //TOdo Therians
        FormRegistry.register(true, PokemonEntity.KYUREM, PokemonEntity.KYUREM_WHITE, PokemonEntity.KYUREM_BLACK); //Kyurem Form Acquisition
        FormRegistry.register(false, PokemonEntity.KELDEO, PokemonEntity.KELDEO_RESOLUTE); //TODO Keldeo switch
        FormRegistry.register(false, PokemonEntity.MELOETTA_ARIA, PokemonEntity.MELOETTA_PIROUETTE);
        FormRegistry.register(true, PokemonEntity.GRENINJA, PokemonEntity.GRENINJA_ASH); //TODO: Ash Greninja acquisition
        FormRegistry.register(false, PokemonEntity.FLOETTE, PokemonEntity.FLOETTE_ETERNAL); //TODO: Eternal Floette
        FormRegistry.register(false, PokemonEntity.MEOWSTIC_FEMALE, PokemonEntity.MEOWSTIC_MALE);
        FormRegistry.register(false, PokemonEntity.AEGISLASH_BLADE, PokemonEntity.AEGISLASH_SHIELD); //TODO: Check the states again
        FormRegistry.register(true, PokemonEntity.SLIGGOO, PokemonEntity.SLIGGOO_HISUI);
        FormRegistry.register(true, PokemonEntity.GOODRA, PokemonEntity.GOODRA_HISUI);
        FormRegistry.register(false, PokemonEntity.PUMPKABOO_AVERAGE, PokemonEntity.PUMPKABOO_SMALL, PokemonEntity.PUMPKABOO_LARGE, PokemonEntity.PUMPKABOO_SUPER); //TODO Pumpkaboo Gourgeist (maybe just make the sizes a custom data attribute)
        FormRegistry.register(false, PokemonEntity.GOURGEIST_AVERAGE, PokemonEntity.GOURGEIST_SMALL, PokemonEntity.GOURGEIST_LARGE, PokemonEntity.GOURGEIST_SUPER);
        FormRegistry.register(true, PokemonEntity.AVALUGG, PokemonEntity.AVALUGG_HISUI);
        FormRegistry.register(true, PokemonEntity.ZYGARDE_10, PokemonEntity.ZYGARDE_50, PokemonEntity.ZYGARDE_COMPLETE); //TODO: Zygarde
        FormRegistry.register(true, PokemonEntity.HOOPA, PokemonEntity.HOOPA_UNBOUND); //TODO: Hoopa
        FormRegistry.register(true, PokemonEntity.DECIDUEYE, PokemonEntity.DECIDUEYE_HISUI);
        FormRegistry.register(true, PokemonEntity.ORICORIO_BAILE, PokemonEntity.ORICORIO_PAU, PokemonEntity.ORICORIO_SENSU, PokemonEntity.ORICORIO_POM_POM);
        FormRegistry.register(true, PokemonEntity.LYCANROC_MIDDAY, PokemonEntity.LYCANROC_DUSK, PokemonEntity.LYCANROC_MIDNIGHT);
        FormRegistry.register(true, PokemonEntity.WISHIWASHI_SOLO, PokemonEntity.WISHIWASHI_SCHOOL); //TODO: Wishiwashi form acquisition
        FormRegistry.register(false, PokemonEntity.MINIOR_METEOR, PokemonEntity.MINIOR_CORE);
        FormRegistry.register(true, PokemonEntity.NECROZMA, PokemonEntity.NECROZMA_DUSK_MANE, PokemonEntity.NECROZMA_DAWN_WINGS, PokemonEntity.NECROZMA_ULTRA); //TODO: Necrozma form acquisition
        FormRegistry.register(false, PokemonEntity.TOXTRICITY_AMPED, PokemonEntity.TOXTRICITY_LOW_KEY);
        FormRegistry.register(false, PokemonEntity.EISCUE_ICE, PokemonEntity.EISCUE_NOICE);
        FormRegistry.register(false, PokemonEntity.INDEEDEE_MALE, PokemonEntity.INDEEDEE_FEMALE);
        FormRegistry.register(false, PokemonEntity.MORPEKO_FULL, PokemonEntity.MORPEKO_HANGRY);
        FormRegistry.register(false, PokemonEntity.ZACIAN, PokemonEntity.ZACIAN_CROWNED);
        FormRegistry.register(false, PokemonEntity.ZAMAZENTA, PokemonEntity.ZAMAZENTA_CROWNED);
        FormRegistry.register(false, PokemonEntity.ETERNATUS, PokemonEntity.ETERNATUS_ETERNAMAX);
        FormRegistry.register(true, PokemonEntity.URSHIFU_SINGLE_STRIKE, PokemonEntity.URSHIFU_RAPID_STRIKE); //TODO: Urshifu
        FormRegistry.register(true, PokemonEntity.CALYREX, PokemonEntity.CALYREX_ICE_RIDER, PokemonEntity.CALYREX_SHADOW_RIDER); //TODO: Calyrex
        FormRegistry.register(false, PokemonEntity.BASCULEGION_FEMALE, PokemonEntity.BASCULEGION_MALE);
        FormRegistry.register(false, PokemonEntity.ENAMORUS, PokemonEntity.ENAMORUS_THERIAN);
        FormRegistry.register(false, PokemonEntity.OINKOLOGNE_FEMALE, PokemonEntity.OINKOLOGNE_MALE);
        FormRegistry.register(true, PokemonEntity.SQUAWKABILLY_GREEN, PokemonEntity.SQUAWKABILLY_WHITE, PokemonEntity.SQUAWKABILLY_BLUE, PokemonEntity.SQUAWKABILLY_YELLOW);
        FormRegistry.register(false, PokemonEntity.PALAFIN_ZERO, PokemonEntity.PALAFIN_HERO);
        FormRegistry.register(true, PokemonEntity.TATSUGIRI_STRETCHY, PokemonEntity.TATSUGIRI_DROOPY, PokemonEntity.TATSUGIRI_CURLY);
        FormRegistry.register(true, PokemonEntity.GIMMIGHOUL, PokemonEntity.GIMMIGHOUL_ROAMING);
    }

    public static boolean hasFormData(PokemonEntity e)
    {
        return FORM_DATA.containsKey(e);
    }

    public static FormData getFormData(PokemonEntity e)
    {
        return FORM_DATA.get(e);
    }

    private static void register(boolean switchable, PokemonEntity defaultForm, PokemonEntity... forms)
    {
        FormData data = new FormData(defaultForm, switchable, forms);

        FORM_DATA.put(defaultForm, data);
        for(PokemonEntity e : forms) FORM_DATA.put(e, data);
    }

    public static class FormData
    {
        private final EnumSet<PokemonEntity> forms;
        private final PokemonEntity defaultForm;
        private final boolean switchable;

        FormData(PokemonEntity defaultForm, boolean switchable, PokemonEntity... forms)
        {
            this.defaultForm = defaultForm;
            this.switchable = switchable;
            this.forms = EnumSet.of(defaultForm, forms);
        }

        public boolean isSwitchable()
        {
            return this.switchable;
        }

        public PokemonEntity getDefaultForm()
        {
            return this.defaultForm;
        }

        public EnumSet<PokemonEntity> getForms()
        {
            return this.forms;
        }
    }
}
