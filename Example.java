import com.tripit.api.*;
import com.tripit.auth.*;
import java.io.*;
import java.util.*;

public class Example {
    public static void main(String[] args) throws Exception {
	Credential cred;
	Client client;
	cred = new OAuthCredential("85bffb1da0b56bffc2bc1c6dc8c9774a74a3c28f",
				   "5dff1f8a303149c6dc74c106902cd66cb767755b",
				   "ecf5eea2058aa9c99a8fac194e6fd8108a6bcdb6",
				   "3ee134a46872c58f80524eb11980fbea9e91f34d");
	client = new Client(Client.DEFAULT_API_URI_PREFIX, cred);

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