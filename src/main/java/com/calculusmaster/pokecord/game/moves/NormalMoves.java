package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.elements.Weather;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.util.Global;

import java.util.*;
import java.util.stream.Collectors;

public class NormalMoves
{
    public String Tackle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Growl(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.ATK, -1, 100, false)
                .execute();
    }

    public String Growth(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int stage = duel.weather.equals(Weather.HARSH_SUNLIGHT) ? 2 : 1;

        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, stage, 100, true)
                                .add(Stat.SPATK, stage))
                .execute();
    }

    public String HiddenPower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int a = user.getIVs().get(Stat.HP) % 2;
        int b = user.getIVs().get(Stat.ATK) % 2;
        int c = user.getIVs().get(Stat.DEF) % 2;
        int d = user.getIVs().get(Stat.SPD) % 2;
        int e = user.getIVs().get(Stat.SPATK) % 2;
        int f = user.getIVs().get(Stat.SPDEF) % 2;

        int typeVal = (int)((15 * (a + 2 * b + 4 * c + 8 * d + 16 * e + 32 * f)) / 63.0);

        Type t = switch(typeVal) {
                    case 0 -> Type.FIGHTING;
                    case 1 -> Type.FLYING;
                    case 2 -> Type.POISON;
                    case 3 -> Type.GROUND;
                    case 4 -> Type.ROCK;
                    case 5 -> Type.BUG;
                    case 6 -> Type.GHOST;
                    case 7 -> Type.STEEL;
                    case 8 -> Type.FIRE;
                    case 9 -> Type.WATER;
                    case 10 -> Type.GRASS;
                    case 11 -> Type.ELECTRIC;
                    case 12 -> Type.PSYCHIC;
                    case 13 -> Type.ICE;
                    case 14 -> Type.DRAGON;
                    case 15 -> Type.DARK;
                    default -> throw new IllegalStateException("Unexpected value: " + typeVal);
                };

        move.setType(t);

        return Move.simpleDamageMove(user, opponent, duel, move) + " Hidden Power's type was " + Global.normalCase(t.toString()) + "! ";
    }

    //TODO: Come up with a custom idea for it
    public String Roar(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String WorkUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 100, true)
                                .add(Stat.SPATK, 1))
                .execute();
    }

    public String TakeDown(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 4D)
                .execute();
    }

    //TODO: Evasion or custom idea
    public String SweetScent(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String DoubleEdge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 3D)
                .execute();
    }

    public String Scratch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Smokescreen(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Accuracy Stat Multiplier
        return move.getNotImplementedResult();
    }

    public String ScaryFace(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPD, -2, 100, false)
                .execute();
    }

    public String Slash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String TailWhip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, -1, 100, false)
                .execute();
    }

    public String RapidSpin(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPD, 1, 100, true)
                .execute();
    }

    public String Protect(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).protectUsed = true;

        return user.getName() + " is now protected!";
    }

    public String SkullBash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.DEF, 1, 100, true)
                .execute();
    }

    public String ShellSmash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.DEF, -1, 100, true)
                                .add(Stat.SPDEF, -1))
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 2, 100, true)
                                .add(Stat.SPATK, 2)
                                .add(Stat.SPD, 2))
                .execute();
    }

    public String DefenseCurl(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).defenseCurlUsed = true;

        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, 1, 100, true)
                .execute();
    }

    public String Supersonic(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.CONFUSED)
                .execute();
    }

    public String Safeguard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Whirlwind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Captivate(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPATK, -2, 50, false)
                .execute();
    }

    public String Harden(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, 1, 100, true)
                .execute();
    }

    public String FuryAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String Rage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).rageUsed = true;
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Temporary: multiplies move crit chance by 3
    public String FocusEnergy(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).focusEnergyUsed = true;

        return user.getName() + "'s critical ratio was increased!";
    }

    public String Endeavor(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = opponent.getHealth() - user.getHealth();

        if(damage > 0)
        {
            return MoveEffectBuilder.make(user, opponent, duel, move)
                    .addFixedDamageEffect(damage)
                    .execute();
        }
        else return move.getNothingResult();
    }

    public String TriAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean status = new Random().nextInt(100) < 20;
        String statusStr = "";

        if(status)
        {
            int s = new Random().nextInt(9);

            switch (s)
            {
                case 0, 1, 2 -> {
                    opponent.addStatusCondition(StatusCondition.BURNED);
                    statusStr = opponent.getName() + " is burned!";
                }
                case 3, 4, 5 -> {
                    opponent.addStatusCondition(StatusCondition.PARALYZED);
                    statusStr = opponent.getName() + " is paralyzed!";
                }
                case 6, 7, 8 -> {
                    opponent.addStatusCondition(StatusCondition.FROZEN);
                    statusStr = opponent.getName() + " is frozen!";
                }
            }
        }

        return Move.simpleDamageMove(user, opponent, duel, move) + statusStr;
    }

    public String SonicBoom(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.isType(Type.GHOST)) return move.getNoEffectResult(opponent);
        else
        {
            return MoveEffectBuilder.make(user, opponent, duel, move)
                    .addFixedDamageEffect(20)
                    .execute();
        }
    }

    public String Screech(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, -2, 100, false)
                .execute();
    }

    public String LockOn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).lockOnUsed = true;
        return user.getName() + " is guaranteed to hit the next attack!";
    }

    public String SwordsDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.ATK, 2, 100, true)
                .execute();
    }

    public String HyperBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Frustration(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Return(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Recover(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1 / 2D)
                .execute();
    }

    public String MindReader(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Leer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, -1, 100, false)
                .execute();
    }

    public String AfterYou(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Endure(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setEndure(true);
        return user.getName() + " braces for the next attack!";
    }

    public String Disable(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String LaserFocus(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.getName() + " now has guaranteed critical hits!";
    }

    public String Swift(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PsychUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setDefaultStatMultipliers();

        for(Stat s : Stat.values()) user.changeStatMultiplier(s, opponent.getStageChange(s));

        return user.getName() + " copied " + opponent.getName() + "'s Stat Changes!";
    }

    public String MeFirst(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String QuickAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String RelicSong(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.ASLEEP, 10);
    }

    public String Yawn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String SlackOff(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1 / 2D)
                .execute();
    }

    public String Headbutt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String MorningSun(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(switch(duel.weather) {
                    case CLEAR -> 1 / 2D;
                    case HARSH_SUNLIGHT -> 2 / 3D;
                    default -> 1 / 4D;
                })
                .execute();
    }

    public String WringOut(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower((int)(120 * (user.getHealth() / (double)user.getStat(Stat.HP))));

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MeanLook(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String ViseGrip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Thrash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.CONFUSED, 100, true)
                .execute();
    }

    public String Bind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BOUND)
                .execute();
    }

    public String Slam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String HyperVoice(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ExtremeSpeed(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String BodySlam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
    }

    public String CrushGrip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(120 * opponent.getHealth() / (double)opponent.getStat(Stat.HP));
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String DizzyPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.CONFUSED, 20);
    }

    public String Foresight(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String GigaImpact(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String NobleRoar(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, -1, 100, false)
                                .add(Stat.SPATK, -1))
                .execute();
    }

    public String MegaPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PerishSong(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).perishSongTurns = 3;
        duel.data(opponent.getUUID()).perishSongTurns = 3;

        return user.getName() + " sang a Perish Song!";
    }

    public String Sing(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.ASLEEP)
                .execute();
    }

    public String Round(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String NaturalGift(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Refresh(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.removeStatusCondition(StatusCondition.PARALYZED);
        user.removeStatusCondition(StatusCondition.POISONED);
        user.removeStatusCondition(StatusCondition.BURNED);

        return user.getName() + " was refreshed!";
    }

    //TODO: Silvally Memory Disc
    public String MultiAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Bide(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).bideTurns = 2;

        return user.getName() + " is storing energy!";
    }

    public String BellyDrum(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = user.getStat(Stat.HP) / 2;
        user.damage(damage);

        user.changeStatMultiplier(Stat.ATK, 12);

        return user.getName() + " sacrificed " + damage + " HP! " + user.getName() + "'s Attack rose to its maximum!";
    }

    public String DoubleHit(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move, 2);
    }

    public String LastResort(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    //TODO: Affects more things than just entry hazards: https://bulbapedia.bulbagarden.net/wiki/Court_Change_(move)
    public String CourtChange(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int current = duel.playerIndexFromUUID(user.getUUID());
        int other = current == 0 ? 1 : 0;

        DuelHelper.EntryHazardHandler temp = duel.entryHazards[current];
        duel.entryHazards[current] = duel.entryHazards[other];
        duel.entryHazards[other] = temp;

        return "Entry Hazards were swapped!";
    }

    //TODO: Companion Moves
    public String Stockpile(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.DEF, 1, 100, true)
                                .add(Stat.SPDEF, 1))
                .execute();
    }

    public String Feint(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Flail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double N = 48.0 * user.getHealth() / user.getStat(Stat.HP);

        if(N < 2) move.setPower(200);
        else if(N < 4) move.setPower(150);
        else if(N < 9) move.setPower(100);
        else if(N < 16) move.setPower(80);
        else if(N < 32) move.setPower(40);
        else if(N <= 48) move.setPower(20);
        else move.setPower(5); //Fallback if somehow user health is greater than max

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String OdorSleuth(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String HelpingHand(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Confide(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPATK, -1, 100, false)
                .execute();
    }

    public String Tickle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, -1, 100, false)
                                .add(Stat.DEF, -1))
                .execute();
    }

    public String HornDrill(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addOHKOEffect()
                .execute();
    }

    public String StuffCheeks(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    //Ignore changes to Defense and Evasion
    public String ChipAway(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MegaKick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SuperFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedDamageEffect(opponent.getHealth() / 2)
                .execute();
    }

    public String Assist(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        List<String> banned = Arrays.asList("Assist", "Baneful Bunker", "Beak Blast", "Belch", "Bestow", "Bounce", "Chatter", "Circle Throw", "Copycat", "Counter", "Covet", "Destiny Bond", "Detect", "Dig", "Dive", "Dragon Tail", "Endure", "Feint", "Fly", "Focus Punch", "Follow Me", "Helping Hand", "Hold Hands", "Kings Shield", "Mat Block", "Me First", "Metronome", "Mimic", "Mirror Coat", "Mirror Move", "Nature Power", "Phantom Force", "Protect", "Rage Powder", "Roar", "Shadow Force", "Shell Trap", "Sketch", "Sky Drop", "Sleep Talk", "Snatch", "Spiky Shield", "Spotlight", "Struggle", "Switcheroo", "Thief", "Transform", "Trick", "Whirlwind");
        List<String> pool = new ArrayList<>();
        List<Pokemon> team = List.copyOf(duel.getPlayers()[duel.playerIndexFromUUID(user.getUUID())].team);

        for(Pokemon p : team) pool.addAll(p.getLearnedMoves());

        pool = pool.stream().distinct().filter(s -> !banned.contains(s)).collect(Collectors.toList());

        move = new Move(pool.get(new Random().nextInt(pool.size())));

        return move.logic(user, opponent, duel);
    }

    public String PlayNice(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.ATK, -1, 100, false)
                .execute();
    }

    public String Explosion(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.damage(user.getHealth());
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Conversion(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        Type t = new Move(user.getLearnedMoves().get(0)).getType();
        user.setType(t, 0);
        user.setType(t, 1);

        return user.getName() + " transformed into a " + Global.normalCase(t.toString()) + " Type!";
    }

    public String CometPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String TailSlap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String SpikeCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String DoubleSlap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String FurySwipes(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String Barrage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String MilkDrink(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1 / 2D)
                .execute();
    }

    public String Metronome(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        List<String> banned = Arrays.asList("After You", "Assist", "Baneful Bunker", "Beak Blast", "Belch", "Bestow", "Celebrate", "Chatter", "Copycat", "Counter", "Covet", "Crafty Shield", "Destiny Bond", "Detect", "Diamond Storm", "Dragon Ascent", "Endure", "Feint", "Fleur Cannon", "Focus Punch", "Follow Me", "Freeze Shock", "Helping Hand", "Hold Hands", "Hyperspace Fury", "Hyperspace Hole", "Ice Burn", "Instruct", "Kings Shield", "Light Of Ruin", "Mat Block", "Me First", "Metronome", "Mimic", "Mind Blown", "Mirror Coat", "Mirror Move", "Nature Power", "Origin Pulse", "Photon Geyser", "Plasma Fists", "Precipice Blades", "Protect", "Quash", "Quick Guard", "Rage Powder", "Relic Song", "Secret Sword", "Shell Trap", "Sketch", "Sleep Talk", "Snarl", "Snatch", "Snore", "Spectral Thief", "Spiky Shield", "Spotlight", "Steam Eruption", "Struggle", "Switcheroo", "Techno Blast", "Thief", "Thousand Arrows", "Thousand Waves", "Transform", "Trick", "V Create", "Wide Guard");
        List<String> pool = new ArrayList<>(Move.MOVES.keySet()).stream().filter(m -> !banned.contains(m)).collect(Collectors.toList());

        move = new Move(pool.get(new Random().nextInt(pool.size())));

        return move.logic(user, opponent, duel);
    }

    public String Copycat(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Entrainment(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String RazorWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }
}
