package com.landmarkgroup.sahlawarehouse.core.common

/**
 * Direct port of `SahlaWH.UtilityManager.Enum.UserFacility` - maps a facility/location id
 * (as returned in `AuthorizationModel.Authorizations.Locations`) to its short facility code name,
 * used to populate the facility-picker list and resolve `FacilityModel.facilityName`.
 */
object UserFacility {

    private val idToName: Map<Int, String> = mapOf(
        9999000 to "EU", 6099999 to "EB", 2299999 to "ER", 3099999 to "EK", 2399999 to "EJ",
        2199999 to "ED", 1088000 to "EM", 6599999 to "EE", 4099999 to "EQ", 5099999 to "EO",
        2100000 to "DC",

        1029000 to "HB", 1001000 to "BU", 1003000 to "SU", 1035000 to "CU", 1004000 to "HU",
        1022000 to "IU", 1005000 to "LU", 1006000 to "MU", 1034000 to "SI", 1002000 to "SM",
        1017000 to "SX",

        6006000 to "BM", 6029000 to "XB", 6099000 to "BH", 6504000 to "HE", 6506000 to "ME",
        2104000 to "HD", 2106000 to "MD", 2129000 to "XD", 2199000 to "CD", 2204000 to "HR",
        2206000 to "MR", 2227000 to "OR", 2229000 to "XR", 2299000 to "CR", 2304000 to "HJ",
        2306000 to "MJ", 2329000 to "XJ", 2399000 to "CJ", 3004000 to "HK", 3006000 to "MK",
        3099000 to "CK", 4004000 to "QH", 4006000 to "QM", 4029000 to "QX", 4099000 to "QC",
        5004000 to "HO", 5006000 to "MO", 5002000 to "SO", 5099000 to "CO", 5001000 to "BO",
        6004000 to "HB_BAH", 2388888 to "EMM"
    )

    /** True if [locationId] (as a numeric string) is a recognised facility id. */
    fun isDefined(locationId: String): Boolean =
        locationId.toIntOrNull()?.let { idToName.containsKey(it) } ?: false

    /** Facility short-code name for [locationId], or the raw id if unrecognised. */
    fun nameFor(locationId: String): String =
        locationId.toIntOrNull()?.let { idToName[it] } ?: locationId
}
