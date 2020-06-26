package PackingProblem.Algorithms.DLX;

import PackingProblem.Model.Coordinate;
import PackingProblem.Model.StartPentomino;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class DLXRow {
    private ArrayList<Integer> columns;
    private double value;
    private int amount;
    private int id;

    public DLXRow(ArrayList<Integer> columns) {
        this.columns = columns;
    }

    public ArrayList<Integer> getColumns() {
        return columns;
    }

    public int getColumnAt(int index) {
        return columns.get(index);
    }

    public void setColumns(ArrayList<Integer> columns) {
        this.columns = columns;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean containsColumn(int column) {
        return columns.contains(column);
    }

    public int decreaseAndGetAmount() {
        amount--;
        return amount;
    }

    public void increaseAmount() {
        amount++;
    }
}
