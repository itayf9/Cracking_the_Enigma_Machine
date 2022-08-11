package utill;

public enum Problem {
    NOT_ENOUGH_ELEMENTS, TOO_MANY_ELEMENTS, OUT_OF_RANGE_ID, NOT_IN_ALPHABET, NO_CONFIGURATION,
    SELF_PLUGGING, ALREADY_PLUGGED, ROTOR_DUPLICATION,



    FILE_NOT_FOUND, JAXB_ERROR ,
    FILE_NOT_IN_FORMAT,
    ODD_ALPHABET_AMOUNT,

    NOT_ENOUGH_ROTORS,
    ROTORS_COUNT_BELOW_TWO,
    ROTOR_MAPPING_NOT_IN_ALPHABET,
    ROTOR_MAPPING_NOT_A_LETTER,
    ROTOR_INVALID_ID_RANGE,
    OUT_OF_RANGE_NOTCH,

    NUM_OF_REFLECTS_IS_NOT_HALF_OF_ABC , REFLECTOR_INVALID_ID_RANGE, REFLECTOR_SELF_MAPPING, REFLECTOR_ID_DUPLICATIONS,
    TOO_MANY_REFLECTORS, REFLECTOR_OUT_OF_RANGE_ID,



    UNKNOWN, NO_PROBLEM;

    public void printMe() {
        System.out.println(this.name());
    }

}
