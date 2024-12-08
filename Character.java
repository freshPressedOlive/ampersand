package ampersand;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Character
{
    //default average array
    public static int[] defaultStats = {10, 10, 10, 10, 10, 10};

    // generic instance variables
    private String name;
    int[] stats; // array of actual stat array
    private ArrayList<Modifier> mods = new ArrayList<>(); // arraylist of modifiers
    private int proficiencyBonus;
    private int armorClass;
    int speed;
    String size = "Medium";
    private int maxHP;
    private int currentHP;
    private int numHitDice;
    private int typeHitDice;
    String[] languages;
    private boolean darkvision;
    int darkvisionRange;
    boolean sunlightSensitivity;
    boolean advantageAgainstPoison;
    DamageType[] damageTypes = new DamageType[13];

    // spells available through innate spellcasting or for NPCs
    // I honestly don't even know what to do for phb vs later spellcasting where you can use spell slots or not
    ArrayList<Spell> spellsAvailable;

    // constructors
    public Character()
    {
        this.name = "Commoner";
        this.stats = defaultStats;
        setMods();
        this.proficiencyBonus = 2;
    }
    public Character(String name, int[] stats, int maxHP, String[] languages)
    {
        this.name = name;
        this.stats = stats;
        setMods();
        this.armorClass = 10 + getMod("dex");
        this.speed = 30;
        this.maxHP = this.currentHP = maxHP;
        this.languages = languages;
    }

    // lets a character cast a spell
    public String castSpell(String text)
    {
        System.out.println("Going through the character version of spellcasting!");

        // the name of the spell to be cast, with &cast removed
        String spellName = StringMethods.removeCommand(text);
        String extraInfo = "";

        // if it contains extra info, get just the first word
        if (spellName.contains(" "))
        {
            extraInfo = spellName.substring(spellName.indexOf(" "));
            spellName = spellName.substring(0, spellName.indexOf(" "));
        }

        // remove punctuation
        spellName = StringMethods.removePunctuation(spellName);

        // if there's innate spellcasting, look through available spells
        // used for non-class spells like racial spells, as well as for NPCs
        if (spellsAvailable != null)
        {
            String innateAttempt = findSpell(spellsAvailable, spellName, extraInfo, null);
            if (innateAttempt != null)
                return innateAttempt;
        }

        if (this instanceof PlayerCharacter)
            return ((PlayerCharacter)this).castSpellPC(spellName, extraInfo);

        return "A non-PC is casting a spell somehow!";
    }

    // actually finds the spell and casts it
    public String findSpell(ArrayList<Spell> spellsAvailable, String spellName, String extraInfo, PCClass theClass)
    {
        for (Spell spell : spellsAvailable)
        {
            // if the spell name given matches the id
            if (spellName.equalsIgnoreCase(spell.id))
            {
                // the spell can be cast
                try
                {
                    // the parameters can be determined with getParameterTypes() and if statements
                    Class<?>[] parameters = spell.spellFunction.getParameterTypes();
                    // different spells will take different parameters
                        // if statements can be used to determine what to pass it when invoking it
                        // many might take the spell save DC, spell attack mod, or targets
                            // all of these come from the theClass parameter
                        // whatever is in extra info should be parsed here, if possible

                    if (parameters.length == 1 && parameters[0] == PlayerCharacter.class)
                        return (String) (spell.spellFunction.invoke(null, this));

                    // invoke the spell's function
                    // you'll want to pass it things based on the class or, if null, the determined things
                    return (String) (spell.spellFunction.invoke(null, this, extraInfo));
                }
                catch (IllegalAccessException | InvocationTargetException e)
                {
                    System.out.println("Couldn't invoke the function for the spell " + (spell.name));
                }
            }
        }

        return null;
    }

    // adds a spell to the list of spells available
    public void learnSpell(String spellName)
    {
        if (spellsAvailable == null)
            spellsAvailable = new ArrayList<>();

        Spell spell = Spell.findSpell(spellName);

        if (spell == null)
        {
            System.out.println("Spell " + spellName + " not found!");
            return;
        }

        spellsAvailable.add(spell);
    }


    // setters
    private void setMods()
    {
        for (int i = 0; i < 6; i++)
        {
            Modifier thisMod = new Modifier(Modifier.whichStatReturnString(i), stats[i]);
            mods.add(thisMod);
        }
    }
    public void setProficiencyBonus(int prof)
    {
        this.proficiencyBonus = prof;
    }
    public void setNumHitDice(int hitDice)
    {
        numHitDice = hitDice;
    }
    public void setTypeHitDice(int hitDice)
    {
        typeHitDice = hitDice;
    }
    public void setDarkvision()
    {
        this.darkvision = true;
    }

    private void makeDamageTypes()
    {
        for (int i = 0; i < 13; i++)
            damageTypes[i] = new DamageType(i);
    }
    public void increaseArmorClass(int increase)
    {
        armorClass += increase;
    }
//    public void increaseArmorClassByOne()
//    {
//        armorClass += 1;
//    }
    public void setArmorClass(int newAC)
    {
        armorClass = newAC;
    }


    // getters
    public String getName()
    {
        return name;
    }
    public String sendName()
    {
        return "character is named " + name;
    }
    public int getStat(int stat)
    {
        return stats[stat];
    }
    public int getStat(String stat)
    {
        return stats[Modifier.whichStatReturnInt(stat)];
    }
    public int[] getStatArray()
    {
        return stats;
    }
    public int getMod(String stat)
    {
        int mod = 0;

        // iterates through modifiers
        for (Modifier modifier : mods)
        {
            // if given stat name equals modifier stat name
            if (stat.equals(modifier.getName()))
                mod = modifier.getModifier();
        }
        return mod;
    }
    // returns a certain modifier if given 0-5 inclusive
    public int getMod(int stat)
    {
        int mod = 0;

        // iterates through modifiers
        for (Modifier modifier : mods)
        {
            // if name associated with stat equals mod name
            if (Modifier.whichStatReturnString(stat).equals(modifier.getName()))
                mod = modifier.getModifier();
        }
        return mod;
    }
    // returns modifier as an object for saves code
    public Modifier getModObject(String stat)
    {
        for (Modifier modifier : mods)
        {
            // if given stat name equals modifier stat name
            if (stat.equals(modifier.getName()))
                return modifier;
        }
        return new Modifier("mod not identified", 0);
    }
    public ArrayList<Modifier> getMods()
    {
        return mods;
    }
    public int getProf()
    {
        return proficiencyBonus;
    }
    public int getArmorClass()
    {
        return armorClass;
    }
    public int getNumHitDice()
    {
        return numHitDice;
    }
    public int getTypeHitDice()
    {
        return typeHitDice;
    }
    public int getCurrentHP()
    {
        return currentHP;
    }
    public int getMaxHP()
    {
        return maxHP;
    }
    public String[] getLanguages()
    {
        return languages;
    }
}
