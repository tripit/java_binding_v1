import com.tripit.api.*;
import com.tripit.auth.*;
import java.io.*;
import java.util.*;

public class Example {
    public static void main(String[] args) throws Exception {
	Credential cred;
	Client client;

	cred = new OAuthCredential("CONSUMER_TOKEN",
				   "CONSUMER_SECRET",
				   "OAUTH_TOKEN",
				   "OAUTH_TOKEN_SECRET");
	client = new Client(cred, Client.DEFAULT_API_URI_PREFIX);

	// list
	Map<String, String> listMap = new HashMap<String, String>();
	listMap.put("traveler", "false");
	listMap.put("format", "json");

	Response r;
	Type t = null;
	t = Type.TRIP;
	r = client.list(t, listMap);
	System.out.println(r);

	// create
	Map<String, String> createMap = new HashMap<String, String>();
	createMap.put("xml", "<Request><Trip><start_date>2009-12-09</start_date><end_date>2009-12-27</end_date><primary_location>New York, NY</primary_location></Trip></Request>");
	r = client.create(createMap);
	System.out.println(r);
    }
}
