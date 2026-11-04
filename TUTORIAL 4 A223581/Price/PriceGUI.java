import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PriceGUI extends JFrame {

    private static final String[] TYPES = {"SmartPhone", "Laptop", "Car"};
    private static final String[] DEFAULT_NAMES = {"Iphone 14 ProMax", "Lenovo Yoga", "BMW"};
    private static final String[] DEFAULT_PRICES = {"5500.00", "6000.00", "200000.00"};
    private static final String[] NAME_LABELS = {"Name:", "Name:", "Brand:"};

    private final JComboBox<String> typeBox = new JComboBox<>(TYPES);
    private final JLabel productNameLabel = new JLabel(NAME_LABELS[0]);
    private final JTextField productNameField = new JTextField(DEFAULT_NAMES[0], 14);
    private final JTextField priceField = new JTextField(DEFAULT_PRICES[0], 10);

    private final JTextField customerField = new JTextField("Sally", 14);
    private final JTextField salaryField = new JTextField("3000.00", 10);

    private final JLabel verdictLabel = new JLabel("No result yet", SwingConstants.CENTER);
    private final JTextArea log = new JTextArea();

    private int lastTypeIndex = 0;
    private int count = 0;

    public PriceGUI() {
        super("Price - Can the Customer Buy It?");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));


        JPanel productPanel = new JPanel(new GridBagLayout());
        productPanel.setBorder(BorderFactory.createTitledBorder("Product"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 0; productPanel.add(new JLabel("Type:"), g);
        g.gridx = 1; productPanel.add(typeBox, g);
        g.gridx = 0; g.gridy = 1; productPanel.add(productNameLabel, g);
        g.gridx = 1; productPanel.add(productNameField, g);
        g.gridx = 0; g.gridy = 2; productPanel.add(new JLabel("Price (RM):"), g);
        g.gridx = 1; productPanel.add(priceField, g);

        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer"));
        g.gridx = 0; g.gridy = 0; customerPanel.add(new JLabel("Name:"), g);
        g.gridx = 1; customerPanel.add(customerField, g);
        g.gridx = 0; g.gridy = 1; customerPanel.add(new JLabel("Salary (RM):"), g);
        g.gridx = 1; customerPanel.add(salaryField, g);

        JPanel forms = new JPanel(new GridLayout(1, 2, 10, 0));
        forms.add(productPanel);
        forms.add(customerPanel);

        JButton checkBtn = new JButton("Check Price");
        JButton demoBtn = new JButton("Run Demo");
        JButton clearBtn = new JButton("Clear");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(checkBtn);
        buttons.add(demoBtn);
        buttons.add(clearBtn);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(forms, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        verdictLabel.setOpaque(true);
        verdictLabel.setFont(verdictLabel.getFont().deriveFont(Font.BOLD, 16f));
        verdictLabel.setBorder(new EmptyBorder(8, 8, 8, 8));
        verdictLabel.setBackground(new Color(235, 235, 235));

        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        log.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(BorderFactory.createTitledBorder("Output"));

        JPanel result = new JPanel(new BorderLayout(0, 8));
        result.add(verdictLabel, BorderLayout.NORTH);
        result.add(scroll, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);
        root.add(result, BorderLayout.CENTER);
        add(root);

        typeBox.addActionListener(e -> onTypeChanged());
        checkBtn.addActionListener(e -> onCheck());
        salaryField.addActionListener(e -> onCheck());
        demoBtn.addActionListener(e -> runDemo());
        clearBtn.addActionListener(e -> {
            log.setText("");
            count = 0;
            setVerdict(null);
        });

        setSize(760, 560);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
    }

    private void onTypeChanged() {
        int idx = typeBox.getSelectedIndex();
        if (productNameField.getText().trim().isEmpty()
                || productNameField.getText().trim().equals(DEFAULT_NAMES[lastTypeIndex])) {
            productNameField.setText(DEFAULT_NAMES[idx]);
        }
        if (priceField.getText().trim().isEmpty()
                || priceField.getText().trim().equals(DEFAULT_PRICES[lastTypeIndex])) {
            priceField.setText(DEFAULT_PRICES[idx]);
        }
        productNameLabel.setText(NAME_LABELS[idx]);
        lastTypeIndex = idx;
    }

    private Price createProduct(String type, String name, double price) {
        switch (type) {
            case "SmartPhone": return new SmartPhone("SmartPhone", name, price);
            case "Laptop":     return new Laptop("Laptop", name, price);
            default:           return new Car(name, price);
        }
    }

    private void onCheck() {
        String productName = productNameField.getText().trim();
        String customerName = customerField.getText().trim();
        if (productName.isEmpty()) {
            showError("Product name must not be empty.");
            productNameField.requestFocusInWindow();
            return;
        }
        if (customerName.isEmpty()) {
            showError("Customer name must not be empty.");
            customerField.requestFocusInWindow();
            return;
        }
        Double price = parseMoney(priceField, "Price");
        if (price == null) return;
        Double salary = parseMoney(salaryField, "Salary");
        if (salary == null) return;

        Price product = createProduct((String) typeBox.getSelectedItem(), productName, price);
        process(product, new Customer(customerName, salary));
    }

    private void runDemo() {
        process(new SmartPhone("SmartPhone", "Iphone 14 ProMax", 5500.00), new Customer("Sally", 3000.00));
        process(new Laptop("Laptop", "Lenovo Yoga", 6000.00), new Customer("Aaron", 8000.00));
        process(new Car("BMW", 200000.00), new Customer("Simon", 10000.00));
    }

    private void process(Price product, Customer customer) {
        String output = capture(product, customer);
        count++;
        log.append(count + ".\n" + output + "\n");
        log.setCaretPosition(log.getDocument().getLength());
        setVerdict(!output.contains("cannot buy"));
    }

    private static String capture(Price product, Customer customer) {
        PrintStream original = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(buf, true);
        System.setOut(capture);
        try {
            product.getPrice(customer);
        } finally {
            capture.flush();
            System.setOut(original);
        }
        return buf.toString().replace("\r\n", "\n");
    }

    private void setVerdict(Boolean canBuy) {
        if (canBuy == null) {
            verdictLabel.setText("No result yet");
            verdictLabel.setBackground(new Color(235, 235, 235));
            verdictLabel.setForeground(Color.DARK_GRAY);
        } else if (canBuy) {
            verdictLabel.setText("CAN BUY");
            verdictLabel.setBackground(new Color(200, 235, 200));
            verdictLabel.setForeground(new Color(20, 100, 20));
        } else {
            verdictLabel.setText("CANNOT BUY");
            verdictLabel.setBackground(new Color(245, 205, 205));
            verdictLabel.setForeground(new Color(150, 20, 20));
        }
    }

    private Double parseMoney(JTextField field, String label) {
        double value;
        try {
            value = Double.parseDouble(field.getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            showError(label + " must be a number.");
            field.requestFocusInWindow();
            return null;
        }
        if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) {
            showError(label + " must be a non-negative number.");
            field.requestFocusInWindow();
            return null;
        }
        return value;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PriceGUI().setVisible(true));
    }
}