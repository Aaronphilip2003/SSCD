package practice.lab1;

import java.util.*;
import java.io.*;

public class Tokenizing {

    private static final Set<String> MNEMONICS = new HashSet<>(Arrays.asList("START", "DC", "DS", "END"));

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
            put("DC", new OpInfo(1, "DL"));
            put("DS", new OpInfo(2, "DL"));
            put("START", new OpInfo(1, "AD"));
            put("END", new OpInfo(2, "AD"));
        }
    };

    public static void main(String args[]) {
        try {
            File file = new File("practice/lab1/input.txt");
            Scanner sc = new Scanner(file);
            int locationCounter = 0;

            ArrayList<String> mnemonics = new ArrayList<>();
            ArrayList<String> label = new ArrayList<>();
            ArrayList<String[]> intermediateCode = new ArrayList<>();

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
                    // classifyInstruction(input, locationCounter, mnemonics, label);
                    generateInermediateCode(input, intermediateCode, locationCounter);
                } else if (input.equals("END")) {
                    System.out.println(locationCounter + " " + input);
                    mnemonics.add("END");
                    break;
                } else {
                    generateInermediateCode(input, intermediateCode, locationCounter);
                    // Special handling for DS instruction
                    String[] parts = input.split("\\s+");
                    String mnemonic = MNEMONICS.contains(parts[0]) ? parts[0] : parts[1];
                    if (mnemonic.equals("DS")) {
                        // Get the operand value and increment by that amount
                        String operand = parts[parts.length - 1];
                        locationCounter += Integer.parseInt(operand);
                    } else {
                        locationCounter++;
                    }
                }
            }

            for (String[] code : intermediateCode) {
                System.out.printf("%-3s %-11s %-15s %-6s%n",
                        code[0], // LC
                        code[1] + " " + code[2], // Instruction (Class + Opcode)
                        "-", // Opcode1(Reg) - empty for these instructions
                        code[3] // Opcode2 - the actual operand value
                );
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        }
    }

    public static void classifyInstruction(String instruction, int locationCounter, ArrayList<String> mnemonics,
            ArrayList<String> labels) {
        String parts[] = instruction.split("\\s+");

        String mnemonic = "N/A";
        String label = "N/A";
        String register = "N/A";
        String operand = "N/A";

        if (parts.length > 0) {
            if (MNEMONICS.contains(parts[0])) {
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
            if (MNEMONICS.contains(parts[1])) {
                mnemonic = parts[1];
                mnemonics.add(mnemonic);
            } else {
                operand = parts[1];
            }
        }

        if (parts.length > 2) {
            operand = parts[2];
        }

        if (parts.length > 3) {
            operand = parts[3];
        }

        System.out.printf("%d\t %s\t %s\t %s\t %s\t \n", locationCounter, label, mnemonic, register, operand);
    }

    public static void generateInermediateCode(String instruciton, ArrayList<String[]>  intermediateCode,
            int locationCounter) {
        String[] parts = instruciton.split("\\s+");

        String mnemonic;
        String operand1 = "";
        String operand2 = "";

        if (MNEMONICS.contains(parts[0])) {
            mnemonic = parts[0];
            if (parts.length > 1) {
                operand1 = parts[1];
            }
            if (parts.length > 2) {
                operand2 = parts[2];
            }
        } else {
            mnemonic = parts[1];
            if (parts.length > 2)
                operand1 = parts[2];
            if (parts.length > 3)
                operand2 = parts[3];
        }

        OpInfo opinfo = OPTAB.get(mnemonic);
        if (opinfo != null) {
            String[] entry = new String[] {
                    (mnemonic.equals("START") || mnemonic.equals("END")) ? "" : String.valueOf(locationCounter),
                    opinfo.instructionClass,
                    String.valueOf(opinfo.opcode),
                    operand1,
                    operand2
            };
            intermediateCode.add(entry);
        }
    }

}
