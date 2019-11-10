package app.Algorithm;

import app.Data.Symbol;

import java.util.HashMap;
import java.util.Map;

//class that corresponds to a single cell and keeps track of how many solutions agree upon the characters for it
public class CellAgreement implements Comparable<CellAgreement>
{
    private int column;
    private int row;
    private int mostAgreedCount;
    private Symbol mostAgreedSymbol;
    private HashMap<Character, Integer> agreementCounts;

    public CellAgreement(int column, int row)
    {
        this.column = column;
        this.row = row;
        mostAgreedCount = 0;
        agreementCounts = new HashMap<>();
    }

    public CellAgreement(CellAgreement cellAgreement)
    {
        this.column = cellAgreement.getColumn();
        this.row = cellAgreement.getRow();
        mostAgreedCount = cellAgreement.getMostAgreedCount();
        agreementCounts = new HashMap<>(cellAgreement.getAgreementCounts());
        if(cellAgreement.getMostAgreedSymbol() != null)
            this.mostAgreedSymbol = new Symbol(cellAgreement.getMostAgreedSymbol());
    }

    public int getColumn()
    {
        return column;
    }

    public int getRow()
    {
        return row;
    }

    public int getMostAgreedCount()
    {
        return mostAgreedCount;
    }

    public Symbol getMostAgreedSymbol()
    {
        return mostAgreedSymbol;
    }

    public HashMap<Character, Integer> getAgreementCounts()
    {
        return agreementCounts;
    }

    public void incrementCharacterCount(char character)
    {
        if(agreementCounts.containsKey(character))
        {
            int count = agreementCounts.get(character);
            count++;
            agreementCounts.put(character, count);
        }
        else
            agreementCounts.put(character, 1);

        update();
    }

    //sometimes need  to remove top character and try next best if that character is unusable
    public void removeMostAgreedCharacter()
    {
        agreementCounts.remove(mostAgreedSymbol.getCharacter());
        mostAgreedSymbol = null;
        mostAgreedCount = 0;
        update();
    }

    //update the highest variables
    private void update()
    {
        if(!agreementCounts.isEmpty())
        {
            for(Map.Entry<Character, Integer> entry: agreementCounts.entrySet())
            {
                if(mostAgreedCount == 0 || entry.getValue() > mostAgreedCount)
                {
                    mostAgreedCount = entry.getValue();
                    mostAgreedSymbol = new Symbol(entry.getKey(), false);
                }
            }
        }
    }

    @Override
    public int compareTo(CellAgreement other)
    {
        Integer count = mostAgreedCount;
        return count.compareTo(other.getMostAgreedCount());
    }
}
