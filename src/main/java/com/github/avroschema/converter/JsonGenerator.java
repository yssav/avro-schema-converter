package com.github.avroschema.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.github.victools.jsonschema.generator.*;
import example.avro.User;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class JsonGenerator {

    private static final ObjectMapper mapper = new ObjectMapper();

    public void generateViaVictools() {
        try {
            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
                    SchemaVersion.DRAFT_2019_09,
                    OptionPreset.PLAIN_JSON
            );

            SchemaGeneratorConfig config = configBuilder.build();
            SchemaGenerator generator = new SchemaGenerator(config);

            List<Class> classes = getClasses("example.avro");
            for (Class cls : classes) {
                writeToFile(cls.getName() + ".json", generator.generateSchema(cls));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void generateViaJackson() {
        try {
            SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
            mapper.acceptJsonFormatVisitor(User.class, visitor);
            JsonSchema schema = visitor.finalSchema();
            writeToFile("generated-jackson.json", schema);

            JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
            JsonSchema schema2 = schemaGen.generateSchema(User.class);
            writeToFile("generated-jackson-2.json", schema2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String filename, Object node) throws IOException {
        Files.writeString(
                Path.of("output/" + filename),
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node)
        );
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private List<Class> getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        List<File> dirs = new ArrayList();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        ArrayList<Class> classes = new ArrayList();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
        List classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

        /*public void generateJsonSwagger() {
        try {
            Swagger2Module module = new Swagger2Module();
            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
                    SchemaVersion.DRAFT_2019_09,
                    OptionPreset.PLAIN_JSON
            ).with(module);

            SchemaGeneratorConfig config = configBuilder.build();
            SchemaGenerator generator = new SchemaGenerator(config);

            List<Class> classes = getClasses("example.avro");
            for (Class cls : classes) {
                JsonNode jsonSchema = generator.generateSchema(cls);
                Files.writeString(
                        Path.of("output/" + cls.getName() + "-swagger.json"),
                        JacksonUtils.prettyPrint(jsonSchema)
                );
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }*/

}
