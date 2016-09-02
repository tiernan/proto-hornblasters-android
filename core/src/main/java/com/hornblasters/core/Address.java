package com.hornblasters.core;

import android.provider.BaseColumns;

public final class Address implements BaseColumns {
    public static final String TABLE_NAME               = "addresses";
    public static final String COLUMN_NAME_ID           = "id";
    public static final String COLUMN_NAME_FIRST        = "first_name";
    public static final String COLUMN_NAME_LAST         = "last_name";

    public static final String COLUMN_NAME_EMAIL        = "email";
    public static final String COLUMN_NAME_PHONE        = "phone";

    public static final String COLUMN_NAME_COMPANY      = "company";
    public static final String COLUMN_NAME_STREET1      = "street1";
    public static final String COLUMN_NAME_STREET2      = "street2";
    public static final String COLUMN_NAME_CITY         = "city";
    public static final String COLUMN_NAME_POSTAL       = "postal";
    public static final String COLUMN_NAME_TERRITORY    = "territory";
    public static final String COLUMN_NAME_COUNTRY      = "country";

    public static final String[] ALL_FIELDS = {
            _ID,
            COLUMN_NAME_ID,
            COLUMN_NAME_FIRST,
            COLUMN_NAME_LAST,
            COLUMN_NAME_EMAIL,
            COLUMN_NAME_PHONE,
            COLUMN_NAME_COMPANY,
            COLUMN_NAME_STREET1,
            COLUMN_NAME_STREET2,
            COLUMN_NAME_CITY,
            COLUMN_NAME_POSTAL,
            COLUMN_NAME_TERRITORY,
            COLUMN_NAME_COUNTRY,
    };

    public int _id = 0;
    public int id = 0;
    public String firstName = null;
    public String lastName = null;
    public String email = null;
    public String phone = null;
    public String company = null;
    public String street1 = null;
    public String street2 = null;
    public String city = null;
    public String postal = null;
    private String territory = null;
    private String country = null;
    private float taxRate = 0;

    public void setTerritory(String territory, String country) {
        final String territoryCode = String.format("%S", territory);
        final String countryCode = String.format("%S", country);

        switch (country) {
            case "US":
                switch (territoryCode) {
                    case "FL":
                        this.taxRate = 0.7f;
                }
        }
        this.territory = territoryCode;
        this.country = countryCode;
    }

    public String getTerritory() {
        return this.territory;
    }

    public String getCountry() {
        return this.country;
    }

    public float getTaxRate() {
        return this.taxRate;
    }
}
