package de.mpimp.golm.robin.misc;

/**
* Sequence only packing format using 4-bits per base. 'N's and ambiguous bases are supported.
* 
* @author tony
*
*/

public class Seq4Packing
{
	private final static int BASE_A=0x1;
	private final static int BASE_C=0x4;
	private final static int BASE_G=0x8;
	private final static int BASE_T=0x2;

	private final static char unpackArray[]={
			0, //
			'A', // A
			'T', // T
			'W', // T A
			
			'C', // C
			'M', // C A
			'Y', // C T
			'H', // C T A
			
			'G', // G
			'R', // G A
			'K', // G T
			'D', // G T A
			
			'S', // G C
			'V', // G C A
			'B', // G C T  
			'N'  // G C T A
	};
	
	// A:1 T:2 C:4 G:8
	private final static int packArray[] = { 
		BASE_A, 						// A
		BASE_C|BASE_G|BASE_T, 			// B -> C G T
		BASE_C, 						// C
		BASE_A|BASE_G|BASE_T, 			// D -> A G T
		0x0,   						    // E -> invalid
		0x0,							// F -> invalid
		BASE_G, 						// G
		BASE_A|BASE_C|BASE_T, 			// H -> A C T
		0x0,							// I -> invalid
		0x0,							// J -> invalid
		BASE_G|BASE_T, 					// K -> G T
		0x0,							// L -> invalid
		BASE_A|BASE_C, 					// M -> A C
		BASE_A|BASE_C|BASE_G|BASE_T, 	// N -> A C G T
		0x0, 							// O -> invalid
		0x0, 							// P -> invalid
		0x0, 							// Q -> invalid
		BASE_A|BASE_G, 					// R -> A G
		BASE_C|BASE_G, 					// S -> C G
		BASE_T, 						// T
		0x0, 							// U -> invalid (should be mapped to T earlier)
		BASE_A|BASE_C|BASE_G, 			// V -> A C G
		BASE_A|BASE_T, 					// W -> A T
		0x0, 							// X -> invalid
		BASE_C|BASE_T, 					// Y -> C T
		0x0  							// Z -> invalid
	};
	
	public static byte[] pack(byte sequence[]) throws PaganUtilException
	{
		int outLength=(sequence.length+1)/2;
	
		byte[] out = new byte[outLength];

		// Even index -> high nibble
		// Odd index (if any) -> low nibble
		
		int pairs=sequence.length/2;
		
		for (int i = 0; i < pairs; i++)
			out[i]=(byte)((packLookup(sequence[i*2]) << 4) + packLookup(sequence[i*2+1]));
		
		if(pairs!=outLength)
			{
			out[pairs]=(byte)(packLookup(sequence[pairs*2]) << 4);
			}
		

		return out;
	}

	private static int packLookup(byte in) throws PaganUtilException
	{
		if((in>='A')&&(in<='Z'))
			return packArray[in-'A'];
		
		return 0;
	}
	
	public static char unpackSingleBase(byte packedData[], int index)
	{
		int data = packedData[index/2] & 0xFF;

		if((index & 0x1) == 0)
			data >>= 4;
		else
			data &= 0xF;
		
		return unpackArray[data];
	}

	public static String unpack(byte packedData[])
	{
		StringBuilder sb=new StringBuilder();

		int lastIndex=packedData.length-1;
		for(int index=0;index<packedData.length;index++)
			{
			int data = packedData[index] & 0xFF;

			int tmp=data>>4;
			if(index!=lastIndex || tmp!=0)
				sb.append(unpackArray[tmp]);
			
			data &= 0xF;
			if(index!=lastIndex || data!=0)
				sb.append(unpackArray[data]);
			}
		
		return sb.toString();
	}

	public static int getUnpackedLength(byte packedData[])
	{
		int lastPos=packedData.length-1;
		byte last=packedData[lastPos];
		
		if((last & 0xF) == 0)
			return lastPos*2+1;
		else
			return lastPos*2+2;
	}

    public static class PaganUtilException extends Exception {

        public PaganUtilException() {
        }
    }
}

