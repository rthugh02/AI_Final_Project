package app.UI;

import app.Algorithm.GA;
import app.Algorithm.GASettings;
import app.Algorithm.WOC;
import app.Data.*;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class UIController
{
    @FXML
    private GridPane grid;
    @FXML
    private TextField tfPopSize;
    @FXML
    private TextField tfMutChance;
    @FXML
    private TextField tfElitism;
    @FXML
    private TextField tfTourney;
    @FXML
    private CheckBox cbWOC;
    @FXML
    private Label lblIteration;
    @FXML
    private ToggleGroup tgTrials;
    @FXML
    private RadioMenuItem rmiTrial1;
    @FXML
    private RadioMenuItem rmiTrial5;
    @FXML
    private RadioMenuItem rmiTrial10;
    @FXML
    private RadioMenuItem rmiTrial25;
    @FXML
    private RadioMenuItem rmiTrial50;

    private LSQ lsq;

    @FXML
    private void initialize()
    {
        GASettings.setDefaults();
        initListeners();
    }

    public void onClickOpen()
    {
        Optional<File> opFile = ReadLSQFile.fileSelection();
        if(opFile.isPresent())
        {
            lsq = ReadLSQFile.createLSQFromFile(opFile.get());
            drawGrid(lsq);
            Stage stage = (Stage) grid.getScene().getWindow();
            stage.setTitle("Latin Square Solver - " + opFile.get().getName());
        }
    }

    public void onClickRun()
    {
        if(lsq != null)
         {
             //run set number of times specified via settings
            for(int x = 0; x < GASettings.getNumTrials(); x++)
            {
                Population population = new Population(lsq, GASettings.getPopSize());
                GA.calcGeneticSolution(population);
            }

            drawGAProgression();
         }

        //#####test code#####
      /*  if(lsq != null)
        {
            Population pop = new Population(lsq, GASettings.getPopSize());
            System.out.println(WOC.getWOCSolution(pop));
        }*/
        //###################

    }

    public void onClickDrawBest()
    {
        if(Statistics.getBestSolution() != null)
            drawGrid(Statistics.getBestSolution());
    }

    //initializes grid for n x n square
    private void drawGrid(LSQ lsq)
    {
        int n = lsq.getDimension();
        //remove any current column and row constraints
        while(grid.getColumnConstraints().size() > 0)
            grid.getColumnConstraints().remove(0);
        while(grid.getRowConstraints().size() > 0)
            grid.getRowConstraints().remove(0);

        //add new constraints to grid
        for(int i = 0; i < n; i++)
        {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setFillWidth(true);
            cc.setHgrow(Priority.NEVER);
            cc.setHalignment(HPos.CENTER);
            cc.setPercentWidth((1.0 / (double) n)*100);
            grid.getColumnConstraints().add(cc);
        }
        for(int j = 0; j < n; j++)
        {
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.NEVER);
            rc.setValignment(VPos.CENTER);
            rc.setPercentHeight((1.0 / (double) n)*100);
            grid.getRowConstraints().add(rc);
        }

        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                //pane added along with symbol
                //with style class, provides background with border as inset to show grid lines
                Pane cell = new Pane();
                cell.getStyleClass().add("cell");
                grid.add(cell, i, j, 1, 1);
                Symbol symbol = lsq.getSymbol(i, j);
                Label lblSymbol = new Label(Character.toString(symbol.getCharacter()));
                if(!symbol.isLocked())
                    lblSymbol.getStyleClass().add("symbol");
                else
                    lblSymbol.getStyleClass().add("symbol-locked");
                grid.add(lblSymbol, i, j, 1, 1);
            }
        }
    }

    private void drawGAProgression()
    {
        FadeTransition ft;
        SequentialTransition st = new SequentialTransition();
        for(int i = 1; i < Statistics.getSolutionProgression().size();i++)
        {
            //don't redraw identical solutions
            if(!Statistics.getSolutionProgression().get(i-1).equals(Statistics.getSolutionProgression().get(i)))
            {
                drawGrid(Statistics.getSolutionProgression().get(i));
                //set up instant fade in transition and add to sequential transition
                ft = new FadeTransition(Duration.millis(0.1), grid);
                ft.setFromValue(0.0);
                ft.setToValue(100.0);
                ft.setDelay(Duration.millis(0.1));
                st.getChildren().add(ft);
                //don't fade out on last solution
                if(i != Statistics.getSolutionProgression().size() - 1)
                {
                    //set up delayed instant fade out transition and add to sequential transition
                    ft = new FadeTransition(Duration.millis(0.1), grid);
                    ft.setFromValue(100.0);
                    ft.setToValue(0.0);
                    ft.setDelay(Duration.millis(100));
                    st.getChildren().add(ft);
                }
            }
        }
        st.play();
    }

    //set up and return a text formatter class that will restrict a text field entry to an integer between two bounds
    private TextFormatter<Integer> getIntTextFormatter(int lowerBound, int upperBound, int defaultValue)
    {
        return new TextFormatter<>(new IntegerStringConverter(),
                defaultValue, change ->
                {
                    try
                    {
                        //only allow change if it can be parsed as int and is within bounds
                        int intValue = Integer.parseInt(change.getControlNewText());
                        if(intValue >= lowerBound && intValue <= upperBound)
                            return change;
                        else
                            return null;
                    }
                    catch (NumberFormatException ex)
                    {
                        return null;
                    }
                } );
    }

    //set up and return a text formatter class that will restrict a text field entry to a double between two bounds
    private TextFormatter<Double> getDblTextFormatter(double lowerBound, double upperBound, double defaultValue)
    {
        return new TextFormatter<>(new DoubleStringConverter(), defaultValue,
                change ->
                {
                    try
                    {
                        //only allow change if it can be parsed as double and is within bounds
                        double dblValue = Double.parseDouble(change.getControlNewText());
                        if(dblValue >= lowerBound && dblValue <= upperBound)
                            return change;
                        else
                            return null;
                    }
                    catch (NumberFormatException ex)
                    {
                        return null;
                    }
                } );
    }

    //set up text formatters and listeners for editable ui objects
    private void initListeners()
    {
        TextFormatter<Integer> popTF = getIntTextFormatter(0, 1000, GASettings.getPopSize());
        popTF.valueProperty().addListener((obs, oldValue, newValue) ->
        {
            GASettings.setPopSize(newValue);
            System.out.println("Pop Size changed to: " + GASettings.getPopSize());
        });
        tfPopSize.setTextFormatter(popTF);

        TextFormatter<Double> mutTF = getDblTextFormatter(0.0, 1.0,
                GASettings.getMutationChance());
        mutTF.valueProperty().addListener((obs, oldValue, newValue) ->
        {
            GASettings.setMutationChance(newValue);
            System.out.println("Mutation Chance changed to: " + GASettings.getMutationChance());
        });
        tfMutChance.setTextFormatter(mutTF);
        TextFormatter<Integer> elitismTF = getIntTextFormatter(0, 5, GASettings.getElitism());
        elitismTF.valueProperty().addListener((obs, oldValue, newValue) ->
        {
            GASettings.setElitism(newValue);
            System.out.println("Elitism changed to: " + GASettings.getElitism());
        });
        tfElitism.setTextFormatter(elitismTF);
        TextFormatter<Integer> tourneyTF = getIntTextFormatter(0, 10,
                GASettings.getTourneySelectionNumber());
        tourneyTF.valueProperty().addListener((obs, oldValue, newValue) ->
        {
            GASettings.setTourneySelectionNumber(newValue);
            System.out.println("Tourney Selection Number changed to: " + GASettings.getTourneySelectionNumber());
        });
        tfTourney.setTextFormatter(tourneyTF);

        cbWOC.selectedProperty().addListener((obs, oldValue, newValue) ->
        {
            GASettings.setWisdomOfCrowds(newValue);
            System.out.println("Use Wisdom of Crowds changed to: " + GASettings.isWisdomOfCrowds());
        });

        tgTrials.selectedToggleProperty().addListener((obs, oldValue, newValue) ->
        {
            if(newValue.equals(rmiTrial1))
                GASettings.setNumTrials(1);
            else if(newValue.equals(rmiTrial5))
                GASettings.setNumTrials(5);
            else if(newValue.equals(rmiTrial10))
                GASettings.setNumTrials(10);
            else if(newValue.equals(rmiTrial25))
                GASettings.setNumTrials(25);
            else if(newValue.equals(rmiTrial50))
                GASettings.setNumTrials(50);

            System.out.println("NumTrials changed to: " + GASettings.getNumTrials());
        });
    }

    public void onClickGraph()
    {
        if(Statistics.getSolutionProgression() != null && !Statistics.getSolutionProgression().isEmpty())
        {
            launchGraph();
        }
    }


    private void launchGraph()
    {
        try
        {
            Stage graphWindow = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../../fxml/graph.fxml"));
            graphWindow.setTitle("Graph");
            graphWindow.setScene(new Scene(root, 800, 600));
            graphWindow.show();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
