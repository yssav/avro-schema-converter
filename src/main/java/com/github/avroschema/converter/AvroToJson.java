package com.github.avroschema.converter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.avro.Avro2JsonSchemaProcessor;
import com.github.fge.avro.IllegalAvroSchemaException;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class AvroToJson {

    private static final Logger logger = Logger.getLogger(AvroToJson.class.getName());

    /**
     * @param avscFile input file with avro schema
     * @param jsonFile output file to contain the converted avro schema
     */
    public void convertAvroToJson(String avscFile, String jsonFile) {
        try {
            String avscContent = Files.readString(Paths.get(avscFile));
            JsonNode node = JsonLoader.fromString(avscContent);

            // convert avro schema to json schema
            SchemaTree jsonSchema = new Avro2JsonSchemaProcessor().rawProcess(
                    new ListProcessingReport(),
                    new SimpleJsonTree(node)
            );

            // update json schema values
            JsonNode updatedSchema = updateJsonSchema(jsonSchema.getBaseNode());

            // validate updated json schema
            if (JsonSchemaFactory.byDefault().getSyntaxValidator().schemaIsValid(updatedSchema)) {
                writeToFile(jsonSchema.getBaseNode(), jsonFile);
            }
        } catch (IllegalAvroSchemaException e) {
            logger.info(String.format("Invalid avro schema from input file: %s\n%s ", avscFile, e.getMessage()));
        } catch (NoSuchFileException e) {
            logger.info(String.format("Unable to load file: %s", e.getFile()));
        } catch (ProcessingException | JsonParseException e) {
            logger.info(String.format("Unable to process input file: %s\n%s ", avscFile, e.getMessage()));
        } catch (IOException e) {
            logger.info(String.format("Unable to write to output file: %s\n%s ", jsonFile, e.getMessage()));
        }
    }

    /**
     * @param rootNode jsonNode to be updated
     * @return updated JsonNode
     */
    public JsonNode updateJsonSchema(JsonNode rootNode) {
        ObjectNode jsonNode = (ObjectNode) rootNode;

        // TODO update values
        //jsonNode.fieldNames().forEachRemaining(System.out::println);

        return jsonNode;
    }

    /**
     * @param node jsonNode to be written to the output file
     * @param filename filename for output
     * @throws IOException
     */
    public void writeToFile(JsonNode node, String filename) throws IOException {
        Files.writeString(Path.of("output/" + filename), JacksonUtils.prettyPrint(node));
        logger.info("Successfully saved to: " + filename);
    }
}