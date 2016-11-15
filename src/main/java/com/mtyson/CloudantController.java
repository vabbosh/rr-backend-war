package com.mtyson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Set;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

import spark.Request;
import spark.Response;
import spark.Spark;


public class CloudantController implements Controller {

	protected static ObjectMapper mapper = JsonFactory.create();

	private static CloudantClient cloudant = null;
	private static Database db = null;
	private static boolean emptyDB = true;

	private static String databaseName = "recruiters_db";

	private static String user = null;
	private static String password = null;

	static {

		initDB();

		if (emptyDB) { // Only add samples data when the db is new
			Map r = new HashMap<String, Object>();
			String id = UUID.randomUUID().toString();
			r.put("_id", id);
			r.put("id", id);
			r.put("name", "Fahrenheit");
			r.put("rank", 5);
			r.put("description", "A mid-size recruiting agency.");
			db.save(r);

			r = new HashMap<String, Map>();
			id = UUID.randomUUID().toString();
			r.put("_id", id);
			r.put("id", id);
			r.put("name", "Modis");
			r.put("rank", 4);
			r.put("description", "A corporate recruiting agency.");
			db.save(r);

			r = new HashMap<String, Map>();
			id = UUID.randomUUID().toString();
			r.put("_id", id);
			r.put("id", id);
			r.put("name", "Experis");
			r.put("rank", 3);
			r.put("description", "A corporate recruiting agency.");
			db.save(r);

			r = new HashMap<String, Map>();
			id = UUID.randomUUID().toString();
			r.put("_id", id);
			r.put("id", id);
			r.put("name", "TekSystems");
			r.put("rank", 3);
			r.put("description", "A corporate recruiting agency.");
			db.save(r);
		}
	}

	private static Database initDB() {
		if (cloudant == null) {
			initClient();
		}

		if (db == null) {
			try {
				db = cloudant.database(databaseName, true);
				List<String> allDocIds = db.getAllDocsRequestBuilder()
																	.build()
																	.getResponse()
																	.getDocIds();
				emptyDB = (allDocIds != null && allDocIds.isEmpty());
			} catch (Exception e) {
				throw new RuntimeException("DB Not found", e);
			}
		}
		return db;
	}

	private static void initClient() {
		if (cloudant == null) {
			synchronized (CloudantController.class) {
				if (cloudant != null) {
					return;
				}
				cloudant = createClient();

			} // end synchronized
		}
	}

	private static CloudantClient createClient() {
		// VCAP_SERVICES is a system environment variable
		// Parse it to obtain the NoSQL DB connection info
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		String serviceName = null;

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			JsonObject obj = (JsonObject) new JsonParser().parse(VCAP_SERVICES);
			Entry<String, JsonElement> dbEntry = null;
			Set<Entry<String, JsonElement>> entries = obj.entrySet();
			// Look for the VCAP key that holds the cloudant no sql db information
			for (Entry<String, JsonElement> eachEntry : entries) {
				if (eachEntry.getKey().toLowerCase().contains("cloudant")) {
					dbEntry = eachEntry;
					break;
				}
			}
			if (dbEntry == null) {
				throw new RuntimeException("Could not find cloudantNoSQLDB key in VCAP_SERVICES env variable");
			}

			obj = (JsonObject) ((JsonArray) dbEntry.getValue()).get(0);
			serviceName = (String) dbEntry.getKey();
			System.out.println("Service Name - " + serviceName);

			obj = (JsonObject) obj.get("credentials");

			user = obj.get("username").getAsString();
			password = obj.get("password").getAsString();

		} else {
			throw new RuntimeException("VCAP_SERVICES not found");
		}

		try {
			CloudantClient client = ClientBuilder.account(user)
					.username(user)
					.password(password)
					.build();
			return client;
		} catch (CouchDbException e) {
			throw new RuntimeException("Unable to connect to repository", e);
		}
	}

	public String login(Request req, Response res) {
		Map<String,Object> data = mapper.readValue(req.body(), Map.class);
		data.put("id", UUID.randomUUID());
		return mapper.toJson(data);
	}

	public String getRecruiters(Request req, Response res) {
		List<HashMap> allDocs = null;

		try {
			allDocs = db.getAllDocsRequestBuilder()
									.includeDocs(true)
									.build()
									.getResponse()
									.getDocsAs(HashMap.class);

		} catch (Exception e) {
			System.out.println("Exception thrown : " + e.getMessage());
			res.status(401);
		}

		return mapper.toJson(allDocs);
	}

	public String postRecruiter(Request req, Response res) {
		Map<String,Object> data =	mapper.readValue(req.body(), Map.class);
		HashMap<String, Object> objFromDB = db.find(HashMap.class, data.get("id") + "");
		if (objFromDB != null) {
				objFromDB.put("description", data.get("description"));
				objFromDB.put("name", data.get("name"));
				objFromDB.put("rank", data.get("rank"));
				db.update(objFromDB);
		}

		return mapper.toJson(data);
	}

	public String putRecruiter(Request req, Response res) {
		Map<String,Object> data =	mapper.readValue(req.body(), Map.class);
		String id = UUID.randomUUID().toString();
		data.put("_id", id);
		data.put("id", id);
		db.save(data);

		return mapper.toJson(data);
	}

	public String deleteRecruiter(Request req, Response res) {
		Map<String,Object> data =	mapper.readValue(req.body(), Map.class);
		HashMap<String, Object> objFromDB = db.find(HashMap.class, data.get("id") + "");
		if (objFromDB != null) {
				db.remove(objFromDB);
		}

		return mapper.toJson(data);
	}

	public String test(Request req, Response res) {
		return "TEST OK";
	}

}
