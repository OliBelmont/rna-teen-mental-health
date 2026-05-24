package preprocessing;

public class ColumnMapper {

    /**
     * Mapeando todas as colunas da planilha
     */
    private static final int COLUMN_AGE = 0;
    private static final int COLUMN_GENDER = 1;
    private static final int COLUMN_DAILY_SOCIAL_MEDIA_HOURS = 2;
    private static final int COLUMN_PLATFORM_USAGE = 3;
    private static final int COLUMN_SLEEP_HOURS = 4;
    private static final int COLUMN_SCREEN_TIME_BEFORE_SLEEP = 5;
    private static final int COLUMN_ACADEMIC_PERFORMANCE = 6;
    private static final int COLUMN_PHYSICAL_ACTIVITY = 7;
    private static final int COLUMN_SOCIAL_INTERACTION_LEVEL = 8;
    private static final int COLUMN_STRESS_LEVEL = 9;
    private static final int COLUMN_ANXIETY_LEVEL = 10;
    private static final int COLUMN_ADDICTION_LEVEL = 11;
    private static final int COLUMN_DEPRESSION_LABEL = 12;

    /**
     * Recebe um array contendo uma linha inteira do seu CSV
     */
    public double[] extrairDados(double[] row){

        /**
         * Cria um array menor com 12 posiçoes para pegar somente
         * os 11 elementos de 0 a 11
         */
        double[] inputs = new double[12];
        for(int i=0; i < 12; i++){
            inputs[i] = row[i];
        }
        return inputs;
    }

    /**
     * Funcao que pega o valor do resultado diretamente na coluna que vai ser usado no momento
     * da comparacao do treinamento, para medir se acertou ou nao
     */
    public double extrairLabel(double[] row){
        return row[COLUMN_DEPRESSION_LABEL];
    }

}
