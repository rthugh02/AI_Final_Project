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

    public double getFitness() 
    {
        return fitness;
    }

    //initialize the empty cells of a table with random characters
    public void fillEmptyCells()
    {
        HashMap<Character, Integer> charPool = getCharacterPool();

        Random rand = new Random();
        //iterate through columns and rows of table and add random symbols
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                int poolIndex;
                if(lsqTable[i][j] == null)
                {
                    //create arraylist from charPool keys to pull by index
                    ArrayList<Character> charPoolList = new ArrayList<>(charPool.keySet());
                    //select random index to pull
                    poolIndex = rand.nextInt(charPoolList.size());
                    //get random character and create new symbol
                    char c = charPoolList.get(poolIndex);
                    lsqTable[i][j] = new Symbol(c, false);

                    //decrement currently selected character or remove if only one left
                    if(charPool.get(c) > 1)
                        charPool.put(c, charPool.get(c) - 1);
                    else
                    {
                        charPool.remove(c);
                    }
                }
            }
        }
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

    //get a pool of counts of remaining available characters to fill an incomplete solution
    public HashMap<Character, Integer> getCharacterPool()
    {
        //fill up character pool based on start char
        HashMap<Character, Integer> characterPool = new HashMap<>();
        char c = startChar;
        for(int x = 0; x < dimension; x++)
        {
            characterPool.put(c, dimension);
            c++;
        }

        //subtract existing symbols from character pool
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                if(lsqTable[i][j] != null)
                {
                    Character character = lsqTable[i][j].getCharacter();
                    if(characterPool.containsKey(character))
                    {
                        if(characterPool.get(character) > 1)
                            characterPool.put(character, characterPool.get(character) - 1);
                        else
                            characterPool.remove(character);
                    }
                }
            }
        }

        return characterPool;
    }

    //if the cell is valid, an empty characterpool should be created from it
    public boolean validateTable()
    {
        if(getCharacterPool().isEmpty())
            return true;
        else
            return false;
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
                str.append("\t");
            }
        }
        return str.toString();
    }

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
