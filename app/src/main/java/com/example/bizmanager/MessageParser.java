package com.example.bizmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

    public Map<String,String> parse(final String message){
        String[] exploded = message.toLowerCase().split(" ");

        //Get Transaction code
        String code = exploded[0];

        //Get transaction type
        String transactionType = getTransactionType(message.toLowerCase());

        //Get sender/recipient
        String participant = getParticipant(transactionType,message.toLowerCase());

        String date = exploded[2];

        //Get mpesa amount
        String amount = "";
        String balance = "";
        String transactionCost = "";
        String timeNo = exploded[4];
        String time = "";

        for (String str : exploded) {
            if (str.startsWith("pmksh")) {
                amount = str.replace("pmksh", "");
                String timeStamp = "PM";
                time = timeNo + timeStamp;
            } else if (str.startsWith("amksh")) {
                amount = str.replace("amksh", "");
                String timeStamp = "AM";
                time = timeNo + timeStamp;
            }
        }

        int moneyCount = 0;
        for(String str : exploded){
            if(str.startsWith("ksh")){
                String money = str.replace("ksh","");
                if(moneyCount == 0){
                    balance = money.substring(0,money.length()-1);
                }else if(moneyCount == 1){
                    transactionCost = money.substring(0,money.length()-1);
                }
                moneyCount++;
            }
        }

        Map<String,String> parsed = new HashMap<>();
        parsed.put("type",transactionType.trim());
        parsed.put("code",code.trim());
        parsed.put("amount",amount.trim());
        parsed.put("balance",balance.trim());
        parsed.put("cost",transactionCost.trim());
        parsed.put("participant",participant.trim());
        parsed.put("date", date.trim());
        parsed.put("time", time.trim());
        return parsed;
    }

    private String getTransactionType(String message){
        String transactionType = "unknown";
        if(message.toLowerCase().contains("you have received")){
            transactionType = "received";
        }else if(message.toLowerCase().contains("sent to")){
            transactionType = "sent";
        }else if(message.toLowerCase().contains("withdraw")){
            transactionType = "withdraw";
        }else if(message.toLowerCase().contains("paid to")){
            transactionType = "paybill";
        }else if(message.toLowerCase().contains("you bought")){
            transactionType = "airtime";
        } else if (message.toLowerCase().contains("received from")) {
            transactionType = "Till Payment";
        }
        return transactionType;
    }

    private String getParticipant(String transactionType, String msg){
        switch (transactionType) {
            case "received": {
                Pattern findSubject = Pattern.compile("from(.*)on");
                Matcher matcher = findSubject.matcher(msg);
                while (matcher.find()) {
                    return matcher.group(1);
                }
                break;
            }
            case "Till Payment": {
                Pattern findSubject = Pattern.compile("from(.*). new account");
                Matcher matcher = findSubject.matcher(msg);
                while (matcher.find()) {
                    return matcher.group(1);
                }
                break;
            }
            case "withdraw": {
                Pattern findSubject = Pattern.compile("from(.*)new m-pesa");
                Matcher matcher = findSubject.matcher(msg);
                while (matcher.find()) {
                    return matcher.group(1);
                }
                break;
            }
            case "sent":
            case "paybill": {
                Pattern findSubject = Pattern.compile("to(.*)on [1-9]");
                Matcher matcher = findSubject.matcher(msg);
                while (matcher.find()) {
                    return matcher.group(1);
                }
                break;
            }
        }
        return "";
    }
}