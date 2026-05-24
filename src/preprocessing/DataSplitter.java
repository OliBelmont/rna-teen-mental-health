package preprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DataSplitter {

    public double[][][] split(double[][] data, double trainRatio) {

        /**
         * Transforma a matriz em uma lista
         */
        List<double[]> listaDados = new ArrayList<>(Arrays.asList(data));

        /**
         * Embaralha a lista usando a função nativa do Java.
         * A semente '42' garante que toda vez que você rodar, os dados vão embaralhar
         * na mesma ordem
         */
        Collections.shuffle(listaDados, new Random(42));

        /**
         * Descobre em qual linha deve ser feito o corte
         */
        int pontoDeCorte = (int) (listaDados.size() * trainRatio);

        /**
         * Corta a lista em duas partes usando o subList
         */
        List<double[]> treino = listaDados.subList(0, pontoDeCorte);
        List<double[]> teste = listaDados.subList(pontoDeCorte, listaDados.size());

        /**
         * Converte as listas de volta a matriz e retorna
         */
        return new double[][][] {
                treino.toArray(new double[0][]),
                teste.toArray(new double[0][])
        };
    }
}