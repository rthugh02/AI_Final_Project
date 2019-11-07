package app;

import java.util.ArrayList;

public class GA
{
    
    public static LSQ calcGeneticSolution(Population population) 
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
        */
        

        return null;     
    }

    //crossover that passes on each parent's best(least conflicts) row and column to separate children
    //..and fills the remaining cells with the opposite parent's corresponding cells
    public static ArrayList<LSQ> crossover(LSQ parent1, LSQ parent2)
    {
        ArrayList<LSQ> children = new ArrayList<>();
        //since remaining cells are filled from opposite parent, just start with clone of opposite parent
        LSQ child1 = new LSQ(parent2);
        LSQ child2 = new LSQ(parent1);

        int p1Col = parent1.getLowestColumn();
        int p1Row = parent1.getLowestRow();
        int p2Col = parent2.getLowestColumn();
        int p2Row = parent2.getLowestRow();

        //i is index that can represent col or row
        for(int i = 0; i < parent1.getDimension(); i++)
        {
            //copy values of parent1's best row to child1
            if(!child1.getSymbol(i, p1Row).isLocked())
                child1.setSymbol(new Symbol(parent1.getSymbol(i, p1Row)), i, p1Row);

            //copy values of parent2's best row to child2
            if(!child2.getSymbol(i, p2Row).isLocked())
                child2.setSymbol(new Symbol(parent2.getSymbol(i, p2Row)), i, p2Row);

            //copy values of parent1's best col to child1
            if(!child1.getSymbol(p1Col, i).isLocked())
                child1.setSymbol(new Symbol(parent1.getSymbol(p1Col, i)), p1Col, i);

            //copy values of parent2's best col to child2
            if(!child2.getSymbol(p2Col, i).isLocked())
                child2.setSymbol(new Symbol(parent2.getSymbol(p2Col, i)), p2Col, i);
        }
        //recalculate new fitness
        child1.calcFitness();
        child2.calcFitness();

        //return the new children
        children.add(child1);
        children.add(child2);
        return children;
    }
}