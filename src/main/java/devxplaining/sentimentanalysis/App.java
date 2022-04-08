package devxplaining.sentimentanalysis;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.google.protobuf.DescriptorProtos.GeneratedCodeInfo.Annotation;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.util.CoreMap;

public class App {

    public static void main(String[] args) throws Exception {
        var is = App.class.getClassLoader().getResourceAsStream("comments.txt");
        var text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        // System.out.println(text);

        var props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        var pipeline = new StanfordCoreNLP(props);
        var annotation = pipeline.process(text);
        System.out.println("Annotation: " + annotation);
        System.out.println("Output:");
        int sentimentInt;
        String sentimentName;
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            var tree = sentence.get(SentimentAnnotatedTree.class);
            sentimentInt = RNNCoreAnnotations.getPredictedClass(tree);
            sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println(sentimentName + "\t" + sentimentInt + "\t" + sentence);
        }

    }
}
