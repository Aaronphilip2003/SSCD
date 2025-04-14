package practice.lab1;

import java.util.*;
import java.io.*;

public class Tokenizing1 {

    private static final Set<String> MNEMONICS = new HashSet<>(Arrays.asList("START","END","DC","DS"));

    private static class OpInfo{
        private final int opcode;
        private final String instructionClass;

        public OpInfo(int opcode,String instructionClass) {
            this.opcode = opcode;
            this.instructionClass=instructionClass;
        }
    }

    private static final HashMap<String,OpInfo> OPTAB = new HashMap<String,OpInfo>() {
        {
        put("DC", new OpInfo(1, "DL"));
        put("DS", new OpInfo(2, "DL"));
        put("START", new OpInfo(1, "AD"));
        put("END", new OpInfo(2, "AD"));
        }
    };

    public static void main(String args[])
    {
        try
        {
            File file = new File("practice/lab1/input.txt");
            Scanner sc = new Scanner(file);
            int locationCounter = 0;

            ArrayList<String> mnemonics = new ArrayList<>();
            ArrayList<String> label = new ArrayList<>();
            ArrayList<String[]> intermediateCode = new ArrayList<>();

            while (sc.hasNextLine())
            {
                String input = sc.nextLine().trim();
                
                if(input.isEmpty())
                continue;

                if(input.startsWith("START"))
                {
                    String[] parts = input.split("\\s+");
                    if (parts.length > 1 )
                    {
                        locationCounter = Integer.parseInt(parts[1]);
                    }
                    else
                    {
                        locationCounter=0;
                    }
                    generateInermediateCode(input, intermediateCode, locationCounter);
                }
                else if (input.equals("END"))
                {
                    generateInermediateCode(input, intermediateCode, locationCounter);
                    mnemonics.add("END");
                    break;
                }
                else
                {
                    generateInermediateCode(input, intermediateCode, locationCounter);
                    String parts[] = input.split("\\s+");
                    String mnemonic=MNEMONICS.contains(parts[0]) ? parts[0]:parts[1];
                    if(mnemonic.equals("DS"))
                    {
                        String operand = parts[parts.length - 1];
                        locationCounter+=Integer.parseInt(operand);
                    }
                    else
                    {
                        locationCounter++;
                    }
                }

            }

            for(String[] code:intermediateCode){
                System.out.printf("%s %s %s %s%n",
                code[0],
                code[1]+" "+code[2],
                "-",
                code[3]
                );
            }

        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not Found");
        }
    }


    public static void generateInermediateCode(String instruction, ArrayList<String[]> intermediateCode, int locationCounter)
    {
        String parts[] = instruction.split("\\s+");

        String mnemonic;
        String operand1="";
        String operand2="";

        if(MNEMONICS.contains(parts[0]))
        {
            mnemonic=parts[0];
            if(parts.length>1)
            operand1=parts[1];
            if(parts.length>2)
            operand1=parts[2];
        }
        else
        {
            mnemonic=parts[1];
            if (parts.length > 2)
            operand1 = parts[2];
            if (parts.length > 3)
            operand2 = parts[3];
        }

        OpInfo opInfo = OPTAB.get(mnemonic);
        if(opInfo!=null)
        {
            String[] entry=new String[] {
                (mnemonic.equals("START") || mnemonic.equals("END")) ? "" : String.valueOf(locationCounter),
                opInfo.instructionClass,
                String.valueOf(opInfo.opcode),
                operand1,
                operand2
            };
            intermediateCode.add(entry);
        }

    }
    
}
