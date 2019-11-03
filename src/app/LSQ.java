package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class LSQ
{
    private final int dimension;
    private Symbol[][] lsqTable;
    private final char startChar;

    public LSQ(int dimension, Symbol[][] lsqTable, char startChar)
    {
        this.dimension = dimension;
        this.lsqTable = lsqTable.clone();
        this.startChar = startChar;
        init();
    }

    public LSQ(LSQ lsq)
    {
        this.dimension = lsq.getDimension();
        this.lsqTable = lsq.getLsqTable().clone();
        this.startChar = lsq.getStartChar();
    }

    public int getDimension()
    {
        return dimension;
    }

    public Symbol[][] getLsqTable()
    {
        return lsqTable.clone();
    }

    public void setLsqTable(Symbol[][] lsqTable)
    {
        this.lsqTable = lsqTable;
    }

    public char getStartChar()
    {
        return startChar;
    }

    public Symbol getSymbol(int column, int row)
    {
        return lsqTable[column][row];
    }

    //initialize the empty cells of a table with random characters
    private void init()
    {
        //pool of usable characters, incremented from start character
        ArrayList<Character> charPool = new ArrayList<>();
        //number of times character can be placed
        HashMap<Character, Integer> charCount = new HashMap<>();
        Random random = new Random();
        char c = startChar;
        for(int x = 0; x < dimension; x++)
        {
            charPool.add(c);
            charCount.put(c, dimension);
            c++;
        }

        //find initial locked characters and reduce counts
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                if(lsqTable[i][j] != null)
                {
                    c = lsqTable[i][j].getCharacter();
                    charCount.put(c, charCount.get(c) - 1);
                }
            }
        }

        //iterate through columns and rows of table
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                int poolIndex;
                if(lsqTable[i][j] == null)
                {
                    //select random index to pull from pool
                    poolIndex = random.nextInt(charPool.size());
                    c = charPool.get(poolIndex);
                    lsqTable[i][j] = new Symbol(c, false);

                    //decrement currently selected character or remove if only one left
                    if(charCount.get(c) > 1)
                        charCount.put(c, charCount.get(c) - 1);
                    else
                    {
                        charCount.remove(c);
                        charPool.remove((Character) c);
                    }
                }
            }
        }
    }
}
