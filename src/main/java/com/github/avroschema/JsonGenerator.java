package com.github.avroschema;

import com.example.avro.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonLoader;
import com.github.victools.jsonschema.generator.*;
import example.test.Employee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonGenerator {

    public void generateJsonSchema(String outputFile) {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode jsonSchema = generator.generateSchema(User.class);

        try {
            JsonNode node = JsonLoader.fromString(jsonSchema.toString());
            Files.writeString(Path.of(outputFile), JacksonUtils.prettyPrint(node));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
