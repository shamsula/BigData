import java.io.*;
import java.util.*;
import java.time.*;


public class pcy {

	public static void main(String[] args) {
		
		String filename = "retail.txt";
		
		int total = 0, numLines = 0,  hash_bucket_size = 0;
		float baskets =0, minSupport=0;
		int remainder = 0;
		float sampleSize = 0, supportPercent = 0;
		hash_bucket_size = 10000;
		String input;
		FileInputStream fstream;
		int line_chk = 0;
		
		 HashMap<String, Integer> itemCount = new HashMap<String, Integer>();
	     ArrayList<String> items = new ArrayList<String>();
	     ArrayList<String> frequentItems = new ArrayList<String>();
	     HashMap<String, Integer> pairCount = new HashMap<String, Integer>();
	     ArrayList<String> frequentPairs = new ArrayList<String>();
		
		
		
		
		
		//gets the number of lines in the file 
		try {
		fstream = new FileInputStream(filename);
		
		
		
		DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
		 
		
		while (( br.readLine()) != null) {
			++numLines;
		}
	 
		br.close();
		
		Scanner sc = new Scanner(System.in);
		
		
		System.out.print("Enter sample size (in percentage): "); //Set the sample size
		    sampleSize = sc.nextInt();
		   
		    System.out.print("Enter support threshold (in percentage): "); //Set the  threshold (recommend 1%, 5% or 10%)
		   supportPercent = sc.nextInt();
		   
		   sc.close();

		   
		   
		   
		 } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	   //System.out.print("the total number of lines is: "+numLines);
	   
	   baskets = ((sampleSize / 100) * numLines);
	   minSupport =((supportPercent / 100) * baskets);
	   
	   //First pass
	   
	 //starting the timer 
	   Long start_timer = System.currentTimeMillis();
	   
	   Map<Integer, Integer> PCYMap = new HashMap<Integer, Integer>();
	   
	   try {
           fstream = new FileInputStream(filename);
           
           DataInputStream in = new DataInputStream(fstream);
           
           BufferedReader br = new BufferedReader(new InputStreamReader(in));
           
           String strLine;

           int line_cnt = 0;

           while ((strLine = br.readLine()) != null && line_cnt <= baskets) {
        	   
               String[] tokens = strLine.split(" ");

               for (int i = 0; i < tokens.length; i++) {

                   if (itemCount.containsKey(tokens[i]) && items.contains(tokens[i]))
                	   
                       itemCount.put(tokens[i], itemCount.get(tokens[i]) + 1);
                   else {
                	   
                       itemCount.put(tokens[i], 1);
                       items.add(tokens[i]);
                   }
               }

               line_cnt++;

               
               for (int i = 0; i < tokens.length; i++) {
                   int itemNoI = Integer.parseInt(tokens[i]);

                   for (int j = i + 1; j < tokens.length; j++) {
                       int itemNoJ = Integer.parseInt(tokens[j]);

                       int hashValue = (itemNoI + itemNoJ) % hash_bucket_size;

                       if (PCYMap.containsKey(hashValue)) {
                    	   
                           int currentNum = PCYMap.get(hashValue);
                           
                           currentNum++;
                           
                           PCYMap.put(hashValue, currentNum);
                           
                       } else {
                    	   
                           PCYMap.put(hashValue, 1);
                       }
                   }
               }
           }
           in.close();
       } catch (Exception e) {
           System.err.println("Error: " + e.getMessage());
       }
	   
	   		// Add Frequent items to list
       addFrequentItems (items, itemCount, minSupport, frequentItems);

       		//PCY Bit Map
       Map<Integer, Boolean> bit_map = new HashMap<Integer, Boolean>();

       for (int key : PCYMap.keySet()) {
           int count = PCYMap.get(key);
           if (count > minSupport) {
               bit_map.put(key, true);
           } else {
               bit_map.put(key, false);
           }
       }
       
       // Second pass 
       try {
            fstream = new FileInputStream(filename);
            
           DataInputStream in = new DataInputStream(fstream);
           
           BufferedReader br = new BufferedReader(new InputStreamReader(in));
           
           String strLine;

           int line_cnt = 0;

           while ((strLine = br.readLine()) != null && line_cnt <= baskets) {
               String[] tokens = strLine.split(" ");

               for(int i=0; i<tokens.length; i ++) {

                   String itemNo_i = tokens[i];

                   for (int j = i + 1; j < tokens.length; j++) {
                       String itemNo_j = tokens[j];

                       int hashValue = (Integer.parseInt(itemNo_i.trim()) + Integer.parseInt(itemNo_j.trim()))%hash_bucket_size;

                       if(frequentItems.contains(itemNo_i) && frequentItems.contains(itemNo_j)){
                           if(bit_map.get(hashValue)) {
                        	   
                               String itemKey = itemNo_i + "-" + itemNo_j;
                               
                               if (pairCount.containsKey(itemKey)) {
                                   int currentNum = pairCount.get(itemKey);
                                   currentNum++;
                                   pairCount.put(itemKey, currentNum);
                               } else {
                                   pairCount.put(itemKey, 1);
                               }
                           }
                       }
                   }

               }

               line_cnt++;
           }
           in.close();
           line_chk = line_cnt;
       } catch (Exception e) {
           System.err.println("Error: "+ e.getMessage());
       }
       
         //Add the pairs to  frequentPairs

       for(String key:pairCount.keySet()){
    	   
           int count = pairCount.get(key);
           
           if(count >minSupport){
               frequentPairs.add(key);
           }
       }

       //End the timer
       long end_timer = System.currentTimeMillis();

       
       
       long runTime = end_timer - start_timer; //Calculate runtime

       System.out.println("\nRuntime: " +runTime+ " ms");

       System.out.println("Support: " +minSupport + " Baskets: "+ baskets + " LineChk: "+ line_chk);

       System.out.println("items: " + items.size());
       
       System.out.println("Frequent items: " + frequentItems.size());
       System.out.println("pairs Count: " + pairCount.size());
       System.out.println("Freq pairs: " + frequentPairs.size());


	   

	}
	
	// Add frequent items to frequentItems
    static void addFrequentItems (ArrayList<String> items, HashMap<String, Integer> itemCount, float realSupport, ArrayList<String> frequentItems) {
        
    	for (int i = 0; i < items.size(); i++) {
    		
            if (itemCount.get(items.get(i)) >= realSupport && !frequentItems.contains( items.get(i) ))
            	
                frequentItems.add( items.get(i) );
        }
    }
	
	
	

}
