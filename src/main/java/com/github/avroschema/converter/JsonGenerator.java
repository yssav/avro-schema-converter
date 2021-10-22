package com.github.avroschema.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.github.victools.jsonschema.generator.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

            List<Class> classes = getClasses("com.equifax.au");
            classes.forEach(cls -> {
                if (!cls.getName().contains("$Builder")) {
                    writeToFile(cls.getName() + ".json", generator.generateSchema(cls));
                }
            });
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void generateViaJackson() {
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        //mapper.acceptJsonFormatVisitor(User.class, visitor);
        JsonSchema schema = visitor.finalSchema();
        writeToFile("generated-jackson.json", schema);

        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
        //JsonSchema schema2 = schemaGen.generateSchema(User.class);
        //writeToFile("generated-jackson-2.json", schema2);
    }

    public void writeToFile(String filename, Object node) {
        try {
            Files.writeString(Path.of("output/" + filename),
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void generateViaConversionPlugin() {
        try {
            String avscContent = Files.readString(Paths.get("src/main/avro/User.avsc"));
            Schema schema = new Schema.Parser().parse(avscContent);
            GenericRecord record = new GenericData.Record(schema);

            AvroToJsonPlugin plugin = new AvroToJsonPlugin();
            initializePlugin(plugin);
            System.out.println(plugin.process(record));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializePlugin(AvroToJsonPlugin avroToJsonPlugin) {
        Map<String, String> field;
        List<Object> fields = new ArrayList<>();

        field = new HashMap<>();
        field.put("mapping", "user.name");
        field.put("field", "name");
        fields.add(field);
        field = new HashMap<>();
        field.put("mapping", "user.favoriteNumber");
        field.put("field", "favorite_number");
        fields.add(field);
        field = new HashMap<>();
        field.put("mapping", "user.favoriteColor");
        field.put("field", "favorite_color");
        fields.add(field);

        Map<String, Object> configuration = new HashMap<>();
        configuration.put("fields", fields);
        configuration.put("recordClass", "example.avro.User");

        avroToJsonPlugin.setConfig(configuration);
        avroToJsonPlugin.init();
    }*/

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
