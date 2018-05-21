package utils;

import java.util.ArrayList;

public enum Comparison {
	 LONG("http://dbpedia.org/ontology/length" ),
	 LONGER("http://dbpedia.org/ontology/length"),
	 LONGEST("http://dbpedia.org/ontology/length", "DESC"),
	 OLD("http://dbpedia.org/ontology/openingYear,http://dbpedia.org/ontology/birthDate"),
	 OLDER("http://dbpedia.org/ontology/openingYear,http://dbpedia.org/ontology/birthDate"),
	 OLDEST("http://dbpedia.org/ontology/openingYear,http://dbpedia.org/ontology/birthDate", "DESC"),
	 TALL("http://dbpedia.org/ontology/height"),
	 TALLER("http://dbpedia.org/ontology/height"),
	 TALLEST("http://dbpedia.org/ontology/height", "DESC"),
	 SHORT("http://dbpedia.org/ontology/height"),
	 SHORTER("http://dbpedia.org/ontology/height"),
	 SHORTEST("http://dbpedia.org/ontology/height" , "ASC"),
	 HIGH("http://dbpedia.org/ontology/elevation"),
	 HIGHER("http://dbpedia.org/ontology/elevation,http://dbpedia.org/property/higher"),
	 HIGHEST("http://dbpedia.org/ontology/elevation,http://dbpedia.org/property/highest" , "DESC"),
	 SMALL("http://dbpedia.org/ontology/areaTotal"),
	 SMALLER("http://dbpedia.org/ontology/areaTotal"),
	 SMALLEST("http://dbpedia.org/ontology/areaTotal" , "ASC"),
	 LARGE ("http://dbpedia.org/ontology/areaTotal"),
	 LARGER("http://dbpedia.org/ontology/areaTotal"),
	 LARGEST("http://dbpedia.org/ontology/areaTotal", "DESC"),
	 BIG("http://dbpedia.org/ontology/areaTotal"),
	 BIGGER("http://dbpedia.org/ontology/areaTotal"),
	 BIGGEST("http://dbpedia.org/ontology/areaTotal","DESC");
 
	  private String order;
	  private ArrayList<String>  uri = new ArrayList<String>();
	  
	  Comparison(String pURI){
		   String[] uris = pURI.split(",");
		    for(String u: uris) {
		      uri.add(u);
		    } 
		    
	  }
	  
	  Comparison(String pURI, String pOrder) {
		this.order = pOrder;
	    String[] uris = pURI.split(",");
	    for(String u: uris) {
	      uri.add(u);
	    }   
	}

	public String getURI(int i) {
		return uri.get(i);
	}


	public String getOrder() {
		return this.order;
	}
}
