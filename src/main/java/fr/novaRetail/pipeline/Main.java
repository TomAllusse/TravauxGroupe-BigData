package fr.novaRetail.pipeline;

public class Main {
    public static void main(String[] args){
        /* Niveau d'accréditation => Refus */
        //int niveauAccessRefus = 3;

        /* Niveau d'accréditation => Autorisation */
        int niveauAccess = 10;
        
        if(!SystemBoot.checkAccess(niveauAccess))
            return;

        DataEngine.runPipeline();
    }
}
