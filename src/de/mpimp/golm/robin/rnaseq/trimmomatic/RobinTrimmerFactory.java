package de.mpimp.golm.robin.rnaseq.trimmomatic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.usadellab.trimmomatic.fastq.trim.BarcodeSplitter;
import org.usadellab.trimmomatic.fastq.trim.CropTrimmer;
import org.usadellab.trimmomatic.fastq.trim.IlluminaClippingTrimmer;
import org.usadellab.trimmomatic.fastq.trim.LeadingTrimmer;
import org.usadellab.trimmomatic.fastq.trim.MinLenTrimmer;
import org.usadellab.trimmomatic.fastq.trim.SlidingWindowTrimmer;
import org.usadellab.trimmomatic.fastq.trim.TrailingTrimmer;
import org.usadellab.trimmomatic.fastq.trim.Trimmer;

public class RobinTrimmerFactory
{
	public RobinTrimmerFactory()
	{
	}
        
        public Trimmer makeRobinTrimmer(TMTrimmerArguments args) throws IOException
	{
		
                String trimmerName = args.getIdentifier();
                
                if(trimmerName.equals("ILLUMINACLIP"))
			return new IlluminaClippingTrimmer(
                                    (File) args.get("seqs"),
                                    (Integer) args.get("seedMaxMiss"),
                                    (Integer) args.get("minPalindromeLikelihood"),
                                    (Integer) args.get("minSequenceLikelihood")
                                );                                
                
                if(trimmerName.equals("BARCODESPLITTER"))
			return new BarcodeSplitter(
                                    (HashMap<String, String>) args.get("barcodes"),
                                    (Integer) args.get("mism"),
                                    (Boolean) args.get("clip")
                                );
		
		if(trimmerName.equals("LEADING"))
			return new LeadingTrimmer(
                                    (Integer) args.get("qual")
                                );
		
		if(trimmerName.equals("CROP"))
			return new CropTrimmer(
                                    (Integer) args.get("len")
                                );
		
		if(trimmerName.equals("TRAILING"))
			return new TrailingTrimmer(
                                    (Integer) args.get("qual")
                                );
	
		if(trimmerName.equals("SLIDINGWINDOW")) 
			return new SlidingWindowTrimmer(
                                    (Integer) args.get("windowLength"),
                                    (Float) args.get("requiredQuality")                                
                                );
		
		if(trimmerName.equals("MINLEN"))
			return new MinLenTrimmer(
                                    (Integer) args.get("minLen")
                                );

		throw new RuntimeException("Unknown trimmer: "+trimmerName);
	}
}
