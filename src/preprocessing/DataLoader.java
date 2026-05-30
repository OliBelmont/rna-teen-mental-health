package preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    protected String path = "data/treino.csv";
    protected String headers[];

    /**
     * Construtor vazio para não sobreescrever o valor inicial do path
     */
    public DataLoader(){
    }

    public List<String[]> loadFile() {

        List<String[]> linhas = new ArrayList<>();

        /**
         * Usar Try-With-Resources
         */
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            /**
             * Faz essa primeira validaçao na entrada do arquivo para não dar NullPointerException
             */
            String headerLine = br.readLine();
            if(headerLine != null){
                headers = headerLine.split(",");
            }

            /**
             * Faz a validaçao de linha a linha
             */
            String line;
            while((line = br.readLine()) != null){
                if(!line.trim().isEmpty()){
                    linhas.add(line.split(","));
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return linhas;
    }

    /*
    public String[] getHeaders(){
        return headers;
    }
    */
}
