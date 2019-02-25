import java.io.*;
import java.util.*;

public class a_priori {


    public static void main(String[] args) {

        String filename = "retail.txt";
        int hash_bucket_size = 10000;
        int numLines = 0;
        int line_chk = 0;

        HashMap<String, Integer> itemCount = new HashMap<String, Integer>();
        ArrayList<String> items = new ArrayList<String>();
        ArrayList<String> frequentItems = new ArrayList<String>();
        HashMap<String, Integer> pairCount = new  HashMap<String, Integer>();
        ArrayList<String> frequentPairs = new ArrayList<String>();
        FileInputStream fstream;
        
        try {
    		fstream = new FileInputStream(filename);
    		
    		
    		
    		DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		 
    		
    		while (( br.readLine()) != null) {
    			++numLines;
    		}
    	 
    		br.close();
    		
    		

    		   
    		   
    		   
    		 } catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

        Scanner sc = new Scanner(System.in);

        
        System.out.print("Enter sample size (in percentage): ");   //set sample size
        float chunk = sc.nextFloat();

        System.out.print("Enter support threshold (in percentage): ");    // set support threshold
        float support = sc.nextFloat();
        
        sc.close();
        

        float baskets = (chunk / 100) * numLines;
        
        float realSupport = (support/100) * baskets;


        //Start the timer
        Long startTime = System.currentTimeMillis();
        

        // First pass 

        Map<Integer, Integer> PCYMap = new HashMap<Integer, Integer>();

        try {
            fstream = new FileInputStream(filename);
            
            DataInputStream in = new DataInputStream(fstream);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String strLine;

            int lineCount = 0;

            while ((strLine = br.readLine()) != null && lineCount <= baskets) {
                String[] tokens = strLine.split(" ");

                for (int i = 0; i < tokens.length; i++) {

                    if (itemCount.containsKey(tokens[i]) && items.contains(tokens[i]))
                        itemCount.put(tokens[i], itemCount.get(tokens[i]) + 1);
                    else {
                    	
                        itemCount.put(tokens[i], 1);
                        items.add(tokens[i]);
                    }
                }

                lineCount++;

                //ParkChenYuuuuuuu
                for (int i = 0; i < tokens.length; i++) {
                    int itemNoI = Integer.parseInt(tokens[i]);

                    for (int j = i + 1; j < tokens.length; j++) {
                    	
                        int itemNoJ = Integer.parseInt(tokens[j]);

                        int hashValue = (itemNoI + itemNoJ) % hash_bucket_size;

                        if (PCYMap.containsKey(hashValue)) {
                        	
                            int curr_num = PCYMap.get(hashValue);
                            
                            curr_num++;
                            
                            PCYMap.put(hashValue, curr_num);
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

        // Add frequent items to a list
        addFrequentItems (items, itemCount, realSupport, frequentItems);

        //Bit Map
        Map<Integer, Boolean> bit_map = new HashMap<Integer, Boolean>();

        for (int key : PCYMap.keySet()) {
        	
            int count = PCYMap.get(key);
            
            if (count > realSupport) {
            	
                bit_map.put(key, true);
                
            } else {
            	
                bit_map.put(key, false);
                
            }
        }


        // Second pass over the data to calculate frequency of pairs
        try {
        	
            fstream = new FileInputStream(filename);
            
            DataInputStream in = new DataInputStream(fstream);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            String strLine;

            int lineCount = 0;

            while ((strLine = br.readLine()) != null && lineCount <= baskets) {
            	
                String[] tokens = strLine.split(" ");

                for(int i=0; i<tokens.length; i ++) {

                    String itemNoI = tokens[i];

                    for (int j = i + 1; j < tokens.length; j++) {
                        String itemNoJ = tokens[j];

                        int hashValue = (Integer.parseInt(itemNoI.trim()) + Integer.parseInt(itemNoJ.trim()))%hash_bucket_size;

                        if(frequentItems.contains(itemNoI) && frequentItems.contains(itemNoJ)){
                            if(bit_map.get(hashValue)) {
                                String itemKey = itemNoI + "-" + itemNoJ;
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

                lineCount++;
            }
            in.close();
            line_chk = lineCount;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        //Add pairs to freq pairs

        for(String key:pairCount.keySet()){
            int count = pairCount.get(key);
            if(count > realSupport){
                frequentPairs.add(key);
            }
        }

        //end the timer
        
        long endTime = System.currentTimeMillis();
        
        long runTime = endTime - startTime;  //calculated runtime

        System.out.println("Runtime: " + runTime+ " ms");

        System.out.println("Support: " + realSupport + " baskets: " + baskets + " LineChk: " + line_chk);

        System.out.println("Items: " + items.size());
        System.out.println("Frequent items: " + frequentItems.size());
        System.out.println("pairs Count: " + pairCount.size());
        System.out.println("Freq pairs: " + frequentPairs.size());

    }


    // function to add frequent items to frequentItems
    
    static void addFrequentItems (ArrayList<String> items, HashMap<String, Integer> itemCount, Float realSupport, ArrayList<String> frequentItems) {
        
    	for (int i = 0; i < items.size(); i++) {
        	
            if (itemCount.get(items.get(i)) >= realSupport && !frequentItems.contains( items.get(i) ))
            	
                frequentItems.add( items.get(i) );
            
        }
    }


}