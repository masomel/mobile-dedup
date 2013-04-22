 /*
 *@author Madhuvanthi Jayakumar
 */
public class Cache{

/* Sample: Prototype deduplication with relatively small amounts of data
	small enough to be stored in main memory.
	Main purpose: Simulate caching and determine content overlap 
	within a website as well as between websites 
	MODEL:  |--------------------------------------------|
			|URL1 | chunk1 -> chunk2 -> chunk3 -> chunk4 |  
			|-----|--------------------------------------|
			|URL2 | chunk1 -> chunk2 -> chunk3 -> chunk4 | 
			|--------------------------------------------|*/

	private HashTable cache;
	private double missrate;

	public Cache(){
		cache = new HashTable()<String,ArrayList>;
		missrate=0;

	}

	public double getMissRate(){
		return missrate;
	}

	public HashTable getCache(){
		return cache;
	}

	/** @params: url, chunked content of the webpage in byte array
	    @return: int array with content or fingerprint 
	    Procedure: 
	    At <Key = url> in the cache, iterate through 
	    array list to determine overlap
		If fingerprints match, use fingerprint
		Else Replace and use content.
		Sets hitrate equal to the percent of matches
	     */
	public byte[] getWebContent(String url, Chunk[] content){
		missrate=0;
		if(!cache.containsKey(url)){
			setValue(url,content);
			missrate+=content.length;
			return reconstructData(cache.get(url));
		}
		ArrayList diff = new ArrayList();	//output page (comb of old data and new data)
		int len = cache.get(url).size();	//number of items in arraylist of chunks
		ArrayList chunks = cache.get(url);	//arraylist of chunk objects:
		while(int i= 0; i<len; i++){	//go through each of the chunks
			diff.add(chunk.get(i).fingerprint);	//always add fp
			if(chunk.get(i).fingerprint != content[i].fingerprint){} //fingerprint not yet implemented.(?)
				chunk.put(i, content[i]); //does put overwrite? We've overwritten cache with new chunk
				missrate+=1;
			}
		missrate = missrate/content.length;
		Chunk[] chunksOfWebpage = FPArrayListToChunkArray(diff);
		return reconstructData(chunksOfWebpage);
		}
		//...
	}

	/** COULD BE IN URL CLASS? */
	private Chunk[] FPArrayListToChunkArray(ArrayList fp){
		int len = fp.size();
		Chunk[] chunk = new Chunk[len];
		for(int i=0; i<len; i++){
			chunk[i] = fp.get(i);
		}

	}

	/** COULD BE IN URL CLASS? */
	/** Given array of Chunk objects, reconstruct the full web content 
		in the form of a byte array using the data from the chunks*/ 
	private byte[] reconstructData(Chunk[] chunk){
		int len = chunk.length*10;
		byte[] webcontent = new byte[len]; //ten hardcoded (size of each chunk) ?changelater
		byte[] chunkData = new byte[10]; //hardcoded, change later
		for(int i=0; i<len; i++){
			chunkData = chunk.getData(i);
			for(int j=0; j<10; j++){
				webcontent[i] = chunkData[j];
			}
		}
		return webcontent; //returns entirety of everything in chunks, constructing a full webpage..
	}

	/** */


	/** @param: url that is not found in cache 
		Procedure:
		Sets value of url equal to an arraylist containing contents */
	private void setValue(String url, Chunk[] content){
		ArrayList data = new ArrayList();	//VALUE in cache = ArrayList of Chunks
		for(int i=0; i<content.length; i++){
			data.add(content[i]);
		}
		cache.put(url,data);
	}

}