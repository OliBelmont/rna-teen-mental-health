package model;

import preprocessing.ColumnMapper;
import preprocessing.DataLoader;
import preprocessing.DataProcessor;
import preprocessing.DataSplitter;

import java.util.List;

/**
 * Classe de teste automático de hiperparâmetros.
 * Ela testa várias combinações de configurações da rede neural
 * e no final mostra qual foi a melhor — sem você precisar ficar
 * rodando manualmente.
 */
public class Teste {

    public static void main(String[] args) throws Exception {

        // -----------------------------------------------------------------------
        // Carrega e prepara os dados uma única vez
        // (não precisa repetir isso para cada teste)
        // -----------------------------------------------------------------------
        DataLoader loader = new DataLoader();
        List<String[]> rawData = loader.loadFile();

        DataProcessor processor = new DataProcessor();
        String[] plataformas = {"Instagram", "TikTok", "Twitter", "Facebook", "YouTube", "Snapchat"};
        String[] generos     = {"Male", "Female", "Non-binary"};
        String[] niveis      = {"Low", "Medium", "High"};

        double[][] dataset = new double[rawData.size()][13];
        for (int i = 0; i < rawData.size(); i++) {
            String[] row = rawData.get(i);
            dataset[i][0]  = Double.parseDouble(row[0].trim());
            dataset[i][1]  = processor.encodeCategories(row[1].trim(), generos);
            dataset[i][2]  = Double.parseDouble(row[2].trim());
            dataset[i][3]  = processor.encodeCategories(row[3].trim(), plataformas);
            dataset[i][4]  = Double.parseDouble(row[4].trim());
            dataset[i][5]  = Double.parseDouble(row[5].trim());
            dataset[i][6]  = processor.encodeCategories(row[6].trim(), niveis);
            dataset[i][7]  = processor.encodeCategories(row[7].trim(), niveis);
            dataset[i][8]  = processor.encodeCategories(row[8].trim(), niveis);
            dataset[i][9]  = Double.parseDouble(row[9].trim());
            dataset[i][10] = Double.parseDouble(row[10].trim());
            dataset[i][11] = Double.parseDouble(row[11].trim());
            dataset[i][12] = Double.parseDouble(row[12].trim());
        }

        double[][] normalizado = processor.normalizeDataSet(dataset);

        ColumnMapper mapper = new ColumnMapper();
        double[][] completo = new double[normalizado.length][13];
        for (int i = 0; i < normalizado.length; i++) {
            System.arraycopy(mapper.extrairDados(normalizado[i]), 0, completo[i], 0, 12);
            completo[i][12] = mapper.extrairLabel(normalizado[i]);
        }

        DataSplitter splitter = new DataSplitter();
        double[][][] splits = splitter.split(completo, 0.8);

        double[][] Xtreino = new double[splits[0].length][12];
        double[]   ytreino = new double[splits[0].length];
        double[][] Xteste  = new double[splits[1].length][12];
        double[]   yteste  = new double[splits[1].length];

        for (int i = 0; i < splits[0].length; i++) {
            System.arraycopy(splits[0][i], 0, Xtreino[i], 0, 12);
            ytreino[i] = splits[0][i][12];
        }
        for (int i = 0; i < splits[1].length; i++) {
            System.arraycopy(splits[1][i], 0, Xteste[i], 0, 12);
            yteste[i] = splits[1][i][12];
        }

        // -----------------------------------------------------------------------
        // Combinações de hiperparâmetros para testar
        // Sinta-se livre para adicionar mais valores
        // -----------------------------------------------------------------------
        int[]    hidden  = {4, 8, 16, 32};         // quantidade de neurônios ocultos
        double[] lr      = {0.001, 0.01, 0.05, 0.1}; // taxa de aprendizado
        int[]    maxIter = {500, 1000, 3000, 5000};   // máximo de iterações

        // Variáveis para guardar a melhor configuração encontrada
        double melhorF1     = -1;
        int    melhorHidden = 0;
        double melhorLr     = 0;
        int    melhorIter   = 0;

        int total  = hidden.length * lr.length * maxIter.length;
        int atual  = 0;

        System.out.println("=== Iniciando busca de hiperparâmetros ===");
        System.out.println("Total de combinações a testar: " + total);
        System.out.println("--------------------------------------------------");

        // -----------------------------------------------------------------------
        // Loop principal — testa todas as combinações
        // -----------------------------------------------------------------------
        for (int h : hidden) {
            for (double l : lr) {
                for (int m : maxIter) {
                    atual++;
                    System.out.printf("%n[%d/%d] Testando: hidden=%d | lr=%.3f | maxIter=%d%n",
                            atual, total, h, l, m);

                    // Cria a configuração com os valores atuais do loop
                    AdrenaConfig config = new AdrenaConfig();
                    config.setHiddenNeurons(h);
                    config.setLearningRate(l);
                    config.setMaxIterations(m);

                    // Treina a rede com essa configuração
                    RedeNeural rede = new RedeNeural(config);
                    rede.treinar(Xtreino, ytreino);

                    // Calcula o F1-Score no conjunto de teste
                    double f1 = calcularF1(rede, Xteste, yteste);
                    System.out.printf("F1-Score: %.2f%%%n", f1 * 100);

                    // Guarda se for melhor que o anterior
                    if (f1 > melhorF1) {
                        melhorF1     = f1;
                        melhorHidden = h;
                        melhorLr     = l;
                        melhorIter   = m;
                        System.out.println("*** Nova melhor configuração encontrada! ***");
                    }
                }
            }
        }

        // -----------------------------------------------------------------------
        // Resultado final
        // -----------------------------------------------------------------------
        System.out.println("\n==================================================");
        System.out.println("=== MELHOR CONFIGURAÇÃO ENCONTRADA ===");
        System.out.println("==================================================");
        System.out.printf("Neurônios ocultos : %d%n",   melhorHidden);
        System.out.printf("Taxa de aprendizado: %.3f%n", melhorLr);
        System.out.printf("Max iterações      : %d%n",   melhorIter);
        System.out.printf("F1-Score           : %.2f%%%n", melhorF1 * 100);
        System.out.println("\nUse esses valores no AdrenaConfig para o resultado final!");
    }

    /**
     * Calcula o F1-Score da rede no conjunto de teste.
     * Separado aqui para não poluir o loop principal.
     */
    private static double calcularF1(RedeNeural rede, double[][] X, double[] y) throws Exception {
        int tp = 0, fp = 0, fn = 0;

        for (int i = 0; i < X.length; i++) {
            int previsto = rede.prever(X[i]);
            int esperado = (int) y[i];

            if      (previsto == 1 && esperado == 1) tp++;
            else if (previsto == 1 && esperado == 0) fp++;
            else if (previsto == 0 && esperado == 1) fn++;
        }

        double precisao = (tp + fp) == 0 ? 0 : (double) tp / (tp + fp);
        double recall   = (tp + fn) == 0 ? 0 : (double) tp / (tp + fn);
        return (precisao + recall) == 0 ? 0 : 2 * (precisao * recall) / (precisao + recall);
    }
}