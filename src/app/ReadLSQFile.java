package app;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;

public class ReadLSQFile
{
    //opens a file selection dialog and returns file objected wrapped in an
    //..optional if one was selected
    public static Optional<File> fileSelection()
    {
        JFileChooser fileChooser = new JFileChooser("samplefiles", FileSystemView.getFileSystemView());
        fileChooser.setFileFilter(new FileNameExtensionFilter("LSQ Files", "lsq"));

        int ok = fileChooser.showOpenDialog(null);

        //check if a file was selected and return as an optional
        if(ok == JFileChooser.APPROVE_OPTION)
            return Optional.of(new File(fileChooser.getSelectedFile().getAbsolutePath()));
            //just return an empty optional
        else
            return Optional.empty();
    }

    public static LSQ createLSQFromFile(File file)
    {
        Scanner fileScan = null;
        //try catch to catch filenotfoundexception and exit program
        try
        {
            fileScan = new Scanner(file);
        }
        catch(FileNotFoundException fnf)
        {
            System.out.println(fnf.getMessage());
            System.exit(0);
        }

        char startChar, c;
        int dimension, col, row;

        //scan until dimension line is found
        while(!fileScan.hasNext("DIMENSION:"))
        {
            fileScan.next();
            fileScan.nextLine();
        }

        fileScan.next();
        try
        {
            //assign dimension size
            dimension = fileScan.nextInt();
            //next line should have start char
            fileScan.nextLine();
            String str = fileScan.next();
            //cast int as char (ascii)
            startChar = (char) fileScan.nextInt();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error Reading File");
        }

        //create blank table
        Symbol[][] lsqTable = new Symbol[dimension][dimension];

        try{
            //continue with scanner until EOF
            while(fileScan.hasNextLine())
            {
                //check if the next line starts with int (indicates start of col row indices)
                if(fileScan.hasNextInt())
                {
                    //skip the first int
                    fileScan.next();
                    //cast next int (ascii) as char
                    c = (char) fileScan.nextInt();
                    //get col then row
                    col = fileScan.nextInt();
                    row = fileScan.nextInt();

                    lsqTable[col][row] = new Symbol(c, true);
                }
                //move to next line
                fileScan.nextLine();
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error Reading File");
        }

        LSQ newLSQ = new LSQ(dimension, lsqTable, startChar);
        return newLSQ;
    }
}
