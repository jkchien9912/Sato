package nachos.membench;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public final class MemPattern {
    
    public MemPattern(final String mTraceFilename) {
        Long minAddr64 = Long.MAX_VALUE;
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(mTraceFilename));
            String line = reader.readLine();
            while (line != null) {
                String[] parseRecord = line.split("\\s+");
                if (parseRecord.length == 3) {
                    if ((parseRecord[0].equals("R") || parseRecord[0].equals("W")) &&
                        isInteger(parseRecord[2]) && 
                        (parseRecord[1].startsWith("0x") && parseRecord[1].length() == 14)) {
                        // System.out.printf("%s %s %s\n", parseRecord[0], parseRecord[1], parseRecord[2]);
                        Long addr64 = Long.decode(parseRecord[1]);
                        memTraceRecord record = new memTraceRecord(addr64,
                                                                parseRecord[0].charAt(0), 
                                                                4);
                        records.add(record);
                        minAddr64 = Math.min(minAddr64, addr64);
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Calculate addr32 for each record
        int maxOffset = 0;
        for (int i = 0; i < records.size(); ++i) {
            if (records.get(i).addr64 - minAddr64 <= Integer.MAX_VALUE) {
                int offset = (int)(records.get(i).addr64 - minAddr64);
                maxOffset = Math.max(maxOffset, offset);
                records.get(i).addr32 = magic + offset;
            }
        }
        range = maxOffset;
    }

    public memTraceRecord getNextRecord() {
        return records.get(currIndex++);
    }

    public void dump() {
        for (int i = 0; i < records.size(); ++i) {
            memTraceRecord tmp = records.get(i);
            System.out.printf("%c %d/%d %d\n", tmp.type, tmp.addr64, tmp.addr32, tmp.size);
        }
        System.out.printf("\n");
    }

    private static boolean isInteger(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int num = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public class memTraceRecord {
        public memTraceRecord(Long addr64, char type, int size) {
            this.addr64 = addr64;
            this.type = type;
            this.size = size;
        }
        public Long addr64;
        public int addr32;
        public char type;
        public int size;
    }

    public ArrayList<memTraceRecord> records = new ArrayList<>();
    public int currIndex;
    private final int magic = 0;
    public int range = 0;
}