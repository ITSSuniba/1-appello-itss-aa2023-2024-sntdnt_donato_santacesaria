
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.example.RandomNumberGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class RandomNumberGeneratorAutomateTestCase {
    @ParameterizedTest
    @CsvSource(value = {
            "LOW LIMIT, UP LIMIT, #ELEMENTS",
            "null,10,5", //T1: lowerLimit è null
            ",10,5", //T2: lowerLimit è empty
            "1,null,5",//T3: upperLimit è null
            "1,,5",//T4: upperLimit è empty
            "1,10,null",//T5: numCount è null
            "1,10,",//T6: numCount è empty
    }, useHeadersInDisplayName = true)
    void oneElementNullOrEmptyThrowsException(String lower, String upper, String numCount) {
        assertThrows(IllegalArgumentException.class, () ->
                RandomNumberGenerator.generateUniqueRandomNumbersInRange(lower, upper, numCount));
    }
    @ParameterizedTest
    @CsvSource(value = {
            "LOW LIMIT, UP LIMIT, #ELEMENTS",
            "a,2147483648,5",  // T7: solo numCount è String To Int
            "a,10,%",          // T8: solo upperLimit è String To Int
            "1,2147483648,@",  // T9: solo lowerLimit è String To Int
    }, useHeadersInDisplayName = true)
    void oneElementStringToIntThrowsException(String lower, String upper, String numCount) {
        assertThrows(IllegalArgumentException.class, () ->
                RandomNumberGenerator.generateUniqueRandomNumbersInRange(lower, upper, numCount));
    }
    @ParameterizedTest
    @CsvSource(value = {
            "LOW LIMIT, UP LIMIT, #ELEMENTS",
            "a,10,5",           // T10: lowerLimit è Not To Int, upperLimit è String To Int, numCount è String To Int.
            "1,2147483648,5",   // T11: lowerLimit è String To Int, upperLimit è Not To Int, numCount è String To Int.
            "1,10,@"            // T12: lowerLimit è String To Int, upperLimit è String To Int, numCount è Not To Int.
    }, useHeadersInDisplayName = true)
    void twoElementStringToIntThrowsException(String lower, String upper, String numCount) {
        assertThrows(IllegalArgumentException.class, () ->
                RandomNumberGenerator.generateUniqueRandomNumbersInRange(lower, upper, numCount));
    }
    @Test
    void threeElementsStringToIntWithValidIntervalAndValidNumCountReturnSortedArray(){
        int[] result1 = RandomNumberGenerator.generateUniqueRandomNumbersInRange("1", "10", "5");
        assertEquals(5, result1.length); //T13: lowerLimit < upperLimit, 1 ≤ numCount ≤ (upperLimit - lowerLimit +1)
        int[] result2 = RandomNumberGenerator.generateUniqueRandomNumbersInRange("5", "7", "3");
        assertArrayEquals(new int[]{5, 6, 7}, result2); //T14: lowerLimit < upperLimit, numCount = (upperLimit - lowerLimit +1)
        int[] result3 = RandomNumberGenerator.generateUniqueRandomNumbersInRange("1", "10", "1");
        assertEquals(1, result3.length); //T15: lowerLimit < upperLimit, numCount = 1
    }
    @ParameterizedTest
    @CsvSource(value = {
            "LOW LIMIT, UP LIMIT, #ELEMENTS",
            "10,1,5",   // T16: lowerLimit > upperLimit, 1 ≤ numCount ≤ (upperLimit - lowerLimit +1)
            "10,10,1"   // T17: lowerLimit = upperLimit, 1 ≤ numCount ≤ (upperLimit - lowerLimit +1)
    }, useHeadersInDisplayName = true)
    void threeElementsStringToIntWithNOTValidIntervalAndValidNumCountThrowsException(String lower, String upper, String numCount) {
        assertThrows(IllegalArgumentException.class, () ->
                RandomNumberGenerator.generateUniqueRandomNumbersInRange(lower, upper, numCount));
    }
    @ParameterizedTest
    @CsvSource(value = {
            "LOW LIMIT, UP LIMIT, #ELEMENTS",
            "1,10,-5",  // T18: lowerLimit < upperLimit, numCount < 0
            "1,10,0",   // T19: lowerLimit < upperLimit, numCount = 0
            "1,10,11"   // T20: lowerLimit < upperLimit, numCount > (upperLimit - lowerLimit +1)
    }, useHeadersInDisplayName = true)
    void threeElementsStringToIntWithValidIntervalAndNOTValidNumCountThrowsException(String lower, String upper, String numCount) {
        assertThrows(IllegalArgumentException.class, () ->
                RandomNumberGenerator.generateUniqueRandomNumbersInRange(lower, upper, numCount));
    }
    @ParameterizedTest
    @CsvSource(value = {
            "LOW LIMIT, UP LIMIT, #ELEMENTS",
            "' 1',10,5",   // T21: lowerLimit è String To Int + Whitespace, upperLimit è String To Int, CountNum è String To Int.
            "1,' 10',5",   // T22: lowerLimit è String To Int, upperLimit è String To Int + Whitespace, CountNum è String To Int.
            "1,10,' 5'"    // T23: lowerLimit è String To Int, upperLimit è String To Int, CountNum è String To Int + Whitespace.
    }, useHeadersInDisplayName = true)
    void whiteSpaceInOneElementThrowsException(String lower, String upper, String numCount) {
        assertThrows(IllegalArgumentException.class, () ->
                RandomNumberGenerator.generateUniqueRandomNumbersInRange(lower, upper, numCount));
    }

}
