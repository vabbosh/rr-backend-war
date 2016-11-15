package com.mtyson;

import spark.Request;
import spark.Response;

public interface Controller {

	public String login(Request req, Response res);
	public String getRecruiters(Request req, Response res);
	public String postRecruiter(Request req, Response res);
	public String putRecruiter(Request req, Response res);
	public String deleteRecruiter(Request req, Response res);
	public String test(Request req, Response res);

}
