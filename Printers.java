package ampersand;

import org.javacord.api.event.message.MessageCreateEvent;

public class Printers
{
    private PlayerCharacter PC;

    // CHANGE TO SOMETHING NOT SPECIFIC TO PCS
    public static String printName(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        return currentPC.getName();
    }

    // prints a character's modifiers
    public static String printMods(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party); //uses static method from main to find the pc
        StringBuilder mods = new StringBuilder(); //makes a new string builder to add to, whatever that is

        // goes through the stat array, adds the mod for that particular 0-5 and adds a space
        for (int i = 0; i < 6; i++)
            mods.append(currentPC.getMod(i)).append(" ");

        return mods.toString(); // returns string with toString because builder
    }

    // same as printMods but with the stat array
    public static String printStats(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        StringBuilder arr = new StringBuilder();

        //iterates through the stats adds to string, etc.
        for (int i = 0; i < 6; i++)
            arr.append(currentPC.getStat(i)).append(" ");

        return arr.toString();
    }

    public static String printLanguages(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        String[] array = currentPC.getLanguages();
        StringBuilder retval = new StringBuilder();

        if (array[0].isEmpty())
            return "No languages known!";

        retval.append(array[0]);

        for (int i = 1; i < array.length; i++)
            retval.append(", ").append(array[i]);

        return retval.toString();
    }

    public static String printClass(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        return currentPC.getPCClass();
    }

    // prints class and level, like "Fighter 1"
    public static String printClassNLevel(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        StringBuilder classNLevel = new StringBuilder();

        for (int i = 0; i < PCClass.NUM_CLASSES; i++)
        {
            if (currentPC.getPCClass().contains(PCClass.whichClassReturnString(i)))
            {
                classNLevel.append(PCClass.whichClassReturnString(i)).append(" ");
                classNLevel.append(currentPC.getClasses().get(i).getLevel()).append(" ");
            }
        }

        return classNLevel.toString();
    }

    // prints level, adding up levels in various classes
    public static String printTotalLevel(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        String level = "";
        level += currentPC.getLevel();
        return level;
    }

    // prints all the skills that the character is proficient in
    public static String printSkills(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        StringBuilder allSkills = new StringBuilder();

        for (Skill skill: currentPC.getSkills())
        {
            allSkills.append(skill.getName()).append(": ");
            if (skill.isExpert())
                allSkills.append("expertise");
            else if (skill.isProf())
                allSkills.append("proficient");
            else
                allSkills.append("none");
            allSkills.append("\n");
        }

        return allSkills.toString();
    }

    // prints all weapon proficiencies
    public static String printWeaponProfs(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        StringBuilder weaponProfs = new StringBuilder();

        //System.out.println(currentPC.getWeaponProfs().size());

        for (Weapon weapon: currentPC.getWeaponProfs())
        {
            weaponProfs.append(weapon.getName()).append(": ");
            if (weapon.isProf())
                weaponProfs.append("proficient");
            else
                weaponProfs.append("none");
            weaponProfs.append("\n");
        }

        return weaponProfs.toString();
    }

    // prints what armor the character is wearing
    public static String printArmor(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        return String.valueOf(currentPC.getArmorWorn());
    }

    // prints the character's armor class
    public static String printArmorClass(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        return String.valueOf(currentPC.getArmorClass());
    }

    // prints the number and type of maximum hit dice
    public static String printHitDice(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        StringBuilder toReturn = new StringBuilder();

        toReturn.append(currentPC.getNumHitDice()).append("d").append(currentPC.getTypeHitDice());

        // if it's a multiclass
        if (currentPC.getNumHitDice2() != 0)
            toReturn.append(", ").append(currentPC.getNumHitDice2()).append("d").append(currentPC.getTypeHitDice2());

        return String.valueOf(toReturn);
    }

    public static String printHealth(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        return String.valueOf(currentPC.getCurrentHP());
    }

    public static String printMaxHealth(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        return String.valueOf(currentPC.getMaxHP());
    }

    public static String printMaxSpellSlots(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        StringBuilder maxSpellSlots = new StringBuilder();
        int [] spellSlotsArray = currentPC.getMaxSpellSlots();

        if (spellSlotsArray[1] == 0)
            return "This character has no spell slots.";
        maxSpellSlots.append("1st level: ").append(spellSlotsArray[1]).append("\n");

        if (spellSlotsArray[2] == 0)
            return maxSpellSlots.toString();
        maxSpellSlots.append("2nd level: ").append(spellSlotsArray[2]).append("\n");

        if (spellSlotsArray[3] == 0)
            return maxSpellSlots.toString();
        maxSpellSlots.append("3rd level: ").append(spellSlotsArray[3]).append("\n");

        for (int i = 4; i <= 9; i++)
        {
            if (spellSlotsArray[i] == 0)
                return maxSpellSlots.toString();
            maxSpellSlots.append(i).append("th level: ").append(spellSlotsArray[i]).append("\n");
        }

        return maxSpellSlots.toString();
    }

    public static String printSaveProfs(MessageCreateEvent event)
    {
        PlayerCharacter currentPC = Main.getPC(event, PlayerCharacter.party);
        StringBuilder retval = new StringBuilder();

        for (Modifier mod: currentPC.getMods())
        {
            retval.append(mod.getName()).append(": ");
            if (mod.isProf())
                retval.append("proficient");
            else
                retval.append("none");
            retval.append("\n");
        }

        return retval.toString();
    }
}
