import java.io.*;
import java.util.*;

public class MacroProcessing {
    // MNT: Index, Name, MDT Index
    static class MNTEntry {
        int index;
        String name;
        int mdtIndex;

        MNTEntry(int index, String name, int mdtIndex) {
            this.index = index;
            this.name = name;
            this.mdtIndex = mdtIndex;
        }
    }

    // MDT: Index, Instruction
    static class MDTEntry {
        int index;
        String instruction;

        MDTEntry(int index, String instruction) {
            this.index = index;
            this.instruction = instruction;
        }
    }

    // ALA: Index (#1, #2...), Formal Parameter, Actual Parameter
    static class ALAEntry {
        String index;
        String formalParam;
        String actualParam; // Will be empty in Pass 1

        ALAEntry(String index, String formalParam) {
            this.index = index;
            this.formalParam = formalParam;
            this.actualParam = ""; // Empty in Pass 1
        }
    }

    public static ArrayList<MNTEntry> Pass1(String filename) throws IOException {
        ArrayList<MNTEntry> mnt = new ArrayList<>();
        ArrayList<MDTEntry> mdt = new ArrayList<>();
        ArrayList<ALAEntry> ala = new ArrayList<>();
        ArrayList<String> programLines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        int mntIndex = 1;
        int mdtIndex = 1;
        boolean isMacroDefinition = false;
        while ((line = br.readLine()) != null) {
            if (line.trim().equals("MACRO")) {
                isMacroDefinition = true;
                // Read macro prototype
                String prototype = br.readLine().trim();
                String[] parts = prototype.split(" ");
                String macroName = parts[0];

                mnt.add(new MNTEntry(mntIndex++, macroName, mdtIndex));

                mdt.add(new MDTEntry(mdtIndex++, prototype));

                if (parts.length > 1) {
                    String[] params = parts[1].split(",");
                    for (int i = 0; i < params.length; i++) {
                        // Create ALA entry with #number format
                        ala.add(new ALAEntry("#" + (i + 1), params[i]));
                    }
                }

                while (!(line = br.readLine()).trim().equals("MEND")) {
                    // Replace formal parameters with #numbers in MDT
                    String modifiedLine = line;
                    for (ALAEntry alaEntry : ala) {
                        modifiedLine = modifiedLine.replace(alaEntry.formalParam, alaEntry.index);
                    }
                    mdt.add(new MDTEntry(mdtIndex++, modifiedLine));
                }

                mdt.add(new MDTEntry(mdtIndex++, "MEND"));

                while ((line = br.readLine()) != null) {
                    programLines.add(line.trim());
                }
            } else if (line.trim().equals("MEND")) {
                isMacroDefinition = false;
            } else if (!isMacroDefinition) {
                programLines.add(line.trim());
            }
        }

        // Print tables
        System.out.println("Macro Name Table (MNT):");
        System.out.println("Index\tName\tMDT Index");
        for (MNTEntry entry : mnt) {
            System.out.println(entry.index + "\t" + entry.name + "\t" + entry.mdtIndex);
        }

        System.out.println("\nMacro Definition Table (MDT):");
        System.out.println("Index\tInstruction");
        for (MDTEntry entry : mdt) {
            System.out.println(entry.index + "\t" + entry.instruction);
        }

        System.out.println("\nArgument List Array (ALA):");
        System.out.println("Index\tFormal Parameter\tActual Parameter");
        for (ALAEntry entry : ala) {
            System.out.println(entry.index + "\t" + entry.formalParam + "\t" + entry.actualParam);
        }

        // Print the program (START to END)
        System.out.println("\nProgram:");
        for (String programLine : programLines) {
            System.out.println(programLine);
        }

        // Save program lines to pass1_output.txt
        FileWriter writer = new FileWriter("pass1_output.txt");
        for (String programLine : programLines) {
            writer.write(programLine + "\n");
        }
        writer.close();

        // Save MDT to mdt_output.txt
        writer = new FileWriter("mdt_output.txt");
        for (MDTEntry entry : mdt) {
            writer.write(entry.index + "\t" + entry.instruction + "\n");
        }
        writer.close();

        return mnt;
    }

    public static void Pass2(ArrayList<MNTEntry> mnt, String filename) throws IOException {
        ArrayList<ALAEntry> ala = new ArrayList<>();
        ArrayList<MDTEntry> mdt = new ArrayList<>();

        ala.add(new ALAEntry("#1", "&A1"));

        BufferedReader mdtReader = new BufferedReader(new FileReader("mdt_output.txt"));
        String mdtLine;
        while ((mdtLine = mdtReader.readLine()) != null) {
            String[] parts = mdtLine.split("\t");
            mdt.add(new MDTEntry(Integer.parseInt(parts[0]), parts[1]));
        }
        mdtReader.close();

        BufferedReader br = new BufferedReader(new FileReader("pass1_output.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            String firstWord = line.trim().split(" ")[0];
            for (MNTEntry entry : mnt) {
                if (firstWord.equals(entry.name)) {
                    String actualParam = line.trim().split(" ")[1];
                    ala.get(0).actualParam = actualParam;
                    break;
                }
            }
        }
        br.close();

        // Print ALA first
        System.out.println("\nArgument List Array (ALA) after adding actual parameters:");
        System.out.println("Index\t\tFormal Parameter\t\tActual Parameter");
        System.out.println("------------------------------------------------");
        for (ALAEntry entry : ala) {
            System.out.printf("%s\t\t%-20s\t%s%n",
                    entry.index,
                    entry.formalParam,
                    entry.actualParam);
        }

        // Then print expanded program
        System.out.println("\nExpanded Program:");
        br = new BufferedReader(new FileReader("pass1_output.txt"));
        while ((line = br.readLine()) != null) {
            String firstWord = line.trim().split(" ")[0];
            boolean isMacroCall = false;

            for (MNTEntry entry : mnt) {
                if (firstWord.equals(entry.name)) {
                    isMacroCall = true;
                    String actualParam = line.trim().split(" ")[1];

                    for (int i = 1; i < mdt.size(); i++) {
                        if (mdt.get(i).instruction.equals("MEND")) {
                            break;
                        }
                        String expandedInstr = mdt.get(i).instruction.replace("#1", actualParam);
                        System.out.println(expandedInstr);
                    }
                    break;
                }
            }

            if (!isMacroCall) {
                System.out.println(line.trim());
            }
        }

        br.close();
    }

    public static void main(String[] args) throws IOException {
        ArrayList<MNTEntry> mnt = Pass1("input.txt");
        System.out.println("\nPass 2:");
        Pass2(mnt, "input.txt");
    }
}