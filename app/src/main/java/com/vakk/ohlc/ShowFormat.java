package com.vakk.ohlc;

import com.vakk.ohlc.api.quandl.models.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vakk on 3/15/16.
 */
public class ShowFormat{
    public static List<Data> fullList;
    public static List<Data> currentList;
    public static int perPage=10;
    public static int page=0;
    // sort type
    public static final int SORT_BY_DATE=0;
    public static final int SORT_BY_OPEN=1;
    public static final int SORT_BY_HIGH=2;
    public static final int SORT_BY_LOW=3;
    public static final int SORT_BY_CLOSE=4;
    // order
    public static boolean less = true;
    //date range
    public static String lowRange;
    public static String highRange;

    public static boolean setDateRange(String lowRange,String highRange){
        if(!checkDateFormat(lowRange)||!checkDateFormat(highRange)){
            return false;
        }
        if (!checkDateValues(lowRange)||!checkDateValues(highRange)){
            return false;
        }
        currentList.clear();
        currentList.addAll(fullList);
        page=0;
        ShowFormat.lowRange=lowRange;
        ShowFormat.highRange=highRange;
        filterList();
        return true;
    }
    public static boolean checkDateFormat(String date){
        Pattern p = Pattern.compile("^[\\d]{4}-[\\d]{2}-[\\d]{2}");
        Matcher m = p.matcher(date);
        return m.matches();
    }

    public static boolean checkDateValues(String date){
        String [] dates = date.split("-");
        if (Integer.parseInt(dates[0])<1970||Integer.parseInt(dates[0])>2020){
            return false;
        }
        if (Integer.parseInt(dates[1])<1||Integer.parseInt(dates[1])>12){
            return false;
        }
        if (Integer.parseInt(dates[2])<1||Integer.parseInt(dates[2])>31){
            return false;
        }
        return true;
    }

    public static void filterList(){
        String date;
        for (Iterator<Data>iterator=currentList.iterator();iterator.hasNext();){
            Data data = iterator.next();
            date = data.getDate();
            if (compareDate(date, lowRange)==-1||compareDate(date, highRange)==1){
                iterator.remove();
            }
        }
    }
    private static int compareDate(String date1,String date2){
        String [] spliteDate1 = date1.split("-");
        String [] spliteDate2 = date2.split("-");
        int [] splite1 = new  int[3];
        int [] splite2 = new int[3];
        for (int i=0;i<3;i++){
            splite1[0] = Integer.parseInt(spliteDate1[0]);
            splite1[1] = Integer.parseInt(spliteDate1[1]);
            splite1[2] = Integer.parseInt(spliteDate1[2]);
            splite2[0] = Integer.parseInt(spliteDate2[0]);
            splite2[1] = Integer.parseInt(spliteDate2[1]);
            splite2[2] = Integer.parseInt(spliteDate2[2]);
        }
        if (splite1[0]==splite2[0]){
            if (splite1[1]==splite2[1]){
                if (splite1[2]==splite2[2]){
                    return 0;
                }
                else if (splite1[2]<splite2[2]){
                    return -1;
                }
                else if (splite1[2]>splite2[2]){
                    return 1;
                }
            }
            else if (splite1[1]<splite2[1]){
                return -1;
            }
            else if (splite1[1]>splite2[1]){
                return 1;
            }
        }
        else if (splite1[0]<splite2[0]){
            return -1;
        }
        else if (splite1[0]>splite2[0]){
            return 1;
        }
        return 0;

    }

    public static void orderBy(int param){
        page=0;

        switch (param){
            case SORT_BY_DATE:{
                Collections.sort(currentList, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        return lhs.getDate().compareTo(rhs.getDate());

                    }
                });
                if (!less){
                    Collections.reverse(currentList);
                }
                break;
            }

            case SORT_BY_OPEN:{
                Collections.sort(currentList, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        if (lhs.getOpen()>rhs.getOpen()){
                            return 1;
                        }
                        if (lhs.getOpen()<rhs.getOpen()){
                            return -1;
                        }
                        return 0;
                    }
                });
                if (!less){
                    Collections.reverse(currentList);
                }
                break;
            }

            case SORT_BY_HIGH:{
                Collections.sort(currentList, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        if (lhs.getHigh()>rhs.getHigh()){
                            return 1;
                        }
                        if (lhs.getHigh()<rhs.getHigh()){
                            return -1;
                        }
                        return 0;
                    }
                });
                if (!less){
                    Collections.reverse(currentList);
                }
                break;
            }

            case SORT_BY_LOW:{
                Collections.sort(currentList, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        if (lhs.getLow()>rhs.getLow()){
                            return 1;
                        }
                        if (lhs.getLow()<rhs.getLow()){
                            return -1;
                        }
                        return 0;
                    }
                });
                if (!less){
                    Collections.reverse(currentList);
                }
                break;
            }

            case SORT_BY_CLOSE:{
                Collections.sort(currentList, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        if (lhs.getClose()>rhs.getClose()){
                            return 1;
                        }
                        if (lhs.getClose()<rhs.getClose()){
                            return -1;
                        }
                        return 0;
                    }
                });
                if (!less){
                    Collections.reverse(currentList);
                }
                break;
            }
        }

    }
}
