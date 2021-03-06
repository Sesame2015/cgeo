package cgeo.geocaching.sorting;

import cgeo.geocaching.models.Geocache;

/**
 * Compares caches by date. Used only for event caches.
 */
public class EventDateComparator extends DateComparator {

    public static final EventDateComparator INSTANCE = new EventDateComparator();

    @Override
    protected int sortSameDate(final Geocache left, final Geocache right) {
        return compare(left.guessEventTimeMinutes(), right.guessEventTimeMinutes());
    }

    /**
     * copy of {@link Integer#compare(int, int)}, as that is not available on lower API levels
     *
     */
    private static int compare(final int left, final int right) {
        return left < right ? -1 : (left == right ? 0 : 1);
    }

    @Override
    public boolean isAutoManaged() {
        return true;
    }
}
