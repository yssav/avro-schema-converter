package com.github.avroschema;

import com.github.avroschema.converter.AvroToJson;
import com.github.avroschema.converter.JsonGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AvroSchemaConverterApplication {

	public static void main(String[] args) {

		// avro schema to json schema converter
		AvroToJson converter = new AvroToJson();
		converter.convertAvroToJson("src/main/avro/User.avsc", "generated-fge.json");

		// json converter from avro-generated classes
		JsonGenerator generator = new JsonGenerator();
		generator.generateViaVictools();
		generator.generateViaJackson();
	}
}
