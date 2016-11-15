package com.mtyson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

import spark.Request;
import spark.Response;
import spark.Spark;

public class MemDBController implements Controller {
	protected static ObjectMapper mapper = JsonFactory.create();
	private static List<Map> recruiters = new ArrayList<Map>();

	static {
		Map r = new HashMap<String, Object>();
		r.put("id", UUID.randomUUID().toString());
		r.put("name", "Fahrenheit");
		r.put("rank", 5);
		r.put("description", "A mid-size recruiting agency.");
		recruiters.add(r);

		r = new HashMap<String, Map>();
		r.put("id", UUID.randomUUID().toString());
		r.put("name", "Modis");
		r.put("rank", 4);
		r.put("description", "A corporate recruiting agency.");
		recruiters.add(r);

		r = new HashMap<String, Map>();
		r.put("id", UUID.randomUUID().toString());
		r.put("name", "Experis");
		r.put("rank", 3);
		r.put("description", "A corporate recruiting agency.");
		recruiters.add(r);

		r = new HashMap<String, Map>();
		r.put("id", UUID.randomUUID().toString());
		r.put("name", "TekSystems");
		r.put("rank", 3);
		r.put("description", "A corporate recruiting agency.");
		recruiters.add(r);
	}

	public String login(Request req, Response res){
		Map<String,Object> data =
				mapper.readValue(req.body(), Map.class);
		data.put("id", UUID.randomUUID());
		return mapper.toJson(data);
	}
	public String getRecruiters(Request req, Response res){
		return mapper.toJson(recruiters);
	}
	public String postRecruiter(Request req, Response res){
		Map<String,Object> data =
				mapper.readValue(req.body(), Map.class);

		for (Map r : recruiters){
			if (r.get("id").equals(data.get("id"))){
				r.put("description", data.get("description"));
			}
		}

		return mapper.toJson(data);
	}
	public String putRecruiter(Request req, Response res){
		Map<String,Object> data =
				mapper.readValue(req.body(), Map.class);
		data.put("id", UUID.randomUUID().toString());
		recruiters.add(data);

		return mapper.toJson(data);
	}
	public String deleteRecruiter(Request req, Response res){
		Map<String,Object> data =
				mapper.readValue(req.body(), Map.class);

		for (Iterator<Map> i = recruiters.iterator(); i.hasNext();) {
		    Map r = i.next();
		    if (r.get("id").equals(data.get("id"))) {
		       i.remove();
		    }
		}

		return mapper.toJson(data);
	}

	public String test(Request req, Response res){
		return "TEST OK";
	}
}
