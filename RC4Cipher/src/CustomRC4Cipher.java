import java.io.*;
import java.util.*;
import javax.swing.*;
public class RC4Cipher {
    private static final String CHARACTER_SET = "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
            "0123456789!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?~ ";
    private static List<Integer> initializeState(String key) {
        List<Integer> state = new ArrayList<>();
        for (int index = 0; index < CHARACTER_SET.length(); index++) {
            state.add(index);
        }
        int keyLength = key.length();
        int j = 0;
        for (int i = 0; i < CHARACTER_SET.length(); i++) {
            j = (j + state.get(i) + CHARACTER_SET.indexOf(key.charAt(i % keyLength))) % CHARACTER_SET.length();
            Collections.swap(state, i, j);
        }
        return state;
    }
    private static Iterator<Integer> generateKeystream(List<Integer> state) {
        return new Iterator<>() {
            private int i = 0;
            private int j = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Integer next() {
                i = (i + 1) % CHARACTER_SET.length();
                j = (j + state.get(i)) % CHARACTER_SET.length();
                Collections.swap(state, i, j);
                return state.get((state.get(i) + state.get(j)) % CHARACTER_SET.length());
            }
        };
    }

    public static String encrypt(String key, String input) {
        List<Integer> state = initializeState(key);
        Iterator<Integer> keystream = generateKeystream(state);
        StringBuilder output = new StringBuilder();

        System.out.println("Шифрование:");
        for (char ch : input.toCharArray()) {
            if (!CHARACTER_SET.contains(String.valueOf(ch))) {
                output.append(ch);
                continue;
            }
            int keystreamValue = keystream.next();
            int charIndex = CHARACTER_SET.indexOf(ch);
            int encryptedIndex = (charIndex + keystreamValue) % CHARACTER_SET.length();
            char encryptedChar = CHARACTER_SET.charAt(encryptedIndex);
            
            // Выводим действия на каждом шаге шифрования
            System.out.printf("Текущий символ: '%c', Индекс: %d, Генерируемый ключ: %d, Зашифрованный индекс: %d, Зашифрованный символ: '%c'%n", 
                    ch, charIndex, keystreamValue, encryptedIndex, encryptedChar);
            
            output.append(encryptedChar);
        }
        return output.toString();
    }

    public static String decrypt(String key, String encrypted) {
        List<Integer> state = initializeState(key);
        Iterator<Integer> keystream = generateKeystream(state);
        StringBuilder output = new StringBuilder();

        System.out.println("Расшифровка:");
        for (char ch : encrypted.toCharArray()) {
            if (!CHARACTER_SET.contains(String.valueOf(ch))) {
                output.append(ch);
                continue;
            }
            int keystreamValue = keystream.next();
            int charIndex = CHARACTER_SET.indexOf(ch);
            int decryptedIndex = (charIndex - keystreamValue + CHARACTER_SET.length()) % CHARACTER_SET.length();
            char decryptedChar = CHARACTER_SET.charAt(decryptedIndex);
            
            // Выводим действия на каждом шаге расшифровки
            System.out.printf("Текущий символ: '%c', Индекс: %d, Генерируемый ключ: %d, Расшифрованный индекс: %d, Расшифрованный символ: '%c'%n", 
                    ch, charIndex, keystreamValue, decryptedIndex, decryptedChar);
            
            output.append(decryptedChar);
        }
        return output.toString();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Зашифровать текст");
            System.out.println("2. Расшифровать текст");
            System.out.println("3. Зашифровать файл");
            System.out.println("4. Расшифровать файл");
            System.out.print("Выберите опцию: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" ->                     {
                        System.out.print("Введите текст: ");
                        String plaintext = scanner.nextLine();
                        System.out.print("Введите ключ: ");
                        String key = scanner.nextLine();
                        String encrypted = encrypt(key, plaintext);
                        System.out.println("Зашифрованный текст: " + encrypted);
                    }
                case "2" ->                     {
                        System.out.print("Введите зашифрованный текст: ");
                        String encryptedText = scanner.nextLine();
                        System.out.print("Введите ключ: ");
                        String key = scanner.nextLine();
                        String decrypted = decrypt(key, encryptedText);
                        System.out.println("Расшифрованный текст: " + decrypted);
                    }
                case "3" ->                     {
                        JFileChooser fileChooser = new JFileChooser();
                        System.out.println("Выберите файл для шифрования.");
                        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File inputFile = fileChooser.getSelectedFile();
                            System.out.println("Выберите место для сохранения зашифрованного файла.");
                            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                                File outputFile = fileChooser.getSelectedFile();
                                System.out.print("Введите ключ для шифрования: ");
                                String key = scanner.nextLine();
                                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                                        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        writer.write(encrypt(key, line));
                                        writer.newLine();
                                    }
                                } catch (IOException e) {
                                    System.err.println("Ошибка при работе с файлами: " + e.getMessage());
                                }
                                System.out.println("Файл зашифрован и сохранён как " + outputFile.getAbsolutePath());
                            }
                        }                          }
                case "4" ->                     {
                        JFileChooser fileChooser = new JFileChooser();
                        System.out.println("Выберите файл для расшифрования.");
                        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File inputFile = fileChooser.getSelectedFile();
                            System.out.println("Выберите место для сохранения расшифрованного файла.");
                            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                                File outputFile = fileChooser.getSelectedFile();
                                System.out.print("Введите ключ для расшифрования: ");
                                String key = scanner.nextLine();
                                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                                        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        writer.write(decrypt(key, line));
                                        writer.newLine();
                                    }
                                } catch (IOException e) {
                                    System.err.println("Ошибка при работе с файлами: " + e.getMessage());
                                }
                                System.out.println("Файл расшифрован и сохранён как " + outputFile.getAbsolutePath());
                            }
                        }                          }
                default -> System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
            }
        }
    }
}


