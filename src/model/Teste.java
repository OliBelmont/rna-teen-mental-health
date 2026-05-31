package model;

import java.util.List;
import preprocessing.ColumnMapper;
import preprocessing.DataLoader;
import preprocessing.DataLoaderTeste;
import preprocessing.DataProcessor;

public class Teste {

    public static void main(String[] args) throws Exception {

        DataProcessor processor = new DataProcessor();
        String[] plataformas = {"Instagram", "TikTok", "Twitter", "Facebook", "YouTube", "Snapchat"};
        String[] generos     = {"Male", "Female", "Non-binary"};
        String[] niveis      = {"Low", "Medium", "High"};

        DataLoader loaderTreino = new DataLoader();
        List<String[]> rawTreino = loaderTreino.loadFile();

        double[][] datasetTreino = new double[rawTreino.size()][13];
        for (int i = 0; i < rawTreino.size(); i++) {
            String[] row = rawTreino.get(i);
            datasetTreino[i][0]  = Double.parseDouble(row[0].trim());
            datasetTreino[i][1]  = processor.encodeCategories(row[1].trim(), generos);
            datasetTreino[i][2]  = Double.parseDouble(row[2].trim());
            datasetTreino[i][3]  = processor.encodeCategories(row[3].trim(), plataformas);
            datasetTreino[i][4]  = Double.parseDouble(row[4].trim());
            datasetTreino[i][5]  = Double.parseDouble(row[5].trim());
            datasetTreino[i][6]  = processor.encodeCategories(row[6].trim(), niveis);
            datasetTreino[i][7]  = processor.encodeCategories(row[7].trim(), niveis);
            datasetTreino[i][8]  = processor.encodeCategories(row[8].trim(), niveis);
            datasetTreino[i][9]  = Double.parseDouble(row[9].trim());
            datasetTreino[i][10] = Double.parseDouble(row[10].trim());
            datasetTreino[i][11] = Double.parseDouble(row[11].trim());
            datasetTreino[i][12] = Double.parseDouble(row[12].trim());
        }

        DataLoaderTeste loaderTeste = new DataLoaderTeste();
        List<String[]> rawTeste = loaderTeste.loadFile();

        double[][] datasetTeste = new double[rawTeste.size()][13];
        for (int i = 0; i < rawTeste.size(); i++) {
            String[] row = rawTeste.get(i);
            datasetTeste[i][0]  = Double.parseDouble(row[0].trim());
            datasetTeste[i][1]  = processor.encodeCategories(row[1].trim(), generos);
            datasetTeste[i][2]  = Double.parseDouble(row[2].trim());
            datasetTeste[i][3]  = processor.encodeCategories(row[3].trim(), plataformas);
            datasetTeste[i][4]  = Double.parseDouble(row[4].trim());
            datasetTeste[i][5]  = Double.parseDouble(row[5].trim());
            datasetTeste[i][6]  = processor.encodeCategories(row[6].trim(), niveis);
            datasetTeste[i][7]  = processor.encodeCategories(row[7].trim(), niveis);
            datasetTeste[i][8]  = processor.encodeCategories(row[8].trim(), niveis);
            datasetTeste[i][9]  = Double.parseDouble(row[9].trim());
            datasetTeste[i][10] = Double.parseDouble(row[10].trim());
            datasetTeste[i][11] = Double.parseDouble(row[11].trim());
            datasetTeste[i][12] = Double.parseDouble(row[12].trim());
        }

        // Normaliza os dois juntos para usar a mesma escala
        double[][] tudo = new double[datasetTreino.length + datasetTeste.length][13];
        System.arraycopy(datasetTreino, 0, tudo, 0, datasetTreino.length);
        System.arraycopy(datasetTeste,  0, tudo, datasetTreino.length, datasetTeste.length);
        double[][] normTudo = processor.normalizeDataSet(tudo);

        double[][] normTreino = new double[datasetTreino.length][13];
        double[][] normTeste  = new double[datasetTeste.length][13];
        System.arraycopy(normTudo, 0,                    normTreino, 0, datasetTreino.length);
        System.arraycopy(normTudo, datasetTreino.length, normTeste,  0, datasetTeste.length);

        ColumnMapper mapper = new ColumnMapper();
        double[][] Xtreino = new double[normTreino.length][12];
        double[]   ytreino = new double[normTreino.length];
        for (int i = 0; i < normTreino.length; i++) {
            Xtreino[i] = mapper.extrairDados(normTreino[i]);
            ytreino[i] = mapper.extrairLabel(normTreino[i]);
        }

        double[][] Xteste = new double[normTeste.length][12];
        double[]   yteste = new double[normTeste.length];
        for (int i = 0; i < normTeste.length; i++) {
            Xteste[i] = mapper.extrairDados(normTeste[i]);
            yteste[i] = mapper.extrairLabel(normTeste[i]);
        }

        // daqui pra baixo fica tudo igual — arrays de hiperparâmetros, loops, calcularF1

        // -----------------------------------------------------------------------
        // Combinações de hiperparâmetros para testar
        // Sinta-se livre para adicionar mais valores
        // -----------------------------------------------------------------------
        int[]    hidden     = {4, 8, 16, 32};
        double[] lr         = {0.001, 0.005, 0.01};
        int[]    maxIter    = {1000, 2000, 5000};
        double[] thresholds = {0.2, 0.3, 0.4, 0.5};
        int[]    oversamplings = {1, 3, 5};
        // Variáveis para guardar a melhor configuração encontrada
        double melhorF1     = -1;
        int    melhorHidden = 0;
        double melhorLr     = 0;
        int    melhorIter   = 0;
        double melhorThreshold = 0;
        int    melhorOversampling = 0;

int total = hidden.length * lr.length * maxIter.length * thresholds.length * oversamplings.length;
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
            for (double t : thresholds) {
                for (int o : oversamplings) {
                    atual++;
                    System.out.printf("%n[%d/%d] Testando: hidden=%d | lr=%.4f | maxIter=%d | threshold=%.2f | oversampling=%d%n",
                            atual, total, h, l, m, t, o);

                    AdrenaConfig config = new AdrenaConfig();
                    config.setHiddenNeurons(h);
                    config.setLearningRate(l);
                    config.setMaxIterations(m);
                    config.setThreshold(t);
                    config.setOversamplingFactor(o);

                    RedeNeural rede = new RedeNeural(config);
                    rede.treinar(Xtreino, ytreino);

                    double f1 = calcularF1(rede, Xteste, yteste);
                    System.out.printf("F1-Score: %.2f%%%n", f1 * 100);
<<<<<<< HEAD
=======
                    BenchmarkCSV.salvar(
                        h,
                        l,
                        m,
                        t,
                        o,
                        f1
                    );

                    System.out.println("Resultado salvo no benchmark.csv");
>>>>>>> origin/branch_klinsmann

                    if (f1 > melhorF1) {
                        melhorF1           = f1;
                        melhorHidden       = h;
                        melhorLr           = l;
                        melhorIter         = m;
                        melhorThreshold    = t;
                        melhorOversampling = o;
                        System.out.println("*** Nova melhor configuração encontrada! ***");
                    }
                }
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
System.out.printf("Neurônios ocultos : %d%n",     melhorHidden);
System.out.printf("Taxa de aprendizado: %.4f%n",   melhorLr);
System.out.printf("Threshold          : %.2f%n",   melhorThreshold);
System.out.printf("Max iterações      : %d%n",     melhorIter);
System.out.printf("Oversampling       : %d%n",     melhorOversampling);
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