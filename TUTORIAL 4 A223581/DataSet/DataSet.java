public class DataSet implements Measurable {
    private double sum;
    private Measurable max;
    private int count;
    private String name = "DataSet";

    public DataSet() {
        sum = 0;
        count = 0;
        max = null;
    }

    public void add(Measurable x) {
        sum += x.getMeasure();
        if (count == 0 || max.getMeasure() < x.getMeasure()) {
            max = x;
        }
        count++;
    }

    public Measurable getMax() {
        return max;
    }

    public double getAverage() {
        if (count == 0) return 0;
        return sum / count;
    }

    public double getMeasure() {
        return getAverage();
    }

    public String getName() {
        return name;
    }
}