package com.dremok.main;

import java.util.ArrayList;

import android.util.Log;

import com.dremok.model.Person;
import com.dremok.services.GenericSeeker;

public class PersonSeeker extends GenericSeeker<Person> {
	
	private static final String PEOPLE_SEARCH_PATH = "Person.search/";

	@Override
	public ArrayList<Person> find(String query) {
		ArrayList<Person> personList = retrievePersonList(query);
		return personList;
	}

	@Override
	public ArrayList<Person> find(String query, int maxResults) {
		ArrayList<Person> personList = retrievePersonList(query);
		return retrieveFirstResults(personList, maxResults);
	}
	
	private ArrayList<Person> retrievePersonList(String query) {
		String url = constructSearchUrl(query);
		String response = httpRetriever.retrieve(url);
		Log.d(getClass().getSimpleName(), response);
		return xmlParser.parsePeopleResponse(response);
	}

	@Override
	public String retrieveSearchMethodPath() {
		return PEOPLE_SEARCH_PATH;
	}

}
