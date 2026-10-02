
package com.andric;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;


@Setter
@NoArgsConstructor
public class EidInfo {

    /** eID information codes. */


    /** Builds an instance of EID info. */
    public static class Builder {

        Map<Tag, String> builder;

        public Builder() {
            builder = new HashMap<Tag, String>();
        }


        public Builder addValue(Tag tag, String value) {
            builder.put(tag, value);
            return this;
        }

        public EidInfo build() {
            return new EidInfo(builder);
        }
    }

    private Map<Tag, String> fields;

    private EidInfo(Map<Tag, String> fields) {
        this.fields = fields;
    }

    /** Returns the value associated with the supplied tag. */
    public String get(Tag tag) {
        return fields.get(tag);
    }

    /** Returns if there is a value associated with the supplied tag. */
    public boolean has(Tag tag) {
    	String value = get(tag);
    	return (value != null && value.length() > 0);
    }

    /** Append tag value using given separator as a prefix. */
    private void appendTo(StringBuilder builder, String separator, Tag tag) {
        if (has(tag)) {
            builder.append(separator);
            builder.append(get(tag));
        }
    }

    /** Append tag value without any prefix. */
    private void appendTo(StringBuilder builder, Tag tag) {
        appendTo(builder, "", tag);
    }

    /** Append formatted tag value using given separator as a prefix */
    private void appendTo(StringBuilder builder, String separator, String format, Tag tag) {
        if (has(tag)) {
            builder.append(separator);
            builder.append(String.format(format, get(tag)));
        }
    }

    /**
     * Require at least one %s in the format string
     *
     * If given format is null or does not contain %s, replace with "%s"
     */
    private String sanitizeFormat(String format) {
        return (format != null && format.contains("%s"))
            ? format
            : "%s";
    }
    
    private String formatDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        return date.length() == 8 ?
                String.format("%s.%s.%s", date.substring(0, 2), date.substring(2, 4), date.substring(4, 8))
                : date;
    }

    /**
     * Get given name, parent given name and a surname as a single string.
     *
     * @return Nicely formatted full name
     */
    public String getNameFull() {
        return String.format(
            "%s %s %s", get(Tag.GIVEN_NAME), get(Tag.PARENT_GIVEN_NAME), get(Tag.SURNAME));
    }

    /**
     * Get place of residence as multiline string. Format parameters can be used to provide better
     * output or null/empty strings can be passed for no special formating.
     *
     * For example if floorLabelFormat is "%s. sprat" returned string will contain "5. sprat" for
     * floor number 5.
     *
     * Recommended values for Serbian are "ulaz %s", "%s. sprat" and "br. %s"
     *
     * @param entranceLabelFormat String to format entrance label or null
     * @param floorLabelFormat String to format floor label or null
     * @param appartmentLabelFormat String to format apartment label or null
     * @return Nicely formatted place of residence as multiline string
     *
     * FIXME: Use one parameterized format string to allow both "Main street 11" and 
     * "11 Main street"
     * 
     * FIXME: Think about how to handle short form format with missing ENTRANCE/FLOOR 
     * label (line 298).
     */
    public String getPlaceFull(
            String entranceLabelFormat, String floorLabelFormat, String appartmentLabelFormat) {

        StringBuilder out = new StringBuilder();

        entranceLabelFormat = sanitizeFormat(entranceLabelFormat);
        floorLabelFormat = sanitizeFormat(floorLabelFormat);
        appartmentLabelFormat = sanitizeFormat(appartmentLabelFormat);

        // Main street, Main street 11, Main street 11A
        appendTo(out, Tag.STREET);
        appendTo(out, " ", Tag.HOUSE_NUMBER);
        appendTo(out, Tag.HOUSE_LETTER);

        // For entranceLabel = "ulaz %s" gives "Main street 11A ulaz 2"
        appendTo(out, " ", entranceLabelFormat, Tag.ENTRANCE);

        // For floorLabel = "%s. sprat" gives "Main street 11 ulaz 2, 5. sprat"
        appendTo(out, ", ", floorLabelFormat, Tag.FLOOR);

        if (has(Tag.APPARTMENT_NUMBER)) {
            // For appartmentLabel "br. %s" gives "Main street 11 ulaz 2, 5. sprat, br. 4"
            if (has(Tag.ENTRANCE) || has(Tag.FLOOR)) {
                appendTo(out, ", ", appartmentLabelFormat, Tag.APPARTMENT_NUMBER);
            } else {
                // short form: Main street 11A/4
                appendTo(out, "/", Tag.APPARTMENT_NUMBER);
            }
        }

        appendTo(out, "\n", Tag.PLACE);
        appendTo(out, ", ", Tag.COMMUNITY);

        String rawState = get(Tag.STATE);

        out.append("\n");

        if ("SRB".equals(rawState)) {
            // small cheat for a better output
            out.append("REPUBLIKA SRBIJA");
        } else if (rawState != null && !rawState.isEmpty()) {
            out.append(rawState);
        }

        return out.toString();
    }

    /**
     * Get full place of birth as a multiline string, including community and state if present.
     *
     * @return Nicely formatted place of birth as a multiline string.
     */
    public String getPlaceOfBirthFull()
    {
        StringBuilder out = new StringBuilder();

        appendTo(out, Tag.PLACE_OF_BIRTH);
        appendTo(out, ", ", Tag.COMMUNITY_OF_BIRTH);
        appendTo(out, "\n", Tag.STATE_OF_BIRTH);

        return out.toString();
    }

    public String getDocRegNo() {
        return get(Tag.DOC_REG_NO);
    }
    public String getIssuingDate() {
        return formatDate(get(Tag.ISSUING_DATE));
    }
    public String getExpiryDate() {
        return formatDate(get(Tag.EXPIRY_DATE));
    }
    public String getIssuingAuthority() {
        return get(Tag.ISSUING_AUTHORITY);
    }
    public String getPersonalNumber() {
        return get(Tag.PERSONAL_NUMBER);
    }
    public String getSurname() {
        return get(Tag.SURNAME);
    }
    public String getGivenName() {
        return get(Tag.GIVEN_NAME);
    }
    public String getParentGivenName() {
        return get(Tag.PARENT_GIVEN_NAME);
    }
    public String getSex() {
        return get(Tag.SEX);
    }
    public String getPlaceOfBirth() {
        return get(Tag.PLACE_OF_BIRTH);
    }
    public String getCommunityOfBirth() {
        return get(Tag.COMMUNITY_OF_BIRTH);
    }
    public String getStateOfBirth() {
        return get(Tag.STATE_OF_BIRTH);
    }
    public String getDateOfBirth() {
        return formatDate(get(Tag.DATE_OF_BIRTH));
    }
    public String getState() {
        return get(Tag.STATE);
    }
    public String getCommunity() {
        return get(Tag.COMMUNITY);
    }
    public String getPlace() {
        return get(Tag.PLACE);
    }
    public String getStreet() {
        return get(Tag.STREET);
    }
    public String getHouseNumber() {
        return get(Tag.HOUSE_NUMBER);
    }
    public String getHouseLetter() {
        return get(Tag.HOUSE_LETTER);
    }
    public String getEntrance() {
        return get(Tag.ENTRANCE);
    }
    public String getFloor() {
        return get(Tag.FLOOR);
    }
    public String getAppartmentNumber() {
        return get(Tag.APPARTMENT_NUMBER);
    }
    public String getAddressDate() {
        String value = get(Tag.ADDRESS_DATE);
        if (value == null || value.equals("01010001"))
            return null;
        return formatDate(value);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<Tag, String> field : fields.entrySet()) {
            out.append(String.format("%s: %s", field.getKey(), field.getValue()));
        }
        return out.toString();
    }

    @SuppressWarnings("unchecked")
	public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("name_full", getNameFull());
        obj.put("place_full", getPlaceFull("ulaz %s", "%s. sprat", "br. %s"));
        obj.put("place_of_birth_full", getPlaceOfBirthFull());
        for (Map.Entry<Tag, String> field : fields.entrySet()) {
            Tag tag = field.getKey();
            obj.put(tag.getKey(), field.getValue());
        }
        return obj;
    }
}
