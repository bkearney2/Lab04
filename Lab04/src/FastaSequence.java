import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastaSequence 
{
	private static List<String> lines = new ArrayList<String>();
	protected String header;
	protected String sequence;
	
	public FastaSequence (List<String> lines)
	{
		FastaSequence.lines = lines;
	}
	
	public String getHeader() 
	{
		
		// Remove "> " or ">" from header, probably should be moved so header can be used elsewhere
		String newHeader = "";
		if (header.startsWith("> ")) {
			newHeader = header.substring(2);
		} else if (header.startsWith(">")) {
			newHeader = header.substring(1);
		} else newHeader = header;
		return newHeader;
		
	}
	
	public String getSequence() 
	{
		return sequence;	
	}
	
	public float getGCRatio() 
	{
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i=0;i<sequence.length();i++) {
			
			// based off of example from slides
			Integer count = map.get(sequence.charAt(i));

			if (count == null) {
				count = 0;
			}
			count++;
			char cha = sequence.charAt(i);
			// only interested in storing GC in map
			if (cha==('C') || cha==('G')) {
				map.put(cha,count);
			}
		}
//		System.out.println(map);
		
		// Make sure code doesn't break if no Gs or Cs 
		float gCount = 0f;
		float cCount = 0f;
		if (sequence.contains("G")) {
			gCount = map.get('G');
		}
		if (sequence.contains("C")) {
			cCount = map.get('C');
		}
		// Calculate ratio
		float gcRatio = (cCount+gCount)/ (float) sequence.length();
		return gcRatio;
		
	}
	
	public static List<FastaSequence> readFastaFile(String filepath) throws Exception 
	{
		List<FastaSequence> fasta_list = new ArrayList<FastaSequence>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(filepath)));
		String line = reader.readLine();
		
		// Use counter to not store an empty sequence at start of file
		int counter = 0;
		
		// There is probably too much depth here (lines is unnecessary?)
		while (line != null) {
			if (line.startsWith(">")) {
				if (counter > 0) {
					FastaSequence fasta = new FastaSequence(lines);
					fasta_list.add(fasta);
					fasta.header = FastaSequence.lines.get(0);
					String seqBuild = "";
					for (int i = 1; i<FastaSequence.lines.size();i++) {
						seqBuild = seqBuild + FastaSequence.lines.get(i);
					}
					fasta.sequence = seqBuild;
				}
				counter++;
				lines.clear();		
			}
			lines.add(line);
			line = reader.readLine();
		}
		
		// Store last sequence after while loop breaks (Redundant code but couldn't figure out any other way)
		FastaSequence fasta = new FastaSequence(lines);
		fasta_list.add(fasta);
		fasta.header = FastaSequence.lines.get(0);
		
		String seqBuild = "";
		for (int i = 1; i<FastaSequence.lines.size();i++) {
			seqBuild = seqBuild + FastaSequence.lines.get(i);
		}
		fasta.sequence = seqBuild;
		reader.close();
		
		return fasta_list;
	}
	
	public static void writeUnique(File inFile, File outFile) throws Exception 
	{
		List<FastaSequence> uniqueFile = FastaSequence.readFastaFile(inFile.getAbsolutePath());
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

	    Map<String,Integer> uniqueMap = new HashMap<>();

	    // Store unique sequences in hashmap and #occurences
		for (FastaSequence fs:uniqueFile) {
			String fsSequence = fs.getSequence();
			if(uniqueMap.containsKey(fsSequence)) {
                uniqueMap.put(fsSequence, uniqueMap.get(fsSequence) + 1);
			}
			else {
				uniqueMap.put(fsSequence, 1);
			}
		}
		System.out.println("Unordered hashmap: "+uniqueMap);
		List<String> listKeys = new ArrayList<String>(uniqueMap.keySet());
		List<Integer> listValues = new ArrayList<Integer>(uniqueMap.values());
		
//		System.out.println(listValues+"LISTVALUES");
//		System.out.println(listKeys);

		int max = Collections.max(listValues);
		int tick = 1;
		
		// Iterate from 1 to max # of occurences and write keys (sequences) in ascending order
		while (tick < max+1) {
			for (int i=0;i<listValues.size();i++) {
				if (tick == listValues.get(i)) {
//					System.out.println(i);
//					System.out.println(listKeys.get(i));
//					System.out.println(tick);
					writer.write(">"+String.valueOf(tick));
					writer.newLine();
					writer.write(listKeys.get(i));
					writer.newLine();
//					listValues.remove(i);
//					listKeys.remove(i);
				} else continue;
			}
			tick++;
		}
		
		writer.flush();
		writer.close();
	}
	
}
