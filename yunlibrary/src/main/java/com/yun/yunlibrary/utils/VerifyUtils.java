package com.yun.yunlibrary.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 * Created by zhuofeng on 2015/4/10.
 */
public class VerifyUtils {

    /**
     * 简单手机正则校验
     *
     * @param MobileNo 手机号
     * @return
     */
    public static boolean isValidMobileNo(@NonNull String MobileNo) {

        String regPattern = "^1[3-9]\\d{9}$";
        return Pattern.matches(regPattern, MobileNo);

    }

    /**
     * 8-12位数字密码正则校验
     *
     * @param Pwd 密码
     * @return
     */
    public static boolean isValidPwd(@NonNull String Pwd) {

       /* Pattern pat = Pattern.compile("[\\da-zA-Z]{8,12}");
        Pattern patno = Pattern.compile(".*\\d.*");
        Pattern paten = Pattern.compile(".*[a-zA-Z].*");
        Matcher mat = pat.matcher(Pwd);
        Matcher matno = patno.matcher(Pwd);
        Matcher maten = paten.matcher(Pwd);
        return (matno.matches() && maten.matches() && mat.matches());*/
        //Pattern patno = Pattern.compile(".*\\d.*");
        //Pattern paten = Pattern.compile(".*[a-zA-Z].*");
        //Matcher matno = patno.matcher(Pwd);
        //Matcher maten = paten.matcher(Pwd);
        //数字和字母的组合密码
        Pattern pat = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,12}$");
        //只能为数字
        //  Pattern pat = Pattern.compile("[\\d]{8,12}");
        Matcher mat = pat.matcher(Pwd);
        return mat.matches();
    }

    /**
     * 登录密码的校验,只校验密码的长度
     *
     * @param Pwd 登录密码
     * @return
     */
    public static boolean logonisValidPwd(@NonNull String Pwd) {

       /* Pattern pat = Pattern.compile("[\\da-zA-Z]{8,12}");
        Pattern patno = Pattern.compile(".*\\d.*");
        Pattern paten = Pattern.compile(".*[a-zA-Z].*");
        Matcher mat = pat.matcher(Pwd);
        Matcher matno = patno.matcher(Pwd);
        Matcher maten = paten.matcher(Pwd);
        return (matno.matches() && maten.matches() && mat.matches());*/
        //Pattern patno = Pattern.compile(".*\\d.*");
        //Pattern paten = Pattern.compile(".*[a-zA-Z].*");
        //Matcher matno = patno.matcher(Pwd);
        //Matcher maten = paten.matcher(Pwd);
        //数字和字母的组合密码
        // Pattern pat = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,12}$");
        //只能为数字
        Pattern pat = Pattern.compile("^.{8,12}$");
        Matcher mat = pat.matcher(Pwd);
        return mat.matches();
    }

    /**
     * 密码长度校验,默认不忽略最大长度
     *
     * @param pwd
     * @return
     */
    public static boolean isValidServicePwd(@NonNull String pwd) {
        return isValidServicePwd(pwd, false);
    }

    /**
     * 密码长度校验
     *
     * @param pwd
     * @param ignoreMax, 是否忽略最大长度
     * @return
     */
    public static boolean isValidServicePwd(@NonNull String pwd, boolean ignoreMax) {
        return pwd.length() > 5 && (ignoreMax || pwd.length() < 13);
    }

    /**
     * 判断邮箱格式是否正确：
     */
    public static boolean isValidEmail(@NonNull String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * Luhn算法验证银行卡卡号是否有效
     */
    public static boolean isBankcardNo(String number) {
        int s1 = 0, s2 = 0;
        String reverse = new StringBuffer(number).reverse().toString();
        for (int i = 0; i < reverse.length(); i++) {
            int digit = Character.digit(reverse.charAt(i), 10);
            if (i % 2 == 0) {//this is for odd digits, they are 1-indexed in the algorithm
                s1 += digit;
            } else {//add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
                s2 += 2 * digit;
                if (digit >= 5) {
                    s2 -= 9;
                }
            }
        }
        return (s1 + s2) % 10 == 0;
    }

    /**
     * 判断是否是姓名
     *
     * @param name
     * @return
     */
    public static boolean isUserName(CharSequence name) {
        return !TextUtils.isEmpty(name);
    }

    /**
     * 判断是否是身份证号码
     *
     * @param code
     * @return
     */
    public static boolean isIdCode(String code) {
        return com.shipfocus.commonlibrary.utils.IdcardUtils.validateCard(code);
    }

    /**
     * 验证码校验
     *
     * @param code
     * @return
     */
    public static boolean isServiceCode(@NonNull String code) {
        return code.length() > 0 && code.length() < 10;
    }

    /**
     * URL检查<br>
     * <br>
     *
     * @param pInput 要检查的字符串<br>
     * @return boolean   返回检查结果<br>
     */
    public static boolean isUrl(String pInput) {
        if (pInput == null) {
            return false;
        }
        String regEx = "^((https|http|ftp|rtsp|mms)?:\\/\\/)[^\\s]+";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(pInput);
        return matcher.matches();
    }

    /**
     * 邀请码正则校验
     *
     * @param MobileNo 手机号
     * @return
     */
    public static boolean isInvitedCode (@NonNull String MobileNo) {
        String regPattern = "^\\d{6}$";
        return Pattern.matches(regPattern, MobileNo);

    }
}
