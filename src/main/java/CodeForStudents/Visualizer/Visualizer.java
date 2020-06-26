package CodeForStudents.Visualizer;
import java.io.File;
import java.io.IOException;
import java.lang.*;

import CodeForStudents.Model.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javax.imageio.ImageIO;
import java.util.*;

// Parts of the code from: https://www.genuinecoder.com/javafx-3d-tutorial-object-transform-rotation-with-mouse/

public class Visualizer extends Application {
    private int screen_width = 1000;
    private int screen_height = 700;

    private double transparency = 0.0; //Sets the transparancy of the containers

    // Define a scale
    private final double scale = 10;
    private final double distanceBetween = 0.0;// scale/10;
    private double zoomSpeed = scale/10;
    private double rotationSpeed = 0.5;

    //Keep track of the starting point of the mouse
    private double anchorX, anchorY;
    //Keep track of current angle for x and y
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    // Variables for setting the object angles
    // Rotate the object slightly for the starting position
    private final DoubleProperty angleX = new SimpleDoubleProperty(12.5);
    private final DoubleProperty angleY = new SimpleDoubleProperty(-27.5);

    //Light settings
    private double ambientLightIntensity = 0.4;

    private Container container;

    private PerspectiveCamera camera;
    private Group rootGroup;
    private Group objectGroup;
    private Group lightGroup;
    private Group axisGroup;

    @Override
    public void start(Stage stage) throws Exception {
        Parameters params = getParameters();
        List<String> args = params.getRaw();
        String problem = args.get(0);
        String inputFilename = args.get(1);
        String outputFilename = args.get(2);

        // Load and fill the group depending on the problem
        if( problem.equals("pentomino")){
            this.objectGroup = loadPentominoProblem(inputFilename, outputFilename);
        }
        else if(problem.equals("parcel")){
            this.objectGroup = loadParcelProblem(inputFilename, outputFilename);
        }
        else{
            throw new IllegalArgumentException(String.format("No valid problem found. " +
                    "Problem has to be 'parcel' or 'pentomino'. Received: '%s'", problem));
        }

        // Add the container boundaries
        addContainer(objectGroup);

        // Add some lights
        this.lightGroup = new Group();
        AmbientLight ambientLight = new AmbientLight(Color.color(this.ambientLightIntensity, this.ambientLightIntensity,
                this.ambientLightIntensity));
        this.lightGroup.getChildren().add(ambientLight);

        // Add a light behind the camera position
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateZ(-80*this.scale);
        this.lightGroup.getChildren().add(pointLight);

        // Create the root group
        this.rootGroup = new Group();
        this.objectGroup.getChildren().add(this.axisGroup);
        this.rootGroup.getChildren().add(this.objectGroup);
        this.rootGroup.getChildren().add(this.lightGroup);

        // Center everything to the middle of the screen
        this.rootGroup.translateXProperty().set(this.screen_width/2);
        this.rootGroup.translateYProperty().set(this.screen_height/2);

        Scene scene = new Scene(this.rootGroup, this.screen_width, this.screen_height, true);

        // Setup mouse control, only move the container, not the lights
        initMouseControl(this.objectGroup, scene);

        // Setup key control
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                onKeyPressed(keyEvent);
            }
        });

        // Make the camera
        this.camera = new PerspectiveCamera();
        this.camera.setNearClip(0.001);
        this.camera.setFarClip(10000);
        // Move the camera a bit forward
        this.camera.setTranslateZ(50*this.scale);
        scene.setCamera(this.camera);

        // Finish the scene
        scene.setFill(Color.GRAY);
        stage.setScene(scene);
        stage.setTitle("Visualizer");

        // Set static screen dimensions
        stage.setWidth(this.screen_width);
        stage.setHeight(this.screen_height);
        stage.setMinWidth(this.screen_width);
        stage.setMinHeight(this.screen_height);
        stage.setMaxHeight(this.screen_height);
        stage.setMaxWidth(this.screen_width);

        stage.show();

        saveScreenshot(scene);
    }

    private void addContainer(Group group){
        // Add the container boundaries
        double containerIntensity = 0.1;
        double containerTransparency = 0.01;

        // Make the containerBox slightly bigger than the actual size to get rit of flickering textures
        double containerOversize = 0.01*scale;
        Box containerBox = new Box(container.getX_size()*scale + 2*containerOversize,
                container.getY_size()*scale + 2*containerOversize,
                container.getZ_size()*scale + 2*containerOversize);

        this.axisGroup = this.makeAxisGroup(container.getX_size()*scale + 2*containerOversize,
                container.getY_size()*scale + 2*containerOversize,
                container.getZ_size()*scale + 2*containerOversize);

        PhongMaterial containerMaterial = new PhongMaterial();

        containerMaterial.setDiffuseColor(Color.color(containerIntensity, containerIntensity, containerIntensity,
                containerTransparency));
        // No reflections
        containerMaterial.setSpecularColor(Color.color(0.0, 0.0, 0.0, 0.0));

        containerBox.setMaterial(containerMaterial);
        group.getChildren().add(containerBox);
    }

    // Build the Axes

    private Group makeAxisGroup(double x_size, double y_size, double z_size) {
        Group axisGroup = new Group();
        System.out.println("buildAxes()");
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        double bar_width = 4;

        final Box xAxis = new Box(x_size, bar_width, bar_width);
        final Box yAxis = new Box(bar_width, y_size, bar_width);
        final Box zAxis = new Box(bar_width, bar_width, z_size);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        xAxis.setTranslateY(y_size/2);
        xAxis.setTranslateZ(-z_size/2);

        yAxis.setTranslateX(-x_size/2);
        yAxis.setTranslateZ(-z_size/2);

        zAxis.setTranslateX(-x_size/2);
        zAxis.setTranslateY(y_size/2);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        return axisGroup;
    }

    private Group loadParcelProblem(String inputFilename, String outputFilename){
        ParcelProblem parcelProblem = new ParcelProblem();
        // Get the argument and load from file
        parcelProblem.loadFromFile(inputFilename, outputFilename);

        // Create the group to put all the objects in
        Group group = new Group();

        ArrayList<Parcel> elements = parcelProblem.getParcels();

        // Get the container
        this.container = parcelProblem.getContainer();

        //  Center the container with an offset, make sure the center of the container is in the middle
        double offsetX = -0.5*this.scale*this.container.getX_size(); // Not sure why byt 0.5*scale centered the object
        // correctly
        double offsetY = -0.5*this.scale*this.container.getY_size();
        double offsetZ = -0.5*this.scale*this.container.getZ_size();

        // Put the containers in the group
        for (Parcel element: elements) {

            Coordinate location = element.getLocation();
            int x_size = element.getX_size();
            int y_size = element.getY_size();
            int z_size = element.getZ_size();

            // Create material
            PhongMaterial phongMaterial = new PhongMaterial();
            // TODO: make this so that the texture is right for the shape
            int texture_width = 100;
            int texture_height = 100;
            int border_width = texture_width/20;


            phongMaterial.setDiffuseMap(getRandomBoxTexture(texture_width, texture_height, border_width));

            phongMaterial.setDiffuseColor(Color.color(1, 1, 1, 1.0-transparency));
            double specularIntensity = 0.1;
            phongMaterial.setSpecularColor(Color.color(specularIntensity, specularIntensity, specularIntensity));

            // Create the box
            Box box = new Box(x_size*scale- distanceBetween, y_size*scale- distanceBetween, z_size*scale- distanceBetween);

            // Note that the (0,0,0) point of our container is on the bottom top left corner which is different from the
            // coordinate system of javafx. The box is centered on the center, not on the bottom top left corner
            double box_x_location = location.x*scale + offsetX + 0.5*box.getWidth();
            double box_y_location = container.getY_size()*scale - location.y*scale + offsetY - 0.5*box.getHeight();
            double box_z_location = container.getZ_size()*scale - location.z*scale + offsetZ - 0.5*box.getDepth();

            box.setTranslateX(box_x_location);
            box.setTranslateY(box_y_location);
            box.setTranslateZ(box_z_location);

            box.setMaterial(phongMaterial);
            group.getChildren().add(box);

        }
        return group;
    }

    private Group loadPentominoProblem(String inputFilename, String outputFilename){
        PentominoProblem pentominoProblem = new PentominoProblem();
        // Get the argument and load from file
        pentominoProblem.loadFromFile(inputFilename, outputFilename);

        // Create the group to put all the objects in
        Group group = new Group();

        ArrayList<Pentomino> elements = pentominoProblem.getPentominoes();

        // Get the container
        this.container = pentominoProblem.getContainer();

        //  Center the container with an offset, make sure the center of the container is in the middle
        // Boxes are positioned on their center, since we use 1x1x1 cubes we need to offset them by 0.5*scale
        double offsetX = -0.5*scale*container.getX_size()+0.5*scale;
        double offsetY = -0.5*scale*container.getY_size() +0.5*scale;
        double offsetZ = -0.5*scale*container.getZ_size()+0.5*scale;

        // Put the containers in the group
        for (Pentomino element: elements) {

            // Create material
            PhongMaterial phongMaterial = new PhongMaterial();
            int texture_width = 100;
            int texture_height = 100;
            int border_width = texture_width/20;


            phongMaterial.setDiffuseMap(getRandomBoxTexture(texture_width, texture_height, border_width));

            phongMaterial.setDiffuseColor(Color.color(1, 1, 1, 1.0-transparency));
            double specularIntensity = 0.1;

            phongMaterial.setSpecularColor(Color.color(specularIntensity, specularIntensity, specularIntensity));

            Coordinate[] coordinates = element.getCoordinates();
            for (Coordinate coordinate:coordinates) {
                Box box = new Box(scale- distanceBetween, scale- distanceBetween, scale- distanceBetween);

                box.setTranslateX(coordinate.x *scale + offsetX);
                box.setTranslateY(coordinate.y*scale + offsetY);
                box.setTranslateZ(coordinate.z*scale + offsetZ);

                box.setMaterial(phongMaterial);
                group.getChildren().add(box);
            }
        }
        return group;
    }

    private WritableImage getRandomBoxTexture(int width, int height, int borderWidth){
        // Create the image for the texture
        int texture_width = width;
        int texture_height = height;
        int border_width = borderWidth;

        //Random fill color
        Color randomFillColor = Color.color(Math.random(), Math.random(), Math.random());

        //Border color
        Color borderColor = Color.color(0,0,0);

        // Create a graphics contents on the buffered image
        Canvas canvas = new Canvas(texture_width, texture_height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(borderColor);
        gc.fillRect(0,0,texture_width, texture_height);
        gc.setFill(randomFillColor);
        gc.fillRect(border_width, border_width, texture_width-2*border_width, texture_height-2*border_width);

        WritableImage textureImage = new WritableImage(texture_width, texture_height);
        canvas.snapshot(null, textureImage);
        return textureImage;
    }

    private void initMouseControl(Group group, Scene scene) {
        Rotate xRotate;
        Rotate yRotate;
        group.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );
        xRotate.angleProperty().bind(this.angleX);
        yRotate.angleProperty().bind(this.angleY);

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                onMousePressed(mouseEvent);
            }

        });

        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                onMouseDragged(mouseEvent);
            }

        });

        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
                onMouseScrolled(scrollEvent);
            }
        });
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        //Save the start point
        this.anchorX = mouseEvent.getSceneX();
        this.anchorY = mouseEvent.getSceneY();
        //Save current rotation angle
        this.anchorAngleX = this.angleX.get();
        this.anchorAngleY = this.angleY.get();
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        // Rotate the objects
        this.angleX.set(this.anchorAngleX - this.rotationSpeed*(this.anchorY - mouseEvent.getSceneY()));
        this.angleY.set(this.anchorAngleY + this.rotationSpeed*(this.anchorX - mouseEvent.getSceneX()));
    }

    public void onMouseScrolled(ScrollEvent scrollEvent){
        // Move the object closer or farther away: zoom
        this.objectGroup.translateZProperty().set(this.objectGroup.getTranslateZ() - this.zoomSpeed*scrollEvent.getDeltaY());
    }

    public void onKeyPressed(KeyEvent keyEvent){
        double rotationSpeedIncrease = 5;
        double zoomsSpeedIncrease = 10;
        switch (keyEvent.getCode()){
            case LEFT:
            case A:
                this.angleY.set(this.angleY.get() - this.rotationSpeed*rotationSpeedIncrease);
                break;
            case RIGHT:
            case D:
                this.angleY.set(this.angleY.get() + this.rotationSpeed*rotationSpeedIncrease);
                break;
            case UP:
            case W:
                this.angleX.set(this.angleX.get() - this.rotationSpeed*rotationSpeedIncrease);
                break;
            case DOWN:
            case S:
                this.angleX.set(this.angleX.get() + this.rotationSpeed*rotationSpeedIncrease);
                break;

            case PLUS:
            case EQUALS: //Some keyboards return an equals when the plus is pressed
                this.objectGroup.translateZProperty().set(this.objectGroup.getTranslateZ() - this.zoomSpeed*zoomsSpeedIncrease);
                break;
            case MINUS:
                this.objectGroup.translateZProperty().set(this.objectGroup.getTranslateZ() + this.zoomSpeed*zoomsSpeedIncrease);
                break;
        }
    }

    public void saveScreenshot(Scene scene) {
        System.out.println("Saving screenshot...");
        WritableImage image = scene.snapshot(null);
        File file = new File("visualizer-output.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run(String problem, String inputFilename, String outputFilename) {
        launch(problem, inputFilename, outputFilename);
    }

    public static void main(String[] args) {
        String inputFilename = args[0];
        String outputFilename = args[1];

        // Get the problem type
        String[] path = inputFilename.split("\\\\");
        String problem = path[path.length-1].split("_")[0];

        Visualizer visualizer = new Visualizer();
        visualizer.run(problem, inputFilename, outputFilename);
    }
}