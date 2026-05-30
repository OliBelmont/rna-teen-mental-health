package model;

/**
 * Classe de configuração da Rede Neural.
 * Aqui ficam todos os hiperparâmetros — os "botões" que controlam
 * como a rede vai aprender. Alterar esses valores muda o comportamento
 * do treinamento sem precisar mexer no código da rede em si.
 */
public class AdrenaConfig {

    private int    inputNeurons    = 12;
    private int    hiddenNeurons   = 16;
    private int    outputNeurons   = 1;
    private double learningRate    = 0.001;
    private int    maxIterations   = 1000;
    private double errorRate       = 0.01;
    private double threshold       = 0.20;
    private int    oversamplingFactor = 3;  // <- novo

    // Getters
    public int    getInputNeurons()        { return inputNeurons; }
    public int    getHiddenNeurons()       { return hiddenNeurons; }
    public int    getOutputNeurons()       { return outputNeurons; }
    public double getLearningRate()        { return learningRate; }
    public int    getMaxIterations()       { return maxIterations; }
    public double getErrorRate()           { return errorRate; }
    public double getThreshold()           { return threshold; }
    public int    getOversamplingFactor()  { return oversamplingFactor; }  // <- novo

    // Setters
    public void setInputNeurons(int n)          { this.inputNeurons = n; }
    public void setHiddenNeurons(int n)         { this.hiddenNeurons = n; }
    public void setOutputNeurons(int n)         { this.outputNeurons = n; }
    public void setLearningRate(double lr)      { this.learningRate = lr; }
    public void setMaxIterations(int n)         { this.maxIterations = n; }
    public void setErrorRate(double e)          { this.errorRate = e; }
    public void setThreshold(double t)          { this.threshold = t; }
    public void setOversamplingFactor(int f)    { this.oversamplingFactor = f; }  // <- novo
}