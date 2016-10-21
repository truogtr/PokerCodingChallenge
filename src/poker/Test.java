package poker;

import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;


public class Test {
	
public static void main(String[] argv) {
		// run tests for each question
		//questionOneTests();
		//questionTwoTests();
		//questionThreeTests();
		//comprehensiveQuestionTwoTest();
		
		
		JSONArray straight = new JSONArray(Arrays.asList(new String[]{"9X", "WH", "KS", "JD", "QH"}));
		PokerHandEvaluator.printEvaluationInfo(straight);
		
		
		
	}

	// tests the evaluator's ability to correctly categorize five card poker hands and print
	// the hand's category as well as its tie breaking information (rank of cards in hand, kickers)
	// each category is tested once in descending order
	public static void questionOneTests() {

		System.out.println("\nQUESTION ONE TESTS\n");
		
		JSONArray royalFlush = new JSONArray(Arrays.asList(new String[]{"KS", "AS", "JS", "10S", "QS"}));
		PokerHandEvaluator.printEvaluationInfo(royalFlush);
		
		JSONArray straightFlush = new JSONArray(Arrays.asList(new String[]{"8C", "4C", "6C", "5C", "7C"}));
		PokerHandEvaluator.printEvaluationInfo(straightFlush);
		
		JSONArray fourOfAKind = new JSONArray(Arrays.asList(new String[]{"7D", "8H", "7S", "7H", "7D"}));
		PokerHandEvaluator.printEvaluationInfo(fourOfAKind);
		
		JSONArray fullHouse = new JSONArray(Arrays.asList(new String[]{"3D", "9H", "3S", "3H", "9D"}));
		PokerHandEvaluator.printEvaluationInfo(fullHouse);
		
		JSONArray flush = new JSONArray(Arrays.asList(new String[]{"2H", "4H", "6H", "5H", "7H"}));
		PokerHandEvaluator.printEvaluationInfo(flush);
		
		JSONArray straight = new JSONArray(Arrays.asList(new String[]{"9C", "10H", "KS", "JD", "QH"}));
		PokerHandEvaluator.printEvaluationInfo(straight);
		
		JSONArray threeOfAKind = new JSONArray(Arrays.asList(new String[]{"JC", "10H", "JS", "JD", "QH"}));
		PokerHandEvaluator.printEvaluationInfo(threeOfAKind);
		
		JSONArray twoPair = new JSONArray(Arrays.asList(new String[]{"KC", "KH", "JS", "QD", "QH"}));
		PokerHandEvaluator.printEvaluationInfo(twoPair);
		 
		JSONArray onePair = new JSONArray(Arrays.asList(new String[]{"4H", "6D", "KH", "KC", "5C"}));
		PokerHandEvaluator.printEvaluationInfo(onePair);
		
		JSONArray highCard = new JSONArray(Arrays.asList(new String[]{"4H", "6D", "QH", "JC", "5C"}));
		PokerHandEvaluator.printEvaluationInfo(highCard);
		
		System.out.println("------------------------------------------------------------");
	}
	
	public static void questionTwoTests(){
		
		System.out.println("\nQUESTION TWO TESTS\n");
		
		ArrayList<JSONArray> hands = new ArrayList<JSONArray>();
		
		// some five card hands
		JSONArray twoPair1 = new JSONArray(Arrays.asList(new String[]{"KC", "KH", "10S", "QD", "QH"})); 
		JSONArray twoPair2 = new JSONArray(Arrays.asList(new String[]{"KC", "QH", "JS", "JD", "QH"}));  
		JSONArray onePair1 = new JSONArray(Arrays.asList(new String[]{"7D", "4K", "QH", "10H", "7D"}));
		JSONArray straight1 = new JSONArray(Arrays.asList(new String[]{"9C", "10H", "KS", "JD", "QH"}));
		JSONArray straight2 = new JSONArray(Arrays.asList(new String[]{"QH", "10H", "9S", "JH", "8H"}));
		JSONArray flush1 = new JSONArray(Arrays.asList(new String[]{"2S", "8S", "6S", "5S", "3S"}));
		JSONArray highCard1 = new JSONArray(Arrays.asList(new String[]{"3H", "6D", "QH", "JC", "5C"}));
		JSONArray threeOfAKind1 = new JSONArray(Arrays.asList(new String[]{"7D", "4H", "5S", "7H", "7D"}));
		JSONArray flush2 = new JSONArray(Arrays.asList(new String[]{"4S", "8S", "6S", "5S", "3S"}));
		
		// find best hand of these five hands
		hands.add(threeOfAKind1);
		hands.add(straight2);
		hands.add(twoPair1);
		hands.add(highCard1);
		hands.add(straight1);
		
		PokerHandEvaluator.findBestHand(hands, false);	
		
		hands = new ArrayList<JSONArray>();
		
		hands.add(threeOfAKind1);
		hands.add(twoPair2);
		hands.add(twoPair1);
		hands.add(highCard1);
		hands.add(onePair1);
		
		PokerHandEvaluator.findBestHand(hands, false);	
		
		hands = new ArrayList<JSONArray>();
		
		hands.add(twoPair2);
		hands.add(twoPair1);
		hands.add(highCard1);
		hands.add(onePair1);
		
		PokerHandEvaluator.findBestHand(hands, false);	
		
		hands = new ArrayList<JSONArray>();
		
		hands.add(straight1);
		hands.add(flush2);
		hands.add(straight2);
		hands.add(flush1);
				
		PokerHandEvaluator.findBestHand(hands, false);	
		
		System.out.println("------------------------------------------------------------");
	}
	
	public static void questionThreeTests() {
		
		System.out.println("\nQUESTION THREE TESTS\n");
		
		// two flushes with more than five cards each tied by card rank down
		// to the last card (out of best five cards): picks the better flush
		JSONArray twoFlushes = new JSONArray(Arrays.asList(new String[]
				{"3H", "4H", "7H", "6H", "8H", "10H", "10S", "8S", "7S", "6S", "5S", "3S"}));
		PokerHandEvaluator.printEvaluationInfo(twoFlushes);
		
		
		// straight flush and a regular flush, where the straight flush's high card
		// is lower than the regular flush's high card: picks straight flush
		JSONArray RegAndStraightFlush = new JSONArray(Arrays.asList(new String[]
				{"2H", "3H", "4H", "6H", "5H", "AS", "JS", "QS", "KS", "9S"}));
		PokerHandEvaluator.printEvaluationInfo(RegAndStraightFlush);
		
		// straight flush with more than 5 of a suite, returns the best 5 cards
		// (works with straight and regular flush)
		JSONArray flushMoreThanFive = new JSONArray(Arrays.asList(new String[]
				{"2H", "3H", "4H", "6H", "5H", "7H"}));
		PokerHandEvaluator.printEvaluationInfo(flushMoreThanFive);

		//hand with two straights: picks the higher straight
		JSONArray twoStraights = new JSONArray(Arrays.asList(new String[]
				{"9C", "10S", "2H", "3H", "4D", "6H", "5H", "KS", "JS", "QD"}));
		PokerHandEvaluator.printEvaluationInfo(twoStraights);
		
		//hand with a straight but with duplicate cards of the same rank: picks straight
		JSONArray duplicateCardStraight = new JSONArray(Arrays.asList(new String[]
				{"3H", "4D", "6H", "5H", "5S", "6S", "7D"}));
		PokerHandEvaluator.printEvaluationInfo(duplicateCardStraight);
		
		// Below tests demonstrate that when multiple combinations are present
		// the best category and the best cards within that category are chosen
		JSONArray twoFourKind = new JSONArray(Arrays.asList(new String[]
				{"4C", "4S", "KS", "4H", "4D", "KD", "KH", "KC", "5C"}));
		PokerHandEvaluator.printEvaluationInfo(twoFourKind);
		
		JSONArray multiFullHouse = new JSONArray(Arrays.asList(new String[]
				{"4C", "7H", "KS", "4H", "4D", "KH", "KC", "5C", "5H", "7C"}));
		PokerHandEvaluator.printEvaluationInfo(multiFullHouse);
		
		JSONArray multiTwoPair = new JSONArray(Arrays.asList(new String[]
				{"7H", "4H", "4D", "KH", "KC", "5C", "5H", "7C"}));
		PokerHandEvaluator.printEvaluationInfo(multiTwoPair);
		
		System.out.println("------------------------------------------------------------");
		

	}
	
	public static void comprehensiveQuestionTwoTest() {
			
		System.out.println("\nCOMPREHENSIVE QUESTION TWO TESTS\n");
		ArrayList<JSONArray> hands = new ArrayList<JSONArray>();
		
		// The below groups of hands are designed to test many ways that two hands of the same
		// category can win or lose to each other (it also shows that hands of different categories are correctly ordered
		// for example, with the two pair category, the following cases are tested:
		// hands with different first cards
		// hands with the same first card, but different second card
		// hands with the same first and second cards, but different kicker
		
		// K, Q, 10 kicker
		JSONArray twoPair1 = new JSONArray(Arrays.asList(new String[]{"KC", "KH", "10S", "QD", "QH"})); 
		// Q, J (test first card different)
		JSONArray twoPair2 = new JSONArray(Arrays.asList(new String[]{"KC", "QH", "JS", "JD", "QH"}));  
		// K, Q, J kicker (test kicker different)
		JSONArray twoPair3 = new JSONArray(Arrays.asList(new String[]{"KC", "KH", "JS", "QD", "QH"}));  
		// K, J (test second card different)
		JSONArray twoPair4 = new JSONArray(Arrays.asList(new String[]{"KC", "KH", "JS", "JD", "QH"}));  
		
		JSONArray straightFlush1 = new JSONArray(Arrays.asList(new String[]{"8C", "4C", "6C", "5C", "7C"}));
		JSONArray straightFlush2 = new JSONArray(Arrays.asList(new String[]{"8S", "7S", "6S", "5S", "9S"}));
		JSONArray royalFlush = new JSONArray(Arrays.asList(new String[]{"KS", "AS", "JS", "10S", "QS"}));
		
		JSONArray fullHouse1 = new JSONArray(Arrays.asList(new String[]{"KC", "KH", "KS", "QD", "QH"}));
		JSONArray fullHouse2 = new JSONArray(Arrays.asList(new String[]{"KC", "KH", "JS", "JD", "KH"}));
		JSONArray fullHouse3 = new JSONArray(Arrays.asList(new String[]{"QC", "QH", "JS", "JD", "QH"}));

		JSONArray fourOfAKind4 = new JSONArray(Arrays.asList(new String[]{"8D", "8H", "7S", "8H", "8D"}));
		JSONArray fourOfAKind1 = new JSONArray(Arrays.asList(new String[]{"7D", "8H", "7S", "7H", "7D"}));
		JSONArray fourOfAKind2 = new JSONArray(Arrays.asList(new String[]{"7D", "8H", "7S", "7H", "7D"}));
		JSONArray fourOfAKind3 = new JSONArray(Arrays.asList(new String[]{"7D", "AH", "7S", "7H", "7D"}));
		
		JSONArray threeOfAKind1 = new JSONArray(Arrays.asList(new String[]{"7D", "4H", "5S", "7H", "7D"}));
		JSONArray threeOfAKind2 = new JSONArray(Arrays.asList(new String[]{"7D", "5H", "3S", "7H", "7D"}));
		JSONArray threeOfAKind3 = new JSONArray(Arrays.asList(new String[]{"7D", "AH", "7S", "2H", "7D"}));
		JSONArray threeOfAKind4 = new JSONArray(Arrays.asList(new String[]{"8D", "8H", "2S", "8H", "3D"}));
		
		JSONArray straight1 = new JSONArray(Arrays.asList(new String[]{"9C", "10H", "KS", "JD", "QH"}));
		JSONArray straight2 = new JSONArray(Arrays.asList(new String[]{"AH", "10H", "KS", "JH", "QH"}));
		
		JSONArray onePair1 = new JSONArray(Arrays.asList(new String[]{"7D", "JH", "10S", "QH", "7D"}));
		JSONArray onePair2 = new JSONArray(Arrays.asList(new String[]{"7D", "4K", "QH", "10H", "7D"}));
		JSONArray onePair3 = new JSONArray(Arrays.asList(new String[]{"7D", "AK", "2H", "3H", "7D"}));
		JSONArray onePair4 = new JSONArray(Arrays.asList(new String[]{"8D", "8H", "2S", "3H", "6D"}));
		JSONArray onePair5 = new JSONArray(Arrays.asList(new String[]{"7D", "3H", "QH", "10H", "7D"}));
		
		JSONArray flush1 = new JSONArray(Arrays.asList(new String[]{"8H", "6H", "4H", "5H", "3H"}));
		JSONArray flush2 = new JSONArray(Arrays.asList(new String[]{"2S", "8S", "6S", "5S", "3S"}));

		JSONArray highCard1 = new JSONArray(Arrays.asList(new String[]{"3H", "6D", "QH", "JC", "5C"}));
		JSONArray highCard2 = new JSONArray(Arrays.asList(new String[]{"4H", "6D", "QH", "JC", "3C"}));
		
		
		hands.add(twoPair1);
		hands.add(twoPair2);
		hands.add(twoPair3);
		hands.add(twoPair4);
		
		hands.add(fullHouse1);
		hands.add(fullHouse2);
		hands.add(fullHouse3);
		
		hands.add(straightFlush1);
		hands.add(straightFlush2);
		hands.add(royalFlush);
		
		hands.add(fourOfAKind1);
		hands.add(fourOfAKind2);		
		hands.add(fourOfAKind3);
		hands.add(fourOfAKind4);
		
		hands.add(straight1);
		hands.add(straight2);
		
		hands.add(threeOfAKind1);
		hands.add(threeOfAKind2);		
		hands.add(threeOfAKind3);
		hands.add(threeOfAKind4);
		
		hands.add(onePair1);
		hands.add(onePair2);		
		hands.add(onePair3);
		hands.add(onePair4);
		hands.add(onePair5);
		
		hands.add(flush1);
		hands.add(flush2);		
		hands.add(highCard1);
		hands.add(highCard2);

		PokerHandEvaluator.findBestHand(hands, true);	 
	}

	
}
