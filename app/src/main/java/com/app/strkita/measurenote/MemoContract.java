package com.app.strkita.measurenote;

import android.provider.BaseColumns;

/**
 * 定数ファイル
 * Created by kitada on 2017/02/09.
 */

public final class MemoContract {

    public MemoContract() {}

    public static abstract class Notes implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COL_BODY = "body";
        public static final String COL_ELAPSED_TIME = "elapsed_time";
        public static final String COL_CURRENT_COUNT= "current_count";
        public static final String COL_GOAL_COUNT= "goal_count";
        public static final String COL_UPDATED = "updated";
        public static final String COL_CREATED = "created";
        public static final String NO_GOAL_MODE = "no_goal_mode";
        public static final String GOAL_MODE = "goal_mode";
    }
}
