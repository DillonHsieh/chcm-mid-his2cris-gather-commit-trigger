package chcm.mid.his2cris.gather.commit.trigger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.zip.GZIPInputStream;

public class HandlerGatherCommitTrigger{	
	
	public static String handlerRequest(String logger){
		Decoder decoder = Base64.getDecoder();
		byte[] decodedEvent = decoder.decode(logger);
		StringBuilder output = new StringBuilder();
		try {
			GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(decodedEvent));
	        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
	        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	        bufferedReader.lines().forEach( line -> {
	        	System.out.println(line);
	        	output.append(line);
	        });
	        
	        
	        
	        
		} catch(IOException e) {
        	System.out.println("ERROR: " + e.toString());
        }
		return "";
	}
}
