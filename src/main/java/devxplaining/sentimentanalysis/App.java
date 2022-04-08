package devxplaining.sentimentanalysis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.util.CoreMap;

public class App {

    public static void main(String[] args) throws Exception {
        var text = loadResourceFromClasspath();
        tokenize(text);
        //analyze(text);
        //System.out.println(analyzeAndReturn(text));
    }

    /**
     * Let's tokenize the text using simple API
     * Simple API also stays fast as long as you call fast operations
     * 
     * @param content
     */
    public static void tokenize(String content) {
        var document = new edu.stanford.nlp.simple.Document(content);
        document.sentences().forEach(s -> System.out.println(s.words()));
    }

    public static void analyze(String content) {
        var props = new Properties();
        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        var pipeline = new StanfordCoreNLP(props);
        var annotation = pipeline.process(content);
        annotation.get(CoreAnnotations.SentencesAnnotation.class).forEach(sentence -> {
            var tree = sentence.get(SentimentAnnotatedTree.class);
            var sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
            var sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println(sentimentName + "\t" + sentimentInt + "\t" + sentence);
        });
    }

    record SentimentRecord(String name, int value, String sentence) {
    }

    public static SentimentRecord convertToSentimentRecord(CoreMap sentence) {
        var tree = sentence.get(SentimentAnnotatedTree.class);
        return new SentimentRecord(
                sentence.get(SentimentCoreAnnotations.SentimentClass.class),
                RNNCoreAnnotations.getPredictedClass(tree),
                sentence.toString());
    }

    public static boolean negativeComments(SentimentRecord sentimentRecord) {
        return sentimentRecord.value < 2;
    }

    public static boolean positiveComments(SentimentRecord sentimentRecord) {
        return sentimentRecord.value > 2;
    }

    public static List<SentimentRecord> analyzeAndReturn(String content) {
        var props = new Properties();
        // tokenizer, sentence splitting, consistuency parsing, sentiment analysis
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        var pipeline = new StanfordCoreNLP(props);
        var annotation = pipeline.process(content);
        return annotation.get(CoreAnnotations.SentencesAnnotation.class).stream()
                .map(App::convertToSentimentRecord)
                //.filter(App::negativeComments)
                .filter(App::positiveComments)
                .collect(Collectors.toList());
    }

    /**
     * A tiny util that loads a resource from classpath using getResourceAsStream -
     * that works also in .jar packaged format
     * 
     * @return
     * @throws IOException
     */
    private static String loadResourceFromClasspath() throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream("comments.txt");
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
