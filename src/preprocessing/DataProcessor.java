package preprocessing;


import java.util.Arrays;

/**
 * Classe responsável por parametrizar os resultados entre 0 e 1, fazendo com que
 * ao programa comparar um valor maior da coluna X com outro da coluna Y, ele se
 * mantenha entre o intervalo de 0 a 1 mantendo a proporçao, chamado de Normalizaçao Min-Max
 */
public class DataProcessor {

    /**
     * Guarda o valor minimo e o maximo de cada uma das colunas do dataset
     * Exemplo, se na coluna idade há o intervalo de 12 a 19 anos o min guarda 12
     * e o max guarda 19
     */
    private double[] min;
    private double[] max;


    /**
     * Pega um número qualquer do dataset, o mínimo da coluna e o máximo e aplica
     * uma fórmula
     * Exemplo, se a idade varia entre 12 a 19 e a idade analisada seria 15
     * a conta ficaria
     * (15 - 12) / (19 - 12) = 3/7 = 0.428
     *
     * Resultado para a rede neural 0.428, o if usado é uma trava de segurança para
     * não dividir por zero caso todos na base tenham a mesma idade
     */
    public double normalize(double value, double min, double max){
        if(max == min) return 0.0; //Evita a divisão por zero
        return (value - min) / (max - min);
    }

    /**
     * Funçao preparatoria de dados para o treino da rede neural
     *
     * Primeiro laço varre 1200 linhas de adolescentes e anota quem são
     * os verdadeiros mínimos e máximos
     *
     * Segundo laço cria uma matriz zerada e passa novamente pelos dados,
     * substituindo cada n° original pela versao normalizada no intervalo 0 - 1
     */
    public double[][] normalizeDataSet(double[][] data){
        int cls = data[0].length;
        min = new double[cls];
        max = new double[cls];

        Arrays.fill(min, Double.MAX_VALUE); // Para achar o mínimo, comece do máximo
        Arrays.fill(max, -Double.MAX_VALUE); // Para achar o máximo, comece do mínimo, mais próximo de 0 por isso -

        /**
         * For each pega a matriz data e lê uma linha de cada vez
         */
        for(double[] row : data){

            for(int j=0; j < cls; j++){ //Laço interno que vai percorrer cada coluna representada pelo j

                /**
                 * Verifica se o valor minimo é menor do que o menor valor que eu já encontrei
                 * para esta mesma característica até agora. Se for, o array min é atualizado
                 * com esse novo recorde de valor mais baixo.
                 */
                if(row[j] < min[j])
                    min[j] = row[j];

                /**
                 * A mesma lógica aplica-se ao valor máximo. Este valor é maior
                 * do que o maior valor que eu já anotei para esta característica?
                 * Se a resposta for sim, o array max regista o novo recorde.
                 */
                if(row[j] > max[j])
                    max[j] = row[j];
            }
        }

        /**
         * Este bloco faz o trabalho de converter todos os numeros
         * Cria a matriz com o mesmo tamanho da matriz original, com o
         * objetivo de gravar os dados transformados para manter os originais
         * intactos e seguros em memória
         */
        double[][] normalized = new double[data.length][cls];
        for(int i=0; i < data.length; i++){ //Percorre os dados de cima a baixo

            //Laço interno que percorre as colunas para o adolescente da linha i
            //ele passa por cada uma das colunas j (genero, idade, tempo_de_tela)
            for(int j=0; j < cls; j++){
                normalized[i][j] = normalize(data[i][j], min[j], max[j]);
            }
        }
        return normalized;
    }

    /**
     * Funçao utilizada depois que a rede neural ja aprendeu, exemplo
     * se adicionar novos dados referentes sobre um novo adolescente
     * para constatar sinais de depressão ele não ira varrer todas as
     * linhas novamente, ele pega os novos dados e passa por essa funçao
     *
     */
    public double[] normalizeRow(double[] row){
        double[] result = new double[row.length];
        for(int j=0; j < row.length; j++){
            result[j] = normalize(row[j], min[j], max[j]);
        }
        return result;
    }

    /**
     * Redes Neurais não validam texto
     * Recebe um texto por exemplo {TikTok, Instagram, Twitter}
     * se for TikTok a funçao ve que é a posiçao 1 e faz uma conta
     * proporcional e devolve 0.5
     */
    public double encodeCategories(String value, String[] categories){
        for(int i=0; i < categories.length; i++){
            if(categories[i].equalsIgnoreCase(value.trim())){
                return (double) i / (categories.length - 1);
            }
        }
        return 0.0;
    }
}
