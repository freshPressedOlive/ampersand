package ampersand;

import java.util.ArrayList;

public class DamageType
{
    public String name;

    public boolean resistant;
    public boolean vulnerable;

    public boolean physical;
    public boolean silverWeakness;
    public boolean magicalWeaponWeakness;

    public static ArrayList<DamageType> allTypes = new ArrayList<>();

    public DamageType(int i)
    {
        this.name = whichTypeReturnString(i);

        // sets some types as physical
        if (i == 0 || i == 1 || i == 2)
            physical = true;
    }

    // returns damage types based on an int
    // physical types first, then alphabetical
    public static String whichTypeReturnString(int type)
    {
        if(type == 0)
            return "bludgeoning";
        if(type == 1)
            return "piercing";
        if(type == 2)
            return "slashing";
        if(type == 3)
            return "acid";
        if(type == 4)
            return "cold";
        if(type == 5)
            return "fire";
        if(type == 6)
            return "force";
        if(type == 7)
            return "lightning";
        if(type == 8)
            return "necrotic";
        if(type == 9)
            return "poison";
        if(type == 10)
            return "psychic";
        if(type == 11)
            return "radiant";
        if(type == 12)
            return "thunder";
        return "";
    }
}
