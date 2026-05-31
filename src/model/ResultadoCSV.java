package model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ResultadoCSV {

    // Onde vai salvar o arqv
    private static final String CAMINHO = "results/resultados.csv";

    // Cabeçalho
    private static final String CABECALHO =
            "Experimento;Data;Neuronios Ocultos;Learning Rate;Max Iterations;Error Rate;Threshold;Oversampling;Acuracia (%);Precisao (%);Recall (%);F1-Score (%);VP;VN;FP;FN";

    /**
     * Método responsável por salvar os resultados da execução da RNA.
     */
    public static void salvar(

            // Configurações da rede neural
            AdrenaConfig config,

            // Métricas calculadas
            double acuracia,
            double precisao,
            double recall,
            double f1,

            // Valores da matriz de confusão
            int tp,
            int tn,
            int fp,
            int fn

    ) throws IOException {

        // Verifica se existe a pasta
        File pasta = new File("results");

        // Caso não exista, cria automaticamente
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        // Cria referência para o arquivo CSV
        File arquivo = new File(CAMINHO);

        // Verifica se é a primeira execução
        boolean novoArquivo = !arquivo.exists();

        // Conta quantos experimentos já existem
        // para gerar:
        // Experimento 1
        // Experimento 2
        // Experimento 3 ...
        int experimento = contarExperimentos(arquivo) + 1;

        // Abre o arquivo em modo de adicionar conteúdo
        FileWriter writer = new FileWriter(arquivo, true);

        // Se for um arquivo novo, escreve o cabeçalho
        if (novoArquivo) {
            writer.write(CABECALHO + "\n");
        }

        // Pega a data e hora atual
        String data = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        );

        // Escreve uma linha da tabela com os resultados atuais
        writer.write(String.format(
                Locale.US,

                // Estrutura da linha
                "%d;%s;%d;%.4f;%d;%.4f;%.4f;%d;%.2f;%.2f;%.2f;%.2f;%d;%d;%d;%d\n",

                // Número do experimento
                experimento,

                // Data e hora
                data,

                // Configurações da RNA
                config.getHiddenNeurons(),
                config.getLearningRate(),
                config.getMaxIterations(),
                config.getErrorRate(),
                config.getThreshold(),
                config.getOversamplingFactor(),

                // Métricas convertidas para %
                acuracia * 100,
                precisao * 100,
                recall * 100,
                f1 * 100,

                // Matriz de confusão
                tp,
                tn,
                fp,
                fn
        ));

        // Fecha o arquivo
        writer.close();
    }

    /**
     * Conta quantos experimentos já existem no CSV.
     * Isso serve para numerar automaticamente:
     * Experimento 1
     * Experimento 2
     * Experimento 3 ...
     */
    private static int contarExperimentos(File arquivo) throws IOException {

        // Se o arquivo ainda não existe
        if (!arquivo.exists()) {
            return 0;
        }

        // Lê o arquivo linha por linha
        BufferedReader reader = new BufferedReader(new FileReader(arquivo));

        int linhas = 0;

        while (reader.readLine() != null) {
            linhas++;
        }

        reader.close();

        // Remove 1 linha referente ao cabeçalho
        return Math.max(0, linhas - 1);
    }
}