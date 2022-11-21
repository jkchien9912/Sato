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
                if (parseRecord.length == 3 && 
                    parseRecord[0].equals("R") || parseRecord[0].equals("W")) {
                    // System.out.printf("%s %s %s\n", parseRecord[0], parseRecord[1], parseRecord[2]);
                    Long addr64 = Long.decode(parseRecord[1]);
                    memTraceRecord record = new memTraceRecord(addr64,
                                                               parseRecord[0].charAt(0), 
                                                               Integer.parseInt(parseRecord[2]));
                    records.add(record);
                    minAddr64 = Math.min(minAddr64, addr64);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Calculate addr32 for each record
        for (int i = 0; i < records.size(); ++i) {
            records.get(i).addr32 = magic + 
                                    (int)Math.min(records.get(i).addr64 - minAddr64, Integer.MAX_VALUE);
        }
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
    final int magic = 6666;
}