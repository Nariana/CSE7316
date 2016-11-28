import java.util.List;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class POS {
	public static void main(String [] args){
		String featureRequest = args[0]; //Your feature request
		String [] fr = featureRequest.split("\\W+");
		String answer = args[1]; //answer 
		String [] a = answer.split("\\W+");
		int s1, s2, s3; //structured representation of answers

		s1 = s1(featureRequest, answer);
		if(s1 != -1){

			s2 = s2(fr, a);
			s3 = a.length;
			System.out.println(s1 + ", " + s2 + ", " + s3);

			Properties props = new Properties();
			props.setProperty("ner.useSUTime", "false");
			props.setProperty("annotators", " tokenize, ssplit, pos, lemma, ner, parse, dcoref ");
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			
			Annotation document = new Annotation(featureRequest);
			pipeline.annotate(document);
			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			List<CoreLabel> tokens;
			for (CoreMap sentence : sentences) {
				tokens = sentence.get(TokensAnnotation.class);
				for (CoreLabel token : tokens) {
					String pos = token.get(PartOfSpeechAnnotation.class);//Get POS of each word
					System.out.print(token + "/" + pos + " ");
				}
			}
		}
	}

	public static int s1(String featureRequest, String answer){
		if(Pattern.compile(Pattern.quote(answer), Pattern.CASE_INSENSITIVE).matcher(featureRequest).find())
			return 1; //should be 1 for every sentence
		return -1; //error
	}

	public static int s2(String [] fr, String [] a){
		int s2 = Arrays.asList(fr).indexOf(a[0]);
		if (s2 != -1)
			return s2 +1; //start index at 1
		return s2; //returns -1: error, not found
	}
}