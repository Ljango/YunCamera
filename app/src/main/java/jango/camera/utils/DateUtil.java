package jango.camera.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Title:
 * @Description:
 * @Author:����
 * @Since:2014��4��30��
 * @Version:1.1.0
 */
public class DateUtil {

    /**
     * ȡ��Ӧ��ʽ��ʱ��
     * 
     * @param format
     * @return
     */
    public static String getFormatTime(String format) {
        TimeZone timeZone = TimeZone.getDefault();
        // Calendar calendar = Calendar.getInstance(Locale.CHINA);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(timeZone);
        String time = sdf.format(new Date());
        return time;
    }

}
