import java.io.File;
import java.util.List;

public class FastaFactory {
	
	

	public static void main(String[] args) throws Exception {
		List<FastaSequence> fastaList = FastaSequence.readFastaFile("fasta_input.txt");
		
		for (FastaSequence fs:fastaList) {
			System.out.println(fs.getHeader());
			System.out.println(fs.getSequence());
			System.out.println(fs.getGCRatio());
		}
		
		FastaSequence.writeUnique(new File("fasta_input.txt")
				, new File("unique_out.txt"));
	}

}
