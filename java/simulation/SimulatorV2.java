package simulation;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import caching.ICache;
import chunking.Chunk;
import chunking.Chunking;
import devices.Mobile;
import devices.ProxyServer;

/**
 * Class representing the first version of the simulator.
 * @author Marcela Melara, Nayden Nedev
 *
 */
public class SimulatorV2 implements ISimulator {

	private String path;
	private String website;
	private int window_size;
	private int proxyCacheSize;
	private int mobileCacheSize;
	private int numIters;

	public SimulatorV2(String path, String website, int window_size, int proxyCacheSize, int mobileCacheSize, int numIters) {
		this.path = path;
		this.website = website;
		this.window_size = window_size;
		this.proxyCacheSize = proxyCacheSize;
		this.mobileCacheSize = mobileCacheSize;
		this.numIters = numIters;
	}

	@Override
	public void simulate() {
		// The Chunking Facility

		// A proxy server
		ProxyServer proxy = new ProxyServer(proxyCacheSize); //holds 2000 chunks; TODO: this should be an argument

		// A mobile device; Could imagine simulating multiple devices
		Mobile mobile = new Mobile(mobileCacheSize); //holds 500 chunks; TODO: this should be an argument

		// Used to calculated the average missrates
		double proxySum = 0;
		double mobSum = 0;

		// loop over all files of a given website and look at stats
		for(int i = 1; i <= numIters; i++) { // Caution: Hardcoded!
			
		    ArrayList<Chunk> chunks = new ArrayList<Chunk>();

		    try{
			chunks = proxy.chunkWebDataSliding(path +"/packet_bytes/" + website + i + ".txt", window_size);
		    }
		    catch(IOException e){
			System.out.println("An error occurred when trying to chunk data");
			return;
		    }

		    System.out.println("Processing file: " + Chunking.getFilename());

		    // System.out.println("Proxy server grabs all the web data for a requested webpage");		    
		    // System.out.println("Proxy server partitions the received data into fixed-size chunks");


		    
		    // System.out.println("Proxy server is computing the fingerprints for all received chunks.");

		    // Proxy computes the fingerprints of all the chunks it received
		    ArrayList<Integer> fps = proxy.sendAllFps(chunks);
		    
		    // System.out.println("Proxy server is sending the computed fingerprints to the mobile device.");

		    mobile.receiveFps(fps);

		    // System.out.println("Mobile device is checking its cache for the fingerprints it received from the proxy server.");
    
		    // Mobile device checks its cache for the fps it received from the proxy
		    ArrayList<Integer> mobNeeded = mobile.sendNeededFps();
		    
		    // System.out.println("Mobile device sends back a list of fingerprints of the chunks it does not have in its cache.");

		    proxy.receiveNeededFps(mobNeeded);

		    ArrayList<Chunk> neededChunks = proxy.sendNeededChunks();
		    
		    // System.out.println("Proxy sends back a list of chunks according to the needed fingerprints.");		    
		    // System.out.println("Mobile device caches the received content.");

		    mobile.receiveChunks(neededChunks);
		    
		    // System.out.println("Mobile device reconstructs the web data of the requested webpage.");
		    try {
		    	byte[] webdata = mobile.reconstructDataSliding();
		    	mobile.outputData(webdata, path, website, i);
		    } catch (FileNotFoundException fnfe) {
		    	fnfe.printStackTrace();
		    }
		    
		    System.out.println("----------------------");
		    
		    System.out.println("Number of chunks inspected: "+chunks.size());	
		    System.out.println("Remaining proxy cache capacity: "+proxy.getCache().getCapacity());
		    System.out.print("Proxy missrate: ");
		    customFormat("##.####", proxy.getProcessor().getMissRate());
		    System.out.println("Remaining mobile cache capacity: "+mobile.getCache().getCapacity());
		    System.out.print("Mobile missrate: ");
		    customFormat("##.####", mobile.getProcessor().getMissRate());
		    System.out.println();

		    proxySum += proxy.getProcessor().getMissRate();
		    mobSum += mobile.getProcessor().getMissRate();		
		} 

		// Hardcoded number of sites!
		System.out.print("Avg proxy missrate over all inspected sites: ");
		customFormat("##.####", proxySum/numIters);
		System.out.print("Avg mobile missrate over all inspected sites: ");
		customFormat("##.####", mobSum/numIters);
	}
        
    private static Chunk[] FPArrayListToChunkArray(ArrayList<Integer> fp, ICache cache){
	int len = fp.size();
	Chunk[] chunks = new Chunk[len];
	for(int i=0; i<len; i++){
	    chunks[i] = (Chunk)(cache.get((Integer)(fp.get(i))));
	}
	return chunks;
	
    }

    /** Helper function:
     * Formats how the doubles are printed given a specific pattern
     */
    static public void customFormat(String pattern, double value ) {
	DecimalFormat myFormatter = new DecimalFormat(pattern);
	String output = myFormatter.format(value);
	System.out.println(output);
    }
    
}