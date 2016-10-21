package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.json.JSONArray;

/***
 * evaluate the category of n card poker hands and
 * compare 5 card poker hands to determine the winner
 * @author Trevor Truog
 */
public class PokerHandEvaluator {
	
	// enumeration for all possible card categories
	public static enum Category { 
		HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT, FLUSH,
		FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH, ROYAL_FLUSH;
	}
	
	// takes a JSONArray of n poker cards, gets the final category and tie breaking info and prints it
	// used for answering questions 1 and 3
	public static void printEvaluationInfo(JSONArray hand) {
		
		EvaluationInfo info = evalHand(hand);
		
		System.out.print("Input Hand: ");
		printHand(hand);
		
		System.out.println("Category: " + info.getCategory());
		System.out.print("Cards Used: ");
		
		for(Object card: info.getCardsUsed()) {
			printCardHelper((int) card);
		}
		System.out.println();
		
		System.out.print("Kickers: ");
		
		for(Object card: info.getKickers()) {
			printCardHelper((int) card);

		}
		System.out.println("\n");	
	}

	// determines the final evaluation for a hand using the below helper methods
	// and returns the hand info
	public static EvaluationInfo evalHand(JSONArray hand) {
		
		// get the info from the three helper methods
		EvaluationInfo straight = evalStraight(hand);
		EvaluationInfo flush = evalFlush(hand);
		EvaluationInfo other = evalOther(hand);
		
		// instantiate final info-holding variables
		Category cat = Category.HIGH_CARD;
		JSONArray cardsUsed = new JSONArray();
		JSONArray kickers = new JSONArray();
		
		// check each category from best to worst to determine the highest category
		// set the info based on the first accepted category
		if (flush.getCategory() == Category.ROYAL_FLUSH || flush.getCategory() == Category.STRAIGHT_FLUSH) {
			cat = flush.getCategory();
			cardsUsed = flush.getCardsUsed();
		} else if (other.getCategory() == Category.FOUR_OF_A_KIND || other.getCategory() == Category.FULL_HOUSE) {
			cat = other.getCategory();
			cardsUsed = other.getCardsUsed();
			kickers = other.getKickers();
		} else if (flush.getCategory() == Category.FLUSH) {
			cat = flush.getCategory();
			cardsUsed = flush.getCardsUsed();
		} else if (straight.getCategory() == Category.STRAIGHT) {
			cat = straight.getCategory();
			cardsUsed = straight.getCardsUsed();
		// if none of the above categories are hit 'evalOther' will have found
		// the highest category for this hand
		} else {
			cat = other.getCategory();
			cardsUsed = other.getCardsUsed();
			kickers = other.getKickers();
		}
			
		return new EvaluationInfo(cat, cardsUsed, kickers);
	}
	
	// evaluates a hand to determine whether or not it contains a straight
	private static EvaluationInfo evalStraight(JSONArray hand) {
		
		//ensure that the hand is sorted
		sortByValue(hand);
			
		// Iterate through the array backwards of card ranks
		// keep track of number of consecutive adjacent ranks
		// (increase by 1 or stay the same if there are two cards of the same rank)
		// break if a straight is found
		boolean straight = false;
		int lowNumber = 0;
		int numInARow = 1;
		int value = cardToNumber(hand.getString(hand.length() - 1 ));
		
		for (int i = hand.length() - 2; i >= 0; i--) {
			
			int nextValue = cardToNumber(hand.getString(i));
			
			// if the difference is 1, increase numInARow by 1, if it is 0, keep it the same, otherwise, reset
			if (value - nextValue == 1) { 
				numInARow += 1;
				if (numInARow == 5) {
					straight = true;
					lowNumber = nextValue;
					break;
				}
			} else if (value - nextValue != 0) {
				numInARow = 1;
			}
			
			value = nextValue;
		}
		
		Category cat = straight ? Category.STRAIGHT : Category.HIGH_CARD;
		
		// only return number value to maintain consistency
		JSONArray cardsUsed = new JSONArray();
		
		for(int i = 0; i < 5; i++) {
			cardsUsed.put(lowNumber);
			lowNumber++;
		}
		
		JSONArray cardsUsedFinal = straight ? cardsUsed : new JSONArray();
		
		
		return new EvaluationInfo(cat, cardsUsedFinal, new JSONArray());
		
	}
	
	// evaluates a hand to determine whether or not it contains a
	// royal flush, straight flush, or regular flush
	private static EvaluationInfo evalFlush(JSONArray hand) {
		
		
		// check for flush with a five card hand (skips extra work required for longer hands)
		if (hand.length() == 5) 
			return evalFlushFiveCard(hand);

		ArrayList<JSONArray> flushes = getFlushes(hand);
		
		//if there are no flushes, return high card as category
		if (flushes.size() == 0) 
			return new EvaluationInfo(Category.HIGH_CARD, new JSONArray(), new JSONArray());
		
		
		EvaluationInfo isStraightFlush = evalStraightandRoyalFlush(flushes);

		Category cat = isStraightFlush.getCategory();
		if (cat != Category.HIGH_CARD) 
			return isStraightFlush;

		// if we don't have a straight or royal flush,
		// find the highest ranking flush
		
		// first, trim all flushes to size 5
		// Remove the smallest card in each flush until it is of size 5
		for(JSONArray f : flushes) {
			while (f.length() > 5 ) {
				f.remove(0);
			}
		}
		
		// get default best flush
		JSONArray bestFlush = flushes.get(0);
		
		// if there are more than one flush, get the best flush
		if (flushes.size() > 1) {
			bestFlush = getBestFlush(flushes);
		}

		// only return number value to maintain consistency
		JSONArray cardsUsed = new JSONArray();

		for(Object card : bestFlush)
			cardsUsed.put(cardToNumber((String) card));

		return new EvaluationInfo(Category.FLUSH, cardsUsed, new JSONArray());

	}
	
	// evalFlush() helper method: evaluate a five card hand for a flush
	private static EvaluationInfo evalFlushFiveCard(JSONArray hand){
		sortByValue(hand);
		
		Character suite = cardToNumber(hand.getString(0)) == 10 ?
				hand.getString(0).charAt(2) : hand.getString(0).charAt(1);
		
		for (int i = 1; i < hand.length(); i++) {
			
			Character nextSuite = cardToNumber(hand.getString(i)) == 10 ?
					hand.getString(i).charAt(2) : hand.getString(i).charAt(1);
					
			if (suite.compareTo(nextSuite) != 0) {
				return new EvaluationInfo(Category.HIGH_CARD, new JSONArray(), new JSONArray());
			}
		}
		
		EvaluationInfo helperInfo = evalStraight(hand);
		
		boolean isStraight = helperInfo.getCategory() == Category.STRAIGHT ? true : false;
		
		Category cat = Category.FLUSH;
		
		if (isStraight) cat = Category.STRAIGHT_FLUSH;
		
		if (isStraight && cardToNumber(hand.getString(4)) == 14) cat = Category.ROYAL_FLUSH;
		
		JSONArray cardsUsed = new JSONArray();
		for(Object card : hand)
			cardsUsed.put(cardToNumber((String) card));

		return new EvaluationInfo(cat, cardsUsed, new JSONArray());
		
	}
	
	// evalFlush() helper method: return a list of all possible flushes in the hand
	private static ArrayList<JSONArray> getFlushes(JSONArray hand) {
			// Count the number of time each suite appears in a hand
			ArrayList<JSONArray> counts = new ArrayList<JSONArray>(4);

			for (int i = 0; i < 4; i++) counts.add(new JSONArray());

			for(Object card : hand) {


				char suite  = ((String) card).charAt(1);
				if (((String)card).length() == 3) suite  = ((String) card).charAt(2);

				if (suite == 'H') counts.get(0).put(card);
				else if (suite == 'C') counts.get(1).put(card);
				else if (suite == 'D') counts.get(2).put(card);
				else counts.get(3).put(card);
			}


			//Get each flush (group of 5 or more cards of the same suite) in this hand 
			ArrayList<JSONArray> flushes = new ArrayList<JSONArray>();
			for (JSONArray cardsBySuite : counts) {
				if (cardsBySuite.length() >= 5) {
					sortByValue(cardsBySuite);
					flushes.add(cardsBySuite);
				}
			}
			return flushes;
		}
	
	// evalFlush() helper method: check for straight and royal flush
	private static EvaluationInfo evalStraightandRoyalFlush(ArrayList<JSONArray> flushes) {

		// Determine whether any of these flushes are a straight flush or royal flush
		// picks the best possible straight or royal flush out of all flushes
		Category cat = Category.HIGH_CARD;
		JSONArray cardsUsed = new JSONArray();

		int curHighCard = 0;

		for (int i = 0; i < flushes.size();i++) {
			JSONArray flush = flushes.get(i);

			EvaluationInfo info = evalStraight(flush);

			if (info.getCategory() == Category.STRAIGHT) {

				// only need to look at high card when comparing straights
				if (info.getCardsUsed().getInt(4) > curHighCard) {
					curHighCard = info.getCardsUsed().getInt(4);
					cardsUsed = info.getCardsUsed();
					cat = Category.STRAIGHT_FLUSH;

					if ( curHighCard == 14) {
						cat = Category.ROYAL_FLUSH;
						break;
					}

				}
			}

		}

		return new EvaluationInfo(cat, cardsUsed, new JSONArray());
	}
	
	// evalFlush() helper method: get highest ranking standard flush out of all standard flushes
	private static JSONArray getBestFlush (ArrayList<JSONArray> flushes) {

		// look at the highest card in each flush and compare it
		// with the other highest cards. Remove all flushes
		// with a lower highest card. Iterate to determine ties
		for (int i = 4; i >= 0; i--) {

			ArrayList<Integer> cardAtIndex = new ArrayList<Integer>();

			// get the number of highest card in each flush (then second highest... down to least highest in the case of ties)
			for (JSONArray f : flushes) {
				cardAtIndex.add(cardToNumber(f.getString(i)));
			}

			// find the highest card of each flush at this index
			int maxAtIndex = 0;
			for(int e = 0; e < cardAtIndex.size(); e++) {

				int cur = cardAtIndex.get(e);
				if ( cur > maxAtIndex) {
					maxAtIndex = cur;
				}	
			}


			// remove all flushes from group of potential flushes that do not have the highest card
			ArrayList<Integer> toRemove = new ArrayList<Integer>();
			for(int e = 0; e < flushes.size(); e++) {
				JSONArray cur = flushes.get(e);
				int valAtIndex = cardToNumber(cur.getString(i));
				if (valAtIndex < maxAtIndex) toRemove.add(e);
			}

			// remove all flushes that 'lost' at this iteration
			// go backwards to ensure indexes don't change
			for(int e = toRemove.size()-1; e >= 0; e--) {
				int removeIndex = toRemove.get(e);
				//System.out.println(toRemove.get(e));
				flushes.remove(removeIndex);
			}

		}

		return flushes.get(0);
	}
	
	// determines the highest rank for the hand that is not a straight or a type of flush
	private static EvaluationInfo evalOther(JSONArray hand) {
		
		// create a 'bucket' for each possible rank in order to track how many 
		// cards of each rank appear in the hand
		//using indices 2 through 14
		int[] cardCounts = new int[15];
		
		// populate buckets
		for (Object card : hand) cardCounts[cardToNumber((String) card)]++;
		
		// track categories that this hand contains
		boolean onePair = false;
		boolean twoPair = false;
		boolean threeOfAKind = false;
		boolean fullHouse = false;
		boolean fourOfAKind = false;
		
		// track (highest) rank of card that fulfills this category
		int pairRank = 0;
		int twoPairRank = 0;
		int threeRank = 0;
		int fourRank = 0;
		
		// determine which categories this hand satisfies
		for(int i = 2; i < cardCounts.length-1; i++) {
			
			if (onePair && cardCounts[i] >= 2) {
				twoPair = true;
			}
			
			if (cardCounts[i] >= 2) {

				if (pairRank <= twoPairRank) pairRank = i;
				else twoPairRank = i;
			
				onePair = true;
			
			}	
					
			if (cardCounts[i] >= 3) {
				threeRank = i;
				threeOfAKind = true;	
			}
			
			if (cardCounts[i] >= 4) {
				fourRank = i;
				fourOfAKind = true;
			} 
				
		}
		
		if (threeOfAKind && twoPair) fullHouse = true;
		
		// instantiate return information
		Category cat = Category.HIGH_CARD;
		JSONArray usedCards = new JSONArray();
		JSONArray kickers = new JSONArray();
		
		sortByValue(hand);
		
		// put all the cards in the hand in the list of kickers
		// used cards will be removed
		for (int i = 0; i < hand.length();i++) {
			kickers.put(cardToNumber(hand.getString(i)));
		}
		
		
		// find the highest possible category this hand satisfies using the above information
		// Once that category is found, populate the usedCards and kickers lists
		if(fourOfAKind) {
			
			cat = Category.FOUR_OF_A_KIND;
			
			for(int i = 0; i < 4; i++) usedCards.put(fourRank);
			
			for (int i = kickers.length() - 1; i >= 0;i--) {
				if (kickers.getInt(i) == fourRank) {
					kickers.remove(i);
				}
			}
	
		} else if (fullHouse) {
			
			cat = Category.FULL_HOUSE;
			
			
			
			// threeRank and the maximum of the pair ranks could be the same
			// if they are, use the minimum of the pair ranks instead
			int twos = Math.max(pairRank, twoPairRank);
			if (twos == threeRank) twos =  Math.min(pairRank, twoPairRank);
			
			// make sure cards are inserted in ascending order
			if (twos < threeRank) {
				for(int i = 0; i < 2; i++) usedCards.put(twos);
				for(int i = 0; i < 3; i++) usedCards.put(threeRank);
			} else {
				for(int i = 0; i < 3; i++) usedCards.put(threeRank);
				for(int i = 0; i < 2; i++) usedCards.put(twos);
			}
				
			// no kickers for a full house, so clear array
			kickers = new JSONArray();
		
		} else if (threeOfAKind) {
			
			cat = Category.THREE_OF_A_KIND;
			
			for(int i = 0; i < 3; i++) usedCards.put(threeRank);
			int numRemaining = 3;
			
			for (int i = kickers.length() - 1; i >= 0;i--) {
				if (kickers.getInt(i) == threeRank) {
					kickers.remove(i);
				numRemaining--;
				if(numRemaining == 0) break;
				}
			}

		} else if (twoPair) {
			
			cat = Category.TWO_PAIR;

			for(int i = 0; i < 2; i++) usedCards.put((Integer)Math.min(pairRank, twoPairRank));
			for(int i = 0; i < 2; i++) usedCards.put((Integer)Math.max(pairRank, twoPairRank));
			
			int numRemainingPair1 = 2;
			int numRemainingPair2 = 2;
			
			for (int i = kickers.length() - 1; i >= 0;i--) {
				if (kickers.getInt(i) == pairRank) {
					kickers.remove(i);
					numRemainingPair2--;
				} else if (kickers.getInt(i) == twoPairRank) {
					kickers.remove(i);
					numRemainingPair2--;
				}
				if(numRemainingPair1 == 0 && numRemainingPair2 == 0) break;
			}

		} else if (onePair) {
			
			cat = Category.ONE_PAIR;
			
			for(int i = 0; i < 2; i++) usedCards.put(Math.max(pairRank, twoPairRank));
			int numRemaining = 2;
			
			for (int i = kickers.length() - 1; i >= 0;i--) {
				if (kickers.getInt(i) == Math.max(pairRank, twoPairRank)) {
					kickers.remove(i);
				numRemaining--;
				if(numRemaining == 0) break;
				}
			}
		}

		return new EvaluationInfo(cat, usedCards, kickers);
	}
	
	//prints each card in a hand
	private static void printHand(JSONArray hand) {
		for(Object card : hand) {
			System.out.print(card + " ");
		}
		System.out.println();
	}

	// Sort a hand of cards by card rank in ascending order
	// I used insertion sort because it is simple and we will only be sorting small arrays
	private static void sortByValue(JSONArray cards) {
				
		for (int i = 0; i < cards.length(); i++) {
			int j = i;
			
			while ( j > 0 && cardToNumber(cards.getString(j-1)) > cardToNumber(cards.getString(j))) {
				
				String temp = cards.getString(j);
				cards.put(j, cards.get(j-1));
				cards.put(j-1, temp);
				
				j -= 1;
			}
		}
	}
	
	//takes a card and returns a numerical rank for that card, for comparison purposes
	private static int cardToNumber(String card) {
		
		Character value  = ((String) card).charAt(0);
		
		if (card.length() == 3) return 10;
		else if (Character.isDigit(value)) return Character.getNumericValue(value);
		else if (value == 'J') return 11;
		else if (value == 'Q') return 12;
		else if (value == 'K') return 13;
		else return 14;
		
	}
	
	// converts integers above ten to their face card equivalent  
	private static void printCardHelper(int card) {
		if ( card <= 10) System.out.print(card + " ");
		else if ( card == 11) System.out.print("J ");
		else if ( card == 12) System.out.print("Q ");
		else if ( card == 13) System.out.print("K ");
		else System.out.print("A ");
	}
	
	// A container class to allow returning values of different types
	private static class EvaluationInfo {
		private Category handCategory;
		private JSONArray cardsUsed;
		private JSONArray kickers;
		
		public EvaluationInfo() {
			handCategory = Category.HIGH_CARD;
			cardsUsed = new JSONArray();
			kickers = new JSONArray();
		}
		
		public EvaluationInfo(Category handCategory, JSONArray cardsUsed, JSONArray kickers) {
			this.handCategory = handCategory;
			this.cardsUsed = cardsUsed;
			this.kickers = kickers;
		}
		
		public Category getCategory() {
			return this.handCategory;
		}
		
		public void setCategory(Category handCategory) {
			this.handCategory = handCategory;
		}
		
		public JSONArray getCardsUsed() {
			return cardsUsed;
		}
		// get array of cards
		public void setCardsUsed(JSONArray cardsUsed) {
			this.cardsUsed = cardsUsed;
		}
		
		public JSONArray getKickers() {
			return kickers;
		}
		// get array of cards
		public void setKickers(JSONArray kickers) {
			this.kickers = kickers;
		}	
	}

	// Uses a comparator to sort a list of hands in ascending order
	// if showAll is set to true, it prints the entire list of sorted hands
	// otherwise, just the winning hand is printed
	public static void findBestHand(ArrayList<JSONArray> hands, boolean showAll) {
		
		Collections.sort(hands, new Comparator<JSONArray>() {

			public int compare(JSONArray hand, JSONArray otherHand) {
				return compareHelper(hand, otherHand);
			}
         
        });
		
		
		if (showAll){
			for (JSONArray hand : hands) 
				printEvaluationInfo(hand);	
		} else {
			printEvaluationInfo(hands.get(hands.size()-1));
		}
	}
	
	// sets up information for below helper methods and returns final result
	private static int compareHelper(JSONArray hand, JSONArray otherHand) {	
		
		EvaluationInfo handOne = evalHand(hand);
		EvaluationInfo handTwo = evalHand(otherHand);
		int categoryCompare = handOne.getCategory().compareTo(handTwo.getCategory());
		if(categoryCompare != 0) {
			return categoryCompare;
		} 
		
		Category cat = handOne.getCategory();
		boolean fullHouse = cat == Category.FULL_HOUSE ? true : false;
		boolean twoPair = cat == Category.TWO_PAIR ? true : false;
		boolean fourKind = cat == Category.FOUR_OF_A_KIND ? true : false;
		boolean threeKind = cat == Category.THREE_OF_A_KIND ? true : false;
		boolean onePair = cat == Category.ONE_PAIR ? true : false;
		boolean highCard = cat == Category.HIGH_CARD ? true : false;
		boolean flush = cat == Category.FLUSH ? true : false;

		if (cat == Category.ROYAL_FLUSH) return 0;
		
		if (cat == Category.STRAIGHT_FLUSH || cat == Category.STRAIGHT) {
			return straightFlushStraightTieBreaker(handOne, handTwo);
		}

		if (fullHouse || twoPair) {
			return twoUsedRanksTieBreaker(handOne, handTwo, cat);
		}

		if (fourKind || threeKind || onePair) {
			return oneUsedRankTieBreaker(handOne, handTwo, cat);
		}

		if (highCard || flush) {
			return checkAllFiveTieBreaker(handOne, handTwo, cat);
			
		}
		
		return 0;
	}
	
	// breaks ties between straight flushes and straights
	private static int straightFlushStraightTieBreaker(EvaluationInfo handOne, EvaluationInfo handTwo){
		Integer handOneHighCard = handOne.getCardsUsed().getInt(4);
		Integer handTwoHighCard = handTwo.getCardsUsed().getInt(4);
		
		return handOneHighCard.compareTo(handTwoHighCard);
	}
	
	// breaks ties between hands that require cards of two different ranks
	private static int twoUsedRanksTieBreaker(EvaluationInfo handOne, EvaluationInfo handTwo, Category cat) {
		boolean fullHouse = cat == Category.FULL_HOUSE ? true : false;
		boolean twoPair = cat == Category.TWO_PAIR ? true : false;
		
		Integer handOneHighCard = handOne.getCardsUsed().getInt(3);
		Integer handTwoHighCard = handTwo.getCardsUsed().getInt(3);
		Integer highCardComparison = handOneHighCard.compareTo(handTwoHighCard);
		
		if (highCardComparison != 0) {
			return highCardComparison;
		}
		
		Integer handOneLowCard = handOne.getCardsUsed().getInt(0);
		Integer handTwoLowCard = handTwo.getCardsUsed().getInt(0);
		Integer lowCardComparison = handOneLowCard.compareTo(handTwoLowCard);
			
		if (lowCardComparison != 0) {
			return lowCardComparison;
		} if (fullHouse) {
			return 0;
		}
		// kicker for two pair
		Integer handOneKicker = handOne.getKickers().getInt(0);
		Integer handTwoKicker = handTwo.getKickers().getInt(0);
		Integer kickerComparison = handOneKicker.compareTo(handTwoKicker);
		
		return kickerComparison;
		
		//return lowCardComparison;
		//else return highCardComparison;
	}
	
	// breaks ties between hands that require cards of one rank
	private static int oneUsedRankTieBreaker(EvaluationInfo handOne, EvaluationInfo handTwo, Category cat) {
		
		boolean fourKind = cat == Category.FOUR_OF_A_KIND ? true : false;
		boolean threeKind = cat == Category.THREE_OF_A_KIND ? true : false;
		boolean onePair = cat == Category.ONE_PAIR ? true : false;
		
		// check highCard
		Integer handOneHighCard = handOne.getCardsUsed().getInt(0);
		Integer handTwoHighCard = handTwo.getCardsUsed().getInt(0);
		Integer highCardComparison = handOneHighCard.compareTo(handTwoHighCard);

		if (highCardComparison == 0) {

			JSONArray handOneKickers = handOne.getKickers();
			JSONArray handTwoKickers = handTwo.getKickers();
			int handOneKickersLength = handOneKickers.length();
			int handTwoKickersLength = handOneKickers.length();

			Integer handOneFirstKicker = handOneKickers.getInt(handOneKickersLength-1);
			Integer handTwoFirstKicker = handTwoKickers.getInt(handTwoKickersLength-1);
			Integer firstKickerComparison = handOneFirstKicker.compareTo(handTwoFirstKicker);

			if (firstKickerComparison != 0) {
				return firstKickerComparison;
			} else if (fourKind)
			{
				return 0;
			}

			Integer handOneSecondKicker = handOneKickers.getInt(handOneKickersLength-2);
			Integer handTwoSecondKicker = handTwoKickers.getInt(handTwoKickersLength-2);
			Integer secondKickerComparison = handOneSecondKicker.compareTo(handTwoSecondKicker);

			if (secondKickerComparison != 0) {
				return secondKickerComparison;
			} if (threeKind) {
				return 0;
			}

			Integer handOneThirdKicker = handOneKickers.getInt(handOneKickersLength-3);
			Integer handTwoThirdKicker = handTwoKickers.getInt(handTwoKickersLength-3);
			Integer thirdKickerComparison = handOneThirdKicker.compareTo(handTwoThirdKicker);

			if (thirdKickerComparison != 0) {
				return thirdKickerComparison;
			} else return 0;

		} else return highCardComparison;
	}

	// breaks ties between flushes and high card, where potentially every card needs to be checked
	private static int checkAllFiveTieBreaker(EvaluationInfo handOne, EvaluationInfo handTwo, Category cat){
		
		boolean highCard = cat == Category.HIGH_CARD ? true : false;
		boolean flush = cat == Category.FLUSH ? true : false;
		
		JSONArray handOneArray;
		JSONArray handTwoArray;
		
		if (highCard) {
			handOneArray = handOne.getKickers();
			handTwoArray = handTwo.getKickers();
		} else {
			handOneArray = handOne.getCardsUsed();
			handTwoArray = handTwo.getCardsUsed();
		}
		
		Integer handOneValue;
		Integer handTwoValue;
		Integer valueComparison;
		for (int i = 4; i >= 0; i--) {
			handOneValue = handOneArray.getInt(i);
			handTwoValue = handTwoArray.getInt(i);
			valueComparison = handOneValue.compareTo(handTwoValue);
			
			if (valueComparison != 0) {
				return valueComparison;
			}	
		}
		
		return 0;
		
	}
	
}
