package descriptio.net.venture.io;

import android.provider.BaseColumns;

/**
 * Created by raharri on 4/25/2016.
 */
public class AstuStateContract {

    public AstuStateContract() {};

    public static abstract class AstuState implements BaseColumns {
        public static final String TABLE_NAME = "astea";
        public static final String COLUMN_NAME_FILEPATH = "url";
        public static final String COLUMN_NAME_LOC_TYPE = "locType";
    }

    public static abstract class LocTypes {
        public static final int asset = 1;
        public static final int cloud = 2;
    }
}
