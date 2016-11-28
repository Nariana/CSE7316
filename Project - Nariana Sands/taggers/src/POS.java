import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;
import java.io.FileWriter;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class POS {
	public static void main(String [] args){
		String fr = args[0]; //feature request training set e.g. frtrsf.txt or frtrss.txt
		String fa = args[1]; //feature request training set answers e.g. fatrsf.txt or fatrss.txt
		String lr = args[2]; //labeling results training set e.g. lrtrsf.txt or lrtrss.txt
		
		String request, featureRequest, question, answer;
		try{
			
			List<String> features = Files.readAllLines(Paths.get(fr));
			List<String> template = Files.readAllLines(Paths.get(fa));
			
			FileWriter writer = new FileWriter(lr); 
			
			for(int i = 0; i < features.size(); i++){
				request = features.get(i).substring(0, 2); //e.g. R1
				featureRequest = features.get(i).substring(3); //e.g. "Eat cake"
				question = template.get(i).substring(0, 2);//e.g. Q1
				answer = template.get(i).substring(3);
				features.set(i, processFR(request, featureRequest, question, answer));
				writer.write(features.get(i)+"\n");
			}
			
			writer.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	public static String processFR(String request, String featureRequest, String question, String answer){
//		String request = args[0].trim(); //Number of your feature request
//		String featureRequest = args[1].trim(); //Text for your feature request. Put in quotes.
//		String question = args[2].trim(); //question being answered
//		String answer = args[3].trim(); //answer. Put in quotes.

		System.out.printf("%s %s %s %s\n", request, featureRequest, question, answer);
		//structured representation of answers
		int s1, s2, s3; 
		String s4, s5, s6, s7;
		String result = "";

		s1 = s1(featureRequest, answer);
		if(s1 == -1)
			return "Failed to read feature request. Exiting.";
			
		

		String [] fr /*= featureRequest.split("\\W+")*/; //words in feature request
		String [] a = answer.split("\\W+"); //words in answer
		String [] frPOS; //parts of speech for feature request 

		Properties props = new Properties();
		props.setProperty("ner.useSUTime", "false");
		props.setProperty("annotators", " tokenize, ssplit, pos, lemma, ner, parse, dcoref ");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation(featureRequest);

		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<CoreLabel> tokens;

		// Stanford Core NLP POS
//		for(int i = 0; i < sentences.size(); i++){
			CoreMap sentence = sentences.get(0);
			tokens = sentence.get(TokensAnnotation.class);
			fr = new String[tokens.size()]; 
			frPOS = new String[tokens.size()]; //feature request parts of speech

			for(int j = 0; j < tokens.size(); j++){
				CoreLabel token = tokens.get(j); //get each token
				fr[j] = token.get(TextAnnotation.class); //get each word
				frPOS[j] = token.get(PartOfSpeechAnnotation.class); //get each POS
				//System.out.print(fr[j] + "/" + frPOS[j] + " "); //for testing output of POS
			}

			s2 = s2(fr, a);
			s3 = s3(a);
			s4 = s4(frPOS,s2-1); //remember that we started index at 1
			s5 = s5(frPOS, s2-1, s3);
			s6 = s6(frPOS, s2-1);
			s7 = s7(frPOS, s2-1, s3);
//			System.out.printf("R%s\tQ%s\t%s\t%d\t%d\t%d\t%s\t%s\t%s\t%s\n", request, question, answer,s1,s2,s3,s4,s5,s6,s7);
			result = String.format("R%s\tQ%s\t%s\t%d\t%d\t%d\t%s\t%s\t%s\t%s\n", request, question, answer,s1,s2,s3,s4,s5,s6,s7);
			
//		}
		return result;


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

	public static int s3(String [] a){
		return a.length;
	}

	public static String s4(String [] frPOS, int s2){
		return frPOS[s2];
	}

	public static String s5(String [] frPOS, int s2, int s3){
		return frPOS[s2+s3-1];
	}

	public static String s6(String [] frPOS, int s2){
		return frPOS[s2-1];
	}

	public static String s7(String [] frPOS, int s2, int s3){
		if(s2+s3 >= frPOS.length)
			return "nil";
		return frPOS[s2+s3];
	}
}