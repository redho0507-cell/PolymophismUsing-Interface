import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DataSetGUI extends JFrame {

    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");

    
    interface MeasurableFactory {
        Measurable create(String name, double value);
    }


    static class DataSetPanel extends JPanel {
        private DataSet dataSet = new DataSet();
        private final List<Measurable> items = new ArrayList<>();

        private final boolean hasNameInput;
        private final MeasurableFactory factory;
        private final Supplier<List<Measurable>> sampleSupplier;

        private final JTextField nameField = new JTextField(12);
        private final JTextField valueField = new JTextField(10);
        private final DefaultTableModel model;
        private final JTable table;

        private final JLabel countLabel = new JLabel("0");
        private final JLabel averageLabel = new JLabel("-");
        private final JLabel maxValueLabel = new JLabel("-");
        private final JLabel maxNameLabel = new JLabel("-");

        DataSetPanel(String nameLabel, String valueLabel, boolean hasNameInput,
                     MeasurableFactory factory, Supplier<List<Measurable>> sampleSupplier) {
            this.hasNameInput = hasNameInput;
            this.factory = factory;
            this.sampleSupplier = sampleSupplier;

            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(12, 12, 12, 12));

            JPanel input = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            input.setBorder(BorderFactory.createTitledBorder("Add Data"));
            if (hasNameInput) {
                input.add(new JLabel(nameLabel + ":"));
                input.add(nameField);
            }
            input.add(new JLabel(valueLabel + ":"));
            input.add(valueField);

            JButton addBtn = new JButton("Add");
            JButton sampleBtn = new JButton("Load Sample");
            JButton resetBtn = new JButton("Reset");
            input.add(addBtn);
            input.add(sampleBtn);
            input.add(resetBtn);

            model = new DefaultTableModel(new Object[]{"#", "Name", valueLabel}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(model);
            table.setRowHeight(24);
            table.getColumnModel().getColumn(0).setMaxWidth(50);

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object value,
                        boolean isSelected, boolean hasFocus, int row, int col) {
                    Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                    boolean isMax = row < items.size() && items.get(row) == dataSet.getMax();
                    if (!isSelected) {
                        c.setBackground(isMax ? new Color(200, 235, 200) : Color.WHITE);
                    }
                    c.setFont(c.getFont().deriveFont(isMax ? Font.BOLD : Font.PLAIN));
                    return c;
                }
            };
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }

            JPanel result = new JPanel(new GridLayout(1, 4, 10, 0));
            result.setBorder(BorderFactory.createTitledBorder("Result"));
            result.add(card("Count", countLabel));
            result.add(card("Average", averageLabel));
            result.add(card("Highest " + valueLabel, maxValueLabel));
            result.add(card("Highest Name", maxNameLabel));

            add(input, BorderLayout.NORTH);
            add(new JScrollPane(table), BorderLayout.CENTER);
            add(result, BorderLayout.SOUTH);

            addBtn.addActionListener(e -> onAdd());
            valueField.addActionListener(e -> onAdd());
            nameField.addActionListener(e -> valueField.requestFocusInWindow());
            sampleBtn.addActionListener(e -> {
                for (Measurable m : sampleSupplier.get()) {
                    addMeasurable(m);
                }
            });
            resetBtn.addActionListener(e -> reset());
        }

        private static JPanel card(String title, JLabel value) {
            JPanel p = new JPanel(new GridLayout(2, 1));
            JLabel t = new JLabel(title, SwingConstants.CENTER);
            t.setForeground(Color.GRAY);
            value.setHorizontalAlignment(SwingConstants.CENTER);
            value.setFont(value.getFont().deriveFont(Font.BOLD, 18f));
            p.add(t);
            p.add(value);
            return p;
        }

        private void onAdd() {
            String name = nameField.getText().trim();
            if (hasNameInput && name.isEmpty()) {
                showError("Name must not be empty.");
                nameField.requestFocusInWindow();
                return;
            }

            double value;
            try {
                value = Double.parseDouble(valueField.getText().trim().replace(',', '.'));
            } catch (NumberFormatException ex) {
                showError("Value must be a number.");
                valueField.requestFocusInWindow();
                return;
            }
            if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) {
                showError("Value must be a non-negative number.");
                valueField.requestFocusInWindow();
                return;
            }

            addMeasurable(factory.create(name, value));
            nameField.setText("");
            valueField.setText("");
            (hasNameInput ? nameField : valueField).requestFocusInWindow();
        }

        private void addMeasurable(Measurable m) {
            dataSet.add(m);
            items.add(m);
            model.addRow(new Object[]{items.size(), m.getName(), DF.format(m.getMeasure())});
            refreshResult();
        }

        private void reset() {
            dataSet = new DataSet();
            items.clear();
            model.setRowCount(0);
            refreshResult();
        }

        private void refreshResult() {
            countLabel.setText(String.valueOf(items.size()));
            if (items.isEmpty()) {
                averageLabel.setText("-");
                maxValueLabel.setText("-");
                maxNameLabel.setText("-");
            } else {
                Measurable max = dataSet.getMax();
                averageLabel.setText(DF.format(dataSet.getAverage()));
                maxValueLabel.setText(DF.format(max.getMeasure()));
                maxNameLabel.setText(max.getName());
            }
            table.repaint();
        }

        private void showError(String msg) {
            JOptionPane.showMessageDialog(this, msg, "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    public DataSetGUI() {
        super("DataSet - Measurable Statistics");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Bank Account", new DataSetPanel(
                "Name", "Balance", false,
                (name, value) -> new BankAccount(value),
                () -> {
                    List<Measurable> l = new ArrayList<>();
                    l.add(new BankAccount(0));
                    l.add(new BankAccount(10000));
                    l.add(new BankAccount(2000));
                    return l;
                }));

        tabs.addTab("Coin", new DataSetPanel(
                "Name", "Value", true,
                (name, value) -> new Coin(value, name),
                () -> {
                    List<Measurable> l = new ArrayList<>();
                    l.add(new Coin(0.25, "Quarter"));
                    l.add(new Coin(0.1, "Dime"));
                    l.add(new Coin(0.05, "Nickel"));
                    return l;
                }));

        tabs.addTab("Student", new DataSetPanel(
                "Name", "Height (cm)", true,
                (name, value) -> new Student(name, value),
                () -> {
                    List<Measurable> l = new ArrayList<>();
                    l.add(new Student("Ali", 157.50));
                    l.add(new Student("Alex", 165.00));
                    l.add(new Student("Maria", 147.00));
                    return l;
                }));

        add(tabs);
        setSize(700, 480);
        setMinimumSize(new Dimension(620, 400));
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DataSetGUI().setVisible(true));
    }
}