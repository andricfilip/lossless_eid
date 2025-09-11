package com.andric;

public enum Tag {
    /** Dummy tag */
    NULL(0, "ignored", "Ignored"),

    /** Registered document number. */
    DOC_REG_NO(101, "doc_reg_no", "Document reg. number"),
    /** The issuing date, e.g. 01.01.2011. */
    ISSUING_DATE(102, "issuing_date", "Issuing date"),
    /** The date that the ID expires */
    EXPIRY_DATE(103, "expiry_date", "Expiry date"),
    /** The authority, e.g. "Ministry of the Interior". */
    ISSUING_AUTHORITY(104, "issuing_authority", "Issuing authority"),

    /** The person's unique identifier number.
     *
     * While mostly unique, due to the non-bulletproof number allocation scheme, has actually
     * been known to repeat for some rare individuals. The last digit is mod 11 checksum, but
     * due the same reason, there exist numbers with incorrect checksum.
     */
    PERSONAL_NUMBER(201, "personal_number", "Personal number"),
    /** Person's last name, e.g. "Smith" for some John Smith */
    SURNAME(202, "surname", "Surname"),
    /** The given name, e.g. "John" for some John Smith. */
    GIVEN_NAME(203, "given_name", "Given name"),
    /**
     * The parent's given name, the usual 'parenthood' middle name used to disambiguate
     * similarly named persons.
     * <p>
     * E.g. "Wiley" for some John Wiley Smith.
     */
    PARENT_GIVEN_NAME(204, "parent_given_name", "Parent given name"),
    /** The gender of the person. */
    SEX(205, "sex", "Gender"),

    /** The place the person was born in, e.g. "Belgrade" */
    PLACE_OF_BIRTH(301, "place_of_birth", "Place of birth"),
    /** The community/municipality the person was born in, e.g. "Savski Venac" */
    COMMUNITY_OF_BIRTH(302, "community_of_birth", "Community of birth"),
    STATE_OF_BIRTH(303, "state_of_birth", "State of birth"),
    STATE_OF_BIRTH_CODE(304, "state_of_birth_code", "State of birth code"),
    DATE_OF_BIRTH(305, "date_of_birth", "Date of birth"),

    /** The state of the person residence */
    STATE(401, "state", "State"),
    /** The community/municipality of the person residence */
    COMMUNITY(402, "community", "Community"),
    /** The place of the person residence */
    PLACE(403, "place", "Place"),
    /** The street name of the person residence */
    STREET(404, "street", "Street name"),
    /** The house number of the person residence */
    HOUSE_NUMBER(405, "house_number", "House number"),
    /** The house letter of the person residence */
    HOUSE_LETTER(406, "house_letter", "House letter"),
    /** The entrance label of the person residence */
    ENTRANCE(407, "entrance_label", "Entrance label"),
    /** The floor number of the person residence */
    FLOOR(408, "floor_number", "Floor number"),
    /** The appartment number of the person residence */
    APPARTMENT_NUMBER(409, "appartment_number", "Appartment number"),
    /** Address update date */
    ADDRESS_DATE(410, "address_date", "Address date");

    private final int code;
    private final String key;
    private final String name;

    /**
     * Initializes a tag with the corresponding raw encoding value.
     */
    Tag(int code, String key, String name) {
        this.code = code;
        this.key = key;
        this.name = name;
    }
    /** Gets the numeric tag code corresponding to this enum. */
    public int getCode() {
        return code;
    }
    /** Gets the string tag key corresponding to this enum. */
    public String getKey() {
        return key;
    }
    /** Gets the string tag name corresponding to this enum. */
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return name;
    }
}