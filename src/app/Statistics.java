package app;

import java.util.ArrayList;

//helper class for keeping track of statistics during running multiple trials
public class Statistics
{
    //number of times the genetic algorithm has been run in succession with no settings change
    private static int iterations;

    private static int minGenerations;
    private static int maxGenerations;
    private static int avgGenerations;
    private static int timesSolutionFound;
    private static double minFitness;
    private static double maxFitness;
    private static double avgFitness;
    private LSQ bestSolution;

    //stores best solution from each generation; used to visualize progression on UI
    private static ArrayList<LSQ> solutionProgression;

    //sets default values for all statistics; should be called on startup or when GA settings change
    public static void reset()
    {
        iterations = 0;
        minGenerations = 0;
        maxGenerations = 0;
        avgGenerations = 0;
        timesSolutionFound = 0;
        minFitness = Double.POSITIVE_INFINITY;
        maxFitness = 0.0;
        avgFitness = 0.0;

        solutionProgression = new ArrayList<>();
    }

    public static int getIterations()
    {
        return iterations;
    }

    public static void setIterations(int iterations)
    {
        Statistics.iterations = iterations;
    }

    public static int getMinGenerations()
    {
        return minGenerations;
    }

    public static void setMinGenerations(int minGenerations)
    {
        Statistics.minGenerations = minGenerations;
    }

    public static int getMaxGenerations()
    {
        return maxGenerations;
    }

    public static void setMaxGenerations(int maxGenerations)
    {
        Statistics.maxGenerations = maxGenerations;
    }

    public static int getAvgGenerations()
    {
        return avgGenerations;
    }

    public static void setAvgGenerations(int avgGenerations)
    {
        Statistics.avgGenerations = avgGenerations;
    }

    public static int getTimesSolutionFound()
    {
        return timesSolutionFound;
    }

    public static void setTimesSolutionFound(int timesSolutionFound)
    {
        Statistics.timesSolutionFound = timesSolutionFound;
    }

    public static double getMinFitness()
    {
        return minFitness;
    }

    public static void setMinFitness(double minFitness)
    {
        Statistics.minFitness = minFitness;
    }

    public static double getMaxFitness()
    {
        return maxFitness;
    }

    public static void setMaxFitness(double maxFitness)
    {
        Statistics.maxFitness = maxFitness;
    }

    public static double getAvgFitness()
    {
        return avgFitness;
    }

    public static void setAvgFitness(double avgFitness)
    {
        Statistics.avgFitness = avgFitness;
    }

    public LSQ getBestSolution()
    {
        return bestSolution;
    }

    public void setBestSolution(LSQ bestSolution)
    {
        this.bestSolution = bestSolution;
    }

    public static ArrayList<LSQ> getSolutionProgression()
    {
        return solutionProgression;
    }

    public static void setSolutionProgression(ArrayList<LSQ> solutionProgression)
    {
        Statistics.solutionProgression = solutionProgression;
    }

    public static void addSolutionToProgression(LSQ solutionEntry) 
    {
        if(Statistics.solutionProgression != null)
            solutionProgression.add(solutionEntry);
    }
}
