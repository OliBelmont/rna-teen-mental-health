package model;

import java.util.List;
import preprocessing.ColumnMapper;
import preprocessing.DataLoader;
import preprocessing.DataProcessor;
import preprocessing.DataSplitter;

public class Main {

    public static void main(String[] args) throws Exception {

        // -----------------------------------------------------------------------
        // PASSO 1: Carregar os dados do CSV
        // O DataLoader abre o arquivo e lê linha por linha
        // -----------------------------------------------------------------------
        DataLoader loader = new DataLoader();
        List<String[]> rawData = loader.loadFile(); // cada elemento é uma linha do CSV em texto
        System.out.println("Linhas carregadas: " + rawData.size());

        // -----------------------------------------------------------------------
        // PASSO 2: Converter texto para número
        // A rede neural só entende números, então precisamos converter tudo.
        // Números já vêm como Double.parseDouble().
        // Textos (ex: "Male", "TikTok", "High") viram números pelo encodeCategories().
        // -----------------------------------------------------------------------
        DataProcessor processor = new DataProcessor();

        // Listas de categorias possíveis para cada coluna de texto
        String[] plataformas = {"Instagram", "TikTok", "Twitter", "Facebook", "YouTube", "Snapchat"};
        String[] generos     = {"Male", "Female", "Non-binary"};
        String[] niveis      = {"Low", "Medium", "High"};

        // Cria a matriz de dados com 1200 linhas e 13 colunas (12 entradas + 1 resultado)
        double[][] dataset = new double[rawData.size()][13];
        for (int i = 0; i < rawData.size(); i++) {
            String[] row = rawData.get(i);
            dataset[i][0]  = Double.parseDouble(row[0].trim());                         // age (número)
            dataset[i][1]  = processor.encodeCategories(row[1].trim(), generos);        // gender (texto → número)
            dataset[i][2]  = Double.parseDouble(row[2].trim());                         // daily_social_media_hours
            dataset[i][3]  = processor.encodeCategories(row[3].trim(), plataformas);    // platform_usage (texto → número)
            dataset[i][4]  = Double.parseDouble(row[4].trim());                         // sleep_hours
            dataset[i][5]  = Double.parseDouble(row[5].trim());                         // screen_time_before_sleep
            dataset[i][6]  = processor.encodeCategories(row[6].trim(), niveis);         // academic_performance (texto → número)
            dataset[i][7]  = processor.encodeCategories(row[7].trim(), niveis);         // physical_activity (texto → número)
            dataset[i][8]  = processor.encodeCategories(row[8].trim(), niveis);         // social_interaction_level (texto → número)
            dataset[i][9]  = Double.parseDouble(row[9].trim());                         // stress_level
            dataset[i][10] = Double.parseDouble(row[10].trim());                        // anxiety_level
            dataset[i][11] = Double.parseDouble(row[11].trim());                        // addiction_level
            dataset[i][12] = Double.parseDouble(row[12].trim());                        // depression_label (0 ou 1)
        }

        // -----------------------------------------------------------------------
        // PASSO 3: Normalizar os dados (deixar tudo entre 0 e 1)
        // Sem isso, uma coluna com valores grandes (ex: horas de tela = 8)
        // dominaria sobre uma coluna pequena (ex: gênero = 0.5).
        // -----------------------------------------------------------------------
        double[][] normalizado = processor.normalizeDataSet(dataset);

        // -----------------------------------------------------------------------
        // PASSO 4: Separar as entradas (X) do gabarito (y)
        // X → as 12 características de cada adolescente
        // y → se tem depressão (1) ou não (0)
        // -----------------------------------------------------------------------
        ColumnMapper mapper = new ColumnMapper();
        double[][] X = new double[normalizado.length][12];
        double[]   y = new double[normalizado.length];
        for (int i = 0; i < normalizado.length; i++) {
            X[i] = mapper.extrairDados(normalizado[i]);  // pega as 12 colunas de entrada
            y[i] = mapper.extrairLabel(normalizado[i]);  // pega a coluna 13 (depression_label)
        }

        // -----------------------------------------------------------------------
        // PASSO 5: Dividir em treino (80%) e teste (20%)
        // O DataSplitter embaralha os dados e faz o corte.
        // Treino → a rede aprende com esses dados
        // Teste  → usamos para medir se a rede realmente aprendeu
        // -----------------------------------------------------------------------

        // Junta X e y numa matriz só para poder embaralhar junto
        double[][] completo = new double[X.length][13];
        for (int i = 0; i < X.length; i++) {
            System.arraycopy(X[i], 0, completo[i], 0, 12); // copia as 12 entradas
            completo[i][12] = y[i];                         // coloca o gabarito na última coluna
        }

        DataSplitter splitter = new DataSplitter();
        double[][][] splits   = splitter.split(completo, 0.8); // 80% treino, 20% teste
        double[][] treino     = splits[0]; // parte de treino
        double[][] teste      = splits[1]; // parte de teste

        // Separa X e y do conjunto de treino
        double[][] Xtreino = new double[treino.length][12];
        double[]   ytreino = new double[treino.length];
        for (int i = 0; i < treino.length; i++) {
            System.arraycopy(treino[i], 0, Xtreino[i], 0, 12);
            ytreino[i] = treino[i][12];
        }

        // Separa X e y do conjunto de teste
        double[][] Xteste = new double[teste.length][12];
        double[]   yteste = new double[teste.length];
        for (int i = 0; i < teste.length; i++) {
            System.arraycopy(teste[i], 0, Xteste[i], 0, 12);
            yteste[i] = teste[i][12];
        }

        System.out.printf("Treino: %d amostras | Teste: %d amostras%n%n",
                Xtreino.length, Xteste.length);

        // -----------------------------------------------------------------------
        // PASSO 6: Criar e treinar a rede neural
        // AdrenaConfig define os hiperparâmetros (neurônios, taxa de aprendizado, etc.)
        // RedeNeural usa esses parâmetros para montar e treinar a rede via API ADReNA
        // -----------------------------------------------------------------------
        AdrenaConfig config = new AdrenaConfig();  // carrega as configurações
        RedeNeural rede = new RedeNeural(config);  // cria a rede com essas configurações
        rede.treinar(Xtreino, ytreino);            // treina com os 80% de treino

        // -----------------------------------------------------------------------
        // PASSO 7: Avaliar a rede com os dados de teste
        // A rede nunca viu esses 20% — é o teste real de aprendizado
        // -----------------------------------------------------------------------
        rede.avaliar(Xteste, yteste);
    }
}