/*
 * Copyright (c) 2020 www.hoprxi.com All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package identity.hoprxi.core.domain.servers;

import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

/***
 * @author <a href="www.hoprxi.com/author/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2020-12-25
 */
public class PasswordService {
    private static final String DIGITS = "0123456789";
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SYMBOLS = "\"`!?$?%^&*()_-+={[}]:;@'~#|\\<,>.?/";
    private static final int STRONG_THRESHOLD = 20;
    private static final int VERY_STRONG_THRESHOLD = 40;
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    public PasswordService() {
        super();
    }

    private boolean isContainChinese(String str) {
        return CHINESE_PATTERN.matcher(str).matches();
    }

    private int calculatePasswordStrength(String plainTextPassword) {
        plainTextPassword = Objects.requireNonNull(plainTextPassword, "Plain text password is required");
        if (isContainChinese(plainTextPassword))
            throw new IllegalArgumentException("Cannot contain Chinese characters");
        int strength = 0;
        int length = plainTextPassword.length();
        if (length > 6) {
            strength += 10;
            // bonus: one point each additional
            strength += (length - 6);
        }
        int digitCount = 0;
        int letterCount = 0;
        int lowerCount = 0;
        int upperCount = 0;
        int symbolCount = 0;
        for (int idx = 0; idx < length; ++idx) {
            char ch = plainTextPassword.charAt(idx);
            if (Character.isLetter(ch)) {
                ++letterCount;
                if (Character.isUpperCase(ch)) {
                    ++upperCount;
                } else {
                    ++lowerCount;
                }
            } else if (Character.isDigit(ch)) {
                ++digitCount;
            } else {
                ++symbolCount;
            }
        }
        strength += (upperCount + lowerCount + symbolCount);
        // bonus: letters and digits
        if (letterCount >= 2 && digitCount >= 2) {
            strength += (letterCount + digitCount);
        }
        return strength;
    }

    /**
     * @return
     */
    public String generateStrongPassword() {
        String generatedPassword = null;
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        boolean isStrong = false;
        int index = 0;
        while (!isStrong) {
            int opt = random.nextInt(4);
            switch (opt) {
                case 0:
                    index = random.nextInt(LETTERS.length());
                    password.append(LETTERS.substring(index, index + 1));
                    break;
                case 1:
                    index = random.nextInt(LETTERS.length());
                    password.append(LETTERS.substring(index, index + 1).toLowerCase());
                    break;
                case 2:
                    index = random.nextInt(DIGITS.length());
                    password.append(DIGITS.substring(index, index + 1));
                    break;
                case 3:
                    index = random.nextInt(SYMBOLS.length());
                    password.append(SYMBOLS.substring(index, index + 1));
                    break;
            }
            generatedPassword = password.toString();
            if (generatedPassword.length() >= 7) {
                isStrong = this.isStrong(generatedPassword);
            }
        }
        return generatedPassword;
    }

    public boolean isStrong(String aPlainTextPassword) {
        return this.calculatePasswordStrength(aPlainTextPassword) >= STRONG_THRESHOLD;
    }

    public boolean isVeryStrong(String aPlainTextPassword) {
        return this.calculatePasswordStrength(aPlainTextPassword) >= VERY_STRONG_THRESHOLD;
    }

    public boolean isWeak(String aPlainTextPassword) {
        return this.calculatePasswordStrength(aPlainTextPassword) < STRONG_THRESHOLD;
    }
}
