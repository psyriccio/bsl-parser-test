package bsl.parser.test;

import c1c.bsl.parser.BSLParser;
import ch.qos.logback.classic.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import static org.parboiled.errors.ErrorUtils.printParseErrors;
import org.parboiled.parserunners.TracingParseRunner;
import org.parboiled.support.ParsingResult;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger("~");

    public static void main(String[] args) {

        LOG.info("BSLParser test");
        LOG.info("Creating parser instance");
        BSLParser parser = Parboiled.createParser(BSLParser.class);
        Rule rootRule = parser.CompilationUnit().suppressNode();
        LOG.info("Loading ./test.bsl");
        String test = readAllText(new File("test.bsl"));
        ParsingResult<?> result = null;
        try {
            result = run(rootRule, test);
        } catch (Exception e) {
            LOG.error("Exception while parsing file {}:{}", "test.bsl", e);
            System.exit(1);
        }
        if (!result.matched) {
            LOG.error("Parse error(s) in file {}:{}", "test.bsl", printParseErrors(result));
            System.exit(1);
        } else {
            System.out.print('.');
        }
    }

    public static ParsingResult<?> run(Rule rootRule, String sourceText) {
        return new TracingParseRunner(rootRule).run(sourceText);
    }
    
    public static String readAllText(File file) {
        return readAllText(file, Charset.forName("UTF8"));
    }

    public static String readAllText(File file, Charset charset) {
        try {
            return readAllText(new FileInputStream(file), charset);
        } catch (FileNotFoundException e) {
            LOG.error("Exception: {}", e);
            return null;
        }
    }

    public static String readAllText(InputStream stream, Charset charset) {
        if (stream == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
        StringWriter writer = new StringWriter();
        copyAll(reader, writer);
        return writer.toString();
    }

    public static void copyAll(Reader reader, Writer writer) {
        try {
            char[] data = new char[4096]; // copy in chunks of 4K
            int count;
            while ((count = reader.read(data)) >= 0) {
                writer.write(data, 0, count);
            }

            reader.close();
            writer.close();
        } catch (IOException e) {
            LOG.error("Exception: {}", e);
        }
    }

}
