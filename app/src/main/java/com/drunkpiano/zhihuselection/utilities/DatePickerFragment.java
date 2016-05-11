package com.drunkpiano.zhihuselection.utilities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by DrunkPiano on 16/5/11.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    TheListener listener;

    public interface TheListener {
        void returnDate(String date,String date2);
    }

    public void onDateSetListener(TheListener listener) {
        this.listener = listener;//传进来一个Listener
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
//        listener = (TheListener) getActivity();
// Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        SimpleDateFormat ghz = new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
        String formattedDate = sdf.format(c.getTime());
        String formattedDateChn = ghz.format(c.getTime());
        if (listener != null) {
            listener.returnDate(formattedDate,formattedDateChn);
        }
    }
}