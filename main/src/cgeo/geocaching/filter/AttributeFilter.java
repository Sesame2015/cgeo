package cgeo.geocaching.filter;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.R;
import cgeo.geocaching.models.Geocache;

import org.eclipse.jdt.annotation.NonNull;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;

class AttributeFilter extends AbstractFilter {

    private final String attribute;

    AttributeFilter(@NonNull final String name, final String attribute) {
        super(name);
        this.attribute = attribute;
    }

    protected AttributeFilter(final Parcel in) {
        super(in);
        attribute = in.readString();
    }

    private static String getName(final String attribute, final Resources res, final String packageName) {
        // dynamically search for a translation of the attribute
        final int id = res.getIdentifier(attribute, "string", packageName);
        return id > 0 ? res.getString(id) : attribute;
    }

    @Override
    public boolean accepts(@NonNull final Geocache cache) {
        return cache.getAttributes().contains(attribute);
    }

    public static class Factory implements IFilterFactory {

        @Override
        @NonNull
        public List<IFilter> getFilters() {
            final String packageName = CgeoApplication.getInstance().getBaseContext().getPackageName();
            final Resources res = CgeoApplication.getInstance().getResources();

            final List<IFilter> filters = new LinkedList<>();
            for (final String id: res.getStringArray(R.array.attribute_ids)) {
                filters.add(new AttributeFilter(getName("attribute_" + id, res, packageName), id));
            }
            return filters;
        }

    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(attribute);
    }

    public static final Creator<AttributeFilter> CREATOR
            = new Parcelable.Creator<AttributeFilter>() {

        @Override
        public AttributeFilter createFromParcel(final Parcel in) {
            return new AttributeFilter(in);
        }

        @Override
        public AttributeFilter[] newArray(final int size) {
            return new AttributeFilter[size];
        }
    };
}
