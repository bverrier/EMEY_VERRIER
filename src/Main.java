public class Main {
    public static void main(String[] args) {
        // Création d'un purse standard, PIN user = 1234, PIN admin = 123456
        Purse purse = new Purse(new int[] {1, 2, 3, 4}, new int[] {1, 2, 3, 4, 5, 6});

        System.out.println("Opération de crédit : 30 euros");
        purse.beginTransactionCredit(30);
        purse.commitTransactionCredit();

        System.out.println("Affichage du solde : "+purse.getData());

        System.out.println("Opération de débit : 20 euros");
        purse.beginTransactionDebit(20);
        purse.commitTransactionDebit();

        System.out.println("Affichage du solde : "+purse.getData());

        System.out.println("Déblocage de la carte");
        purse.PINChangeUnblock();

        System.out.println("Opération de crédit : 15 euros");
        purse.beginTransactionCredit(15);
        purse.commitTransactionCredit();

        System.out.println("Opération de débit : 10 euros");
        purse.beginTransactionDebit(10);
        purse.commitTransactionDebit();
    }
}
