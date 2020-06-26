package TournamentSuite.Code.TournamentCode;

import TournamentSuite.Code.TournamentCode.Model.*;

import java.io.*;
import java.util.ArrayList;

public class TournamentRanking {

    public static void main(String[] args) {
        File inputFolder = new File(args[0]);
        File outputFolder = new File(args[1]);
        File masterFolder = new File(args[2]);

        ArrayList<ArrayList<Score>> totalScores = new ArrayList<>();
        File[] groupFolders = outputFolder.listFiles();
        for(File groupFolder : groupFolders){
            if(groupFolder.isDirectory()) {
                String groupname = groupFolder.getName();
                ArrayList<Score> scores = new ArrayList<>();
                File[] inputFiles = inputFolder.listFiles();
                for (File inputFile : inputFiles) {
                    // Create file names
                    String inputFilename = inputFile.getName();
                    String outputFilename = inputFilename.replace(".in", ".out");
                    String masterFilename = inputFilename.replace(".in", ".master");

                    File outputFile = new File(groupFolder.getAbsolutePath() + "\\" + outputFilename);
                    File masterFile = new File(masterFolder.getAbsolutePath() + "\\" + masterFilename);

                    // Load data from master files
                    ArrayList<String> masterData = new ArrayList<>();

                    if(masterFile.exists()){
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(masterFile));
                            String st;

                            // Read the file to string
                            while ((st = br.readLine()) != null){
                                masterData.add(st);
                            }
                            System.out.println(String.format("Successfully read from file: %s", outputFile.toString()));
                        } catch (FileNotFoundException e) {
                            System.err.println(String.format("The input file: %s does not exist.", outputFile.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // Set weight and master packing value of the current problem
                    int weight = Integer.valueOf(masterData.get(0).trim());
                    int masterValue = Integer.valueOf(masterData.get(1).trim());

                    if(outputFile.exists()) {
                        String problem = inputFilename.split("_")[0];

                        if(problem.equals("pentomino")){
                            try {
                                PentominoProblem pentominoProblem = new PentominoProblem();
                                pentominoProblem.loadFromFile(inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
                                // Check correctness
                                if(pentominoProblem.checkCorrectness()){
                                    int packingValue = 0;
                                    ArrayList<Pentomino> pentominoSubset = pentominoProblem.getPentominoSubset();
                                    for(Pentomino pentomino: pentominoProblem.getPentominoes()){
                                        Shape shape = pentomino.getShape();
                                        Pentomino subsetElement = null;

                                        // Get the pentomino value form the subset
                                        for (int j = 0; j < pentominoSubset.size(); j++) {
                                            if (pentominoSubset.get(j).getShape() == shape) {
                                                subsetElement = pentominoSubset.get(j);
                                                break;
                                            }
                                        }
                                        packingValue += subsetElement.getValue();
                                    }

                                    // Calculate the score by our presented formula
                                    double score = weight*Math.pow(((double) packingValue) / masterValue, 4);
                                    scores.add(new Score(groupname, inputFilename,packingValue, score));
                                }
                                else{
                                    scores.add(new Score(groupname, inputFilename, 0, 0.0));
                                }
                            }catch (Exception exception){
                                scores.add(new Score(groupname, inputFilename,0, 0.0));
                            }
                        }
                        else if(problem.equals("parcel")){
                            try {
                                ParcelProblem parcelProblem = new ParcelProblem();
                                parcelProblem.loadFromFile(inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
                                // Check correctness
                                if(parcelProblem.checkCorrectness()){
                                    int packingValue = 0;
                                    ArrayList<Parcel> parcelSubset = parcelProblem.getParcelSubset();
                                    for(Parcel parcel: parcelProblem.getParcels()){
                                        Parcel subsetElement = null;

                                        // Get the parcel value form the subset
                                        for(int j=0; j<parcelSubset.size(); j++){
                                            if(parcelSubset.get(j).equals(parcel)){
                                                subsetElement = parcelSubset.get(j);
                                                break;
                                            }
                                        }
                                        packingValue += subsetElement.getValue();
                                    }
                                    // Calculate the score by our presented formula
                                    double score = weight*Math.pow(((double) packingValue) / masterValue, 4);
                                    scores.add(new Score(groupname, inputFilename,packingValue, score));
                                }
                                else{
                                    scores.add(new Score(groupname, inputFilename,0, 0.0));
                                }
                            }catch (Exception exception){
                                scores.add(new Score(groupname, inputFilename, 0,0.0));
                            }
                        }
                        else{
                            throw new IllegalArgumentException(String.format("No valid problem found. " +
                                    "Problem has to be 'parcel' or 'pentomino'. Received: '%s'", problem));
                        }
                    }
                    else {
                        scores.add(new Score(groupname, inputFilename, 0,0.0));
                    }
                }
                totalScores.add(scores);

                // Write file for group specific scores
                File file = new File(groupFolder.getAbsolutePath()+"//" + groupname +"_scores.csv");
                try {
                    FileWriter fw = new FileWriter(file);

                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write("file_name;packing_value;score");
                    bw.newLine();

                    for(Score score: scores){
                        bw.write(score.getFilename()+ ";" + score.getPackingValue() + ";" + Math.round(score.getScoreValue() * 100.0) / 100.0);
                        bw.newLine();
                    }
                    bw.close();
                    fw.close();
                    System.out.println(String.format("Successfully wrote to file: %s", file.toString()));
                } catch (IOException e) {
                    System.out.println(String.format("An error occurred in writing file: %s", file.toString()));
                    e.printStackTrace();
                }
            }
        }

        // Write file for group ranking
        File file = new File(outputFolder.getAbsolutePath()+"//total_scores.csv");
        try {
            FileWriter fw = new FileWriter(file);

            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("group_name;total_score");
            bw.newLine();

            for(ArrayList<Score> scores: totalScores){
                double totalScore = 0;

                for (Score score:scores){
                    totalScore += score.getScoreValue();
                }

                bw.write(scores.get(0).getGroupname() + ";" + Math.round(totalScore * 100.0) / 100.0);
                bw.newLine();
            }
            bw.close();
            fw.close();
            System.out.println(String.format("Successfully wrote to file: %s", file.toString()));
        } catch (IOException e) {
            System.out.println(String.format("An error occurred in writing file: %s", file.toString()));
            e.printStackTrace();
        }
    }
}
