import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class EdibleGUI extends JFrame {

    private static final String[] TYPES = {"Animal", "Apple", "Orange"};
    private static final String[] DEFAULT_NAMES = {"Chicken", "Red Apple", "Navel Orange"};
    private static final String[] HIERARCHY = {
            "Animal  \u2192  Edible",
            "Apple  \u2192  Fruit  \u2192  Edible",
            "Orange  \u2192  Fruit  \u2192  Edible"
    };

    private final JTextField personField = new JTextField("Ali", 10);
    private final JComboBox<String> typeBox = new JComboBox<>(TYPES);
    private final JTextField foodField = new JTextField(DEFAULT_NAMES[0], 12);
    private final JLabel hierarchyLabel = new JLabel(HIERARCHY[0]);
    private final JTextArea log = new JTextArea();

    private int lastTypeIndex = 0;
    private int count = 0;

    public EdibleGUI() {
        super("EdibleApp - Polymorphism Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));


        JPanel input = new JPanel(new GridBagLayout());
        input.setBorder(BorderFactory.createTitledBorder("Who is eating what?"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; input.add(new JLabel("Person name:"), g);
        g.gridx = 1; input.add(personField, g);
        g.gridx = 2; input.add(new JLabel("Edible type:"), g);
        g.gridx = 3; input.add(typeBox, g);
        g.gridx = 4; input.add(new JLabel("Food name:"), g);
        g.gridx = 5; input.add(foodField, g);

        g.gridx = 0; g.gridy = 1; g.gridwidth = 6;
        hierarchyLabel.setForeground(new Color(0, 90, 160));
        hierarchyLabel.setFont(hierarchyLabel.getFont().deriveFont(Font.ITALIC));
        JPanel hier = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hier.add(new JLabel("Class hierarchy:  "));
        hier.add(hierarchyLabel);
        input.add(hier, g);

        JButton eatBtn = new JButton("Eat");
        JButton demoBtn = new JButton("Run Demo");
        JButton clearBtn = new JButton("Clear");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(eatBtn);
        buttons.add(demoBtn);
        buttons.add(clearBtn);
        g.gridy = 2;
        input.add(buttons, g);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        log.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        log.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(BorderFactory.createTitledBorder("Output"));

        root.add(input, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        add(root);

        typeBox.addActionListener(e -> onTypeChanged());
        eatBtn.addActionListener(e -> onEat());
        foodField.addActionListener(e -> onEat());
        personField.addActionListener(e -> foodField.requestFocusInWindow());
        demoBtn.addActionListener(e -> runDemo());
        clearBtn.addActionListener(e -> {
            log.setText("");
            count = 0;
        });

        setSize(760, 420);
        setMinimumSize(new Dimension(700, 360));
        setLocationRelativeTo(null);
    }

    private void onTypeChanged() {
        int idx = typeBox.getSelectedIndex();
        String current = foodField.getText().trim();
        if (current.isEmpty() || current.equals(DEFAULT_NAMES[lastTypeIndex])) {
            foodField.setText(DEFAULT_NAMES[idx]);
        }
        hierarchyLabel.setText(HIERARCHY[idx]);
        lastTypeIndex = idx;
    }

    
    private Edible createEdible(String type, String name) {
        switch (type) {
            case "Apple":  return new Apple(name);
            case "Orange": return new Orange(name);
            default:       return new Animal(name);
        }
    }

    private void onEat() {
        String person = personField.getText().trim();
        String food = foodField.getText().trim();
        if (person.isEmpty()) {
            showError("Person name must not be empty.");
            personField.requestFocusInWindow();
            return;
        }
        if (food.isEmpty()) {
            showError("Food name must not be empty.");
            foodField.requestFocusInWindow();
            return;
        }

        Edible edible = createEdible((String) typeBox.getSelectedItem(), food);
        append(new Person(person).eating(edible));
    }

    private void runDemo() {
        Edible[] objects = {
                new Animal("Chicken"),
                new Apple("Red Apple"),
                new Orange("Navel Orange")
        };
        Person p1 = new Person("Ali");
        Person p2 = new Person("Sarah");

        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof Animal) {
                append(p1.eating(objects[i]));
            } else {
                append(p2.eating(objects[i]));
            }
        }
    }

    private void append(String text) {
        count++;
        log.append(count + ".  " + text + "\n\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EdibleGUI().setVisible(true));
    }
}