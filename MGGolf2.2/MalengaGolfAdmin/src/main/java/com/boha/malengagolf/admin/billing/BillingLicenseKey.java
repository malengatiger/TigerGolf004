package com.boha.malengagolf.admin.billing;

/**
 * Created by aubreyM on 2014/04/28.
 */
public class BillingLicenseKey {

    static private String m1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr97MkWHcXt8AruRG8aCBxoJBoAmkuPkKNVzBvcd/R23rP1Ccr53E8RaO2RaI";
    static private String m2 = "X1L7zvOytEedDYMuNCd6YBlUT2C9Id4/zN4e6ZJCLZ47vcNwPogzSFsGyDN5qAZmQQvy+ctWX2Ni767b5BhxAjjs4Hp3Wt9qt2NlhMOiDf1";
    static private String m3 = "/kaN/rH0mEq8vyN9yrZfm+1J08o4VD4X5HK30z8NkCTgA+awxmPwxoBB6P6mAvGORBGUWtAVyICW8c2O4CpXVaiRPgsdJ7xf+PM0LZRbxMNm";
    static private String m4 = "kUAlgXgoLTAFLoHs3mDFhL35bUfZgarEDP6UwCEkJCgF2fTR/H474Y68eGF4m/gCMZQIDAQAB";

    public static String getLicenseKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(m1).append(m2).append(m3).append(m4);
        return sb.toString();
    }


}
