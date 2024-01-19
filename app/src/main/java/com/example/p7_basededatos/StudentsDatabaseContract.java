package com.example.p7_basededatos;

import android.provider.BaseColumns;

public class StudentsDatabaseContract {
    private StudentsDatabaseContract() {
    }

    public static class StudentsTable implements BaseColumns {
        public static final String TABLE = "students";
        public static final String COLUMN_ID_CARD = "id_card";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SURNAME = "surname";
        public static final String COLUMN_CYCLE = "cycle";
        public static final String COLUMN_COURSE = "course";
    }
}
