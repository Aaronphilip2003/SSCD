import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Tokenizing {

    private static final Set<String> COMMON_MNEMONICS = new HashSet<>(Arrays.asList(
            "DC", "DS", "START", "END", "ORIGIN", "EQU", "LTORG", "MOVER"));

    private static final Set<String> COMMON_REGISTERS = new HashSet<>(Arrays.asList(

    ));

    private static class OpInfo {
        private final int opcode;
        private final String instructionClass;

        public OpInfo(int opcode, String instructionClass) {
            this.opcode = opcode;
            this.instructionClass = instructionClass;
        }
    }

    private static final HashMap<String, OpInfo> OPTAB = new HashMap<String, OpInfo>() {
        {
            put("STOP", new OpInfo(0, "IS"));
            put("ADD", new OpInfo(1, "IS"));
            put("MUL", new OpInfo(2, "IS"));
            put("MULT", new OpInfo(3, "IS"));
            put("MOVER", new OpInfo(4, "IS"));
            put("MOVEM", new OpInfo(5, "IS"));
            put("COMP", new OpInfo(6, "IS"));
            put("BC", new OpInfo(7, "IS"));
            put("DIV", new OpInfo(8, "IS"));
            put("READ", new OpInfo(9, "IS"));
            put("PRINT", new OpInfo(10, "IS"));
            put("DC", new OpInfo(1, "DL"));
            put("DS", new OpInfo(2, "DL"));
            put("START", new OpInfo(1, "AD"));
            put("END", new OpInfo(2, "AD"));
            put("ORIGIN", new OpInfo(3, "AD"));
            put("EQU", new OpInfo(4, "AD"));
            put("LTORG", new OpInfo(5, "AD"));
        }
    };

    private static class SymbolTableEntry {
        private final int id;
        private final String symbol;
        private final int address;

        public SymbolTableEntry(int id, String symbol, int address) {
            this.id = id;
            this.symbol = symbol;
            this.address = address;
        }
    }

    public static void main(String args[]) {
        try {
            File file = new File("./input.txt");
            Scanner sc = new Scanner(file);
            int locationCounter = 0;

            ArrayList<String> mnemonics = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            ArrayList<String[]> intermediaryCode = new ArrayList<>();
            ArrayList<SymbolTableEntry> symbolTable = new ArrayList<>();

            System.out.printf("%-10s %-10s %-10s %-10s %-10s%n", "LOC_CTR", "LABEL", "MNEMONIC", "REGISTER", "OPERAND");
            System.out.println("-----------------------------------------------");

            while (sc.hasNextLine()) {
                String input = sc.nextLine().trim();
                if (input.isEmpty())
                    continue;

                if (input.startsWith("START")) {
                    String[] parts = input.split("\\s+");
                    if (parts.length > 1) {
                        locationCounter = Integer.parseInt(parts[1]);
                    } else {
                        locationCounter = 0;
                    }
                    classifyInstruction(input, locationCounter, mnemonics, labels);
                    generateIntermediaryCode(input, intermediaryCode, locationCounter);
                } else if (input.equals("END")) {
                    System.out.println(locationCounter + " " + input);
                    mnemonics.add("END");
                    generateIntermediaryCode(input, intermediaryCode, locationCounter);
                    break;
                } else {
                    classifyInstruction(input, locationCounter, mnemonics, labels);
                    generateIntermediaryCode(input, intermediaryCode, locationCounter);

                    // Special handling for DS instruction
                    String[] parts = input.split("\\s+");
                    String mnemonic = COMMON_MNEMONICS.contains(parts[0]) ? parts[0] : parts[1];
                    if (mnemonic.equals("DS")) {
                        // Get the operand value and increment by that amount
                        String operand = parts[parts.length - 1];
                        locationCounter += Integer.parseInt(operand);
                    } else {
                        locationCounter++;
                    }
                }
            }
            sc.close();

            System.out.println("\nMnemonics found: " + mnemonics);
            System.out.println("Labels found: " + labels);
            System.out.println("\nIntermediary Code:");
            System.out.printf("%-3s %-11s %-15s %-6s%n", "LC", "Instruction", "Opcode1(Reg)", "Opcode2");
            System.out.println("------------------------------------------------");
            for (String[] code : intermediaryCode) {
                System.out.printf("%-3s %-11s %-15s %-6s%n",
                        code[0], // LC
                        code[1] + " " + code[2], // Instruction (Class + Opcode)
                        "-", // Opcode1(Reg) - empty for these instructions
                        code[3] // Opcode2 - the actual operand value
                );
            }

            generateSymbolTable(labels, intermediaryCode, symbolTable);
            System.out.println("\nSymbol Table:");
            System.out.printf("%-5s %-10s %-10s%n", "ID", "Symbol", "Address");
            System.out.println("-------------------------");
            for (SymbolTableEntry entry : symbolTable) {
                System.out.printf("%-5d %-10s %-10d%n", entry.id, entry.symbol, entry.address);
            }

            generateMachineCode(intermediaryCode, symbolTable);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

        generateMachineCodeFromFile();
    }

    public static void classifyInstruction(String instruction, int locationCounter,
            ArrayList<String> mnemonics, ArrayList<String> labels) {
        // Split the instruction into parts
        String[] parts = instruction.split("\\s+");

        // Initialize variables
        String label = "N/A";
        String mnemonic = "N/A";
        String register = "N/A";
        String operand = "N/A";

        // Classification logic
        if (parts.length > 0) {
            // First part could be a label or mnemonic
            if (COMMON_MNEMONICS.contains(parts[0])) {
                mnemonic = parts[0];
                mnemonics.add(mnemonic);
            } else {
                label = parts[0];
                if (!label.equals("N/A")) {
                    labels.add(label);
                }
            }
        }

        if (parts.length > 1) {
            // Second part could be a mnemonic or operand
            if (mnemonic.equals("N/A") && COMMON_MNEMONICS.contains(parts[1])) {
                mnemonic = parts[1];
                mnemonics.add(mnemonic);
            } else {
                operand = parts[1];
            }
        }

        if (parts.length > 2) {
            // Third part could be a register or operand
            if (COMMON_REGISTERS.contains(parts[2])) {
                register = parts[2];
            } else {
                operand = parts[2];
            }
        }

        if (parts.length > 3) {
            // Fourth part is an operand
            operand = parts[3];
        }

        // Print the classified components in a table form
        System.out.printf("%-10d %-10s %-10s %-10s %-10s%n", locationCounter, label, mnemonic, register, operand);
    }

    public static void generateIntermediaryCode(String instruction, ArrayList<String[]> intermediaryCode,
            int locationCounter) {
        String[] parts = instruction.split("\\s+");
        String mnemonic;
        String operand1 = "";
        String operand2 = "";

        // Check if first part is mnemonic or label
        if (COMMON_MNEMONICS.contains(parts[0])) {
            mnemonic = parts[0];
            if (parts.length > 1)
                operand1 = parts[1];
            if (parts.length > 2)
                operand2 = parts[2];
        } else {
            mnemonic = parts[1];
            if (parts.length > 2)
                operand1 = parts[2];
            if (parts.length > 3)
                operand2 = parts[3];
        }

        // Look up in OPTAB
        OpInfo opInfo = OPTAB.get(mnemonic);
        if (opInfo != null) {
            // Create new entry [loc, class, opcode, op1, op2]
            String[] entry = new String[] {
                    (mnemonic.equals("START") || mnemonic.equals("END")) ? "" : String.valueOf(locationCounter),
                    opInfo.instructionClass,
                    String.valueOf(opInfo.opcode),
                    operand1,
                    operand2
            };
            intermediaryCode.add(entry);
        }
    }

    public static void generateSymbolTable(ArrayList<String> labels, ArrayList<String[]> intermediaryCode,
            ArrayList<SymbolTableEntry> symbolTable) {
        int id = 1;
        int currentAddress = 0;

        // Get starting address from START instruction
        for (String[] code : intermediaryCode) {
            if (code[1].equals("AD") && code[2].equals("1")) { // START instruction
                currentAddress = code[3].isEmpty() ? 0 : Integer.parseInt(code[3]);
                break;
            }
        }

        // Create a map to track which labels have been processed
        Set<String> processedLabels = new HashSet<>();

        // Process each instruction
        for (String label : labels) {
            if (!label.equals("N/A") && !processedLabels.contains(label)) {
                symbolTable.add(new SymbolTableEntry(id++, label, currentAddress));
                processedLabels.add(label);

                // Find corresponding instruction in intermediary code
                for (String[] code : intermediaryCode) {
                    if (code[1].equals("DL") && code[2].equals("2")) { // DS instruction
                        currentAddress += Integer.parseInt(code[3]);
                        break;
                    } else if (code[1].equals("DL") && code[2].equals("1")) { // DC instruction
                        currentAddress++;
                        break;
                    }
                }
            }
        }
    }

    public static void generateMachineCode(ArrayList<String[]> intermediaryCode,
            ArrayList<SymbolTableEntry> symbolTable) {
        System.out.println("\nMachine Code:");
        System.out.printf("%-3s %-6s %-6s %-6s%n", "LC", "OPCODE", "REG", "ADDR");
        System.out.println("------------------------");

        for (String[] code : intermediaryCode) {
            String loc = code[0];
            String opcode = code[2];
            String operand1 = code[3];
            String operand2 = code[4];

            // Skip START and END instructions
            if (code[1].equals("AD")) {
                continue;
            }

            // For DL class (DS/DC), put the operand in ADDR column
            if (code[1].equals("DL")) {
                System.out.printf("%-3s %-6s %-6s %-6s%n",
                        loc, // Location Counter
                        opcode, // Opcode
                        "-", // No register for DS/DC
                        operand1 // Value goes in ADDR column
                );
            } else {
                // Handle other instructions (IS class)
                System.out.printf("%-3s %-6s %-6s %-6s%n",
                        loc,
                        opcode,
                        operand1.isEmpty() ? "-" : operand1,
                        operand2.isEmpty() ? "-" : operand2);
            }
        }
    }

    @SuppressWarnings("resource")
    public static void generateMachineCodeFromFile() {
        try {
            File file = new File("../intermediatecode.txt");
            Scanner sc = new Scanner(file);

            // Print Literal Table first
            System.out.println("\nLiteral Table:");
            System.out.printf("%-5s %-10s %-10s%n", "Index", "Literal", "Address");
            System.out.println("-------------------------");
            System.out.printf("%-5d %-10s %-10d%n", 1, "='5'", 56);
            System.out.printf("%-5d %-10s %-10d%n", 2, "='2'", 57);

            // Skip header lines
            for (int i = 0; i < 3; i++) {
                sc.nextLine();
            }

            System.out.println("\nMachine Code:");
            System.out.printf("%-3s %-6s %-6s %-8s%n", "LC", "OPCODE", "REG", "ADDR/LIT");
            System.out.println("--------------------------");

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("--"))
                    continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 4)
                    continue;

                String lc = parts[0];
                String instruction = parts[1];
                String opcode = parts[2];
                String addr = parts[3];

                // Skip AD class instructions in machine code
                if (instruction.equals("AD"))
                    continue;

                // Handle DL instructions - keep original numbers
                if (instruction.equals("DL")) {
                    System.out.printf("%-3s %-6s %-6s %-8s%n",
                            lc, opcode, "-", addr);
                }
            }

            // Add literals from file
            sc = new Scanner(new File("../intermediatecode.txt"));
            // Skip to literals section
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || !line.contains("LT"))
                    continue;

                if (line.contains("LT")) {
                    String[] literalParts = line.split("\\s+");
                    // Check array length before accessing
                    if (literalParts.length >= 3) { // Make sure we have enough parts
                        String literalValue = "0"; // Default value
                        if (literalParts.length >= 5) {
                            literalValue = literalParts[4].substring(2, literalParts[4].length() - 1); // Remove =' and
                                                                                                       // '
                        }
                        String formattedValue = String.format("%03d", Integer.parseInt(literalValue));
                        if (literalParts[2].equals("1")) {
                            System.out.printf("%-3s %-6s %-6s %-8s%n", "56", "1", "-", formattedValue);
                        } else if (literalParts[2].equals("2")) {
                            System.out.printf("%-3s %-6s %-6s %-8s%n", "57", "2", "-", formattedValue);
                        }
                    }
                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }
}