package ampersand;

import java.util.ArrayList;

public abstract class PCClass
{
    public static final int NUM_CLASSES = 13;

    // instance variables
    public int level; // the level that the character is in the class
    public PlayerCharacter PC;

    public PCClass(int level, PlayerCharacter PC)
    {
        this.level = level;
        this.PC = PC;
    }

    public static String whichClassReturnString(int theClass)
    {
        if (theClass == 0)
            return "Artificer";
        if (theClass == 1)
            return "Barbarian";
        if (theClass == 2)
            return "Bard";
        if (theClass == 3)
            return "Cleric";
        if (theClass == 4)
            return "Druid";
        if (theClass == 5)
            return "Fighter";
        if (theClass == 6)
            return "Monk";
        if (theClass == 7)
            return "Paladin";
        if (theClass == 8)
            return "Ranger";
        if (theClass == 9)
            return "Rogue";
        if (theClass == 10)
            return "Sorcerer";
        if (theClass == 11)
            return "Warlock";
        if (theClass == 12)
            return "Wizard";
        return "Fighter";
    }

    public static int whichClassReturnInt(String theClass)
    {
        if (theClass.equalsIgnoreCase("artificer"))
            return 0;
        if (theClass.equalsIgnoreCase("barbarian"))
            return 1;
        if (theClass.equalsIgnoreCase("bard"))
            return 2;
        if (theClass.equalsIgnoreCase("cleric"))
            return 3;
        if (theClass.equalsIgnoreCase("druid"))
            return 4;
        if (theClass.equalsIgnoreCase("fighter"))
            return 5;
        if (theClass.equalsIgnoreCase("monk"))
            return 6;
        if (theClass.equalsIgnoreCase("paladin"))
            return 7;
        if (theClass.equalsIgnoreCase("ranger"))
            return 8;
        if (theClass.equalsIgnoreCase("rogue"))
            return 9;
        if (theClass.equalsIgnoreCase("sorcerer"))
            return 10;
        if (theClass.equalsIgnoreCase("warlock"))
            return 11;
        if (theClass.equalsIgnoreCase("wizard"))
            return 12;
        else
            return 0;
    }

    public static int getTypeHitDiceClass(String theClass)
    {
        return switch (theClass)
        {
            case "Barbarian" -> 12;
            case "Fighter", "Paladin", "Ranger" -> 10;
            case "Artificer", "Bard", "Cleric", "Druid", "Monk", "Rogue", "Warlock" -> 8;
            case "Sorcerer", "Wizard" -> 6;
            default -> 1;
        };
    }

    public abstract void setClassFeatures();

    public int getLevel()
    {
        return level;
    }
}

abstract class Caster extends PCClass
{
    int spellcastingAbilityMod;
    int spellSaveDC;
    int spellAttackMod;

    // which index in the spellLists array for a spell this class is
    int numInSpellListsArray;

    ArrayList<Spell> spellsAvailable = new ArrayList<>();

    public Caster(int level, PlayerCharacter PC, String mod)
    {
        super(level, PC);

        if (level == 0)
            return;

        setSpellMods(mod);
    }

    public void setSpellAttackMod()
    {
        spellAttackMod = PC.getProf() + spellcastingAbilityMod;
    }

    public void setSpellSaveDC()
    {
        spellSaveDC = 8 + PC.getProf() + spellcastingAbilityMod;
    }

    public void setSpellMods(String mod)
    {
        spellcastingAbilityMod = PC.getMod(mod);
        setSpellAttackMod();
        setSpellSaveDC();
    }

    // adds a spell to the list of spells known/prepared
    // should be overridden by wizard
    public void learnSpell(String spellName)
    {
        spellName = StringMethods.removeSpaces(StringMethods.removePunctuation(spellName));

        // finds the spell asked in the particular list of spells
        Spell spell = Spell.findSpell(spellName);

        // if the spell was found
        if (spell != null)
        {
            // if it's not in this class' spell list
            if (!spell.spellLists[this.numInSpellListsArray])
            {
                System.out.println("This spell is not in this class' spell list!");
                return;
            }

            spellsAvailable.add(spell);

            System.out.println("Just learned the spell " + spell.name);
        }
        else // if not found
            System.out.println("This spell wasn't found in the list of all spells!");
    }

    // make an ArrayList of spells available to cast (or maybe one in each class known/prepared?)
        // for some classes, this is known, for some it's prepared
        // for wizard, this is prepared with a separate list for known
        // PlayerCharacter will have a cast method that will check each class to look for the spell
            // this is how it ensures what spells can be cast, and it separates spells known by class
}

class Artificer extends Caster
{
    int numSpellsPrepared;

    public Artificer(int level, PlayerCharacter PC)
    {
        super(level, PC, "int");

        numInSpellListsArray = 0;

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
        setSpellMods("int");
        numSpellsPrepared = PC.getMod("int") + (level / 2);
    }
}

class Barbarian extends PCClass
{
    int numRages = 2;
    int rageDamage = 2;
    boolean unarmoredDefense;

    private boolean feralInstinct;

    public Barbarian(int level, PlayerCharacter PC)
    {
        super(level, PC);

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
        // set number of rages
        if (level >= 3)
            numRages = 3;
        if (level >= 6)
            numRages = 4;
        if (level >= 12)
            numRages = 5;
        if (level >= 17)
            numRages = 6;
        if (level == 20)
            numRages = Integer.MAX_VALUE; // unlimited rages!

        // set rage damage
        if (level >= 9)
            rageDamage = 3;
        if (level >= 16)
            rageDamage = 4;

        // if the PC doesn't already have the unarmored defense trait from monk, give it to them
            // unarmored defense can only be gained once and that's it
            // draconic sorcerer is a choice, so it should automatically choose the higher of the two
        if (!PC.getMonk().unarmoredDefense)
            unarmoredDefense = true;

        if (level >= 7)
            feralInstinct = true;
    }

    public boolean hasFeralInstinct()
    {
        return feralInstinct;
    }
}

class Bard extends Caster
{
    private boolean jackOfAllTrades;
    int numSpellsKnown;

    public Bard(int level, PlayerCharacter PC)
    {
        super(level, PC, "cha");

        numInSpellListsArray = 2;

        if (level == 0)
            return;

        setClassFeatures();
    }

    public void setClassFeatures()
    {
        if (level >= 2)
            jackOfAllTrades = true;

        setSpellMods("cha");
        setNumSpellsKnown();
    }

    public void setNumSpellsKnown()
    {
        if (level == 1)
            numSpellsKnown = 4;
        else if (level == 2)
            numSpellsKnown = 5;
        else if (level == 3)
            numSpellsKnown = 6;
        else if (level == 4)
            numSpellsKnown = 7;
        else if (level == 5)
            numSpellsKnown = 8;
        else if (level == 6)
            numSpellsKnown = 9;
        else if (level == 7)
            numSpellsKnown = 10;
        else if (level == 8)
            numSpellsKnown = 11;
        else if (level == 9)
            numSpellsKnown = 12;
        else if (level == 10)
            numSpellsKnown = 14;
        else if (level == 11 || level == 12)
            numSpellsKnown = 15;
        else if (level == 13)
            numSpellsKnown = 16;
        else if (level == 14)
            numSpellsKnown = 18;
        else if (level == 15 || level == 16)
            numSpellsKnown = 19;
        else if (level == 17)
            numSpellsKnown = 20;
        else if (level == 18 || level == 19 || level == 20)
            numSpellsKnown = 22;
    }

    public boolean isJackOfAllTrades()
    {
        return jackOfAllTrades;
    }
}

class Cleric extends Caster
{
    int numSpellsPrepared;

    public Cleric(int level, PlayerCharacter PC)
    {
        super(level, PC, "wis");

        numInSpellListsArray = 3;

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
        setSpellMods("wis");
        numSpellsPrepared = PC.getMod("wis") + level;
    }
}

class Druid extends Caster
{
    int numSpellsPrepared;

    public Druid(int level, PlayerCharacter PC)
    {
        super(level, PC, "wis");

        numInSpellListsArray = 4;

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
        setSpellMods("wis");
        numSpellsPrepared = PC.getMod("wis") + level;
    }
}

class Fighter extends PCClass
{
    public Fighter(int level, PlayerCharacter PC)
    {
        super(level, PC);

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
    }
}

class EldritchKnight extends Fighter
{
    public EldritchKnight(int level, PlayerCharacter PC)
    {
        super (level, PC);
    }
}

class Monk extends PCClass
{
    boolean unarmoredDefense;
    public Monk(int level, PlayerCharacter PC)
    {
        super(level, PC);

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
        // if the PC doesn't already have the unarmored defense trait from barbarian, give it to them
        // unarmored defense can only be gained once and that's it
        // draconic sorcerer is a choice, so it should automatically choose the higher of the two
        if (!PC.getBarbarian().unarmoredDefense)
            unarmoredDefense = true;
    }
}

class Paladin extends Caster
{
    int numSpellsPrepared;

    public Paladin(int level, PlayerCharacter PC)
    {
        super(level, PC, "cha");

        numInSpellListsArray = 7;

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
        setSpellMods("cha");
        if (level >= 2)
            numSpellsPrepared = PC.getMod("cha") + (level / 2);
    }
}

class Ranger extends Caster
{
    int numSpellsKnown;

    public Ranger(int level, PlayerCharacter PC)
    {
        super(level, PC, "wis");

        numInSpellListsArray = 8;

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
        setSpellMods("wis");
        setNumSpellsKnown();
    }

    public void setNumSpellsKnown()
    {
        if (level == 1)
            numSpellsKnown = 0;
        else if (level == 2)
            numSpellsKnown = 2;
        else if (level == 3 || level == 4)
            numSpellsKnown = 3;
        else if (level == 5 || level == 6)
            numSpellsKnown = 4;
        else if (level == 7 || level == 8)
            numSpellsKnown = 5;
        else if (level == 9 || level == 10)
            numSpellsKnown = 6;
        else if (level == 11 || level == 12)
            numSpellsKnown = 7;
        else if (level == 13 || level == 14)
            numSpellsKnown = 8;
        else if (level == 15 || level == 16)
            numSpellsKnown = 9;
        else if (level == 17 || level == 18)
            numSpellsKnown = 10;
        else if (level == 19 || level == 20)
            numSpellsKnown = 11;
    }
}

class Rogue extends PCClass
{
    public Rogue(int level, PlayerCharacter PC)
    {
        super(level, PC);

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
    }
}

class ArcaneTrickster extends Rogue
{
    public ArcaneTrickster(int level, PlayerCharacter PC)
    {
        super(level, PC);
    }
}

class Sorcerer extends Caster
{
    int numSpellsKnown;
    int numSorceryPoints;

    public Sorcerer(int level, PlayerCharacter PC)
    {
        super(level, PC, "cha");

        numInSpellListsArray = 10;

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
        setSpellMods("cha");
        setNumSpellsKnown();

        if (level >= 2)
            numSorceryPoints = level;
    }

    public void setNumSpellsKnown()
    {
        if (level == 1)
            numSpellsKnown = 2;
        else if (level == 2)
            numSpellsKnown = 3;
        else if (level == 3)
            numSpellsKnown = 4;
        else if (level == 4)
            numSpellsKnown = 5;
        else if (level == 5)
            numSpellsKnown = 6;
        else if (level == 6)
            numSpellsKnown = 7;
        else if (level == 7)
            numSpellsKnown = 8;
        else if (level == 8)
            numSpellsKnown = 9;
        else if (level == 9)
            numSpellsKnown = 10;
        else if (level == 10)
            numSpellsKnown = 11;
        else if (level == 11 || level == 12)
            numSpellsKnown = 12;
        else if (level == 13 || level == 14)
            numSpellsKnown = 13;
        else if (level == 15 || level == 16)
            numSpellsKnown = 14;
        else if (level == 17 || level == 18 || level == 19 || level == 20)
            numSpellsKnown = 15;
    }
}

class Warlock extends Caster
{
    int numSpellsKnown;

    public Warlock(int level, PlayerCharacter PC)
    {
        super(level, PC, "cha");

        numInSpellListsArray = 11;

        if (level == 0)
            return;

        setClassFeatures();
    }

    @Override
    public void setClassFeatures()
    {
        setSpellMods("cha");
        setSpellsKnown();
    }

    public void setSpellsKnown()
    {
        if (level == 1)
            numSpellsKnown = 2;
        else if (level == 2)
            numSpellsKnown = 3;
        else if (level == 3)
            numSpellsKnown = 4;
        else if (level == 4)
            numSpellsKnown = 5;
        else if (level == 5)
            numSpellsKnown = 6;
        else if (level == 6)
            numSpellsKnown = 7;
        else if (level == 7)
            numSpellsKnown = 8;
        else if (level == 8)
            numSpellsKnown = 9;
        else if (level == 9 || level == 10)
            numSpellsKnown = 10;
        else if (level == 11 || level == 12)
            numSpellsKnown = 11;
        else if (level == 13 || level == 14)
            numSpellsKnown = 12;
        else if (level == 15 || level == 16)
            numSpellsKnown = 13;
        else if (level == 17 || level == 18)
            numSpellsKnown = 14;
        else if (level == 19 || level == 20)
            numSpellsKnown = 15;
    }
}

class Wizard extends Caster
{
    // wizards have both spells known and prepared
        // prepared is what's available
    ArrayList<Spell> spellsKnown = new ArrayList<>();

    int numSpellsKnown;
    int numSpellsPrepared;

    // set spells known in the constructor, but be sure to only update it from then on
    // spells prepared can be set whenever, though
    public Wizard(int level, PlayerCharacter PC)
    {
        super(level, PC, "int");

        numInSpellListsArray = 12;

        if (level == 0)
            return;

        setClassFeatures();
        setSpellsKnown();
    }

    @Override
    public void setClassFeatures()
    {
        setSpellMods("int");
        numSpellsPrepared = PC.getMod("int") + level;
    }

    // originally sets spells known 4 + 2*level, only in constructor
    // for each level up add 2, but not in setClassFeatures because it's an update and not a set
    public void setSpellsKnown()
    {
        numSpellsKnown = 4 + (2 * level);
    }
}
