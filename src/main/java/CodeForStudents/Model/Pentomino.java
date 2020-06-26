package CodeForStudents.Model;

import java.util.Arrays;

public class Pentomino {
    private Shape shape;
    private Coordinate[] coordinates;
    private double value;

    public Pentomino(Shape shape, double value) {
        this.value = value;
        this.shape = shape;
    }

    public Pentomino(Coordinate[] coordinates){
        this.coordinates = coordinates;
        this.shape = Pentomino.calculateShapeByCoordinates(coordinates);
    }

    public Shape getShape() {
        return this.shape;
    }

    public double getValue() {
        return this.value;
    }

    public Coordinate[] getCoordinates(){
        return this.coordinates;
    }

    public static Shape calculateShapeByCoordinates(Coordinate[] coordinates){
        if(coordinates.length != 5){
            throw new IllegalArgumentException("There are more or less than 5 coordinates.");
        }

        // Sort the coordinates by priority x, y, z
        Arrays.sort(coordinates);

        int [] xRelative = new int[5];
        int [] yRelative = new int[5];
        int [] zRelative = new int[5];
        // Counters for the appearance of 0 for every plane
        int xZeroCounter = 0;
        int yZeroCounter = 0;
        int zZeroCounter = 0;
        // Store the minimum of the relative coordinates of each plane
        int minXRelative = 0;
        int minYRelative = 0;
        int minZRelative = 0;

        for (int coordinateCounter = 0; coordinateCounter<coordinates.length; coordinateCounter++){
            // Calculate the relative coordinates
            xRelative[coordinateCounter] = coordinates[coordinateCounter].x - coordinates[0].x;
            yRelative[coordinateCounter] = coordinates[coordinateCounter].y - coordinates[0].y;
            zRelative[coordinateCounter] = coordinates[coordinateCounter].z - coordinates[0].z;
            if(xRelative[coordinateCounter]==0){
                xZeroCounter += 1;
            }
            if(yRelative[coordinateCounter]==0){
                yZeroCounter += 1;
            }
            if(zRelative[coordinateCounter]==0){
                zZeroCounter += 1;
            }
            if(xRelative[coordinateCounter]<minXRelative){
                minXRelative = xRelative[coordinateCounter];
            }
            if(yRelative[coordinateCounter]<minYRelative){
                minYRelative = yRelative[coordinateCounter];
            }
            if(zRelative[coordinateCounter]<minZRelative){
                minZRelative = zRelative[coordinateCounter];
            }
        }

        // Swap x- or y-plane values with z-plane if x- or y-plane consist only of 0
        if (xZeroCounter == 5){
            xRelative = zRelative;
            minXRelative = minZRelative;
            zZeroCounter = 5;
        }
        if (yZeroCounter == 5){
            yRelative = zRelative;
            minYRelative = minZRelative;
            zZeroCounter = 5;
        }
        // If there are still values in the z-plane that are not 0, the coordinates are not correct
        if(zZeroCounter != 5){
            throw new IllegalArgumentException("Pentomino coordinates are not valid.");
        }

        // If there are negative values in x- and y-plane relocate pentomino in a positive region
        if(minXRelative < 0 || minYRelative < 0){
            for (int coordinateCounter = 0; coordinateCounter<coordinates.length; coordinateCounter++){
                if(minXRelative < 0) {
                    xRelative[coordinateCounter] -= minXRelative;
                }
                if(minYRelative < 0) {
                    yRelative[coordinateCounter] -= minYRelative;
                }
            }
        }

        // Calculate id
        int id = 0;
        for (int coordinateCounter = 0; coordinateCounter<coordinates.length; coordinateCounter++) {
            // Use binary base to get a unique id
            id += (int) Math.pow(2, 5*yRelative[coordinateCounter] + xRelative[coordinateCounter]);
        }

        Shape[] shapes = { Shape.F, Shape.I, Shape.L, Shape.N, Shape.P, Shape.T, Shape.U, Shape.V, Shape.W, Shape.X, Shape.Y, Shape.Z};

        // Shape ids by row
        int[][] shapeIds = {{1250, 2150, 2243, 2273, 2276, 3266, 4322, 6242},
                            {31, 1082401, 17043521},
                            {47, 271, 481, 488, 33827, 67651, 99361, 100418},
                            {110, 236, 391, 451, 33890, 35906, 67681, 68641},
                            {103, 199, 227, 230, 1123, 2147, 3169, 3170},
                            {1249, 2119, 4324, 7234},
                            {167, 229, 3107, 3139},
                            {1063, 4231, 7201, 7300},
                            {1126, 3268, 4291, 6241},
                            {2274},
                            {79, 143, 482, 484, 33889, 35873, 67682, 68674},
                            {1252, 3142, 4321, 6211}
        };

        Shape shape = null;

        // Check if the id fits to a id of a shape
        outerloop:
        for(int rowCounter = 0; rowCounter<shapeIds.length; rowCounter++){
            for(int columnCounter = 0; columnCounter<shapeIds[rowCounter].length; columnCounter++){
                if(shapeIds[rowCounter][columnCounter] == id){
                    shape = shapes[rowCounter];
                    break outerloop;
                }
            }
        }

        // If id does not fit to a id of a shape, the coordinates are invalid
        if(shape != null){
            return shape;
        }
        else{
            throw new IllegalArgumentException("Pentomino coordinates are not valid.");
        }
    }
}
