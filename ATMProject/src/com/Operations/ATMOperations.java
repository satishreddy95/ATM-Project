package com.Operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.Cards.AxisDebitCard;
import com.Cards.HDFCDebitCard;
import com.Cards.OperatorCard;
import com.Cards.SBIDebitCard;
import com.CustomExceptions.IncorrectPinLimitReachedException;
import com.CustomExceptions.InsufficientBalanceException;
import com.CustomExceptions.InsufficientMachineBalanceException;
import com.CustomExceptions.InvalidPinException;
import com.CustomExceptions.InvalidAmountException;
import com.CustomExceptions.InvalidCardException;
import com.CustomExceptions.NotAOperatorException;
import com.Interfaces.IATMService;

public class ATMOperations {

	// Initial ATM Machine Balance
	public static double ATM_MACHINE_BALANCE = 100000.00;

	// Activities performed on the ATM Machine
	public static ArrayList<String> ACTIVITY = new ArrayList<>();

	// Database to map card numbers to card Object
	public static HashMap<Long, IATMService> dataBase = new HashMap<>();

	// Flag to indicate if the ATM Machine is on or off
	public static boolean MACHINE_ON = true;

	// Reference to the Current Card in use
	public static IATMService card;

	// Validate the inserted cardby checking againstthe database.
	public static IATMService validateCard(long cardNumber) throws InvalidCardException {
		if (dataBase.containsKey(cardNumber)) {
			return dataBase.get(cardNumber);
		} else {
			ACTIVITY.add("Accessed by " + cardNumber + "is not compatible");
			throw new InvalidCardException("This is not A valid card");
		}
	}

	// Display the Activities performed on the ATM Machine
	public static void checkATMMachineActivities() {
		System.out.println("=====================Activities Performed=====================");
		for (String activity : ACTIVITY) {
			System.out.println("==========================================================");
			System.out.println(activity);
			System.out.println("==========================================================");
		}
	}

	// Reset the number of pin Attempts for a user
	public static void resetUserAttempts(IATMService operatorCard) {
		IATMService card = null;
		long number;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Your CardNumber : ");
		number = sc.nextLong();
		try {
			card = validateCard(number);
			card.resetPinChances(); // Reseting pin attempts for user specified card
			ACTIVITY.add("Accessed by " + operatorCard.getUserName() + "To reset number of chances for User");
		} catch (InvalidCardException ive) {
			ive.printStackTrace();
		}
	}

	// Validate user credintials including PIN
	public static IATMService validateCredentials(long cardNumber, int pinNumber)
			throws InvalidCardException, InvalidPinException, IncorrectPinLimitReachedException {
		if (dataBase.containsKey(cardNumber)) {
			card = dataBase.get(cardNumber);
		} else {
			throw new InvalidCardException("This Card is not a valid Card");
		}
		try {
			if (card.getUserType().equals("Operator")) {
				// perator have a different PIN validation Process.
				if (card.getPinNumber() != pinNumber) {
					throw new InvalidPinException("Dear operator, Please enter correct PIN Number");
				} else {
					return card;
				}
			}
		} catch (NotAOperatorException noe) {
			noe.printStackTrace();
		}

		// Validate pin and handle incorrect attempts
		if (card.getChances() < 0) {
			throw new IncorrectPinLimitReachedException(
					"You have reached wrong limit of PIN Number, which is 3 attempt");
		}
		if (card.getPinNumber() != pinNumber) {
			card.decreaseChances();// Decreasing the no.of remaining chances
			throw new InvalidPinException("You have entered a wrong Pin Number");
		} else {
			return card;
		}
	}

	// validate amount for withdrawl to ensure sufficient machine Balance.
	public static void validateAmount(double amount) throws InsufficientMachineBalanceException {
		if (amount > ATM_MACHINE_BALANCE) {
			throw new InsufficientMachineBalanceException("Insufficient cash in the nachine");
		}
	}

	// Validate deposit amount to ensure it needs ATM requirements
	public static void validateDepositAmount(double amount)
			throws InvalidAmountException, InsufficientMachineBalanceException {
		if (amount % 100 != 0) {
			throw new InvalidAmountException("Please deposit multuiples of 100");
		}
		if (amount + ATM_MACHINE_BALANCE > 200000.0) {
			ACTIVITY.add("Unable to deposit Cash in the ATM Machine");
			throw new InsufficientMachineBalanceException(
					"You cannot deposit cash as the limit of the machine is reached ");
		}
	}

	public static void operatorMode(IATMService card) {
		Scanner sc = new Scanner(System.in);
		double amount;
		boolean flag = true;
		while (flag) {
			System.out.println("Operator mode : Operator Name " + card.getUserName());
			System.out.println("===============================================================================");
			System.out.println();
			System.out.println("||                  0. Switch of the Machine                                 ||");
			System.out.println("||                  1. Check ATM MachineBalance                              ||");
			System.out.println("||                  2. Deposit Cash in the Machine                           ||");
			System.out.println("||                  3. Reset the user Pin Attempts                           ||");
			System.out.println("||                  4. To check activities performed in ATM Machine          ||");
			System.out.println("||                  5. Exit Operator mode                                    ||");
			System.out.println("Enter your choice....!");
			int option = sc.nextInt();
			switch (option) {
			case 0:
				MACHINE_ON = false;
				ACTIVITY.add(
						"Accessed by " + card.getUserName() + "Activity performed  : Switching of the ATM Performed");
				flag = false;
				break;

			case 1:
				ACTIVITY.add("Accessed by " + card.getUserName() + "Activity performed : Checking ATM machine balance");
				System.out.println("The Balance of the ATM machine is : " + ATM_MACHINE_BALANCE + "Is Available");
				break;

			case 2:
				System.out.println("Please Enter the amount to Deposit...!");
				amount = sc.nextDouble();
				try {
					validateDepositAmount(amount);// Validate the deposit amount
					ATM_MACHINE_BALANCE += amount;// Update ATM Machine Balance
					ACTIVITY.add("Accessed by " + card.getUserName()
							+ "Activity performed : Deposit Cash in the ATM Machine");
					System.out.println("=============================================================");
					System.out.println("==================Cash Added in the ATM Machine==============");
					System.out.println("=============================================================");
				} catch (InvalidAmountException | InsufficientMachineBalanceException e) {
					e.printStackTrace();
				}
				break;

			case 3:
				resetUserAttempts(card); // Reset user pin Attempts
				System.out.println("=============================================================");
				System.out.println("=====================User Attempts are Reset=================");
				System.out.println("=============================================================");
				ACTIVITY.add("Accessed by " + card.getUserName()
						+ "Activity performed : Reseting the pin Attemmpts of User");
				break;

			case 4:
				checkATMMachineActivities();// Display ATM Activities
				break;

			case 5:
				flag = false;
				break;

			default:
				System.out.println("You Have Entered a Wrong Option.....");
			}
		}
	}

	public static void main(String[] args) throws NotAOperatorException {

		dataBase.put(22222222222l, new AxisDebitCard(22222222222l, "Satish", 50000.0, 2222));
		dataBase.put(333333333333l, new HDFCDebitCard(333333333333l, "Suresh", 70000.0, 3333));
		dataBase.put(44444444444l, new SBIDebitCard(44444444444l, "Mallesh", 85000.0, 4444));
		dataBase.put(11111111111l, new OperatorCard(11111111111l, 1111, "Operator 1"));
		Scanner sc = new Scanner(System.in);
		long cardNumber = 0;
		double depositAmount = 0.0;
		double withdrawAmount = 0.0;
		int pin = 0;
		while (MACHINE_ON) {
			System.out.println("Plesase enter the Debit Card Number....");
			cardNumber = sc.nextLong();

			try {
				System.out.println("Please Enter Pin Number..");
				pin = sc.nextInt();
				card = validateCredentials(cardNumber, pin);// validate card and pin

				if (card == null) {
					System.out.println("Card Validation Failed...");
					continue;
				}
				ACTIVITY.add("Accessed by " + card.getUserName() + " Status : Acess Approved");

				if (card.getUserType().equals("operator")) {
					operatorMode(card);
					continue;
				}
				while (true) {
					System.out.println("USER MODE " + card.getUserName());
					System.out.println("||====================================================||");
					System.out.println("||                 1.Withdraw Amount                  ||");
					System.out.println("||                 2.Deposit Amount                   ||");
					System.out.println("||                 3.Check Balance                    ||");
					System.out.println("||                 4.Change Pin                       ||");
					System.out.println("||                 5.Mini Statement                   ||");
					System.out.println("||====================================================||");
					System.out.println("Enter Your Choice ");
					int option = sc.nextInt();
					try {
						switch (option) {
						case 1:
							System.out.println("Please Enter Withdraw Amount : ");
							withdrawAmount = sc.nextDouble();
							validateAmount(withdrawAmount);
							card.withdrawAmount(withdrawAmount);
							ATM_MACHINE_BALANCE -= withdrawAmount;
							ACTIVITY.add("Accessed by " + card.getUserName() + " Activity : Amount Withdraw "
									+ withdrawAmount + "From Machine..");
							break;
						case 2:
							System.out.println("Please Enter Deposi Amount : ");
							depositAmount = sc.nextDouble();
							validateDepositAmount(depositAmount);
							card.depositAmount(depositAmount);
							ATM_MACHINE_BALANCE += depositAmount;
							ACTIVITY.add("Accessed by " + card.getUserName() + " Activity : Amount Deposit "
									+ depositAmount + " In the Machine..");
							break;
						case 3:
							System.out.println("Your Account Balance is : " + card.checkBalance());
							ACTIVITY.add("Accessed by " + card.getUserName() + " Activity : Checking the Balance ");
							break;
						case 4:
							System.out.println("Enter A New Pin..");
							pin = sc.nextInt();
							card.changePinNumber(pin);
							ACTIVITY.add("Accessed by " + card.getUserName() + " Activity : Changed Pin Number ");
							break;
						case 5:
							ACTIVITY.add("Accessed by " + card.getUserName() + " Activity : Generating Mini-Statement");
							card.getMiniStatement();
							break;
						default:
							System.out.println("You have Entered a Wrong Option....");
							break;
						}
						System.out.println("Do you Want to Continue.......");
						String nextOption = sc.next();
						if (nextOption.equalsIgnoreCase("n")) {
							break;// Exit user Mode
						}
					} catch (InvalidAmountException | InsufficientBalanceException
							| InsufficientMachineBalanceException e) {
						e.printStackTrace();
					}
				}
			} catch (InvalidPinException | InvalidCardException | IncorrectPinLimitReachedException e) {
				ACTIVITY.add("Accessed by " + card.getUserName() + " Status : Access Denied..");
				e.printStackTrace();
			}
		}
			System.out.println("==================================================================================");
			System.out.println("=========================Thanks For Using ICICI ATM Machine=======================");
			System.out.println("==================================================================================");
		

	}

}