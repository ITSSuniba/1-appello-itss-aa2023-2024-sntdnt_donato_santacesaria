import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.statistics.Histogram;
import net.jqwik.api.statistics.Statistics;
import net.jqwik.api.statistics.StatisticsReport;
import org.example.RandomNumberGenerator;
import net.jqwik.api.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyBasedTest {
/*1. PROPRIETA' RELATIVE GLI INPUT*/

/*1.a) Se anche uno solo degli input passati non è valido (i.e. non è una stringa che possa essere convertita in intero),
il programma deve lanciare un’eccezione; */
    @Provide
    Arbitrary<String> nonIntString() {
        return Arbitraries.strings().ascii().filter(s -> !s.matches("\\d+"));
    }

    /*1.a).I Limite inferiore non valido; */
    @Property(generation = GenerationMode.RANDOMIZED)
    @StatisticsReport(format = Histogram.class)
    void invalidLowerLimitThrowsException(
            @ForAll("nonIntString") String lowerLimit,
            @ForAll @IntRange int upperLimit,
            @ForAll @IntRange (min=0, max=4000000)int numCount
    ) {
        assertThrows(IllegalArgumentException.class, () -> {
            RandomNumberGenerator.generateUniqueRandomNumbersInRange(lowerLimit,String.valueOf(upperLimit), String.valueOf(numCount));
        });
        Statistics.collect(lowerLimit);
    }
    /*1.a).II Limite superiore non valido; */
    @Property(generation = GenerationMode.RANDOMIZED)
    @StatisticsReport(format = Histogram.class)
    void invalidUpperLimitThrowsException(
            @ForAll @IntRange  int lowerLimit,
            @ForAll ("nonIntString") String upperLimit,
            @ForAll @IntRange (min=0, max=4000000)int numCount
    ) {
        assertThrows(IllegalArgumentException.class, () -> {
            RandomNumberGenerator.generateUniqueRandomNumbersInRange(String.valueOf(lowerLimit),upperLimit, String.valueOf(numCount));
        });
        Statistics.collect(upperLimit);
    }
    /*1.a).III Numero di elementi da generare non valido; */
    @Property(generation = GenerationMode.RANDOMIZED)
    @StatisticsReport(format = Histogram.class)
    void invalidNumCountThrowsException(
            @ForAll @IntRange  int lowerLimit,
            @ForAll @IntRange  int upperLimit,
            @ForAll ("nonIntString") String numCount
    ) {
        assertThrows(IllegalArgumentException.class, () -> {
            RandomNumberGenerator.generateUniqueRandomNumbersInRange(String.valueOf(lowerLimit),String.valueOf(upperLimit), numCount);
        });
        Statistics.collect(numCount);
    }
/*1.b) Se l'intervallo passato non è valido (i.e. il limite inferiore è maggiore del limite superiore),
il programma deve lanciare un’eccezione; */
    @Property(generation = GenerationMode.RANDOMIZED)
    @StatisticsReport(format = Histogram.class)
    void invalidIntervalThrowsException(
            @ForAll @IntRange int lowerLimit,
            @ForAll @IntRange int upperLimit,
            @ForAll @IntRange int numCount
    ){
        Assume.that(lowerLimit > upperLimit);
        assertThrows(IllegalArgumentException.class, () -> {
            RandomNumberGenerator.generateUniqueRandomNumbersInRange(String.valueOf(lowerLimit),String.valueOf(upperLimit), String.valueOf(numCount));
        });
        String range=lowerLimit<=upperLimit+1?"test su valori boundary vicini ad upperLimit":
                "test su altri scenari";
        Statistics.label("range").collect(range);
        Statistics.label("value").collect(lowerLimit, upperLimit);
    }


/*1.c) Se il numero di elementi da generare non è valido (i.e. minore o uguale a zero o superiore alla dimensione+1 dell'intervallo),
il programma deve lanciare un’eccezione; */
    @Property(generation = GenerationMode.RANDOMIZED)
    @StatisticsReport(format = Histogram.class)
    void invalidElementsToGenerateThrowsException(
            @ForAll @IntRange int lowerLimit,
            @ForAll @IntRange int upperLimit,
            @ForAll @IntRange int numCount
    ){
        Assume.that(lowerLimit < upperLimit);
        Assume.that(numCount <= 0 || numCount>(upperLimit-lowerLimit+1));

        assertThrows(IllegalArgumentException.class, () -> {
            RandomNumberGenerator.generateUniqueRandomNumbersInRange(String.valueOf(lowerLimit),
                    String.valueOf(upperLimit), String.valueOf(numCount));
        });

        String range;
        if (numCount == 0) {
            range = "test su valori boundary vicini a 0";
        } else if (numCount == (upperLimit - lowerLimit+2)) {
            range = "test su valori boundary vicini a dimensione intervallo";
        } else {
            range = "test su altri valori";
        }
        Statistics.label("range").collect(range);
        Statistics.label("value").collect(lowerLimit, upperLimit, numCount);

    }
    /*2. PROPRIETA' RELATIVE L'OUTPUT*/

    /*2.a) L’output generato deve essere un array di dimensione pari al numero di elementi da generare passato al programma; */

    @Property(generation = GenerationMode.RANDOMIZED)
    @StatisticsReport(format = Histogram.class)
    void generatedArrayShouldHaveLengthDefinedByTheUser(
            @ForAll @IntRange int lowerLimit,
            @ForAll @IntRange int upperLimit,
            @ForAll @IntRange (max=4000000)int numCount //si imposta un limite superiore per evitare errori di memoria/tempi di esecuzione
            ) {
        // si assicura che l'intervallo  che il numero di elementi da generare siano validi
        Assume.that(lowerLimit < upperLimit);
        Assume.that(numCount>0 && numCount<=(upperLimit-lowerLimit+1));

        int[] result = RandomNumberGenerator.generateUniqueRandomNumbersInRange(
                String.valueOf(lowerLimit),
                String.valueOf(upperLimit),
                String.valueOf(numCount)
        );
        assertEquals(numCount, result.length, "La lunghezza dell'array non è uguale al numero specificato");
        Statistics.collect(result.length, numCount);

        String range;
        if (numCount == 1) {
            range = "test su valori boundary di NumCount vicini a 0";
        } else if (numCount == (upperLimit - lowerLimit+1)) {
            range = "test su valori boundary di NumCount vicini a dimensione intervallo";
        }else if(lowerLimit==(upperLimit-1)){
            range="test su valori boundary dell'intervallo";
        }
        else {
            range = "test su altri valori";
        }
        Statistics.label("range").collect(range);
        Statistics.label("value").collect(lowerLimit, upperLimit, numCount);
    }
    /*2.b) Gli elementi dell’array devono essere interni all’intervallo passato al programma; */
    @Property(generation = GenerationMode.RANDOMIZED)
    @StatisticsReport(format = Histogram.class)
    void generatedArrayShouldBeWithinSpecifiedRange(
            @ForAll @IntRange int lowerLimit,
            @ForAll @IntRange int upperLimit,
            @ForAll @IntRange (max=4000000)int numCount //si imposta un limite superiore per evitare errori di memoria/tempi di esecuzione
    ) {
        // si assicura che l'intervallo che il numero di elementi da generare siano validi
        Assume.that(lowerLimit < upperLimit);
        Assume.that(numCount > 0 && numCount < (upperLimit - lowerLimit + 1));

        int[] result = RandomNumberGenerator.generateUniqueRandomNumbersInRange(
                String.valueOf(lowerLimit),
                String.valueOf(upperLimit),
                String.valueOf(numCount)
        );
        // Verifica che tutti gli elementi siano nell'intervallo specificato
        for (int number : result) {
            assertTrue(number >= lowerLimit && number <= upperLimit,
                    "Elemento dell'array non è nell'intervallo specificato");
        }
        String range="";
        for (int number:result){
            if(number==lowerLimit){
                range = "test su valori boundary sul limite inferiore";
            } else if ((number==upperLimit)) {
                range = "test su valori boundary sul limite superiore";
            }else{
                range = "test su valori boundary su altri valori";
            }
        }
        Statistics.label("range").collect(range);
        Statistics.label("value").collect(lowerLimit, upperLimit);
    }
    /*2.c) Gli elementi dell’array devono essere unici; */
    @Property(generation = GenerationMode.RANDOMIZED)
    @StatisticsReport(format = Histogram.class)
    void generatedArrayShouldHaveUniqueElements(
            @ForAll @IntRange int lowerLimit,
            @ForAll @IntRange int upperLimit,
            @ForAll @IntRange (max=4000000)int numCount //si imposta un limite superiore per evitare errori di memoria/tempi di esecuzione
    ) {
        // si assicura che l'intervallo che il numero di elementi da generare siano validi
        Assume.that(lowerLimit < upperLimit);
        Assume.that(numCount > 0 && numCount < (upperLimit - lowerLimit + 1));
        int[] result = RandomNumberGenerator.generateUniqueRandomNumbersInRange(
                String.valueOf(lowerLimit),
                String.valueOf(upperLimit),
                String.valueOf(numCount)
        );
        // si verifica che non ci siano elementi duplicati nell'array
        Set<Integer> uniqueElements = new HashSet<>();
        for (int number : result) {
            assertFalse(uniqueElements.contains(number),
                    "Elementi duplicati nell'array generato");
            uniqueElements.add(number);
        }

        String range="";
        if(numCount<100){
            range="Test effettuati su un numero basso di valori";
        } else if (numCount>=100&&numCount<3000000) {
            range="Test effettuati su un numero medio di valori";
        } else if (numCount>=3000000) {
            range="Test effettuati su un numero alto di valori";
        }
        Statistics.label("range").collect(range);
        Statistics.label("value").collect(result.length);
    }
    /*2.d) Gli elementi dell’array devono essere ordinati in modo crescente; */
    @Property(generation = GenerationMode.RANDOMIZED)
    @StatisticsReport(format = Histogram.class)
    void generatedArrayShouldBeSortedInAscendingOrder(
            @ForAll @IntRange int lowerLimit,
            @ForAll @IntRange int upperLimit,
            @ForAll @IntRange(max = 4000000) int numCount // si imposta un limite superiore per evitare errori di memoria/tempi di esecuzione
    ) {
        // si assicura che l'intervallo che il numero di elementi da generare siano validi
        Assume.that(lowerLimit < upperLimit);
        Assume.that(numCount > 0 && numCount < (upperLimit - lowerLimit + 1));
        int[] result = RandomNumberGenerator.generateUniqueRandomNumbersInRange(
                String.valueOf(lowerLimit),
                String.valueOf(upperLimit),
                String.valueOf(numCount)
        );
        // Verifica che gli elementi siano ordinati in modo crescente
        for (int i = 1; i < result.length; i++) {
            assertTrue(result[i - 1] <= result[i],
                    "Gli elementi dell'array non sono ordinati in modo crescente");
        }
        String range="";
        if(numCount<100){
            range="Test effettuati su un numero basso di valori";
        } else if (numCount>=100&&numCount<3000000) {
            range="Test effettuati su un numero medio di valori";
        } else if (numCount>=3000000) {
            range="Test effettuati su un numero alto di valori";
        }
        Statistics.label("range").collect(range);
        Statistics.label("value").collect(result.length);
    }
}
