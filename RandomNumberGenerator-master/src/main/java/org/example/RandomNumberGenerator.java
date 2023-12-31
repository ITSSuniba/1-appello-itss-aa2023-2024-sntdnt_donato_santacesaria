package org.example;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.util.Random;
import java.util.Scanner;
import java.util.*;

public class RandomNumberGenerator {

    public static int[] generateUniqueRandomNumbersInRange(String lowerLimit, String upperLimit, String numCount)
            throws IllegalArgumentException {

        try {
            // Conversione degli operandi in interi
            int lower = Integer.parseInt(lowerLimit);
            int upper = Integer.parseInt(upperLimit);
            int count = Integer.parseInt(numCount);

            if((upper-lower+1)<count){
                throw new IllegalArgumentException("\n->Errore: Il numero degli elementi da generare deve essere al massimo pari alla differenza+1 tra il limite superiore ed il limite inferiore.");
            }

            // Verifica se l'intervallo è valido
            if (lower >= upper) {
                throw new IllegalArgumentException("\n->Errore: Limite inferiore deve essere inferiore al limite superiore.");
            }

            // Verifica se il numero di numeri è positivo
            if (count <= 0) {
                throw new IllegalArgumentException("\n->Errore: Il numero di elementi deve essere positivo.");
            }

            Set<Integer> uniqueNumbers = new HashSet<>();
            Random random = new Random();

            // Genera numeri unici nell'intervallo
            while (uniqueNumbers.size() < count) {
                int randomNumber = random.nextInt(upper - lower + 1) + lower;
                uniqueNumbers.add(randomNumber);
            }

            // Converti il set in un array ordinato
            int[] result = uniqueNumbers.stream().sorted().mapToInt(Integer::intValue).toArray();

            return result;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("\n->Errore: Inserito un valore non valido. Assicurati di inserire valori validi.");
        }


    }
}
