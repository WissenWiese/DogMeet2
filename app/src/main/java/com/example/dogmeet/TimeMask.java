package com.example.dogmeet;

import android.text.TextWatcher;
import android.text.Editable;
import java.util.Locale;

public class TimeMask implements TextWatcher{

    private static final int MAX_LENGTH = 8;
    private static final int MIN_LENGTH = 2;

    private String updatedText;
    private boolean editing;


    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        if (text.toString().equals(updatedText) || editing) return;

        String digits = text.toString().replaceAll("\\D", "");
        int length = digits.length();

        if (length <= MIN_LENGTH) {
            updatedText = digits;
            return;
        }

        if (length > MAX_LENGTH) {
            digits = digits.substring(0, MAX_LENGTH);
        }

        if (length <= 4) {
            String startHour= digits.substring(0, 2);
            String startMinutes= digits.substring(2);

            updatedText = String.format(Locale.US, "%s:%s", startHour, startMinutes);
        }
        else if (length <= 6){
            String startHour= digits.substring(0, 2);
            String startMinutes= digits.substring(2, 4);
            String endHour=digits.substring(4);

            updatedText = String.format(Locale.US, "%s:%s-%s", startHour, startMinutes, endHour);
        }
        else {
            String startHour= digits.substring(0, 2);
            String startMinutes= digits.substring(2, 4);
            String endHour=digits.substring(4, 6);
            String endMinutes=digits.substring(6);

            updatedText = String.format(Locale.US, "%s:%s-%s:%s", startHour, startMinutes, endHour, endMinutes);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

        if (editing) return;

        editing = true;

        editable.clear();
        editable.insert(0, updatedText);

        editing = false;
    }
}
