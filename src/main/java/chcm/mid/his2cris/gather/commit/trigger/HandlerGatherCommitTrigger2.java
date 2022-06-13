package chcm.mid.his2cris.gather.commit.trigger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Base64.Decoder;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;

import chcm.mid.his2cris.gather.commit.trigger.dto.CHCM;
import chcm.mid.his2cris.gather.commit.trigger.dto.COMMIT;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class HandlerGatherCommitTrigger2{	

	
	public void handlerRequest(String test){
		List<CHCM> chcms = new ArrayList<>();
		CHCM chcmA = new CHCM();
		CHCM chcmB = new CHCM();
		
		COMMIT commitA =new COMMIT();
		commitA.setId("A");
		commitA.setCommitStatus("A status");
		chcmA.setCommit(commitA);
		COMMIT commitB =new COMMIT();
		commitB.setId("B");
		commitB.setCommitStatus("B status");
		chcmB.setCommit(commitB);
		
		chcms.add(chcmA);
		chcms.add(chcmB);
		System.out.println("chcms = "+chcms.toString());
		
		ArrayList<COMMIT> result = (ArrayList<COMMIT>) chcms.stream().map(x ->x.getCommit()).collect(Collectors.toList());
		System.out.println(result.toString());
	}

    

  

}
