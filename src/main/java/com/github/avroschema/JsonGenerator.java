package com.github.avroschema;

import com.example.avro.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonLoader;
import com.github.victools.jsonschema.generator.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonGenerator {

    public void generateJsonSchema() {
        try {
            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON);
            SchemaGeneratorConfig config = configBuilder.build();
            SchemaGenerator generator = new SchemaGenerator(config);
            JsonNode jsonSchema = generator.generateSchema(User.class); //TODO update to check packages

            ObjectMapper mapper = new ObjectMapper();

            // configure mapper, if necessary, then create schema generator
            JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
            JsonSchema schema = schemaGen.generateSchema(User.class);

            JsonNode node = JsonLoader.fromString(schema.toString());
            Files.writeString(Path.of("generated-schema.json"), JacksonUtils.prettyPrint(node));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
