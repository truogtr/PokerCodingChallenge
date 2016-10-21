
ABOUT
————————————————
This program is a poker hand classifier. it can categorize hands and pick the best hand out of a group of hands (able to break ties). I did this project as a coding challenge for the Broad Institute. The challenge was to classify 5 card hands, n card hands, and then be able to pick the best hand from a group. The Test.java class includes some tests for each question.

Author: Trevor Truog


COMPILATION INSTRUCTIONS

————————————————

To compile from command line, enter the below command (from root of BroadChallenge directory):

javac -d bin/ -cp src src/Poker/Test.java

warnings may appear.

to run program enter the below command:

java -cp bin poker.Test

————————————————

OUTPUT

After running, you will see test output for question 1,2 and 3.
Output is in the form of multiple four line blocks containing information on the hand being evaluated. The first line shows the cards in the hand (sorted by value), the second line shows the hand’s category, the third line shows the cards used to fulfill that category (for breaking ties between two hands of the same category), and the final line shows any kickers for the given hand (also for breaking ties).

For question 1, I tested each category (in descending order) one time
For question 2, I tested some 3-5 player groups of hands, the winning hand in that group is output. The output here doesn’t really prove much. Please uncomment and see results for comprehensiveQuestionTwoTest() for a detailed test of this programs ability to sort ties of the same category but with different cards and kickers.
For question 3, I tested some hands that may break a five card hand evaluator. For example, I tested a hand with two straights, a hand with a straight flush and a regular flush, a hand with a full house with multiple options for the two-card and three-card slots, etc. Please see comments (in the code) above each hand for details.

All tests are in Test.java in the poker package.
I chose to write the tests manually.

————————————————

EXPLANATION OF FUNCTIONS

A high-level description of the key functions is given here.
See inline comments for more details.

First, note that many of my functions return an EvaluationInfo instance. The EvaluationInfo class is a wrapper class that holds a Category (enum) value, the cards used (JSONArray) and kickers (JSONArray).

evalStraight() simply walks backwards through the sorted cards in hand and returns the first consecutive five-card sequence if one exists.

evalFlush() first checks if the hand is only five cards and evaluates it on this basis to save some time. It then collects the suite counts in the hand for each suite. If there are flushes, we first check for royal or straight flushes since they beat any regular flush. Then if more than one flush exists, the highest ranking one is selected and returned.

evalOther() checks for all the other possible combinations by counting occurrences of cards of each rank in the hand and performing some logic to find the best hand category, the best cards to use in the hand within that category, and the best kickers.
Note: I’m aware that this function has duplicate code and is too long. Also, it could check if the hand was only five cards long and use some tricks to categorize the hand faster. Unfortunately, I ran out of time to do this refactoring.

evalHand() takes a JSON array of cards and uses the above three methods to determine the highest category for a given n card hand. All necessary information for comparisons is returned.

findBestHand() uses the information returned by evalHand() to sort a list of poker hands first by category, then by cards used in that category, and then by kickers. It uses helper methods to do this (similarities between characteristics of hands of different categories are used to determine which method is used for determining the hand order).

The code is in PokerHandEvaluator.java in the poker package.


