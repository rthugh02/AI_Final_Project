package app;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
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
        //###DEBUG CODE FOR QUICK FILE SELECTION###
        //Optional<File> opFile = Optional.of(new File("samplefiles/lsq10.lsq"));
        //###END DEBUG CODE###
        if(opFile.isPresent())
        {
            lsq = ReadLSQFile.createLSQFromFile(opFile.get());
            drawGrid(lsq);
        }
    }

    public void onClickRun()
    {
        if(lsq != null)
         {
            lsq.randomize();
            drawGrid(lsq);
         }
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
    }
}
