package com.Cards;

import java.util.ArrayList;
import java.util.Collections;

import com.CustomExceptions.InsufficientBalanceException;
import com.CustomExceptions.InsufficientMachineBalanceException;
import com.CustomExceptions.InvalidAmountException;
import com.CustomExceptions.NotAOperatorException;
import com.Interfaces.IATMService;

public class AxisDebitCard implements IATMService{
	
	String name;
	long debitCardNumber;
	double accountBalance;
	int pinNumber;
	ArrayList<String> statement;
	final String type = "user";
	int chances;
	
	public AxisDebitCard(long debitCardNumber,String name,double accountBalance,int pinNumber) {
		chances = 3;
		this.name=name;
		this.accountBalance = accountBalance;
		this.pinNumber = pinNumber;
		statement = new ArrayList<>();
	}

	@Override
	public String getUserType() throws NotAOperatorException {
		
		return type;
	}

	@Override
	public double withdrawAmount(double wthAmount)
			throws InvalidAmountException, InsufficientBalanceException, InsufficientMachineBalanceException {
		if(wthAmount<=0) {
			throw new InvalidAmountException("you can't enter zero amount to withdraw, please enter the amount");
		}else if(wthAmount%100 != 0) {
			throw new InvalidAmountException("please withdraw multiples of 100...");
		}else if(wthAmount<500) {
			throw new InvalidAmountException("please withdraw morethan 500 rupees...");
		}else if(wthAmount >accountBalance) {
			throw new InsufficientBalanceException("you don't have sufficient balance to withdraw... Please check your Balance");
		}else {
			accountBalance = accountBalance - wthAmount;
			statement.add("Debited : " + wthAmount);
			  System.out.println("Withdrawal successful! Amount withdrawn: " + wthAmount);
		        System.out.println("Your updated account balance is: " + accountBalance);  
		}
		return wthAmount;
	}
//	@Override
//	public double withdrawAmount(double wthAmount)
//	        throws InvalidAmountException, InsufficientBalanceException, InsufficientMachineBalanceException {
//	    
//	    // Check for invalid amounts
//	    if (wthAmount <= 0) {
//	        throw new InvalidAmountException("You can't enter zero or negative amount to withdraw, please enter a valid amount.");
//	    } else if (wthAmount % 100 != 0) {
//	        throw new InvalidAmountException("Please withdraw multiples of 100...");
//	    } else if (wthAmount < 500) {
//	        throw new InvalidAmountException("Please withdraw more than 500 rupees...");
//	    } 
//	    // Check for sufficient account balance
//	    else if (wthAmount > accountBalance) { // Corrected condition
//	        throw new InsufficientBalanceException("You don't have sufficient balance to withdraw. Please check your balance.");
//	    } 
//	    // Deduct amount and log the transaction
//	    else {
//	        accountBalance -= wthAmount; // Deduct the withdrawal amount from account balance
//	        statement.add("Debited: " + wthAmount);
//	    }
//
//	    return wthAmount; // Return the withdrawn amount
//	}


	@Override
	public double depositAmount(double dptAmount) throws InvalidAmountException {
		
		if(dptAmount<500) {
			throw new InvalidAmountException("you can't deposit zero or less than Zero rupees..");
		}else if(dptAmount%100 != 0) {
			throw new InvalidAmountException("Please Deposit multiples of 100...");
		}else {
			accountBalance = accountBalance + dptAmount;
			statement.add("Credited : " + dptAmount);
		}
		return dptAmount;
	}

	@Override
	public double checkBalance() {
		
		return accountBalance;
	}

	@Override
	public void changePinNumber(int pinNumber) {
		this.pinNumber = pinNumber;
		
	}

	@Override
	public int getPinNumber() {
		return pinNumber;
		
	}

	@Override
	public String getUserName() {
		return name;
	}

	@Override
	public int getChances() {
		
		return chances;
	}

	@Override
	public void decreaseChances() {
		
		--chances;
	}

	@Override
	public void resetPinChances() {
		chances = 3;
		
	}

	@Override
	public void getMiniStatement() {
		int count = 5;
		if(statement.size()==0) {
			System.out.println("There are not Transcations Happened..");
			return;
			
		}
		System.out.println("===============Last 5 Transcations =============================");
		Collections.reverse(statement);
		for(String trans : statement) {
			if(count==0) {
				break;
			}
			System.out.println(trans);
			count--;
		}
		Collections.reverse(statement);
		
	}

}
