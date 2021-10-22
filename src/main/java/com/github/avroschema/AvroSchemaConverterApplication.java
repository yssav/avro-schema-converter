package com.github.avroschema;

import com.github.avroschema.converter.AvroToJson;
import com.github.avroschema.converter.JsonGenerator;
import com.github.avroschema.converter.XsdToAvro;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AvroSchemaConverterApplication {

	public static void main(String[] args) {
		AvroSchemaConverterApplication.convertAvscToJson();
		//AvroSchemaConverterApplication.convertXsdToAvsc();
	}

	public static void convertXsdToAvsc() {
		XsdToAvro.convertXsdToAvsc();
	}

	public static void convertAvscToJson() {
		// avro schema to json schema converter
		AvroToJson.convertAvroToJson("src/main/java/com/equifax/au/User.avsc", "generated-fge.json");

		// json converter from avro-generated classes
		JsonGenerator generator = new JsonGenerator();
		generator.generateViaVictools();
		generator.generateViaJackson();

		// avro to json plugin
		//generator.generateViaConversionPlugin();
	}
}
