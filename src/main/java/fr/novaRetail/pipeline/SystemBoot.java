package fr.novaRetail.pipeline;

public class SystemBoot {
    public static boolean checkAccess(int niveau) {
        if(niveau < 5){
            System.out.println("Accès refusé => Le niveau d'accréditation trop faible !");
            return false;
        }else{
            System.out.println("Accès autorisé. Bienvenue !");
            return true;
        }
    }
}
