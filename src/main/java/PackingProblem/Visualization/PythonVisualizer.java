package PackingProblem.Visualization;

import java.io.IOException;

public class PythonVisualizer {

    public void executePythonScript() throws IOException {
        Process p = Runtime.getRuntime().exec(".\\venv\\Scripts\\python.exe .\\src\\main\\python\\packing_problem_visualizer.py");
    }

}
