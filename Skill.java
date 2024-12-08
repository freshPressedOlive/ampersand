package ampersand;

public class Skill
{
    public static final int NUM_SKILLS = 18;
    private final String name;
    private boolean isProf;
    private boolean isExpert;
    private final String defMod;

    public Skill(int name, boolean isProf, boolean isExpert)
    {
        this.name = whichSkillReturnString(name);
        this.isProf = isProf;
        this.isExpert = isExpert;
        defMod = whichDefModReturnString(name);
    }

    public String getName(){
        return name;
    }
    public boolean isProf(){
        return isProf;
    }
    public boolean isExpert()
    {
        return isExpert;
    }
    public void setProf()
    {
        isProf = true;
    }

    //gives skills in alphabetical order, not sure why
    public static String whichSkillReturnString(int skill)
    {
        if(skill == 0)
            return "acrobatics";
        if(skill == 1)
            return "animal handling";
        if(skill == 2)
            return "arcana";
        if(skill == 3)
            return "athletics";
        if(skill == 4)
            return "deception";
        if(skill == 5)
            return "history";
        if(skill == 6)
            return "insight";
        if(skill == 7)
            return "intimidation";
        if(skill == 8)
            return "investigation";
        if(skill == 9)
            return "medicine";
        if(skill == 10)
            return "nature";
        if(skill == 11)
            return "perception";
        if(skill == 12)
            return "performance";
        if(skill == 13)
            return "persuasion";
        if(skill == 14)
            return "religion";
        if(skill == 15)
            return "sleight of hand";
        if(skill == 16)
            return "stealth";
        if(skill == 17)
            return "survival";
        return "";
    }
    //returns default mod for skills
    public static String whichDefModReturnString(int skill)
    {
        if(skill == 0)
            return "dex";
        if(skill == 1)
            return "wis";
        if(skill == 2)
            return "int";
        if(skill == 3)
            return "str";
        if(skill == 4)
            return "cha";
        if(skill == 5)
            return "int";
        if(skill == 6)
            return "wis";
        if(skill == 7)
            return "cha";
        if(skill == 8)
            return "int";
        if(skill == 9)
            return "wis";
        if(skill == 10)
            return "int";
        if(skill == 11)
            return "wis";
        if(skill == 12)
            return "cha";
        if(skill == 13)
            return "cha";
        if(skill == 14)
            return "int";
        if(skill == 15)
            return "dex";
        if(skill == 16)
            return "dex";
        if(skill == 17)
            return "wis";
        return "";
    }
}
