package app;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LSQ implements Comparable<LSQ>
{
    private final int dimension;
    private Symbol[][] lsqTable;
    private final char startChar;
    private Boolean isCompleteSolution = false;

    //-1 indicates lowest row has not been determined, other values indicate index
    private int lowestRow = -1;
    private int lowestColumn = -1;

    private double fitness = -1;

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
        this.startChar = lsq.getStartChar();
        this.lowestColumn = lsq.getLowestColumn();
        this.lowestRow = lsq.getLowestRow();
        this.fitness = lsq.getFitness();

        //copy array with newly instantiated Symbols
        lsqTable = new Symbol[dimension][dimension];
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                this.lsqTable[i][j] = new Symbol(lsq.getSymbol(i, j));
            }
        }
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
        return new Symbol(lsqTable[column][row]);
    }

    public void setSymbol(Symbol symbol, int column, int row) { this.lsqTable[column][row] = symbol;}

    public int getLowestRow() 
    {
        return lowestRow == -1 ? null : lowestRow;
    }

    public int getLowestColumn()
    {
        return lowestColumn == -1 ? null : lowestColumn;
    }

    public double getFitness() 
    {
        return fitness;
    }

    public Boolean isCompleteSolution()
    {
        return isCompleteSolution;
    }

    //initialize the empty cells of a table with random characters
    private void init()
    {
        //pool of usable characters, incremented from start character
        ArrayList<Character> charPool = new ArrayList<>();
        //number of times character can be placed
        HashMap<Character, Integer> charCount = new HashMap<>();
        char c = startChar;
        for(int x = 0; x < dimension; x++)
        {
            charPool.add(c);
            charCount.put(c, dimension);
            c++;
        }

        //find existing symbols and reduce counts
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

        Random rand = new Random();
        //iterate through columns and rows of table and add random symbols
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                int poolIndex;
                if(lsqTable[i][j] == null)
                {
                    //select random index to pull from pool
                    poolIndex = rand.nextInt(charPool.size());
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
        calcFitness();
    }

    public void randomize()
    {
        //clear the table of non-locked symbols
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                if(!lsqTable[i][j].isLocked())
                    lsqTable[i][j] = null;
            }
        }

        //call init again to get a randomized table
        init();
        calcFitness();
    }
    public void calcFitness() 
    {
        int repititions = 0;
        int lowestRowCount = dimension + 1;
        int rowCountStatics = dimension + 1;
        int lowestColumnCount = dimension + 1;
        int columnCountStatics = dimension + 1;

        //counting repitions in each column, and determining column with lowest repitions
        for(int i = 0; i < dimension; i++) 
        {
            HashMap<Character, Integer> repititionCounters = new HashMap<Character, Integer>(dimension);
            for(int j = 0; j < dimension; j++)
                repititionCounters.put(lsqTable[i][j].getCharacter(), 1);
            int localColumnRepitions = 0;
            int localColumnStatics = 0;
            for(int j = 0; j < dimension; j++)
            {
                if(repititionCounters.get(lsqTable[i][j].getCharacter()) < 1)
                {
                    repititions++;
                    localColumnRepitions++;
                }
                else
                   repititionCounters.put(lsqTable[i][j].getCharacter(), 0);
                if(lsqTable[i][j].isLocked())
                    localColumnStatics++; 
            }
            if(localColumnRepitions < lowestColumnCount)
            {
                lowestColumnCount = localColumnRepitions;
                columnCountStatics = localColumnStatics;
                this.lowestColumn = i;
            }
            else if(localColumnRepitions == lowestColumnCount && localColumnStatics < columnCountStatics)
            {
                columnCountStatics = localColumnStatics;
                this.lowestColumn = i;
            }
        }

        //doing the same, but now for the rows
        for(int i = 0; i < dimension; i++) 
        {
            HashMap<Character, Integer> repititionCounters = new HashMap<Character, Integer>(dimension);
            for(int j = 0; j < dimension; j++)
                repititionCounters.put(lsqTable[j][i].getCharacter(), 1);
            int localRowRepitions = 0;
            int localRowStatics = 0;
            for(int j = 0; j < dimension; j++)
            {
                if(repititionCounters.get(lsqTable[j][i].getCharacter()) < 1)
                {
                    repititions++;
                    localRowRepitions++;
                }
                else
                   repititionCounters.put(lsqTable[j][i].getCharacter(), 0);
                if(lsqTable[j][i].isLocked())
                    localRowStatics++; 
            }
            if(localRowRepitions < lowestRowCount)
            {
                lowestRowCount = localRowRepitions;
                rowCountStatics = localRowStatics;
                this.lowestRow = i;
            }
            else if(localRowRepitions == lowestRowCount && localRowStatics < rowCountStatics)
            {
                rowCountStatics = localRowStatics;
                this.lowestRow = i;
            }
        }

        if(repititions != 0)
            this.fitness = (double)1/repititions;
        else
            this.isCompleteSolution = true;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder("\nDimension: " + dimension
                + "\nStart Char: " + startChar
                + "\nLowest Column: " + lowestColumn
                + "\nLowest Row: " + lowestRow
                + "\nFitness: " + fitness);

        for(int j = 0; j < dimension; j++)
        {
            str.append("\n");
            for(int i = 0; i < dimension; i++)
            {
                str.append(lsqTable[i][j].getCharacter());
                str.append("  ");
            }
        }
        return str.toString();
    }

    @Override
    public int compareTo(LSQ o) {
        return Double.compare(o.getFitness(), this.fitness);
    }


}
