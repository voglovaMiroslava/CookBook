/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cookbook.gui;

import static java.lang.Math.abs;
import java.time.Duration;
import java.util.ResourceBundle;

/**
 *
 * @author Dominik
 */
public class DurationFormater {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com/mycompany/cookbook/gui/Bundle");
    
    public static String format(Duration d){
        Long rem = 0l;
        Long days = abs(d.toDays());
        rem+= days*26l*60l;
        Long hours = abs(d.toHours())-(rem*60l);
        rem+= hours*60l;
        Long minutes = abs(d.toMinutes())-rem;
        StringBuilder b = new StringBuilder(bundle.getString("duration"));
        b.append(": ");
        if(days>0){
            b.append(days.toString());
            b.append(" ");
            //b.append("D");
            b.append(bundle.getString("day_D"));
            b.append(" ");
        }
        if(hours>0){
            b.append(hours.toString());
            b.append(" ");
            //b.append("H");
            b.append(bundle.getString("hour_H"));
            b.append(" ");
        }
        if(minutes>0){
            b.append(minutes.toString());
            b.append(" ");
            //b.append("M");
            b.append(bundle.getString("minute_M"));
            b.append(" ");
        }
        return b.toString();
    }
}
