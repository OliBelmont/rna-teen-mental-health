package model;

import java.util.List;
import preprocessing.ColumnMapper;
import preprocessing.DataLoader;
import preprocessing.DataLoaderTeste;
import preprocessing.DataProcessor;

public class Main {

    public static void main(String[] args) throws Exception {

        DataProcessor processor = new DataProcessor();
        String[] plataformas = {"Instagram", "TikTok", "Twitter", "Facebook", "YouTube", "Snapchat"};
        String[] generos     = {"Male", "Female", "Non-binary"};
        String[] niveis      = {"Low", "Medium", "High"};

        // Carrega treino (já com SMOTE)
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

        // Carrega teste (dados originais, sem SMOTE)
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

        // Normaliza treino e teste juntos para usar a mesma escala
        double[][] tudo = new double[datasetTreino.length + datasetTeste.length][13];
        System.arraycopy(datasetTreino, 0, tudo, 0, datasetTreino.length);
        System.arraycopy(datasetTeste,  0, tudo, datasetTreino.length, datasetTeste.length);
        double[][] normTudo = processor.normalizeDataSet(tudo);

        double[][] normTreino = new double[datasetTreino.length][13];
        double[][] normTeste  = new double[datasetTeste.length][13];
        System.arraycopy(normTudo, 0,                    normTreino, 0, datasetTreino.length);
        System.arraycopy(normTudo, datasetTreino.length, normTeste,  0, datasetTeste.length);

        // Separa X e y
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

        System.out.printf("Treino: %d amostras | Teste: %d amostras%n%n",
                Xtreino.length, Xteste.length);

        AdrenaConfig config = new AdrenaConfig();
        RedeNeural rede = new RedeNeural(config);
        rede.treinar(Xtreino, ytreino);
        rede.avaliar(Xteste, yteste);
    }
}