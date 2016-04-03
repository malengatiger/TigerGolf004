package com.boha.malengagolf.library.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aubreyM on 2014/09/24.
 */
public class PayGate {
    private String version = "21",
        payGateID = "10011013800",
        reference, returnUrl, currency = "ZAR",
        subsFrequency  , checkSum;
    private boolean processNow;
    private int amount, processNowAmount;
    private Date transactionDate, subsStartDate, subsEndDate;

    public String getRequest() {
        StringBuilder sb = new StringBuilder();
        sb.append("https://www.paygate.co.za/paysubs/process.trans?");
        sb.append("VERSION=").append(version).append("&");
        sb.append("PAYGATE_ID=").append(payGateID).append("&");
        sb.append("REFERENCE=").append(reference).append("&");
        sb.append("AMOUNT=").append(amount).append("&");
        sb.append("CURRENCY=").append(currency).append("&");
        sb.append("RETURN_URL=").append(Statics.CRASH_REPORTS_URL).append("&");
        sb.append("TRANSACTION_DATE=").append(sdfTime.format(transactionDate)).append("&");
        sb.append("SUBS_START_DATE=").append(sdf.format(subsStartDate)).append("&");
        sb.append("SUBS_END_DATE=").append(sdf.format(subsEndDate)).append("&");
        sb.append("SUBS_FREQUENCY=").append(FIRST_DAY_OF_MONTH).append("&");
        if (processNow) {
            sb.append("PROCESS_NOW=YES").append("&");
            sb.append("PROCESS_NOW_AMOUNT").append(amount).append("&");
        } else {
            sb.append("PROCESS_NOW=NO").append("&");
            sb.append("PROCESS_NOW_AMOUNT=0").append("&");
        }


        sb.append("CHECKSUM").append(getMD5Checksum());
        return sb.toString();
    }

    private String getMD5Checksum() {
        //TODO - not done here
        String checkSum = null;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(reference);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(sb2.toString().getBytes("UTF-8"));
            checkSum = bytes.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return checkSum;
    }
    public static final int FIRST_DAY_OF_MONTH = 201, LAST_DAY_OF_MONTH = 22;

    static final SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    static final String LOG = PayGate.class.getSimpleName();
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPayGateID() {
        return payGateID;
    }

    public void setPayGateID(String payGateID) {
        this.payGateID = payGateID;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSubsFrequency() {
        return subsFrequency;
    }

    public void setSubsFrequency(String subsFrequency) {
        this.subsFrequency = subsFrequency;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public boolean isProcessNow() {
        return processNow;
    }

    public void setProcessNow(boolean processNow) {
        this.processNow = processNow;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getProcessNowAmount() {
        return processNowAmount;
    }

    public void setProcessNowAmount(int processNowAmount) {
        this.processNowAmount = processNowAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getSubsStartDate() {
        return subsStartDate;
    }

    public void setSubsStartDate(Date subsStartDate) {
        this.subsStartDate = subsStartDate;
    }

    public Date getSubsEndDate() {
        return subsEndDate;
    }

    public void setSubsEndDate(Date subsEndDate) {
        this.subsEndDate = subsEndDate;
    }
}
