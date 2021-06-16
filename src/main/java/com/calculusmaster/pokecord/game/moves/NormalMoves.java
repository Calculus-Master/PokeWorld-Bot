package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.elements.Weather;
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
        opponent.changeStatMultiplier(Stat.ATK, -1);
        return "It lowered " + opponent.getName() + "'s Attack by one stage!";
    }

    public String Growth(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean harshSun = duel.weather.equals(Weather.HARSH_SUNLIGHT);
        user.changeStatMultiplier(Stat.ATK, harshSun ? 2 : 1);
        user.changeStatMultiplier(Stat.SPATK, harshSun ? 2 : 1);
        return "It increased " + user.getName() + "'s Attack and Special Attack by " + (harshSun ? 2 : 1) + " stage" + (harshSun ? "s" : "") + "!";
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

        Type t = switch(typeVal)
                {
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

        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage) + "Hidden Power's type was " + Global.normalCase(t.toString()) + "! ";
    }

    //TODO: Come up with a custom idea for it
    public String Roar(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String WorkUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.ATK, 1);
        user.changeStatMultiplier(Stat.SPATK, 1);

        return "It increased " + user.getName() + "'s Attack and Special Attack by 1 stage!";
    }

    public String TakeDown(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);

        opponent.damage(damage);
        user.damage(damage / 4);

        return move.getDamageResult(opponent, damage) + " " + move.getRecoilDamageResult(user, damage / 4);
    }

    //TODO: Evasion or custom idea
    public String SweetScent(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String DoubleEdge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);

        opponent.damage(damage);
        user.damage(damage / 3);

        return move.getDamageResult(opponent, damage) + " " + move.getRecoilDamageResult(user, damage / 3);
    }

    public String Scratch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String Smokescreen(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Accuracy Stat Multiplier
        return move.getNotImplementedResult();
    }

    public String ScaryFace(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.SPD, -2);

        return opponent.getName() + "'s Speed fell by 2 stages!";
    }

    public String Slash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(3);
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);
        user.setCrit(1);

        return move.getDamageResult(opponent, damage);
    }

    public String TailWhip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.DEF, -1);

        return opponent.getName() + "'s Defense was lowered by 1 stage!";
    }

    public String RapidSpin(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.changeStatMultiplier(Stat.SPD, 1);

        return move.getDamageResult(opponent, damage) + " " + user.getName() + "'s Speed rose by 1 stage!";
    }

    public String Protect(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).protectUsed = true;

        return user.getName() + " is now protected!";
    }

    public String SkullBash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.changeStatMultiplier(Stat.DEF, 1);

        return move.getDamageResult(opponent, damage) + " " + user.getName() + "'s Defense rose by 1 stage!";
    }

    public String ShellSmash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, -1);
        user.changeStatMultiplier(Stat.SPDEF, -1);
        user.changeStatMultiplier(Stat.ATK, 2);
        user.changeStatMultiplier(Stat.SPATK, 2);
        user.changeStatMultiplier(Stat.SPD, 2);

        return user.getName() + "'s Defense and Special Defense were lowered by 1 stage each! " + user.getName() + "'s Attack, Special Attack and Speed rose by 2 stages each!";
    }

    public String DefenseCurl(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, 1);

        duel.data(user.getUUID()).defenseCurlUsed = true;

        return user.getName() + "'s Defense rose by 1 stage!";
    }

    public String Supersonic(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.addStatusCondition(StatusCondition.CONFUSED);

        return opponent.getName() + " is confused!";
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
        boolean success = new Random().nextInt(100) < 50;
        if(success) opponent.changeStatMultiplier(Stat.SPATK, -2);

        return success ? opponent.getName() + "'s Special Attack was lowered by 2 stages!" : move.getNothingResult();
    }

    public String Harden(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, 1);

        return user.getName() + "'s Defense rose by 1 stage!";
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

    public String FocusEnergy(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(user.getCrit() * 3);

        return user.getName() + "'s critical ratio was increased!";
    }

    public String Endeavor(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = opponent.getHealth() - user.getHealth();

        if(damage > 0)
        {
            opponent.damage(damage);
            return move.getDamageResult(opponent, damage);
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
            opponent.damage(20);

            return move.getDamageResult(opponent, 20);
        }
    }

    public String Screech(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.DEF, -2);
        return opponent.getName() + "'s Defense was lowered by 2 stages!";
    }

    public String LockOn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).lockOnUsed = true;
        return user.getName() + " is guaranteed to hit the next attack!";
    }

    public String SwordsDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.ATK, 2);

        return user.getName() + "'s Attack rose by 2 stages!";
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
        int heal = new Random().nextInt(user.getStat(Stat.HP) / 2) + 1;

        user.heal(heal);

        return user.getName() + " healed for " + heal + " HP!";
    }

    public String MindReader(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Leer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.DEF, -1);

        return opponent.getName() + "'s Defense was lowered by 1 stage!";
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
        user.setCrit(24);
        return user.getName() + " has guaranteed critical hits!";
    }

    public String Swift(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PsychUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setDefaultStatMultipliers();

        for(Stat s : Stat.values()) if(opponent.getStatMultiplier(s) > 0) user.changeStatMultiplier(s, (int)(opponent.getStatMultiplier(s) * 2));

        return user.getName() + " copied " + opponent.getName() + "'s stat multipliers!";
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
        user.heal(user.getStat(Stat.HP) / 2);

        return user.getName() + " healed for " + (user.getStat(Stat.HP) / 2) + " HP!";
    }

    public String Headbutt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String MorningSun(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int HP = switch(duel.weather)
                {
                    case CLEAR -> user.getStat(Stat.HP) / 2;
                    case HARSH_SUNLIGHT -> user.getStat(Stat.HP) * 2 / 3;
                    default -> user.getStat(Stat.HP) / 4;
                };

        user.heal(HP);

        return user.getName() + " healed for " + HP + " HP!";
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
        user.addStatusCondition(StatusCondition.CONFUSED);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + " is confused!";
    }

    public String Bind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.addStatusCondition(StatusCondition.BOUND);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + opponent.getName() + " is Bound!";
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
        opponent.changeStatMultiplier(Stat.ATK, -1);
        opponent.changeStatMultiplier(Stat.SPATK, -1);

        return opponent.getName() + "'s Attack and Special Attack were lowered by 1 stage!";
    }

    public String MegaPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PerishSong(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).perishSongTurns = 3;
        duel.data(opponent.getUUID()).perishSongTurns = 3;

        return user.getName() + " sung a Perish Song!";
    }

    public String Sing(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.addStatusCondition(StatusCondition.ASLEEP);

        return opponent.getName() + " is asleep!";
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
        int damage = move.getDamage(user, opponent);
        damage += move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
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
        user.changeStatMultiplier(Stat.DEF, 1);
        user.changeStatMultiplier(Stat.SPDEF, 1);

        return user.getName() + "'s Defense and Special Defense rose by 1 stage!";
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
        opponent.changeStatMultiplier(Stat.SPATK, -1);

        return opponent.getName() + "'s Special Attack was lowered by 1 stage!";
    }

    public String Tickle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.ATK, -1);
        opponent.changeStatMultiplier(Stat.DEF, -1);

        return opponent.getName() + "'s Attack and Defense were lowered by 1 stage!";
    }

    public String HornDrill(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.getLevel() > user.getLevel()) return move.getNoEffectResult(opponent);
        else
        {
            int damage = opponent.getHealth();
            opponent.damage(damage);

            return move.getDamageResult(opponent, damage);
        }
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
        int damage = opponent.getHealth() / 2;
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String Assist(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        List<String> banned = Arrays.asList("Assist", "Baneful Bunker", "Beak Blast", "Belch", "Bestow", "Bounce", "Chatter", "Circle Throw", "Copycat", "Counter", "Covet", "Destiny Bond", "Detect", "Dig", "Dive", "Dragon Tail", "Endure", "Feint", "Fly", "Focus Punch", "Follow Me", "Helping Hand", "Hold Hands", "Kings Shield", "Mat Block", "Me First", "Metronome", "Mimic", "Mirror Coat", "Mirror Move", "Nature Power", "Phantom Force", "Protect", "Rage Powder", "Roar", "Shadow Force", "Shell Trap", "Sketch", "Sky Drop", "Sleep Talk", "Snatch", "Spiky Shield", "Spotlight", "Struggle", "Switcheroo", "Thief", "Transform", "Trick", "Whirlwind");
        List<String> pool = new ArrayList<>();
        List<Pokemon> team = List.copyOf(duel.getPlayers()[duel.playerIndexFromUUID(user.getUUID())].team);

        for(Pokemon p : team) pool.addAll(p.getLearnedMoves());

        pool = pool.stream().distinct().filter(s -> !banned.contains(s)).collect(Collectors.toList());

        move = new Move(pool.get(new Random().nextInt(pool.size())));

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PlayNice(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.ATK, -1);
        return opponent.getName() + "'s Attack was lowered by 1 stage!";
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

    //TODO: Fix multi hit attacks (Comet Punch, Fury Attack, Water Shuriken, Icicle Crash)
    public String CometPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }
}
