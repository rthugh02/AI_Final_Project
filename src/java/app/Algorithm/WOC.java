package app.Algorithm;

import app.Data.LSQ;
import app.Data.Population;
import app.Data.Symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class WOC
{
    private static ArrayList<LSQ> bestSolutions;
    private static HashMap<Character, Integer> characterPool;
    private static ArrayList<CellAgreement> cellAgreements;
    private static LSQ wocSolution;

    public static LSQ getWOCSolution(Population population)
    {
        initBestSolutions(population);
        //create clone of one of the solutions
        wocSolution = new LSQ(population.getMember(0));
        //remove all non-locked symbols
        wocSolution.emptyTable();
        //get character pool from empty table
        characterPool = wocSolution.getCharacterPool();
        initCellAgreements();
        fillWOCSolution();
        return wocSolution;
    }

    //initialize bestSolutions, which is a subset of the population with the highest fitness scores
    private static void initBestSolutions(Population population)
    {
        bestSolutions = new ArrayList<>();
        ArrayList<LSQ> sortedPop = new ArrayList<>(population.getPopulationMembersSorted());
        //add percentage of population to bestSolutions
        for(int x = 0; x < population.size() * 0.3 ;x++)
        {
            bestSolutions.add(sortedPop.remove(x));
        }
    }

    //initialize cellAgreements, which keep track of the agreement counts for each symbol for each cell
    private static void initCellAgreements()
    {
        //create and fill a table with a CellAgreement object for each cell
        int dim = bestSolutions.get(0).getDimension();
        //make 2d array to allow lookup by col and row when tallying agreement counts
        CellAgreement[][] cellAgreementsTable = new CellAgreement[dim][dim];
        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {
                //assign CellAgreement if not locked; resulting null values signify locked cells
                if(!bestSolutions.get(0).getSymbol(i, j).isLocked())
                {
                    cellAgreementsTable[i][j] = new CellAgreement(i, j);
                }
            }
        }

        //increment count of each cell of each solution
        ArrayList<LSQ> bestSolutionsCopy = new ArrayList<>(bestSolutions);

        while(!bestSolutionsCopy.isEmpty())
        {
            LSQ lsq = bestSolutionsCopy.remove(0);
            for(int i = 0; i < dim; i++)
            {
                for(int j = 0; j < dim; j++)
                {
                    //if not null, i.e. locked, increment the count for that character in CellAgreement
                    if(cellAgreementsTable[i][j] != null)
                        cellAgreementsTable[i][j].incrementCharacterCount(lsq.getSymbol(i, j).getCharacter());
                }
            }
        }

        //add all entries from table to ArrayList so they can be sorted
        cellAgreements = new ArrayList<>();
        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {
                if(cellAgreementsTable[i][j] != null)
                {
                    cellAgreements.add(new CellAgreement(cellAgreementsTable[i][j]));
                }
            }
        }

        //cellAgreements sorted by whichever character has most agreement in desc order
        Collections.sort(cellAgreements);
        Collections.reverse(cellAgreements);
    }

    //creates the solution
    private static void fillWOCSolution()
    {
        //go through each available cell to be assigned via the ArrayList
        while(!cellAgreements.isEmpty() )
        {
            CellAgreement cellAgreement = cellAgreements.remove(0);
            Character character = cellAgreement.getMostAgreedSymbol().getCharacter();
            //if the character is available assign to solution and decrement characterPool
            if(characterPool.containsKey(character))
            {
                if(characterPool.get(character) > 1)
                    characterPool.put(character, characterPool.get(character) - 1);
                else
                    characterPool.remove(character);

                wocSolution.setSymbol(new Symbol(cellAgreement.getMostAgreedSymbol()),
                        cellAgreement.getColumn(), cellAgreement.getRow());
            }
            //if the highest agreed upon symbol for that cell is unavailable, remove the character and add
            //..the cellAgreement back to the ArrayList, where it will have a new highest agreed upon
            //..symbol and can be sorted into a new position
            else
            {
                if(cellAgreement.getAgreementCounts().size() > 1)
                {
                    cellAgreement.removeMostAgreedCharacter();
                    cellAgreements.add(new CellAgreement(cellAgreement));
                    Collections.sort(cellAgreements);
                    Collections.reverse(cellAgreements);
                }
            }
        }
        //try to fill any empty cells
        wocSolution.fillEmptyCells();

    }
}
