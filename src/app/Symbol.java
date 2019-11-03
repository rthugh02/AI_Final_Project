package app;

public class Symbol
{
    private Character character;
    private boolean isLocked;

    public Symbol(Character character)
    {
        this.character = character;
        this.isLocked = false;
    }

    public Symbol(Character character, boolean isLocked)
    {
        this.character = character;
        this.isLocked = isLocked;
    }

    public Character getCharacter()
    {
        return character;
    }

    public void setCharacter(Character character)
    {
        this.character = character;
    }

    public boolean isLocked()
    {
        return isLocked;
    }

    public void setLocked(boolean locked)
    {
        isLocked = locked;
    }
}
