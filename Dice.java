package ampersand;

import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Random;

public class Dice
{
    public static Random rand = new Random();

    public static int roll(int dType)
    {
        return (int) (Math.random() * dType + 1);
    }

    public static String determineStat(String message)
    {
        String stat = "";
        if (message.contains("strength") || message.contains("str"))
            stat = "str";
        else if (message.contains("dexterity") || message.contains("dex"))
            stat = "dex";
        else if (message.contains("constitution") || message.contains("con"))
            stat = "con";
        else if (message.contains("intelligence") || (message.contains("int") && !message.contains("intimidation")))
            stat = "int";
        else if (message.contains("wisdom") || message.contains("wis"))
            stat = "wis";
        else if (message.contains("charisma") || message.contains("cha"))
            stat = "cha";

        return stat;
    }
    public static String rollCheck(MessageCreateEvent event)
    {
        String message = event.getMessageContent();
        message = StringMethods.removeCommand(message);
        message = StringMethods.removeSpaces(message);
        message = message.toLowerCase();
        System.out.println(message);

        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);

        if (message.contains("damage"))
            return rollDamage(currentPC, message);

        int total = 0;
        int roll = roll(20);
        int roll2 = -1;

        StringBuilder totalRoll = new StringBuilder();

        String stat = determineStat(message);

        boolean disadvantage = false, advantage = false;

        // if you're wearing the wrong armor, everything is disadvantage
        // also disadvantage if the armor says so
        if ((stat.equals("str") || stat.equals("dex")) && currentPC.isWearingArmorWithoutProficiency()
                || (currentPC.hasArmorStealthDisadvantage() && message.contains("stealth")))
            disadvantage = true;

        // needs to determine adv/disadv before calculating to see if they cancel
            // "disadvantage" should have been removed and replaced with "disadv"
        if (message.contains("disadv"))
        {
            disadvantage = true;
            message = message.substring(0, message.length() - 5); // removes "disadv"
        }
        else if (message.contains("adv"))
        {
            advantage = true;
            message = message.substring(0, message.length() - 2); // removes "adv"
        }

        // at level 7, barbarians get advantage on initiative rolls (called feral instinct)
        if (message.contains("initiative") && currentPC.getBarbarian().hasFeralInstinct())
            advantage = true;

        // can't have both
        if (disadvantage && advantage)
            disadvantage = advantage = false;

        if (advantage)
        {
            roll2 = roll(20);
            total += Integer.max(roll, roll2);
        }
        else if (disadvantage)
        {
            roll2 = roll(20);
            total += Integer.min(roll, roll2);
        }
        else
            total += roll;

        appendD20(totalRoll, roll, roll2, advantage, disadvantage);

        // if rolling a skill check
        for (int i = 0; i < 18; i++)
        {
            // make sure to check for skills with spaces in them
            if (message.contains(Skill.whichSkillReturnString(i)) ||
                    (Skill.whichSkillReturnString(i).equalsIgnoreCase("animal handling")
                            && message.contains("animalhandling")) ||
                    (Skill.whichSkillReturnString(i).equalsIgnoreCase("sleight of hand")
                    && message.contains("sleightofhand")))
            {
                // if the message doesn't specify a stat
                if(stat.isEmpty())
                    stat = Skill.whichDefModReturnString(i);

                return rollSkillCheck(totalRoll, currentPC, total, stat, i);
            }
        }

        if (message.contains("death"))
            return totalRoll.append(" = ").append(total).toString();

        if(message.contains("save"))
            return rollSave(totalRoll, currentPC, total, stat);

        if (message.contains("attack"))
            return rollAttack(totalRoll, currentPC, total, message);

        if (message.contains("initiative"))
            return rollInitiative(totalRoll, currentPC, total);

        if (message.contains("randchar") || message.contains("stats") || message.contains("character"))
        {
            if (message.contains("custom"))
                return rollCustomCharacter();
            else
                return rollCharacter(false);
        }

        if (message.contains("stat") || message.contains("ability score"))
            return rollBasicDice("4d6kh3");

        // this probably shouldn't be here, but idk
        if (advantage || disadvantage)
            return totalRoll.append(" = ").append(total).toString();

        // if it's not a d20 skill check
        return rollBasicDice(message);
    }

    // rolls a skill check
    public static String rollSkillCheck(StringBuilder totalRoll, PlayerCharacter currentPC, int total, String stat, int i)
    {
        // modifier for that stat
        int mod = currentPC.getMod(stat);

        if (mod < 0)
            totalRoll.append(" ").append(mod).insert(totalRoll.length() - 1, " ");
        else
            totalRoll.append(" + ").append(mod);

        total += mod;
        totalRoll.append(" (").append(stat).append(")");

        if (currentPC.getSkills().get(i).isProf())
        {
            total += currentPC.getProf();
            totalRoll.append(" + ").append(currentPC.getProf()).append(" (proficiency)");
        }
        // checks if one is jack of all trades
        else if (currentPC.getBard().isJackOfAllTrades())
        {
            totalRoll.append(" + ").append(currentPC.getProf() / 2).append(" (Jack of All Trades)");
            total += currentPC.getProf() / 2;
        }

        if (currentPC.getSkills().get(i).isExpert())
        {
            total += currentPC.getProf();
            totalRoll.append(" + ").append(currentPC.getProf()).append(" (expertise)");
        }

        totalRoll.append(" = ").append(total);
        return totalRoll.toString();
    }

    // rolls a saving throw
    public static String rollSave(StringBuilder totalRoll, PlayerCharacter currentPC, int total, String stat)
    {
        int mod = currentPC.getMod(stat); // modifier for that stat

        if(mod < 0)
            totalRoll.append(" ").append(mod).insert(totalRoll.length() - 1, " ");
        else
            totalRoll.append(" + ").append(mod);

        total += mod;
        totalRoll.append(" (").append(stat).append(")"); // adds modifier and says which stat it is

        if (currentPC.getModObject(stat).isProf())
        {
            total += currentPC.getProf(); // adds proficiency to total
            totalRoll.append(" + ").append(currentPC.getProf()).append(" (proficiency)"); //adds proficiency to totalRoll
        }
        totalRoll.append(" = ").append(total); //adds the total
        return totalRoll.toString(); //returns totalRoll
    }

    // rolls a d20 attack roll
    public static String rollAttack(StringBuilder totalRoll, PlayerCharacter currentPC, int total, String message)
    {
        Weapon currentWeapon = Weapon.findWeapon(message);
        if (currentWeapon.error)
            return currentWeapon.name;

        // gets the weapon within the PC's array of weapons instead of the general one
        Weapon pcCurrentWeapon = currentPC.getWeaponProfs().get(Weapon.allWeapons.indexOf(currentWeapon));

        String stat = pcCurrentWeapon.dexOrStr(message);
        int mod = currentPC.getMod(stat);
        total += mod;

        if (mod < 0)
            totalRoll.append(" ").append(mod).insert(totalRoll.length() - 1, " ");
        else
            totalRoll.append(" + ").append(mod);

        totalRoll.append(" (").append(stat).append(")");

        System.out.println(pcCurrentWeapon);

        if (pcCurrentWeapon.isProf())
        {
            total += currentPC.getProf();
            totalRoll.append(" + ").append(currentPC.getProf()).append(" (proficiency)");
        }

        totalRoll.append(" = ").append(total);
        return totalRoll.toString();
    }

    // rolls for damage with a weapon
    public static String rollDamage(PlayerCharacter currentPC, String message)
    {
        StringBuilder totalRoll = new StringBuilder();

        Weapon currentWeapon = Weapon.findWeapon(message);
        if (currentWeapon.error)
            return currentWeapon.name;

        int numDice = currentWeapon.numDice;
        int damageDie = currentWeapon.damageDie;

        if (message.contains("two-handed") && currentWeapon.isVersatile)
            damageDie = currentWeapon.versatileDie;

        totalRoll.append(numDice).append("d").append(damageDie);

        String stat = currentWeapon.dexOrStr(message);
        int mod = currentPC.getMod(stat);

        if (mod < 0)
            totalRoll.append(mod);
        else
            totalRoll.append("+").append(mod);

        totalRoll.append(" (").append(stat).append(")");

        // if it's a damage roll, it's just a normal roll with the num of dice, damage die, and mod just added
        return rollBasicDice(String.valueOf(totalRoll));
    }

    // rolls initiative, can be modified later to include bonuses like war magic or swashbuckler
    public static String rollInitiative(StringBuilder totalRoll, PlayerCharacter currentPC, int total)
    {
        String stat = "dex";
        int mod = currentPC.getMod(stat);

        if (mod < 0)
            totalRoll.append(" ").append(mod).insert(totalRoll.length() - 1, " ");
        else
            totalRoll.append(" + ").append(mod);

        total += mod;
        totalRoll.append(" (").append(stat).append(")");

        if (currentPC.getRace() instanceof Harengon)
        {
            totalRoll.append(" + ").append(currentPC.getProf()).append(" (Harengon)");
            total += currentPC.getProf();
        }
        else if (currentPC.getBard().isJackOfAllTrades())
        {
            totalRoll.append(" + ").append(currentPC.getProf() / 2).append(" (Jack of All Trades)");
            total += currentPC.getProf() / 2;
        }

        totalRoll.append(" = ").append(total);
        return totalRoll.toString();
    }

    public static String rollCustomCharacter()
    {
        return rollCharacter(true) + "\n" +
                rollCharacter(true) + "\n" +
                rollCharacter(true);
    }
    public static String rollCharacter(boolean custom)
    {
        StringBuilder retval = new StringBuilder();
        int total = 0;

        for (int i = 0; i < 6; i++)
        {
            retval.append(rollBasicDice("4d6kh3"));

            total += Integer.parseInt(StringMethods.removeSpaces(retval.substring(retval.length() - 2)));

            retval.append("\n");
        }

        retval.append("Total: ").append(total);

        if (custom && (total < 70 || total > 75))
            return rollCharacter(true);

        return retval.toString();
    }

    public static String rollBasicDice(String message)
    {
        // check if there's a k or if index d in substring index d to the end is greater than -1
            // roll dropping highest or lowest
        // place to do this is later

        StringBuilder totalRoll = new StringBuilder();
        int total = 0;
        int numSigns = StringMethods.countSigns(message); // the number of +/- signs in the message

        boolean keepHigh = false;
        int numToKeep;

        // prevents a bug where it counts signs one too many and runs too many times
        if (message.charAt(0) == '-')
            numSigns--;

        for (int i = 0; i <= numSigns; i++)
        {
            int nextSign = StringMethods.findNextSign(message);

            if (message.charAt(0) == '+')
            {
                totalRoll.append(" + ");
                totalRoll.append(message, 1, nextSign); // adds the message without the plus at the beginning
            }
            else if (message.charAt(0) == '-')
            {
                totalRoll.append(" - ");
                totalRoll.append(message, 1, nextSign); // adds the message without the minus at the beginning
            }
            else
            {
                totalRoll.append(message, 0, nextSign); // adds the message
            }


            // if there's a d, not if it's just a number
            // make sure not to do this for skills, although it shouldn't be here if it's a skill
            if (message.contains("d") && message.indexOf("d") < nextSign
                    && message.charAt(message.indexOf("d") + 1) != 'l'
                    && message.charAt(message.indexOf("d") + 1) != 'e'
                    && message.charAt(message.indexOf("d") + 1) != 'a'
                    && message.charAt(message.indexOf("d") + 1) != 'i')
            {
                int numDice;

                try
                {
                    if (message.charAt(0) == '-')
                        numDice = Integer.parseInt(message.substring(1, message.indexOf("d")));
                    else
                        numDice = Integer.parseInt(message.substring(0, message.indexOf("d")));
                }
                catch (NumberFormatException e)
                {
                    System.out.println("Setting number of dice to 1");
                    numDice = 1;
                }

                int typeDice;

                // if this is a roll like 4d6kh3
                if (message.contains("k") && message.indexOf("k") < nextSign)
                {
                    typeDice = Integer.parseInt(message.substring(message.indexOf("d") + 1, message.indexOf("k")));

                    if (message.charAt(message.indexOf("k") + 1) == 'h')
                        keepHigh = true;
                    else if (message.charAt(message.indexOf("k") + 1) == 'l')
                        keepHigh = false;

                    numToKeep = Integer.parseInt(message.substring(message.indexOf("k") + 2));

                    // the results of each of the die rolls
                    // use DiceRoll instead of int to keep track of which ones are the highest/lowest
                    DiceRoll [] results = new DiceRoll[numDice];

                    // roll the actual dice and put them into the array
                    for (int j = 0; j < results.length; j++)
                        results[j] = new DiceRoll((int)(Math.random() * typeDice + 1));

                    // if the command says to keep more dice than there are, go to the next iteration
                    if (numToKeep >= numDice)
                    {
                        // append the first value to avoid an extra comma and space
                        totalRoll.append(" (").append(results[0].roll);
                        total += results[0].roll;

                        for (int k = 1; k < results.length; k++)
                        {
                            totalRoll.append(", ").append(results[i].roll);
                            total += results[i].roll;
                        }

                        totalRoll.append(")");

                        message = message.substring(StringMethods.findNextSign(message));

                        continue;
                    }

                    // determine which rolls to keep based on command and mark them to keep
                    // go through until there's enough rolls
                    for (int j = 0; j < numToKeep; j++)
                    {
                        int indexToKeep;

                        // find the first value not marked to be kept as a default indexToKeep
                        for (indexToKeep = 0; results[indexToKeep].kept; indexToKeep++)
                            ;

                        // iterate through the results of the die rolls
                        // if keepHigh, check for the highest values
                        if (keepHigh)
                        {
                            for (int k = 0; k < results.length; k++)
                            {
                                // find index of the largest value in the array not already marked to keep
                                if (!results[k].kept && results[k].roll > results[indexToKeep].roll)
                                    indexToKeep = k;
                            }
                        }
                        else
                        {
                            for (int k = 0; k < results.length; k++)
                            {
                                // find index of the smallest value in the array not already marked to keep
                                if (!results[k].kept && results[k].roll < results[indexToKeep].roll)
                                    indexToKeep = k;
                            }
                        }

                        // mark that largest/smallest result as one to keep
                        results[indexToKeep].kept = true;
                    }

                    totalRoll.append(" (");

                    // loop through the result array and add the values to the resultMessage
                    // if a value's not marked to be kept, mark that it's not being added with ~~
                    // otherwise, add its value to the total
                    for (int j = 0; j < results.length; j++)
                    {
                        if (results[j].kept)
                        {
                            totalRoll.append(results[j].roll);
                            total += results[j].roll;
                        }
                        else
                            totalRoll.append("~~").append(results[j].roll).append("~~");

                        totalRoll.append(", ");
                    }

                    // remove the ", " at the end
                    totalRoll.delete(totalRoll.length() - 2, totalRoll.length());

                    totalRoll.append(")");
                }
                else // if no k, just normal dice
                {
                    typeDice = Integer.parseInt(message.substring(message.indexOf("d") + 1, nextSign));

                    totalRoll.append(" (");

                    for (int j = 0; j < numDice; j++)
                    {
                        int roll = roll(typeDice);
                        totalRoll.append(roll).append(", ");
                        total += roll;
                        if (message.charAt(0) == '-')
                            total -=  2 * roll;
                    }

                    totalRoll.replace(totalRoll.length() - 2, totalRoll.length(), ")");
                }
            }
            // have an else if k or many ds
            else // if it's just a number being added/subtracted
            {
                // if the "next sign" is the end of the string, it sets that variable to be the next non-digit char instead
                if (nextSign == message.length())
                {
                    for (int j = 0; j < message.length(); j++)
                        if (!(java.lang.Character.isDigit(message.charAt(j))
                                || message.charAt(j) == '+' || message.charAt(j) == '-')
                                || message.charAt(j) == ' ')
                        {
                            nextSign = j;
                            break;
                        }
                }
                try
                {
                    total += Integer.parseInt(message.substring(0, nextSign));
                }
                catch(NumberFormatException e)
                {
                    System.out.println("error when adding/subtracting a number!");
                    return "Womp womp! Something went wrong with your command. Please try again.";
                }
            }

            // removes the thing we just looked at from the message
            message = message.substring(StringMethods.findNextSign(message));
        }

        totalRoll.append(" = ").append(total);
        return totalRoll.toString();
    }

    // appends the d20 and if advantage
    public static void appendD20(StringBuilder totalRoll, int roll, int roll2, boolean adv, boolean dis)
    {
        totalRoll.append("1d20");

        if (adv)
        {
            totalRoll.append(" with advantage (");
            if (roll > roll2)
                totalRoll.append(roll).append(", ~~").append(roll2).append("~~");
            else
                totalRoll.append("~~").append(roll).append("~~, ").append(roll2);
        }
        else if (dis)
        {
            totalRoll.append(" with disadvantage (");
            if (roll < roll2)
                totalRoll.append(roll).append(", ~~").append(roll2).append("~~");
            else
                totalRoll.append("~~").append(roll).append("~~, ").append(roll2);
        }
        else
            totalRoll.append(" (").append(roll);

        totalRoll.append(")"); //adds the roll
    }
}

class DiceRoll
{
    public int roll;

    public boolean kept;

    public DiceRoll(int roll)
    {
        this.roll = roll;
    }
}
