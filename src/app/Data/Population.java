package app.Data;

import java.util.ArrayList;
import java.util.Collections;



public class Population
{
    private ArrayList<LSQ> populationMembers;
    private LSQ givenLSQ;

    public Population(LSQ givenLSQ, int popSize)
    {
        this.givenLSQ = givenLSQ; 
        initPop(popSize);
    }

    public Population(Population population) 
    {
        this.populationMembers = new ArrayList<LSQ>(population.getPopulationMembers());
    }

    public Population() 
    {
        populationMembers = new ArrayList<LSQ>();
    }

    public int size()
    {
        return populationMembers.size();
    }

    public ArrayList<LSQ> getPopulationMembers() 
    {
        return this.populationMembers;
    }

    public LSQ getMember(int index) 
    {
        return this.populationMembers.get(index);
    }

    public void removeMember(int index) 
    {
        this.populationMembers.remove(index);
    }

    public ArrayList<LSQ> getPopulationMembersSorted() 
    {
        Collections.sort(this.populationMembers);
        return this.populationMembers;
    }

    public void addMember(LSQ member) 
    {
        this.populationMembers.add(member);
    }

    public void addMembers(ArrayList<LSQ> members) 
    {
        this.populationMembers.addAll(members);
    }

    //Take the LSQ, generate random solutions for the given population
    private void initPop(int popSize)
    {
        populationMembers = new ArrayList<>(popSize);
        
        for(int i = 0; i < popSize; i++) 
        {
            LSQ newMember = new LSQ(givenLSQ);
            newMember.randomize();
            populationMembers.add(newMember);
        }
    }
}