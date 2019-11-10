package app.Algorithm;

import app.Data.LSQ;
import app.Data.Population;
import app.Data.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class GA
{
    //variable counting number of generations where highest fitness hasn't changed
    private static int noFitnessChanges = 0;
    private static LSQ bestSolution;
    //Best solution will be added to Statistics.SolutionProgression for each generation
    public static void calcGeneticSolution(Population population)
    {
        //TODO implement Genetic Algorithm based on population
        
        /*
            GA Algorithm set up:
            
            while(!termination_condition)
            {
                - apply mutation operation
                - evaluate population fitness
                - Perform selection process (tournament)
                - Perform crossover operation between parents
                - integrate children into population
                - kill off weakest solutions
            }

            //TODO make sure to call Statistics.updateAggregateData() at end of GA calculation
        */  
          while(noFitnessChanges != 500 || (bestSolution != null && bestSolution.getFitness() != 2))
          {
              //1. apply mutation operator to population
              Random rand = new Random();
              double mutationChance = GASettings.getMutationChance();
              Iterator<LSQ> it = population.getPopulationMembers().iterator();
              ArrayList<LSQ> mutatedMembers = new ArrayList<>();
              while(it.hasNext())
              {
                LSQ solution = it.next();
                double randomVal = rand.nextDouble();
                if(randomVal <= mutationChance)
                {
                    LSQ mutatedSolution = mutation(solution);
                    mutatedMembers.add(mutatedSolution);
                    it.remove();
                }
              }
              population.addMembers(mutatedMembers);

              //2. Selection
          }  

    }

    //crossover that passes on each parent's best(least conflicts) row and column to separate children
    //..and fills the remaining cells with as many of the opposite parent's corresponding cells as possible
    private static ArrayList<LSQ> crossover(LSQ parent1, LSQ parent2)
    {
        ArrayList<LSQ> children = new ArrayList<>();
        LSQ child1 = new LSQ(parent1);
        LSQ child2 = new LSQ(parent1);
        child1.emptyTable();
        child2.emptyTable();

        int p1Col = parent1.getLowestColumn();
        int p1Row = parent1.getLowestRow();
        int p2Col = parent2.getLowestColumn();
        int p2Row = parent2.getLowestRow();

        //i is index that can represent col or row
        for(int i = 0; i < parent1.getDimension(); i++)
        {
            //copy values of parent1's best row to child1
            if(!parent1.getSymbol(i, p1Row).isLocked())
                child1.setSymbol(new Symbol(parent1.getSymbol(i, p1Row)), i, p1Row);

            //copy values of parent2's best row to child2
            if(!parent2.getSymbol(i, p2Row).isLocked())
                child2.setSymbol(new Symbol(parent2.getSymbol(i, p2Row)), i, p2Row);

            //copy values of parent1's best col to child1
            if(!parent1.getSymbol(p1Col, i).isLocked())
                child1.setSymbol(new Symbol(parent1.getSymbol(p1Col, i)), p1Col, i);

            //copy values of parent2's best col to child2
            if(!parent2.getSymbol(p2Col, i).isLocked())
                child2.setSymbol(new Symbol(parent2.getSymbol(p2Col, i)), p2Col, i);
        }

        HashMap<Character, Integer> charPool1 = child1.getCharacterPool();
        HashMap<Character, Integer> charPool2 = child2.getCharacterPool();
        //try to fill empty cells with opposite parent if possible
        for(int i = 0; i < parent1.getDimension(); i++)
        {
            for(int j = 0; j < parent1.getDimension(); j++)
            {
                //for empty cell, if symbol in opposite parent's cell is available, assign it
                if(child1.getSymbol(i, j) == null && charPool1.containsKey(parent2.getSymbol(i, j).getCharacter()))
                {
                    child1.setSymbol(new Symbol(parent2.getSymbol(i, j)), i, j);
                    if(charPool1.get(parent2.getSymbol(i, j).getCharacter()) > 1)
                        {charPool1.put(parent2.getSymbol(i, j).getCharacter(),
                                charPool1.get(parent2.getSymbol(i, j).getCharacter()) - 1);}
                    else
                        charPool1.remove(parent2.getSymbol(i, j).getCharacter());
                }
                //same for other child
                if(child2.getSymbol(i, j) == null && charPool2.containsKey(parent1.getSymbol(i, j).getCharacter()))
                {
                    child2.setSymbol(new Symbol(parent1.getSymbol(i, j)), i, j);
                    if(charPool2.get(parent1.getSymbol(i, j).getCharacter()) > 1)
                       {charPool2.put(parent1.getSymbol(i, j).getCharacter(),
                                charPool2.get(parent1.getSymbol(i, j).getCharacter()) - 1);}
                    else
                        charPool2.remove(parent1.getSymbol(i, j).getCharacter());
                }
            }
        }

        child1.fillEmptyCells();
        child2.fillEmptyCells();

        //return the new children
        children.add(child1);
        children.add(child2);
        return children;
    }

    private static LSQ selectByTournament(Population population, int selectionAmount) 
    {
        Random rand = new Random();
        int randomIndex;
        
        //copy for full removal
        Population copy = new Population(population);
        Population tournament = new Population();

        for(int i = 0; i < selectionAmount; i++) 
        {
            randomIndex = rand.nextInt(copy.size());
            tournament.addMember(copy.getMember(randomIndex));
            copy.removeMember(randomIndex);
        }

        return tournament.getPopulationMembersSorted().get(0);
    }
      
    
    private static LSQ mutation(LSQ original)
    {
    	LSQ mutated = new LSQ(original);
    	int max = original.getDimension();		//uses the dimension to get the range for the random array slot
    	boolean symb1clear = false;		//booleans to make sure that both symbols are not locked
    	boolean symb2clear = false;
    	int symb1col, symb1row, symb2col, symb2row;
    	symb1col = symb1row = symb2col = symb2row = 0;

    	Random rand = new Random();
    	while(!symb1clear)		//randomly picks symbol until it isn't locked
    	{
    		symb1col = rand.nextInt(max);
    		symb1row = rand.nextInt(max);
    		if(!original.getSymbol(symb1col, symb1row).isLocked())
    		{
    			symb1clear = false;
    		}
    	}
    	while(!symb2clear)
    	{
    		symb2col = rand.nextInt(max);
    		symb2row = rand.nextInt(max);
    		if(!original.getSymbol(symb2col, symb2row).isLocked())
    		{
    			symb2clear = true;
    		}
    	}
    		
    		//first replaces the first symbol with the second from the parent
    		mutated.setSymbol(new Symbol(original.getSymbol(symb2col, symb2row)), symb1col, symb1row);
    		
    		//next replaces the second symbol with the first symbol
    		mutated.setSymbol(new Symbol(original.getSymbol(symb1col, symb2row)), symb2col, symb2row);
   
   		return mutated;
    }
}
