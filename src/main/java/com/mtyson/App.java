package com.mtyson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.boon.core.reflection.Mapper;
import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

import spark.Spark;
import spark.servlet.SparkApplication;


public class App implements SparkApplication {

	private static Controller controller = null;
	static {
		if (Boolean.valueOf(System.getenv("USE_MEMDB"))) {
			System.out.println("Using MemDB");
			controller = new MemDBController();
		} else {
			System.out.println("Using CloudantDB");
			controller = new CloudantController();
		}
	}

	public static void main( String[] args ) {
    	new App().init();
  }

  public void init() {
    	// Remember: Need to allow Bluemix environment to set port
    	String strPort = System.getenv().get("PORT");
    	Integer port = 4567;
    	if (strPort != null){
    		port = Integer.parseInt(strPort);
    	}
    	Spark.port(port);

    	Spark.staticFileLocation("/public"); // The static asset location

    	Spark.post("/api/login", (req, res) -> {
    		return controller.login(req, res);
    	});
    	Spark.post("/api/login/", (req, res) -> {
    		return controller.login(req, res);
    	});
    	Spark.get("/api/recruiter", (req, res) -> {
    		return controller.getRecruiters(req, res);
    	});
    	Spark.get("/api/recruiter/", (req, res) -> {
    		return controller.getRecruiters(req, res);
    	});
    	Spark.post("/api/recruiter", (req, res) -> {
    		return controller.postRecruiter(req, res);
    	});
    	Spark.post("/api/recruiter/", (req, res) -> {
    		return controller.postRecruiter(req, res);
    	});
    	Spark.put("/api/recruiter", (req, res) -> {
    		return controller.putRecruiter(req, res);
    	});
    	Spark.put("/api/recruiter/", (req, res) -> {
    		return controller.putRecruiter(req, res);
    	});
    	Spark.delete("/api/recruiter", (req, res) -> {
    		return controller.deleteRecruiter(req, res);
    	});
    	Spark.delete("/api/recruiter/", (req, res) -> {
    		return controller.deleteRecruiter(req, res);
    	});
    	Spark.get("/api/test", (req, res) -> {
    		return controller.test(req, res);
    	});
    	Spark.get("/api/test/", (req, res) -> {
    		return controller.test(req, res);
    	});
  }
}
