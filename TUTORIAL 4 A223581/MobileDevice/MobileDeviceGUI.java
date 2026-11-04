import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MobileDeviceGUI extends JFrame {

    
    private static class Entry {
        final String kind;
        final String type;
        final MobileDevice device;

        Entry(String kind, String type, MobileDevice device) {
            this.kind = kind;
            this.type = type;
            this.device = device;
        }
    }

    private static final String BASIC = "Basic Phone";
    private static final String SMART = "Smart Phone";

    private final List<Entry> entries = new ArrayList<>();

    private final JComboBox<String> kindBox = new JComboBox<>(new String[]{BASIC, SMART});
    private final JTextField manufacturerField = new JTextField(10);
    private final JTextField modelField = new JTextField(10);
    private final JLabel typeLabel = new JLabel("Type:");
    private final JTextField typeField = new JTextField("Galaxy", 8);

    private final JTextField stationField = new JTextField(12);
    private final JTextField frequencyField = new JTextField(8);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"#", "Kind", "Manufacturer", "Model", "Type", "Station", "Frequency"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JTextArea details = new JTextArea();

    public MobileDeviceGUI() {
        super("MobileDevice - Inheritance & Interface Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        addPanel.setBorder(BorderFactory.createTitledBorder("Add Device"));
        addPanel.add(new JLabel("Kind:"));
        addPanel.add(kindBox);
        addPanel.add(new JLabel("Manufacturer:"));
        addPanel.add(manufacturerField);
        addPanel.add(new JLabel("Model:"));
        addPanel.add(modelField);
        addPanel.add(typeLabel);
        addPanel.add(typeField);
        typeLabel.setVisible(false);
        typeField.setVisible(false);

        JButton addBtn = new JButton("Add");
        JButton sampleBtn = new JButton("Load Sample");
        JButton removeBtn = new JButton("Remove Selected");
        JButton clearBtn = new JButton("Clear All");
        addPanel.add(addBtn);
        addPanel.add(sampleBtn);
        addPanel.add(removeBtn);
        addPanel.add(clearBtn);

        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(700, 170));

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        radioPanel.setBorder(BorderFactory.createTitledBorder("Radio Tuner (selected device)"));
        radioPanel.add(new JLabel("Station:"));
        radioPanel.add(stationField);
        radioPanel.add(new JLabel("Frequency (MHz):"));
        radioPanel.add(frequencyField);
        JButton radioBtn = new JButton("Set Radio");
        radioPanel.add(radioBtn);

        details.setEditable(false);
        details.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        details.setBorder(new EmptyBorder(6, 8, 6, 8));
        details.setText("Select a device in the table to see its details.");
        JScrollPane detailsScroll = new JScrollPane(details);
        detailsScroll.setBorder(BorderFactory.createTitledBorder("Output"));

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.add(radioPanel, BorderLayout.NORTH);
        bottom.add(detailsScroll, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.add(tableScroll, BorderLayout.NORTH);
        center.add(bottom, BorderLayout.CENTER);

        root.add(addPanel, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        add(root);

        kindBox.addActionListener(e -> {
            boolean smart = SMART.equals(kindBox.getSelectedItem());
            typeLabel.setVisible(smart);
            typeField.setVisible(smart);
            addPanel.revalidate();
            addPanel.repaint();
        });
        addBtn.addActionListener(e -> onAdd());
        modelField.addActionListener(e -> onAdd());
        sampleBtn.addActionListener(e -> loadSample());
        removeBtn.addActionListener(e -> onRemove());
        clearBtn.addActionListener(e -> {
            entries.clear();
            model.setRowCount(0);
            showDetails();
        });
        radioBtn.addActionListener(e -> onSetRadio());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetails();
            }
        });

        setSize(860, 600);
        setMinimumSize(new Dimension(780, 520));
        setLocationRelativeTo(null);
    }


    private void onAdd() {
        String manufacturer = manufacturerField.getText().trim();
        String modelName = modelField.getText().trim();
        boolean smart = SMART.equals(kindBox.getSelectedItem());
        String type = typeField.getText().trim();

        if (manufacturer.isEmpty()) {
            showError("Manufacturer must not be empty.");
            manufacturerField.requestFocusInWindow();
            return;
        }
        if (modelName.isEmpty()) {
            showError("Model must not be empty.");
            modelField.requestFocusInWindow();
            return;
        }
        if (smart && type.isEmpty()) {
            showError("Type must not be empty for a Smart Phone.");
            typeField.requestFocusInWindow();
            return;
        }

        if (smart) {
            addEntry(new Entry(SMART, type, new SmartPhone(manufacturer, modelName, type)));
        } else {
            addEntry(new Entry(BASIC, "-", new BasicPhone(manufacturer, modelName)));
        }
        manufacturerField.setText("");
        modelField.setText("");
        manufacturerField.requestFocusInWindow();
    }

    private void loadSample() {
        SmartPhone galaxy = new SmartPhone("Samsung", "S20", "Galaxy");
        galaxy.setStation("Hot FM");
        galaxy.setFrequency(97.6);

        BasicPhone walkman = new BasicPhone("Sonny Ericson", "Walkman");
        walkman.setStation("Fly FM");
        walkman.setFrequency(109.8);

        addEntry(new Entry(SMART, "Galaxy", galaxy));
        addEntry(new Entry(BASIC, "-", walkman));
    }

    private void onRemove() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Select a device to remove.");
            return;
        }
        entries.remove(row);
        model.removeRow(row);
        renumber();
        showDetails();
    }

    private void onSetRadio() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Select a device in the table first.");
            return;
        }
        String station = stationField.getText().trim();
        if (station.isEmpty()) {
            showError("Station must not be empty.");
            stationField.requestFocusInWindow();
            return;
        }

        double freq;
        try {
            freq = Double.parseDouble(frequencyField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            showError("Frequency must be a number (e.g. 97.6).");
            frequencyField.requestFocusInWindow();
            return;
        }
        if (Double.isNaN(freq) || Double.isInfinite(freq) || freq <= 0) {
            showError("Frequency must be greater than 0.");
            frequencyField.requestFocusInWindow();
            return;
        }

        RadioTuner tuner = entries.get(row).device;
        tuner.setStation(station);
        tuner.setFrequency(freq);

        refreshRow(row);
        showDetails();
    }


    private void addEntry(Entry entry) {
        entries.add(entry);
        model.addRow(new Object[]{entries.size(), entry.kind, entry.device.manufacturer,
                entry.device.model, entry.type, stationText(entry.device), frequencyText(entry.device)});
        int last = model.getRowCount() - 1;
        table.setRowSelectionInterval(last, last);
    }

    private void refreshRow(int row) {
        MobileDevice d = entries.get(row).device;
        model.setValueAt(stationText(d), row, 5);
        model.setValueAt(frequencyText(d), row, 6);
    }

    private void renumber() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
    }

    private static String stationText(MobileDevice d) {
        return d.station == null ? "-" : d.station;
    }

    private static String frequencyText(MobileDevice d) {
        return d.station == null ? "-" : d.frequency + " MHz";
    }

    private void showDetails() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= entries.size()) {
            details.setText("Select a device in the table to see its details.");
            return;
        }
        Entry entry = entries.get(row);
        MobileDevice d = entry.device;

        StringBuilder sb = new StringBuilder();
        sb.append(capturePrintDetails(d));
        if (d.station == null) {
            sb.append("(Radio not set)\n");
        } else {
            sb.append("Station:").append(d.station).append("\n");
            sb.append("Frequency:").append(d.frequency).append("\n");
        }
        details.setText(sb.toString());
        details.setCaretPosition(0);

        stationField.setText(d.station == null ? "" : d.station);
        frequencyField.setText(d.station == null ? "" : String.valueOf(d.frequency));
    }

    private static String capturePrintDetails(MobileDevice d) {
        PrintStream original = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(buf, true);
        System.setOut(capture);
        try {
            d.printDetails();
        } finally {
            capture.flush();
            System.setOut(original);
        }
        return buf.toString().replace("\r\n", "\n");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MobileDeviceGUI().setVisible(true));
    }
}