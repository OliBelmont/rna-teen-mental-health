package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BenchmarkCSV {

    private static final String CAMINHO = "results/benchmark.csv";

    public static void salvar(
            int hidden,
            double learningRate,
            int maxIterations,
            double threshold,
            int oversampling,
            double f1
    ) throws IOException {

        File pasta = new File("results");

        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        File arquivo = new File(CAMINHO);

        boolean novoArquivo = !arquivo.exists();

        FileWriter writer = new FileWriter(arquivo, true);

        if (novoArquivo) {
            writer.write(
                "Hidden Neurons;Learning Rate;Max Iterations;Threshold;Oversampling;F1-Score (%)\n"
            );
        }

        writer.write(String.format(
                "%.0f;%.4f;%d;%.2f;%d;%.2f\n",
                (double) hidden,
                learningRate,
                maxIterations,
                threshold,
                oversampling,
                f1 * 100
        ));

        writer.close();
    }
}