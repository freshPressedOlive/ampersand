package ampersand;

import java.util.ArrayList;

public class Armor
{
    // a field for how many types of armor there are, to avoid magic numbers
    public static int numArmorTypes;
    public static ArrayList<Armor> allArmors = new ArrayList<>();

    public String name;
    public int costInGP;
    public int weight;
    public String proficiencyType; // light, medium, or heavy
    public int baseAC;
    public boolean stealthDisadvantage;
    public int strengthRequirement; // default 0 is ok

    public Armor(String name, int costInGP, int weight, String proficiencyType, int baseAC)
    {
        this.name = name;
        this.costInGP = costInGP;
        this.weight = weight;
        this.proficiencyType = proficiencyType; // light, medium, or heavy
        this.baseAC = baseAC;
        if (proficiencyType.equals("heavy"))
            this.stealthDisadvantage = true;
    }

    public Armor(String name, int costInGP, int weight, String proficiencyType, int baseAC, boolean stealthDisadvantage)
    {
        this(name, costInGP, weight, proficiencyType, baseAC);
        this.stealthDisadvantage = stealthDisadvantage;
    }

    // if it has a str requirement, it must be heavy, so no point in passing stealth disadvantage in the constructor
    public Armor(String name, int costInGP, int weight, String proficiencyType, int baseAC, int strengthRequirement)
    {
        this(name, costInGP, weight, proficiencyType, baseAC);
        this.strengthRequirement = strengthRequirement;
    }

    public static void makeArmor()
    {
        // light armors
        Armor padded = new Armor("padded", 5, 8, "light", 11, true);
        allArmors.add(padded); // 0

        Armor leather = new Armor("leather", 10, 10, "light", 11);
        allArmors.add(leather); // 1

        Armor studdedLeather = new Armor("studded leather", 45, 13, "light", 12);
        allArmors.add(studdedLeather); // 2

        // medium armors
        Armor hide = new Armor("hide", 10, 12, "medium", 12);
        allArmors.add(hide); // 3

        Armor chainShirt = new Armor("chain shirt", 50, 20, "medium", 13);
        allArmors.add(chainShirt); // 4

        Armor scaleMail = new Armor("scale mail", 50, 45, "medium", 14, true);
        allArmors.add(scaleMail); // 5

        Armor spikedArmor = new Armor("spiked armor", 50, 75, "medium", 14, true);
        allArmors.add(spikedArmor); // 6

        Armor breastplate = new Armor("breastplate", 400, 20, "medium", 14);
        allArmors.add(breastplate); // 7

        Armor halfplate = new Armor("halfplate", 750, 40, "medium", 15, true);
        allArmors.add(halfplate); // 8

        // heavy armors
        Armor ringMail = new Armor("ring mail", 30, 40, "heavy", 14);
        allArmors.add(ringMail); // 9

        Armor chainMail = new Armor("chain mail", 75, 55, "heavy", 16, 13);
        allArmors.add(chainMail); // 10

        Armor splint = new Armor("splint", 200, 60, "heavy", 17, 15);
        allArmors.add(splint); // 11

        Armor plate = new Armor("plate", 1500, 65, "heavy", 18, 15);
        allArmors.add(plate); // 12

        // other
        Armor draconicAncestry = new Armor("draconic ancestry", 0, 0, "other", 13);
        allArmors.add(draconicAncestry); // 13

        Armor shield = new Armor("shield", 10, 6, "medium", 2);
        allArmors.add(shield); // 14

        Armor barbarianUnarmoredDefense = new Armor("barbarian unarmored defense", 0, 0, "other", 10);
        allArmors.add(barbarianUnarmoredDefense); // 15

        Armor monkUnarmoredDefense = new Armor("monk unarmored defense", 0, 0, "other", 10);
        allArmors.add(monkUnarmoredDefense); // 16

        Armor none = new Armor("none", 0, 0, "other", 10);
        allArmors.add(none); // 17
    }
}
