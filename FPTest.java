/** Simple test function for the Fingerprinting class.
 *@author Marcela S. Melara
 *@since 14 April 2013
*/

public class FPTest{

    public static void main(String[] args){

	// some "packet bytes"
	int[] pkt_bytes = {0x6d, 0x5e, 0xFF, 0x63, 0x6d, 0x5e, 0xFF, 0x63, 0x6d, 0x5e, 0xFF, 0x63, 0x6d, 0x5e, 0xFF, 0x63};

	// this test class does not pack 4 bytes into a single int
	int fp = Fingerprinting.fingerprint(pkt_bytes);

	System.out.println("The fingerprint for this chunk is: "+fp);

    }

}
