package cgeo.calendar;

import org.apache.commons.lang3.CharEncoding;
import org.eclipse.jdt.annotation.NonNull;

import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;

class CalendarEntry {

    @NonNull
    private final String shortDesc;
    @NonNull
    private final String hiddenDate;
    @NonNull
    private final String url;
    @NonNull
    private final String personalNote;
    @NonNull
    private final String name;
    @NonNull
    private final String coords;
    private int startTimeMinutes = -1;
    @NonNull
    private final Uri uri;

    CalendarEntry(@NonNull final Uri uri) {
        this.uri = uri;
        this.shortDesc = getParameter(ICalendar.PARAM_SHORT_DESC);
        this.hiddenDate = getParameter(ICalendar.PARAM_HIDDEN_DATE);
        this.url = getParameter(ICalendar.PARAM_URL);
        this.personalNote = getParameter(ICalendar.PARAM_NOTE);
        this.name = getParameter(ICalendar.PARAM_NAME);
        coords = getParameter(ICalendar.PARAM_COORDS);
        final String startTime = getParameter(ICalendar.PARAM_START_TIME_MINUTES);
        if (startTime.length() > 0) {
            try {
                this.startTimeMinutes = Integer.parseInt(startTime);
            } catch (final NumberFormatException e) {
                Log.e("cgeo", "CalendarEntry creation", e);
            }
        }
    }

    @NonNull
    private String getParameter(@NonNull final String paramKey) {
        try {
            final String param = uri.getQueryParameter(paramKey);
            if (param == null) {
                return "";
            }
            return URLDecoder.decode(param, CharEncoding.UTF_8).trim();
        } catch (final UnsupportedEncodingException e) {
            Log.e("cgeo", "CalendarEntry.getParameter", e);
        }
        return "";
    }

    public boolean isValid() {
        return getName().length() > 0 && getHiddenDate().length() > 0;
    }

    @NonNull
    public String getHiddenDate() {
        return hiddenDate;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @NonNull
    public String getPersonalNote() {
        return personalNote;
    }

    @NonNull
    public String getShortDesc() {
        return shortDesc;
    }

    /**
     * @return {@code Date} based on hidden date. Time is set to 00:00:00.
     */
    @NonNull
    protected Date parseDate() {
        try {
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(getHiddenDate()));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            return cal.getTime();
        } catch (final NumberFormatException e) {
            // cannot happen normally, but static code analysis does not know
            throw new IllegalStateException("hidden date must be a valid date for cache calendar entries");
        }
    }

    /**
     * @return description string with images removed and personal note included
     */
    @NonNull
    protected String parseDescription() {
        final StringBuilder description = new StringBuilder();
        description.append(getUrl());
        if (getShortDesc().length() > 0) {
            // remove images in short description
            final Spanned spanned = Html.fromHtml(getShortDesc(), null, null);
            String text = spanned.toString();
            final ImageSpan[] spans = spanned.getSpans(0, spanned.length(), ImageSpan.class);
            for (int i = spans.length - 1; i >= 0; i--) {
                text = text.substring(0, spanned.getSpanStart(spans[i])) + text.substring(spanned.getSpanEnd(spans[i]));
            }
            if (text.length() > 0) {
                description.append("\n\n");
                description.append(text);
            }
        }

        if (getPersonalNote().length() > 0) {
            description.append("\n\n").append(Html.fromHtml(getPersonalNote()).toString());
        }

        return description.toString();
    }

    @NonNull
    public String getName() {
        return name;
    }

    public int getStartTimeMinutes() {
        return startTimeMinutes;
    }

    @NonNull
    public String getCoords() {
        return coords;
    }

}
