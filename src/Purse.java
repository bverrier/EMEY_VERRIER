import java.util.Arrays;
import java.util.Scanner;

public class Purse {
    enum LCS{
        PRE_PERSO, USE, BLOCKED, DEAD
    }

    private int[] userPIN;
    private int[] adminPIN;
    private final int MAX_USER_TRIES;
    private final int MAX_ADMIN_TRIES;
    private final int MAX_TRANS;
    private final int MAX_BALANCE;
    private final int MAX_CREDIT_AMOUNT;
    private final int MAX_DEBIT_AMOUNT;

    private int userTriesLeft;
    private int adminTriesLeft;
    private int balance;
    private int copy;
    private int transLeft;
    private boolean userAuthenticate;
    private boolean inTransaction;
    private boolean adminAuthenticate;
    private LCS lifeCycleState;

    Purse(int MAX_USER_TRIES, int MAX_ADMIN_TRIES, int MAX_TRANS, int MAX_BALANCE, int MAX_CREDIT_AMOUNT, int MAX_DEBIT_AMOUNT, int[] userPIN, int[] adminPIN){
        this.MAX_USER_TRIES = MAX_USER_TRIES;
        this.MAX_ADMIN_TRIES = MAX_ADMIN_TRIES;
        this.MAX_TRANS = MAX_TRANS;
        this.MAX_BALANCE = MAX_BALANCE;
        this.MAX_CREDIT_AMOUNT = MAX_CREDIT_AMOUNT;
        this.MAX_DEBIT_AMOUNT = MAX_DEBIT_AMOUNT;
        this.userPIN = userPIN;
        this.adminPIN = adminPIN;
        initialize();
        reset();
    }

    public Purse(int[] userPIN, int[] adminPIN) {
        this(3, 4, 500, 100, 50, 30, userPIN, adminPIN);
    }

    private void initialize() {
        this.userTriesLeft = MAX_USER_TRIES;
        this.adminTriesLeft = MAX_ADMIN_TRIES;
        this.balance = 0;
        this.transLeft = MAX_TRANS;
        this.userAuthenticate = false;
        this.adminAuthenticate = false;
        this.lifeCycleState = LCS.USE;
    }

    boolean verifyPINUser(int[] PINCode){
        if (lifeCycleState != LCS.USE || userTriesLeft <= 0) {
            return false;
        }
        if (Arrays.equals(userPIN, PINCode)) {
            userTriesLeft = MAX_USER_TRIES;
            return true;
        } else {
            userTriesLeft--;
            if (userTriesLeft == 0) {
                lifeCycleState = LCS.BLOCKED;
            }
            return false;
        }
    }

    boolean verifyPINAdmin(int[] PINCode){
        if (lifeCycleState == LCS.DEAD || adminTriesLeft <= 0) {
            return false;
        }
        if (Arrays.equals(PINCode, adminPIN)) {
            adminTriesLeft = MAX_ADMIN_TRIES;
            adminAuthenticate = true;
            return true;
        } else {
            adminTriesLeft--;
            if (adminTriesLeft == 0) {
                lifeCycleState = LCS.DEAD;
            }
            return false;
        }
    }

    private boolean getIdentificationAdmin(){
        System.out.println("Veuillez entrer le code PIN administrateur :");
        Scanner scanner = new Scanner(System.in);
        for(int loop = 0 ; loop < MAX_ADMIN_TRIES; loop++){
            String input = scanner.nextLine();
            int[] inputPIN = new int[input.length()];
            for (int i = 0; i < input.length(); i++) {
                inputPIN[i] = Character.getNumericValue(input.charAt(i));
            }
            if(verifyPINAdmin(inputPIN)){
                adminAuthenticate = true;
                break;
            }else{
                System.out.println("Code PIN incorrect, veuillez réessayer.");
            }
        }
        return adminAuthenticate;
    }

    private boolean getIdentificationUser(){
        if(lifeCycleState != LCS.USE){
            System.out.println("La carte n'est pas utilisable actuellement.");
            return false;
        }
        System.out.println("Veuillez entrer le code PIN :");
        Scanner scanner = new Scanner(System.in);
        for(int loop = 0 ; loop < MAX_USER_TRIES; loop++){
            String input = scanner.nextLine();
            int[] inputPIN = new int[input.length()];
            for (int i = 0; i < input.length(); i++) {
                inputPIN[i] = Character.getNumericValue(input.charAt(i));
            }
            if(verifyPINUser(inputPIN)){
                userAuthenticate = true;
                break;
            }else{
                System.out.println("Code PIN incorrect, veuillez réessayer.");
            }
        }
        return userAuthenticate;
    }

    void PINChangeUnblock(){
        if (getIdentificationAdmin() && lifeCycleState == LCS.BLOCKED) {
            userTriesLeft = MAX_USER_TRIES;
            lifeCycleState = LCS.USE;
        }
    }

    void beginTransactionDebit(int amount){
        try {
            if (lifeCycleState != LCS.USE || transLeft <= 0 || amount > MAX_DEBIT_AMOUNT || amount > balance) {
                throw new IllegalStateException("Transaction non autorisée.");
            }
            inTransaction = true;
            copy = balance;
            balance -= amount;
        } catch (Exception e) {
            System.out.println("Erreur pendant la transaction");
            reset();
        }

    }

    void beginTransactionCredit(int amount){
        try{
            if (lifeCycleState != LCS.USE || transLeft <= 0 || amount > MAX_CREDIT_AMOUNT || balance + amount > MAX_BALANCE) {
                throw new IllegalStateException("Transaction non autorisée.");
            }
            if (getIdentificationUser()) {
                inTransaction = true;
                copy = balance;
                balance += amount;
            } else {
                throw new IllegalStateException("Authentification utilisateur requise.");
            }
        } catch (Exception e) {
            System.out.println("Erreur pendant la transaction");
            reset();
        }
    }

    void commitTransactionDebit(){
        if (lifeCycleState == LCS.USE) {
            transLeft--;
            checkCardStatus();
            inTransaction = false;
        }
    }
    void commitTransactionCredit(){
        if (lifeCycleState == LCS.USE) {
            transLeft--;
            userAuthenticate = false;
            checkCardStatus();
            inTransaction = false;
        }
    }

    private void checkCardStatus() {
        if (transLeft <= 0) {
            lifeCycleState = LCS.DEAD;
        }
    }

    int getData(){
        return balance;
    }

    void reset() {
        if (inTransaction) {
            abortTransaction();
        }
    }

    void abortTransaction() {
        balance = copy;
        inTransaction = false;
        System.out.println("Transaction annulée");
        reset();
    }
}
