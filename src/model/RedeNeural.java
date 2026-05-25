package model;

import ADReNA_API.NeuralNetwork.Backpropagation;
import ADReNA_API.Data.DataSet;
import ADReNA_API.Data.DataSetObject;

/**
 * Classe responsável por criar, treinar e avaliar a Rede Neural Artificial.
 * Ela usa a API ADReNA por baixo dos panos, mas organiza tudo de forma
 * mais simples para o nosso projeto.
 */
public class RedeNeural {

    // Guarda as configurações da rede (número de neurônios, taxa de aprendizado, etc.)
    private AdrenaConfig config;

    // Esse é o objeto da API ADReNA que faz o trabalho pesado da rede neural
    private Backpropagation rede;

    /**
     * Construtor: quando você cria um objeto RedeNeural, ele já monta a rede
     * automaticamente usando os valores definidos no AdrenaConfig.
     */
    public RedeNeural(AdrenaConfig config) {
        this.config = config;

        // Cria a rede neural com:
        // - config.getInputNeurons()  → quantas entradas (12 variáveis do adolescente)
        // - config.getOutputNeurons() → quantas saídas (1 = tem depressão ou não)
        // - new int[]{ config.getHiddenNeurons() } → tamanho da camada oculta
        this.rede = new Backpropagation(
            config.getInputNeurons(),
            config.getOutputNeurons(),
            new int[]{ config.getHiddenNeurons() }
        );

        // Passa os hiperparâmetros do AdrenaConfig para a API ADReNA
        rede.SetLearningRate(config.getLearningRate());        // velocidade de aprendizado
        rede.SetMaxIterationNumber(config.getMaxIterations()); // máximo de voltas no treino
        rede.SetErrorRate(config.getErrorRate());              // erro aceitável para parar
    }

    /**
     * Método treinar: recebe os dados dos adolescentes (X) e os resultados
     * esperados (y = 0 sem depressão, 1 com depressão) e treina a rede neural.
     *
     * Técnica de oversampling: os casos de depressão (y=1) são adicionados
     * várias vezes ao dataset para compensar o desbalanceamento — só 31 de 1200
     * adolescentes têm depressão no CSV, então repetimos esses casos para a rede
     * aprender a identificá-los melhor.
     */
    public void treinar(double[][] X, double[] y) throws Exception {

        // Cria um DataSet no formato que a API ADReNA entende
        DataSet ds = new DataSet(config.getInputNeurons(), config.getOutputNeurons());

        for (int i = 0; i < X.length; i++) {
            double[] saida = new double[]{ y[i] };
            ds.Add(new DataSetObject(X[i], saida)); // adiciona o adolescente normalmente

            // Oversampling: se tiver depressão, adiciona mais vezes
            // Isso faz a rede "prestar mais atenção" nos casos positivos
            if (y[i] == 1.0) {
                for (int k = 0; k < 10; k++) {
                    ds.Add(new DataSetObject(X[i], saida));
                }
            }
        }

        System.out.println("=== Iniciando treinamento ===");
        System.out.printf("Amostras: %d | Entradas: %d | Neurônios ocultos: %d%n",
                X.length, config.getInputNeurons(), config.getHiddenNeurons());

        // Chama o método Learn da API ADReNA — aqui acontece o backpropagation de verdade
        rede.Learn(ds);

        System.out.printf("Treinamento concluído | Iterações: %d%n", rede.GetIterationNumber());
        System.out.printf("Erro final: %.6f%n", rede.Error);
    }

    /**
     * Método prever: dado um adolescente (array de 12 valores), retorna
     * 0 (sem depressão) ou 1 (com depressão).
     *
     * Usa o threshold do AdrenaConfig em vez de 0.5 fixo.
     * Como o dataset é desbalanceado, um threshold menor (ex: 0.2) faz a rede
     * ser mais sensível — detecta mais casos de depressão mesmo com menor certeza.
     */
    public int prever(double[] x) throws Exception {
        double[] resultado = rede.Recognize(x);              // pergunta para a rede neural
        return resultado[0] >= config.getThreshold() ? 1 : 0; // usa o threshold do AdrenaConfig
    }

    /**
     * Método avaliar: testa a rede com dados que ela nunca viu (conjunto de teste)
     * e calcula as métricas de desempenho.
     *
     * As métricas são calculadas manualmente porque a API ADReNA não fornece isso.
     */
    public void avaliar(double[][] X, double[] y) throws Exception {

        // Contadores para montar a Matriz de Confusão
        int tp = 0; // VP: previu depressão e tinha depressão (acerto positivo)
        int tn = 0; // VN: previu sem depressão e não tinha (acerto negativo)
        int fp = 0; // FP: previu depressão mas não tinha (falso alarme)
        int fn = 0; // FN: previu sem depressão mas tinha (perdeu um caso real)

        for (int i = 0; i < X.length; i++) {
            int previsto = prever(X[i]);   // o que a rede acha
            int esperado = (int) y[i];     // o que é real (gabarito)

            if      (previsto == 1 && esperado == 1) tp++;
            else if (previsto == 0 && esperado == 0) tn++;
            else if (previsto == 1 && esperado == 0) fp++;
            else if (previsto == 0 && esperado == 1) fn++;
        }

        // Acurácia: de tudo que analisou, quantos acertou no total
        double acuracia = (double)(tp + tn) / (tp + tn + fp + fn);

        // Precisão: das vezes que disse "tem depressão", quantas estava certa
        double precisao = (tp + fp) == 0 ? 0 : (double) tp / (tp + fp);

        // Recall: dos que realmente tinham depressão, quantos a rede encontrou
        double recall   = (tp + fn) == 0 ? 0 : (double) tp / (tp + fn);

        // F1-Score: média entre precisão e recall
        double f1       = (precisao + recall) == 0 ? 0
                        : 2 * (precisao * recall) / (precisao + recall);

        System.out.println("\n=== Métricas de Avaliação ===");
        System.out.printf("Acurácia : %.2f%%%n", acuracia * 100);
        System.out.printf("Precisão : %.2f%%%n", precisao * 100);
        System.out.printf("Recall   : %.2f%%%n", recall   * 100);
        System.out.printf("F1-Score : %.2f%%%n", f1       * 100);
        System.out.println("\nMatriz de Confusão:");
        System.out.printf("  VP - acertou depressão     : %d%n", tp);
        System.out.printf("  VN - acertou sem depressão : %d%n", tn);
        System.out.printf("  FP - falso alarme          : %d%n", fp);
        System.out.printf("  FN - perdeu caso real      : %d%n", fn);
    }
}