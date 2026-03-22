import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Клас для зберігання числа та результату підрахунку чергувань бітів.
 */
class BitAnalysisData implements Serializable {
    private static final long serialVersionUID = 1L;
    private int number;
    private int transitions;
    private transient String binaryString; // transient - не зберігаємо рядок у файлі

    public BitAnalysisData(int number, int transitions, String binaryString) {
        this.number = number;
        this.transitions = transitions;
        this.binaryString = binaryString;
    }

    @Override
    public String toString() {
        return String.format("Число: %d | Бінарно: %s | Чергувань: %d", 
                number, (binaryString == null ? "відновлено" : binaryString), transitions);
    }
}

/**
 * Головне вікно з графічним інтерфейсом.
 */
public class SwingBitCounter extends JFrame {
    private JTextField inputField;
    private JTextArea logArea;
    private BitAnalysisData lastResult;
    private static final String FILE_NAME = "bit_data.ser";

    public SwingBitCounter() {
        setTitle("Аналізатор чергувань бітів");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Панель введення
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Введіть число:"));
        inputField = new JTextField(10);
        topPanel.add(inputField);

        // Кнопки
        JButton btnCalc = new JButton("Підрахувати");
        JButton btnSave = new JButton("Зберегти");
        JButton btnLoad = new JButton("Завантажити");
        
        topPanel.add(btnCalc);
        JPanel botPanel = new JPanel();
        botPanel.add(btnSave);
        botPanel.add(btnLoad);

        logArea = new JTextArea();
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(botPanel, BorderLayout.SOUTH);

        // Логіка
        btnCalc.addActionListener(e -> processNumber());
        btnSave.addActionListener(e -> saveAction());
        btnLoad.addActionListener(e -> loadAction());
    }

    private void processNumber() {
        try {
            int num = Integer.parseInt(inputField.getText());
            String bin = Integer.toBinaryString(num);
            
            // Алгоритм підрахунку чергувань
            int count = 0;
            for (int i = 0; i < bin.length() - 1; i++) {
                if (bin.charAt(i) != bin.charAt(i + 1)) {
                    count++;
                }
            }

            lastResult = new BitAnalysisData(num, count, bin);
            logArea.append("РЕЗУЛЬТАТ: " + lastResult + "\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Будь ласка, введіть ціле число!");
        }
    }

    private void saveAction() {
        if (lastResult == null) return;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(lastResult);
            logArea.append("--- Об'єкт серіалізовано у файл ---\n");
        } catch (IOException ex) {
            logArea.append("Помилка збереження: " + ex.getMessage() + "\n");
        }
    }

    private void loadAction() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            lastResult = (BitAnalysisData) ois.readObject();
            logArea.append("ВІДНОВЛЕНО: " + lastResult + "\n");
            logArea.append("(Двійковий рядок null, бо він transient)\n");
        } catch (Exception ex) {
            logArea.append("Помилка завантаження: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingBitCounter().setVisible(true));
    }
}