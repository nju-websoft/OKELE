package common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Constants {
	public static final String POPULAR_FILE_PATH = "./synthetic_data/";
	public static final String TAIL_FILE_PATH = "./real-world_data/";
	public static final String TAIL_COMPARE_FILE_PATH = "./real-world_data_compare/";
	public static final String PRIOR_DATA_FILE_PATH_TAIL = "./prior_data_real-world/";
	public static final String PRIOR_DATA_FILE_PATH_HEAD = "./prior_data_synthetic/";

	public static final String delimiterText =  ",";
	public static final String sourceClaimIdentifierDelimiter = "-";
	public static final String dataItemIdentifierDelimiter = "#";
	public static final String LT = "<";
	public static final String GT = ">";
	public static final int maxIterationCount = 100;//50
	public static final Charset FILE_ENCODING = StandardCharsets.UTF_8;
	
	public static final String FreebaseVirtGraph = "http://www.freebase.com";
	public static final String FreebasePrefix = "PREFIX fb: <http://rdf.freebase.com/ns/>";
	public static final String FREEBASE_NS = "http://rdf.freebase.com/ns";
}
