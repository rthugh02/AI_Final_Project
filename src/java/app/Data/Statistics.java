package app.Data;

import javafx.scene.chart.XYChart;

import java.text.DecimalFormat;
import java.util.ArrayList;

//helper class for keeping track of statistics during running multiple trials
public class Statistics
{

    //##########Aggregate data over multiple trial runs/iterations############
    //number of times the genetic algorithm has been run in succession with no settings change
    private static int iterations;
    private static int minGenerations;
    private static int maxGenerations;
    private static int avgGenerations;
    private static int timesSolutionFound;
    private static double minFitness;
    private static double maxFitness;
    private static double avgFitness;
    private static int minConflicts;
    private static int maxConflicts;
    private static int avgConflicts;
    private static double stdDev;
    private static LSQ aggregateBestSolution;
    private static ArrayList<LSQ> solutionHistory;
    private static int totalGenerations;
    //########################################################################

    //##########data for the current run##############
    private static LSQ currentSolution;
    private static int currentGenerations;
    //stores best solution from each generation for one iteration; used to visualize progression on UI
    private static ArrayList<LSQ> solutionProgression;
    //################################################

    //##########graph data#############################
    //series data stored here for comparison on graph
    //graph results can be saved to either A or B and compared
    private static XYChart.Series<Integer, Integer> seriesA;
    private static XYChart.Series<Integer, Integer> seriesB;
    //####################################################

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
        maxConflicts = 0;
        minConflicts = 0;
        avgConflicts = 0;
        stdDev = 0.0;
        currentSolution = null;
        currentGenerations = 0;
        aggregateBestSolution = null;
        solutionProgression = new ArrayList<>();
        solutionHistory = new ArrayList<>();
        totalGenerations = 0;
    }

    public static void resetProgression()
    {
        solutionProgression = new ArrayList<>();
    }

    public static int getIterations()
    {
        return iterations;
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

    public static double getStdDev()
    {
        return stdDev;
    }

    public static void setStdDev(double stdDev)
    {
        Statistics.stdDev = stdDev;
    }

    public static LSQ getCurrentSolution()
    {
        return currentSolution;
    }

    public static void setCurrentSolution(LSQ lsq)
    {
        Statistics.currentSolution = new LSQ(lsq);
    }

    public static LSQ getAggregateBestSolution()
    {
        return aggregateBestSolution;
    }

    public static void setAggregateBestSolution(LSQ lsq)
    {
        Statistics.aggregateBestSolution = new LSQ(lsq);
    }

    public static ArrayList<LSQ> getSolutionProgression()
    {
        return solutionProgression;
    }

    public static void setSolutionProgression(ArrayList<LSQ> solutionProgression)
    {
        Statistics.solutionProgression = new ArrayList<>(solutionProgression);
    }

    public static void addSolutionToProgression(LSQ solutionEntry) 
    {
        if(Statistics.solutionProgression != null)
            solutionProgression.add(solutionEntry);
    }

    public static XYChart.Series<Integer, Integer> getSeriesA()
    {
        return seriesA;
    }

    public static void setSeriesA(XYChart.Series<Integer, Integer> series)
    {
        Statistics.seriesA = new XYChart.Series<>();
        Statistics.seriesA.setName(series.getName());
        for(XYChart.Data<Integer, Integer> data: series.getData())
        {
            XYChart.Data<Integer, Integer> d = new XYChart.Data<>();
            d.setXValue(data.getXValue());
            d.setYValue(data.getYValue());
            Statistics.seriesA.getData().add(d);
        }
    }

    public static XYChart.Series<Integer, Integer> getSeriesB()
    {
        return seriesB;
    }

    public static void setSeriesB(XYChart.Series<Integer, Integer> series)
    {
        Statistics.seriesB = new XYChart.Series<>();
        Statistics.seriesB.setName(series.getName());
        for(XYChart.Data<Integer, Integer> data: series.getData())
        {
            XYChart.Data<Integer, Integer> d = new XYChart.Data<>();
            d.setXValue(data.getXValue());
            d.setYValue(data.getYValue());
            Statistics.seriesB.getData().add(d);
        }
    }

    //adds new solution from a GA to aggregate data and recalculates statistics
    public static void updateAggregateData(LSQ solution, int numGenerations)
    {
        Statistics.iterations++;
        Statistics.currentSolution = new LSQ(solution);
        currentGenerations = numGenerations;
        totalGenerations += numGenerations;
        Statistics.solutionHistory.add(solution);
        if(Double.compare(solution.getFitness(), 2.0) == 0)
            Statistics.timesSolutionFound++;

        //update min/max values if applicable
        if(solution.getFitness() < minFitness)
        {
            Statistics.minFitness = solution.getFitness();
            aggregateBestSolution = new LSQ(solution);
        }
        if(solution.getFitness() > maxFitness)
            Statistics.maxFitness = solution.getFitness();
        if(numGenerations < minGenerations || minGenerations == 0)
            Statistics.minGenerations = numGenerations;
        if(numGenerations > maxGenerations)
            Statistics.maxGenerations = numGenerations;
        if(solution.getNumConflicts() < minConflicts || minConflicts == 0)
            Statistics.minConflicts = solution.getNumConflicts();
        if(solution.getNumConflicts() > maxConflicts)
            Statistics.maxConflicts = solution.getNumConflicts();
        //update averages
        double totalFitness = 0.0;
        double totalConflicts = 0.0;
        for(LSQ lsq: solutionHistory)
        {
            totalFitness += lsq.getFitness();
            totalConflicts += lsq.getNumConflicts();
        }
        Statistics.avgFitness = totalFitness / (double) Statistics.solutionHistory.size();
        Statistics.avgGenerations = (int) ((double) totalGenerations / (double) Statistics.solutionHistory.size());
        Statistics.avgConflicts = (int) (totalConflicts / (double) Statistics.solutionHistory.size());

        //calculate standard deviation
        double totalDeviation = 0.0;
        for(LSQ lsq: solutionHistory)
        {
            totalDeviation += Math.sqrt(Math.abs(lsq.getFitness() - avgFitness));
        }
        stdDev = Math.sqrt(totalDeviation / (double) iterations);
    }

    public static String statsAsString()
    {
        DecimalFormat df = new DecimalFormat("0.##");
        return "CURRENT SOLUTION: "
                + "\n\tTotal Number of Conflicts: " + currentSolution.getNumConflicts()
                + "\n\tFitness: " + df.format(currentSolution.getFitness())
                + "\n\tNumber of Generations: " + currentGenerations
                + "\n\nAGGREGATE DATA:"
                + "\n\tIterations: " + iterations
                + "\n\tMinimum Number of Conflicts: " + minConflicts
                + "\n\tMaximum Number of Conflicts: " + maxConflicts
                + "\n\tAverage Number of Conflicts: " + avgConflicts
                + "\n\tMinimum Fitness " + df.format(minFitness)
                + "\n\tMaximum Fitness " + df.format(maxFitness)
                + "\n\tAverage Fitness " + df.format(avgFitness)
                + "\n\tFitness Standard Deviation: " + stdDev
                + "\n\tMinimum Number of Generations " + minGenerations
                + "\n\tMaximum Number of Generations " + maxGenerations
                + "\n\tAverage Number of Generations " + avgGenerations
                ;
    }
}
