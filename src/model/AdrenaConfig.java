package model;

/**
 * Classe de configuração da Rede Neural.
 * Aqui ficam todos os hiperparâmetros — os "botões" que controlam
 * como a rede vai aprender. Alterar esses valores muda o comportamento
 * do treinamento sem precisar mexer no código da rede em si.
 */
public class AdrenaConfig {

    // Quantidade de neurônios na camada de entrada
    // Deve ser igual ao número de variáveis do dataset (12 características do adolescente)
    private int inputNeurons = 12;

    // Quantidade de neurônios na camada oculta (intermediária)
    // Mais neurônios = mais capacidade de aprender padrões complexos
    // Definido pelo grid search como melhor valor
    private int hiddenNeurons = 8;

    // Quantidade de neurônios na camada de saída
    // 1 = binário (tem depressão ou não tem)
    private int outputNeurons = 1;

    // Taxa de aprendizado: controla o tamanho do passo no ajuste dos pesos
    // Muito alto = aprendizado instável | Muito baixo = aprendizado lento
    // Definido pelo grid search como melhor valor
    private double learningRate = 0.1;

    // Número máximo de iterações (épocas) do treinamento
    // A rede para antes se atingir o errorRate
    // Definido pelo grid search como melhor valor
    private int maxIterations = 1000;

    // Taxa de erro mínima aceitável para parar o treinamento antes do máximo de iterações
    // Quando o erro cai abaixo desse valor, o treinamento encerra automaticamente
    private double errorRate = 0.01;

    // Threshold (limiar) de decisão: define a partir de qual valor a rede
    // considera que o adolescente tem depressão.
    // Como o dataset é desbalanceado (poucos casos de depressão),
    // usamos 0.2 em vez de 0.5 para a rede ser mais sensível a casos positivos
    private double threshold = 0.2;

    // -------------------------------------------------------------------------
    // Getters — usados pela RedeNeural para ler as configurações
    // -------------------------------------------------------------------------
    public int getInputNeurons()    { return inputNeurons; }
    public int getHiddenNeurons()   { return hiddenNeurons; }
    public int getOutputNeurons()   { return outputNeurons; }
    public double getLearningRate() { return learningRate; }
    public int getMaxIterations()   { return maxIterations; }
    public double getErrorRate()    { return errorRate; }
    public double getThreshold()    { return threshold; }

    // -------------------------------------------------------------------------
    // Setters — usados pelo Teste.java para testar diferentes configurações
    // -------------------------------------------------------------------------
    public void setInputNeurons(int inputNeurons)       { this.inputNeurons = inputNeurons; }
    public void setHiddenNeurons(int hiddenNeurons)     { this.hiddenNeurons = hiddenNeurons; }
    public void setOutputNeurons(int outputNeurons)     { this.outputNeurons = outputNeurons; }
    public void setLearningRate(double learningRate)    { this.learningRate = learningRate; }
    public void setMaxIterations(int maxIterations)     { this.maxIterations = maxIterations; }
    public void setErrorRate(double errorRate)          { this.errorRate = errorRate; }
    public void setThreshold(double threshold)          { this.threshold = threshold; }
}