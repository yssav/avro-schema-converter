package com.github.avroschema;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AvroSchemaConverterApplication {

	public static void main(String[] args) {
		// TODO update args handling
//		if (args.length >= 2) {
//			AvroToJson converter = new AvroToJson();
//			converter.convertAvroToJson(args[0], args[1]);
//		}

		JsonGenerator generator = new JsonGenerator();
		generator.generateJsonSchema();
	}
}
