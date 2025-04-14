package practice.lab3;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class MacroProcessing {

    static class MNT {
        int index;
        String name;
        int mdtIndex;

        MNT(int index, String name, int mdtIndex) {
            this.index = index;
            this.name = name;
            this.mdtIndex = mdtIndex;
        }
    }

    static class MDT {
        int index;
        String instruction;

        MDT(int index, String instruction) {
            this.index = index;
            this.instruction = instruction;
        }
    }

    static class ALA {
        int index;
        String formalParameter;
        String actualParameter;

        ALA(int index, String formalParameter, String actualParameter) {
            this.index = index;
            this.formalParameter = formalParameter;
            this.actualParameter = actualParameter;
        }
    }

    static ArrayList<MNT> mntList = new ArrayList<>();
    static ArrayList<MDT> mdtList = new ArrayList<>();
    static ArrayList<ALA> alaList = new ArrayList<>();

    public static void main(String args[]) {
        try {
            File file = new File("practice/lab3/input.txt");
            Scanner sc = new Scanner(file);
            ArrayList<String> instructions = new ArrayList<>();

            while (sc.hasNextLine()) {
                String input = sc.nextLine().trim();
                instructions.add(input);
            }
            Pass1(instructions);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        }
    }

    public static void Pass1(ArrayList<String> instructions) {
        boolean isMacroDefinition = false;
        int mntIndex=0;
        int mdtIndex=0;
        int alaIndex=0;
        String macroName="";

        for(int i=0;i<instructions.size();i++)
        {
            String line = instructions.get(i).trim();
            String[] parts = line.split("\\s+");

            if(parts[0].equals("MACRO"))
            {
                isMacroDefinition = true;

                i++;
                line = instructions.get(i).trim();
                parts= line.split("\\s+");
                macroName=parts[0];
                System.out.println(macroName);

                // MNT Entry
                mntList.add(new MNT(mntIndex++, macroName, mdtIndex));

                if (parts.length > 1){
                    String[] params = parts[1].split(",");
                    for(String param: params){
                        alaList.add(new ALA(alaIndex++,param,""));
                    }
                }

            }
            else if(parts[0].equals("MEND"))
            {
                isMacroDefinition = false;
                mdtList.add(new MDT(mdtIndex++, line));
            }
            else if(isMacroDefinition)
            {
                mdtList.add(new MDT(mdtIndex++, line));
            }
        }

        System.out.println("Macro Name Table");
        for(MNT entry: mntList) {
            System.out.println("Index: " + entry.index + 
            ", Name: " + entry.name + 
            ", MDT Index: " + entry.mdtIndex);
        }

        System.out.println("Macro Definition Table");
        for(MDT entry: mdtList) {
            System.out.println(entry.index + "\t" + entry.instruction);
        }

        System.out.println("Argument List Array");
        for(ALA entry: alaList) {
            System.out.println(entry.index+"\t"+entry.formalParameter+"\t"+entry.actualParameter);
        }

        System.out.println("Program after Pass1");
        boolean isProgramSection = false;
        for(String line : instructions) {
            if(line.trim().equals("START")) {
                isProgramSection = true;
            }
            if(isProgramSection) {
                System.out.println(line.trim());
            }
            if(line.trim().equals("END")) {
                break;
            }
        }


    }
}
