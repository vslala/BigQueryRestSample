package com.example.bigquery;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.api.services.bigquery.model.JobReference;
import com.google.cloud.bigquery.BigQueryError;
import com.google.cloud.bigquery.Schema;

public class BigQueryResponseVO {
	@JsonProperty
	String kind;
	@JsonProperty
	String etag;
	@JsonProperty
	Schema schema;
	@JsonProperty
	JobReference jobReference;
	@JsonProperty
	long totalRows;
	@JsonProperty
	String pageToken;
	@JsonProperty
	boolean cacheHit;
	@JsonProperty
	long totalBytesProcessed;
	@JsonProperty
	List<BigQueryError> errors;
	@JsonProperty
	long numDmlAffectedRows;
	
	
}
