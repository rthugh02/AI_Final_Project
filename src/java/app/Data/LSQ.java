package app.Data;

import java.util.*;

public class LSQ implements Comparable<LSQ>
{
    private final int dimension;
    private Symbol[][] lsqTable;
    private final char startChar;

    //-1 indicates lowest row has not been determined, other values indicate index
    private int lowestRow = -1;
    private int lowestColumn = -1;
    private int numConflicts = -1;
    private double fitness = -1;

    public LSQ(int dimension, Symbol[][] lsqTable, char startChar)
    {
        this.dimension = dimension;
        this.lsqTable = lsqTable.clone();
        this.startChar = startChar;
        fillEmptyCells();
    }

    public LSQ(LSQ lsq)
    {
        this.dimension = lsq.getDimension();
        this.startChar = lsq.getStartChar();
        this.lowestColumn = lsq.getLowestColumn();
        this.lowestRow = lsq.getLowestRow();
        this.numConflicts = lsq.getNumConflicts();
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
        if(lsqTable[column][row] != null)
            return new Symbol(lsqTable[column][row]);
        else
            return null;
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

    public int getNumConflicts()
    {
        return numConflicts;
    }

    public double getFitness()
    {
        return fitness;
    }

    //initialize the empty cells of a table with random characters
    public void fillEmptyCells()
    {
        ArrayList<Character> charPool = getCharacterPool();

        Random rand = new Random();
        //iterate through columns and rows of table and add random symbols
        //create arraylist from charPool keys to pull by index
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                if(lsqTable[i][j] == null)
                {
                    //select a random character from the available pool of characters
                    Collections.shuffle(charPool);
                    char c = charPool.remove(0);
                    lsqTable[i][j] = new Symbol(c, false);
                }
            }
        }
        if(!this.validateTable())
            System.out.println("invalid solution created by LSQ.fillEmptyCells()");
        calcFitness();
    }

    public void randomize()
    {
        emptyTable();
        //call fillEmptyCells to get a randomized table
        fillEmptyCells();
    }

    public void calcFitness() 
    {
        int repetitions = 0;
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
            int localColumnRepetitions = 0;
            int localColumnStatics = 0;
            for(int j = 0; j < dimension; j++)
            {
                if(repititionCounters.get(lsqTable[i][j].getCharacter()) < 1)
                {
                    repetitions++;
                    localColumnRepetitions++;
                }
                else
                   repititionCounters.put(lsqTable[i][j].getCharacter(), 0);
                if(lsqTable[i][j].isLocked())
                    localColumnStatics++; 
            }
            if(localColumnRepetitions < lowestColumnCount)
            {
                lowestColumnCount = localColumnRepetitions;
                columnCountStatics = localColumnStatics;
                this.lowestColumn = i;
            }
            else if(localColumnRepetitions == lowestColumnCount && localColumnStatics < columnCountStatics)
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
            int localRowRepetitions = 0;
            int localRowStatics = 0;
            for(int j = 0; j < dimension; j++)
            {
                if(repititionCounters.get(lsqTable[j][i].getCharacter()) < 1)
                {
                    repetitions++;
                    localRowRepetitions++;
                }
                else
                   repititionCounters.put(lsqTable[j][i].getCharacter(), 0);
                if(lsqTable[j][i].isLocked())
                    localRowStatics++; 
            }
            if(localRowRepetitions < lowestRowCount)
            {
                lowestRowCount = localRowRepetitions;
                rowCountStatics = localRowStatics;
                this.lowestRow = i;
            }
            else if(localRowRepetitions == lowestRowCount && localRowStatics < rowCountStatics)
            {
                rowCountStatics = localRowStatics;
                this.lowestRow = i;
            }
        }

        numConflicts = repetitions;
        if(repetitions != 0)
            this.fitness = ( 1.0 / (double)repetitions);
        else
            this.fitness = 2.0;
    }

    //remove all non-locked symbols from the table
    public void emptyTable()
    {
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                if(!lsqTable[i][j].isLocked())
                    lsqTable[i][j] = null;
            }
        }
    }

    //get a pool remaining available characters to fill an incomplete solution
    public ArrayList<Character> getCharacterPool()
    {
        //using startChar, create list of all available characters
        //ex: a 3x3 that starts with A would have A,A,A,B,B,B,C,C,C
        ArrayList<Character> characterPool = new ArrayList<>();
        char c = startChar;
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                characterPool.add(c);
            }
            c++;
        }

        //find already filled cells and remove their characters from the pool
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                if(lsqTable[i][j] != null)
                {
                    characterPool.remove( lsqTable[i][j].getCharacter());
                }
            }
        }
        return characterPool;
    }

    //check if table has no more n of each character, where n is dimension
    public boolean validateTable()
    {
        ArrayList<Character> characterPool = getCharacterPool();
        return characterPool.isEmpty();
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder("\nDimension: " + dimension
                + "\nStart Char: " + startChar
                + "\nLowest Column: " + lowestColumn
                + "\nLowest Row: " + lowestRow
                + "\nNumber of Conflicts: " + numConflicts
                + "\nFitness: " + fitness);

        for(int j = 0; j < dimension; j++)
        {
            str.append("\n");
            for(int i = 0; i < dimension; i++)
            {
                str.append(lsqTable[i][j].getCharacter());
                str.append("\t");
            }
        }
        return str.toString();
    }

    //inverse comparison: sorting results in highest fitness first
    @Override
    public int compareTo(LSQ o) {
        return Double.compare(o.getFitness(), this.fitness);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LSQ lsq = (LSQ) o;

        for(int i = 0;i < this.dimension;i++)
        {
            for(int j = 0;j < this.dimension;j++)
            {
                if(this.lsqTable[i][j].getCharacter() != (lsq.getSymbol(i, j).getCharacter()))
                    return false;
            }
        }

        return getDimension() == lsq.getDimension() &&
                getStartChar() == lsq.getStartChar() &&
                getLowestRow() == lsq.getLowestRow() &&
                getLowestColumn() == lsq.getLowestColumn() &&
                Double.compare(lsq.getFitness(), getFitness()) == 0;
    }

}
