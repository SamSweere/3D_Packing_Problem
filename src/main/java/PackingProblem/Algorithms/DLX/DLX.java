package PackingProblem.Algorithms.DLX;

import PackingProblem.Algorithms.IPentominoSolver;
import PackingProblem.Model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DLX implements IPentominoSolver {
    private Container container;
    private double currentBestValue;
    private ArrayList<Pentomino> currentBestSolution;

    private int bestPossibleAmount;

    private long latestEndTime;
    boolean returnFirstSolution;
    boolean solved;

    private final long maxSecondsToRun;

    private double upper_limit_value_pent = -1;

    public DLX(long maxSecondsToRun, boolean returnFirstSolution) {
        this.maxSecondsToRun = maxSecondsToRun;
        this.returnFirstSolution = returnFirstSolution;
    }

    public double getUpper_limit_value_pent() {
        return upper_limit_value_pent;
    }

    @Override
    public void solve(PentominoProblem pentominoProblem) {

        double max_val = 0;
        for(Pentomino pent:pentominoProblem.getPentominos()){
            double pent_val = pent.getValue();
            if(pent_val > max_val){
                max_val = pent_val;
            }
        }

        double max_dens = max_val/5.0;

        this.upper_limit_value_pent = pentominoProblem.getContainer().getVolume()*max_dens;

        long startTime = System.nanoTime();
        latestEndTime = startTime + (maxSecondsToRun - 1) * 1000000000L;

        currentBestValue = Double.MIN_VALUE;
        currentBestSolution = new ArrayList<>();

        container = pentominoProblem.getContainer();

        bestPossibleAmount = container.getVolume() / 5;
        ArrayList<StartPentomino> pentominoSubset = pentominoProblem.getPentominoSubset();

        pentominoSubset.sort(Collections.reverseOrder());

        // 5*Coordinate, Value, Amount, StartPentominoID
        ArrayList<DLXRow> rows = new ArrayList<>();
        Set<Integer> columns = new HashSet<>();

        for (int i = 0; i < container.getVolume(); i++) {
            columns.add(i);
        }

        for (int i = 0; i < container.getX_size(); i++) {
            for (int j = 0; j < container.getY_size(); j++) {
                for (int k = 0; k < container.getZ_size(); k++) {
                    Coordinate anchor = new Coordinate(i, j, k);
                    for (int l = 0; l < pentominoSubset.size(); l++) {
                        StartPentomino startPentomino = pentominoSubset.get(l);
                        ArrayList<Coordinate[]> possibleCoordinates = Pentomino.calculatePossibleCoordinates(anchor, startPentomino.getShape(), container);
                        for (Coordinate[] possibleCoordinateArray : possibleCoordinates) {
                            if (System.nanoTime() > latestEndTime || solved) {
                                return;
                            }

                            DLXRow row = new DLXRow(Arrays.stream(possibleCoordinateArray).parallel()
                                    .map(c -> c.x * container.getY_size() * container.getZ_size() + c.y * container.getZ_size() + c.z)
                                    .collect(Collectors.toCollection(ArrayList::new)));
                            row.setId(l);
                            row.setAmount(startPentomino.getAmount());
                            row.setValue(startPentomino.getValue());
                            rows.add(row);
                        }
                    }
                }
            }
        }

        rows = rows.parallelStream().filter(distinctByKeys(DLXRow::getColumns, DLXRow::getId)).collect(Collectors.toCollection(ArrayList::new));

        long timeElapsed = (System.nanoTime() - startTime) / 1000000;
        //System.out.println("Matrix initialized! Duration: " + timeElapsed + "ms. Size: " + rows.size() + "x" + columns.size());
        //System.out.println("Best Possible Solution: " + bestPossibleAmount * 5 + "/" + container.getVolume());
        dancingLinks(columns, rows, new ArrayList<>(), 0.0);

        pentominoProblem.setPentominos(currentBestSolution);
        pentominoProblem.setTotalValue(currentBestValue);
    }

    private ArrayList<Pentomino> dancingLinks(Set<Integer> columns, ArrayList<DLXRow> rows, ArrayList<Pentomino> solutionSet, Double value) {
        if (returnFirstSolution && currentBestSolution.size() == bestPossibleAmount) {
            solved = true;
        }

        if (solved || System.nanoTime() > latestEndTime) {
            return solutionSet;
        }

        HashSet<Integer> columnsToRemove = new HashSet<>();

        int minCount = Integer.MAX_VALUE;
        int nextCol = 0;
        boolean foundColumn = false;
        for (int i : columns) {
            int currentCount = (int) rows.stream().parallel().filter(e -> e.containsColumn(i)).count();
            if (currentCount == 0) columnsToRemove.add(i);
            if (currentCount < minCount && currentCount > 0) {
                nextCol = i;
                minCount = currentCount;
                foundColumn = true;
            }
        }

        if (!foundColumn) return solutionSet;

        columns.removeAll(columnsToRemove);

        int finalNextCol = nextCol;
        for (DLXRow row : rows.stream().filter(e -> e.containsColumn(finalNextCol)).collect(Collectors.toCollection(ArrayList::new))) {

            Coordinate[] pentominoCoordinates = new Coordinate[5];
            for (int i = 0; i < 5; i++) {
                pentominoCoordinates[i] = getCoordinateFromColumn(row.getColumnAt(i), container);
            }
            Pentomino solutionPentomino = new Pentomino(pentominoCoordinates);
            solutionPentomino.setValue(row.getValue());
            solutionSet.add(solutionPentomino);
            value += row.getValue();

            if (value > currentBestValue) {
                currentBestSolution = new ArrayList<>(solutionSet);
                currentBestValue = value;
                //System.out.println("New best solution found with a value of " + currentBestValue);
            }

            Set<DLXRow> rowsToRemove = new HashSet<>();
            Set<DLXRow> rowsAmountReduced = new HashSet<>();
            Set<DLXRow> rowsAmountTooLow = new HashSet<>();

            for (DLXRow anotherRow : rows) {
                if (anotherRow.getId() == row.getId()) {
                    int newAmount = anotherRow.decreaseAndGetAmount();
                    rowsAmountReduced.add(anotherRow);
                    if (newAmount == 0) {
                        rowsAmountTooLow.add(anotherRow);
                    }
                }
            }

            for (int columnToRemove : row.getColumns()) {
                rows.stream().filter(e -> e.containsColumn(columnToRemove)).collect(Collectors.toCollection(() -> rowsToRemove));
                columns.removeIf(e -> e.equals(columnToRemove));
            }
            rows.removeAll(rowsToRemove);
            rows.removeAll(rowsAmountTooLow);

            solutionSet = dancingLinks(columns, rows, solutionSet, value);

            solutionSet.remove(solutionPentomino);
            value -= row.getValue();
            rows.addAll(rowsToRemove);
            columns.addAll(row.getColumns());

            for (DLXRow rowAmountReduced : rowsAmountReduced) {
                rowAmountReduced.increaseAmount();
            }
            for (DLXRow rowAmountTooLow : rowsAmountTooLow) {
                rowAmountTooLow.increaseAmount();
                rows.add(rowAmountTooLow);
            }
        }
        columns.addAll(columnsToRemove);

        return solutionSet;
    }

    private Coordinate getCoordinateFromColumn(int column, Container container) {
        int zDirection = column % container.getZ_size();
        int yDirection = (column / container.getZ_size()) % container.getY_size();
        int xDirection = column / (container.getY_size() * container.getZ_size());

        return new Coordinate(xDirection, yDirection, zDirection);
    }

    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }


}
